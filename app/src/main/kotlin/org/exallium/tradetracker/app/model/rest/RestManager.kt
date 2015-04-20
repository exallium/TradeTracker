package org.exallium.tradetracker.app.model.rest

import android.content.Context
import com.orm.query.Condition
import com.orm.query.Select
import org.exallium.tradetracker.app.model.entities.Card
import org.exallium.tradetracker.app.model.entities.CardSet
import retrofit.RestAdapter
import rx.Observable
import java.util.UUID

public class RestManager(val context: Context) {

    private val mtgApiRestService: MtgApiRestService
    private val mtgPriceRestService: MtgPriceRestService

    init {
        val r1 = RestAdapter.Builder()
            .setEndpoint("http://api.mtgapi.com/v2/")
            .build()
        mtgApiRestService = r1.create(javaClass<MtgApiRestService>())

        val r2 = RestAdapter.Builder()
            .setEndpoint("http://magictcgprices.appspot.com/api/")
            .build()
        mtgPriceRestService = r2.create(javaClass<MtgPriceRestService>())
    }

    public fun getCardSetObservable(): Observable<String> {
        return mtgApiRestService.getSets().map({ m -> m.get("sets") }).flatMap({ list ->
            Observable.from(list as List<*>)
        }).map({ s : Any? ->
            val setInfo = s as Map<String, Any>
            val setCode = setInfo.get("code").toString()
            var cardSet = Select.from(javaClass<CardSet>()).where(Condition.prop("code").eq("code")).first()

            if (cardSet == null) {
                cardSet = CardSet()
                cardSet.code = setCode
                cardSet.name = setInfo.get("name").toString()
                cardSet.count = setInfo.get("cardCount") as Int
            }

            setCode
        })
    }

    public fun getCardsForSetObservable(cardSet: CardSet, page: Int) : Observable<CardSet> {
        val cardSetCode = cardSet.code
        return mtgApiRestService.getCardsForSet(cardSetCode, page).map({ m -> m.get("cards") }).flatMap({
            list -> Observable.from(list as List<*>)
        }).map({ c: Any? ->
            val cardInfo = c as Map<String, Any>
            val cardName = cardInfo.get("name").toString()
            val cardId = UUID.nameUUIDFromBytes("%s[%s]".format(cardName, cardSetCode).toByteArray("UTF-8"))
            var card = Select.from(javaClass<Card>()).where(Condition.prop("uuid").eq(cardId)).first()
            if (card == null) {
                card = Card()
                card.name = cardName
                card.uuid = cardId.toString()
                card.cardSet = cardSet
            }

            cardSet
        })
    }

    public fun getPriceForCardObservable(card: Card): Observable<List<String>> {
        return mtgPriceRestService.getPriceInfo(card.name, card.cardSet.code)
    }

}
