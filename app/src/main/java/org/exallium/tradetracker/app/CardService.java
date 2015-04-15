package org.exallium.tradetracker.app;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.orm.query.Condition;
import com.orm.query.Select;
import org.exallium.tradetracker.app.model.entities.Card;
import org.exallium.tradetracker.app.model.entities.CardSet;
import org.exallium.tradetracker.app.model.rest.RestServiceManager;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class CardService extends Service {

    private static final String TAG = CardService.class.getSimpleName();
    private static final String PREFS_REQUIRE_DISK_UPDATE = "org.exallium.tradetracker.app";
    private static final int NOTIFICATION_ID = 1;

    private RestServiceManager restServiceManager;

    private class CardJsonObject {
        String name;
        String set;
    }

    private class CardSetJsonObject {
        int count;
        String code;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_NOT_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        restServiceManager = new RestServiceManager(this);

        if (getSharedPreferences(MainApplication.PREFERENCES, Context.MODE_PRIVATE).getBoolean(PREFS_REQUIRE_DISK_UPDATE, true))
            doDiskLoad();
        else
            doWebLoad();

    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
    }

    private void doDiskLoad() {

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle("Initialising Database")
                .setContentText("This might take a while");
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, builder.build());

        Observable.create(subscriber -> {

            InputStream jsonIn = getResources().openRawResource(R.raw.card_data);

            try {
                Gson gson = new Gson();
                JsonReader reader = new JsonReader(new InputStreamReader(jsonIn));
                reader.beginArray();
                reader.beginArray();

                List<CardSet> cardSets = new ArrayList<>();
                while (reader.hasNext()) {
                    CardSetJsonObject cardSetJsonObject = gson.fromJson(reader, CardSetJsonObject.class);
                    CardSet cardSet = Select.from(CardSet.class).where(Condition.prop("code").eq(cardSetJsonObject.code)).first();

                    if (cardSet == null) {
                        cardSet = new CardSet();
                        cardSet.count = cardSetJsonObject.count;
                        cardSet.code = cardSetJsonObject.code;
                        cardSets.add(cardSet);
                    }

                }

                CardSet.saveInTx(cardSets);

                reader.endArray();
                reader.beginArray();

                List<Card> cards = new ArrayList<>();
                while (reader.hasNext()) {
                    CardJsonObject cardJsonObject = gson.fromJson(reader, CardJsonObject.class);
                    Card card = Select.from(Card.class)
                            .where(Condition.prop("cardSet.code").eq(cardJsonObject.set), Condition.prop("name").eq(cardJsonObject.name))
                            .first();

                    if (card == null) {
                        CardSet cardSet = Select.from(CardSet.class).where(Condition.prop("code").eq(cardJsonObject.set)).first();
                        card = new Card();
                        card.cardSet = cardSet;
                        card.name = cardJsonObject.name;
                        cards.add(card);
                    }
                }

                Card.saveInTx(cards);

                reader.endArray();
                reader.endArray();

                reader.close();

            } catch (IOException e) {
                e.printStackTrace();
            }

        }).onBackpressureBlock()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io()).doOnCompleted(CardService.this::diskLoadComplete).subscribe();
    }

    private void diskLoadComplete() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFICATION_ID);

        getSharedPreferences(MainApplication.PREFERENCES, Context.MODE_PRIVATE)
            .edit().putBoolean(PREFS_REQUIRE_DISK_UPDATE, false).apply();
        doWebLoad();
    }

    private void doWebLoad(){

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentText("This might take a while");
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        restServiceManager.getCardSetObservable().subscribe(new Subscriber<String>() {

            @Override
            public void onStart() {
                request(1);
            }

            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                Log.d("CardService", "Something bad happened", e);
            }

            @Override
            public void onNext(String cardSetCode) {
                CardSet cardSet = Select.from(CardSet.class).where(Condition.prop("code").eq(cardSetCode)).first();
                List<Card> cardResults = Select.from(Card.class).where(Condition.prop("cardSet.code").eq(cardSetCode)).list();

                if (cardResults.size() != cardSet.count) {
                    int page = 1;
                    int pageCount = (int) Math.round(Math.ceil(cardSet.count / 20.0f));

                    builder.setContentTitle(String.format("Downloading Page %d/%d of " + cardSet.code, page, pageCount));
                    notificationManager.notify(NOTIFICATION_ID, builder.build());

                    restServiceManager.getCardsForSetObservable(cardSet, 1).doOnCompleted(new OnCardPageDownloadedAction(cardSet.code, page, pageCount)).subscribe();
                } else {
                    request(1);
                }

            }

            class OnCardPageDownloadedAction implements Action0 {

                final int page;
                final int total;
                final String cardSetCode;

                OnCardPageDownloadedAction(String cardSetCode, int page, int total) {
                    this.page = page;
                    this.total = total;
                    this.cardSetCode = cardSetCode;
                }

                @Override
                public void call() {
                    if (page != total) {
                        CardSet cardSet = Select.from(CardSet.class).where(Condition.prop("code").eq(cardSetCode)).first();
                        restServiceManager.getCardsForSetObservable(cardSet, page).doOnCompleted(new OnCardPageDownloadedAction(cardSetCode, page + 1, total)).subscribe();
                        builder.setContentTitle(String.format("Downloading Page %d/%d of " + cardSetCode, page + 1, total));
                        notificationManager.notify(NOTIFICATION_ID, builder.build());
                    } else {
                        notificationManager.cancel(NOTIFICATION_ID);
                        request(1);
                    }
                }
            }
        });
    }

}
