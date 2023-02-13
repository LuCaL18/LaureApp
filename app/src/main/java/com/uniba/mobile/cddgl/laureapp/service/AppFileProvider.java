package com.uniba.mobile.cddgl.laureapp.service;

import androidx.core.content.FileProvider;

import com.uniba.mobile.cddgl.laureapp.R;

public class AppFileProvider extends FileProvider {
    public AppFileProvider() {
        super(R.xml.file_paths);
    }
}