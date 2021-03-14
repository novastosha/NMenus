package dev.nova.menus.menu.actions;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

public abstract class Action {

    public abstract void executeAction(Player player, ClickType clickType);

}
