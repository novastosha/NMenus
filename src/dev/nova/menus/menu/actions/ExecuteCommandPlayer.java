package dev.nova.menus.menu.actions;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

public class ExecuteCommandPlayer extends Action{

    private final String command;

    public static final String CODE = "execute_player_command";

    public ExecuteCommandPlayer(String command,String actionId){
        super(actionId);
        this.command = command;
    }

    public String getCommand() {
        return command;
    }

    @Override
    public void executeAction(Player player, ClickType clickType) {
        String fixedActionString = command.replaceAll("\"","").replaceAll("%player%",player.getName()).replaceAll("%player-displayName%", player.getDisplayName());
        player.performCommand(fixedActionString);
    }
}
