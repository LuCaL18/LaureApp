package it.uniba.dib.sms222327.laureapp.ui.task.viewHolder;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.navigation.Navigation;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import it.uniba.dib.sms222327.laureapp.R;
import it.uniba.dib.sms222327.laureapp.data.RoleUser;
import it.uniba.dib.sms222327.laureapp.data.TaskState;
import it.uniba.dib.sms222327.laureapp.data.model.LoggedInUser;
import it.uniba.dib.sms222327.laureapp.data.model.Task;
import it.uniba.dib.sms222327.laureapp.data.model.Tesi;
import it.uniba.dib.sms222327.laureapp.ui.task.VisualizzaTask;

/**
 * ViewHolder che si occupa della visualizzazione del singolo task
 */
public class TaskListViewHolder {
    private final TextView nomeTasktextView;
    private final TextView scadenzaTasktextView;
    private final TextView tesiTasktextView;
    private final TextView studenteTextView;
    private final View view;
    private final ProgressBar progressBar;

    public TaskListViewHolder(View itemView) {
        view = itemView;
        nomeTasktextView = itemView.findViewById(R.id.nomeTask);
        scadenzaTasktextView = itemView.findViewById(R.id.scadenzaTask);
        tesiTasktextView = itemView.findViewById(R.id.tesiTask);
        studenteTextView = itemView.findViewById(R.id.studenteTask);
        progressBar = itemView.findViewById(R.id.progressBar);
    }

    public void bind(Task task, LoggedInUser user) {
        nomeTasktextView.setText(task.getNomeTask());
        scadenzaTasktextView.setText(task.getScadenza());
        TaskState taskState = task.getStato();
        switch (taskState) {
            case NEW:
                progressBar.setProgress(10);
                break;
            case STARTED:
                progressBar.setProgress(50);
                break;
            case COMPLETED:
                progressBar.setProgress(100);
                break;
            case CLOSED:
                progressBar.setProgress(0);
                break;
        }

        FirebaseFirestore.getInstance().collection("tesi")
                .whereEqualTo("id", task.getTesiId())
                .get().addOnCompleteListener(task1 -> {

                    if (task1.isSuccessful()) {
                        for (DocumentSnapshot doc : task1.getResult().getDocuments()) {
                            Tesi tesi = doc.toObject(Tesi.class);
                            if (tesi != null && tesi.getId().equals(task.getTesiId())) {
                                tesiTasktextView.setText(tesi.getNomeTesi());
                                studenteTextView.setText(tesi.getStudent().getDisplayName());
                            }
                        }
                    } else {
                        Log.e("FirebaseListAdapter", "Listen failed.", task1.getException());
                    }
                });

        if (user.getRole() == RoleUser.PROFESSOR) {
            tesiTasktextView.setVisibility(View.VISIBLE);
            studenteTextView.setVisibility(View.VISIBLE);
        } else if (user.getRole() == RoleUser.STUDENT) {
            tesiTasktextView.setVisibility(View.VISIBLE);
            studenteTextView.setVisibility(View.GONE);
        }

        view.setOnClickListener(v -> {
            view.setSelected(true);

            Bundle bundle = new Bundle();
            bundle.putSerializable(VisualizzaTask.TASK_TO_VISUALIZE, task);
            bundle.putString(VisualizzaTask.STUDENT_TASK, studenteTextView.getText().toString());
            bundle.putString(VisualizzaTask.TESI_TASK, tesiTasktextView.getText().toString());
            Navigation.findNavController(v).navigate(R.id.nav_visualizza_task, bundle);
        });
    }
}
