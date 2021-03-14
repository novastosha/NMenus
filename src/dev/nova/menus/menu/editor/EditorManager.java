package dev.nova.menus.menu.editor;

import dev.nova.menus.menu.Menu;
import dev.nova.menus.menu.MenuSlot;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftInventory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EditorManager implements Listener{

    public static HashMap<Player,EditorMenu> MENUS = new HashMap<>();

    public static void openEditMenu(Menu menu, Player player) {
        EditorMenu editorMenu = new EditorMenu(menu);
        MENUS.put(player,editorMenu);
        player.openInventory(editorMenu.getInventory());
    }

    @EventHandler
    public void onClick(InventoryClickEvent event){
        EditorMenu menu = MENUS.get((Player) event.getWhoClicked());
        if(menu == null) return;

        event.getWhoClicked().sendMessage("Clicked!");
    }

    @EventHandler
    public void onInvClose(InventoryCloseEvent event){
        EditorMenu menu = MENUS.get(((Player) event.getPlayer()).getPlayer());
        if(menu == null) return;
        //...
        MENUS.remove(((Player) event.getPlayer()).getPlayer());
    }

}
