package org.exallium.tradetracker.app;


public enum Screen {
    NONE(0),
    TRADES(1),
    CARDS(2),
    PEOPLE(3),
    STATS(4);

    private final int id;

    Screen(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}