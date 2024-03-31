package net.mirolls.bettertips.mixin;

import net.minecraft.entity.damage.DamageRecord;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTracker;
import net.minecraft.entity.damage.DeathMessageType;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.mirolls.bettertips.BetterTips.LOGGER;


@Mixin(DamageTracker.class)
public abstract class BetterTipsDeath implements BetterTipsDeathAccessor {

    @Inject(method = "getDeathMessage", at = @At("RETURN"))
    private void getDeathMessage(CallbackInfoReturnable<Text> cir) {
        DamageTracker tracker = (DamageTracker) (Object) this;
        if (this.getRecentDamage().isEmpty()) {
            // death.attack.generic
            LOGGER.info("death.attack.generic");
        } else {
            DamageRecord damageRecord = this.getRecentDamage().get(this.getRecentDamage().size() - 1);
            DamageSource damageSource = damageRecord.damageSource();
            DeathMessageType deathMessageType = damageSource.getType().deathMessageType();
            String translationKey = getTranslationKey(deathMessageType, damageSource);
            LOGGER.info(translationKey);
        }
    }

    @Unique
    private String getTranslationKey(DeathMessageType deathMessageType, DamageSource damageSource) {
        return switch (deathMessageType) {
            case FALL_VARIANTS -> "death.fall." + damageSource.getName();
            case INTENTIONAL_GAME_DESIGN -> "death.attack." + damageSource.getName() + ".message";
            default -> "death.attack." + damageSource.getName();
        };
    }
}
