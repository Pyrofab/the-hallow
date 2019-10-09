package com.fabriccommunity.spookytime.block;

import com.fabriccommunity.spookytime.block.entity.InfusionAltarBlockEntity;
import com.fabriccommunity.spookytime.block.entity.InfusionPillarBlockEntity;
import com.fabriccommunity.spookytime.registry.SpookyBlocks;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.EntityContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class InfusionPillarBlock extends Block implements BlockEntityProvider {
	private static final VoxelShape SHAPE = Block.createCuboidShape(4, 0, 4, 12, 12, 12);

	public InfusionPillarBlock(Block.Settings settings) {
		super(settings);
	}

	public InfusionAltarBlockEntity getAltar(World world, BlockPos blockPos) {
		for (Direction direction : HorizontalFacingBlock.FACING.getValues()) {
			BlockPos offsetPos = blockPos.offset(direction, 3);
			if (world.getBlockState(offsetPos).getBlock() == SpookyBlocks.INFUSION_ALTAR_BLOCK) {
				InfusionAltarBlockEntity altarEntity = (InfusionAltarBlockEntity) world.getBlockEntity(offsetPos);
				if (altarEntity != null) {
					return altarEntity;
				}
			}
		}
		return null;
	}

	@Override
	public BlockEntity createBlockEntity(BlockView world) {
		return new InfusionPillarBlockEntity();
	}

	@Override
	public boolean activate(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, BlockHitResult blockHitResult) {
		InfusionPillarBlockEntity pillarEntity = (InfusionPillarBlockEntity) world.getBlockEntity(blockPos);
		if (pillarEntity != null) {
			if (playerEntity.getStackInHand(hand).isEmpty()) {
				playerEntity.inventory.insertStack(pillarEntity.takeStack());
			} else {
				playerEntity.setStackInHand(hand, pillarEntity.putStack(playerEntity.getStackInHand(hand)));
			}
		}
		return true;
	}

	@Override
	public void onBroken(IWorld world, BlockPos blockPos, BlockState blockState) {
		InfusionAltarBlockEntity altarEntity = getAltar(world.getWorld(), blockPos);
		if (altarEntity != null) {
			altarEntity.removePillar(blockPos, (InfusionPillarBlockEntity) world.getBlockEntity(blockPos));
		}
		super.onBroken(world, blockPos, blockState);
	}

	@Override
	public void onPlaced(World world, BlockPos blockPos, BlockState blockState, @Nullable LivingEntity livingEntity, ItemStack itemStack) {
		super.onPlaced(world, blockPos, blockState, livingEntity, itemStack);
		InfusionAltarBlockEntity altarEntity = getAltar(world, blockPos);
		if (altarEntity != null) {
			altarEntity.addPillar(blockPos, (InfusionPillarBlockEntity) world.getBlockEntity(blockPos));
		}
	}

	@Override
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	public VoxelShape getOutlineShape(BlockState blockState, BlockView blockView, BlockPos blockPosition, EntityContext entityContext) {
		return SHAPE;
	}
}
