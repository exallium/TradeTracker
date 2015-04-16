package org.exallium.tradetracker.app;


/**
 * These are all Fragments
 */
public enum Screen {

    NONE(0, R.string.screen_unknown),
    TRADES(1, R.string.screen_trades),
    CARD_SETS(2, R.string.screen_cards),
    PEOPLE(3, R.string.screen_people),
    STATS(4, R.string.screen_stats),
    CARDS(5, R.string.screen_cards),
    TRADE(6, R.string.screen_trade);

    private final int id;
    private final int nameRes;

    Screen(int id, int nameRes) {
        this.id = id;
        this.nameRes = nameRes;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return MainApplication.getInstance().getString(nameRes);
    }

    public static Screen getById(int id) {
        for (Screen screen : Screen.values()) {
            if (screen.id == id)
                return screen;
        }
        return NONE;
    }
}