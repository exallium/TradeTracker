package org.exallium.tradetracker.app.controller.forms;

import android.widget.EditText;
import com.exallium.djforms.lib.fields.EditTextField;
import org.exallium.tradetracker.app.R;

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
}
