package com.fabriccommunity.thehallow.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.player.PlayerEntity;

import com.fabriccommunity.thehallow.registry.HallowedItems;
import dev.emi.trinkets.api.TrinketComponent;
import dev.emi.trinkets.api.TrinketsApi;

@Mixin(EndermanEntity.class)
public class EndermanEntityMixin {
	@Inject(at = @At("HEAD"), method = "isPlayerStaring(Lnet/minecraft/entity/player/PlayerEntity;)Z", cancellable = true)
	private void isPlayerStaring(final PlayerEntity playerEntity, final CallbackInfoReturnable<Boolean> info) {
		TrinketComponent trinketPlayer = TrinketsApi.getTrinketComponent(playerEntity);
		if (trinketPlayer.getStack("head:mask").getItem().equals(HallowedItems.PAPER_BAG)) {
			info.setReturnValue(false);
		}
	}
}
