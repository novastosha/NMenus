package dev.nova.menus;

import dev.nova.menus.commands.CustomCommandListener;
import dev.nova.menus.commands.NMenusCommand;
import dev.nova.menus.menu.MenuClickListener;
import dev.nova.menus.menu.anvil.AnvilGUI;
import dev.nova.menus.menu.manager.MenuManager;
import dev.nova.menus.utils.DebugMessenger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.function.BiFunction;

public class Main extends JavaPlugin {

    public static File MENUS_FOLDER;
    public static DebugMessenger DEBUG;

    @Override
    public void onEnable() {

        File menus = new File(getDataFolder(),"menus");
        MENUS_FOLDER = menus;
        menus.mkdirs();
        File config = new File(getDataFolder(),"config.yml");
        try {
            config.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Reader defConfigStream = null;
        try {
            defConfigStream = new InputStreamReader(getResource("config.yml"), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (defConfigStream != null) {
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
            getConfig().setDefaults(defConfig);
            getConfig().options().copyDefaults(true);
            saveConfig();
        }
        DEBUG = new DebugMessenger(this);
        int loadedMenus = MenuManager.loadMenus(menus);
        Bukkit.getConsoleSender().sendMessage("["+ ChatColor.YELLOW+"NMenus"+"§r] Loaded: "+loadedMenus+" menu(s)!");
        Bukkit.getPluginManager().registerEvents(new MenuClickListener(),this);
        Bukkit.getPluginManager().registerEvents(new CustomCommandListener(),this);
        //Bukkit.getPluginManager().registerEvents(new EditorManager(),this);
        getCommand("nmenus").setExecutor(new NMenusCommand());

    }
}
