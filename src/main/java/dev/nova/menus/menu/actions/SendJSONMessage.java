package dev.nova.menus.menu.actions;

import dev.nova.menus.menu.manager.MenuManager;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

public class SendJSONMessage extends Action{

    private final ConfigurationSection message;

    public static final String CODE = "send_json_message";
    private final String actionId;

    public SendJSONMessage(ConfigurationSection message,String actionId){
        super(actionId);
        this.actionId = actionId;
        this.message = message;
    }

    public ConfigurationSection getMessage() {
        return message;
    }

    @Override
    public void executeAction(Player player, ClickType clickType) {
        player.spigot().sendMessage(MenuManager.buildText(actionId,message));
    }
}
