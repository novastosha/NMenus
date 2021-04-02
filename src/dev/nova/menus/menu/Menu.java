package dev.nova.menus.menu;

import dev.nova.menus.Main;
import dev.nova.menus.menu.anvil.AnvilGUI;
import dev.nova.menus.menu.manager.MenuManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.BiFunction;

public class Menu {

    private final String displayName;
    private final String codeName;
    private final List<Slot> slots;
    private final Inventory inventory;
    private final List<String> commandName;
    private final InventoryType inventoryType;
    private final int rows;
    private final ArrayList<Inventory> inventories;
    private final ArrayList<AnvilGUI.Builder> anvilsOpen;
    private final boolean canPutItems;
    private ArrayList<Integer> slotTaskIDies;
    private final boolean shareable;
    private final YamlConfiguration configuration;
    private final File menuFile;
    private final Material borderItem;
    private final int borderRefresh;
    private final MenuBorderColor borderColor;
    private final List<Integer> borderSlotList;
    private int refreshTaskID;
    private AnvilGUI.Builder anvil;
    private AnvilGUI.Builder tempAnvil;

    public Menu(String codeName, String displayName, List<String> commandName, List<Slot> menuSlots, InventoryType inventoryType, int rows, boolean shareable, YamlConfiguration configuration, File menuFile, MenuBorderColor borderColor, Material borderItem, int refresh,boolean canPutItems) {
        this.inventories = new ArrayList<>();
        this.configuration = configuration;
        this.codeName = codeName;
        this.canPutItems = canPutItems;
        this.shareable = shareable;
        this.inventoryType = inventoryType;
        this.anvilsOpen = new ArrayList<>();
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
                                ItemMeta itemMeta = item.getItemMeta();
                                itemMeta.setDisplayName("§e                 ");
                                itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                                item.setItemMeta(itemMeta);
                                inventory.setItem(i,item);
                            }
                        }

                        inColor++;

                        if(inColor == 16){
                            inColor = 0;
                        }
                    }
                }, 0L, 20L * refresh);
            }else{
                for(Inventory inventory : inventories){

                    for (int i : borderSlotList) {

                        ItemStack item;
                        if(borderItem.equals(Material.GLASS)) item = new ItemStack(Material.STAINED_GLASS_PANE,1,(byte) borderColor.getNumber());
                        else item = new ItemStack(borderItem,1);
                        ItemMeta itemMeta = item.getItemMeta();
                        itemMeta.setDisplayName("§e                 ");
                        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                        item.setItemMeta(itemMeta);
                        inventory.setItem(i,item);
                    }
                }
            }
        }
    }

    public ArrayList<Integer> getSlotTaskIDies() {
        return slotTaskIDies;
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

    public boolean canPutItems() {
        return canPutItems;
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
        } else if(inventoryType != InventoryType.ANVIL){
            inventory = Bukkit.createInventory(null, inventoryType, displayName);
        }else{
            inventory = null;
            this.anvil = new AnvilGUI.Builder();
            anvil.title(displayName);
            anvil.plugin(Main.getPlugin(Main.class));
        }
        if(inventory != null) {
            for (Slot menuSlot : slots) {
                if (menuSlot instanceof MenuSlot) {
                    try {
                        inventory.setItem(((MenuSlot) menuSlot).getSlotNumber(), ((MenuSlot) menuSlot).getItem());
                    } catch (ArrayIndexOutOfBoundsException e) {
                        Bukkit.getConsoleSender().sendMessage("§7[" + ChatColor.YELLOW + "NMenus" + "§7] The menu: " + displayName + "§r has a slot that cannot be set! (" + e.getMessage() + ")");

                    }
                }
            }
        }else{
            for (Slot menuSlot : slots) {
                if (menuSlot instanceof MenuSlot) {
                    if(((MenuSlot) menuSlot).getSlotNumber() == 0){
                        anvil.itemLeft(((MenuSlot) menuSlot).getItem());
                    }
                    if(((MenuSlot) menuSlot).getSlotNumber() == 1){
                        anvil.itemRight(((MenuSlot) menuSlot).getItem());
                    }
                }
            }
        }
        if(inventory != null) inventories.add(inventory);
        else anvilsOpen.add(anvil);
        if(inventory != null) {
            for (Slot menuSlot : slots) {
                if (menuSlot instanceof MenuAnimatedSlot) {
                    slotTaskIDies.add(Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(Main.getPlugin(Main.class), new Runnable() {

                        private int inFrame;

                        @Override
                        public void run() {

                            MenuSlot slot = ((MenuAnimatedSlot) menuSlot).getSlots().get(inFrame);
                            for (Inventory inventory1 : inventories) {
                                inventory1.setItem(((MenuAnimatedSlot) menuSlot).getSlotNumber(), slot.getItem());
                            }

                            inFrame++;

                            if (inFrame == ((MenuAnimatedSlot) menuSlot).getSlots().size()) {
                                inFrame = 0;
                            }
                        }
                    }, 0L, 20L * ((MenuAnimatedSlot) menuSlot).getRefreshRate()));

                }
            }
        }else{
            for (Slot menuSlot : slots) {
                if (menuSlot instanceof MenuAnimatedSlot) {
                    slotTaskIDies.add(Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(Main.getPlugin(Main.class), new Runnable() {

                        private int inFrame;

                        @Override
                        public void run() {

                            MenuSlot slot = ((MenuAnimatedSlot) menuSlot).getSlots().get(inFrame);
                            for (AnvilGUI.Builder inventory1 : anvilsOpen) {
                                if(((MenuAnimatedSlot) menuSlot).getSlotNumber() == 0){
                                    inventory1.itemLeft(slot.getItem());
                                }
                                if(((MenuAnimatedSlot) menuSlot).getSlotNumber() == 1){
                                    inventory1.itemRight(slot.getItem());
                                }
                            }

                            inFrame++;

                            if (inFrame == ((MenuAnimatedSlot) menuSlot).getSlots().size()) {
                                inFrame = 0;
                            }
                        }
                    }, 0L, 20L * ((MenuAnimatedSlot) menuSlot).getRefreshRate()));

                }
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

    public List<String> getCommandName() {
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
            if(inventoryType != InventoryType.ANVIL) player.openInventory(getInventory());
            else anvil.open(player);
        } else {
            if(inventoryType != InventoryType.ANVIL) {
                Inventory inventory = copy(getInventory());
                inventories.add(inventory);
                player.openInventory(inventory);
            }else{
                copy(null);
                tempAnvil.open(player);
                this.tempAnvil = null;
            }
        }
    }

    private Inventory copy(Inventory inventory) {
        Inventory copied = null;
        switch (inventoryType) {
            case CHEST:
                copied = Bukkit.createInventory(inventory.getHolder(), inventory.getSize(), inventory.getTitle());
                break;
            case ANVIL:
                AnvilGUI.Builder anvilM = new AnvilGUI.Builder();
                anvilM.title(displayName);
                anvilM.plugin(Main.getPlugin(Main.class));
                anvilsOpen.add(anvilM);
                anvilM.onComplete(new BiFunction<Player, String, AnvilGUI.Response>() {
                    @Override
                    public AnvilGUI.Response apply(Player player, String s) {
                        return AnvilGUI.Response.text("gg");
                    }
                });
                this.tempAnvil = anvilM;
            default:
                copied = Bukkit.createInventory(inventory.getHolder(), inventory.getType(), inventory.getTitle());
        }
        copied.setContents(inventory.getContents());
        return copied;
    }
}
