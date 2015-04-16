package org.exallium.tradetracker.app.controller.adapters;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Pair;
import org.exallium.tradetracker.app.Screen;
import org.exallium.tradetracker.app.model.Observables;
import rx.Subscriber;
import rx.subjects.PublishSubject;
import rx.subjects.Subject;

public class ViewModelAdapterFactory {

    public static final String CARD_SET = "ViewModelAdapterFactory.cardSetCode";

    public static ViewModelAdapter createAdapter(Screen screen, @Nullable Bundle args) {

        String cardSetCode = null;
        if (args != null) {
            cardSetCode = args.getString(CARD_SET, null);
        }

        switch (screen) {
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
