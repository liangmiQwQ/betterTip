package net.mirolls.bettertips.mixin;

import net.minecraft.entity.damage.DamageRecord;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTracker;
import net.minecraft.entity.damage.DeathMessageType;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(DamageTracker.class)
public abstract class BetterTipsEntityFallDeath implements BetterTipsDeathAccessor {

    @Inject(method = "getDeathMessage", at = @At("RETURN"))
    private void getDeathMessage(CallbackInfoReturnable<Text> cir) {
        if (this.getRecentDamage().isEmpty()) {
            // death.attack.generic
        }
        DamageTracker tracker = (DamageTracker) (Object) this; // 相当于this
        DamageRecord damageRecord = this.getRecentDamage().get(this.getRecentDamage().size() - 1);
        DamageSource damageSource = damageRecord.damageSource();
        DeathMessageType deathMessageType = damageSource.getType().deathMessageType();
    }
}
