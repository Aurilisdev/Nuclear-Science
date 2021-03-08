package nuclearscience.common.tile;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import electrodynamics.api.tile.processing.IO2OProcessor;
import electrodynamics.common.block.subtype.SubtypeOre;
import electrodynamics.common.tile.generic.GenericTileProcessor;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import nuclearscience.DeferredRegisters;
import nuclearscience.common.inventory.container.ContainerChemicalExtractor;
import nuclearscience.common.settings.Constants;

public class TileChemicalExtractor extends GenericTileProcessor implements IO2OProcessor, IFluidHandler {
    public static final int TANKCAPACITY = 5000;
    public static final int REQUIRED_WATER_CAP = 4800;

    public static final int[] SLOTS_UP = new int[] { 0 };
    public static final int[] SLOTS_SIDE = new int[] { 2 };
    public static final int[] SLOTS_DOWN = new int[] { 1 };
    private final LazyOptional<IFluidHandler> holder = LazyOptional.of(() -> this);

    public FluidStack tankWater = new FluidStack(Fluids.WATER, 0);

    @Override
    @Nonnull
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction facing) {
	if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
	    return holder.cast();
	}
	return super.getCapability(capability, facing);
    }

    @Override
    public boolean compareCapabilityDirectionElectricity(Direction dir) {
	return true;
    }

    public TileChemicalExtractor() {
	super(DeferredRegisters.TILE_CHEMICALEXTRACTOR.get());
	addUpgradeSlots(3, 4, 5);
    }

    @Override
    public double getJoulesPerTick() {
	return Constants.CHEMICALEXTRACTOR_USAGE_PER_TICK * currentSpeedMultiplier;
    }

    @Override
    public double getVoltage() {
	return DEFAULT_BASIC_MACHINE_VOLTAGE * 2;
    }

    @Override
    public boolean canProcess() {
	trackInteger(0, (int) currentOperatingTick);
	trackInteger(1, (int) getVoltage());
	trackInteger(2, (int) Math.ceil(getJoulesPerTick()));
	trackInteger(3, getRequiredTicks() == 0 ? 1 : getRequiredTicks());
	trackInteger(4, (int) (tankWater.getAmount() / (float) TANKCAPACITY * 100));
	ItemStack bucketStack = getStackInSlot(2);
	if (!bucketStack.isEmpty() && bucketStack.getCount() > 0 && bucketStack.getItem() == Items.WATER_BUCKET
		&& tankWater.getAmount() <= TANKCAPACITY - 1000) {
	    setInventorySlotContents(2, new ItemStack(Items.BUCKET));
	    tankWater.setAmount(Math.min(tankWater.getAmount() + 1000, TANKCAPACITY));
	}
	if (world.getDayTime() % 5 == 0) {
	    sendCustomPacket();
	}
	int requiredWater = getRequiredWater();
	return getJoulesStored() >= getJoulesPerTick() && !getStackInSlot(0).isEmpty()
		&& getStackInSlot(0).getCount() > 0 && tankWater.getAmount() >= requiredWater && requiredWater > 0;
    }

    private int getRequiredWater() {
	ItemStack input = getInput();
	Item item = input.getItem();
	ItemStack output = getOutput();
	int requiredWater = -1;
	if (output.getCount() < output.getMaxStackSize() || output.isEmpty()) {
	    if (item == DeferredRegisters.ITEM_CELLEMPTY.get()) {
		if (output.getItem() == DeferredRegisters.ITEM_CELLHEAVYWATER.get() || output.isEmpty()) {
		    requiredWater = REQUIRED_WATER_CAP;
		}
	    } else if (item == DeferredRegisters.ITEM_CELLHEAVYWATER.get()) {
		if (output.getItem() == DeferredRegisters.ITEM_CELLDEUTERIUM.get() || output.isEmpty()) {
		    requiredWater = REQUIRED_WATER_CAP;
		}
	    } else if (item == electrodynamics.DeferredRegisters.SUBTYPEITEM_MAPPINGS.get(SubtypeOre.uraninite)
		    && (output.getItem() == DeferredRegisters.ITEM_YELLOWCAKE.get() || output.isEmpty())) {
		requiredWater = REQUIRED_WATER_CAP / 3;
	    }
	}
	return requiredWater;
    }

    @Override
    public CompoundNBT writeCustomPacket() {
	CompoundNBT nbt = super.writeCustomPacket();
	nbt.putFloat("clientFluidProgress", tankWater.getAmount() / 5000.0f);
	return nbt;
    }

    @Override
    public void readCustomPacket(CompoundNBT nbt) {
	super.readCustomPacket(nbt);
	clientFluidProgress = nbt.getFloat("clientFluidProgress");
    }

    @Override
    public void process() {
	int requiredWater = getRequiredWater();
	ItemStack stack = getInput();
	Item item = stack.getItem();
	ItemStack output = getOutput();
	if (item == DeferredRegisters.ITEM_CELLEMPTY.get()) {
	    if (output.isEmpty()) {
		setInventorySlotContents(1, new ItemStack(DeferredRegisters.ITEM_CELLHEAVYWATER.get()));
	    } else {
		output.setCount(output.getCount() + 1);
	    }
	} else if (item == DeferredRegisters.ITEM_CELLHEAVYWATER.get()) {
	    if (output.isEmpty()) {
		setInventorySlotContents(1, new ItemStack(DeferredRegisters.ITEM_CELLDEUTERIUM.get()));
	    } else {
		output.setCount(output.getCount() + 1);
	    }
	} else if (item == electrodynamics.DeferredRegisters.SUBTYPEITEM_MAPPINGS.get(SubtypeOre.uraninite)) {
	    if (output.isEmpty()) {
		setInventorySlotContents(1, new ItemStack(DeferredRegisters.ITEM_YELLOWCAKE.get()));
	    } else {
		output.setCount(output.getCount() + 1);
	    }
	}
	stack.setCount(stack.getCount() - 1);
	tankWater.shrink(requiredWater);
    }

    @Override
    public int getRequiredTicks() {
	return Constants.CHEMICALEXTRACTOR_REQUIRED_TICKS;
    }

    @Override
    public int getSizeInventory() {
	return 6;
    }

    @Override
    public int[] getSlotsForFace(Direction side) {
	return side == Direction.UP ? SLOTS_UP : side == Direction.DOWN ? SLOTS_EMPTY : SLOTS_SIDE;
    }

    @Override
    protected Container createMenu(int id, PlayerInventory player) {
	return new ContainerChemicalExtractor(id, player, this, getInventoryData());
    }

    @Override
    public ITextComponent getDisplayName() {
	return new TranslationTextComponent("container.chemicalextractor");
    }

    @Override
    public ItemStack getInput() {
	return getStackInSlot(0);
    }

    @OnlyIn(value = Dist.CLIENT)
    public float clientFluidProgress;

    @Override
    public void setOutput(ItemStack stack) {
	setInventorySlotContents(1, stack);
    }

    @Override
    public ItemStack getOutput() {
	return getStackInSlot(1);
    }

    @Override
    public FluidStack getFluidInTank(int tank) {
	return tankWater;
    }

    @Override
    public int getTanks() {
	return 1;
    }

    @Override
    public int getTankCapacity(int tank) {
	return TANKCAPACITY;
    }

    @Override
    public boolean isFluidValid(int tank, FluidStack stack) {
	return stack.getFluid() == Fluids.WATER;
    }

    @Override
    public int fill(FluidStack resource, FluidAction action) {
	int amount = Math.min(TANKCAPACITY - tankWater.getAmount(), resource.getAmount());
	if (action == FluidAction.EXECUTE) {
	    tankWater.grow(amount);
	}
	return amount;
    }

    @Override
    public FluidStack drain(FluidStack resource, FluidAction action) {
	return drain(resource.getAmount(), action);
    }

    @Override
    public FluidStack drain(int maxDrain, FluidAction action) {
	int amount = Math.min(tankWater.getAmount(), maxDrain);
	if (action == FluidAction.EXECUTE) {
	    tankWater.shrink(amount);
	}
	return amount == 0 ? FluidStack.EMPTY : new FluidStack(tankWater.getFluid(), amount);
    }
}
