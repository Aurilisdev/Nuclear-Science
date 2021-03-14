package nuclearscience.client.screen;

import com.mojang.blaze3d.matrix.MatrixStack;

import electrodynamics.api.formatting.ElectricUnit;
import electrodynamics.api.utilities.ElectricityChatFormatter;
import electrodynamics.client.screen.generic.GenericContainerScreenUpgradeable;
import electrodynamics.common.tile.generic.component.ComponentType;
import electrodynamics.common.tile.generic.component.type.ComponentElectrodynamic;
import electrodynamics.common.tile.generic.component.type.ComponentFluidHandler;
import electrodynamics.common.tile.generic.component.type.ComponentProcessor;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import nuclearscience.References;
import nuclearscience.common.inventory.container.ContainerGasCentrifuge;
import nuclearscience.common.tile.TileGasCentrifuge;

@OnlyIn(Dist.CLIENT)
public class ScreenGasCentrifuge extends GenericContainerScreenUpgradeable<ContainerGasCentrifuge> {
    public static final ResourceLocation SCREEN_BACKGROUND = new ResourceLocation(
	    References.ID + ":textures/gui/gascentrifuge.png");

    public ScreenGasCentrifuge(ContainerGasCentrifuge container, PlayerInventory playerInventory,
	    ITextComponent title) {
	super(container, playerInventory, title);
    }

    @Override
    public ResourceLocation getScreenBackground() {
	return SCREEN_BACKGROUND;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(MatrixStack matrixStack, int mouseX, int mouseY) {
	font.func_243248_b(matrixStack, title, titleX, titleY, 4210752);
	TileGasCentrifuge centrifuge = container.getHostFromIntArray();
	if (centrifuge != null) {
	    ComponentElectrodynamic electro = centrifuge.getComponent(ComponentType.Electrodynamic);
	    ComponentProcessor processor = centrifuge.getComponent(ComponentType.Processor);
	    font.func_243248_b(matrixStack,
		    new TranslationTextComponent("gui.gascentrifuge.usage", ElectricityChatFormatter
			    .getDisplayShort(processor.getJoulesPerTick() * 20, ElectricUnit.WATT)),
		    playerInventoryTitleX, playerInventoryTitleY, 4210752);
	    font.func_243248_b(matrixStack,
		    new TranslationTextComponent("gui.gascentrifuge.voltage",
			    ElectricityChatFormatter.getDisplayShort(electro.getVoltage(), ElectricUnit.VOLTAGE)),
		    (float) playerInventoryTitleX + 85, playerInventoryTitleY, 4210752);
	    font.func_243248_b(matrixStack, new TranslationTextComponent("U-238"), (float) playerInventoryTitleX + 30,
		    playerInventoryTitleY - 33 + 17f, 4210752);
	    font.func_243248_b(matrixStack, new TranslationTextComponent("U-235"), (float) playerInventoryTitleX + 30,
		    playerInventoryTitleY - 33 - 17f, 4210752);
	}
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack stack, float partialTicks, int mouseX, int mouseY) {
	super.drawGuiContainerBackgroundLayer(stack, partialTicks, mouseX, mouseY);
	TileGasCentrifuge centrifuge = container.getHostFromIntArray();
	if (centrifuge != null) {
	    ComponentFluidHandler handler = centrifuge.getComponent(ComponentType.FluidHandler);
	    blit(stack, guiLeft + 9,
		    (int) (guiTop + 67
			    - handler.getStackFromFluid(Fluids.WATER).getAmount()
				    / (float) TileGasCentrifuge.TANKCAPACITY * 50),
		    214, 31, 16, (int) (handler.getStackFromFluid(Fluids.WATER).getAmount()
			    / (float) TileGasCentrifuge.TANKCAPACITY * 50));
	    blit(stack, guiLeft + 72,
		    (int) (guiTop + 38
			    - centrifuge.stored235 * (float) TileGasCentrifuge.TANKCAPACITY / TileGasCentrifuge.REQUIRED
				    * 22),
		    214, 31, 16, (int) (centrifuge.stored235 * (float) TileGasCentrifuge.TANKCAPACITY
			    / TileGasCentrifuge.REQUIRED * 22));
	    blit(stack, guiLeft + 72,
		    (int) (guiTop + 69
			    - centrifuge.stored238 * (float) TileGasCentrifuge.TANKCAPACITY / TileGasCentrifuge.REQUIRED
				    * 22),
		    214, 31, 16, (int) (centrifuge.stored238 * (float) TileGasCentrifuge.TANKCAPACITY
			    / TileGasCentrifuge.REQUIRED * 22));
	}
    }

}