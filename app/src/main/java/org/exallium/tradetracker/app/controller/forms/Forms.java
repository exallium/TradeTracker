package org.exallium.tradetracker.app.controller.forms;

import android.content.Context;
import com.exallium.djforms.lib.DJForm;
import com.exallium.djforms.lib.fields.AutoCompleteTextField;
import com.exallium.djforms.lib.fields.DateDialogField;
import com.exallium.djforms.lib.fields.EditTextField;
import com.exallium.djforms.lib.fields.NumberField;
import org.exallium.tradetracker.app.controller.adapters.CardAutoCompleteCursorAdapter;
import org.exallium.tradetracker.app.controller.adapters.PersonCursorAdapter;

public class Forms {

    public static class TradeForm extends DJForm {

        public AutoCompleteTextField<PersonCursorAdapter> person = new AutoCompleteTextField<>(new PersonCursorAdapter());
        public DateDialogField tradeDate = new DateDialogField();

        public TradeForm(Context context) {
            super(context);
        }
    }

    public static class CardForm extends DJForm {

        public AutoCompleteTextField<CardAutoCompleteCursorAdapter> card = new AutoCompleteTextField<>(new CardAutoCompleteCursorAdapter());
        public NumberField quantity = new NumberField();

        public CardForm(Context context) {
            super(context);
        }
    }

    public static class CashForm extends DJForm {

        public Fields.ValueField value = new Fields.ValueField();

        public CashForm(Context context) {
            super(context);
        }
    }

    public static class MiscForm extends DJForm {

        public EditTextField description = new EditTextField();
        public Fields.ValueField value = new Fields.ValueField();

        public MiscForm(Context context) {
            super(context);
        }
    }
}

