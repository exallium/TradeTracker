package org.exallium.tradetracker.app.controller.forms

import android.os.Bundle
import com.orm.SugarRecord
import com.orm.query.Condition
import com.orm.query.Select
import org.exallium.tradetracker.app.controller.BundleConstants
import org.exallium.tradetracker.app.model.entities.LineItem
import org.exallium.tradetracker.app.model.entities.Trade

public abstract class LineItemForm : Form<LineItem>(javaClass<LineItem>()) {
    override fun populateEntityFromBundle(entity: LineItem, bundle: Bundle?) {
        entity.direction = bundle?.getBoolean(BundleConstants.LINE_ITEM_DIRECTION, false)?:false
        val tradeId = bundle?.getLong(BundleConstants.TRADE_ID, BundleConstants.NEW_OBJECT)?:BundleConstants.NEW_OBJECT

        if (tradeId != BundleConstants.NEW_OBJECT) {
            val trade = SugarRecord.findById(javaClass<Trade>(), tradeId)
            entity.trade = trade
        }
    }
}
