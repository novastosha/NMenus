package dev.nova.menus.utils;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class DebugMessenger
{
    private final Plugin plugin;

    public DebugMessenger(Plugin menus){
        this.plugin = menus;
    }

    public void sendMLM(String message) {
        if(plugin.getConfig().getBoolean("more-loading-messages")){
            Bukkit.getConsoleSender().sendMessage("§7[§eNMenus§7] "+message);
        }
    }
}
