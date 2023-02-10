package com.uniba.mobile.cddgl.laureapp.ui.tesi.adapters;

import android.app.DownloadManager;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.uniba.mobile.cddgl.laureapp.R;
import com.uniba.mobile.cddgl.laureapp.data.DownloadedFile;
import com.uniba.mobile.cddgl.laureapp.ui.tesi.VisualizeThesisViewModel;
import com.uniba.mobile.cddgl.laureapp.ui.tesi.viewHolder.DocumentViewHolder;

import java.util.List;

public class DocumentAdapter extends RecyclerView.Adapter<DocumentViewHolder> {
    private final List<String> documents;
    private final java.lang.String idThesis;
    private final VisualizeThesisViewModel thesisModel;

    public DocumentAdapter(List<String> documents, java.lang.String idThesis, VisualizeThesisViewModel model) {
        this.documents = documents;
        this.thesisModel = model;
        this.idThesis = idThesis;
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
    }

    private void downloadFile(String filename) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference fileRef = storageRef.child("documents/" + idThesis + "/" + filename);

        fileRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if(task.isSuccessful()) {
                    Uri uri = task.getResult();

                    DownloadManager.Request request = new DownloadManager.Request(uri);
                    request.setTitle(filename.split("\\.")[0]);
                    request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, filename);
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                    request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,filename);

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
}
