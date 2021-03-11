package nuclearscience.common.inventory.container;

import electrodynamics.common.inventory.container.GenericContainer;
import electrodynamics.common.inventory.container.slot.GenericSlot;
import electrodynamics.common.inventory.container.slot.SlotRestricted;
import electrodynamics.common.item.subtype.SubtypeProcessorUpgrade;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.Items;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IntArray;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import nuclearscience.DeferredRegisters;
import nuclearscience.common.tile.TileChemicalBoiler;

public class ContainerChemicalBoiler extends GenericContainer<TileChemicalBoiler> {

    public ContainerChemicalBoiler(int id, PlayerInventory playerinv) {
	this(id, playerinv, new Inventory(5));
    }

    public ContainerChemicalBoiler(int id, PlayerInventory playerinv, IInventory inventory) {
	this(id, playerinv, inventory, new IntArray(6 + 3));
    }

    public ContainerChemicalBoiler(int id, PlayerInventory playerinv, IInventory inventory, IIntArray inventorydata) {
	super(DeferredRegisters.CONTAINER_CHEMICALBOILER.get(), id, playerinv, inventory, inventorydata);
    }

    public ContainerChemicalBoiler(ContainerType<?> type, int id, PlayerInventory playerinv, IInventory inventory,
	    IIntArray inventorydata) {
	super(type, id, playerinv, inventory, inventorydata);
    }

    @Override
    public void addInventorySlots(IInventory inv, PlayerInventory playerinv) {
	addSlot(new GenericSlot(inv, nextIndex(), 83, 31));
	addSlot(new SlotRestricted(inv, nextIndex(), 83, 51, Items.WATER_BUCKET));
	addSlot(new SlotRestricted(inv, nextIndex(), 186, 14,
		electrodynamics.DeferredRegisters.SUBTYPEITEM_MAPPINGS.get(SubtypeProcessorUpgrade.basicspeed),
		electrodynamics.DeferredRegisters.SUBTYPEITEM_MAPPINGS.get(SubtypeProcessorUpgrade.advancedspeed)));
	addSlot(new SlotRestricted(inv, nextIndex(), 186, 34,
		electrodynamics.DeferredRegisters.SUBTYPEITEM_MAPPINGS.get(SubtypeProcessorUpgrade.basicspeed),
		electrodynamics.DeferredRegisters.SUBTYPEITEM_MAPPINGS.get(SubtypeProcessorUpgrade.advancedspeed)));
	addSlot(new SlotRestricted(inv, nextIndex(), 186, 54,
		electrodynamics.DeferredRegisters.SUBTYPEITEM_MAPPINGS.get(SubtypeProcessorUpgrade.basicspeed),
		electrodynamics.DeferredRegisters.SUBTYPEITEM_MAPPINGS.get(SubtypeProcessorUpgrade.advancedspeed)));
    }

    @OnlyIn(Dist.CLIENT)
    public int getBurnLeftScaled() {
	return (int) (inventorydata.get(3 + 0) * 34
		/ (float) (inventorydata.get(3 + 3) == 0 ? 1 : inventorydata.get(3 + 3)));
    }

    @OnlyIn(Dist.CLIENT)
    public boolean isProcessing() {
	return inventorydata.get(3 + 0) > 0;
    }

    @OnlyIn(Dist.CLIENT)
    public int getVoltage() {
	return inventorydata.get(3 + 1);
    }

    @OnlyIn(Dist.CLIENT)
    public int getJoulesPerTick() {
	return inventorydata.get(3 + 2);
    }

    @OnlyIn(Dist.CLIENT)
    public int getU6FLevelScaled() {
	return (int) (inventorydata.get(3 + 4) / 100.0 * 50);
    }

    @OnlyIn(Dist.CLIENT)
    public int getWaterLevelScaled() {
	return (int) (inventorydata.get(3 + 5) / 100.0 * 50);
    }

}
