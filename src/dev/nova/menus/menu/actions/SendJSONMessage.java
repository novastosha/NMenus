package dev.nova.menus.menu.actions;

import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

public class SendJSONMessage extends Action{

    private final TextComponent message;

    public SendJSONMessage(TextComponent message){
        this.message = message;
    }

    public TextComponent getMessage() {
        return message;
    }

    @Override
    public void executeAction(Player player, ClickType clickType) {
        player.spigot().sendMessage(message);
    }
}
