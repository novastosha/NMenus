package dev.nova.menus.menu.manager;

import dev.nova.menus.Main;
import dev.nova.menus.menu.*;
import dev.nova.menus.menu.actions.Action;
import dev.nova.menus.menu.actions.ConsoleExecuteCommand;
import dev.nova.menus.menu.actions.ExecuteCommandPlayer;
import dev.nova.menus.menu.actions.SendMessage;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class MenuManager {

    public static List<Menu> MENUS = new ArrayList<>();

    public static int loadMenus(File menus) {
        int loaded = 0;
        Bukkit.getConsoleSender().sendMessage("[" + ChatColor.YELLOW + "NMenus" + "§r] §rLoading menus in: " + menus.getPath());
        for (File file : Objects.requireNonNull(menus.listFiles())) {
            if (!file.isDirectory()) {
                if (file.getName().endsWith(".yml") && !file.getName().startsWith("-")) {
                    if (loadMenu(file)) loaded++;

                }
            } else {
                loadMenus(file);
            }
        }
        return loaded;
    }

    public static void reloadMenus(CommandSender sender) {
        if (!(sender == null)) sender.sendMessage("§7[" + ChatColor.YELLOW + "NMenus" + "§7] Reloading menus...");
        for(Menu menu : MENUS){
            Bukkit.getServer().getScheduler().cancelTask(menu.getRefreshTaskID());
        }
        MENUS = new ArrayList<>();
        if (!(sender == null))
            sender.sendMessage("§7[" + ChatColor.YELLOW + "NMenus" + "§7] Loaded: " + loadMenus(new File(Main.getPlugin(Main.class).getDataFolder() + "/menus")) + " menu(s)");
    }

    public static boolean loadMenu(File file) {
        Bukkit.getConsoleSender().sendMessage("§7[" + ChatColor.YELLOW + "NMenus" + "§7] Loading the menu: §e" + file.getName());
        YamlConfiguration configuration = new YamlConfiguration();

        try {
            configuration.load(file);
        } catch (IOException | InvalidConfigurationException ioException) {
            ioException.printStackTrace();
        }

        if (!configuration.contains("code-name")) {
            Bukkit.getConsoleSender().sendMessage("§7[" + ChatColor.YELLOW + "NMenus" + "§7] The menu: " + file.getName() + " does not contain a code name!");
            return false;
        }
        String codeName = configuration.getString("code-name");
        if (!configuration.contains("display-name")) {
            Bukkit.getConsoleSender().sendMessage("§7[" + ChatColor.YELLOW + "NMenus" + "§7] The menu: " + codeName + " does not contain a display name!");
            return false;
        }
        String displayName = configuration.getString("display-name").replaceAll("\"", "").replaceAll("&", "§");
        String command = null;
        if (configuration.contains("command")) {
            command = configuration.getString("command");
            for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
                for (String commandL : plugin.getDescription().getCommands().keySet()) {
                    if (commandL.equals(command)) {
                        command = null;
                        Bukkit.getConsoleSender().sendMessage("§7[" + ChatColor.YELLOW + "NMenus" + "§7] The menu: " + codeName + "§r contains a command that interferes with '" + plugin.getName() + "'");
                        Bukkit.getConsoleSender().sendMessage("§7(Commands will be registered under NMenus in a later update...)");
                        return false;
                    }
                }
            }
            for (Menu menu : MENUS) {
                if (menu.getCommandName().equals(command)) {
                    command = null;
                    Bukkit.getConsoleSender().sendMessage("§7[" + ChatColor.YELLOW + "NMenus" + "§7] The menu: " + codeName + "§r contains a command that interferes with an other menu '" + menu.getCodeName() + "'");
                    return false;
                }
            }
        }

        InventoryType inventoryType = null;
        if (configuration.contains("inventoryType")) {
            try {
                inventoryType = InventoryType.valueOf(configuration.getString("inventoryType").toUpperCase());
            } catch (IllegalArgumentException e) {
                inventoryType = null;
                Bukkit.getConsoleSender().sendMessage("§7[" + ChatColor.YELLOW + "NMenus" + "§7] The menu: " + codeName + "§r contains an inventory type that is unknown! (" + configuration.getString("inventoryType") + ")");
                return false;
            }

        } else {
            Bukkit.getConsoleSender().sendMessage("§7[" + ChatColor.YELLOW + "NMenus" + "§7] The menu: " + codeName + "§r does not a specific a inventory type!");
            return false;
        }

        int rows = 0;
        if (configuration.contains("rows")) {
            if (configuration.getString("inventoryType").equalsIgnoreCase("CHEST")) {
                rows = configuration.getInt("rows");
            } else {
                Bukkit.getConsoleSender().sendMessage("§7[" + ChatColor.YELLOW + "NMenus" + "§7] The menu: " + codeName + "§r contains a size value but the inventory type is not 'chest', Ignoring...");
            }
        } else if (configuration.getString("inventoryType").equalsIgnoreCase("chest")) {
            Bukkit.getConsoleSender().sendMessage("§7[" + ChatColor.YELLOW + "NMenus" + "§7] The menu: " + codeName + "§r does not contain rows number!");
            return false;
        }

        boolean shareable = false;
        if (configuration.contains("shareable")) {
            shareable = configuration.getBoolean("shareable");
        }

        Material borderType = null;
        MenuBorderColor borderColor = null;
        int borderRefresh = 1;
        if(configuration.contains("border")){
            try {
                borderType = Material.getMaterial(configuration.getString("border").toUpperCase());
            }catch (IllegalArgumentException e){
                Bukkit.getConsoleSender().sendMessage("§7[" + ChatColor.YELLOW + "NMenus" + "§7] The menu: " + codeName + "§r contains a border item that is unknown!");
            }
            if(configuration.contains("border-color")) {
                assert borderType != null;
                if (borderType.equals(Material.GLASS)) {
                        try {
                            borderColor = MenuBorderColor.valueOf(configuration.getString("border-color").toUpperCase());

                        } catch (IllegalArgumentException e) {
                            Bukkit.getConsoleSender().sendMessage("§7[" + ChatColor.YELLOW + "NMenus" + "§7] The menu: " + codeName + "§r contains a border color that is unknown");
                        }
                    if(configuration.contains("border-refresh") && borderType.equals(Material.GLASS) && borderColor.equals(MenuBorderColor.RAINBOW)){
                        borderRefresh = configuration.getInt("border-refresh");
                    }
                }
            }
        }

        List<Slot> slotsArray = new ArrayList<>();

        if (configuration.contains("slots")) {
            ConfigurationSection slots = configuration.getConfigurationSection("slots");
            int finalRows = rows;
            Material finalBorderType = borderType;
            slots.getKeys(false).forEach(slot -> {

                ConfigurationSection slotConfig = slots.getConfigurationSection(slot);
                ItemStack item = null;

                if(!slotConfig.contains("animation")) item = makeSlot(slotConfig,slot,codeName);
                boolean canBePicked = false;
                if (slotConfig.contains("canBePicked")) {
                    canBePicked = slotConfig.getBoolean("canBePicked");
                }
                HashMap<Action,ClickType> actions = new HashMap<>();

                if (slotConfig.contains("actions")) {
                    ConfigurationSection actionsConfig = slotConfig.getConfigurationSection("actions");
                    actionsConfig.getKeys(false).forEach(actionType -> {
                        try {
                            ClickType clickType = ClickType.valueOf(actionType.toUpperCase());
                            ConfigurationSection actionConfig = slotConfig.getConfigurationSection("actions."+actionType);
                            actionConfig.getKeys(false).forEach(action -> {
                                if(action.contains("execute_player_command_")){
                                    actions.put(new ExecuteCommandPlayer(actionConfig.getString(action).replaceAll("\"", "")),clickType);
                                }
                                if(action.contains("execute_console_command_")){
                                    actions.put(new ExecuteCommandPlayer(actionConfig.getString(action).replaceAll("\"", "")),clickType);
                                }
                                if(action.contains("send_message_")){
                                    actions.put(new SendMessage(actionConfig.getString(action).replaceAll("\"", "")),clickType);
                                }
                            });
                        }catch (IllegalArgumentException e){
                            Bukkit.getConsoleSender().sendMessage("§7[" + ChatColor.YELLOW + "NMenus" + "§7] The menu: " + codeName + "§r slot: " + slot + " contains a click type that is unknown ("+actionType+")");
                        }
                    });
                }

                String[] slotsTo = slot.split(",");
                if(slotsTo.length != 1) {
                    for (String slotS : slotsTo) {
                        String[] between = slotS.split("-");
                        int first = Integer.parseInt(between[0]);
                        int second;
                        try{
                            second = Integer.parseInt(between[1]);
                        }catch (ArrayIndexOutOfBoundsException e){
                            second = Integer.parseInt(between[0]);
                        }

                        for (int i = Integer.parseInt(between[0]); i <= second; i++) {
                            if(generateBorderSlotList(finalRows).contains(i) && finalBorderType != null) {
                                Bukkit.getConsoleSender().sendMessage("§7[" + ChatColor.YELLOW + "NMenus" + "§7] The menu: " + codeName + "§r slot: " + i + " interferes with the border slots!");
                                return;
                            }
                            if(!slotConfig.contains("animation")) slotsArray.add(new MenuSlot(i, item, actions, canBePicked));
                            else {
                                List<MenuSlot> items = new ArrayList<>();

                                ConfigurationSection animationConfig = slotConfig.getConfigurationSection("animation.frames");
                                boolean finalCanBePicked = canBePicked;
                                animationConfig.getKeys(false).forEach(frame -> {
                                    ConfigurationSection frameConfig = animationConfig.getConfigurationSection(frame);
                                    items.add(new MenuSlot(-1,makeSlot(frameConfig,frame,codeName),actions, finalCanBePicked));
                                });
                                slotsArray.add(new MenuAnimatedSlot(i,actions,canBePicked,slotConfig.getConfigurationSection("animation").getInt("refresh-rate"), items));
                            }
                        }
                    }
                }else if (slotsTo.length == 1){
                    if(!slotConfig.contains("animation")){
                        if(generateBorderSlotList(finalRows).contains(Integer.parseInt(slot)) && finalBorderType != null) {
                            Bukkit.getConsoleSender().sendMessage("§7[" + ChatColor.YELLOW + "NMenus" + "§7] The menu: " + codeName + "§r slot: " + slot + " interferes with the border slots!");
                            return;
                        }
                        slotsArray.add(new MenuSlot(Integer.parseInt(slot), item, actions, canBePicked));
                    }
                    else{
                        if(generateBorderSlotList(finalRows).contains(Integer.parseInt(slot)) && finalBorderType != null) {
                            Bukkit.getConsoleSender().sendMessage("§7[" + ChatColor.YELLOW + "NMenus" + "§7] The menu: " + codeName + "§r slot: " + slot + " interferes with the border slots!");
                            return;
                        }
                        List<MenuSlot> items = new ArrayList<>();

                        ConfigurationSection animationConfig = slotConfig.getConfigurationSection("animation.frames");
                        boolean finalCanBePicked = canBePicked;
                        animationConfig.getKeys(false).forEach(frame -> {
                               ConfigurationSection frameConfig = animationConfig.getConfigurationSection(frame);
                               items.add(new MenuSlot(-1,makeSlot(frameConfig,frame,codeName),actions, finalCanBePicked));
                        });
                        slotsArray.add(new MenuAnimatedSlot(Integer.parseInt(slot),actions,canBePicked,slotConfig.getConfigurationSection("animation").getInt("refresh-rate"), items));
                    }
                }
            });
        }
        Menu menu = new Menu(codeName, displayName, command, slotsArray, inventoryType, rows, shareable,configuration,file,borderColor,borderType,borderRefresh);
        for (Slot slot : slotsArray) {

            if(slot instanceof MenuSlot) ((MenuSlot)slot).setMenu(menu);
            else ((MenuAnimatedSlot) slot).setMenu(menu);
        }
        MENUS.add(menu);

        return true;
    }

    private static ItemStack makeSlot(ConfigurationSection slotConfig,String slot, String codeName) {
        ItemStack item = new ItemStack(Material.getMaterial(slotConfig.getString("material")), slotConfig.getInt("amount"));

        ItemMeta itemMeta = item.getItemMeta();
        List<String> flags = new ArrayList<>();
        if (slotConfig.contains("flags")) {
            flags = slotConfig.getStringList("flags");
        }
        for (String flag : flags) {
            flag = flag.toUpperCase();
            try {
                ItemFlag itemFlag = ItemFlag.valueOf(flag);
                itemMeta.addItemFlags(itemFlag);
            } catch (IllegalArgumentException e) {
                Bukkit.getConsoleSender().sendMessage("§7[" + ChatColor.YELLOW + "NMenus" + "§7] The menu: " + codeName + "§r slot: " + slot + " contains a flag that is unknown (" + flag + ")");
            }
        }
        List<String> enchants = new ArrayList<>();
        if (slotConfig.contains("enchants")) {
            enchants = slotConfig.getStringList("enchants");
        }
        for (String enchant : enchants) {
            enchant = enchant.replaceAll("\"", "");
            int level = 0;
            try {
                level = Integer.parseInt(stripNonDigits(enchant));
            } catch (NumberFormatException e) {
                Bukkit.getConsoleSender().sendMessage("§7[" + ChatColor.YELLOW + "NMenus" + "§7] The menu: " + codeName + "§r slot: " + slot + " the enchant level is not correct!");
            }
            enchant = enchant.replaceAll("[0-9]", "");
            enchant = enchant.replaceAll("\\(", "");
            enchant = enchant.replaceAll("\\)", "");
            try {
                Enchantment enchantment = Enchantment.getByName(enchant);
                itemMeta.addEnchant(enchantment, level, true);

            } catch (IllegalArgumentException e) {
                Bukkit.getConsoleSender().sendMessage("§7[" + ChatColor.YELLOW + "NMenus" + "§7] The menu: " + codeName + "§r slot: " + slot + " contains an enchant that is unknown (" + enchant + ")");
            }

        }
        if (slotConfig.contains("name")) {
            String name = slotConfig.getString("name");
            itemMeta.setDisplayName(name.replaceAll("\"", "").replaceAll("&", "§"));
        }
        item.setItemMeta(itemMeta);

        return item;
    }

    private static String stripNonDigits(
            final CharSequence input) {
        final StringBuilder sb = new StringBuilder(
                input.length());
        for (int i = 0; i < input.length(); i++) {
            final char c = input.charAt(i);
            if (c > 47 && c < 58) {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    public static Menu getByCodeName(String codeName) {
        for (Menu menu : MENUS) {
            if (menu.getCodeName().equals(codeName)) return menu;
        }
        return null;
    }

    public static List<Integer> generateBorderSlotList(int rows) {
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i <= 8; i++) {
            list.add(i);
        }

        for (int i = rows*9 - 9; i < rows*9; i++) {
            list.add(i);
        }

        int inRow = 9;
        while(inRow < rows*9 - 9){
            list.add(inRow);
            inRow = inRow+9;
        }

        int inRow2 = 17;
        while(inRow2 <= rows*9 - 9){
            list.add(inRow2);
            inRow2 = inRow2+9;
        }
        return list;
    }
}
