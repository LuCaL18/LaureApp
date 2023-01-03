package com.uniba.mobile.cddgl.laureapp.ui.login;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import android.util.Log;
import android.util.Patterns;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.uniba.mobile.cddgl.laureapp.data.RoleUser;
import com.uniba.mobile.cddgl.laureapp.data.model.LoggedInUser;
import com.uniba.mobile.cddgl.laureapp.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginViewModel extends ViewModel {

    private final MutableLiveData<LoginFormState> loginFormState = new MutableLiveData<>();
    private final MutableLiveData<RegisterFormState> registerFormState = new MutableLiveData<>();
    private final MutableLiveData<LoginResult> loginResult = new MutableLiveData<>();
    private final MutableLiveData<LoggedInUser> loggedUser = new MutableLiveData<>();
    private FirebaseAuth auth;

    LoginViewModel() {
        auth = FirebaseAuth.getInstance();

        //TODO gestire la logout nel navigation view del main activity
        auth.signOut();

        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            this.loggedUser.setValue(new LoggedInUser(user.getEmail(), user.getDisplayName()));
        }
    }

    LiveData<LoginFormState> getLoginFormState() {
        return loginFormState;
    }

    public MutableLiveData<RegisterFormState> getRegisterFormState() {
        return registerFormState;
    }

    public LiveData<LoginResult> getLoginResult() {
        return loginResult;
    }

    public LiveData<LoggedInUser> getLoggedUser() {
        return loggedUser;
    }

    public void login(String username, String password) {

        auth.signInWithEmailAndPassword(username, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            FirebaseUser user = auth.getCurrentUser();
                            loginResult.setValue(new LoginResult(new LoggedInUser(username, user.getDisplayName())));
                        } else {
                            loginResult.setValue(new LoginResult(R.string.login_failed));
                        }
                    }
                });
    }

    public void register(String username, String password, String name, String surname, String dob, String bio) {
        auth.createUserWithEmailAndPassword(username, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {
                    FirebaseUser user = auth.getCurrentUser();

                    LoggedInUser loggedInUser = new LoggedInUser(username, name, surname, dob, bio, RoleUser.STUDENT);

                    UserProfileChangeRequest update = new UserProfileChangeRequest.Builder().setDisplayName(loggedInUser.getDisplayName()).build();
                    user.updateProfile(update).addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            Log.i("Update", "User updated");
                            //TODO: user.sendEmailVerification();
                        }
                    });

                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference myRef = database.getReference("users");

                    myRef.child(user.getUid()).setValue(loggedInUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                loginResult.setValue(new LoginResult(loggedInUser));
                            } else {
                                loginResult.setValue(new LoginResult(R.string.registration_failed));
                            }
                        }
                    });

                } else {

                    try {
                        throw task.getException();
                    } catch (FirebaseAuthWeakPasswordException e) {
                        loginResult.setValue(new LoginResult(R.string.weak_password));
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        loginResult.setValue(new LoginResult(R.string.invalid_credentials));
                    } catch (FirebaseAuthUserCollisionException e) {
                        loginResult.setValue(new LoginResult(R.string.user_collision));
                    } catch (Exception e) {
                        loginResult.setValue(new LoginResult(R.string.registration_failed));
                    }
                }
            }
        });
    }

    public void loginDataChanged(String username, String password) {
        if (!isUserNameValid(username)) {
            loginFormState.setValue(new LoginFormState(R.string.invalid_username, null));
        } else if (!isPasswordValid(password)) {
            loginFormState.setValue(new LoginFormState(null, R.string.invalid_password));
        } else {
            loginFormState.setValue(new LoginFormState(true));
        }
    }

    public void registerDataChanged(String username, String password, String name, String surname, String bio) {
        if (!isUserNameValid(username)) {
            registerFormState.setValue(new RegisterFormState(R.string.invalid_username, null, null, null, null));
        } else if (!isPasswordValid(password)) {
            registerFormState.setValue(new RegisterFormState(null, R.string.invalid_password, null, null, null));
        } else if (!isNameValid(name)) {
            registerFormState.setValue(new RegisterFormState(null, null, R.string.invalid_name, null, null));
        } else if (!isNameValid(surname)) {
            registerFormState.setValue(new RegisterFormState(null, null, null, R.string.invalid_surname, null));
        } else if (!isBioValid(bio)) {
            registerFormState.setValue(new RegisterFormState(null, null, null, null, R.string.invalid_bio));
        } else {
            registerFormState.setValue(new RegisterFormState(true));
        }
    }

    public void setLoggedUser(LoggedInUser user) {
        this.loggedUser.setValue(user);
    }

    // A placeholder username validation check
    private boolean isUserNameValid(String username) {
        if (username == null) {
            return false;
        }
        if (username.contains("@")) {
            return Patterns.EMAIL_ADDRESS.matcher(username).matches();
        } else {
            return !username.trim().isEmpty();
        }
    }

    // A placeholder name and surname validation check
    private boolean isNameValid(String name) {
        if (name == null) {
            return false;
        }

        String strPattern = "[~!@#$%^&*()_+{}\\[\\]:;,.<>/?-]";
        Pattern p = Pattern.compile(strPattern);
        Matcher m = p.matcher(name);
        if (m.find()) {
            return false;
        }

        return true;
    }

    // La descrizione deve avere massimo 200 caratteri
    private boolean isBioValid(String bio) {
        return bio.split("").length < 200;
    }

    // A placeholder password validation check
    private boolean isPasswordValid(String password) {
        return password != null && password.trim().length() > 5;
    }
}