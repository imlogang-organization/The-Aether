package com.gildedgames.aether.entity.projectile;

import com.gildedgames.aether.entity.projectile.dart.AbstractDart;
import com.gildedgames.aether.effect.AetherEffects;
import com.gildedgames.aether.entity.AetherEntityTypes;
import com.gildedgames.aether.mixin.mixins.common.accessor.PlayerAccessor;
import com.gildedgames.aether.network.AetherPacketHandler;
import com.gildedgames.aether.network.packet.client.ZephyrSnowballHitPacket;
import com.gildedgames.aether.util.EquipmentUtil;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

import javax.annotation.Nonnull;

public class PoisonNeedle extends AbstractDart {
    public PoisonNeedle(EntityType<? extends PoisonNeedle> type, Level level) {
        super(type, level);
        this.setBaseDamage(1.0);
    }

    public PoisonNeedle(Level level, LivingEntity shooter) {
        super(AetherEntityTypes.POISON_NEEDLE.get(), shooter, level);
        this.setBaseDamage(1.0);
    }

    @Override
    protected void onHit(@Nonnull HitResult result) {
        super.onHit(result);
        if (result.getType() == HitResult.Type.ENTITY) {
            Entity entity = ((EntityHitResult) result).getEntity();
            if (entity instanceof Player player && player.isBlocking()) {
                PlayerAccessor playerAccessor = (PlayerAccessor) player;
                playerAccessor.callHurtCurrentlyUsedShield(3.0F);
            }
        }
    }

    @Override
    protected void doPostHurtEffects(@Nonnull LivingEntity living) {
        super.doPostHurtEffects(living);
        living.addEffect(new MobEffectInstance(AetherEffects.INEBRIATION.get(), 500, 0));
    }

    @Nonnull
    @Override
    protected ItemStack getPickupItem() {
        return ItemStack.EMPTY;
    }
}
