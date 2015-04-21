package org.exallium.tradetracker.app.controller.forms

import android.os.Bundle
import android.view.View
import org.exallium.tradetracker.app.model.entities.LineItem

public class LineItemMiscForm(view: View): Form<LineItem>(javaClass<LineItem>()) {
    override fun isValid(): Boolean {
    }

    override fun populateFields(entity: LineItem) {
    }

    override fun populateEntity(entity: LineItem) {
    }

    override fun populateEntityFromBundle(entity: LineItem, bundle: Bundle?) {

    }

}
