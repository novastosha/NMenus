package dev.nova.menus.misc.binding.wrapper;

import dev.nova.menus.menu.Menu;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface BindWrapper {

    ItemStack bind(ItemStack itemStack, Menu menu);
    void bind(Block block, Menu menu);

    boolean clickItem(Player player);

}
