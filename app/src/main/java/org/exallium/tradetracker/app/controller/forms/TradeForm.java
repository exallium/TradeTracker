package org.exallium.tradetracker.app.controller.forms;

import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import butterknife.InjectView;
import com.google.common.collect.Iterables;
import com.orm.query.Select;
import org.exallium.tradetracker.app.R;
import org.exallium.tradetracker.app.model.entities.Person;
import org.exallium.tradetracker.app.model.entities.Trade;
import rx.Observable;

import java.util.ArrayList;
import java.util.List;

public class TradeForm extends Form<Trade> {

    @InjectView(R.id.trade_person) AutoCompleteTextView tradePerson;
    @InjectView(R.id.trade_date) EditText tradeDate;

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
        if (tradePerson.getText().length() == 0) {
            return false;
        }

        return tradeDate.getText().length() != 0;

    }

    @Override
    protected Trade populateEntity(@Nullable Trade entity) {
        return null;
    }

    @Override
    protected void populateFields(@Nullable Trade entity) {

    }
}
