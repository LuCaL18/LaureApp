package it.uniba.dib.sms222327.laureapp.ui.component;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import it.uniba.dib.sms222327.laureapp.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Classe che estende DialogFragment e implementa l'interfaccia DatePickerDialog.OnDateSetListener
 * utilizzata per mostrare il dialog per la selezione di una data
 */
public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    private static final int FRAGMENT_REGISTRATION = R.layout.fragment_registration;
    private static final int FRAGMENT_NEWTASK = R.layout.fragment_new_task;
    private static final int FRAGMENT_EDIT_PROFILE = R.layout.fragment_edit_profile;
    private final int layout;

    public DatePickerFragment(int contentLayoutId) {
        layout = contentLayoutId;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
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
                case FRAGMENT_NEWTASK:
                dateField = getParentFragment().getView().findViewById(R.id.scadenza);
                break;
            case FRAGMENT_EDIT_PROFILE:
                    dateField = getParentFragment().getView().findViewById(R.id.birth_date_edit_text);
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
