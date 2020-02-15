package com.fabriccommunity.thehallow.item;

import net.fabricmc.fabric.api.dimension.v1.FabricDimensions;
import net.fabricmc.fabric.api.util.NbtType;

import net.minecraft.block.BlockState;
import net.minecraft.block.DispenserBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Quaternion;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

import com.fabriccommunity.thehallow.block.HallowedGateBlock;
import com.fabriccommunity.thehallow.registry.HallowedBlocks;
import com.fabriccommunity.thehallow.registry.HallowedDimensions;
import com.fabriccommunity.thehallow.registry.HallowedItems;
import dev.emi.trinkets.api.ITrinket;
import dev.emi.trinkets.api.SlotGroups;
import dev.emi.trinkets.api.Slots;

import java.util.Random;

public class HallowCharmItem extends Item implements ITrinket {
	
	private static final Quaternion ROTATION_CONSTANT = new Quaternion(Vector3f.POSITIVE_Z, -180f, true);
	
	public HallowCharmItem(Settings settings) {
		super(settings);
		DispenserBlock.registerBehavior(this, TRINKET_DISPENSER_BEHAVIOR);
	}
	
	@Override
	public ActionResult useOnBlock(ItemUsageContext context) {
		PlayerEntity player = context.getPlayer();
		if (context.getWorld().isClient) return ActionResult.PASS;
		BlockState state = context.getWorld().getBlockState(context.getBlockPos());
		if(state.getBlock() == HallowedBlocks.HALLOWED_GATE) {
			if (context.getWorld().getDimension().getType() == DimensionType.OVERWORLD) {
				if (HallowedGateBlock.isValid(context.getWorld(), context.getBlockPos(), state)) {
					BlockPos pos = player.getBlockPos();
					CompoundTag tag = new CompoundTag();
					tag.putInt("x", pos.getX());
					tag.putInt("y", pos.getY());
					tag.putInt("z", pos.getZ());
					context.getStack().putSubTag("PortalLoc", tag);
					FabricDimensions.teleport(player, HallowedDimensions.THE_HALLOW);
					return ActionResult.SUCCESS;
				} else {
					player.addChatMessage(new TranslatableText("text.thehallow.gate_incomplete"), true);
				}
			} else {
				player.addChatMessage(new TranslatableText("text.thehallow.gate_in_wrong_dimension"), true);
			}
		}
		return ActionResult.PASS;
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
		if (world.getDimension().getType() == HallowedDimensions.THE_HALLOW) {
			player.setCurrentHand(hand);
			player.playSound(SoundEvents.BLOCK_PORTAL_TRIGGER, 1F, 1F);
			return new TypedActionResult<>(ActionResult.SUCCESS, player.getActiveItem());
		} else {
			return ITrinket.equipTrinket(player, hand);
		}
	}

	@Override
	public int getMaxUseTime(ItemStack stack) {
		return 100;
	}

	@Override
	public UseAction getUseAction(ItemStack stack) {
		return UseAction.BOW;
	}

	@Override
	public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
		//always happens when in The Hallow
		if (world.isClient) return stack;
		if (!(user instanceof PlayerEntity)) return stack;
		PlayerEntity player = (PlayerEntity)user;
		CompoundTag tag = stack.getOrCreateTag();
		if (tag.contains("PortalLoc", NbtType.COMPOUND)) {
			CompoundTag locTag = tag.getCompound("PortalLoc");
			BlockPos pos = new BlockPos(locTag.getInt("x"), locTag.getInt("y"), locTag.getInt("z"));
			tag.remove("PortalLoc");
			FabricDimensions.teleport(player, DimensionType.OVERWORLD, HallowedDimensions.FIND_SURFACE);
			player.teleport(pos.getX(), pos.getY(), pos.getZ());
			return stack;
		} else {
			FabricDimensions.teleport(player, DimensionType.OVERWORLD, HallowedDimensions.FIND_SURFACE);
			BlockPos pos = player.getEntityWorld().getSpawnPos();
			player.teleport(pos.getX(), pos.getY(), pos.getZ());
			return stack;
		}
	}

	@Override
	public boolean hasEnchantmentGlint(ItemStack stack) {
		return stack.getOrCreateTag().contains("PortalLoc");
	}

	@Override
	public boolean canWearInSlot(String group, String slot) {
		return group.contains(SlotGroups.HEAD) && slot.contains(Slots.NECKLACE);
	}

	@Override
	public void render(String slot, MatrixStack matrix, VertexConsumerProvider vertexConsumer, int light, PlayerEntityModel<AbstractClientPlayerEntity> model, AbstractClientPlayerEntity player, float headYaw, float headPitch) {
		ItemRenderer renderer = MinecraftClient.getInstance().getItemRenderer();
		matrix.push();
		translateToChest(model, player, headYaw, headPitch, matrix); //TODO switch back to trinkets version once it's fixed
		matrix.translate(0, -0.15, 0);
		matrix.scale(0.5F, 0.5F, 0.5F);
		matrix.multiply(ROTATION_CONSTANT);
		renderer.renderItem(new ItemStack(HallowedItems.HALLOW_CHARM), ModelTransformation.Mode.FIXED, light, OverlayTexture.DEFAULT_UV, matrix, vertexConsumer);
		matrix.pop();
	}

	@Override
	public void usageTick(World world, LivingEntity user, ItemStack stack, int ticksLeft) {
		Random random = new Random();
		BlockPos pos = user.getBlockPos();
		double x = pos.getX() + random.nextFloat();
		double y = pos.getY() + random.nextFloat();
		double z = pos.getZ() + random.nextFloat();
		double velX = (random.nextFloat() - 0.5D) * 0.5D;
		double velY = (random.nextFloat() - 0.5D) * 0.5D;
		double velZ = (random.nextFloat() - 0.5D) * 0.5D;
		world.addParticle(ParticleTypes.PORTAL, x, y, z, velX, velY, velZ);
	}
	
	public static void translateToChest(PlayerEntityModel<AbstractClientPlayerEntity> model, AbstractClientPlayerEntity player, float headYaw, float headPitch, MatrixStack matrix) {
		if (player.isInSneakingPose() && !model.riding && !player.isSwimming()) {
			matrix.translate(0, 0.2, 0);
			matrix.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(model.torso.pitch * 57.5f));
		}
		matrix.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(model.torso.yaw * 57.5f));
		matrix.translate(0, 0.4, -0.16);
	}
}
