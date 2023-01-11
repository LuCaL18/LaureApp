package com.uniba.mobile.cddgl.laureapp.ui.task;

import android.util.Patterns;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.uniba.mobile.cddgl.laureapp.R;
import com.uniba.mobile.cddgl.laureapp.data.RoleUser;
import com.uniba.mobile.cddgl.laureapp.data.model.LoggedInUser;
import com.uniba.mobile.cddgl.laureapp.data.model.NewTaskIn;
import com.uniba.mobile.cddgl.laureapp.ui.login.LoginFormState;
import com.uniba.mobile.cddgl.laureapp.ui.login.LoginResult;
import com.uniba.mobile.cddgl.laureapp.ui.login.registration.RegisterFormState;

import org.w3c.dom.Document;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NewTaskViewModel extends ViewModel {

    private final MutableLiveData<NewTaskFormState> newtaskFormState = new MutableLiveData<>();
    private final MutableLiveData<NewTaskResult> newtaskResult = new MutableLiveData<>();
    private final MutableLiveData<NewTaskIn> newtask = new MutableLiveData<>();
    private final FirebaseFirestore db;
    private NewTaskIn task;

    NewTaskViewModel() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
    }

    public MutableLiveData<NewTaskFormState> getNewTaskFormState() {
        return newtaskFormState;
    }

    public MutableLiveData<NewTaskResult> getNewTaskResult() {
        return newtaskResult;
    }

    /* public LiveData<LoggedInUser> getLoggedUser() {
        return loggedUser;
    } */

    public MutableLiveData<NewTaskIn> getNewTask() {
        return newtask;
    }

    public void newtask(String nometask, String stato, String descrizione) {

        auth.signInWithEmailAndPassword(nometask,stato,descrizione)
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {
                        FirebaseUser task = auth.get();
                        newtaskResult.setValue(new LoginResult(new LoggedInUser(nometask, task.getDisplayName())));
                    } else {
                        newtaskResult.setValue(new LoginResult(R.string.invalid_task));
                    }
                });
    }

    public void newtaskDataChanged(String nometask, String stato, String descrizione) {
        if (!isNameTaskValid(nometask)) {
            newtaskFormState.setValue(new NewTaskFormState(R.string.invalid_taskname, null, null));
        } else if (!isStateValid(stato)) {
            newtaskFormState.setValue(new NewTaskFormState(null, R.string.invalid_state, null));
        } else if (!isDescriptionValid(descrizione)) {
            newtaskFormState.setValue(new NewTaskFormState( null, null, R.string.invalid_description));
        } else {
            newtaskFormState.setValue(new NewTaskFormState(true));
        }
    }

    public void confirmNewTask(String id) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("task");

        myRef.child(id).setValue(task).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                newtaskResult.setValue(new NewTaskResult(task));
            } else {
                newtaskResult.setValue(new NewTaskResult(R.string.invalid_task));
            }
        });
    }

    public void setTask (NewTaskIn task) {
        this.loggedUser.setValue(task);
    }

    // A placeholder name and surname validation check
    private boolean isNameTaskValid(String nometask) {
        if (nometask == null || nometask.isEmpty()) {
            return false;
        }

        String strPattern = "[~!@#$%^&*()_+{}\\[\\]:;,.<>/?-]";
        Pattern p = Pattern.compile(strPattern);
        Matcher m = p.matcher(nometask);
        return !m.find();
    }

    private boolean isDescriptionValid(String descrizione) {
        return descrizione.split("").length < 200;
    }

    // A placeholder password validation check
    private boolean isStateValid(String stato) {
        return stato != null && stato.trim().length() > 5;
    }

    public LoggedInUser getCurrentLoggedUser() {
        return currentLoggedUser;
    }

}