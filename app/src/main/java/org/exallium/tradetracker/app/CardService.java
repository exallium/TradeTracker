package org.exallium.tradetracker.app;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import io.realm.Realm;
import io.realm.RealmResults;
import org.exallium.tradetracker.app.model.RealmManager;
import org.exallium.tradetracker.app.model.entities.Card;
import org.exallium.tradetracker.app.model.entities.CardSet;
import org.exallium.tradetracker.app.model.rest.RestServiceManager;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

import java.io.*;
import java.util.List;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

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

    private int cardCount = 0;

    private void doDiskLoad() {

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle("Initialising Database")
                .setContentText("This might take a while");
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, builder.build());

        Observable.create(subscriber -> {

            Gson gson = new Gson();
            ZipInputStream zipInputStream = new ZipInputStream(getResources().openRawResource(R.raw.card_data));
            ZipEntry entry = null;
            Realm realm = Realm.getInstance(CardService.this);

            try {
                entry = zipInputStream.getNextEntry();

                while (entry != null) {
                    JsonReader reader = new JsonReader(new InputStreamReader(zipInputStream, "UTF-8"));
                    reader.beginArray();
                    reader.beginArray();

                    while (reader.hasNext()) {
                        CardSetJsonObject object = gson.fromJson(reader, CardSetJsonObject.class);
                        CardSet cardSet = realm.allObjects(CardSet.class).where().equalTo("code", object.code).findFirst();
                        if (cardSet == null) {
                            realm.beginTransaction();
                            cardSet = realm.createObject(CardSet.class);
                            cardSet.setCode(object.code);
                            cardSet.setCount(object.count);
                            realm.commitTransaction();
                        }

                    }

                    reader.endArray();
                    reader.beginArray();

                    while (reader.hasNext()) {
                        CardJsonObject cardJsonObject = gson.fromJson(reader, CardJsonObject.class);
                        cardCount++;
                        if (cardCount % 100 == 0) {
                            builder.setContentText("Loading cards: " + cardCount);
                            notificationManager.notify(NOTIFICATION_ID, builder.build());
                        }
                        subscriber.onNext(cardJsonObject);
                    }

                    reader.endArray();
                    reader.endArray();
                    reader.close();
                    entry = zipInputStream.getNextEntry();
                }

            } catch (FileNotFoundException e) {
                Log.e(TAG, "Couldn't find file in zip", e);
            } catch (IOException e) {
                Log.e(TAG, "Couldn't get entry", e);
            }

            try {
                zipInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            realm.close();
            subscriber.onCompleted();
        })
                .onBackpressureBuffer()
                .observeOn(Schedulers.newThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Subscriber<Object>() {

                    @Override
                    public void onCompleted() {
                        diskLoadComplete();
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(Object cjo) {
                        CardJsonObject cardJsonObject = (CardJsonObject) cjo;
                        Realm realm = Realm.getInstance(CardService.this);
                        Card card = realm.allObjects(Card.class).where().equalTo("name", cardJsonObject.name).equalTo("cardSet.code", cardJsonObject.set).findFirst();

                        if (card == null) {
                            CardSet cardSet = realm.allObjects(CardSet.class).where().equalTo("code", cardJsonObject.set).findFirst();
                            realm.beginTransaction();
                            card = realm.createObject(Card.class);
                            card.setName(cardJsonObject.name);
                            card.setCardSet(cardSet);
                            UUID cardId = UUID.nameUUIDFromBytes(String.format("%s[%s]", cardJsonObject.name, cardJsonObject.set).getBytes());
                            card.setId(cardId.toString());
                            realm.commitTransaction();
                        }

                        realm.close();

                        cardCount--;
                        if (cardCount % 100 == 0) {
                            builder.setContentText("Loading Cards: " + cardCount);
                            notificationManager.notify(NOTIFICATION_ID, builder.build());
                        }
                    }
                });
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
                Realm realm = RealmManager.INSTANCE.getRealm();
                CardSet cardSet = realm.allObjects(CardSet.class).where().equalTo("code", cardSetCode).findFirst();
                RealmResults<Card> cardResults = realm.allObjects(Card.class).where().equalTo("cardSet.code", cardSetCode).findAll();

                if (cardResults.size() != cardSet.getCount()) {
                    int page = 1;
                    int pageCount = (int) Math.round(Math.ceil(cardSet.getCount() / 20.0f));

                    builder.setContentTitle(String.format("Downloading Page %d/%d of " + cardSet.getCode(), page, pageCount));
                    notificationManager.notify(NOTIFICATION_ID, builder.build());

                    restServiceManager.getCardsForSetObservable(cardSet, 1).doOnCompleted(new OnCardPageDownloadedAction(cardSet.getCode(), page, pageCount)).subscribe();
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
                        Realm realm = RealmManager.INSTANCE.getRealm();
                        CardSet cardSet = realm.allObjects(CardSet.class).where().equalTo("code", cardSetCode).findFirst();
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
