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
            this(name, R.layout.numberfield);
        }

        public ValueField(String name, int layoutId) {
            super(name, layoutId);
        }

        @Override
        public Object getValue(EditText view) {
            return Math.round(Double.parseDouble((String) super.getValue(view)) * 100);
        }
    }
}
