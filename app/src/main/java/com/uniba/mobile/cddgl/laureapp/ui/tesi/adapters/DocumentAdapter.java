package com.uniba.mobile.cddgl.laureapp.ui.tesi.adapters;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.uniba.mobile.cddgl.laureapp.R;
import com.uniba.mobile.cddgl.laureapp.data.DownloadedFile;
import com.uniba.mobile.cddgl.laureapp.ui.tesi.VisualizeTesiFragment;
import com.uniba.mobile.cddgl.laureapp.ui.tesi.viewModels.VisualizeThesisViewModel;
import com.uniba.mobile.cddgl.laureapp.ui.tesi.viewHolder.DocumentViewHolder;

import java.util.List;

/**
 * Adapter che si occupa della visualizzazione della lista dei document della tesi
 */
public class DocumentAdapter extends RecyclerView.Adapter<DocumentViewHolder> {
    private List<String> documents;
    private final java.lang.String idThesis;
    private final VisualizeThesisViewModel thesisModel;
    private final boolean permissionDelete;
    private final Context context;
    private VisualizeTesiFragment tesiFragment;

    public DocumentAdapter(Context context, List<String> documents, java.lang.String idThesis, VisualizeThesisViewModel model) {
        this.context = context;
        this.documents = documents;
        this.thesisModel = model;
        this.idThesis = idThesis;
        this.permissionDelete = false;
    }

    public DocumentAdapter(VisualizeTesiFragment requiredFragment, List<String> documents, java.lang.String idThesis, VisualizeThesisViewModel model, boolean permissionDelete) {
        this.context = requiredFragment.getContext();
        this.documents = documents;
        this.thesisModel = model;
        this.idThesis = idThesis;
        this.permissionDelete = permissionDelete;
        this.tesiFragment = requiredFragment;
    }

    @NonNull
    @Override
    public DocumentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_document, parent, false);
        return new DocumentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DocumentViewHolder holder, int position) {
        String document = documents.get(position);
        holder.getDocumentFileName().setText(document);

        holder.getDownloadButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadFile(document);
            }
        });

        if (permissionDelete) {
            ImageView deleteImageView = holder.getDeleteButton();
            deleteImageView.setVisibility(View.VISIBLE);

            deleteImageView.setOnClickListener(view -> {

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(context.getString(R.string.title_dialog_delete_document));
                builder.setMessage(context.getString(R.string.message_dialog_delete_document, document));
                builder.setPositiveButton(context.getString(R.string.yes_text), (dialog, which) -> deleteDocument(document));
                builder.setNegativeButton(context.getString(R.string.no_text), null);
                builder.create().show();
            });
        }

    }

    private void downloadFile(String filename) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference fileRef = storageRef.child("documents/" + idThesis + "/" + filename);

        fileRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri uri = task.getResult();

                    DownloadManager.Request request = new DownloadManager.Request(uri);
                    request.setTitle(filename.split("\\.")[0]);
                    request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, filename);
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                    request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, filename);

                    thesisModel.getRequestDocument().setValue(new DownloadedFile(filename, request));
                    return;
                }

                thesisModel.getError().setValue(R.string.unable_downloaded_file);
            }
        });
    }


    @Override
    public int getItemCount() {
        return documents.size();
    }

    public void setDocuments(List<String> documents) {
        this.documents = documents;
        notifyDataSetChanged();
    }

    private void deleteDocument(String filename) {
        FirebaseStorage.getInstance().getReference().child("documents/" + idThesis + "/" + filename)
                .delete()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        tesiFragment.removeDocument(filename);
                    }
                });
    }

}
