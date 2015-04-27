package org.exallium.tradetracker.app.controller.forms

import android.os.Bundle
import android.view.View
import android.widget.EditText
import butterknife.ButterKnifeViewHolder
import butterknife.bindView
import com.orm.query.Condition
import com.orm.query.Select
import org.exallium.tradetracker.app.R
import org.exallium.tradetracker.app.controller.BundleConstants
import org.exallium.tradetracker.app.controller.MainApplication
import org.exallium.tradetracker.app.model.entities.LineItem
import org.exallium.tradetracker.app.model.entities.Trade

public class LineItemCashForm(val view: View): LineItemForm() {

    inner class ViewHolder : ButterKnifeViewHolder(itemView = view) {
        val cashAmount : EditText by bindView(R.id.cash_amount)
    }

    private val viewHolder = ViewHolder()

    override fun isValid(): Boolean {
        return viewHolder.cashAmount.getText().length() != 0
    }

    override fun populateFields(entity: LineItem) {
        viewHolder.cashAmount.setText((entity.value/100).toString())
    }

    override fun populateEntity(entity: LineItem) {
        val valStr = viewHolder.cashAmount.getText().toString()
        if (valStr.length() != 0)
            entity.value = valStr.toLong() * 100
        entity.description = MainApplication.instance.get()?.getResources()?.getString(
                R.string.cash_description, viewHolder.cashAmount.getText().toString())
    }

}
