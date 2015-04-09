package org.exallium.tradetracker.app;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import org.exallium.tradetracker.app.model.rest.RestServiceManager;

public class CardService extends Service {

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

    }
}
