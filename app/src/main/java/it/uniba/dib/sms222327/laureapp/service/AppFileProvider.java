package it.uniba.dib.sms222327.laureapp.service;

import androidx.core.content.FileProvider;

import it.uniba.dib.sms222327.laureapp.R;

/**
 * La classe AppFileProvider estende la classe FileProvider,
 * che è una classe di utilità fornita da Android per consentire l'accesso sicuro ai file all'interno di un'app.
 */
public class AppFileProvider extends FileProvider {
    public AppFileProvider() {
        super(R.xml.file_paths);
    }
}