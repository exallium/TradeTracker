package org.exallium.tradetracker.app.controller.forms

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.EditText
import com.exallium.AndroidForms.Drain
import com.exallium.AndroidForms.Form
import com.exallium.AndroidForms.ViewSource
import com.exallium.AndroidForms.validators.EditTextNotEmptyValidator
import com.exallium.AndroidForms.validators.Validator
import com.orm.SugarRecord
import com.orm.query.Condition
import com.orm.query.Select
import org.exallium.tradetracker.app.R
import org.exallium.tradetracker.app.controller.BundleConstants
import org.exallium.tradetracker.app.model.entities.*
import org.exallium.tradetracker.app.utils.printForField
import org.exallium.tradetracker.app.utils.toLocalDate
import org.exallium.tradetracker.app.view.widgets.CardFormView
import org.exallium.tradetracker.app.view.widgets.CashFormView
import org.exallium.tradetracker.app.view.widgets.MiscFormView
import org.exallium.tradetracker.app.view.widgets.TradeFormView
import org.joda.time.LocalDate
import java.util
import java.util.Arrays
import java.util.UUID

public fun createTradeForm(view: TradeFormView, model: Trade?) : Form<*, *> {
    return Form.Builder<TradeSource, TradeDrain>(TradeSource(view), TradeDrain(model), TradeMapper()).build()
}

public fun createCardForm(view: CardFormView, model: LineItem?) : Form<*, *> {
    return Form.Builder<CardSource, LineItemDrain>(CardSource(view), LineItemDrain(model), CardMapper())
            .withDrainExtrasMapper(LineItemBundleMapper())
            .build()
}

public fun createCashForm(view: CashFormView, model: LineItem?) : Form<*, *> {
    return Form.Builder<CashSource, LineItemDrain>(CashSource(view), LineItemDrain(model), CashMapper())
            .withDrainExtrasMapper(LineItemBundleMapper())
            .build()
}

public fun createMiscForm(view: MiscFormView, model: LineItem?) : Form<*, *> {
    return Form.Builder<MiscSource, LineItemDrain>(MiscSource(view), LineItemDrain(model), MiscMapper())
            .withDrainExtrasMapper(LineItemBundleMapper())
            .build()
}

private class EditTextIsDate(editText: EditText) : Validator<EditText>(editText) {
    override fun onValidate(editText: EditText?): Boolean {
        try {
            editText!!.getText().toString().toLocalDate()
            return true
        } catch (e: Exception) {
            return false
        }
    }
}

private class EditTextIsNumber(editText: EditText): Validator<EditText>(editText) {
    override fun onValidate(editText: EditText?): Boolean {
        try {
            editText!!.getText().toString().toInt()
            return true
        } catch (e: NumberFormatException) {
            return false
        }
    }
}

private class EditTextIsCardValidator(editText: EditText): Validator<EditText>(editText) {
    override fun onValidate(editText: EditText?): Boolean {
        if (editText != null && editText.length() != 0) {
            val uuid = UUID.nameUUIDFromBytes(editText.getText().toString().toByteArray("UTF-8"));
            val card: Card? = Select.from(javaClass<Card>()).where(Condition.prop("uuid").eq(uuid.toString())).first()
            return card != null
        }
        return false
    }
}

private abstract class RecordDrain<R : Record<R>>(drain: R?) : Drain<R>(drain) {
    override fun onSave(record: R?) {
        record?.save()
    }
}

private class TradeDrain(trade: Trade?) : RecordDrain<Trade>(trade) {
    override fun onCreate(): Trade? {
        return Trade()
    }
}

private class LineItemDrain(lineItem: LineItem?) : RecordDrain<LineItem>(lineItem) {
    override fun onCreate(): LineItem? {
        return LineItem()
    }
}

private class TradeSource(view: TradeFormView) : ViewSource<TradeFormView>(view) {
    override fun onSourceCreated(): MutableList<out Validator<out Any?>>? {
        val tradeForm = getSource()

        val people = Select.from(javaClass<Person>()).list().map { person -> person.name }
        if (people.size() != 0)
            tradeForm.person.setAdapter(ArrayAdapter(tradeForm.getContext(), R.layout.support_simple_spinner_dropdown_item, people))

        return Arrays.asList(EditTextNotEmptyValidator(tradeForm.person), EditTextIsDate(tradeForm.date))
    }
}

private class CardSource(view: CardFormView) : ViewSource<CardFormView>(view) {
    override fun onSourceCreated(): MutableList<out Validator<out Any?>>? {
        val cardForm = getSource()
        return Arrays.asList(EditTextIsCardValidator(cardForm.uuid), EditTextIsNumber(cardForm.quantity))
    }
}

private class CashSource(view: CashFormView) : ViewSource<CashFormView>(view) {
    override fun onSourceCreated(): MutableList<out Validator<out Any?>>? {
        val cashForm = getSource()
        return Arrays.asList(EditTextIsNumber(cashForm.amount))
    }
}

