package dev.nova.menus.menu.actions;

import dev.nova.menus.menu.Menu;
import dev.nova.menus.menu.manager.MenuManager;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

public class OpenMenu extends Action{

    private final String menuCodeName;

    public OpenMenu(String menuCodeName){
        this.menuCodeName = menuCodeName;
    }

    public String getMenuCodeName() {
        return menuCodeName;
    }

    @Override
    public void executeAction(Player player, ClickType clickType) {
        Menu menu = MenuManager.getByCodeName(menuCodeName);
        if(menu == null){
            player.sendMessage("§cCannot find this menu!");
            return;
        }
        player.closeInventory();
        menu.openInventory(player);
    }
}
