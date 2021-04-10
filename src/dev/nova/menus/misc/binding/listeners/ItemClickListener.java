package dev.nova.menus.misc.binding.listeners;

import dev.nova.menus.Main;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class ItemClickListener implements Listener {

    @EventHandler
    public void onClick(PlayerInteractEvent event){
        if(event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK) && !event.getClickedBlock().getType().equals(Material.CHEST)){
            if(Main.WRAPPER.get().clickItem(event.getPlayer())) event.setCancelled(true);
        }else if( event.getAction().equals(Action.RIGHT_CLICK_BLOCK) && event.getClickedBlock().getType().equals(Material.CHEST)){
            //Chest binding...
        }
    }

}
