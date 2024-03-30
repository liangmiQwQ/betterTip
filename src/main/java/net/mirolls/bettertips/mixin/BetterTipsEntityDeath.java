package net.mirolls.bettertips.mixin;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.mirolls.bettertips.BetterTips.LOGGER;


@Mixin(PlayerEntity.class)
public abstract class BetterTipsEntityDeath {
    @Inject(method = "onDeath", at = @At("RETURN"))
    private void onDeath(DamageSource damageSource, CallbackInfo ci) {
        // 为了检测
        LOGGER.info("[BetterTips]" + "发现有人死亡" + damageSource);
        LOGGER.info("[BetterTips]" + damageSource.getType());
    }

}
