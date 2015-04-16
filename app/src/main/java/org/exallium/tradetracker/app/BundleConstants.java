package org.exallium.tradetracker.app;

public class BundleConstants {
    public static final long NEW_OBJECT = -1;

    public static final String TRADE_ID = "tradeId";
    public static final String SCREEN_ID = "screenId";
    public static final String BUNDLE_ID = "bundleId";
    public static final String CARD_SET = "cardSetCode";
    public static final String LINE_ITEM_DIRECTION = "lineItemDirection";

    // Temporary Trade ID for proper creation of line items.  If user does not confirm
    // Trade creation and validation never successfully passes, trade and any referenced line items
    // should be destroyed
    public static final String TEMP_TRADE_ID = "tempTradeId";
}
