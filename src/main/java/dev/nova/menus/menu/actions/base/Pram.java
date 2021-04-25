package dev.nova.menus.menu.actions.base;

public class Pram {

    private final Object value;
    private final String config;
    private final int index;

    public Pram(Object value, String config,int index){
        this.value = value;
        this.index = index;
        this.config = config;
    }

    public Object getValue() {
        return value;
    }

    public String getConfig() {
        return config;
    }

    public int getIndex() {
        return index;
    }
}
