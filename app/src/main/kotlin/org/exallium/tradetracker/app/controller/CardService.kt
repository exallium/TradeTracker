package org.exallium.tradetracker.app.controller

import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.support.v4.app.NotificationCompat
import android.util.Log
import com.google.gson.Gson
import com.google.gson.stream.JsonReader
import com.orm.SugarRecord
import com.orm.query.Condition
import com.orm.query.Select
import org.exallium.tradetracker.app.R
import org.exallium.tradetracker.app.model.entities.Card
import org.exallium.tradetracker.app.model.entities.CardSet
import org.exallium.tradetracker.app.model.entities.LineItem
import org.exallium.tradetracker.app.model.entities.Trade
import org.exallium.tradetracker.app.model.rest.RestManager
import rx.Observable
import rx.Subscriber
import rx.functions.Action0
import rx.schedulers.Schedulers
import java.io.InputStreamReader
import java.util.ArrayList
import java.util.LinkedList
import java.util.UUID

public class CardService : Service() {

    private var restManager = RestManager(this)

    override fun onCreate() {
        super.onCreate()

        // Delete Temporary Trades FIRST
        var temp = Select.from(javaClass<Trade>()).where(Condition.prop("is_temporary").eq(true)).list()
        Observable.from(temp)
                .map { trade -> trade.getId() }
                .forEach({ id ->
                    SugarRecord.deleteAll(javaClass<LineItem>(), "trade = ?", id.toString())
                    SugarRecord.deleteAll(javaClass<Trade>(), "id = ?", id.toString())
                })

        if (getSharedPreferences(MainApplication.PREFERENCES, Context.MODE_PRIVATE).getBoolean(PREFERENCES_REQUIRE_DISK_UPDATE, true)) {
            doDiskLoad()
        } else {
            // doWebLoad()
        }

    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        return Service.START_NOT_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    private fun doDiskLoad() {
        val notificationBuilder = NotificationCompat.Builder(this)
            .setSmallIcon(R.drawable.ic_launcher)
            .setContentTitle(getString(R.string.init_title))
            .setContentText(getString(R.string.init_subtitle))
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())

        Observable?.create<Unit> { subscriber ->

            val jsonIn = getResources().openRawResource(R.raw.card_data)
            val gson = Gson()

            val reader = JsonReader(InputStreamReader(jsonIn))

            reader.beginArray()
            reader.beginArray()

            // Card Sets
            val cardSets = LinkedList<CardSet?>()
            while (reader.hasNext()) {
                val cardSetJsonObject: CardSetJsonObject = gson.fromJson(reader, javaClass<CardSetJsonObject>())
                var cardSet: CardSet? = Select.from(javaClass<CardSet>()).where(Condition.prop("code").eq(cardSetJsonObject.code)).first()

                if (cardSet == null) {
                    cardSet = CardSet()
                    cardSet?.count = cardSetJsonObject.count
                    cardSet?.code = cardSetJsonObject.code
                    cardSet?.name = cardSetJsonObject.name
                    cardSets.add(cardSet)
                }
            }

            SugarRecord.saveInTx(cardSets)

            reader.endArray()
            reader.beginArray()

            // Cards
            var cardSet: CardSet? = null
            val cards = LinkedList<Card?>()
            while (reader.hasNext()) {
                val cardJsonObject: CardJsonObject = gson.fromJson(reader, javaClass<CardJsonObject>())
                val cardSetCode = cardSet?.code

                if (cardSetCode == null || !cardJsonObject.set.equals(cardSetCode)) {
                    cardSet = Select.from(javaClass<CardSet>())
                        .where(Condition.prop("code").eq(cardJsonObject.set)).first()
                }

                var card: Card? = Select.from(javaClass<Card>())
                    .where(Condition.prop("card_set").eq(cardSet?.getId()),
                            Condition.prop("name").eq(cardJsonObject.name))
                    .first()

                if (card == null) {
                    card = Card()
                    card?.cardSet = cardSet
                    card?.name = cardJsonObject.name
                    card?.multiverseId = cardJsonObject.multiverseId
                    card?.uuid = UUID.nameUUIDFromBytes("%s[%s]".format(cardJsonObject.name, cardJsonObject.set).toByteArray("UTF-8")).toString()
                    cards.add(card)
                }
            }

            SugarRecord.saveInTx(cards)

            reader.endArray()
            reader.endArray()

            reader.close()

            subscriber.onCompleted()
        }.onBackpressureBlock().subscribeOn(Schedulers.io()).observeOn(Schedulers.io()).doOnCompleted {
            diskLoadCompleted()
        }.subscribe()

    }

    private fun diskLoadCompleted() {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(NOTIFICATION_ID)

        getSharedPreferences(MainApplication.PREFERENCES, Context.MODE_PRIVATE)
                .edit().putBoolean(PREFERENCES_REQUIRE_DISK_UPDATE, false).apply();

        // Do Web Load
        // doWebLoad()
    }

    private fun doWebLoad() {
        val builder = NotificationCompat.Builder(this)
            .setSmallIcon(R.drawable.ic_launcher)
            .setContentText(getString(R.string.init_subtitle))
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        restManager.getCardSetObservable().subscribe(object: Subscriber<String>() {

            override fun onStart() { request(1) }
            override fun onCompleted() {}
            override fun onError(e: Throwable) { Log.d("CardService", "Something went Horribly Wrong", e) }

            override fun onNext(cardSetCode: String) {
                val cardSet = Select.from(javaClass<CardSet>()).where(Condition.prop("code").eq(cardSetCode)).first()
                val cardResults = Select.from(javaClass<Card>()).where(Condition.prop("card_set").eq(cardSetCode)).list()

                if (cardResults.size() != cardSet.count) {
                    var page = 1;
                    var pageCount : Int = Math.ceil(cardSet.count / 20.0).toInt()

                    builder.setContentTitle(getString(R.string.download_title, page, pageCount, cardSet.code))
                    manager.notify(NOTIFICATION_ID, builder.build())

                    restManager.getCardsForSetObservable(cardSet, 1).doOnCompleted(OnCardPageDownloadedAction(cardSetCode, page, pageCount)).subscribe()
                } else {
                    request(1)
                }
            }


            inner class OnCardPageDownloadedAction(val cardSetCode: String, val page: Int, val total: Int) : Action0 {
                override fun call() {
                    if (page != total) {
                        val cardSet = Select.from(javaClass<CardSet>()).where(Condition.prop("code").eq(cardSetCode)).first()
                        restManager.getCardsForSetObservable(cardSet, page).doOnCompleted(OnCardPageDownloadedAction(cardSetCode, page + 1, total)).subscribe()
                        builder.setContentTitle(getString(R.string.download_title, page, total, cardSetCode))
                        manager.notify(NOTIFICATION_ID, builder.build())
                    } else {
                        manager.cancel(NOTIFICATION_ID)
                        request(1)
                    }
                }
            }

        })

    }

    private class CardJsonObject {
        var multiverseId : Int = -1
        var name : String = ""
        var set : String = ""
    }

    private class CardSetJsonObject {
        var count : Int = -1
        var code : String = ""
        var name : String = ""
    }

    companion object {
        val TAG = "CardService"
        val PREFERENCES_REQUIRE_DISK_UPDATE = "diskUpdateRequired"
        val NOTIFICATION_ID = 1
    }

}
