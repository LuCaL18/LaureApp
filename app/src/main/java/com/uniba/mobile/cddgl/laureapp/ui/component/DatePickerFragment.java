package com.uniba.mobile.cddgl.laureapp.ui.component;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.EditText;

import androidx.fragment.app.DialogFragment;

import com.uniba.mobile.cddgl.laureapp.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    private static final int FRAGMENT_REGISTRATION = R.layout.fragment_registration;
    private static final int FRAGMENT_RICEVIMENTO = R.layout.fragment_ricevimento;
    private static final int FRAGMENT_NEWTASK = R.layout.fragment_new_task;
    private final int layout;

    public DatePickerFragment(int contentLayoutId) {
        layout = contentLayoutId;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        final Calendar c = Calendar.getInstance();
        c.set(year, month, day);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String dateString = dateFormat.format(c.getTime());

        EditText dateField = null;

        if (getParentFragment() != null && getParentFragment().getView() != null) {
            switch (layout) {
                case FRAGMENT_REGISTRATION:
                    dateField = getParentFragment().getView().findViewById(R.id.birthDay);
                    break;
                case FRAGMENT_RICEVIMENTO:
                    dateField = getParentFragment().getView().findViewById(R.id.dataE);
                    break;
                case FRAGMENT_NEWTASK:
                    dateField = getParentFragment().getView().findViewById(R.id.scadenza);
                    break;
                default:
                    break;
            }
        }

        if (dateField != null) {
            dateField.setError(null);
            dateField.setText(dateString);
        }
    }
}
