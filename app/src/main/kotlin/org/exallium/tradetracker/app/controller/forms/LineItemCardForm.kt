package org.exallium.tradetracker.app.controller.forms

import android.os.Bundle
import android.view.View
import android.widget.AutoCompleteTextView
import butterknife.ButterKnifeViewHolder
import butterknife.bindView
import com.orm.query.Condition
import com.orm.query.Select
import org.exallium.tradetracker.app.R
import org.exallium.tradetracker.app.controller.BundleConstants
import org.exallium.tradetracker.app.model.entities.Card
import org.exallium.tradetracker.app.model.entities.LineItem
import org.exallium.tradetracker.app.model.entities.Trade
import org.joda.time.LocalDate
import java.util.UUID

public class LineItemCardForm(val view: View) : Form<LineItem>(entityClass = javaClass<LineItem>()) {

    inner class ViewHolder : ButterKnifeViewHolder(itemView = view) {
        val cardUUID : AutoCompleteTextView by bindView(R.id.card_uuid)
    }

    val viewHolder = ViewHolder()
    var card: Card? = null

    override fun isValid(): Boolean {
        var valid = viewHolder.cardUUID.getText().length() != 0
        if (valid) {
            var uuid = UUID.nameUUIDFromBytes(viewHolder.cardUUID.getText().toString().toByteArray("UTF-8"))
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

    override fun populateEntityFromBundle(entity: LineItem, bundle: Bundle?) {
        entity.direction = bundle?.getBoolean(BundleConstants.LINE_ITEM_DIRECTION, false)?:false
        val tradeId = bundle?.getLong(BundleConstants.TRADE_ID, BundleConstants.NEW_OBJECT)?:BundleConstants.NEW_OBJECT

        if (tradeId != BundleConstants.NEW_OBJECT) {
            val trade = Select.from(javaClass<Trade>()).where(Condition.prop("id").eq(tradeId)).first()
            entity.trade = trade
        }
    }

}
