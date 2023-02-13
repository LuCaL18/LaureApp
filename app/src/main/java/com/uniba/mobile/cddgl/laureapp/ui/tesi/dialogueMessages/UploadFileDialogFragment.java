package com.uniba.mobile.cddgl.laureapp.ui.tesi.dialogueMessages;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.uniba.mobile.cddgl.laureapp.MainViewModel;
import com.uniba.mobile.cddgl.laureapp.R;
import com.uniba.mobile.cddgl.laureapp.data.model.Tesi;
import com.uniba.mobile.cddgl.laureapp.ui.tesi.VisualizeThesisViewModel;

public class UploadFileDialogFragment extends DialogFragment {

    private static final String THESIS_UPLOAD = "thesis_upload";

    private ActivityResultLauncher<Intent> pickFileLauncher;
    private Tesi tesi;

    public UploadFileDialogFragment() {}

    public UploadFileDialogFragment(Tesi tesi) {
        this.tesi = tesi;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(savedInstanceState != null && savedInstanceState.getSerializable(THESIS_UPLOAD) != null) {
            this.tesi = (Tesi) savedInstanceState.getSerializable(THESIS_UPLOAD);
        }


    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dialog_upload_file, container, false);

        Button uploadButton = view.findViewById(R.id.button_upload_file);
        uploadButton.setEnabled(false);

        EditText title = view.findViewById(R.id.title_file);

        uploadButton.setOnClickListener(view1 -> {
            pickFile();
        });

        pickFileLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri fileUri = result.getData().getData();

                        String mimeType = (getContext().getContentResolver().getType(fileUri)).split("/")[1];
                        // Upload the file
                        uploadFile(title.getText().toString() + '.' + mimeType, fileUri);
                    }
                });


        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                String filename = title.getText().toString();

                if(filename.equals("")) {
                    uploadButton.setEnabled(false);
                    return;
                }

                if(filename.matches("^[a-zA-Z0-9\\-_ ()+@\\[\\]^*áéíóúÁÉÍÓÚÑñ]+$")) {
                    uploadButton.setEnabled(true);

                    return;
                }

                title.setError(getString(R.string.invalid_filename));
                uploadButton.setEnabled(false);
            }
        };

        title.addTextChangedListener(afterTextChangedListener);

        return view;
    }


    private void pickFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");

        pickFileLauncher.launch(intent);
    }


    private void uploadFile(String title, Uri fileUri) {

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference documentRef = storageRef.child("documents/" + tesi.getId() + "/" + title);

        // Upload the file to Firebase Storage
        documentRef.putFile(fileUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        showSaveToast(R.string.file_saved_with_success);

                        VisualizeThesisViewModel thesisViewModel = new ViewModelProvider(requireParentFragment()).get(VisualizeThesisViewModel.class);
                        tesi.getDocuments().add(title);
                        thesisViewModel.getThesis().setValue(tesi);
                        getDialog().dismiss();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // An error occurred during the upload
                        showSaveToast(R.string.error_upload);
                    }
                });
    }

    private void showSaveToast(@StringRes Integer message) {
        if (getContext() != null && getContext().getApplicationContext() != null) {
            Toast.makeText(
                    getContext().getApplicationContext(),
                    message,
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putSerializable(THESIS_UPLOAD, tesi);
    }
}
