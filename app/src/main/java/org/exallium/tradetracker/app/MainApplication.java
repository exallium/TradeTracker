package org.exallium.tradetracker.app;

import android.app.Application;
import android.content.Intent;
import net.danlew.android.joda.JodaTimeAndroid;

public class MainApplication extends Application {

    private static MainApplication instance;

    {
        instance = this;
    }

    public static MainApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        JodaTimeAndroid.init(this);

        Intent i = new Intent();
        i.setClass(this, CardService.class);
        startService(i);
    }
}
