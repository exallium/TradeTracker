package org.exallium.tradetracker.app.model;

import android.net.Uri;
import com.orm.query.Condition;
import com.orm.query.Select;
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
import java.util.List;

public abstract class Observables {

    public static Observable<CardSetViewModel> getCardSetObservable() { return cardSetObservable; }
    public static Observable<TradeViewModel> getTradeObservable() { return tradeObservable; }

    public static Observable<CardViewModel> getCardObservable(String cardSetCode) {
        return Observable.create(subscriber -> {

            final CardSet cardSet = Select.from(CardSet.class).where(Condition.prop("code").eq(cardSetCode)).first();
            final List<Card> cards = Select.from(Card.class)
                    .where(Condition.prop("card_set").eq(cardSet.getId()))
                    .orderBy("name").list();

            for (Card card : cards) {
                subscriber.onNext(new CardViewModel(card.name, cardSetCode));
            }

            subscriber.onCompleted();
        });

    }

    private static Observable<CardSetViewModel> cardSetObservable = Observable.create(subscriber -> {

        final List<CardSet> cardSets = Select.from(CardSet.class).orderBy("name").list();
        for (CardSet cardSet : cardSets) {
            subscriber.onNext(new CardSetViewModel(cardSet.code, cardSet.name));
        }

        subscriber.onCompleted();
    });

    private static Observable<TradeViewModel> tradeObservable = Observable.create(subscriber -> {

        final List<Trade> trades = Select.from(Trade.class).orderBy("trade_date").list();
        for (Trade trade : trades)
            subscriber.onNext(trade);

        subscriber.onCompleted();

    }).map(t -> {

        Trade trade = (Trade) t;

        List<LineItem> lineItems = Select.from(LineItem.class).where(Condition.prop("trade").eq(trade.getId())).list();

        long value = lineItems.isEmpty() ? 0 : Observable.from(lineItems)
                .map(lineItem -> lineItem.value)
                .reduce((l1, l2) -> l1 + l2)
                .map(l1 -> l1 / 100)
                .toBlocking().last();

        Iterator<Card> cardIterator = Observable.from(lineItems)
                .map(lineItem -> lineItem.card)
                .filter(c -> c != null)
                .distinct(card1 -> card1.name)
                .toBlocking().getIterator();

        Iterator<String> descIterator = Observable.from(lineItems)
                .map(lineItem -> lineItem.description)
                .filter(d -> d != null)
                .distinct()
                .toBlocking().getIterator();

        Uri imageUri = null;
        StringBuilder itemsTradedBuilder = new StringBuilder();

        while (cardIterator.hasNext()) {
            Card card = cardIterator.next();
            if (imageUri == null && card.multiverseId != -1)
                imageUri = Uri.parse("http://gatherer.wizards.com/handlers/image.ashx/?type=card&multiverseid=" + card.multiverseId);
            itemsTradedBuilder.append(String.format("%s [%s], ", card.name, card.cardSet.code));
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
                trade.getId(),
                String.format("%c%d", value >= 0 ? '+' : '-', value),
                trade.person.name,
                imageUri,
                lineItems.size(),
                itemsTradedBuilder.toString(),
                LocalDate.fromDateFields(trade.tradeDate)
        );
    });

}
