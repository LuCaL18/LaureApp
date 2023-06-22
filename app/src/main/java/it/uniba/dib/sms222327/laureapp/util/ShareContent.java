package it.uniba.dib.sms222327.laureapp.util;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.core.content.FileProvider;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import it.uniba.dib.sms222327.laureapp.MainActivity;
import it.uniba.dib.sms222327.laureapp.R;
import it.uniba.dib.sms222327.laureapp.data.model.Tesi;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLConnection;
import java.util.Date;

/**
 * Classe per condividere dati
 */
public class ShareContent {

    public static final String AUTHORITY = "it.uniba.dib.sms222327.laureapp.service.AppFileProvider";
    private final Context context;

    public ShareContent(Context context) {
        this.context = context;
    }

    public Bitmap generateQRCode(String data) {
        // Use the ZXing library to generate the QR code.
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(data.toString(), BarcodeFormat.QR_CODE, 800, 800);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();

            return barcodeEncoder.createBitmap(bitMatrix);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Intent shareJPG(String filename, Bitmap image) {
        try {
            File file = new File(context.getExternalFilesDir(null), filename);
            FileOutputStream fOut = new FileOutputStream(file);

            image.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
            fOut.flush();
            fOut.close();
            file.setReadable(true, false);

            final Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Uri photoURI = FileProvider.getUriForFile(context, ShareContent.AUTHORITY, file);

            intent.putExtra(Intent.EXTRA_STREAM, photoURI);
            intent.setType("image/jpeg");

            return intent;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Intent shareThesisData(Tesi tesi) {
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.thesis_data));
        emailIntent.putExtra(Intent.EXTRA_TITLE, context.getString(R.string.title).toUpperCase());
        emailIntent.putExtra(Intent.EXTRA_TEXT, context.getString(R.string.data_shared_data,
                tesi.getNomeTesi(), tesi.getDescrizione(), tesi.getRelatore().getDisplayName(),
                tesi.getTempistiche() + " " + context.getString(R.string.weeks),
                String.valueOf(tesi.getMediaVoto()), tesi.getEsami(), tesi.getSkill(), tesi.getNote()));
        return emailIntent;
    }

    public boolean saveImageOnDevice(String filename, Bitmap bitmap) throws IOException {

        String filenameDevice = filename.split("\\.")[0] + "_" + new Date().getTime() + "." + filename.split("\\.")[1];

        File qrCodeFile = new File(MainActivity.getExternalStorageDirectory(Environment.DIRECTORY_PICTURES), filenameDevice );

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "QRCode");

        values.put(MediaStore.Images.Media.DISPLAY_NAME, filenameDevice);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.Images.Media.DATE_ADDED, new Date().toString());
        values.put(MediaStore.Images.Media.DATA, qrCodeFile.getAbsolutePath());

        ContentResolver resolver = context.getContentResolver();
        Uri imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, imageUri));

        FileOutputStream outputStream = new FileOutputStream(qrCodeFile);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);

        outputStream.close();

        return true;
    }

    public Intent viewFileDownloaded(String filename) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);

        Uri fileUri = FileProvider.getUriForFile(
                context,
                ShareContent.AUTHORITY,
                new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), filename)
        );

        String mimeType = URLConnection.guessContentTypeFromName(filename);
        intent.setDataAndType(fileUri, mimeType);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        return intent;
    }

    public Intent viewImageOnline(Uri fileUri) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);

        String mimeType = "image/*";
        intent.setDataAndType(fileUri, mimeType);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        return intent;
    }
}
