package com.exallium.djforms.lib.fields;

import android.content.Context;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Filterable;
import com.exallium.djforms.lib.DJField;

public class AutoCompleteTextField<T extends BaseAdapter & Filterable> extends DJField<AutoCompleteTextView> {

    private final T adapter;

    public AutoCompleteTextField(T adapter) {
        super(null, NO_LAYOUT);
        this.adapter = adapter;
    }

    public AutoCompleteTextField(String name, int layoutId, T adapter) {
        super(name, layoutId);
        this.adapter = adapter;
    }

    @Override
    protected AutoCompleteTextView createView(Context context) {
        return new AutoCompleteTextView(context);
    }

    @Override
    protected void onViewCreated(AutoCompleteTextView view){
        view.setAdapter(adapter);
    }

    @Override
    protected boolean isValid(AutoCompleteTextView view) {
        return view.getText().length() != 0;
    }

    @Override
    public Object getValue(AutoCompleteTextView view) {
        return view.getText().toString();
    }

}
