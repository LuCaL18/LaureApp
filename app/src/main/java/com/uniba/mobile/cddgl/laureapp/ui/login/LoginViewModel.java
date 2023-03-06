package com.uniba.mobile.cddgl.laureapp.ui.login;

import android.util.Patterns;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.uniba.mobile.cddgl.laureapp.R;
import com.uniba.mobile.cddgl.laureapp.data.RoleUser;
import com.uniba.mobile.cddgl.laureapp.data.model.LoggedInUser;
import com.uniba.mobile.cddgl.laureapp.ui.login.registration.RegisterFormState;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginViewModel extends ViewModel {

    private final MutableLiveData<LoginFormState> loginFormState = new MutableLiveData<>();
    private final MutableLiveData<RegisterFormState> registerFormState = new MutableLiveData<>();
    private final MutableLiveData<LoginResult> loginResult = new MutableLiveData<>();
    private final MutableLiveData<LoggedInUser> loggedUser = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isUserVerification = new MutableLiveData<>();
    private final FirebaseAuth auth;
    private LoggedInUser currentLoggedUser;

    LoginViewModel() {
        auth = FirebaseAuth.getInstance();
    }

    public LiveData<LoginFormState> getLoginFormState() {
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

    public MutableLiveData<Boolean> getIsUserVerification() {
        return isUserVerification;
    }

    public void login(String username, String password) {

        auth.signInWithEmailAndPassword(username, password)
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {
                        FirebaseUser user = auth.getCurrentUser();
                        CollectionReference database = FirebaseFirestore.getInstance().collection("users");

                        database.document(user.getUid()).get().addOnCompleteListener(task1 -> {
                            if(task1.isSuccessful()) {
                                loginResult.setValue(new LoginResult((LoggedInUser) ((DocumentSnapshot) task1.getResult()).toObject(LoggedInUser.class)));
                            } else {
                                loginResult.setValue(new LoginResult(new LoggedInUser(user.getEmail(), user.getDisplayName())));
                            }
                        });
                    } else {
                        loginResult.setValue(new LoginResult(R.string.login_failed));
                    }
                });
    }

    public void register(String username, String password, String name, String surname, String dob, String bio, RoleUser role, List<String> ambiti) {
        auth.createUserWithEmailAndPassword(username, password).addOnCompleteListener(task -> {

            if (task.isSuccessful()) {
                isUserVerification.setValue(false);
                currentLoggedUser = new LoggedInUser(auth.getCurrentUser().getUid(), username, name, surname, dob, bio, role, ambiti);
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

    public void registerDataChanged(String username, String password, String name, String surname, String date, String bio, String confirmPassword) {
        if (!isUserNameValid(username)) {
            registerFormState.setValue(new RegisterFormState(R.string.invalid_username, null, null, null, null, null, null));
        } else if (!isNameValid(name)) {
            registerFormState.setValue(new RegisterFormState(null, null, R.string.invalid_name, null, null, null, null));
        } else if (!isNameValid(surname)) {
            registerFormState.setValue(new RegisterFormState(null, null, null, R.string.invalid_surname, null, null, null));
        } else if (!isDateValid(date)) {
            registerFormState.setValue(new RegisterFormState(null, null, null, null, R.string.invalid_date, null, null));
        } else if (!isBioValid(bio)) {
            registerFormState.setValue(new RegisterFormState(null, null, null, null, null, R.string.invalid_bio, null));
        } else if (!isPasswordValid(password)) {
            registerFormState.setValue(new RegisterFormState(null, R.string.invalid_password, null, null, null, null, null));
        }else if (!password.equals(confirmPassword)){
            registerFormState.setValue(new RegisterFormState(null, null, null, null, null, null, R.string.invalid_confirm_password));
        }else {
            registerFormState.setValue(new RegisterFormState(true));
        }
    }

    public void confirmRegistration(String id) {
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        CollectionReference myRef = database.collection("users");

        myRef.document(id).set(currentLoggedUser).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                loginResult.setValue(new LoginResult(currentLoggedUser));
            } else {
                loginResult.setValue(new LoginResult(R.string.registration_failed));
            }
        });
    }

    public void setLoggedUser(LoggedInUser user) {
        this.loggedUser.setValue(user);
    }

    // A placeholder username validation check
    private boolean isUserNameValid(String username) {
        if (username == null) {
            return false;
        }

        return Patterns.EMAIL_ADDRESS.matcher(username).matches();
    }

    // A placeholder name and surname validation check
    private boolean isNameValid(String name) {
        if (name == null || name.isEmpty()) {
            return false;
        }

        String strPattern = "[~!@#$%^&*()_+{}\\[\\]:;,.<>/?-]";
        Pattern p = Pattern.compile(strPattern);
        Matcher m = p.matcher(name);
        return !m.find();
    }

    private boolean isDateValid(String dateString) {
        if (dateString == null) {
            return false;
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        try {
            Date date = dateFormat.parse(dateString);
            return date.getTime() < new Date().getTime();
        } catch (ParseException e) {
            return false;
        }
    }

    private boolean isBioValid(String bio) {
        return bio.split("").length < 200;
    }

    // A placeholder password validation check
    private boolean isPasswordValid(String password) {
        return password != null && password.trim().length() > 5;
    }

    public LoggedInUser getCurrentLoggedUser() {
        return currentLoggedUser;
    }
}