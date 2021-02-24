package nuclearscience.common.inventory.container;

import electrodynamics.common.inventory.container.GenericContainerInventory;
import electrodynamics.common.inventory.container.slot.GenericSlot;
import electrodynamics.common.inventory.container.slot.SlotRestricted;
import electrodynamics.common.item.subtype.SubtypeProcessorUpgrade;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IntArray;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import nuclearscience.DeferredRegisters;
import nuclearscience.common.tile.TileGasCentrifuge;

public class ContainerGasCentrifuge extends GenericContainerInventory {

    public ContainerGasCentrifuge(int id, PlayerInventory playerinv) {
	this(id, playerinv, new Inventory(5));
    }

    public ContainerGasCentrifuge(int id, PlayerInventory playerinv, IInventory inventory) {
	this(id, playerinv, inventory, new IntArray(7));
    }

    public ContainerGasCentrifuge(int id, PlayerInventory playerinv, IInventory inventory, IIntArray inventorydata) {
	super(DeferredRegisters.CONTAINER_GASCENTRIFUGE.get(), id, playerinv, inventory, inventorydata);
    }

    public ContainerGasCentrifuge(ContainerType<?> type, int id, PlayerInventory playerinv, IInventory inventory,
	    IIntArray inventorydata) {
	super(type, id, playerinv, inventory, inventorydata);
    }

    @Override
    public void addInventorySlots(IInventory inv, PlayerInventory playerinv) {
	addSlot(new GenericSlot(inv, nextIndex(), 126, 21));
	addSlot(new GenericSlot(inv, nextIndex(), 126, 48));
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
	return inventorydata.get(0) * 34 / (inventorydata.get(3) == 0 ? 1 : inventorydata.get(3));
    }

    @OnlyIn(Dist.CLIENT)
    public boolean isProcessing() {
	return inventorydata.get(0) > 0;
    }

    @OnlyIn(Dist.CLIENT)
    public int getVoltage() {
	return inventorydata.get(1);
    }

    @OnlyIn(Dist.CLIENT)
    public int getJoulesPerTick() {
	return inventorydata.get(2);
    }

    @OnlyIn(Dist.CLIENT)
    public int getU6FLevelScaled() {
	return (int) (inventorydata.get(4) / 100.0 * 50);
    }

    @OnlyIn(Dist.CLIENT)
    public int getU235LevelScaled() {
	return (int) (inventorydata.get(5) * 100.0 / TileGasCentrifuge.REQUIRED * 22);
    }

    @OnlyIn(Dist.CLIENT)
    public int getU238LevelScaled() {
	return (int) (inventorydata.get(6) * 100.0 / TileGasCentrifuge.REQUIRED * 22);
    }

}
