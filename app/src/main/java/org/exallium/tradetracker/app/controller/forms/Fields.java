package org.exallium.tradetracker.app.controller.forms;

import android.widget.EditText;
import com.exallium.djforms.lib.fields.EditTextField;

public class Fields {
    public static class ValueField extends EditTextField {
        @Override
        public Object getValue(EditText view) {
            return Math.round(Double.parseDouble((String) super.getValue(view)) * 100);
        }
    }
}
