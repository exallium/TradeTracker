package org.exallium.tradetracker.app.model.entities

import com.orm.SugarRecord
import org.exallium.tradetracker.app.MainApplication

public open class Record<T> : SugarRecord<T>() {

    override fun save() {
        super.save()
        // We should move to an Observer Model, where when we save a new Model, we emit it via an observer, and
        // it gets sent down the chain.  The Adapter sees this new item, finds the appropriate position in the view
        // to add it, and notifyAdded at the appropriate location, or notifyModified if this is just a modification
        // (The item already exists in the list)
        MainApplication.onObjectSavedSubject.onNext(this)
    }
}
