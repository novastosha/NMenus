package dev.nova.menus;

import dev.nova.menus.commands.NMenusCommand;
import dev.nova.menus.menu.MenuClickListener;
import dev.nova.menus.menu.actions.manager.ActionManager;
import dev.nova.menus.menu.manager.MenuManager;
import dev.nova.menus.playerdata.PlayerListener;
import dev.nova.menus.register.command.CCommand;
import dev.nova.menus.utils.DebugMessenger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.SimplePluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.util.Arrays;

public class Main extends JavaPlugin {

    public static File MENUS_FOLDER;
    public static DebugMessenger DEBUG;
    private static Main INSTANCE;
    private static SimpleCommandMap scm;
    private SimplePluginManager spm;

    @Override
    public void onEnable() {
        INSTANCE = this;
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
        setupSimpleCommandMap();
        DEBUG = new DebugMessenger(this);
        ActionManager.loadActionAddons(new File("./plugins"));
        int loadedMenus = MenuManager.loadMenus(menus);
        Bukkit.getConsoleSender().sendMessage("["+ ChatColor.YELLOW+"NMenus"+"§r] Loaded: "+loadedMenus+" menu(s)!");
        MenuManager.setLoaded(0);
        Bukkit.getPluginManager().registerEvents(new MenuClickListener(),this);
        Bukkit.getPluginManager().registerEvents(new PlayerListener(),this);

        getCommand("nmenus").setExecutor(new NMenusCommand());

    }

    public static Main getInstance() {
        return INSTANCE;
    }
    public void registerCommands(CCommand... commands) {
        Arrays.stream(commands).forEach(command -> scm.register(getName(), command));
    }

    private void setupSimpleCommandMap() {
        spm = (SimplePluginManager) this.getServer().getPluginManager();
        Field f = null;
        try {
            f = SimplePluginManager.class.getDeclaredField("commandMap");
        } catch (Exception e) {
            e.printStackTrace();
        }
        f.setAccessible(true);
        try {
            scm = (SimpleCommandMap) f.get(spm);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
