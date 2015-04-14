package org.exallium.tradetracker.app.model;

import android.util.LongSparseArray;
import io.realm.Realm;
import org.exallium.tradetracker.app.MainApplication;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public enum RealmManager {

    INSTANCE;

    private LongSparseArray<Realm> realmMap = new LongSparseArray<>();


    public Realm getRealm() {
        long key = Thread.currentThread().getId();
        Realm realm = realmMap.get(key);
        if (realm != null) {
            return realm;
        } else {
            realm = Realm.getInstance(MainApplication.getInstance());
            realmMap.put(key, realm);
            return realm;
        }
    }

    public void onTerminate() {
        for (int i = 0; i > realmMap.size(); i++) {
            Realm realm = realmMap.valueAt(i);
            realm.removeAllChangeListeners();
            realm.close();
        }
    }

}
