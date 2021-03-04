package dev.nova.menus;

import dev.nova.menus.menus.manager.MenuManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class Main extends JavaPlugin {

    @Override
    public void onEnable() {

        File menus = new File(getDataFolder(),"menus");
        menus.mkdirs();
        int loadedMenus = MenuManager.loadMenus(menus);
        Bukkit.getConsoleSender().sendMessage("["+ ChatColor.YELLOW+"NMenus"+"§r] Loaded: "+loadedMenus+" menu(s)!");
    }
}
