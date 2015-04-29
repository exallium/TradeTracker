package com.exallium.djforms.lib;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

/**
 * The Building Blocks for DJForms.
 */
public abstract class DJField<V extends View> {

    public static final int NO_LAYOUT = -1;

    protected int layoutId = NO_LAYOUT;
    private String name;

    public DJField() {
        this(null, NO_LAYOUT);
    }

    public DJField(String name, int layoutId) {
        this.layoutId = layoutId;
        this.name = name;
    }

    private V cachedView;

    /**
     * Creates an instance of V, sets it up, and returns it.
     * Initialization should happen in onViewCreated, not here.
     * @param context The context for which to generate the view for
     * @return The View for this field
     */
    protected abstract V createView(Context context);

    /**
     * Initialize the view with listeners, etc.
     * @param view The view to initialize
     */
    protected abstract void onViewCreated(V view);

    @SuppressWarnings({"unchecked"})
    private V createFieldView(Context context) {
        if (layoutId != NO_LAYOUT) {
            V view = (V) LayoutInflater.from(context).inflate(layoutId, null);
            onViewCreated(view);
            return view;
        }
        V view = createView(context);
        onViewCreated(view);
        return view;
    }

    /**
     * Allows for custom field validation in subclasses
     * @param view The View to Validate
     * @return true if valid, false otherwise
     */
    protected abstract boolean isValid(V view);

    public final V getFieldView(Context context) {
        if (cachedView == null) {
            cachedView = createFieldView(context);
        }
        return cachedView;
    }

    /**
     * Validate the data present in the field wrapped in this
     * DJField
     * @return true if valid, otherwise false
     */
    public final boolean isFieldValid() {
        return isValid(cachedView);
    }

    /**
     * Gets the name which maps this field to the proper location
     * in the model we write to.
     * @return The name passed to constructor
     */
    public String getName() { return name; }

    /**
     * Sets the field name from the form if and only if the field
     * doesn't already have a name (it's null)
     */
    void setFieldName(String name) { this.name = name; }

    /**
     * See getValue()
     * @return see getValue()
     */
    Object getFieldValue() {
        return getValue(cachedView);
    }

    /**
     * Computes the final value for population
     * @return The final value.
     */
    public abstract Object getValue(V view);
}
