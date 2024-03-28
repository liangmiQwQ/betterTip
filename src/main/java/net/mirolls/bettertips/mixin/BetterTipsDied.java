package net.mirolls.bettertips.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DamageSource.class)
public abstract class BetterTipsDied {
    @Shadow @Final @Nullable private Entity source;

    @Inject(method="getDeathMessage", at= @At("RETURN"))
    private void onDeath(LivingEntity killed, CallbackInfoReturnable<Text> cir){
        System.out.printf("[BetterTips]%s was killed",killed.getName().getString());

        // 处理死亡原因
        DamageSource source = (DamageSource)(Object)this; // 将当前Mixin实例转换为DamageSource
        DamageType deathType = source.getType(); // 使用msgId字段获取死亡类型的ID
        System.out.printf(String.valueOf(deathType));
        cir.setReturnValue( Text.literal(deathType.toString() + String.valueOf(deathType)));
//        killed.onDeath();
    }
}
