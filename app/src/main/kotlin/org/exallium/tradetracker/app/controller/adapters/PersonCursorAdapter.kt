package org.exallium.tradetracker.app.controller.adapters

import org.exallium.tradetracker.app.model.entities.Person

public class PersonCursorAdapter : AutoCompleteCursorAdapter<Person>(javaClass<Person>(), "name") {
    override public fun getItem(position: Int): String? {
        return getRecord(position).name
    }
}
