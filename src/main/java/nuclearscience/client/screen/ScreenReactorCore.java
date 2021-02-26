package nuclearscience.client.screen;

import com.mojang.blaze3d.matrix.MatrixStack;

import electrodynamics.client.screen.generic.GenericContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import nuclearscience.References;
import nuclearscience.common.inventory.container.ContainerReactorCore;
import nuclearscience.common.tile.TileReactorCore;

@OnlyIn(Dist.CLIENT)
public class ScreenReactorCore extends GenericContainerScreen<ContainerReactorCore> {
    public static final ResourceLocation SCREEN_BACKGROUND = new ResourceLocation(
	    References.ID + ":textures/gui/reactorcore.png");

    public ScreenReactorCore(ContainerReactorCore container, PlayerInventory playerInventory, ITextComponent title) {
	super(container, playerInventory, title);
    }

    @Override
    public ResourceLocation getScreenBackground() {
	return SCREEN_BACKGROUND;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(MatrixStack matrixStack, int mouseX, int mouseY) {
	super.drawGuiContainerForegroundLayer(matrixStack, mouseX, mouseY);
	font.func_243248_b(matrixStack, new TranslationTextComponent("gui.reactorcore.deuterium"), titleX,
		(float) titleY + 14, 4210752);
	font.func_243248_b(matrixStack, new TranslationTextComponent("gui.reactorcore.temperature",
		container.getTemperature() / 4 + 15 + " C"), titleX, (float) titleY + 14 * 3, 4210752);
	if (container.getTemperature() > TileReactorCore.MELTDOWN_TEMPERATURE_ACTUAL) {
	    if (System.currentTimeMillis() % 1000 < 500) {
		font.func_243248_b(matrixStack, new TranslationTextComponent("gui.reactorcore.warning"), titleX,
			(float) titleY + 55, 16711680);
	    }
	}
    }
}