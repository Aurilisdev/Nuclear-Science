package nuclearscience.client.screen;

import com.mojang.blaze3d.matrix.MatrixStack;

import electrodynamics.api.electricity.formatting.ElectricUnit;
import electrodynamics.api.electricity.formatting.ElectricityChatFormatter;
import electrodynamics.api.tile.components.ComponentType;
import electrodynamics.api.tile.components.type.ComponentElectrodynamic;
import electrodynamics.api.tile.components.type.ComponentFluidHandler;
import electrodynamics.api.tile.components.type.ComponentProcessor;
import electrodynamics.client.screen.generic.GenericContainerScreenUpgradeable;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import nuclearscience.References;
import nuclearscience.common.inventory.container.ContainerChemicalExtractor;
import nuclearscience.common.tile.TileChemicalExtractor;

@OnlyIn(Dist.CLIENT)
public class ScreenChemicalExtractor extends GenericContainerScreenUpgradeable<ContainerChemicalExtractor> {
    public static final ResourceLocation SCREEN_BACKGROUND = new ResourceLocation(References.ID + ":textures/gui/chemicalextractor.png");

    public ScreenChemicalExtractor(ContainerChemicalExtractor container, PlayerInventory playerInventory, ITextComponent title) {
	super(container, playerInventory, title);
    }

    @Override
    public ResourceLocation getScreenBackground() {
	return SCREEN_BACKGROUND;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(MatrixStack matrixStack, int mouseX, int mouseY) {
	font.func_243248_b(matrixStack, title, titleX, titleY, 4210752);
	TileChemicalExtractor extractor = container.getHostFromIntArray();
	if (extractor != null) {
	    ComponentElectrodynamic electro = extractor.getComponent(ComponentType.Electrodynamic);
	    ComponentProcessor processor = extractor.getComponent(ComponentType.Processor);
	    font.func_243248_b(matrixStack,
		    new TranslationTextComponent("gui.chemicalextractor.usage",
			    ElectricityChatFormatter.getDisplayShort(processor.getUsage() * 20, ElectricUnit.WATT)),
		    playerInventoryTitleX, playerInventoryTitleY, 4210752);
	    font.func_243248_b(matrixStack,
		    new TranslationTextComponent("gui.chemicalextractor.voltage",
			    ElectricityChatFormatter.getDisplayShort(electro.getVoltage(), ElectricUnit.VOLTAGE)),
		    (float) playerInventoryTitleX + 85, playerInventoryTitleY, 4210752);
	}
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack stack, float partialTicks, int mouseX, int mouseY) {
	super.drawGuiContainerBackgroundLayer(stack, partialTicks, mouseX, mouseY);
	TileChemicalExtractor extractor = container.getHostFromIntArray();
	if (extractor != null) {
	    ComponentProcessor processor = extractor.getComponent(ComponentType.Processor);
	    ComponentFluidHandler handler = extractor.getComponent(ComponentType.FluidHandler);
	    int burnLeftScaled = (int) (processor.operatingTicks * 34.0 / processor.requiredTicks);
	    blit(stack, guiLeft + 94, guiTop + 30, 212, 14, Math.min(burnLeftScaled, 34), 16);
	    blit(stack, guiLeft + 51,
		    guiTop + 68 - (int) (handler.getStackFromFluid(Fluids.WATER).getAmount() / (float) TileChemicalExtractor.TANKCAPACITY * 50), 214,
		    31, 16, (int) (handler.getStackFromFluid(Fluids.WATER).getAmount() / (float) TileChemicalExtractor.TANKCAPACITY * 50));
	}
    }
}