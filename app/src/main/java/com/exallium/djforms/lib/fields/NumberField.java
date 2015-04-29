package com.exallium.djforms.lib.fields;

import android.widget.EditText;
import org.exallium.tradetracker.app.R;

public class NumberField extends EditTextField {

    public NumberField() {
        this(null);
    }

    public NumberField(String name) {
        this(name, R.layout.numberfield);
    }

    public NumberField(String name, int layoutId) {
        super(name, layoutId);
    }


    @Override
    public Object getValue(EditText view) {
        return Long.parseLong((String) super.getValue(view));
    }
}
