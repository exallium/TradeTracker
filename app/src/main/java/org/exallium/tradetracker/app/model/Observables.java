package org.exallium.tradetracker.app.model;

import android.net.Uri;
import io.realm.Realm;
import io.realm.RealmResults;
import org.exallium.tradetracker.app.MainApplication;
import org.exallium.tradetracker.app.R;
import org.exallium.tradetracker.app.model.entities.Card;
import org.exallium.tradetracker.app.model.entities.CardSet;
import org.exallium.tradetracker.app.model.entities.LineItem;
import org.exallium.tradetracker.app.model.entities.Trade;
import org.exallium.tradetracker.app.view.models.CardSetViewModel;
import org.exallium.tradetracker.app.view.models.CardViewModel;
import org.exallium.tradetracker.app.view.models.TradeViewModel;
import org.joda.time.LocalDate;
import rx.Observable;

import java.util.Iterator;
import java.util.UUID;

public abstract class Observables {

    public static Observable<CardSetViewModel> getCardSetObservable() { return cardSetObservable; }
    public static Observable<TradeViewModel> getTradeObservable() { return tradeObservable; }

    public static Observable<CardViewModel> getCardObservable(String cardSetCode) {
        return Observable.create(subscriber -> {

            final Realm realm = RealmManager.INSTANCE.getRealm();
            final RealmResults<Card> cards = realm.allObjects(Card.class).where().equalTo("cardSet.code", cardSetCode).findAll();
            cards.sort("name");

            for (Card card : cards) {
                subscriber.onNext(new CardViewModel(card.getName(), cardSetCode));
            }

            subscriber.onCompleted();
        });

    }

    private static Observable<CardSetViewModel> cardSetObservable = Observable.create(subscriber -> {

        final Realm realm = RealmManager.INSTANCE.getRealm();

        final RealmResults<CardSet> cardSets = realm.allObjects(CardSet.class);
        cardSets.sort("code");
        for (CardSet cardSet : cardSets) {
            subscriber.onNext(new CardSetViewModel(cardSet.getCode()));
        }

        subscriber.onCompleted();
    });

    private static Observable<TradeViewModel> tradeObservable = Observable.create(subscriber -> {

        final Realm realm = RealmManager.INSTANCE.getRealm();

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
                .filter(c -> c != null)
                .distinct(Card::getName)
                .toBlocking().getIterator();

        Iterator<String> descIterator = Observable.from(trade.getLineItems())
                .map(LineItem::getDescription)
                .filter(d -> d != null)
                .distinct()
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
