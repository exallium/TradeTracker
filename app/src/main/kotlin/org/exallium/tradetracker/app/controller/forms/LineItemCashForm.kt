package org.exallium.tradetracker.app.controller.forms

import android.view.View
import android.widget.EditText
import butterknife.ButterKnifeViewHolder
import butterknife.bindView
import org.exallium.tradetracker.app.R
import org.exallium.tradetracker.app.model.entities.LineItem

public class LineItemCashForm(val view: View): Form<LineItem>(javaClass<LineItem>()) {

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
    }

    // Pop from bundle

}
