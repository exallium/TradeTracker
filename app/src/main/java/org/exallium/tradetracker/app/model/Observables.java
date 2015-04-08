package org.exallium.tradetracker.app.model;

import android.net.Uri;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import org.exallium.tradetracker.app.MainApplication;
import org.exallium.tradetracker.app.model.entities.Card;
import org.exallium.tradetracker.app.model.entities.LineItem;
import org.exallium.tradetracker.app.model.entities.Trade;
import org.exallium.tradetracker.app.view.models.TradeViewModel;
import rx.Observable;

import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class Observables {

    private static Pattern cardNamePattern = Pattern.compile("card\\[(\\d)\\]");

    public static Observable<TradeViewModel> tradeObservable = Observable.create(subscriber -> {

        Realm realm = Realm.getInstance(MainApplication.getInstance());

        RealmResults<Trade> realmResults = realm.allObjects(Trade.class);
        for (Trade trade : realmResults) {
            subscriber.onNext(trade);
        }

        subscriber.onCompleted();

    }).map(t -> {

        Trade trade = (Trade) t;

        long value = Observable.from(trade.getLineItems())
                .map(LineItem::getValue)
                .reduce((l1, l2) -> l1 + l2)
                .map(l1 -> l1 / 100)
                .toBlocking().last();

        Realm realm = Realm.getInstance(MainApplication.getInstance());

        Iterator<String> descriptions = Observable.from(trade.getLineItems()).map(LineItem::getDescription).distinct().toBlocking().getIterator();

        RealmQuery<Card> query = realm.allObjects(Card.class).where();
        while (descriptions.hasNext()) {
            String desc = descriptions.next();
            Matcher matcher = cardNamePattern.matcher(desc);

            if (!matcher.matches())
                 continue;

            int cardId = Integer.parseInt(matcher.group());
            query.equalTo("id", cardId);
            if (descriptions.hasNext())
                query.or();
        }

        Card firstCard = query.findFirst();
        Uri imageUri = firstCard == null ? Uri.EMPTY : Uri.parse(firstCard.getImageUri());

        StringBuilder itemsTradedBuilder = new StringBuilder();
        for (Card card : query.findAll()) {
            itemsTradedBuilder.append(card.getName());
            itemsTradedBuilder.append(',');
        }

        for (LineItem item : trade.getLineItems()) {
            if (cardNamePattern.matcher(item.getDescription()).matches())
                continue;
            itemsTradedBuilder.append(item.getDescription());
            itemsTradedBuilder.append(',');
        }
        itemsTradedBuilder.deleteCharAt(itemsTradedBuilder.length() - 1);

        return new TradeViewModel(
                trade.getId(),
                String.format("%c%d", value >= 0 ? '+' : '-', value),
                trade.getPerson().getName(),
                imageUri,
                trade.getLineItems().size(),
                itemsTradedBuilder.toString()
        );
    });

}
