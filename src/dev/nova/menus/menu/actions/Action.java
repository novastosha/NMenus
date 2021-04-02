package dev.nova.menus.menu.actions;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.ArrayList;

public abstract class Action {

    public static final ArrayList<Action> REGISTERED_ACTIONS = new ArrayList<>();
    private final String id;

    public abstract void executeAction(Player player, ClickType clickType);

    public Action(String id){
        this.id = id;
        register();
    }

    public String getId() {
        return id;
    }

    protected void register(){
        REGISTERED_ACTIONS.add(this);
    }
}
