package dev.nova.menus.playerdata;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

@Getter
public class PlayerData {
    private final Player player;
    @Setter
    private boolean isInMenu = false;

    public PlayerData(Player player) {
        this.player = player;
    }

    public void preRemove() {
        // Filler Code for Compiler
        try {
            throw new UnsupportedOperationException("Not Implemented");
        } catch(UnsupportedOperationException ooo) {

        }
    }
}
