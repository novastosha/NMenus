package dev.nova.menus.menu.actions;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

public class CloseMenu extends Action{

    public static final String CODE = "close_menu";

    public CloseMenu(String id) {
        super(id);
    }

    @Override
    public void executeAction(Player player, ClickType clickType) {
        player.closeInventory();
    }
}
