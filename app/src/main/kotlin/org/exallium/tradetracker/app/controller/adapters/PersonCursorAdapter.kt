package org.exallium.tradetracker.app.controller.adapters

import com.orm.query.Condition
import com.orm.query.Select
import org.exallium.tradetracker.app.model.entities.Person

public class PersonCursorAdapter : AutoCompleteCursorAdapter<Person>(javaClass<Person>(), "name") {
    override fun stringify(record: Person): String {
        return record.name
    }

    override fun getRecord(string: String): Person {
        var person = Select.from(clazz).where(Condition.prop("name").eq(string)).first()
        if (person == null) {
            person = Person()
            person.name = string
            person.save()
        }
        return person
    }
}
