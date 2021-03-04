package dev.nova.menus.menu.actions;

import org.bukkit.entity.Player;

public abstract class Action {

    public abstract void executeAction(Player player, ClickType clickType);

}
