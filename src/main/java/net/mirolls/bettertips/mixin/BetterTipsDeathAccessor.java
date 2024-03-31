package net.mirolls.bettertips.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageRecord;
import net.minecraft.entity.damage.DamageTracker;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;

@Mixin(DamageTracker.class)
public interface BetterTipsDeathAccessor {
    @Accessor("recentDamage")
    List<DamageRecord> getRecentDamage();

    @Invoker("getBiggestFall")
    DamageRecord getBiggestFall();

    @Invoker("getDisplayName")
    Text getDisplayName(@Nullable Entity entity);
}
