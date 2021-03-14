package dev.nova.menus.menu;

import net.minecraft.server.v1_12_R1.EntityHuman;
import net.minecraft.server.v1_12_R1.EntityPlayer;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public interface CustomMenuInventory{
    static EntityPlayer toNMS(Player player) {
        return ((CraftPlayer) player).getHandle();
    }
}
