package dev.nova.menus.menu.actions.base;

import dev.nova.menus.menu.actions.Action;

import java.util.ArrayList;

public class RawAction {

    private final Class<? extends Action> clazz;
    private final ArrayList<Pram> constructorParams;

    public RawAction(Class<? extends Action> clazz, ArrayList<Pram> constructorParameters){
        this.clazz = clazz;
        this.constructorParams = constructorParameters;
    }

    public ArrayList<Pram> getConstructorParams() {
        return constructorParams;
    }

    public Class<? extends Action> getClazz() {
        return clazz;
    }
}
