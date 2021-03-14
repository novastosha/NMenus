package dev.nova.menus.menu.actions;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

public class SendMessage extends Action{

    private final String message;

    public SendMessage(String message){
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public void executeAction(Player player, ClickType clickType) {
        player.sendMessage(message);
    }
}
