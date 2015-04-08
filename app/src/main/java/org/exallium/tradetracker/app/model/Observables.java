package org.exallium.tradetracker.app.model;

import io.realm.Realm;
import io.realm.RealmResults;
import org.exallium.tradetracker.app.MainApplication;
import org.exallium.tradetracker.app.model.entities.LineItem;
import org.exallium.tradetracker.app.model.entities.Trade;
import org.exallium.tradetracker.app.view.models.TradeViewModel;
import rx.Observable;

public abstract class Observables {

    public static Observable<TradeViewModel> tradeObservable = Observable.create(subscriber -> {

        Realm realm = Realm.getInstance(MainApplication.getInstance());

        RealmResults<Trade> realmResults = realm.allObjects(Trade.class);
        for (Trade trade : realmResults) {
            subscriber.onNext(trade);
        }

        subscriber.onCompleted();

    }).map(trade -> {

        long value = 0;
        StringBuilder itemsTradedBuilder = new StringBuilder();
        for (LineItem item : ((Trade) trade).getLineItems()) {
            value += item.getValue();
            itemsTradedBuilder.append(item.getDescription());
            itemsTradedBuilder.append(',');
        }
        itemsTradedBuilder.deleteCharAt(itemsTradedBuilder.length() - 1);
        value = value / 100;

        return new TradeViewModel(
                ((Trade) trade).getId(),
                String.format("%c%d", value >= 0 ? '+' : '-', value),
                ((Trade) trade).getPerson().getName(),
                null,
                ((Trade) trade).getLineItems().size(),
                itemsTradedBuilder.toString()
        );
    });

}
