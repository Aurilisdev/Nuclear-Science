package nuclearscience.client.render.tile;

import java.util.Random;

import com.mojang.blaze3d.matrix.MatrixStack;

import electrodynamics.api.tile.components.ComponentType;
import electrodynamics.api.tile.components.type.ComponentFluidHandler;
import electrodynamics.common.block.BlockGenericMachine;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Quaternion;
import nuclearscience.DeferredRegisters;
import nuclearscience.client.ClientRegister;
import nuclearscience.common.tile.TileNuclearBoiler;

public class RenderNuclearBoiler extends TileEntityRenderer<TileNuclearBoiler> {

    public RenderNuclearBoiler(TileEntityRendererDispatcher rendererDispatcherIn) {
	super(rendererDispatcherIn);
    }

    @Override
    @Deprecated
    public void render(TileNuclearBoiler tileEntityIn, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn,
	    int combinedOverlayIn) {
	matrixStackIn.push();
	IBakedModel ibakedmodel = Minecraft.getInstance().getModelManager().getModel(ClientRegister.MODEL_CHEMICALBOILERWATER);
	Direction face = tileEntityIn.getBlockState().get(BlockGenericMachine.FACING);
	matrixStackIn.translate(0.5, 8.5 / 16.0, 0.5);
	if (face == Direction.NORTH) {
	    matrixStackIn.translate(2.0 / 8.0, 0, 0);
	}
	if (face == Direction.EAST) {
	    matrixStackIn.translate(0, 0, 2.0 / 8.0);
	}
	if (face == Direction.NORTH || face == Direction.SOUTH) {
	    matrixStackIn.rotate(new Quaternion(0, 90, 0, true));
	}
	float prog = tileEntityIn.<ComponentFluidHandler>getComponent(ComponentType.FluidHandler).getStackFromFluid(Fluids.WATER).getAmount()
		/ (float) TileNuclearBoiler.TANKCAPACITY;
	if (prog > 0) {
	    matrixStackIn.scale(1, prog / 16.0f * 12f, 1);
	    Minecraft.getInstance().getBlockRendererDispatcher().getBlockModelRenderer().renderModel(tileEntityIn.getWorld(), ibakedmodel,
		    tileEntityIn.getBlockState(), tileEntityIn.getPos(), matrixStackIn, bufferIn.getBuffer(RenderType.getCutout()), false,
		    tileEntityIn.getWorld().rand, new Random().nextLong(), 1);
	}
	matrixStackIn.pop();
	matrixStackIn.push();
	ibakedmodel = Minecraft.getInstance().getModelManager().getModel(ClientRegister.MODEL_CHEMICALBOILERHEXAFLUORIDE);
	matrixStackIn.translate(0.5, 8.5 / 16.0, 0.5);
	if (face == Direction.NORTH) {
	    matrixStackIn.translate(-2.0 / 8.0, 0, 0);
	}
	if (face == Direction.EAST) {
	    matrixStackIn.translate(0, 0, -2.0 / 8.0);
	}
	if (face == Direction.NORTH || face == Direction.SOUTH) {
	    matrixStackIn.rotate(new Quaternion(0, 90, 0, true));
	}
	prog = tileEntityIn.<ComponentFluidHandler>getComponent(ComponentType.FluidHandler)
		.getStackFromFluid(DeferredRegisters.fluidUraniumHexafluoride).getAmount() / (float) TileNuclearBoiler.TANKCAPACITY;
	if (prog > 0) {
	    matrixStackIn.scale(1, prog / 16.0f * 12f, 1);
	    Minecraft.getInstance().getBlockRendererDispatcher().getBlockModelRenderer().renderModel(tileEntityIn.getWorld(), ibakedmodel,
		    tileEntityIn.getBlockState(), tileEntityIn.getPos(), matrixStackIn, bufferIn.getBuffer(RenderType.getCutout()), false,
		    tileEntityIn.getWorld().rand, new Random().nextLong(), 1);
	}
	matrixStackIn.pop();
    }

}
