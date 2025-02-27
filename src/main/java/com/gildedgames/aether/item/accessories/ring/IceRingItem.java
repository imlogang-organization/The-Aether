package com.gildedgames.aether.item.accessories.ring;

import com.gildedgames.aether.AetherTags;
import com.gildedgames.aether.client.AetherSoundEvents;
import com.gildedgames.aether.item.accessories.abilities.FreezingAccessory;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;

public class IceRingItem extends RingItem implements FreezingAccessory {
    public IceRingItem(Properties properties) {
        super(AetherSoundEvents.ITEM_ACCESSORY_EQUIP_ICE_RING, properties);
    }

    @Override
    public boolean isValidRepairItem(ItemStack repairItem, ItemStack repairMaterial) {
        return repairMaterial.is(AetherTags.Items.ICE_REPAIRING);
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        this.freezeTick(slotContext, stack);
    }
}
