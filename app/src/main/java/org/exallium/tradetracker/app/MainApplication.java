package org.exallium.tradetracker.app;

import android.content.Intent;
import com.orm.SugarApp;
import net.danlew.android.joda.JodaTimeAndroid;
import org.exallium.tradetracker.app.model.RealmManager;
import rx.subjects.PublishSubject;
import rx.subjects.Subject;

public class MainApplication extends SugarApp {

    public static final Subject<Object, Object> onObjectSavedSubject = PublishSubject.create();

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

    @Override
    public void onTerminate() {
        RealmManager.INSTANCE.onTerminate();
    }

    public void onObjectSaved(Object object) {
        onObjectSavedSubject.onNext(object);
    }
}
