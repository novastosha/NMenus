package dev.nova.menus.commands;

import dev.nova.menus.menu.Menu;
import dev.nova.menus.menu.manager.MenuManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class CustomCommandListener implements Listener {

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event){
        long start = System.currentTimeMillis();
        for(Menu menu : MenuManager.MENUS){
            if(menu.getCommandName() != null && event.getMessage().equals("/"+menu.getCommandName())){
                menu.openInventory(event.getPlayer());
                event.setCancelled(true);
            }
        }
        event.getPlayer().sendMessage("Response: "+(System.currentTimeMillis()-start));
    }

}
