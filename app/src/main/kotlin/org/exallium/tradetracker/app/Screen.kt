package org.exallium.tradetracker.app

import android.content.Context

public enum class Screen(public val id: Int, private val nameRes: Int) {

    NONE : Screen(0, R.string.screen_unknown)
    TRADES : Screen(1, R.string.screen_trades)
    CARD_SETS : Screen(2, R.string.screen_cards)
    PEOPLE : Screen(3, R.string.screen_people)
    STATS : Screen(4, R.string.screen_stats)
    CARDS : Screen(5, R.string.screen_cards)
    TRADE : Screen(6, R.string.screen_trade)

    public fun getName(): String {
        return Context.getString(nameRes)
    }

    public fun getById(id: Int): Screen {
        for (screen in values()) {
            if (screen.id == id)
                return screen
        }
        return NONE
    }
}