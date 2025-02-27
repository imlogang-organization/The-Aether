package com.gildedgames.aether.network.packet.server;

import com.gildedgames.aether.inventory.menu.LoreBookMenu;
import com.gildedgames.aether.network.AetherPacket;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;

public record LoreExistsPacket(int playerID, ItemStack itemStack, boolean exists) implements AetherPacket {
    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(this.playerID());
        buf.writeItem(this.itemStack());
        buf.writeBoolean(this.exists());
    }

    public static LoreExistsPacket decode(FriendlyByteBuf buf) {
        int playerID = buf.readInt();
        ItemStack itemStack = buf.readItem();
        boolean exists = buf.readBoolean();
        return new LoreExistsPacket(playerID, itemStack, exists);
    }

    @Override
    public void execute(Player player) {
        if (player != null && player.getServer() != null && player.getLevel().getEntity(this.playerID()) instanceof ServerPlayer && player.containerMenu instanceof LoreBookMenu menu) {
            menu.setLoreEntryExists(this.exists());
        }
    }
}
