package com.uniba.mobile.cddgl.laureapp.ui.component;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.navigation.fragment.NavHostFragment;

import com.uniba.mobile.cddgl.laureapp.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

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

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String dateString = dateFormat.format(c.getTime());

        EditText dateField = null;
        String root = (String) ((NavHostFragment)getParentFragment()).getNavController().getCurrentDestination().getLabel();
        if (root == "fragment_sign_in") {
            dateField = getParentFragment().getView().findViewById(R.id.birthDay);
        } else if (root == "new_task") {
            dateField = getParentFragment().getView().findViewById(R.id.scadenza);
        }
        dateField.setError(null);
        dateField.setText(dateString);
    }
}
