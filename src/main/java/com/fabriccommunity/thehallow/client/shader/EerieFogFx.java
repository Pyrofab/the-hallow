package com.fabriccommunity.thehallow.client.shader;

import com.fabriccommunity.thehallow.TheHallow;
import ladysnake.satin.api.event.PostWorldRenderCallback;
import ladysnake.satin.api.experimental.ReadableDepthFramebuffer;
import ladysnake.satin.api.experimental.managed.Uniform1f;
import ladysnake.satin.api.experimental.managed.Uniform3f;
import ladysnake.satin.api.experimental.managed.UniformMat4;
import ladysnake.satin.api.managed.ManagedShaderEffect;
import ladysnake.satin.api.managed.ShaderEffectManager;
import ladysnake.satin.api.util.GlMatrices;
import ladysnake.satin.config.SatinFeatures;
import net.fabricmc.fabric.api.event.client.ClientTickCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class EerieFogFx implements PostWorldRenderCallback {
	public static final Identifier FOG_SHADER_ID = TheHallow.id("shaders/post/eerie_fog.json");
	public static final EerieFogFx INSTANCE = new EerieFogFx();

	private float prevRadius;
	private float radius;
	private boolean renderingEffect;
	private final Matrix4f projectionMatrix = new Matrix4f();
	private final ManagedShaderEffect shader = ShaderEffectManager.getInstance().manage(FOG_SHADER_ID, shader -> {
		MinecraftClient mc = MinecraftClient.getInstance();
		shader.setSamplerUniform("DepthSampler", ((ReadableDepthFramebuffer)mc.getFramebuffer()).getStillDepthMap());
		shader.setUniformValue("ViewPort", 0, 0, mc.window.getFramebufferWidth(), mc.window.getFramebufferHeight());
	});
	private final Uniform1f uniformSTime = shader.findUniform1f("STime");
	private final UniformMat4 uniformInverseTransformMatrix = shader.findUniformMat4("InverseTransformMatrix");
	private final Uniform3f uniformCameraPosition = shader.findUniform3f("CameraPosition");
	private final Uniform3f uniformCenter = shader.findUniform3f("Center");
	private final Uniform1f uniformRadius = shader.findUniform1f("Radius");

	public void init() {
		SatinFeatures.getInstance().readableDepthFramebuffers.use();
		PostWorldRenderCallback.EVENT.register(this);
		ClientTickCallback.EVENT.register(this::update);
	}

	private void update(MinecraftClient mc) {
		renderingEffect = true;
		prevRadius = radius;
		radius = 20;
	}

	@Override
	public void onWorldRendered(Camera camera, float tickDelta, long nanoTime) {
		if (renderingEffect) {
			Entity e = camera.getFocusedEntity();
			uniformSTime.set((e.world.getTime() + tickDelta) / 20f);
			uniformInverseTransformMatrix.set(GlMatrices.getInverseTransformMatrix(projectionMatrix));
			Vec3d cameraPos = camera.getPos();
			uniformCameraPosition.set((float) cameraPos.x, (float) cameraPos.y, (float) cameraPos.z);
			uniformCenter.set(lerpf(e.x, e.prevX, tickDelta), lerpf(e.y, e.prevY, tickDelta), lerpf(e.z, e.prevZ, tickDelta));
			uniformRadius.set(lerpf(radius, prevRadius, tickDelta));
			shader.render(tickDelta);
		}
	}

	private static float lerpf(double n, double prevN, float tickDelta) {
		return (float) MathHelper.lerp(tickDelta, prevN, n);
	}

}
