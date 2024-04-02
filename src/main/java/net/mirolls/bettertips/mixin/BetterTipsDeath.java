package net.mirolls.bettertips.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.*;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.text.Text;
import net.mirolls.bettertips.color.MinecraftColor;
import net.mirolls.bettertips.death.DeathConfig;
import net.mirolls.bettertips.death.message.MessageInfo;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.IOException;
import java.util.Objects;

import static net.mirolls.bettertips.BetterTips.LOGGER;


@Mixin(DamageTracker.class)
public abstract class BetterTipsDeath implements BetterTipsDeathAccessor {
    @Inject(method = "getDeathMessage", at = @At("RETURN"), cancellable = true)
    private void getDeathMessage(CallbackInfoReturnable<Text> cir) {
        MessageInfo messageInfo;
        if (this.getRecentDamage().isEmpty()) {
            // death.attack.generic 非常纯粹
            messageInfo = new MessageInfo("death.attack.generic", null, null, null);
        } else {
            DamageRecord damageRecord = this.getRecentDamage().get(this.getRecentDamage().size() - 1);
            DamageSource damageSource = damageRecord.damageSource();
            DeathMessageType deathMessageType = damageSource.getType().deathMessageType();
            DamageRecord damageRecord2 = this.getBiggestFall();
            if (deathMessageType == DeathMessageType.FALL_VARIANTS && damageRecord2 != null) {
                // 模仿DamageTracker，获取id
                messageInfo = this.getFallDeathInfo(damageRecord2, damageSource.getAttacker());
            } else {
                if (deathMessageType == DeathMessageType.INTENTIONAL_GAME_DESIGN) {
                    // 床爆炸的死亡信息
                    String string = "death.attack." + damageSource.getName();
                    messageInfo = new MessageInfo(string + ".message", Objects.requireNonNull(this.getEntity().getDisplayName()).getString(), null, null);
                } else {
                    messageInfo = this.getAttackInfo(damageSource, this.getEntity());
                }
            }
        }


        // 结下来，是最喜欢的颜色～
        try {
            String template = DeathConfig.getMsg(messageInfo.getDeceasedName(), messageInfo.getDeathID());
            // 理论上template会给你一个 ${playerName}至少是计算机可读的东西
            String color = DeathConfig.getColor(messageInfo.getDeceasedName(), messageInfo.getDeathID());

            // To fix: Caused by: java.lang.NullPointerException: Cannot invoke "java.lang.CharSequence.toString()" because "replacement" is null
            String killerName = messageInfo.getKillerName() != null ? messageInfo.getKillerName() : "";
            String killItem = messageInfo.getKillItem() != null ? messageInfo.getKillItem() : "";

            String fullMessage = template.replace("${departed}", messageInfo.getDeceasedName())
                    .replace("${departed}", killerName)
                    .replace("${weapon}", killItem);

            Text finalMessage = MinecraftColor.getMinecraftTextWithColor(fullMessage, color);

            cir.setReturnValue(finalMessage);

        } catch (IOException e) {
            LOGGER.error("[BetterTips] Something Error");
            throw new RuntimeException(e);
        }
    }

