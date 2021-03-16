package dev.nova.menus.menu;

import dev.nova.menus.menu.manager.MenuManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;

public class MenuClickListener implements Listener {


    @EventHandler
    public void onInventoryClick(InventoryClickEvent event){
        for(Menu menu : MenuManager.MENUS){
            if(menu.getInventories().contains(event.getClickedInventory())) {
                    if(menu.getBorderItem() != null){
                        if(menu.getBorderSlotList().contains(event.getSlot())) event.setCancelled(true);
                    }
                    Slot slot = menu.getSlotFromNumber(event.getSlot());
                    if (slot != null) {
                        if (slot instanceof MenuSlot) {
                            if (!(((MenuSlot)slot).canBePicked())) event.setCancelled(true);
                            ((MenuSlot)slot).executeActions((Player) event.getWhoClicked(), event.getClick());
                        }else{
                            if (!(((MenuAnimatedSlot)slot).canBePicked())) event.setCancelled(true);
                            ((MenuAnimatedSlot)slot).executeActions((Player) event.getWhoClicked(), event.getClick());
                        }
                    }
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event){
        for(Menu menu : MenuManager.MENUS){
            menu.getInventories().remove(event.getInventory());
        }
    }
}
