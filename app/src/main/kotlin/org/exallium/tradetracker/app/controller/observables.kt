package org.exallium.tradetracker.app.model

import com.orm.query.Select
import org.exallium.tradetracker.app.model.entities.CardSet
import org.exallium.tradetracker.app.view.models.CardSetViewModel
import rx.Observable

private val cardSetObservable: Observable<CardSetViewModel> = Observable.create({ subscriber ->
    val cardSets = Select.from(javaClass<CardSet>()).list()
})
