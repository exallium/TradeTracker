package org.exallium.tradetracker.app.controller

import android.content.Intent
import android.os.Bundle
import com.orm.SugarApp
import net.danlew.android.joda.JodaTimeAndroid
import rx.subjects.PublishSubject
import java.lang.ref.WeakReference


public class MainApplication : SugarApp() {

    init {
        instance = WeakReference<MainApplication?>(this)
    }

    companion object {
        public val onObjectSavedSubject : PublishSubject<Any> = PublishSubject.create()
        public val fragmentRequestedSubject : PublishSubject<Pair<Screen, Bundle?>> = PublishSubject.create()
        public val PREFERENCES : String = "org.exallium.tradetraker.preferences"

        public var instance : WeakReference<MainApplication?> = WeakReference(null)
            private set
    }

    public override fun onCreate() {
        super.onCreate()
        JodaTimeAndroid.init(this)

        var i = Intent()
        i.setClass(this, javaClass<CardService>())
        startService(i)
    }
}