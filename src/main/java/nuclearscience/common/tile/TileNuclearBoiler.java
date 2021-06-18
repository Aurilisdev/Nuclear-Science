package nuclearscience.common.tile;

import electrodynamics.api.electricity.CapabilityElectrodynamic;
import electrodynamics.api.sound.SoundAPI;
import electrodynamics.common.block.subtype.SubtypeOre;
import electrodynamics.common.item.ItemProcessorUpgrade;
import electrodynamics.common.recipe.categories.fluiditem2fluid.FluidItem2FluidRecipe;
import electrodynamics.prefab.tile.GenericTileTicking;
import electrodynamics.prefab.tile.components.ComponentType;
import electrodynamics.prefab.tile.components.type.ComponentContainerProvider;
import electrodynamics.prefab.tile.components.type.ComponentDirection;
import electrodynamics.prefab.tile.components.type.ComponentElectrodynamic;
import electrodynamics.prefab.tile.components.type.ComponentFluidHandler;
import electrodynamics.prefab.tile.components.type.ComponentInventory;
import electrodynamics.prefab.tile.components.type.ComponentPacketHandler;
import electrodynamics.prefab.tile.components.type.ComponentProcessor;
import electrodynamics.prefab.tile.components.type.ComponentProcessorType;
import electrodynamics.prefab.tile.components.type.ComponentTickable;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import nuclearscience.DeferredRegisters;
import nuclearscience.SoundRegister;
import nuclearscience.common.inventory.container.ContainerNuclearBoiler;
import nuclearscience.common.recipe.NuclearScienceRecipeInit;
import nuclearscience.common.settings.Constants;

public class TileNuclearBoiler extends GenericTileTicking {
    
	public static final int MAX_TANK_CAPACITY = 5000;
  
    public static Fluid[] SUPPORTED_INPUT_FLUIDS = new Fluid[] {
    		
    		Fluids.WATER

    };
    public static Fluid[] SUPPORTED_OUTPUT_FLUIDS = new Fluid[] {
    		
    		DeferredRegisters.fluidUraniumHexafluoride
    		
    };
    
    public TileNuclearBoiler() {
		super(DeferredRegisters.TILE_CHEMICALBOILER.get());
		addComponent(new ComponentTickable().tickClient(this::tickClient));
		addComponent(new ComponentDirection());
		addComponent(new ComponentPacketHandler());
		addComponent(new ComponentElectrodynamic(this).input(Direction.DOWN).voltage(CapabilityElectrodynamic.DEFAULT_VOLTAGE * 2)
			.maxJoules(Constants.CHEMICALBOILER_USAGE_PER_TICK * 10));
		addComponent(new ComponentFluidHandler(this)
				.relativeInput(Direction.EAST)
				.addMultipleFluidTanks(SUPPORTED_INPUT_FLUIDS, MAX_TANK_CAPACITY, true)
				.addMultipleFluidTanks(SUPPORTED_OUTPUT_FLUIDS, MAX_TANK_CAPACITY, false)
			);
		addComponent(new ComponentInventory(this).size(5).relativeSlotFaces(0, Direction.EAST, Direction.UP).relativeSlotFaces(1, Direction.DOWN)
			.valid((slot, stack) -> slot < 2 || stack.getItem() instanceof ItemProcessorUpgrade));
		addComponent(new ComponentProcessor(this)
				.upgradeSlots(2, 3, 4)
				.canProcess(component -> canProcessNuclBoil(component))
				.process(component -> component.processFluidItem2FluidRecipe(component, FluidItem2FluidRecipe.class))
				.usage(Constants.CHEMICALBOILER_USAGE_PER_TICK)
				.type(ComponentProcessorType.ObjectToObject)
				.requiredTicks(Constants.CHEMICALBOILER_REQUIRED_TICKS));
		addComponent(new ComponentContainerProvider("container.nuclearboiler")
			.createMenu((id, player) -> new ContainerNuclearBoiler(id, player, getComponent(ComponentType.Inventory), getCoordsArray())));
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
    	return super.getRenderBoundingBox().grow(1);
    }

    protected void tickClient(ComponentTickable tickable) {
		boolean running = this.<ComponentProcessor>getComponent(ComponentType.Processor).operatingTicks > 0;
		if (running && world.rand.nextDouble() < 0.15) {
		    world.addParticle(ParticleTypes.SMOKE, pos.getX() + world.rand.nextDouble(), pos.getY() + world.rand.nextDouble() * 0.4 + 0.5,
			    pos.getZ() + world.rand.nextDouble(), 0.0D, 0.0D, 0.0D);
		}
		if (running && tickable.getTicks() % 100 == 0) {
		    SoundAPI.playSound(SoundRegister.SOUND_NUCLEARBOILER.get(), SoundCategory.BLOCKS, 1, 1, pos);
		}
    }

    protected boolean canProcessNuclBoil(ComponentProcessor processor) {
		ComponentDirection direction = getComponent(ComponentType.Direction);
		ComponentInventory inv = getComponent(ComponentType.Inventory);
		ComponentElectrodynamic electro = getComponent(ComponentType.Electrodynamic);
		ComponentFluidHandler tank = getComponent(ComponentType.FluidHandler);
		BlockPos face = getPos().offset(direction.getDirection().getOpposite().rotateY());
		TileEntity faceTile = world.getTileEntity(face);
		if (faceTile != null) {
		    LazyOptional<IFluidHandler> cap = faceTile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY,
			    direction.getDirection().getOpposite().rotateY().getOpposite());
		    if (cap.isPresent()) {
			IFluidHandler handler = cap.resolve().get();
			if (handler.isFluidValid(0, tank.getStackFromFluid(DeferredRegisters.fluidUraniumHexafluoride))) {
			    tank.getStackFromFluid(DeferredRegisters.fluidUraniumHexafluoride)
				    .shrink(handler.fill(tank.getStackFromFluid(DeferredRegisters.fluidUraniumHexafluoride), FluidAction.EXECUTE));
			}
		    }
		}
		processor.consumeBucket(MAX_TANK_CAPACITY, SUPPORTED_INPUT_FLUIDS, 1);
		return processor.canProcessFluidItem2FluidRecipe(processor, FluidItem2FluidRecipe.class, NuclearScienceRecipeInit.NUCLEAR_BOILER_TYPE);
    }

}
