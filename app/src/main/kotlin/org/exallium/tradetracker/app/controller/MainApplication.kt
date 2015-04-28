package org.exallium.tradetracker.app.controller

import android.content.Intent
import android.content.res.Resources
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
        private var instance : WeakReference<MainApplication?> = WeakReference(null)

        public fun getResources() : Resources? {
            return instance.get()?.getResources()
        }
    }

    public override fun onCreate() {
        super.onCreate()
        JodaTimeAndroid.init(this)

        var i = Intent()
        i.setClass(this, javaClass<CardService>())
        startService(i)
    }
}