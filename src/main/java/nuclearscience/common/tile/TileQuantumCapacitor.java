package nuclearscience.common.tile;

import java.util.UUID;

import electrodynamics.api.electricity.CapabilityElectrodynamic;
import electrodynamics.common.network.ElectricityUtilities;
import electrodynamics.prefab.tile.GenericTileTicking;
import electrodynamics.prefab.tile.components.ComponentType;
import electrodynamics.prefab.tile.components.type.ComponentContainerProvider;
import electrodynamics.prefab.tile.components.type.ComponentElectrodynamic;
import electrodynamics.prefab.tile.components.type.ComponentInventory;
import electrodynamics.prefab.tile.components.type.ComponentPacketHandler;
import electrodynamics.prefab.tile.components.type.ComponentTickable;
import electrodynamics.prefab.utilities.object.CachedTileOutput;
import electrodynamics.prefab.utilities.object.TransferPack;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion.Mode;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import nuclearscience.DeferredRegisters;
import nuclearscience.common.inventory.container.ContainerQuantumCapacitor;
import nuclearscience.common.world.QuantumCapacitorData;

public class TileQuantumCapacitor extends GenericTileTicking implements IEnergyStorage {
    public static final double DEFAULT_MAX_JOULES = Double.MAX_VALUE;
    public static final double DEFAULT_VOLTAGE = 1920.0;
    public double outputJoules = 359.0;
    public int frequency = 0;
    public UUID uuid = UUID.randomUUID();
    private CachedTileOutput outputCache;
    private CachedTileOutput outputCache2;

    public TileQuantumCapacitor() {
	super(DeferredRegisters.TILE_QUANTUMCAPACITOR.get());
	addComponent(new ComponentTickable().tickServer(this::tickServer));
	addComponent(new ComponentPacketHandler().guiPacketReader(this::readGUIPacket).guiPacketWriter(this::writeGUIPacket));
	addComponent(new ComponentElectrodynamic(this).voltage(16 * CapabilityElectrodynamic.DEFAULT_VOLTAGE).output(Direction.DOWN)
		.output(Direction.UP).input(Direction.WEST).input(Direction.EAST).input(Direction.SOUTH).input(Direction.NORTH)
		.receivePower(this::receivePower).setJoules(this::setJoulesStored).getJoules(this::getJoulesStored));
	addComponent(new ComponentInventory(this));
	addComponent(new ComponentContainerProvider("container.quantumcapacitor")
		.createMenu((id, player) -> new ContainerQuantumCapacitor(id, player, getComponent(ComponentType.Inventory), getCoordsArray())));

    }

    public double getOutputJoules() {
	return outputJoules;
    }

    public void tickServer(ComponentTickable tickable) {
	if (outputCache == null) {
	    outputCache = new CachedTileOutput(world, new BlockPos(pos).offset(Direction.UP));
	}
	if (outputCache2 == null) {
	    outputCache2 = new CachedTileOutput(world, new BlockPos(pos).offset(Direction.DOWN));
	}
	if (tickable.getTicks() % 40 == 0) {
	    outputCache.update();
	    outputCache2.update();
	}
	double joules = getJoulesStored();
	if (joules > 0 && outputCache.valid()) {
	    double sent = ElectricityUtilities.receivePower(outputCache.getSafe(), Direction.DOWN,
		    TransferPack.joulesVoltage(Math.min(joules, outputJoules), DEFAULT_VOLTAGE), false).getJoules();
	    QuantumCapacitorData.get(world).setJoules(uuid, frequency, getJoulesStored() - sent);
	}
	joules = getJoulesStored();
	if (joules > 0 && outputCache2.valid()) {
	    double sent = ElectricityUtilities.receivePower(outputCache2.getSafe(), Direction.UP,
		    TransferPack.joulesVoltage(Math.min(joules, outputJoules), DEFAULT_VOLTAGE), false).getJoules();
	    QuantumCapacitorData.get(world).setJoules(uuid, frequency, getJoulesStored() - sent);
	}
	if (tickable.getTicks() % 50 == 0) {
	    this.<ComponentPacketHandler>getComponent(ComponentType.PacketHandler).sendGuiPacketToTracking();
	}
    }

    public double joulesClient = 0;

