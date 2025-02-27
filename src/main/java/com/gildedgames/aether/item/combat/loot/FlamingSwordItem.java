package com.gildedgames.aether.item.combat.loot;

import com.gildedgames.aether.Aether;
import com.gildedgames.aether.item.combat.AetherItemTiers;
import com.gildedgames.aether.item.AetherItems;
import com.gildedgames.aether.item.combat.AetherSwordItem;
import com.gildedgames.aether.util.EquipmentUtil;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import net.minecraft.world.item.Item;

@Mod.EventBusSubscriber(modid = Aether.MODID)
public class FlamingSwordItem extends AetherSwordItem {
	public FlamingSwordItem() {
		super(AetherItemTiers.FLAMING, 3, -2.4F, new Item.Properties().rarity(AetherItems.AETHER_LOOT));
	}

	/**
	 * @see FlamingSwordItem#handleFlamingSwordAbility(LivingEntity, DamageSource) 
	 */
	@SubscribeEvent
	public static void onLivingDamage(LivingDamageEvent event) {
		LivingEntity target = event.getEntity();
		DamageSource damageSource = event.getSource();
		handleFlamingSwordAbility(target, damageSource);
	}

	/**
	 * Inflicts 30 seconds of fire on the target with an extra 4 seconds for every level of Fire Aspect the item has. This occurs if the attacker attacked with full strength as determined by {@link EquipmentUtil#isFullStrength(LivingEntity)}.
	 * @param target The damaged {@link LivingEntity}.
	 * @param source The attacking {@link DamageSource}.
	 */
	private static void handleFlamingSwordAbility(LivingEntity target, DamageSource source) {
		if (source.getDirectEntity() instanceof LivingEntity attacker) {
			if (EquipmentUtil.isFullStrength(attacker)) {
				ItemStack heldStack = attacker.getMainHandItem();
				if (heldStack.is(AetherItems.FLAMING_SWORD.get())) {
					int defaultTime = 30;
					int fireAspectModifier = EnchantmentHelper.getFireAspect(attacker);
					if (fireAspectModifier > 0) {
						defaultTime += (fireAspectModifier * 4);
					}
					target.setSecondsOnFire(defaultTime);
				}
			}
		}
	}
}
