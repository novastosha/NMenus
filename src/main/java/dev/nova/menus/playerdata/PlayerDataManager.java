package dev.nova.menus.playerdata;

import lombok.experimental.UtilityClass;
import org.bukkit.entity.Player;

import java.util.HashMap;

@UtilityClass
public class PlayerDataManager {
    private static HashMap<Player, PlayerData> data;

    public PlayerData getData(Player p) {
        return data.get(p);
    }

    public void addData(Player player) {
        if (data == null) data = new HashMap<>();
        data.putIfAbsent(player, new PlayerData(player));
    }

    public void deleteData(Player player) {
        data.get(player).preRemove();
        data.remove(player);
    }
}
