package com.fabriccommunity.thehallow.fluid;

import com.fabriccommunity.thehallow.api.HallowedFluidInfo;
import com.fabriccommunity.thehallow.registry.HallowedBlocks;
import com.fabriccommunity.thehallow.registry.HallowedFluids;
import com.fabriccommunity.thehallow.registry.HallowedItems;
import com.fabriccommunity.thehallow.registry.HallowedTags;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FluidBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.fluid.BaseFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.state.StateManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.WorldView;

public class BloodFluid extends BaseFluid implements HallowedFluidInfo {
	@Override
	public int getFogColor() {
		return 0xBB0A1E;
	}

	@Override
	public Fluid getFlowing() {
		return HallowedFluids.FLOWING_BLOOD;
	}
	
	@Override
	public Fluid getStill() {
		return HallowedFluids.BLOOD;
	}
	
	@Override
	protected boolean isInfinite() {
		return false;
	}
	
	@Override
	public Item getBucketItem() {
		return HallowedItems.BLOOD_BUCKET;
	}
	
	@Override
	@Environment(EnvType.CLIENT)
	public ParticleEffect getParticle() {
		return ParticleTypes.DRIPPING_WATER;
	}
	
	@Override
	public boolean method_15777(FluidState fluidState, BlockView blockView, BlockPos blockPos, Fluid fluid, Direction direction) {
		return direction == Direction.DOWN && !fluid.matches(HallowedTags.Fluids.BLOOD);
	}
	
	@Override
	public int getTickRate(WorldView viewableWorld) {
		return 10;
	}
	
	@Override
	public boolean matchesType(Fluid fluid) {
		return fluid == getStill() || fluid == getFlowing();
	}
	
	@Override
	public void beforeBreakingBlock(IWorld iWorld, BlockPos blockPos, BlockState blockState) {
		BlockEntity blockEntity = blockState.getBlock().hasBlockEntity() ? iWorld.getBlockEntity(blockPos) : null;
		Block.dropStacks(blockState, iWorld.getWorld(), blockPos, blockEntity);
	}
	
	@Override
	public int method_15733(WorldView viewableWorld) {
		return 4;
	}
	
	@Override
	public int getLevelDecreasePerBlock(WorldView viewableWorld) {
		return 1;
	}
	
	@Override
	public boolean hasRandomTicks() {
		return true;
	}
	
	@Override
	public float getBlastResistance() {
		return 100.f;
	}
	
	@Override
	public BlockState toBlockState(FluidState fluidState) {
		return HallowedBlocks.BLOOD_BLOCK.getDefaultState().with(FluidBlock.LEVEL, method_15741(fluidState));
	}
	
	@Override
	public boolean isStill(FluidState fluidState) {
		return false;
	}
	
	@Override
	public int getLevel(FluidState fluidState) {
		return 0;
	}
	
	public static class Flowing extends BloodFluid {
		public Flowing() {
		
		}
		
		@Override
		protected void appendProperties(StateManager.Builder<Fluid, FluidState> stateBuilder) {
			super.appendProperties(stateBuilder);
			stateBuilder.add(LEVEL);
		}
		
		@Override
		public int getLevel(FluidState fluidState) {
			return fluidState.get(LEVEL);
		}
		
		@Override
		public boolean isStill(FluidState fluidState) {
			return false;
		}
	}
	
	public static class Still extends BloodFluid {
		public Still() {
		
		}
		
		@Override
		public int getLevel(FluidState fluidState) {
			return 8;
		}
		
		@Override
		public boolean isStill(FluidState fluidState) {
			return true;
		}
	}
}
