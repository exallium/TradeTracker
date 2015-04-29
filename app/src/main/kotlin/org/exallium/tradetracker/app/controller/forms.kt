package org.exallium.tradetracker.app.controller.forms

import android.content.Context
import android.widget.ArrayAdapter
import android.widget.EditText
import com.exallium.djforms.lib.DJField
import com.exallium.djforms.lib.DJForm
import com.exallium.djforms.lib.fields.AutoCompleteTextField
import com.exallium.djforms.lib.fields.DateDialogField
import com.exallium.djforms.lib.fields.EditTextField
import com.exallium.djforms.lib.fields.NumberField
import org.exallium.tradetracker.app.controller.adapters.CardAutoCompleteCursorAdapter

public class ValueField(name: String) : EditTextField(name) {
    override public fun getValue(view: EditText): Any {
        return (view.getText().toString().toFloat() * 100).toLong()
    }
}

public class TradeForm(context: Context) : DJForm(context) {
    public val person: AutoCompleteTextField<ArrayAdapter<String>> = AutoCompleteTextField<ArrayAdapter<String>>(null, DJField.NO_LAYOUT, null)
    public val date: DateDialogField = DateDialogField("tradeDate")
}

public class CardForm(context: Context) : DJForm(context) {
    public val card: AutoCompleteTextField<CardAutoCompleteCursorAdapter> = AutoCompleteTextField(null, DJField.NO_LAYOUT, CardAutoCompleteCursorAdapter())
    public val quantity: NumberField = NumberField("quantity")
}

public class CashForm(context: Context) : DJForm(context) {
    public val amount: ValueField = ValueField("value")
}

public class MiscForm(context: Context) : DJForm(context) {
    public val description: EditTextField = EditTextField()
    public val amount: ValueField = ValueField("value")

}
