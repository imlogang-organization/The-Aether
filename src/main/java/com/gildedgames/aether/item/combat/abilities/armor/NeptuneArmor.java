package com.gildedgames.aether.item.combat.abilities.armor;

import com.gildedgames.aether.capability.player.AetherPlayer;
import com.gildedgames.aether.util.EquipmentUtil;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingEvent;

public interface NeptuneArmor {
    /**
     * Boosts the entity's movement in water or bubble columns if wearing a full set of Neptune Armor. The default boost is a multiplier of 1.55, but is modified based on duration in water and whether the boots have Depth Strider.
     * @param entity The {@link LivingEntity} wearing the armor.
     * @see com.gildedgames.aether.event.listeners.abilities.ArmorAbilityListener#onEntityUpdate(LivingEvent.LivingTickEvent)
     */
    static void boostWaterSwimming(LivingEntity entity) {
        if (EquipmentUtil.hasFullNeptuneSet(entity)) {
            if (entity.isInWaterOrBubble()) {
                if (entity instanceof Player player) {
                    AetherPlayer.get(player).ifPresent((aetherPlayer) -> {
                        float defaultBoost = boostWithDepthStrider(entity);
                        aetherPlayer.setNeptuneSubmergeLength(Math.min(aetherPlayer.getNeptuneSubmergeLength() + 0.1, 1.0));
                        defaultBoost *= aetherPlayer.getNeptuneSubmergeLength();
                        Vec3 movement = entity.getDeltaMovement().multiply(defaultBoost, 0.25F, defaultBoost);
                        entity.move(MoverType.SELF, movement);
                    });
                } else {
                    float defaultBoost = boostWithDepthStrider(entity);
                    Vec3 movement = entity.getDeltaMovement().multiply(defaultBoost, 0.25F, defaultBoost);
                    entity.move(MoverType.SELF, movement);
                }
            }
        }
        if (!EquipmentUtil.hasFullNeptuneSet(entity) || !entity.isInWaterOrBubble()) {
            if (entity instanceof Player player) {
                AetherPlayer.get(player).ifPresent((aetherPlayer) -> aetherPlayer.setNeptuneSubmergeLength(0.0));
            }
        }
    }

    /**
     * Adds an extra 1.05 to the boost for every Depth Strider level up to Depth Strider 3.
     * @param entity The {@link LivingEntity} wearing the armor.
     * @return The modified boost as a {@link Float}.
     */
    private static float boostWithDepthStrider(LivingEntity entity) {
        float defaultBoost = 1.55F;
        float depthStriderModifier = Math.min(EnchantmentHelper.getDepthStrider(entity), 3.0F);
        if (depthStriderModifier > 0.0F) {
            defaultBoost += depthStriderModifier * 1.05F;
        }
        return defaultBoost;
    }
}
