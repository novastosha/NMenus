package dev.nova.menus.menu.conditions;

import dev.nova.menus.menu.Menu;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

public abstract class Condition {

    public abstract Object check(Player player, ClickType clickType, Menu menu);

}
