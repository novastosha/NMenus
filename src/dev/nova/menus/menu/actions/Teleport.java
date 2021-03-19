package dev.nova.menus.menu.actions;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

public class Teleport extends Action{

    private final Location location;

    public Teleport(Location location){
        this.location = location;
    }

    public Location getLocation() {
        return location;
    }

    @Override
    public void executeAction(Player player, ClickType clickType) {
        if(!player.teleport(location)){
            player.sendMessage("§cFailed to teleport (Not an NMenus bug)!");
        }
    }
}
