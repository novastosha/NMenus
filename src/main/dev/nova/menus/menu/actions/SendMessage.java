package dev.nova.menus.menu.actions;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

public class SendMessage extends Action{

    private final String message;

    public static final String CODE = "send_message";

    public SendMessage(String actionId,String message){
        super(actionId);
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
