package org.exallium.tradetracker.app;

/**
 * These are all dialogs
 */
public enum DialogScreen {
    LINE_ITEM_CARD_DIALOG(0),
    LINE_ITEM_CASH_DIALOG(1),
    LINE_ITEM_MISC_DIALOG(2),
    LINE_ITEM_TYPE_DIALOG(3);

    private final int id;

    DialogScreen(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
