package org.exallium.tradetracker.app.controller.forms;

import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import butterknife.InjectView;
import com.google.common.collect.Iterables;
import com.orm.query.Condition;
import com.orm.query.Select;
import org.exallium.tradetracker.app.R;
import org.exallium.tradetracker.app.model.entities.Person;
import org.exallium.tradetracker.app.model.entities.Trade;
import org.exallium.tradetracker.app.utils.date.DateFormat;
import org.joda.time.LocalDate;
import rx.Observable;

public class TradeForm extends Form<Trade> {

    @InjectView(R.id.trade_person) AutoCompleteTextView tradePerson;
    @InjectView(R.id.trade_date) EditText tradeDate;

    private LocalDate cachedTradeDate = LocalDate.now();

    public TradeForm(Class<Trade> entityClass, View formView) {
        super(entityClass, formView);
        if (Select.from(Person.class).count() != 0) {
            Iterable<String> personNames = Observable.from(Select.from(Person.class).list()).map(person -> person.name).toBlocking().toIterable();
            String [] namesString = Iterables.toArray(personNames, String.class);
            tradePerson.setAdapter(new ArrayAdapter<>(formView.getContext(), R.layout.support_simple_spinner_dropdown_item, namesString));
        }
    }

    @Override
    public boolean isValid() {
        boolean valid = true;
        if (tradePerson.getText().length() == 0) {
            valid = false;
        }

        LocalDate newDate = DateFormat.fromString(tradeDate.getText().toString());
        if (newDate != null) {
            cachedTradeDate = newDate;
        } else {
            valid = false;
        }
        return valid;
    }

    @Override
    protected void populateEntity(@Nullable Trade entity) {
        if (entity != null && isValid()) {
            String name = tradePerson.getText().toString();
            Person person = Select.from(Person.class).where(Condition.prop("name").eq(name)).first();
            if (person == null) {
                person = new Person();
                person.name = name;
                person.save();
            }
            entity.person = person;
            entity.tradeDate = cachedTradeDate.toDate();
        } else if (entity != null) {
            entity.isTemporary = true;
            entity.tradeDate = cachedTradeDate.toDate();
        }
    }

    @Override
    protected void populateFields(@Nullable Trade entity) {
        if (entity != null) {
            cachedTradeDate = LocalDate.fromDateFields(entity.tradeDate);
            tradeDate.setText(DateFormat.toField(cachedTradeDate));
            tradePerson.setText(entity.person.name);
        }

    }
}
