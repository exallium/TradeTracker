package org.exallium.tradetracker.app.model;

import android.net.Uri;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import org.exallium.tradetracker.app.MainApplication;
import org.exallium.tradetracker.app.R;
import org.exallium.tradetracker.app.model.entities.Card;
import org.exallium.tradetracker.app.model.entities.LineItem;
import org.exallium.tradetracker.app.model.entities.Trade;
import org.exallium.tradetracker.app.view.models.TradeViewModel;
import org.joda.time.LocalDate;
import rx.Observable;

import java.util.Iterator;
import java.util.UUID;
import java.util.regex.Pattern;

public abstract class Observables {

    public static Observable<TradeViewModel> tradeObservable = Observable.create(subscriber -> {

        final Realm realm = Realm.getInstance(MainApplication.getInstance());

        final RealmResults<Trade> realmResults = realm.allObjects(Trade.class);
        realmResults.sort("tradeDate", false);
        for (Trade trade : realmResults)
            subscriber.onNext(trade);

        subscriber.onCompleted();

    }).map(t -> {

        Trade trade = (Trade) t;

        long value = trade.getLineItems().isEmpty() ? 0 : Observable.from(trade.getLineItems())
                .map(LineItem::getValue)
                .reduce((l1, l2) -> l1 + l2)
                .map(l1 -> l1 / 100)
                .toBlocking().last();

        Iterator<Card> cardIterator = Observable.from(trade.getLineItems())
                .map(LineItem::getCard)
                .distinct()
                .filter(c -> c != null)
                .toBlocking().getIterator();

        Iterator<String> descIterator = Observable.from(trade.getLineItems())
                .map(LineItem::getDescription)
                .distinct()
                .filter(d -> d != null)
                .toBlocking().getIterator();

        Uri imageUri = null;
        StringBuilder itemsTradedBuilder = new StringBuilder();

        while (cardIterator.hasNext()) {
            Card card = cardIterator.next();
            if (imageUri == null)
                imageUri = Uri.parse(card.getImageUri());
            itemsTradedBuilder.append(String.format("%s [%s], ", card.getName(), card.getCardSet().getCode()));
        }

        while (descIterator.hasNext()) {
            String description = descIterator.next();
            itemsTradedBuilder.append(String.format("%s, ", description));
        }

        if (itemsTradedBuilder.length() > 0)
            itemsTradedBuilder.deleteCharAt(itemsTradedBuilder.length() - 2);
        else
            itemsTradedBuilder.append(MainApplication.getInstance().getResources().getString(R.string.no_line_items_available));

        return new TradeViewModel(
                UUID.fromString(trade.getUid()),
                String.format("%c%d", value >= 0 ? '+' : '-', value),
                trade.getPerson().getName(),
                imageUri,
                trade.getLineItems().size(),
                itemsTradedBuilder.toString(),
                LocalDate.fromDateFields(trade.getTradeDate())
        );
    });

}
