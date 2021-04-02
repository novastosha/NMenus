package dev.nova.menus.menu.actions.base;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

public enum PramType {

    BOOLEAN(Boolean.class),
    STRING(String.class),
    INTEGER(Integer.class),
    OBJECT(Object.class),
    CONFIGURATION_SECTION(ConfigurationSection.class),
    LOCATION(Location.class);

    private final Class<?> clazz;

    PramType(Class<?> clazz) {
        this.clazz = clazz;
    }

    public Class<?> getClazz() {
        return clazz;
    }
}
