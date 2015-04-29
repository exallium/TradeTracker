package org.exallium.tradetracker.app.model.rest

import android.content.Context
import android.util.Log
import com.orm.SugarRecord
import com.orm.query.Condition
import com.orm.query.Select
import org.exallium.tradetracker.app.model.entities.Card
import org.exallium.tradetracker.app.model.entities.CardSet
import org.exallium.tradetracker.app.model.entities.LineItem
import org.exallium.tradetracker.app.model.entities.TcgProduct
import org.exallium.tradetracker.app.model.rest.converters.SimpleXMLConverter
import retrofit.RestAdapter
import rx.Observable
import java.util.UUID
import kotlin.concurrent.thread

public class RestManager(val context: Context) {

    private val mtgApiRestService: MtgApiRestService
    private val tcgPlayerRestService: TcgPlayerRestService

    init {
        val r1 = RestAdapter.Builder()
            .setEndpoint("http://api.mtgapi.com/v2/")
            .build()
        mtgApiRestService = r1.create(javaClass<MtgApiRestService>())

        // TODO: Spin up a second adapter for tcgplayer
        val r2 = RestAdapter.Builder()
            .setEndpoint("http://partner.tcgplayer.com/x3/phl.asmx")
            .setConverter(SimpleXMLConverter())
            .build()
        tcgPlayerRestService = r2.create(javaClass<TcgPlayerRestService>())
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
            val cardId = UUID.nameUUIDFromBytes("%s [%s]".format(cardName, cardSetCode).toByteArray("UTF-8"))
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

    public fun getPriceForCard(card: Card) {
 /*       tcgPlayerRestService.getPriceForCard("MTGFAMILIA", card.cardSet?.name?:"", card.name)
            .forEach({
                list ->
                list.forEach {
                    prod ->
                    SugarRecord.deleteAll(javaClass<TcgProduct>(), "card = ?", card.getId().toString())
                    prod.card = card
                    prod.save()

                    // TODO possible tx optimization here
                    Select.from(javaClass<LineItem>()).where(Condition.prop("card").eq(card.getId()))
                        .list().forEach {
                        it.value = (prod.mid * 100).toLong()
                        it.save()
                    }
                }
            })*/
        thread {
            val prods = tcgPlayerRestService.getPriceForCard("MTGFAMILIA", card.cardSet?.name?:"", card.name)
            Log.d("THREAD", "" + prods.tcgProducts)
        }
    }
}
