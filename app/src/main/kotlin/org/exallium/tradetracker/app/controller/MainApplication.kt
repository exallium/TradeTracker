package org.exallium.tradetracker.app.controller

import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import com.orm.SugarApp
import net.danlew.android.joda.JodaTimeAndroid
import org.exallium.tradetracker.app.model.rest.RestManager
import rx.subjects.PublishSubject
import java.lang.ref.WeakReference


public class MainApplication : SugarApp() {

    init {
        instance = WeakReference<MainApplication?>(this)
        restManager = RestManager(this)
    }

    companion object {
        public val onObjectSavedSubject : PublishSubject<Any> = PublishSubject.create()
        public val fragmentRequestedSubject : PublishSubject<Pair<Screen, Bundle?>> = PublishSubject.create()
        public val PREFERENCES : String = "org.exallium.tradetraker.preferences"
        private var instance : WeakReference<MainApplication?> = WeakReference(null)
        private var restManager : RestManager? = null

        public fun getResources() : Resources? {
            return instance.get()?.getResources()
        }

        public fun getRestService() : RestManager? {
            return restManager
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