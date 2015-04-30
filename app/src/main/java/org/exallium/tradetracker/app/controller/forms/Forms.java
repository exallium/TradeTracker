package org.exallium.tradetracker.app.controller.forms;

import android.content.Context;
import com.exallium.djforms.lib.DJForm;
import com.exallium.djforms.lib.fields.DateDialogField;
import com.exallium.djforms.lib.fields.EditTextField;
import com.exallium.djforms.lib.fields.NumberField;
import org.exallium.tradetracker.app.R;
import org.exallium.tradetracker.app.controller.MainActivity;
import org.exallium.tradetracker.app.controller.MainApplication;
import org.exallium.tradetracker.app.controller.adapters.CardAutoCompleteCursorAdapter;
import org.exallium.tradetracker.app.controller.adapters.PersonCursorAdapter;
import org.exallium.tradetracker.app.model.entities.Card;
import org.exallium.tradetracker.app.model.entities.LineItem;
import org.exallium.tradetracker.app.model.entities.Person;

public class Forms {

    public static class TradeForm extends DJForm {

        public Fields.AutoCompleteRecordField<Person, PersonCursorAdapter> person = new Fields.AutoCompleteRecordField<>(R.style.TradePersonAutoComplete, new PersonCursorAdapter());
        public DateDialogField tradeDate = new DateDialogField(null, R.style.TradeDatePickerField);

        public TradeForm(Context context) {
            super(context);
        }
    }

    public static class CardForm extends DJForm {

        public Fields.AutoCompleteRecordField<Card, CardAutoCompleteCursorAdapter> card = new Fields.AutoCompleteRecordField<>(R.style.LineItemCardAutoComplete, new CardAutoCompleteCursorAdapter());
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

        @Override
        protected void postSave(Object model) {
            if (model instanceof LineItem)
                ((LineItem) model).setDescription(
                        MainApplication.Companion.getResources().getString(
                                R.string.cash_description, String.format("%.2f", ((LineItem) model).getValue() / 100f)
                        ));
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

