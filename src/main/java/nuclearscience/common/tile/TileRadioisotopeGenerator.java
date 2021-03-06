package nuclearscience.common.tile;

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
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import nuclearscience.DeferredRegisters;
import nuclearscience.api.radiation.IRadioactiveObject;
import nuclearscience.api.radiation.RadiationRegister;
import nuclearscience.common.inventory.container.ContainerRadioisotopeGenerator;
import nuclearscience.common.settings.Constants;

public class TileRadioisotopeGenerator extends GenericTileTicking {

    protected CachedTileOutput output1;
    protected CachedTileOutput output2;

    public TileRadioisotopeGenerator() {
	super(DeferredRegisters.TILE_RADIOISOTOPEGENERATOR.get());
	addComponent(new ComponentTickable().tickServer(this::tickServer));
	addComponent(new ComponentPacketHandler());
	addComponent(new ComponentElectrodynamic(this).voltage(Constants.RADIOISOTOPEGENERATOR_VOLTAGE).extractPower((x, y) -> TransferPack.EMPTY)
		.output(Direction.UP).output(Direction.DOWN));
	addComponent(new ComponentInventory(this).size(1).slotFaces(0, Direction.values())
		.valid((slot, stack) -> RadiationRegister.get(stack.getItem()) != RadiationRegister.NULL));
	addComponent(new ComponentContainerProvider("container.radioisotopegenerator")
		.createMenu((id, player) -> new ContainerRadioisotopeGenerator(id, player, getComponent(ComponentType.Inventory), getCoordsArray())));
    }

    public void tickServer(ComponentTickable tickable) {
	if (output1 == null) {
	    output1 = new CachedTileOutput(world, pos.offset(Direction.UP));
	}
	if (output2 == null) {
	    output2 = new CachedTileOutput(world, pos.offset(Direction.DOWN));
	}
	if (tickable.getTicks() % 40 == 0) {
	    output1.update();
	    output2.update();
	}
	ItemStack in = this.<ComponentInventory>getComponent(ComponentType.Inventory).getStackInSlot(0);
	IRadioactiveObject rad = RadiationRegister.get(in.getItem());
	double currentOutput = in.getCount() * Constants.RADIOISOTOPEGENERATOR_OUTPUT_MULTIPLIER * rad.getRadiationStrength();
	if (currentOutput > 0) {
	    TransferPack transfer = TransferPack.ampsVoltage(currentOutput / (Constants.RADIOISOTOPEGENERATOR_VOLTAGE * 2.0),
		    Constants.RADIOISOTOPEGENERATOR_VOLTAGE);
	    if (output1.valid()) {
		ElectricityUtilities.receivePower(output1.getSafe(), Direction.DOWN, transfer, false);
	    }
	    if (output2.valid()) {
		ElectricityUtilities.receivePower(output2.getSafe(), Direction.UP, transfer, false);
	    }
	}
    }
}