    @Unique
    private MessageInfo getFallDeathInfo(DamageRecord damageRecord, @Nullable Entity attacker) {
        DamageSource damageSource = damageRecord.damageSource();
        // 处理无助攻者的情况
        if (damageSource.isIn(DamageTypeTags.IS_FALL) || damageSource.isIn(DamageTypeTags.ALWAYS_MOST_SIGNIFICANT_FALL)) {
            // 此结构适用于 (摔死 && 没有参与者)
            // 适用于 死者从垂泪藤/脚手架/梯子/缠怨藤/水/etc. 中摔落
            FallLocation fallLocation = Objects.requireNonNullElse(damageRecord.fallLocation(), FallLocation.GENERIC);
            // 返回 (这个方法直接返回的就是id death.fell.accident.(something) )
            return new MessageInfo(fallLocation.getDeathMessageKey(), Objects.requireNonNull(this.getEntity().getDisplayName()).getString(), null, null);
        }
        // 处理有助攻者的情况
        Text text = this.getDisplayName(attacker);
        Entity entity = damageSource.getAttacker();
        Text text2 = this.getDisplayName(entity);
        if (text2 != null && !text2.equals(text)) {
            // minecraft官方的代码是调用了getAttackedFallDeathMessage，然后根据这个实体是否有item进行的死亡消息确认
            // death.fell.finish(.item)也是同理
            // 为了代码的可读性，我拆分了一个方法getAttackedFallDeathID，返回String
            return this.getAttackedFallDeathInfo(entity, text2, "death.fell.assist.item", "death.fell.assist");
        }
        if (text != null) {
            return this.getAttackedFallDeathInfo(attacker, text2, "death.fell.finish.item", "death.fell.finish");
        }
        // 默认情况
        return new MessageInfo("death.fell.killer", Objects.requireNonNull(this.getEntity().getDisplayName()).getString(), null, null);
    }

    @Unique
    private MessageInfo getAttackedFallDeathInfo(Entity attacker, Text attackerDisplayName, String itemDeathTranslationKey, String deathTranslationKey) {
        // 对应着getAttackedFallDeathMessage方法
        // 顺便批评一下mojang, 他的目标是把id和message混着一起生成
        // 我的想法是先生成id，要的东西全部丢messageInfo里，然后由getDeathMessage生成信息
        ItemStack itemStack;
        // 定义一个ItemStack，如果攻击者是LivingEntity（生物实体）的实例，那么获取其主手中的物品栈。如果不是，将itemStack设置为ItemStack.EMPTY，表示空物品栈。
        if (attacker instanceof LivingEntity livingEntity) {
            // 如果是的话
            itemStack = livingEntity.getMainHandStack();
        } else {
            itemStack = ItemStack.EMPTY;
        }

        if (!itemStack.isEmpty() && itemStack.hasCustomName()) {
            // 翻译⬆️ 如果有物品，且这个物品有自定义名称
            return new MessageInfo(itemDeathTranslationKey, Objects.requireNonNull(this.getEntity().getDisplayName()).getString(), attackerDisplayName.getString(), itemStack.toHoverableText().getString());
        } else {
            // 如果没有，就是没有自定义物品的
            return new MessageInfo(deathTranslationKey, Objects.requireNonNull(this.getEntity().getDisplayName()).getString(), attackerDisplayName.getString(), null);
        }
    }

    @Unique
    private MessageInfo getAttackInfo(DamageSource damageSource, LivingEntity killed) {
        // 这个方法其实对应DamageSource的getDeathMessage
        // 为了防止无限的getter，创建一下变量
        Entity attacker = damageSource.getAttacker();
        Entity source = damageSource.getSource();

        String string = "death.attack." + damageSource.getType().msgId();
        if (attacker != null || source != null) {
            ItemStack itemStack;
            Text text = attacker == null ? source.getDisplayName() : attacker.getDisplayName();
            if (attacker instanceof LivingEntity livingEntity) {
                itemStack = livingEntity.getMainHandStack();
            } else {
                itemStack = ItemStack.EMPTY;
            }
            if (!itemStack.isEmpty() && itemStack.hasCustomName()) {
                return new MessageInfo(string + ".item", Objects.requireNonNull(killed.getDisplayName()).getString(), Objects.requireNonNull(text).getString(), itemStack.toHoverableText().getString());
            }
        }
        LivingEntity livingEntity2 = killed.getPrimeAdversary();
        // Mojang在他的源代码是这样写的 String string2 = string + ".player";
        // Mojang变量命名生草无比
        String deceased = string + ".player";
        if (livingEntity2 != null) {
            return new MessageInfo(deceased, Objects.requireNonNull(killed.getDisplayName()).getString(), Objects.requireNonNull(livingEntity2.getDisplayName()).getString(), null);
        }
        return new MessageInfo(string, Objects.requireNonNull(killed.getDisplayName()).getString(), null, null);

    }

}
