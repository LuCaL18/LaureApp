package com.uniba.mobile.cddgl.laureapp.ui.task;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.uniba.mobile.cddgl.laureapp.R;
import com.uniba.mobile.cddgl.laureapp.data.model.Task;
import com.uniba.mobile.cddgl.laureapp.ui.component.DatePickerFragment;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

public class VisualizzaTask extends Fragment {

    private BottomNavigationView navBar;
    private FirebaseFirestore db;
    private TextView nometaskTextView,descrizioneTextView,statoTextView,scadenzaTextView;
    private OnFragmentInteractionListener listener;

    public VisualizzaTask() {
        //
    }

    public static VisualizzaTask newInstance() {
        return new VisualizzaTask();
    }

    @Override
    public void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView (@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.visualizza_task,container,false);
        navBar = getActivity().findViewById(R.id.nav_view);
        navBar.setVisibility(View.INVISIBLE);
        nometaskTextView = view.findViewById(R.id.nometask1);
        descrizioneTextView = view.findViewById(R.id.descrizione2);
        statoTextView = view.findViewById(R.id.stato2);
        scadenzaTextView = view.findViewById(R.id.scadenza4);
        return view;
    }

    public interface OnFragmentInteractionListener {
        void onAddTaskClicked(Task task);
    }

}
