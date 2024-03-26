package net.mirolls.bettertips.mixin;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class BetterTipsDied {

    @Inject(method = "onDeath", at = @At("RETURN"), cancellable = true)
    private void onGetDeathMessage(DamageSource source, CallbackInfoReturnable<Text> cir) {
        Text customDeathMessage = Text.literal("这里是自定义的死亡消息");
        cir.setReturnValue(customDeathMessage);
    }
}
