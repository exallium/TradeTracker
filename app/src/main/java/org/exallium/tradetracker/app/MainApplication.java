package org.exallium.tradetracker.app;

import android.app.Application;

public class MainApplication extends Application {

    private static MainApplication instance;

    {
        instance = this;
    }

    public static MainApplication getInstance() {
        return instance;
    }

}
