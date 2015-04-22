package org.exallium.tradetracker.app.controller.forms

import android.os.Bundle
import android.view.View
import android.widget.EditText
import butterknife.ButterKnifeViewHolder
import butterknife.bindView
import org.exallium.tradetracker.app.R
import org.exallium.tradetracker.app.model.entities.LineItem

public class LineItemMiscForm(val view: View): LineItemForm() {

    val viewHolder = ViewHolder()
    private inner class ViewHolder : ButterKnifeViewHolder(view) {
        val description: EditText by bindView(R.id.misc_description)
        val value: EditText by bindView(R.id.misc_amount)
    }

    override fun isValid(): Boolean {
        var valid = viewHolder.description.length() != 0
        return valid && viewHolder.value.length() != 0
    }

    override fun populateFields(entity: LineItem) {
        viewHolder.description.setText(entity.description)
        viewHolder.value.setText((entity.value / 100).toString())
    }

    override fun populateEntity(entity: LineItem) {
        entity.description = viewHolder.description.getText().toString()
        entity.value = viewHolder.value.getText().toString().toLong() / 100
    }

}
