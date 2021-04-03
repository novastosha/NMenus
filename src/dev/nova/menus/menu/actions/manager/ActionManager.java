package dev.nova.menus.menu.actions.manager;

import dev.nova.menus.menu.actions.*;
import dev.nova.menus.menu.actions.base.Pram;
import dev.nova.menus.menu.actions.base.PramType;
import dev.nova.menus.menu.actions.base.RawAction;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.libs.jline.internal.InputStreamReader;
import org.bukkit.plugin.Plugin;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;
import java.io.File;
import java.util.jar.JarFile;

public class ActionManager {

    public static final ArrayList<RawAction> ACTIONS = new ArrayList<>(Arrays.asList(
            new RawAction(CloseMenu.class,new ArrayList<>(Arrays.asList(new Pram(String.class, null,0)))),
            new RawAction(ConnectToServer.class,new ArrayList<>(Arrays.asList(new Pram(String.class, null,0)))),
            new RawAction(ConsoleExecuteCommand.class, new ArrayList<>(Arrays.asList(new Pram(String.class,"command",0),new Pram(String.class,null,1)))),
            new RawAction(ExecuteCommandPlayer.class,new ArrayList<>(Arrays.asList(new Pram(String.class,"command",0),new Pram(String.class,null,1)))),
            new RawAction(OpenMenu.class, new ArrayList<>(Arrays.asList(new Pram(String.class,"menuCodeName",0),new Pram(String.class,null,1)))),
            new RawAction(SendJSONMessage.class, new ArrayList<>(Arrays.asList(new Pram(String.class,null,0),new Pram(ConfigurationSection.class,"config",1)))),
            new RawAction(SendMessage.class, new ArrayList<>(Arrays.asList(new Pram(String.class,"message",1),new Pram(String.class,null,0)))),
            new RawAction(Teleport.class, new ArrayList<>(Arrays.asList(new Pram(Location.class,"location",0),new Pram(String.class,null,1))))

    ));

    public static boolean check(Class<? extends Action> aClass){
            try{
                Field field = aClass.getDeclaredField("CODE");
                field.setAccessible(true);
                String code = (String) field.get(aClass);
                field.setAccessible(false);
                 return code != null;
            }catch (NoSuchFieldException | IllegalAccessException e) {
                Bukkit.getConsoleSender().sendMessage("§7[" + ChatColor.YELLOW + "NMenus" + "§7]" + "§e            !! REPORT THIS !!");
                Bukkit.getConsoleSender().sendMessage("§7[" + ChatColor.YELLOW + "NMenus" + "§7] " + "Action: §e"+aClass.getSimpleName()+" §7does not contain the static field 'CODE' necessary to register an action!");
                return false;
            }
    }

    public static String getCode(RawAction action){
        Class<? extends Action> aClass = action.getClazz();
        try{
            Field field = aClass.getDeclaredField("CODE");
            field.setAccessible(true);
            String code = (String) field.get(aClass);
            field.setAccessible(false);
            return code;
        }catch (NoSuchFieldException | IllegalAccessException e) {
            Bukkit.getConsoleSender().sendMessage("§7[" + ChatColor.YELLOW + "NMenus" + "§7]" + "§e            !! REPORT THIS !!");
            Bukkit.getConsoleSender().sendMessage("§7[" + ChatColor.YELLOW + "NMenus" + "§7] " + "Action: §e"+aClass.getSimpleName()+" §7does not contain the static field 'CODE' necessary to register an action!");
            return null;
        }
    }

    public static void loadActionAddons(File dest){
        for(File file : Objects.requireNonNull(dest.listFiles())){
            if(file.getName().endsWith(".jar") && !file.getName().startsWith("-")){
                loadAddon(file);
            }
        }
    }

    private static boolean loadAddon(File file) {
        try {
            JarFile jarFile = new JarFile(file);
            if(jarFile.getEntry("plugin.yml") == null){
                return false;
            }
            if(jarFile.getEntry("nmenus_addon.yml") == null){
                return false;
            }
            YamlConfiguration pyml = new YamlConfiguration();
            YamlConfiguration ayml = new YamlConfiguration();
            pyml.load(new InputStreamReader(jarFile.getInputStream(jarFile.getEntry("plugin.yml"))));
            if(!pyml.contains("name")){
                Bukkit.getConsoleSender().sendMessage("§7[" + ChatColor.YELLOW + "NMenus" + "§7] The jar file: "+file.getName()+"'s plugin.yml does not contain a name!");
                return false;
            }
            String pluginName = pyml.getString("name");
            ayml.load(new InputStreamReader(jarFile.getInputStream(jarFile.getEntry("nmenus_addon.yml"))));
            Bukkit.getConsoleSender().sendMessage("§7[" + ChatColor.YELLOW + "NMenus" + "§7] Loading addon: §e"+file.getName());
            if(!ayml.contains("actions")){
                Bukkit.getConsoleSender().sendMessage("§7[" + ChatColor.YELLOW + "NMenus" + "§7] The addon: "+pluginName+" doesn't contain an action class!");
                return false;
            }
            ConfigurationSection actions = ayml.getConfigurationSection("actions");
            actions.getKeys(false).forEach(action -> {
                try {
                    Class<? extends Action> clazz = (Class<? extends Action>) Class.forName(action.replaceAll("/","\\."));
                    if(check(clazz)){
                        ConfigurationSection actionConfig = actions.getConfigurationSection(action);
                        ArrayList<Pram> objects = new ArrayList<>();
                        final int[] index = {0};
                        actionConfig.getKeys(false).forEach(type -> {
                            try {
                                PramType pramType = PramType.valueOf(actionConfig.getConfigurationSection(type).getString("type"));
                                if(type.equalsIgnoreCase("id")) {
                                    type = null;
                                }
                                objects.add(new Pram(pramType.getClazz(), type, index[0]));
                            }catch (IllegalArgumentException ignored) {

                            }
                            index[0] = index[0] +1;
                        });
                        Bukkit.getConsoleSender().sendMessage("§7[" + ChatColor.YELLOW + "NMenus" + "§7] Loaded action: §e"+clazz.getSimpleName()+" §7 for addon: §e"+pluginName);
                        ACTIONS.add(new RawAction(clazz, objects));
                    }
                } catch (ClassNotFoundException e) {
                    Bukkit.getConsoleSender().sendMessage("§7[" + ChatColor.YELLOW + "NMenus" + "§7] The addon: "+file.getName()+" has an action class that doesn't exist!");
                }
            });
            return true;
        } catch (Exception e) {
                e.printStackTrace();

            return false;
        }
    }

}