    public void writeGUIPacket(CompoundNBT nbt) {
	nbt.putDouble("joulesClient", getJoulesStored());
	nbt.putInt("frequency", frequency);
	nbt.putUniqueId("uuid", uuid);
	nbt.putDouble("outputJoules", outputJoules);
    }

    public void readGUIPacket(CompoundNBT nbt) {
	joulesClient = nbt.getDouble("joulesClient");
	frequency = nbt.getInt("frequency");
	uuid = nbt.getUniqueId("uuid");
	outputJoules = nbt.getDouble("outputJoules");
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
	super.write(compound);
	compound.putInt("frequency", frequency);
	compound.putDouble("outputJoules", outputJoules);
	compound.putUniqueId("uuid", uuid);
	return compound;
    }

    @Override
    public void read(BlockState state, CompoundNBT compound) {
	super.read(state, compound);
	outputJoules = compound.getDouble("outputJoules");
	frequency = compound.getInt("frequency");
	if (compound.hasUniqueId("uuid")) {
	    uuid = compound.getUniqueId("uuid");
	}
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> capability, Direction facing) {
	if (capability == CapabilityEnergy.ENERGY) {
	    lastDir = facing;
	    return (LazyOptional<T>) LazyOptional.of(() -> this);
	}
	return super.getCapability(capability, facing);
    }

    private Direction lastDir = null;

    public TransferPack receivePower(TransferPack transfer, boolean debug) {
	double joules = getJoulesStored();
	if (lastDir != Direction.UP && lastDir != Direction.DOWN) {
	    double received = Math.min(Math.min(DEFAULT_MAX_JOULES, transfer.getJoules()), DEFAULT_MAX_JOULES - joules);
	    if (!debug) {
		if (transfer.getVoltage() == DEFAULT_VOLTAGE) {
		    joules += received;
		}
		QuantumCapacitorData.get(world).setJoules(uuid, frequency, joules);
		if (transfer.getVoltage() > DEFAULT_VOLTAGE) {
		    world.setBlockState(pos, Blocks.AIR.getDefaultState());
		    world.createExplosion(null, pos.getX(), pos.getY(), pos.getZ(), (float) Math.log10(10 + transfer.getVoltage() / DEFAULT_VOLTAGE),
			    Mode.DESTROY);
		    return TransferPack.EMPTY;
		}
	    }
	    return TransferPack.joulesVoltage(received, transfer.getVoltage());
	}
	return TransferPack.EMPTY;
    }

    @Override
    @Deprecated
    public int receiveEnergy(int maxReceive, boolean simulate) {
	int calVoltage = 120;
	TransferPack pack = receivePower(TransferPack.joulesVoltage(maxReceive, calVoltage), simulate);
	return (int) Math.min(Integer.MAX_VALUE, pack.getJoules());
    }

    @Override
    @Deprecated
    public int extractEnergy(int maxExtract, boolean simulate) {
	int calVoltage = 120;
	TransferPack pack = this.<ComponentElectrodynamic>getComponent(ComponentType.Electrodynamic)
		.extractPower(TransferPack.joulesVoltage(maxExtract, calVoltage), simulate);
	return (int) Math.min(Integer.MAX_VALUE, pack.getJoules());
    }

    @Override
    @Deprecated
    public int getEnergyStored() {
	return (int) Math.min(Integer.MAX_VALUE, getJoulesStored());
    }

    @Override
    @Deprecated
    public int getMaxEnergyStored() {
	return (int) Math.min(Integer.MAX_VALUE, DEFAULT_MAX_JOULES);
    }

    @Override
    @Deprecated
    public boolean canExtract() {
	return true;
    }

    @Override
    @Deprecated
    public boolean canReceive() {
	return true;
    }

    public void setJoulesStored(double joules) {
	QuantumCapacitorData data = QuantumCapacitorData.get(world);
	if (data != null) {
	    data.setJoules(uuid, frequency, joules);
	}
    }

    public double getJoulesStored() {
	QuantumCapacitorData data = QuantumCapacitorData.get(world);
	return data == null ? 0 : data.getJoules(uuid, frequency);
    }

    public double getMaxJoulesStored() {
	return DEFAULT_MAX_JOULES;
    }

}
