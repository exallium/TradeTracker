package org.exallium.tradetracker.app.controller.adapters

import android.content.Context
import android.database.Cursor
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.orm.query.Condition
import com.orm.query.Select
import org.exallium.tradetracker.app.R
import org.exallium.tradetracker.app.model.entities.Card
import java.util.ArrayList

public class CardAutoCompleteCursorAdapter : AutoCompleteCursorAdapter<Card>(javaClass<Card>(), "name") {
    override fun getItem(position: Int): String? {
        val card = getRecord(position)
        return "%s [%s]".format(card.name, card.cardSet?.code)
    }
}
