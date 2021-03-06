package dev.nova.menus.menu.manager;

import dev.nova.menus.Main;
import dev.nova.menus.menu.*;
import dev.nova.menus.menu.actions.Action;
import dev.nova.menus.menu.actions.base.Pram;
import dev.nova.menus.menu.actions.base.RawAction;
import dev.nova.menus.menu.actions.manager.ActionManager;
import dev.nova.menus.register.command.CCommand;
import dev.nova.menus.utils.head.Head;
import dev.nova.menus.utils.head.NBase64;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.*;

public class MenuManager {

    public static List<Menu> MENUS = new ArrayList<>();
    private static int loaded;

    public static int loadMenus(File menus) {
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

    public static void setLoaded(int loaded) {
        MenuManager.loaded = loaded;
    }

    public static void reloadMenus(CommandSender sender) {
        if (!(sender == null)) sender.sendMessage("§7[" + ChatColor.YELLOW + "NMenus" + "§7] Reloading menus...");
        for(Menu menu : MENUS){
            Bukkit.getServer().getScheduler().cancelTask(menu.getRefreshTaskID());
            for(Integer integer : menu.getSlotTaskIDies()){
                Bukkit.getServer().getScheduler().cancelTask(integer);
            }
        }

        MENUS = new ArrayList<>();
        sender.sendMessage("§7[" + ChatColor.YELLOW + "NMenus" + "§7] Loaded: " + loadMenus(Main.MENUS_FOLDER) + " menu(s)");
        MenuManager.setLoaded(0);
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
        Main.DEBUG.sendMLM("Determined: §e"+codeName+"§7 as a code-name for the menu: §e"+file.getName());
        if (!configuration.contains("display-name")) {
            Bukkit.getConsoleSender().sendMessage("§7[" + ChatColor.YELLOW + "NMenus" + "§7] The menu: " + codeName + " does not contain a display name!");
            return false;
        }
        String displayName = configuration.getString("display-name").replaceAll("\"", "").replaceAll("&", "§");
        Main.DEBUG.sendMLM("Set the display-name (menu-title) of the menu: §e"+codeName+" §7to: §r"+displayName);
        List<String> command = null;
        if (configuration.contains("commands")) {
            command = configuration.getStringList("commands");
            for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
                if(plugin.getDescription().getCommands() != null) {
                    for (String commandL : plugin.getDescription().getCommands().keySet()) {
                        for (String mCmd : command) {
                            if (commandL.equals(mCmd)) {
                                command = null;
                                Bukkit.getConsoleSender().sendMessage("§7[" + ChatColor.YELLOW + "NMenus" + "§7] The menu: " + codeName + "§r contains a command that interferes with '" + plugin.getName() + "'");
                                Bukkit.getConsoleSender().sendMessage("§7(Commands will be registered under NMenus in a later update...)");
                                return false;
                            }
                        }
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

        Main.DEBUG.sendMLM("Set: §e"+inventoryType.toString()+" §7as inventory type of menu:§e "+codeName);
        boolean shareable = false;
        if (configuration.contains("shareable")) {
            shareable = configuration.getBoolean("shareable");
        }

        if(shareable) Main.DEBUG.sendMLM("Set the menu: §e"+codeName+"§7 as a shareable menu!");

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
        boolean canPutItems = false;
        if(configuration.contains("can-put-items")){
            canPutItems = configuration.getBoolean("can-put-items");
        }

        List<Slot> slotsArray = new ArrayList<>();

        if (configuration.contains("slots")) {
            ConfigurationSection slots = configuration.getConfigurationSection("slots");
            int finalRows = rows;
            Material finalBorderType = borderType;
            slots.getKeys(false).forEach(slot -> {

                ConfigurationSection slotConfig = slots.getConfigurationSection(slot);
                ItemStack item = null;
                boolean canBePicked = false;
                if (slotConfig.contains("canBePicked")) {
                    canBePicked = slotConfig.getBoolean("canBePicked");
                }
                if(!slotConfig.contains("animation")) item = makeSlot(slotConfig,slot,codeName);
                else{
                    if(canBePicked){
                        canBePicked = false;
                    }
                }
                HashMap<Action,ClickType> actions = new HashMap<>();

                if (slotConfig.contains("actions")) {
                    ConfigurationSection actionsConfig = slotConfig.getConfigurationSection("actions");
                    actionsConfig.getKeys(false).forEach(actionType -> {
                        try {
                            ClickType clickType = ClickType.valueOf(actionType.toUpperCase());
                            ConfigurationSection actionConfig = slotConfig.getConfigurationSection("actions."+actionType);
                            actionConfig.getKeys(false).forEach(action -> {
                                for(RawAction clazz : ActionManager.ACTIONS){
                                    String code = ActionManager.getCode(clazz);
                                    if(code != null){
                                        if(action.contains(code+"_")){
                                            String id = action.replaceFirst(code+"_","");
                                            try {
                                                ArrayList<Class<?>> array = new ArrayList<Class<?>>(50);
                                                ArrayList<Object> arrayV = new ArrayList<Object>(50);
                                                if(actionConfig.getConfigurationSection(action) != null) {
                                                    for (String value : actionConfig.getConfigurationSection(action).getKeys(false)) {
                                                        for (Pram pram : clazz.getConstructorParams()) {
                                                            if (pram.getConfig() == null) {
                                                                array.add(pram.getIndex(), String.class);
                                                                arrayV.add(pram.getIndex(), id);
                                                            } else if (pram.getConfig() != null && pram.getConfig().equals(value)) {
                                                                array.add(pram.getIndex() - 1, (Class<?>) pram.getValue());
                                                                if (!value.equalsIgnoreCase("config")) {
                                                                    arrayV.add(pram.getIndex() - 1, actionConfig.getConfigurationSection(action).get(value));
                                                                } else {

                                                                    if (actionConfig.getConfigurationSection(action).getString(value).equalsIgnoreCase("this")) {
                                                                        arrayV.add(pram.getIndex() - 1, actionConfig.getConfigurationSection(action));
                                                                    } else {
                                                                        arrayV.add(pram.getIndex() - 1, actionConfig.getConfigurationSection(action).getConfigurationSection(actionConfig.getConfigurationSection(action).getString(value)));
                                                                    }
                                                                }
                                                            }
                                                        }
                                                        if (array.size() >= clazz.getConstructorParams().size()) {
                                                            break;
                                                        }
                                                    }
                                                }else{
                                                    for (Pram pram : clazz.getConstructorParams()) {
                                                        if (pram.getConfig() == null) {
                                                            array.add(pram.getIndex(), String.class);
                                                            arrayV.add(pram.getIndex(), id);
                                                        } else if (pram.getConfig() != null && pram.getConfig().equals(action)) {
                                                            array.add(pram.getIndex() - 1, (Class<?>) pram.getValue());
                                                            if (!action.equalsIgnoreCase("config")) {
                                                                arrayV.add(pram.getIndex() - 1, actionConfig.get(action));
                                                            } else {

                                                                if (actionConfig.getString(action).equalsIgnoreCase("this")) {
                                                                    throw new IllegalArgumentException("A single value cannot have a configuration section!");
                                                                } else {
                                                                    arrayV.add(pram.getIndex() - 1, actionConfig.getConfigurationSection(actionConfig.getString(action)));
                                                                }
                                                            }
                                                        }
                                                        if (array.size() >= clazz.getConstructorParams().size()) {
                                                            break;
                                                        }
                                                    }

                                                }
                                                Class<?>[] classes = array.toArray(new Class<?>[0]);
                                                Object[] objects = arrayV.toArray(new Object[0]);

                                                Constructor<? extends Action> constructor = clazz.getClazz().getDeclaredConstructor(classes);
                                                actions.put((Action) constructor.newInstance(objects),clickType);
                                            } catch (Exception e) {
                                                Bukkit.getConsoleSender().sendMessage("§7[" + ChatColor.YELLOW + "NMenus" + "§7] The menu: " + codeName + " unable to load action: §e"+id+" §7(§e"+action+"§7)");
                                                Bukkit.getConsoleSender().sendMessage("§7[" + ChatColor.YELLOW + "NMenus" + "§7] Reason: §e"+e.getMessage());
                                                for(StackTraceElement element : e.getStackTrace()){
                                                    Bukkit.getConsoleSender().sendMessage("§cERROR: "+element.toString());
                                                }
                                            }
                                        }
                                    }
                                }
                            });
                        }catch (IllegalArgumentException e){
                            Bukkit.getConsoleSender().sendMessage("§7[" + ChatColor.YELLOW + "NMenus" + "§7] The menu: " + codeName + "§r slot: " + slot + " contains a click type that is unknown ("+actionType+")");
                        }
                    });
                }
                for(Action action : actions.keySet()){
                    Main.DEBUG.sendMLM("Added: §e"+action.getClass().getSimpleName()+"§7 as an action to slot(s): §e"+slot);
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
                                int finalI = i;
                                animationConfig.getKeys(false).forEach(frame -> {
                                    ConfigurationSection frameConfig = animationConfig.getConfigurationSection(frame);
                                    items.add(new MenuSlot(-1,makeSlot(frameConfig, finalI+" frame: "+frame,codeName),actions, finalCanBePicked));
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
                               items.add(new MenuSlot(-1,makeSlot(frameConfig,slot+" frame: "+frame,codeName),actions, finalCanBePicked));
                        });
                        slotsArray.add(new MenuAnimatedSlot(Integer.parseInt(slot),actions,canBePicked,slotConfig.getConfigurationSection("animation").getInt("refresh-rate"), items));
                    }
                }
            });
        }
        Menu menu = new Menu(codeName, displayName, command, slotsArray, inventoryType, rows, shareable,configuration,file,borderColor,borderType,borderRefresh,canPutItems);
        for (Slot slot : slotsArray) {

            if(slot instanceof MenuSlot) ((MenuSlot)slot).setMenu(menu);
            else ((MenuAnimatedSlot) slot).setMenu(menu);
        }
        Main.DEBUG.sendMLM("Registering commands...");
        if(command != null) {
            for (String cmd : command) {
                Main.DEBUG.sendMLM("Added: §e" + cmd + "§7 as a command to the menu: §e" + codeName);
                Main.getInstance().registerCommands(new CCommand(cmd) {
                    @Override
                    public void run(CommandSender sender, String commandLabel, String[] arguments) {
                        if(sender instanceof Player) {
                            menu.openInventory((Player) sender);
                        }else{
                            sender.sendMessage("§cOnly players can open menus!");
                        }
                    }
                });
            }
        }
        MENUS.add(menu);
        Bukkit.getConsoleSender().sendMessage("§7[§eNMenus]§7 Loaded the menu: §e"+codeName);
        return true;
    }

    public static TextComponent buildText(String action, ConfigurationSection config) {
        TextComponent textBuilder = new TextComponent(config.getString("message"));
        if(config.contains("hover")){
            if(config.contains("hover.show_text")){
                textBuilder.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,new ComponentBuilder(config.getString("hover.show_text")).create()));
            }
        }else if(config.contains("click")){
            if(config.contains("click.suggest_text")){
                textBuilder.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND,config.getString("click.suggest_text")));
            }
            if(config.contains("click.open_url")){
                textBuilder.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL,config.getString("click.open_url")));
            }
            if(config.contains("click.run_command")){
                textBuilder.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,config.getString("click.run_command")));
            }
        }
        return textBuilder;
    }

    private static ItemStack makeSlot(ConfigurationSection slotConfig,String slot, String codeName) {
        Main.DEBUG.sendMLM("Making slot's item of: §e"+slot);
        ItemStack item = null;
        if(!slotConfig.getString("material").contains("head")) {
            item = new ItemStack(Material.getMaterial(slotConfig.getString("material").toUpperCase()), slotConfig.getInt("amount"));
        }else {
            try{
                String headS = slotConfig.getString("material");
                if (headS.contains("mob")) {
                        Head head = Head.Mob.getFromType(EntityType.valueOf(headS.replaceFirst("head-mob-", "").toUpperCase()));
                        item = head.getHead();
                }
                if(headS.contains("player")){
                    Head head = new Head("PLAYER",headS.replaceFirst("head-player-", ""));
                    item = head.getHead();
                }
                if(headS.contains("base64")){
                    Head head = new Head("PLAYER",new NBase64(headS.replaceFirst("head-base64-", "")));
                    item = head.getHead();
                }
            }catch (IllegalArgumentException e){
            }
        }
        if(item.hasItemMeta()) {
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
                    Main.DEBUG.sendMLM("Added item flag: §e" + flag + " §7to slot: " + slot);
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
                    Enchantment enchantment = Enchantment.getByName(enchant.toUpperCase());
                    itemMeta.addEnchant(enchantment, level, true);
                    Main.DEBUG.sendMLM("Added enchant: §e" + enchantment + "§7 to slot: " + slot);
                } catch (IllegalArgumentException e) {
                    Bukkit.getConsoleSender().sendMessage("§7[" + ChatColor.YELLOW + "NMenus" + "§7] The menu: " + codeName + "§r slot: " + slot + " contains an enchant that is unknown (" + enchant + ")");
                }

            }
            if (slotConfig.contains("name")) {
                String name = slotConfig.getString("name");
                itemMeta.setDisplayName(name.replaceAll("\"", "").replaceAll("&", "§"));
                Main.DEBUG.sendMLM("Set the name of the item in the slot to: §r" + name);
            }
            item.setItemMeta(itemMeta);
        }

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
