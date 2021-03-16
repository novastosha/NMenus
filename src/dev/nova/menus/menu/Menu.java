package dev.nova.menus.menu;

import dev.nova.menus.Main;
import dev.nova.menus.menu.manager.MenuManager;
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
    private final List<Slot> slots;
    private final Inventory inventory;
    private final String commandName;
    private final InventoryType inventoryType;
    private final int rows;
    private final ArrayList<Inventory> inventories;
    private ArrayList<Integer> slotTaskIDies;
    private final boolean shareable;
    private final YamlConfiguration configuration;
    private final File menuFile;
    private final Material borderItem;
    private final int borderRefresh;
    private final MenuBorderColor borderColor;
    private final List<Integer> borderSlotList;
    private int refreshTaskID;

    public Menu(String codeName, String displayName, String commandName, List<Slot> menuSlots, InventoryType inventoryType, int rows, boolean shareable, YamlConfiguration configuration, File menuFile, MenuBorderColor borderColor, Material borderItem, int refresh) {
        this.inventories = new ArrayList<>();
        this.configuration = configuration;
        this.codeName = codeName;
        this.shareable = shareable;
        this.inventoryType = inventoryType;
        this.commandName = commandName;
        this.borderColor = borderColor;
        this.slotTaskIDies = new ArrayList<>();
        this.borderRefresh = refresh;
        this.borderItem = borderItem;
        this.menuFile = menuFile;
        this.displayName = displayName;
        this.slots = menuSlots;
        this.rows = rows;
        this.inventory = setupInventory();
        this.borderSlotList = MenuManager.generateBorderSlotList(rows);
        if (borderItem != null) {
            if(borderItem.equals(Material.GLASS) && borderColor.equals(MenuBorderColor.RAINBOW)){
                this.refreshTaskID = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(Main.getPlugin(Main.class), new Runnable() {
                    private int inColor;

                    @Override
                    public void run() {
                        MenuBorderColor color = MenuBorderColor.getFromNumber(inColor);
                        for(Inventory inventory : inventories){

                            for (int i : borderSlotList) {
                                ItemStack item = new ItemStack(Material.STAINED_GLASS_PANE,1,(byte) color.getNumber());
                                inventory.setItem(i,item);
                            }
                        }

                        inColor++;

                        if(inColor == 16){
                            inColor = 0;
                        }
                    }
                }, 20L * refresh, 20L * refresh);
            }else{
                for(Inventory inventory : inventories){

                    for (int i : borderSlotList) {

                        ItemStack item;
                        if(borderItem.equals(Material.GLASS)) item = new ItemStack(Material.STAINED_GLASS_PANE,1,(byte) borderColor.getNumber());
                        else item = new ItemStack(borderItem,1);
                        inventory.setItem(i,item);
                    }
                }
            }
        }
    }

    public List<Integer> getBorderSlotList() {
        return borderSlotList;
    }

    public int getRefreshTaskID() {
        return refreshTaskID;
    }

    public int getBorderRefresh() {
        return borderRefresh;
    }

    public Material getBorderItem() {
        return borderItem;
    }

    public MenuBorderColor getBorderColor() {
        return borderColor;
    }

    public File getMenuFile() {
        return menuFile;
    }

    public YamlConfiguration getConfiguration() {
        return configuration;
    }

    public void saveConfig() {
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
        for (Slot menuSlot : slots) {
            if(menuSlot instanceof MenuSlot) {
                try {
                    ((CraftInventory) inventory).setItem(((MenuSlot)menuSlot).getSlotNumber(), ((MenuSlot)menuSlot).getItem());
                } catch (ArrayIndexOutOfBoundsException e) {
                    Bukkit.getConsoleSender().sendMessage("§7[" + ChatColor.YELLOW + "NMenus" + "§7] The menu: " + displayName + "§r has a slot that cannot be set! (" + e.getMessage() + ")");

                }
            }
        }
        inventories.add(inventory);
        for (Slot menuSlot : slots){
            if(menuSlot instanceof MenuAnimatedSlot){
                    slotTaskIDies.add(Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(Main.getPlugin(Main.class), new Runnable() {

                        private int inFrame;

                        @Override
                        public void run() {

                            MenuSlot slot = ((MenuAnimatedSlot) menuSlot).getSlots().get(inFrame);
                            for(Inventory inventory1 : inventories){
                                inventory1.setItem(((MenuAnimatedSlot) menuSlot).getSlotNumber(),slot.getItem());
                            }

                            inFrame++;

                            if(inFrame == ((MenuAnimatedSlot) menuSlot).getSlots().size()){
                                inFrame = 0;
                            }
                        }
                    },20L * ((MenuAnimatedSlot) menuSlot).getRefreshRate(),20L * ((MenuAnimatedSlot) menuSlot).getRefreshRate()));
            }
        }
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

    public List<Slot> getSlots() {
        return slots;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public String getCommandName() {
        return commandName;
    }

    public Slot getSlotFromNumber(int slot) {
        for (Slot menuSlot : slots) {
            if (menuSlot instanceof MenuSlot) {
                if (((MenuSlot)menuSlot).getSlotNumber() == slot) return menuSlot;
            }else{
                if (((MenuAnimatedSlot)menuSlot).getSlotNumber() == slot) return menuSlot;

            }
        }
        return null;
    }

    public void openInventory(Player player) {
        if (shareable) {
            player.openInventory(getInventory());
        } else {
            Inventory inventory = copy(getInventory());
            inventories.add(inventory);
            player.openInventory(inventory);
        }
    }

    private Inventory copy(Inventory inventory) {
        Inventory copied = null;
        switch (inventoryType) {
            case CHEST:
                copied = Bukkit.createInventory(inventory.getHolder(), inventory.getSize(), inventory.getTitle());
                break;
            default:
                copied = Bukkit.createInventory(inventory.getHolder(), inventory.getType(), inventory.getTitle());
        }
        copied.setContents(inventory.getContents());
        return copied;
    }
}
