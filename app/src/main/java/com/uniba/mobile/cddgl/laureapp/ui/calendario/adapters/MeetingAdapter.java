package com.uniba.mobile.cddgl.laureapp.ui.calendario.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.uniba.mobile.cddgl.laureapp.R;
import com.uniba.mobile.cddgl.laureapp.data.model.Ricevimento;

import java.util.List;

import io.reactivex.rxjava3.annotations.NonNull;

public class MeetingAdapter extends RecyclerView.Adapter<MeetingAdapter.ViewHolder> {

    private List<Ricevimento> meetingList;

    public MeetingAdapter(List<Ricevimento> meetingList) {
        this.meetingList = meetingList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.meeting, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int itemPosition = holder.getAdapterPosition();

        Ricevimento meeting = meetingList.get(itemPosition);
        holder.titoloM.setText(meeting.getTitolo());
        holder.TesiM.setText(meeting.getNomeTesi());
        holder.OraM.setText(meeting.getTimeString());
        holder.RiepilogoM.setText(meeting.getRiepilogo());

        // Rimuovi tutte le viste precedenti dal layout del Task
        holder.TaskM.removeAllViews();

        // Aggiungi gli elementi del Task al layout
        List<String> taskList = meeting.getTask();
        for (String task : taskList) {
            TextView textView = new TextView(holder.itemView.getContext());
            textView.setText(task);
            holder.TaskM.addView(textView);
        }

        // Gestisci il clic sul tasto di eliminazione
        holder.deleteButton.setOnClickListener(v -> {
            // Ottieni l'istanza di FirebaseFirestore
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            // Definisci l'ID del documento da eliminare
            String documentId = meeting.getRicevimentoId();

            // Ottieni il riferimento al documento specifico
            db.collection("ricevimento")
                    .document(documentId)
                    .delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            // Rimuovi l'elemento dalla lista dei meeting
                            meetingList.remove(itemPosition);

                            // Notifica all'adattatore che un elemento è stato rimosso
                            notifyItemRemoved(itemPosition);
                        }
                    });
        });
    }

    @Override
    public int getItemCount() {
        return meetingList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titoloM;
        TextView TesiM;
        TextView OraM;
        TextView RiepilogoM;
        LinearLayout TaskM;
        ImageButton deleteButton;

        public ViewHolder(View itemView) {
            super(itemView);
            titoloM = itemView.<TextView>findViewById(R.id.titoloM);
            TesiM = itemView.findViewById(R.id.TesiM);
            OraM = itemView.findViewById(R.id.OraM);
            RiepilogoM = itemView.findViewById(R.id.RiepilogoM);
            TaskM = itemView.findViewById(R.id.TaskM);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}
