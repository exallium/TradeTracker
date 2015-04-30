package org.exallium.tradetracker.app.controller.forms;

import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import com.exallium.djforms.lib.fields.AutoCompleteTextField;
import com.exallium.djforms.lib.fields.EditTextField;
import org.exallium.tradetracker.app.R;
import org.exallium.tradetracker.app.controller.adapters.AutoCompleteCursorAdapter;
import org.exallium.tradetracker.app.model.entities.Record;

public class Fields {
    public static class ValueField extends EditTextField {

        public ValueField() {
            this(null);
        }

        public ValueField(String name) {
            this(name, R.style.Form_NumberField);
        }

        public ValueField(String name, int styleId) {
            super(name, styleId);
        }

        @Override
        public Object getValue(EditText view) {
            return Math.round(Double.parseDouble((String) super.getValue(view)) * 100);
        }

        @Override
        public void setValue(EditText view, Object data) {
            super.setValue(view, String.format("%0.2f", ((Long) data) / 100f));
        }
    }

    public static class AutoCompleteRecordField<E extends Record<E>, T extends AutoCompleteCursorAdapter<E>> extends AutoCompleteTextField<T> {

        private T adapter;

        public AutoCompleteRecordField(T adapter) {
            super(adapter);
            this.adapter = adapter;
        }

        public AutoCompleteRecordField(int styleId, T adapter) {
            super(styleId, adapter);
            this.adapter = adapter;
        }

        public AutoCompleteRecordField(String name, int styleId, T adapter) {
            super(name, styleId, adapter);
            this.adapter = adapter;
        }

        @Override
        public Object getValue(AutoCompleteTextView view) {
            return adapter.getRecord(view.getText().toString());
        }

        @Override
        public void setValue(AutoCompleteTextView view, Object data) {
            view.setText(adapter.stringify((E) data));
        }
    }
}
