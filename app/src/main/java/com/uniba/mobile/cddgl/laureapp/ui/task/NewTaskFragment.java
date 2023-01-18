package com.uniba.mobile.cddgl.laureapp.ui.task;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.uniba.mobile.cddgl.laureapp.R;
import com.uniba.mobile.cddgl.laureapp.data.model.Task;
import com.uniba.mobile.cddgl.laureapp.ui.component.DatePickerFragment;

import java.util.HashMap;
import java.util.Map;

public class NewTaskFragment extends Fragment {

    private FirebaseFirestore db;
    private EditText nometaskEditText,statoEditText,descrizioneEditText,scadenzaEditText;
    private Button addtaskButton;
    private OnFragmentInteractionListener listener;

    public NewTaskFragment() {
        //
    }

    public static NewTaskFragment newInstance() {
        return new NewTaskFragment();
    }

    @Override
    public void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView (@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_task,container,false);
        db = FirebaseFirestore.getInstance();
        nometaskEditText = view.findViewById(R.id.nometask);
        statoEditText = view.findViewById(R.id.stato);
        descrizioneEditText = view.findViewById(R.id.descrizione);
        scadenzaEditText = view.findViewById(R.id.scadenza);
        addtaskButton = view.findViewById(R.id.addtask_button);

        scadenzaEditText.setOnClickListener(v -> {
            DialogFragment datePicker = new DatePickerFragment(R.layout.fragment_new_task);
            datePicker.show(getParentFragmentManager(), "date picker");
            addtaskButton.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick (View view) {
                    String nometask = nometaskEditText.getText().toString();
                    String stato = statoEditText.getText().toString();
                    String descrizione = descrizioneEditText.getText().toString();
                    String scadenza = scadenzaEditText.getText().toString();
                    Map<String,Object> listaTask = new HashMap<>();
                    listaTask.put("nomeTask",nometask);
                    listaTask.put("stato",stato);
                    listaTask.put("descrizione",descrizione);
                    listaTask.put("scadenza",scadenza);
                    db.collection("task")
                            .add(listaTask)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Toast.makeText(getContext(), "successfull", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getContext(), "failed", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            });
        });
        return view;
    }

    public interface OnFragmentInteractionListener {
        void onAddTaskClicked(Task task);
    }

}