/**
 * REQUIRES BUNGEECORD
 */
package dev.nova.menus.menu.actions;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

public class ConnectToServer extends Action{

    public static final String CODE = "connect_to_server";

    protected ConnectToServer(String id) {
        super(id);
    }

    @Override
    public void executeAction(Player player, ClickType clickType) {

    }
}