private class MiscSource(view: MiscFormView) : ViewSource<MiscFormView>(view) {
    override fun onSourceCreated(): MutableList<out Validator<out Any?>>? {
        val miscForm = getSource()
        return Arrays.asList(EditTextNotEmptyValidator(miscForm.description), EditTextIsNumber(miscForm.amount))
    }
}

private class TradeMapper : Form.Mapper<TradeSource, TradeDrain> {
    override fun mapForward(source: TradeSource?, drain: TradeDrain?) {
        val tradeForm: TradeFormView? = source?.getSource()
        val trade: Trade? = drain?.getDrain()
        val name : String? = tradeForm?.person?.getText().toString()

        // Create a person if we need to
        var person = Select.from(javaClass<Person>()).where(Condition.prop("name").eq(name)).first()
        if (person == null && name != null) {
            person = Person()
            person.name = name
            person.save()
        }

        trade?.person = person
        trade?.tradeDate = tradeForm?.date?.getText().toString().toLocalDate()?.toDate()?:LocalDate.now().toDate()

    }

    override fun mapBackward(drain: TradeDrain?, source: TradeSource?) {
        val trade: Trade? = drain?.getDrain()
        val tradeForm: TradeFormView? = source?.getSource()
        tradeForm?.person?.setText(trade?.person?.name)
        if (trade?.tradeDate != null)
            tradeForm?.date?.setText(LocalDate(trade?.tradeDate).printForField())
    }
}

private class CardMapper : Form.Mapper<CardSource, LineItemDrain> {
    override fun mapBackward(p0: LineItemDrain?, p1: CardSource?) {
        val view = p1!!.getSource()
        val model = p0!!.getDrain()

        view.uuid.setText("%s [%s]".format(model.card?.name, model.card?.cardSet?.code))
        view.quantity.setText("" + model.quantity)
    }

    override fun mapForward(p0: CardSource?, p1: LineItemDrain?) {
        val view = p0!!.getSource()
        val model = p1!!.getDrain()

        val uuid = UUID.nameUUIDFromBytes(view.uuid.toString().toByteArray("UTF-8")).toString()
        val card : Card? = Select.from(javaClass<Card>()).where(Condition.prop("uuid").eq(uuid)).first()

        val cachedValueItem = Select.from(javaClass<LineItem>())
                .where(Condition.prop("card").eq(card?.getId()),
                        Condition.prop("lastUpdated").lt(LocalDate.now().minusDays(1).toDate().getTime())).first()

        model.description = null
        model.card = card
        model.quantity = view.quantity.getText().toString().toLong()
        model.lastUpdated = if (cachedValueItem != null) cachedValueItem.lastUpdated else LocalDate.now().minusYears(10).toDate()
        model.value = if (cachedValueItem != null) cachedValueItem.value else 0

    }

}

private class CashMapper : Form.Mapper<CashSource, LineItemDrain> {
    override fun mapForward(p0: CashSource?, p1: LineItemDrain?) {
        val view = p0!!.getSource()
        val model = p1!!.getDrain()

        val amount = view.amount.getText().toString().toLong()

        model.description = view.getContext().getResources().getString(R.string.cash_description, amount)
        model.card = null
        model.quantity = 1
        model.lastUpdated = LocalDate.now().toDate()
        model.value = amount * 100L
    }

    override fun mapBackward(p0: LineItemDrain?, p1: CashSource?) {
        val view = p1!!.getSource()
        val model = p0!!.getDrain()

        view.amount.setText("" + model.value / 100)
    }

}

private class MiscMapper : Form.Mapper<MiscSource, LineItemDrain> {
    override fun mapBackward(p0: LineItemDrain?, p1: MiscSource?) {
        val view = p1!!.getSource()
        val model = p0!!.getDrain()

        view.amount.setText("" + model.value / 100)
        view.description.setText(model.description)
    }

    override fun mapForward(p0: MiscSource?, p1: LineItemDrain?) {
        val view = p0!!.getSource()
        val model = p1!!.getDrain()

        model.description = view.description.getText().toString()
        model.card = null
        model.quantity = 1
        model.lastUpdated = LocalDate.now().toDate()
        model.value = view.amount.getText().toString().toLong() * 100
    }

}

private class LineItemBundleMapper : Form.Mapper<Bundle, LineItemDrain> {
    override fun mapForward(p0: Bundle?, p1: LineItemDrain?) {
        val lineItem = p1!!.getDrain()
        lineItem.direction = p0?.getBoolean(BundleConstants.LINE_ITEM_DIRECTION)?:false
        lineItem.trade = null
        val tradeId = p0?.getLong(BundleConstants.TRADE_ID)?:BundleConstants.NEW_OBJECT
        if (tradeId != BundleConstants.NEW_OBJECT) {
            val trade = SugarRecord.findById(javaClass<Trade>(), tradeId)
            lineItem.trade = trade
        }
    }

    override fun mapBackward(p0: LineItemDrain?, p1: Bundle?) {
        throw UnsupportedOperationException()
    }

}
