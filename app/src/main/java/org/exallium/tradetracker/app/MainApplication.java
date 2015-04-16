package org.exallium.tradetracker.app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import com.orm.SugarApp;
import net.danlew.android.joda.JodaTimeAndroid;
import rx.subjects.PublishSubject;
import rx.subjects.Subject;

public class MainApplication extends SugarApp {

    public static final Subject<Object, Object> onObjectSavedSubject = PublishSubject.create();
    public static final Subject<Pair<Screen, Bundle>, Pair<Screen, Bundle>> fragmentRequestSubject = PublishSubject.create();

    public static final String PREFERENCES = "org.exallium.tradetracker.prefs";

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

    public void onObjectSaved(Object object) {
        onObjectSavedSubject.onNext(object);
    }
}
