package dev.nova.menus;

import dev.nova.menus.commands.CustomCommandListener;
import dev.nova.menus.commands.NMenusCommand;
import dev.nova.menus.menu.MenuClickListener;
import dev.nova.menus.menu.manager.MenuManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class Main extends JavaPlugin {

    public static File MENUS_FOLDER;

    @Override
    public void onEnable() {

        File menus = new File(getDataFolder(),"menus");
        MENUS_FOLDER = menus;
        menus.mkdirs();
        int loadedMenus = MenuManager.loadMenus(menus);
        Bukkit.getConsoleSender().sendMessage("["+ ChatColor.YELLOW+"NMenus"+"§r] Loaded: "+loadedMenus+" menu(s)!");
        Bukkit.getPluginManager().registerEvents(new MenuClickListener(),this);
        Bukkit.getPluginManager().registerEvents(new CustomCommandListener(),this);
        //Bukkit.getPluginManager().registerEvents(new EditorManager(),this);
        getCommand("nmenus").setExecutor(new NMenusCommand());

    }
}
