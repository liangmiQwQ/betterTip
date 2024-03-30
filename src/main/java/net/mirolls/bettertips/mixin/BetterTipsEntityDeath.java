package net.mirolls.bettertips.mixin;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.mirolls.bettertips.BetterTips.LOGGER;


@Mixin(PlayerEntity.class)
public abstract class BetterTipsEntityDeath {
    @Inject(method = "handleFallDamage", at = @At("RETURN"))
    private void handleFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource, CallbackInfoReturnable<Boolean> cir) {
        LOGGER.info("[BetterTips]受到摔落伤害");
    }
}
