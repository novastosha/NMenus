package dev.nova.menus.menu.manager;

import dev.nova.menus.Main;
import dev.nova.menus.menu.Menu;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MenuManager {

    public static final List<Menu> MENUS = new ArrayList<>();

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
        if(!(sender == null)) sender.sendMessage("§7["+ ChatColor.YELLOW+"NMenus"+"§7] Loaded: "+loadMenus(new File(Main.getPlugin(Main.class).getDataFolder()+"/menus"))+" menu(s)");
    }

    public static boolean loadMenu(File file) {
        Bukkit.getConsoleSender().sendMessage("§7["+ ChatColor.YELLOW+"NMenus"+"§7] Loading the menu: §e"+file.getName());
        YamlConfiguration configuration = new YamlConfiguration();

        try {
            configuration.load(file);
        } catch (IOException | InvalidConfigurationException ioException) {
            ioException.printStackTrace();
        }

        if(!configuration.contains("code-name")) {
            Bukkit.getConsoleSender().sendMessage("§7["+ ChatColor.YELLOW+"NMenus"+"§7] The menu: "+file.getName()+" does not contain a code name!");
            return false;
        }
        String codeName = configuration.getString("code-name");
        if(!configuration.contains("display-name")) {
            Bukkit.getConsoleSender().sendMessage("§7["+ ChatColor.YELLOW+"NMenus"+"§7] The menu: "+codeName+" does not contain a display name!");
            return false;
        }
        String displayName = configuration.getString("display-name").replaceAll("\"","").replaceAll("&","§");
        if(!configuration.contains("slots")) {
            Bukkit.getConsoleSender().sendMessage("§7["+ ChatColor.YELLOW+"NMenus"+"§7] The menu: "+displayName+" does not contain any slot!");
            return false;
        }
        String command = null;
        if(configuration.contains("command")){
            command = configuration.getString("command");
            for(Plugin plugin : Bukkit.getPluginManager().getPlugins()){
                for(String commandL : plugin.getDescription().getCommands().keySet()){
                    if(commandL.equals(command)){
                        command = null;
                        Bukkit.getConsoleSender().sendMessage("§7["+ ChatColor.YELLOW+"NMenus"+"§7] The menu: "+displayName+" contains a command that interferes with '"+plugin.getName()+"'");
                        Bukkit.getConsoleSender().sendMessage("§7(Commands will be registered under NMenus in a later update...)");
                        return false;
                    }
                }
            }
        }
        ConfigurationSection slots = configuration.getConfigurationSection("slots");
        slots.getKeys(false).forEach(slot ->{
            ConfigurationSection slotConfig = slots.getConfigurationSection(slot);
            ItemStack item = new ItemStack(Material.getMaterial(slotConfig.getString("material")), slotConfig.getInt("amount"));
            ItemMeta itemMeta = item.getItemMeta();
            String name = slotConfig.getString("name");

            Bukkit.getConsoleSender().sendMessage(name.replaceAll("\"","").replaceAll("&","§"));
            itemMeta.setDisplayName(name.replaceAll("\"","").replaceAll("&","§"));
        });



        return true;
    }
}
