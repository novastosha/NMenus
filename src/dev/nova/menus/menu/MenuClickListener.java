package dev.nova.menus.menu;

import dev.nova.menus.menu.manager.MenuManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.inventory.*;

public class MenuClickListener implements Listener {


    @EventHandler
    public void onInventoryClick(InventoryClickEvent event){
        for(Menu menu : MenuManager.MENUS) {
            if (menu.getInventoryType() != InventoryType.ANVIL) {
                if (menu.getInventories().contains(event.getClickedInventory())) {
                    if (menu.getBorderItem() != null) {
                        if (menu.getBorderSlotList().contains(event.getSlot())) event.setCancelled(true);
                    }
                    switch(event.getAction()){
                        case PLACE_ALL:
                        case PLACE_ONE:
                        case PLACE_SOME:
                            if(!menu.canPutItems()){
                                event.setCancelled(true);
                            }

                    }
                    Slot slot = menu.getSlotFromNumber(event.getSlot());
                    if (slot != null) {
                        if (slot instanceof MenuSlot) {
                            if (!(((MenuSlot) slot).canBePicked())) event.setCancelled(true);
                            ((MenuSlot) slot).executeActions((Player) event.getWhoClicked(), event.getClick());
                        } else {
                            if (!(((MenuAnimatedSlot) slot).canBePicked())) event.setCancelled(true);
                            ((MenuAnimatedSlot) slot).executeActions((Player) event.getWhoClicked(), event.getClick());
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event){
        for(Menu menu : MenuManager.MENUS){

            if(!menu.isShareable()) menu.getInventories().remove(event.getInventory());
        }
    }
}
