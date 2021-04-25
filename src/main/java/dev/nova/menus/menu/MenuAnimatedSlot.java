package dev.nova.menus.menu;

import dev.nova.menus.menu.Menu;
import dev.nova.menus.menu.MenuSlot;
import dev.nova.menus.menu.actions.Action;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;

public class MenuAnimatedSlot extends Slot{

    private final int refreshRate;
    private final List<MenuSlot> slots;
    private final int slot;
    private final HashMap<Action,org.bukkit.event.inventory.ClickType> actions;
    private final boolean canBePicked;
    private Menu menu;

    public MenuAnimatedSlot(int slot, HashMap<Action, ClickType> actions, boolean canBePicked , int refreshRate, List<MenuSlot> asSlots){
        this.refreshRate = refreshRate;
        this.actions = actions;
        this.canBePicked = canBePicked;
        this.slot = slot;
        this.slots = asSlots;
    }

    public HashMap<Action, ClickType> getActions() {
        return actions;
    }

    public boolean canBePicked() {
        return canBePicked;
    }

    public List<MenuSlot> getSlots() {
        return slots;
    }

    public int getRefreshRate() {
        return refreshRate;
    }

    public void setMenu(Menu menu) {
        this.menu = menu;
    }

    public Menu getMenu() {
        return menu;
    }

    public int getSlotNumber() {
        return slot;
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
