package com.uniba.mobile.cddgl.laureapp.ui.tesi.dialogs;

import static android.app.Activity.RESULT_OK;
import static com.uniba.mobile.cddgl.laureapp.MainActivity.REQUEST_WRITE_STORAGE_PERMISSION;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.uniba.mobile.cddgl.laureapp.R;
import com.uniba.mobile.cddgl.laureapp.data.model.Tesi;
import com.uniba.mobile.cddgl.laureapp.util.ShareContent;

import java.io.IOException;
import java.io.OutputStream;

public class QRCodeDialogFragment extends DialogFragment {

    private ActivityResultLauncher<Intent> saveImageLauncher;
    private Bitmap qrCode;
    private ShareContent shareContent;
    private final Tesi tesi;

    public QRCodeDialogFragment(Tesi tesi) {
        this.tesi = tesi;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dialog_qrcode, container, false);
        ImageView imageViewQRCode = view.findViewById(R.id.qr_code_image);

        shareContent = new ShareContent(getContext());
        qrCode = shareContent.generateQRCode(getString(R.string.data_shared_data,
                tesi.getNomeTesi(), tesi.getDescrizione(), tesi.getRelatore().getDisplayName(),
                tesi.getTempistiche() + " " + getString(R.string.weeks),
                String.valueOf(tesi.getMediaVoto()), tesi.getEsami(), tesi.getSkill(), tesi.getNote()));

        imageViewQRCode.setImageBitmap(qrCode);


        Button share = view.findViewById(R.id.share_button);
        Button save = view.findViewById(R.id.save_button);

        share.setOnClickListener(view1 -> {
            startActivity(Intent.createChooser(shareContent.shareJPG("/qrcode.jpg", qrCode), "Share QR CODE"));
        });

        save.setOnClickListener(view1 -> {
            try {
                saveQRCodeImage(qrCode, "qrcode.jpg");
            } catch (IOException e) {
                e.printStackTrace();
                showSaveToast(R.string.unable_saved_file);
            } catch (SecurityException e) {
                e.printStackTrace();
                showSaveToast(R.string.file_saved_unauthorised);
            }
        });

        saveImageLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                Uri uri = result.getData().getData();
                if (uri != null) {
                    try {
                        OutputStream outputStream = getContext().getContentResolver().openOutputStream(uri);
                        qrCode.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                        outputStream.flush();
                        Toast.makeText(requireActivity(), getString(R.string.image_saved_in_gallery), Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Toast.makeText(requireActivity(), getString(R.string.cannot_image_saved_in_gallery), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    private void showSaveToast(@StringRes Integer message) {
        if (getContext() != null && getContext().getApplicationContext() != null) {
            Toast.makeText(
                    getContext().getApplicationContext(),
                    message,
                    Toast.LENGTH_LONG).show();
        }
    }

    private void saveQRCodeImage(Bitmap bitmap, String filename) throws IOException {

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
            Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/jpeg");
            intent.putExtra(Intent.EXTRA_TITLE, filename);
            saveImageLauncher.launch(intent);

            return;
        }

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            new AlertDialog.Builder(getContext())
                    .setTitle(getString(R.string.storage_permission))
                    .setMessage(getString(R.string.request_permission_storage_gallery))
                    .setPositiveButton(getString(R.string.next), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            requestPermissions(
                                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                    REQUEST_WRITE_STORAGE_PERMISSION);
                        }
                    })
                    .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(getContext(), getString(R.string.write_external_permission_declined), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .show();
        } else {
            // Permission is already granted, save the image to the external storage
            shareContent.saveImageOnDevice(filename, bitmap);
            showSaveToast(R.string.file_saved_with_success);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_WRITE_STORAGE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted, save the image to the external storage
                try {
                    saveQRCodeImage(qrCode, "/qrcode.jpg");
                    showSaveToast(R.string.file_saved_with_success);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                // Permission is denied, show a message
                Toast.makeText(getContext(), getString(R.string.app_need_permission_to_save_qr_code), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
