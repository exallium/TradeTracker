package org.exallium.tradetracker.app.controller.adapters

import org.exallium.tradetracker.app.model.entities.Person

public class PersonCursorAdapter : AutoCompleteCursorAdapter<Person>(javaClass<Person>(), "name") {
    override public fun stringify(record: Person): String {
        return record.name
    }
}
