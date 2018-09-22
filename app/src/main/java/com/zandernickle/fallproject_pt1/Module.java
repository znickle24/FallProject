package com.zandernickle.fallproject_pt1;

public enum Module {

    WEATHER("WEATHER"),
    HIKES("HIKES"),
    HEALTH("HEALTH");

    private final String str;

    Module(final String str) {
        this.str = str;
    }

    @Override
    public String toString() {
        return str;
    }
}
