package org.exallium.tradetracker.app.controller.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class CardService extends Service {

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
    }
}
