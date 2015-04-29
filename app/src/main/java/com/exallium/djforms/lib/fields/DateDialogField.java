package com.exallium.djforms.lib.fields;

import android.app.DatePickerDialog;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import org.exallium.tradetracker.app.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * DateDialogField is made specifically to show a datepicker dialog
 * when clicked, and fill the information into an EditView.
 */
public class DateDialogField extends EditTextField {

    private static DateFormat dateFormat = new SimpleDateFormat("MMMMM dd, yyyy", Locale.CANADA);

    public DateDialogField() {
        super();
    }

    public DateDialogField(String name) {
        super(name, R.layout.datedialogfield);
    }

    @Override
    protected void onViewCreated(EditText view) {
        super.onViewCreated(view);
        view.setOnClickListener(onClickListener);
    }

    /**
     * Get a DateFormat object describing how you want to input
     * data into the EditText
     * @return a DateFormat instance.
     */
    protected DateFormat getDateFormat() {
        return dateFormat;
    }

    /**
     * Basic validator.  If you want more refined control (say, over a
     * range of dates) it's best to do it in a subclass
     * @param view The view contained.
     * @return true if we're good to go, false otherwise
     */
    @Override
    protected boolean isValid(EditText view) {
        try {
            return super.isValid(view) && dateFormat.parse(view.getText().toString()) != null;
        } catch (ParseException e) {
            return false;
        }
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(final View eView) {
            final Calendar now = Calendar.getInstance();
            new DatePickerDialog(eView.getContext(), new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    now.set(Calendar.YEAR, year);
                    now.set(Calendar.MONTH, monthOfYear + 1);
                    now.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    ((EditText) eView).setText(getDateFormat().format(now.getTime()));
                }
            }, now.get(Calendar.YEAR), now.get(Calendar.MONTH) - 1, now.get(Calendar.DAY_OF_MONTH)).show();
        }
    };

    @Override
    public Object getValue(EditText view) {
        try {
            return getDateFormat().parse((String) super.getValue(view));
        } catch (ParseException e) {
            return null;
        }
    }
}
