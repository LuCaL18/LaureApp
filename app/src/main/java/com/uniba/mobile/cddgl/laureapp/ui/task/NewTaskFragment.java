package com.uniba.mobile.cddgl.laureapp.ui.task;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.uniba.mobile.cddgl.laureapp.R;
import com.uniba.mobile.cddgl.laureapp.data.model.Task;

public class NewTaskFragment extends Fragment {

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
        return view;
    }

    public interface OnFragmentInteractionListener {
        void onAddTaskClicked(Task task);
    }

}