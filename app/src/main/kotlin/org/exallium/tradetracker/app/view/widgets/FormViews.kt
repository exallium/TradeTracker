package org.exallium.tradetracker.app.view.widgets

import android.content.Context
import android.view.LayoutInflater
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.LinearLayout
import butterknife.bindView
import org.exallium.tradetracker.app.R

public abstract class FormView : LinearLayout {

    constructor (context: Context, layoutId: Int) : super(context) {
        initView(layoutId)
    }

    public fun initView(layoutId: Int) {
        LayoutInflater.from(getContext()).inflate(layoutId, this, true)
    }
}

public class TradeFormView : FormView {

    public val person: AutoCompleteTextView by bindView(R.id.trade_person)
    public val date: EditText by bindView(R.id.trade_date)

    constructor (context: Context) : super(context, R.layout.form_trade) {}

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

