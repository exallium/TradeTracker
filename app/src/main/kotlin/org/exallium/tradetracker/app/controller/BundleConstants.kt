package org.exallium.tradetracker.app.controller

public class BundleConstants {
    companion object {
        public val NEW_OBJECT: Long = (-1).toLong()

        public val TRADE_ID: String = "tradeId"
        public val SCREEN_ID: String = "screenId"
        public val BUNDLE_ID: String = "bundleId"
        public val CARD_SET: String = "cardSetCode"
        public val LINE_ITEM_DIRECTION: String = "lineItemDirection"

        // Temporary Trade ID for proper creation of line items.  If user does not confirm
        // Trade creation and validation never successfully passes, trade and any referenced line items
        // should be destroyed
        public val TEMP_TRADE_ID: String = "tempTradeId"
    }
}