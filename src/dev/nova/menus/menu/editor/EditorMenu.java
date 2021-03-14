package dev.nova.menus.menu.editor;

import dev.nova.menus.menu.Menu;
import dev.nova.menus.menu.MenuSlot;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class EditorMenu {

    private final Menu menu;
    private final Inventory inventory;
    private final List<MenuSlot> slots;

    public EditorMenu(Menu reference) {
        this.menu = reference;
        this.slots = new ArrayList<>();

        for(MenuSlot menuSlot : menu.getSlots()) {
            MenuSlot clone = null;
            try {
                clone = menuSlot.clone();
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
            clone.setMenu(null);
            slots.add(clone);
        }
        this.inventory = setupInventory();
    }

    private Inventory setupInventory() {
        Inventory inventory = null;
        if(menu.getInventoryType() == InventoryType.CHEST) inventory = Bukkit.createInventory(null,menu.getRows()*9,"§aEditing: §7"+menu.getCodeName());
        else inventory = Bukkit.createInventory(null,menu.getInventoryType(),"§aEditing: §7"+menu.getCodeName());
        for(MenuSlot menuSlot : slots){
            ItemStack item = menuSlot.getItem();
            ItemMeta itemMeta = item.getItemMeta();

            List<String> lore = itemMeta.getLore() == null ? new ArrayList<>() : itemMeta.getLore();
            lore.add("        ");
            lore.add("§8Left Click §7- Edit");
            itemMeta.setLore(lore);
            item.setItemMeta(itemMeta);
            menuSlot.setItem(item);
            inventory.setItem(menuSlot.getSlotNumber(),item);
        }
        return inventory;
    }

    public Menu getMenu() {
        return menu;
    }

    public List<MenuSlot> getSlots() {
        return slots;
    }

    public Inventory getInventory() {
        return inventory;
    }

}
