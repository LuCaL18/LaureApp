package com.uniba.mobile.cddgl.laureapp.ui.task;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.uniba.mobile.cddgl.laureapp.R;
import com.uniba.mobile.cddgl.laureapp.data.model.Task;

import java.util.ArrayList;

public class NewTaskFragment extends Fragment {

    DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabase;
    EditText nometaskEditText,statoEditText,descrizioneEditText;
    Button addtaskButton;
    private OnFragmentInteractionListener listener;

    public NewTaskFragment() {

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
        firebaseDatabase = FirebaseDatabase.getInstance("https://laureapp-b5243-default-rtdb.firebaseio.com/");
        nometaskEditText = view.findViewById(R.id.nometask);
        statoEditText = view.findViewById(R.id.stato);
        descrizioneEditText = view.findViewById(R.id.descrizione);
        addtaskButton = view.findViewById(R.id.addtask_button);

        addtaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                String nometask = nometaskEditText.getText().toString();
                String stato = statoEditText.getText().toString();
                String descrizione = descrizioneEditText.getText().toString();
                Task task = new Task(nometask,stato,descrizione);
                listener.onAddTaskClicked(task);
            }
        });
        addtaskButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick (View view) {
                Task task = new Task();
                // List<Task> listaTask = new ArrayList<>();
                task.setNometask(nometaskEditText.getText().toString());
                task.setStato(statoEditText.getText().toString());
                task.setDescrizione(descrizioneEditText.getText().toString());
                databaseReference = firebaseDatabase.getReference("task");
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        databaseReference.setValue(task);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }
        });
        return view;
    }

    public interface OnFragmentInteractionListener {
        void onAddTaskClicked(Task task);
    }

}