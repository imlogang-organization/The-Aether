package com.gildedgames.aether.item.combat.loot;

import com.gildedgames.aether.item.AetherItems;
import com.gildedgames.aether.item.combat.AetherItemTiers;
import com.gildedgames.aether.item.combat.AetherSwordItem;
import com.gildedgames.aether.item.tools.abilities.ValkyrieTool;
import com.google.common.collect.Multimap;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;

public class ValkyrieLanceItem extends AetherSwordItem implements ValkyrieTool {
    public ValkyrieLanceItem() {
        super(AetherItemTiers.VALKYRIE, 3, -2.7F, new Item.Properties().rarity(AetherItems.AETHER_LOOT));
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
        return this.extendReachModifier(super.getAttributeModifiers(slot, stack), slot);
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return enchantment.category == EnchantmentCategory.WEAPON && enchantment != Enchantments.SWEEPING_EDGE;
    }

    @Override
    public boolean canPerformAction(ItemStack stack, ToolAction toolAction) {
        return ToolActions.DEFAULT_SWORD_ACTIONS.contains(toolAction) && toolAction != ToolActions.SWORD_SWEEP;
    }
}
