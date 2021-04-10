package dev.nova.menus.menu.conditions;

import dev.nova.menus.menu.Menu;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

public class HasMoney extends Condition {

    public static final String CODE = "has_money";

    @Override
    public Object check(Player player, ClickType clickType, Menu menu) {
        return true;
    }
}
