package com.zandernickle.fallproject_pt1;

public enum Module {

    SIGN_IN("SIGN IN"),
    FITNESS_INPUT("FITNESS_INPUT"),
    MENU_BAR("MENU BAR"),
    WEATHER("WEATHER"),
    HIKES("HIKES"),
    HEALTH("HEALTH"),
    PLAYGROUND("PLAYGROUND"),
    MASTER_LIST("MASTER_LIST"),
    UPDATE_GOALS("UPDATE_GOALS");

    private final String str;

    Module(final String str) {
        this.str = str;
    }

    @Override
    public String toString() {
        return str;
    }
}
