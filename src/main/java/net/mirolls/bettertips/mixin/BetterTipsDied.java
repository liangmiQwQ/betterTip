package net.mirolls.bettertips.mixin;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class BetterTipsDied {

    @Inject(method = "onDeath", at = @At("RETURN"), cancellable = true)
    private void onGetDeathMessage(DamageSource damageSource, CallbackInfo info) {
        if ((Object) this instanceof PlayerEntity player) {

            // 取消默认的死亡消息
            info.cancel();

            // 构造自定义的死亡消息
            Text customDeathMessage = Text.literal(player.getName().getString() + " 在这里遇到了不幸，原因是：" + damageSource.getDeathMessage(player).getString());

            // 发送消息到所有玩家
            ServerWorld world = (ServerWorld) player.getWorld();
            for (PlayerEntity p : world.getPlayers()) {
                p.sendMessage(customDeathMessage, false);
            }
        }
    }
}
