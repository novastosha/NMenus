package dev.nova.menus.menu.actions;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

public class CloseMenu extends Action{

    @Override
    public void executeAction(Player player, ClickType clickType) {
        player.closeInventory();
    }
}
