package dev.nova.menus.commands;

import dev.nova.menus.Main;
import dev.nova.menus.menu.Menu;
import dev.nova.menus.menu.editor.EditorManager;
import dev.nova.menus.menu.manager.MenuManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NMenusCommand implements CommandExecutor {

    private final List<String> settings = new ArrayList<>(Arrays.asList("displayName","command","inventoryType","rows","shareable"));

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)){
            sender.sendMessage("§cOnly players can execute this command!");
            return true;
        }
        Player player = (Player) sender;
        if(command.getName().equalsIgnoreCase("nmenus")) {
            if(args.length == 0) {
                return true;
            }
            if(args.length == 1){
                if(args[0].equalsIgnoreCase("reload")){
                    MenuManager.reloadMenus(player);
                }
                if(args[0].equalsIgnoreCase("settings")){
                    player.sendMessage("§7-------------------------------");
                    player.sendMessage("§edisplayName§7: The menu title.");
                    player.sendMessage("§ecommand§7: (Optional) A custom command to open your menu.");
                    player.sendMessage("§einventoryType§7: (Required) The type of inventory you want to open.");
                    for(InventoryType inventoryType : InventoryType.values()){
                        if(!inventoryType.equals(InventoryType.CREATIVE) && !inventoryType.equals(InventoryType.PLAYER)){
                            player.sendMessage("§7- §e"+inventoryType.toString());
                        }
                    }
                    player.sendMessage("§erows§7: (Required on CHEST inventory type) Amount of rows (space) in your menu.");
                    player.sendMessage("§eshareable§7: (Optional) Opens the same inventory for all players or not.");
                    player.sendMessage("§7(To edit slots use the in-game editor: /nmenus edit <menu>)");
                    player.sendMessage("§7-------------------------------");
                }
            }
            if(args.length >= 2){
                if(args[0].equalsIgnoreCase("edit")){
                    Menu menu = MenuManager.getByCodeName(args[1]);
                    if(menu == null){
                        player.sendMessage(ChatColor.RED+"Cannot find that menu!");
                        return true;
                    }

                    EditorManager.openEditMenu(menu,player);
                }
                if(args[0].equalsIgnoreCase("create")){
                    String codeName = args[1];
                    File file = new File(Main.MENUS_FOLDER,codeName+".yml");
                    if(file.exists()){
                        player.sendMessage(ChatColor.RED+"A file with this name already exists!");
                        return true;
                    }
                    try {
                        file.createNewFile();
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                    player.sendMessage("§7[§eNMenus§7] Created the menu: §e"+file.getName());
                    player.sendMessage("§7To start editing, first assign the base settings using the command: /nmenus setting <codeName> <setting> <value>");
                    YamlConfiguration configuration = new YamlConfiguration();
                    try {
                        configuration.load(file);
                    } catch (IOException | InvalidConfigurationException ioException) {
                        ioException.printStackTrace();
                    }

                    configuration.set("code-name",codeName);
                    try {
                        configuration.save(file);
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }

                }

                if(args[0].equalsIgnoreCase("setting")){
                    String codeName = args[1];
                    File file = new File(Main.MENUS_FOLDER,codeName+".yml");
                    if(settings.contains(args[2])){
                        YamlConfiguration configuration = new YamlConfiguration();
                        try {
                            configuration.load(file);
                        } catch (IOException | InvalidConfigurationException ioException) {
                            ioException.printStackTrace();
                        }

                        switch (args[2].toLowerCase()){
                            case "displayName":
                            case "command":
                                configuration.set(args[2],args[3]);
                                break;
                            case "inventoryType":
                                configuration.set(args[2],args[3].toUpperCase());
                                break;
                            case "shareable":
                                if(args[3].equalsIgnoreCase("true")) {
                                    configuration.set(args[2],true);
                                }else if(args[3].equalsIgnoreCase("false")){
                                    configuration.set(args[2],false);
                                }
                                break;
                            case "rows":
                                configuration.set(args[2],Integer.parseInt(args[3]));
                                break;
                        }

                        try {
                            configuration.save(file);
                        } catch (IOException ioException) {
                            ioException.printStackTrace();
                        }
                        player.sendMessage("§7[§eNMenus§7] The setting: §e"+args[2]+"§7 on menu: "+file.getName()+" has been set to: §e"+args[3]);
                        return true;
                    }
                    player.sendMessage(ChatColor.RED+"Cannot find this setting!");
                }

                if(args[0].equalsIgnoreCase("unload")){
                    Menu menu = MenuManager.getByCodeName(args[1]);
                    if(menu == null){
                        player.sendMessage(ChatColor.RED+"Cannot find that menu!");
                        return true;
                    }
                    MenuManager.MENUS.remove(menu);
                }
                if(args[0].equalsIgnoreCase("load")){
                    Menu menu = MenuManager.getByCodeName(args[1]);
                    if(menu != null){
                        player.sendMessage(ChatColor.RED+"This menu is already loaded!");
                        return true;
                    }
                    MenuManager.loadMenu(new File(Main.MENUS_FOLDER,args[1]+".yml"));
                }
            }
        }
        return true;
    }
}
