package org.exallium.tradetracker.app;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import io.realm.Realm;
import io.realm.RealmResults;
import org.exallium.tradetracker.app.model.entities.Card;
import org.exallium.tradetracker.app.model.entities.CardSet;
import org.exallium.tradetracker.app.model.rest.RestServiceManager;
import rx.Observable;
import rx.Subscriber;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class CardService extends Service {

    private static final int NOTIFICATION_ID = 1;

    private int setDownloadCount = 0;

    private RestServiceManager restServiceManager;

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

        // Output of one observable feeds as input into other observable
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle(String.format("Downloading Set Info for %d sets", setDownloadCount))
                .setContentText("This might take a while");
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, builder.build());

        restServiceManager.getCardSetObservable().subscribe(new Subscriber<CardSet>() {

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
            public void onNext(CardSet cardSet) {
                RealmResults<Card> cardResults = Realm.getInstance(CardService.this).allObjects(Card.class).where().equalTo("cardSet.code", cardSet.getCode()).findAll();
                if (cardResults.size() != cardSet.getCount()) {
                    setDownloadCount++;
                    builder.setContentTitle(String.format("Downloading Set Info for " +  cardSet.getCode()));
                    notificationManager.notify(NOTIFICATION_ID, builder.build());
                    restServiceManager.getCardsForSetObservable(cardSet).doOnCompleted(() -> {
                        request(1);
                        setDownloadCount--;
                        if (setDownloadCount != 0) {
                            builder.setContentTitle(String.format("Downloading Set Info for " + cardSet.getCode()));
                            notificationManager.notify(NOTIFICATION_ID, builder.build());
                        } else {
                            notificationManager.cancel(NOTIFICATION_ID);
                        }
                    }).subscribe();
                }
            }
        });

    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
    }
}
