package com.uniba.mobile.cddgl.laureapp.ui.task;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.uniba.mobile.cddgl.laureapp.data.RoleUser;
import com.uniba.mobile.cddgl.laureapp.data.model.LoggedInUser;
import com.uniba.mobile.cddgl.laureapp.R;
import com.uniba.mobile.cddgl.laureapp.data.model.NewTaskIn;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NewTaskViewModel extends ViewModel {

    private final MutableLiveData<NewTaskFormState> newtaskFormState = new MutableLiveData<>();
    private final MutableLiveData<NewTaskResult> newtaskResult = new MutableLiveData<>();
    // private final MutableLiveData<LoggedInUser> loggedUser = new MutableLiveData<>(); //
    private FirebaseAuth auth;

    NewTaskViewModel() {
        auth = FirebaseAuth.getInstance();

        //TODO gestire la logout nel navigation view del main activity
        auth.signOut();

        FirebaseUser task = auth.getCurrentUser();

    }

    LiveData<NewTaskFormState> getnewtaskFormState() {
        return newtaskFormState;
    }


    public LiveData<NewTaskResult> getnewtaskResult() {
        return newtaskResult;
    }

    /* public LiveData<LoggedInUser> getLoggedUser() {
        return loggedUser;
    } */

    public void insertNewTask(String nomeTask, String stato, String descrizione) {
        auth.createUserWithEmailAndPassword(nomeTask,stato).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {
                    FirebaseUser user = auth.getCurrentUser();

                    NewTaskIn newtaskIn = new NewTaskIn(nomeTask, stato, descrizione, RoleUser.STUDENT);

                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference myRef = database.getReference("newtask");

                    myRef.child(user.getUid()).setValue(newtaskIn).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                newtaskResult.setValue(new NewTaskResult(newtaskIn));
                            } else {
                                newtaskResult.setValue(new NewTaskResult(R.string.invalid_state));
                            }
                        }
                    });

                } else {

                    try {
                        throw task.getException();
                    } catch (FirebaseAuthWeakPasswordException e) {
                        newtaskResult.setValue(new NewTaskResult(R.string.invalid_credentials));
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        newtaskResult.setValue(new NewTaskResult(R.string.invalid_credentials));
                    } catch (FirebaseAuthUserCollisionException e) {
                        newtaskResult.setValue(new NewTaskResult(R.string.invalid_credentials));
                    } catch (Exception e) {
                        newtaskResult.setValue(new NewTaskResult(R.string.invalid_credentials));
                    }
                }
            }
        });
    }

    public void newtaskDataChanged(String nomeTask, String stato, String descrizione) {
        if (!isTaskNameValid(nomeTask)) {
            newtaskFormState.setValue(new NewTaskFormState(R.string.invalid_taskname, null, null));
        } else if (!isStatoValid(stato)) {
            newtaskFormState.setValue(new NewTaskFormState(null, R.string.invalid_state, null));
        } else if (!isDescriptionValid(descrizione)) {
            newtaskFormState.setValue(new NewTaskFormState(null, null, R.string.invalid_description));
        } else {
            newtaskFormState.setValue(new NewTaskFormState(true));
        }
    }

    /*
    public void setLoggedUser(LoggedInUser user) {
        this.loggedUser.setValue(user);
    } */

    // A placeholder task name and state validation check
    private boolean isTaskNameValid(String nometask) {
        if (nometask == null) {
            return false;
        }
        String strPattern = "[~!@#$%^&*()_+{}\\[\\]:;,.<>/?-]";
        Pattern p = Pattern.compile(strPattern);
        Matcher m = p.matcher(nometask);
        if (m.find()) {
            return false;
        }
        return true;
    }

    private boolean isStatoValid(String stato) {
        if (stato == null) {
            return false;
        }
        String strPattern = "[~!@#$%^&*()_+{}\\[\\]:;,.<>/?-]";
        Pattern p = Pattern.compile(strPattern);
        Matcher m = p.matcher(stato);
        if (m.find()) {
            return false;
        }
        return true;
    }

    // La descrizione deve avere massimo 200 caratteri
    private boolean isDescriptionValid(String descrizione) {
        return descrizione.split("").length < 200;
    }

}