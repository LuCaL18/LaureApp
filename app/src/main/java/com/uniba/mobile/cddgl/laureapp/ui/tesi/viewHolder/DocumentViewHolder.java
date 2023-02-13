package com.uniba.mobile.cddgl.laureapp.ui.tesi.viewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.uniba.mobile.cddgl.laureapp.R;

public class DocumentViewHolder extends RecyclerView.ViewHolder {
    private final TextView documentFileName;
    private final ImageView downloadButton;
    private final ImageView deleteButton;


    public DocumentViewHolder(@NonNull View itemView) {
        super(itemView);
        documentFileName = itemView.findViewById(R.id.filename_document_text_view);
        downloadButton = itemView.findViewById(R.id.download_image_view);
        deleteButton = itemView.findViewById(R.id.delete_image_view);

    }

    public TextView getDocumentFileName() {
        return documentFileName;
    }

    public ImageView getDownloadButton() {
        return downloadButton;
    }

    public ImageView getDeleteButton() {
        return deleteButton;
    }
}
