package dev.nova.menus.playerdata;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {

    /*@EventHandler(priority = EventPriority.LOWEST)
    public void antiBlockSteal(InventoryClickEvent ice) {
        if (PlayerDataManager.getData((Player)ice.getWhoClicked()).isInMenu()) ice.setCancelled(true);
    }*/

    @EventHandler(priority = EventPriority.MONITOR)
    public void addPlayerData(PlayerJoinEvent e) {
        PlayerDataManager.addData(e.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void removePlayerData(PlayerQuitEvent e) {
        PlayerDataManager.deleteData(e.getPlayer());
    }

}
