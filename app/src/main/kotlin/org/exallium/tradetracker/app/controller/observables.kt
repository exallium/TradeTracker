package org.exallium.tradetracker.app.model

import android.net.Uri
import com.orm.query.Condition
import com.orm.query.Select
import org.exallium.tradetracker.app.R
import org.exallium.tradetracker.app.controller.BundleConstants
import org.exallium.tradetracker.app.controller.MainApplication
import org.exallium.tradetracker.app.model.entities.Card
import org.exallium.tradetracker.app.model.entities.CardSet
import org.exallium.tradetracker.app.model.entities.LineItem
import org.exallium.tradetracker.app.model.entities.Trade
import org.exallium.tradetracker.app.view.models.CardSetViewModel
import org.exallium.tradetracker.app.view.models.CardViewModel
import org.exallium.tradetracker.app.view.models.LineItemViewModel
import org.exallium.tradetracker.app.view.models.TradeViewModel
import org.joda.time.LocalDate
import rx.Observable

public object Observables {
    public fun getCardSetObservable(): Observable<CardSetViewModel> {
        return cardSetViewModelObservable
    }

    public fun getTradeObservable(): Observable<TradeViewModel> {
        return tradeViewModelObservable
    }

    public fun getLineItemObservable(direction: Boolean, tradeId: Long): Observable<LineItemViewModel> {
        return Observable.create<LineItem> { subscriber ->
            if (tradeId != BundleConstants.NEW_OBJECT) {
                Select.from(javaClass<LineItem>())
                        .where(Condition.prop("trade_id").eq(tradeId), Condition.prop("direction").eq(direction))
                        .list().forEach { lineItem ->
                    subscriber.onNext(lineItem)
                }
            }
            subscriber.onCompleted()
        }.map { lineItem ->
            val description = if (lineItem.card != null) lineItem.card.name else lineItem.description
            LineItemViewModel(description)
        }
    }

    public fun getCardObservable(cardSetCode: String): Observable<CardViewModel> {
        var cardSet = Select.from(javaClass<CardSet>()).where(Condition.prop("code").eq(cardSetCode)).first()
        return Observable.from(Select.from(javaClass<Card>()).where(Condition.prop("card_set").eq(cardSet.getId())).list())
                .map { card -> CardViewModel(card.name, cardSet.code) }
    }

    private val cardSetViewModelObservable: Observable<CardSetViewModel> = Observable.create { subscriber ->
        val cardSets = Select.from(javaClass<CardSet>()).orderBy("name").list()
        cardSets.forEach { cardSet ->
            subscriber.onNext(CardSetViewModel(cardSet.code, cardSet.name))
        }
        subscriber.onCompleted()
    }

    private val tradeViewModelObservable: Observable<TradeViewModel> = Observable.from(
            Select.from(javaClass<Trade>()).where(Condition.prop("is_temporary").eq(false)).orderBy("trade_date").list()
    ).map { trade ->
        val lineItems = Select.from(javaClass<LineItem>()).where(Condition.prop("trade_id").eq(trade.getId())).list()
        val tradeValue = if (lineItems.size() == 0) 0 else Observable.from(lineItems)
                .map { item -> item.value }
                .reduce { item1, item2 -> item1 + item2 }
                .map { item -> item / 100L }
                .toBlocking().last()
        val cardIterator = Observable.from(lineItems)
                .map { lineItem -> lineItem.card }
                .filter { c -> c != null }
                .distinct { card1 -> card1.name }
                .toBlocking().getIterator();
        val descIterator = Observable.from<LineItem>(lineItems)
                .map<String> { lineItem -> lineItem.description }
                .filter { d -> d != null }
                .distinct().toBlocking().getIterator()

        var imageUri: Uri? = null
        val itemsTradedBuilder = StringBuilder()

        for (card in cardIterator) {
            if (imageUri == null && card.multiverseId != -1)
                imageUri = Uri.parse("http://gatherer.wizards.com/handlers/image.ashx/?type=card&multiverseid=" + card.multiverseId);
            itemsTradedBuilder.append("%s [%s], ".format(card.name, card.cardSet.code))
        }

        for (desc in descIterator) {
            itemsTradedBuilder.append("%s, ".format(desc))
        }

        if (itemsTradedBuilder.length() > 0)
            itemsTradedBuilder.deleteCharAt(itemsTradedBuilder.length() - 2)
        else
            itemsTradedBuilder.append(MainApplication.getResources().getString(R.string.no_line_items_available))

        TradeViewModel(
                trade.getId(),
                "%c%d".format(if (tradeValue >= 0) '+' else 0, tradeValue),
                trade.person.name,
                imageUri,
                lineItems.size(),
                itemsTradedBuilder.toString(),
                LocalDate.fromDateFields(trade.tradeDate)
        )
    }
}





