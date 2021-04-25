package dev.nova.menus.menu;

public enum MenuBorderColor {

    WHITE(0),
    ORANGE(1),
    MAGENTA(2),
    LIGHT_BLUE(3),
    YELLOW(4),
    LIME(5),
    PINK(6),
    GRAY(7),
    LIGHT_GRAY(8),
    CYAN(9),
    PURPLE(10),
    BLUE(11),
    BROWN(12),
    GREEN(13),
    RED(14),
    BLACK(15),
    RAINBOW(-1);

    private final int color;

    MenuBorderColor(int i) {
        this.color = i;
    }

    public int getNumber() {
        return color;
    }
    public static MenuBorderColor getFromNumber(int i){
        return MenuBorderColor.values()[i];
    }
}
