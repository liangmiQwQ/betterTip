package net.mirolls.bettertips.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.mirolls.bettertips.BetterTips.LOGGER;

@Mixin(DamageSource.class)
public abstract class BetterTipsDied {
    @Inject(method = "getDeathMessage", at = @At("RETURN"), cancellable = true)
    private void onDeath(LivingEntity killed, CallbackInfoReturnable<Text> cir) {
        // 处理死亡原因
        DamageSource source = (DamageSource) (Object) this; // 将当前Mixin实例转换为DamageSource
        DamageType deathType = source.getType(); // 使用msgId字段获取死亡类型的ID
        Entity killer = source.getAttacker();
        LOGGER.info("[BetterTips]" + killed.getName().getString() + " was killed" + (killer != null ? "by " + killer : null) + deathType.msgId());
        if (killer != null) {
            // 也就是说有击杀者
            LOGGER.info("[BetterTips]" + killer.getType() + killer.getName() + killer.getHandItems());
            // 击杀物品一类的全部书写
        }
        System.out.printf(String.valueOf(deathType));
        cir.setReturnValue(Text.literal(deathType.toString() + deathType));
//        killed.onDeath();
    }
}

