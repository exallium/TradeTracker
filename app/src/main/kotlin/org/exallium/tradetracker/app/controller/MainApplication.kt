package org.exallium.tradetracker.app.controller

import android.content.Intent
import android.os.Bundle
import com.orm.SugarApp
import net.danlew.android.joda.JodaTimeAndroid
import rx.subjects.PublishSubject

public object MainApplication : SugarApp() {
    public val onObjectSavedSubject : PublishSubject<Any> = PublishSubject.create()
    public val fragmentRequestedSubject : PublishSubject<Pair<Screen, Bundle?>> = PublishSubject.create()
    public val PREFERENCES : String = "org.exallium.tradetraker.preferences"

    public override fun onCreate() {
        super.onCreate()
        JodaTimeAndroid.init(this)

        var i = Intent()
        i.setClass(this, javaClass<CardService>())
        startService(i)
    }
}