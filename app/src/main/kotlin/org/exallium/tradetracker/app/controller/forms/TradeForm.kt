package org.exallium.tradetracker.app.controller.forms

import android.app.DatePickerDialog
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.DatePicker
import android.widget.EditText
import butterknife.ButterKnifeViewHolder
import butterknife.bindView
import com.orm.query.Condition
import com.orm.query.Select
import org.exallium.tradetracker.app.R
import org.exallium.tradetracker.app.model.entities.Person
import org.exallium.tradetracker.app.model.entities.Trade
import org.exallium.tradetracker.app.utils.printForField
import org.exallium.tradetracker.app.utils.toLocalDate
import org.joda.time.LocalDate
import rx.android.view.ViewObservable
import java.util.Calendar

public class TradeForm(formView : View) : Form<Trade>(entityClass = javaClass<Trade>()) {

    class ViewHolder(view : View) : ButterKnifeViewHolder(itemView = view) {
        val tradePerson : AutoCompleteTextView by bindView(R.id.trade_person)
        val tradeDate : EditText by bindView(R.id.trade_date)
    }

    private val viewHolder = ViewHolder(formView)
    private var cachedDate : LocalDate = LocalDate.now()

    init {
        if (Select.from(javaClass<Person>()).count() != 0L) {
            val people = Select.from(javaClass<Person>()).list().map { person -> person?.name }
            viewHolder.tradePerson.setAdapter(ArrayAdapter<String>(viewHolder.tradePerson.getContext(), R.layout.support_simple_spinner_dropdown_item, people))
        }

        ViewObservable.clicks(viewHolder.tradeDate).subscribe {
            DatePickerDialog(viewHolder.tradeDate.getContext(), { datePicker: DatePicker?, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                val calendar = Calendar.getInstance()
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, monthOfYear)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                cachedDate = LocalDate.fromCalendarFields(calendar)
                viewHolder.tradeDate.setText(cachedDate.printForField())
            }, cachedDate.getYear(), cachedDate.getMonthOfYear() - 1, cachedDate.getDayOfMonth()).show()
        }

    }

    override fun isValid(): Boolean {
        var valid = viewHolder.tradePerson.getText()?.length() != 0

        var date : LocalDate? = null
        try {
            date = viewHolder.tradeDate.getText()?.toString()?.toLocalDate()
        } catch (e : IllegalArgumentException) {
            // Invalid date
        }

        date?.let({
            cachedDate = date as LocalDate
        })

        valid = date != null && valid

        return valid
    }

    override fun populateFields(entity: Trade) {
        cachedDate = LocalDate.fromDateFields(entity.tradeDate)
        viewHolder.tradeDate.setText(cachedDate.printForField())
        viewHolder.tradePerson.setText(entity.person?.name)
    }

    override fun populateEntity(entity: Trade) {
        entity.isTemporary = !isValid()
        entity.person = getPerson()
        entity.tradeDate = cachedDate.toDate()
    }

    private fun getPerson(): Person? {
        var name = viewHolder.tradePerson.getText()?.toString()?:""
        if (name.length() == 0)
            return null

        var person = Select.from(javaClass<Person>()).where(Condition.prop("name").eq(name)).first()
        if (person == null) {
            person = Person()
            person.name = name
            person.save()
        }
        return person
    }


}
