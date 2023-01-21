package com.uniba.mobile.cddgl.laureapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

/**
 * Fragment per la visualizzazione del logo.
 */
public class LogoFragment extends Fragment {

//    private ProgressBar pgb;
//    private CountDownTimer mCountDownTimer;

    public LogoFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_logo, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        NavController navController = NavHostFragment.findNavController(LogoFragment.this);
        navController.navigate(R.id.action_logoFragment_to_startFragment);
    }

    //    private void progressBarManager() {
////        mCountDownTimer = new CountDownTimer(1000, 500) {
////
////            int i = 0;
////
////            @Override
////            public void onTick(long millisUntilFinished) {
//////                Log.v("Log_tag", "Tick of Progress" + i + millisUntilFinished);
//////                i++;
//////                pgb.setProgress((int) i * 100 / (1000 / 500));
////
////            }
////
////            @Override
////            public void onFinish() {
//////                i++;
//////                pgb.setProgress(100);
////
////            }
////        };
////        mCountDownTimer.start();
//
//    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
//        mCountDownTimer.cancel();
    }
}