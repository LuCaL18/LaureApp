package com.uniba.mobile.cddgl.laureapp.ui.task;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.uniba.mobile.cddgl.laureapp.R;
import com.uniba.mobile.cddgl.laureapp.data.model.Task;

/**
 * Fragment che si occupa della gestione della visualizzazione di un task
 * esistente selezionato dalla lista dei task visibili dall'utente
 */

public class VisualizzaTask extends Fragment {

    private static final String CLASS_ID = "VisualizzaTask";

    public static final String TASK_TO_VISUALIZE = "task_to_visualize";
    public static final String STUDENT_TASK = "student_task";
    public static final String TESI_TASK = "tesi_task";

    private BottomNavigationView navBar;

    private Task task;
    private String studentTask;
    private String tesiTask;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        Bundle bundle = getArguments();

        if (bundle != null && bundle.getSerializable(TASK_TO_VISUALIZE) != null) {
            task = (Task) bundle.getSerializable(TASK_TO_VISUALIZE);
            studentTask = bundle.getString(STUDENT_TASK);
            tesiTask = bundle.getString(TESI_TASK);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.visualizza_task, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if(task != null) {
            TextView title = view.findViewById(R.id.nometask2);
            title.setText(task.getNomeTask());

            setupCardView(view, R.id.tv_descrizione3, task.getDescrizione());
            setupCardView(view, R.id.tv_stato3, task.getStato().toString());
            setupCardView(view, R.id.tv_scadenza3, task.getScadenza());

            if (studentTask != null) {
                setupCardView(view, R.id.tv_studente_task, studentTask);
            }

            if (tesiTask != null) {
                setupCardView(view, R.id.tv_tesi, tesiTask);
            }

            ImageButton editStatoTask = view.findViewById(R.id.edit_task_state);
            editStatoTask.setOnClickListener(v -> showEditStatoDialog());
        }

    }

    private void setupCardView(View root, int textViewId, String text) {
        TextView textView = root.findViewById(textViewId);
        textView.setText(text);
    }

    private void showEditStatoDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_modifica_stato, null);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        ListView listViewStato = dialogView.findViewById(R.id.listview_stato);
        String[] stati = getContext().getResources().getStringArray(R.array.stato_array);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_list_item_1, stati);
        listViewStato.setAdapter(adapter);

        listViewStato.setOnItemClickListener((parent, view, position, id) -> {
            String nuovoStato = stati[position];

            if (getView() != null) {
                TextView statoTask = getView().findViewById(R.id.tv_stato3);
                String currentState = (String) statoTask.getText();

                if (!nuovoStato.equals(currentState)) {
                    statoTask.setText(nuovoStato);
                    updateTaskStato(nuovoStato);
                }

            }

            dialog.dismiss();
        });

        dialog.show();
    }

    private void updateTaskStato(String nuovoStato) {
        FirebaseFirestore.getInstance().collection("task")
                .whereEqualTo("id", task.getId()).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                        String documentId = documentSnapshot.getId();

                        FirebaseFirestore.getInstance().collection("task")
                                .document(documentId)
                                .update("stato", nuovoStato)
                                .addOnSuccessListener(aVoid -> Log.d(CLASS_ID, "Updated task status"))
                                .addOnFailureListener(e -> Log.e(CLASS_ID, "Error while updating task status", e));
                    }
                })
                .addOnFailureListener(e -> Log.e(CLASS_ID, "Errore durante il recupero del task", e));
    }

    @Override
    public void onResume() {
        super.onResume();

        /* Rimozione della navBar dal layout */
        navBar = requireActivity().findViewById(R.id.nav_view);
        navBar.setVisibility(View.GONE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        navBar.setVisibility(View.VISIBLE);
        navBar = null;
    }
}