package com.adaptivesprinting.mixin;

import com.adaptivesprinting.AdaptiveSprinting;
import com.mojang.authlib.GameProfile;
import com.mojang.logging.LogUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin extends AbstractClientPlayerEntity {

	@Unique
	private static final Logger LOGGER = LogUtils.getLogger();

	@Shadow @Final
	protected MinecraftClient client;

	public ClientPlayerEntityMixin(ClientWorld world, GameProfile profile) {
		super(world, profile);
	}

	@Shadow protected abstract boolean shouldStopSprinting();

	@Shadow protected abstract boolean isRidingCamel();

	@Inject(method = "tickMovement", at = @At("TAIL"))
	private void modifySprinting(CallbackInfo ci) {
		while (this.client.options.forwardKey.isPressed() && this.client.getCameraEntity() != null) {
			LOGGER.info("Is player invulnerable: " + this.client.getCameraEntity().isInvulnerable());
		}
		if (
				(this.client.getCameraEntity() != null) // We need the entity to make sprint to actually exist
				&& this.client.getCameraEntity().isControlledByPlayer() // We also need to be able to control the entity to make said entity sprint
				&& (
						AdaptiveSprinting.shouldAlwaysSprint()
						|| this.isRidingCamel() // No hunger consumption on camel, so might as well
						|| this.client.getCameraEntity().isInvulnerable() // If we're invulnerable we can't starve either
				)
						&& !this.shouldStopSprinting() // If you can't sprint in the first place, then don't because that would be cheating
		) {
			this.setSprinting(this.client.options.forwardKey.isPressed());
		}
	}
}
