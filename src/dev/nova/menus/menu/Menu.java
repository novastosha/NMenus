package dev.nova.menus.menu;

import dev.nova.menus.menu.actions.Action;
import net.minecraft.server.v1_12_R1.Slot;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftInventory;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Menu {

    private final String displayName;
    private final String codeName;
    private final List<MenuSlot> slots;
    private final Inventory inventory;
    private final String commandName;
    private final InventoryType inventoryType;
    private final int rows;
    private final ArrayList<Inventory> inventories;
    private final boolean shareable;
    private final YamlConfiguration configuration;
    private final File menuFile;

    public Menu(String codeName, String displayName, String commandName, List<MenuSlot> menuSlots, InventoryType inventoryType, int rows, boolean shareable, YamlConfiguration configuration, File menuFile){
        this.inventories = new ArrayList<>();
        this.configuration = configuration;
        this.codeName = codeName;
        this.shareable = shareable;
        this.inventoryType = inventoryType;
        this.commandName = commandName;
        this.menuFile = menuFile;
        this.displayName = displayName;
        this.slots = menuSlots;
        this.rows = rows;
        this.inventory = setupInventory();

    }

    public File getMenuFile() {
        return menuFile;
    }

    public YamlConfiguration getConfiguration() {
        return configuration;
    }

    public void saveConfig(){
        try {
            configuration.save(menuFile);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    public boolean isShareable() {
        return shareable;
    }

    public ArrayList<Inventory> getInventories() {
        return inventories;
    }

    private Inventory setupInventory() {
        Inventory inventory = null;
        if (inventoryType == InventoryType.CHEST) {
            inventory = Bukkit.createInventory(null, rows * 9, displayName);
        } else {
            inventory = Bukkit.createInventory(null, inventoryType, displayName);
        }
            for (MenuSlot menuSlot : slots) {
                try {
                    ((CraftInventory) inventory).setItem(menuSlot.getSlotNumber(), menuSlot.getItem());
                } catch (ArrayIndexOutOfBoundsException e) {
                    Bukkit.getConsoleSender().sendMessage("§7[" + ChatColor.YELLOW + "NMenus" + "§7] The menu: " + displayName + "§r has a slot that cannot be set! (" + e.getMessage() + ")");

                }
            }
        inventories.add(inventory);
        return inventory;
    }

    public int getRows() {
        return rows;
    }

    public InventoryType getInventoryType() {
        return inventoryType;
    }

    public String getCodeName() {
        return codeName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public List<MenuSlot> getSlots() {
        return slots;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public String getCommandName() {
        return commandName;
    }

    public MenuSlot getSlotFromNumber(int slot) {
        for(MenuSlot menuSlot : slots){
            if(menuSlot.getSlotNumber() == slot) return menuSlot;
        }
        return null;
    }

    public void openInventory(Player player) {
        if(shareable) {
            player.openInventory(getInventory());
        }else{
            Inventory inventory = copy(getInventory());
            inventories.add(inventory);
            player.openInventory(inventory);
        }
    }

    private Inventory copy(Inventory inventory){
        Inventory copied = null;
        switch (inventoryType){
            case CHEST:
                copied = Bukkit.createInventory(inventory.getHolder(),inventory.getSize(),inventory.getTitle());
                break;
            default:
                copied = Bukkit.createInventory(inventory.getHolder(),inventory.getType(),inventory.getTitle());
        }
        copied.setContents(inventory.getContents());
        return copied;
    }
}
