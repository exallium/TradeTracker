package org.exallium.tradetracker.app.controller.adapters;

import org.exallium.tradetracker.app.Screen;
import org.exallium.tradetracker.app.model.Observables;

public class ViewModelAdapterFactory {

    public static ViewModelAdapter createAdapter(Screen screen) {
        switch (screen) {
            case TRADES:
            default:
                return new TradeViewModelAdapter(Observables.tradeObservable);
        }

    }

}
