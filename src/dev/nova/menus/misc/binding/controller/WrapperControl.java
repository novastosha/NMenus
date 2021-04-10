package dev.nova.menus.misc.binding.controller;

import dev.nova.menus.misc.binding.versions.Wrapper1_12_R1;
import dev.nova.menus.misc.binding.wrapper.BindWrapper;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.Arrays;

public class WrapperControl {

    private final String serverVersion = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3].substring(1);

    public static final ArrayList<Class<? extends BindWrapper>> blockWrappers = new ArrayList<>(Arrays.asList(
            Wrapper1_12_R1.class
    ));

    public BindWrapper get(){
            try {
                return blockWrappers.stream()
                        .filter(version -> version.getSimpleName().substring(7).equals(serverVersion))
                        .findFirst().orElseThrow(() -> new RuntimeException("Your server version isn't supported so you can't use biding mechanics!"))
                        .newInstance();
            } catch (IllegalAccessException | InstantiationException ex) {
                throw new RuntimeException(ex);
            }
    }
}
