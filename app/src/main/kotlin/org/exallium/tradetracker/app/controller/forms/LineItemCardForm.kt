package org.exallium.tradetracker.app.controller.forms

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import butterknife.ButterKnifeViewHolder
import butterknife.bindView
import com.orm.query.Condition
import com.orm.query.Select
import org.exallium.tradetracker.app.R
import org.exallium.tradetracker.app.controller.BundleConstants
import org.exallium.tradetracker.app.controller.adapters.CardAutoCompleteCursorAdapter
import org.exallium.tradetracker.app.model.entities.Card
import org.exallium.tradetracker.app.model.entities.LineItem
import org.exallium.tradetracker.app.model.entities.Trade
import org.joda.time.LocalDate
import java.util.UUID

public class LineItemCardForm(val view: View) : LineItemForm() {

    inner class ViewHolder : ButterKnifeViewHolder(itemView = view) {
        val cardUUID : AutoCompleteTextView by bindView(R.id.card_uuid)
    }

    val viewHolder = ViewHolder()
    var card: Card? = null

    init {
        viewHolder.cardUUID.setAdapter(CardAutoCompleteCursorAdapter())
    }

    override fun isValid(): Boolean {
        var valid = viewHolder.cardUUID.getText().length() != 0
        if (valid) {
            var uuid = UUID.nameUUIDFromBytes(viewHolder.cardUUID.getText().toString().toByteArray("UTF-8")).toString()
            card = Select.from(javaClass<Card>()).where(Condition.prop("uuid").eq(uuid)).first()
            valid = card != null
        }

        return valid
    }

    override fun populateFields(entity: LineItem) {
        if (entity.card != null) {
            viewHolder.cardUUID.setText("%s [%s]".format(entity.card?.name, entity.card?.cardSet?.code))
        }
    }

    override fun populateEntity(entity: LineItem) {
        isValid()
        entity.card = card
        entity.lastUpdated = LocalDate.now().toDate()
    }
}
