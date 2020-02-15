package com.fabriccommunity.thehallow.client.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.math.Quaternion;

import com.fabriccommunity.thehallow.block.entity.TinyPumpkinBlockEntity;

@Environment(EnvType.CLIENT)
public class TinyPumpkinRenderer extends BlockEntityRenderer<TinyPumpkinBlockEntity> {
	
	private static final Quaternion NINETY_DEG_X = Vector3f.POSITIVE_X.getDegreesQuaternion(90);
	private static final Quaternion MINUS_NINETY_DEG_Y = Vector3f.POSITIVE_Y.getDegreesQuaternion(-90);
	
	public TinyPumpkinRenderer(BlockEntityRenderDispatcher dispatcher) {
		super(dispatcher);
	}
	
	@Override
	public void render(TinyPumpkinBlockEntity pumpkin, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j) {
		ItemRenderer renderer = MinecraftClient.getInstance().getItemRenderer();
		
		matrixStack.push();
		matrixStack.translate(0.5, 0.275, 0.5);
		matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(360 - pumpkin.getCachedState().get(HorizontalFacingBlock.FACING).asRotation()));
		matrixStack.multiply(NINETY_DEG_X);
		matrixStack.scale(0.75f, 0.75f, 0.75f);
		
		matrixStack.push();
		matrixStack.translate(0.25, 0, 0);
		matrixStack.multiply(MINUS_NINETY_DEG_Y);
		renderer.renderItem(pumpkin.getLeftItem(), ModelTransformation.Mode.FIXED, i, j, matrixStack, vertexConsumerProvider);
		matrixStack.pop();
		
		matrixStack.push();
		matrixStack.translate(-0.25, 0, 0);
		matrixStack.multiply(MINUS_NINETY_DEG_Y);
		renderer.renderItem(pumpkin.getRightItem(), ModelTransformation.Mode.FIXED, i, j, matrixStack, vertexConsumerProvider);
		matrixStack.pop();
		
		matrixStack.pop();
	}
}
