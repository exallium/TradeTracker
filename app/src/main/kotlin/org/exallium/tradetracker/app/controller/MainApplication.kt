package org.exallium.tradetracker.app.controller

import android.content.Intent
import android.os.Bundle
import com.orm.SugarApp
import net.danlew.android.joda.JodaTimeAndroid
import rx.subjects.PublishSubject
import java.lang.ref.WeakReference


public class MainApplication : SugarApp() {

    init {
        instance = WeakReference(this)
    }

    object Manager {
        public fun getInstance(): MainApplication? {
            return instance.get()
        }
    }

    companion object {
        public val onObjectSavedSubject : PublishSubject<Any> = PublishSubject.create()
        public val fragmentRequestedSubject : PublishSubject<Pair<Screen, Bundle?>> = PublishSubject.create()
        public val PREFERENCES : String = "org.exallium.tradetraker.preferences"
        private var instance = WeakReference<MainApplication?>(null)
    }

    public override fun onCreate() {
        super.onCreate()
        JodaTimeAndroid.init(this)

        var i = Intent()
        i.setClass(this, javaClass<CardService>())
        startService(i)
    }
}