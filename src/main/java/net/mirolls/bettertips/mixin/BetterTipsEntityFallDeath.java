package net.mirolls.bettertips.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageRecord;
import net.minecraft.entity.damage.DamageTracker;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.mirolls.bettertips.BetterTips.LOGGER;


@Mixin(DamageTracker.class)
public abstract class BetterTipsEntityFallDeath {
    @Inject(method = "getFallDeathMessage", at = @At("RETURN"))
    private void getFallDeathMessage(DamageRecord damageRecord, Entity attacker, CallbackInfoReturnable<Text> cir) {
        LOGGER.info("[BetterTips]摔落致人死亡");
    }

    @Inject(method = "getAttackedFallDeathMessage", at = @At("RETURN"))
    private void getAttackedFallDeathMessage(Entity attacker, Text attackerDisplayName, String itemDeathTranslationKey, String deathTranslationKey, CallbackInfoReturnable<Text> cir) {
        LOGGER.info("[BetterTips]摔落被击致人死亡");

    }
}
