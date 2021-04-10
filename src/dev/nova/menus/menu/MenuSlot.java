package dev.nova.menus.menu;

import dev.nova.menus.menu.actions.Action;
import dev.nova.menus.menu.conditions.Condition;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.event.inventory.ClickType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MenuSlot extends Slot implements Cloneable {

    private final int slotNumber;
    private final HashMap<Action,org.bukkit.event.inventory.ClickType> actions;
    private final ArrayList<Condition> conditions;
    private Menu menu;
    private ItemStack item;
    private final boolean canBePicked;

    public MenuSlot(int slotNumber, ItemStack item, HashMap<Action,org.bukkit.event.inventory.ClickType> actions, ArrayList<Condition> conditions, boolean canBePicked){
        this.item = item;
        this.conditions = conditions;
        this.slotNumber = slotNumber;
        this.actions = actions;
        this.canBePicked = canBePicked;
    }

    public ArrayList<Condition> getConditions() {
        return conditions;
    }

    public void setMenu(Menu menu) {
        this.menu = menu;
    }

    public int getSlotNumber() {
        return slotNumber;
    }

    public boolean canBePicked() {
        return canBePicked;
    }

    public void setItem(ItemStack item) {
        this.item = item;
    }

    public ItemStack getItem() {
        return item;
    }

    public HashMap<Action, org.bukkit.event.inventory.ClickType> getActions() {
        return actions;
    }

    public Menu getMenu() {
        return menu;
    }

    public void executeActions(Player player, ClickType clickType){
            for (Action action : actions.keySet()) {
                if(actions.get(action).equals(clickType)) action.executeAction(player,actions.get(action));
            }
    }

    @Override
    public MenuSlot clone() throws CloneNotSupportedException {
        return (MenuSlot) super.clone();
    }
}
