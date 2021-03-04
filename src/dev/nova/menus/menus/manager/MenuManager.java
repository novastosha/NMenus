package dev.nova.menus.menus.manager;

import dev.nova.menus.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.io.File;
import java.util.Objects;

public class MenuManager {

    public static int loadMenus(File menus) {
        int loaded = 0;
        Bukkit.getConsoleSender().sendMessage("["+ ChatColor.YELLOW+"NMenus"+"§r] §rLoading menus in: "+menus.getPath());
        for(File file : Objects.requireNonNull(menus.listFiles())) {
            if(!file.isDirectory()){
                if(file.getName().endsWith(".yml") && !file.getName().startsWith("-")){
                    if(loadMenu(file)) loaded++;
                }
            }else{
                loadMenus(file);
            }
        }
        return loaded;
    }

    public static void reloadMenus(CommandSender sender){
        if(!(sender == null)) sender.sendMessage("§7["+ ChatColor.YELLOW+"NMenus"+"§7] Reloading menus...");
        loadMenus(new File(Main.getPlugin(Main.class).getDataFolder()+"/menus"));
    }

    public static boolean loadMenu(File file) {

    }
}
