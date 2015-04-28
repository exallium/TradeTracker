package org.exallium.tradetracker.app.view.widgets

import android.app.DatePickerDialog
import android.content.Context
import android.view.LayoutInflater
import android.widget.AutoCompleteTextView
import android.widget.DatePicker
import android.widget.EditText
import android.widget.LinearLayout
import butterknife.bindView
import org.exallium.tradetracker.app.R
import org.exallium.tradetracker.app.utils.printForField
import org.joda.time.LocalDate
import rx.android.view.ViewObservable
import java.util.Calendar

public abstract class FormView : LinearLayout {

    constructor (context: Context, layoutId: Int) : super(context) {
        initView(layoutId)
    }

    public fun initView(layoutId: Int) {
        LayoutInflater.from(getContext()).inflate(layoutId, this, true)
        setOrientation(LinearLayout.VERTICAL)
        postInit()
    }

    protected open fun postInit() {}
}

public class TradeFormView : FormView {

    public val person: AutoCompleteTextView by bindView(R.id.trade_person)
    public val date: EditText by bindView(R.id.trade_date)

    constructor (context: Context) : super(context, R.layout.form_trade) {}

    override fun postInit() {
        val d = findViewById(R.id.trade_date) as EditText
        val now = LocalDate.now()
        d.setText(now.printForField())
        ViewObservable.clicks(d).subscribe {
            DatePickerDialog(d.getContext(), { datePicker: DatePicker?, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                val calendar = Calendar.getInstance()
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, monthOfYear)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                d.setText(LocalDate.fromCalendarFields(calendar).minusMonths(1).printForField())
            }, now.getYear(), now.getMonthOfYear(), now.getDayOfMonth()).show()
        }
    }

}

public class CardFormView : FormView {

    public val uuid: AutoCompleteTextView by bindView(R.id.card_uuid)
    public val quantity: EditText by bindView(R.id.card_quantity)

    constructor (context: Context) : super(context, R.layout.form_card) {}
}

public class CashFormView : FormView {

    public val amount: EditText by bindView(R.id.cash_amount)

    constructor (context: Context) : super(context, R.layout.form_cash) {}
}

public class MiscFormView : FormView {

    public val description: EditText by bindView(R.id.misc_description)
    public val amount: EditText by bindView(R.id.misc_amount)

    constructor (context: Context) : super(context, R.layout.form_misc) {}
}

