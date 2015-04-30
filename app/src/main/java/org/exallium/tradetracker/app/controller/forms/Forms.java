package org.exallium.tradetracker.app.controller.forms;

import android.content.Context;
import com.exallium.djforms.lib.DJForm;
import com.exallium.djforms.lib.fields.AutoCompleteTextField;
import com.exallium.djforms.lib.fields.DateDialogField;
import com.exallium.djforms.lib.fields.EditTextField;
import com.exallium.djforms.lib.fields.NumberField;
import org.exallium.tradetracker.app.R;
import org.exallium.tradetracker.app.controller.adapters.CardAutoCompleteCursorAdapter;
import org.exallium.tradetracker.app.controller.adapters.PersonCursorAdapter;

public class Forms {

    public static class TradeForm extends DJForm {

        public AutoCompleteTextField<PersonCursorAdapter> person = new AutoCompleteTextField<>(R.style.TradePersonAutoComplete, new PersonCursorAdapter());
        public DateDialogField tradeDate = new DateDialogField(null, R.style.TradeDatePickerField);

        public TradeForm(Context context) {
            super(context);
        }
    }

    public static class CardForm extends DJForm {

        public AutoCompleteTextField<CardAutoCompleteCursorAdapter> card = new AutoCompleteTextField<>(R.style.LineItemCardAutoComplete, new CardAutoCompleteCursorAdapter());
        public NumberField quantity = new NumberField(null, R.style.LineItemCardQuantityField);

        public CardForm(Context context) {
            super(context);
        }
    }

    public static class CashForm extends DJForm {

        public Fields.ValueField value = new Fields.ValueField(null, R.style.LineItemCashAmountField);

        public CashForm(Context context) {
            super(context);
        }
    }

    public static class MiscForm extends DJForm {

        public EditTextField description = new EditTextField(null, R.style.LineItemMiscDescriptionField);
        public Fields.ValueField value = new Fields.ValueField(null, R.style.LineItemMiscValueField);

        public MiscForm(Context context) {
            super(context);
        }
    }
}

