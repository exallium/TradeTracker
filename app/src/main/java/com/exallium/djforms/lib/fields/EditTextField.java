package com.exallium.djforms.lib.fields;

import android.content.Context;
import android.view.View;
import android.widget.EditText;
import com.exallium.djforms.lib.DJField;

public class EditTextField extends DJField<EditText> {

    public EditTextField() {
        super();
    }

    public EditTextField(String name) {
        this(name, NO_LAYOUT);
    }

    public EditTextField(String name, int layoutId) {
        super(name, layoutId);
    }

    /**
     * Creates a new EditText object.  Only called if NO_LAYOUT is supplied to super
     * @param context The context for which to generate the view for
     * @return The new instance
     */
    @Override
    protected EditText createView(Context context) {
        return new EditText(context);
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
