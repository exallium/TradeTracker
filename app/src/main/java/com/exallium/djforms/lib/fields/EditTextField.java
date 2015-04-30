package com.exallium.djforms.lib.fields;

import android.content.Context;
import android.view.View;
import android.widget.EditText;
import com.exallium.djforms.lib.DJField;
import kotlin.properties.NULL_VALUE;

public class EditTextField extends DJField<EditText> {

    public EditTextField() {
        super(EditText.class);
    }

    public EditTextField(String name) {
        this(name, NO_STYLE);
    }

    public EditTextField(String name, int styleId) {
        super(EditText.class, name, styleId);
    }

    /**
     * Does nothing, but we don't want abstract stuff
     * @param view The view to initialize
     */
    @Override
    protected void onViewCreated(EditText view) {}

    /**
     * Simple case check if we have anything in our edittext
     * @param view The View to Validate
     * @return True if we're good to go, false otherwise
     */
    @Override
    protected boolean isValid(EditText view) {
        return view.length() != 0;
    }

    @Override
    public Object getValue(EditText view) {
        return view.getText().toString();
    }
}
