package org.exallium.tradetracker.app.controller.adapters;

import android.os.Bundle;
import android.support.annotation.Nullable;
import org.exallium.tradetracker.app.BundleConstants;
import org.exallium.tradetracker.app.Screen;
import org.exallium.tradetracker.app.model.Observables;

public class ViewModelAdapterFactory {

    public static ViewModelAdapter createAdapter(Screen screen, @Nullable Bundle args) {

        String cardSetCode = null;
        boolean lineItemDirection = false;
        long tradeId = BundleConstants.NEW_OBJECT;
        if (args != null) {
            cardSetCode = args.getString(BundleConstants.CARD_SET, null);
            lineItemDirection = args.getBoolean(BundleConstants.LINE_ITEM_DIRECTION, false);
            tradeId = args.getLong(BundleConstants.TRADE_ID, BundleConstants.NEW_OBJECT);
        }

        switch (screen) {
            case TRADE:
                return new LineItemViewModelAdapter(Observables.getLineItemsObservable(lineItemDirection, tradeId));
            case CARDS:
                return new CardViewModelAdapter(Observables.getCardObservable(cardSetCode));
            case CARD_SETS:
                return new CardSetViewModelAdapter(Observables.getCardSetObservable());
            case TRADES:
            default:
                return new TradeViewModelAdapter(Observables.getTradeObservable());
        }

    }

}
