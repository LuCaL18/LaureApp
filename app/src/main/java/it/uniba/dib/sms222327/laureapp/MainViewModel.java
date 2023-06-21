package it.uniba.dib.sms222327.laureapp;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import it.uniba.dib.sms222327.laureapp.R;

import it.uniba.dib.sms222327.laureapp.data.PersonaTesi;
import it.uniba.dib.sms222327.laureapp.data.Result;
import it.uniba.dib.sms222327.laureapp.data.RoleUser;
import it.uniba.dib.sms222327.laureapp.data.model.LoggedInUser;
import it.uniba.dib.sms222327.laureapp.data.model.Task;
import it.uniba.dib.sms222327.laureapp.util.Utility;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * ViewModel della main activity si occupa del caricamento delle tesi presenti nella home e del recupero dei dati dell'utente
 */
public class MainViewModel extends ViewModel {

    private final static String CLASSNAME = "MainViewModel";

    private final MutableLiveData<LoggedInUser> user = new MutableLiveData<>();
    private String idUser;
    private String fileToOpen;
    private Long downloadReference;
    private final CollectionReference tesiRef;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final MutableLiveData<List<Task>> tasks = new MutableLiveData<>();
    private final MutableLiveData<QuerySnapshot> lastTheses = new MutableLiveData<>();
    private final MutableLiveData<QuerySnapshot> thesesRole = new MutableLiveData<>();
    private final MutableLiveData<QuerySnapshot> thesesAmbito = new MutableLiveData<>();
    private final MutableLiveData<Result> editUserResult = new MutableLiveData<>();


    public MainViewModel() {
        tesiRef = FirebaseFirestore.getInstance().collection("tesi");
    }

    public void init(LoggedInUser user) {
        this.idUser = user.getId();

        if (this.user.getValue() != null) {
            return;
        }

        if (user.getRole() == null) {
            this.fetchDataUser(user.getId());
            return;
        }

        this.user.setValue(user);
    }

    public void fetchDataUser(String id) {
        db.collection("users").document(id).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                user.setValue(task.getResult().toObject(LoggedInUser.class));
                idUser = id;
            }
        });
    }

    public void updateUser(LoggedInUser updatedUser) {

        if (user.getValue() == null) {
            Log.w(CLASSNAME, "User is null");
            return;
        }

        Map<String, Object> updates = Utility.getObjectProperties(updatedUser);
        DocumentReference userRef = db.collection("users").document(user.getValue().getId());
        userRef.update(updates)
                .addOnSuccessListener(aVoid -> {
                    fetchDataUser(user.getValue().getId());
                    editUserResult.setValue(new Result.Success(R.string.edit_profile_success));
                })
                .addOnFailureListener(e -> {
                    editUserResult.setValue(new Result.Error(e));
                    Log.e(CLASSNAME, "Unable updates user");
                });
    }

    public void readTask(RoleUser role, String userId) {

        Query query;

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference tasksRef = db.collection("task");

        if (role.equals(RoleUser.STUDENT)) {
            query = tasksRef.whereEqualTo("student", userId);
        } else {
            query = tasksRef.whereArrayContains("relators", userId);
        }

        query.get().addOnCompleteListener(taskQuery -> {
            if (taskQuery.isSuccessful()) {
                List<Task> taskList = new ArrayList<>();

                for (QueryDocumentSnapshot document : taskQuery.getResult()) {
                    Task task = document.toObject(Task.class);
                    taskList.add(task);
                }
                tasks.setValue(taskList);
            } else {
                Log.d(CLASSNAME, "Error getting documents: ", taskQuery.getException());
            }
        });
    }

    public void loadTesiByRole(RoleUser role) {

        if (user.getValue() == null) {
            return;
        }

        String idUser = user.getValue().getId();

        if (role.equals(RoleUser.PROFESSOR)) {
            Query queryProf = tesiRef.whereEqualTo("relatore.id", idUser).orderBy("created_at", Query.Direction.DESCENDING).limit(3);

            Query queryCoRelatori = tesiRef.whereArrayContains("coRelatori", new PersonaTesi(idUser, user.getValue().getDisplayName(), user.getValue().getEmail(), null))
                    .orderBy("created_at", Query.Direction.DESCENDING)
                    .limit(3);

            com.google.android.gms.tasks.Task<QuerySnapshot> query1Task = queryProf.get();
            com.google.android.gms.tasks.Task<QuerySnapshot> query2Task = queryCoRelatori.get();

            Tasks.whenAllSuccess(query1Task, query2Task)
                    .addOnSuccessListener(new OnSuccessListener<List<Object>>() {
                        @Override
                        public void onSuccess(List<Object> objects) {
                            for (Object object : objects) {
                                QuerySnapshot querySnapshot = (QuerySnapshot) object;
                                thesesRole.setValue(querySnapshot);
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("HomeFragment", "Unable fetch thesis for professor: " + e.getMessage());
                        }
                    });

            return;
        }

        if (role.equals(RoleUser.STUDENT)) {
            Query queryStudent = tesiRef.whereEqualTo("student.id", idUser).orderBy("created_at", Query.Direction.DESCENDING).limit(3);

            queryStudent.get()
                    .addOnSuccessListener(thesesRole::setValue)
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("HomeFragment", "Unable fetch thesis for professor: " + e.getMessage());
                        }
                    });
        }

    }

    public void loadLastTheses() {
        Query queryLastTheses = tesiRef.orderBy("created_at", Query.Direction.DESCENDING).limit(3);

        queryLastTheses.get().addOnSuccessListener(lastTheses::setValue)
                .addOnFailureListener(e -> Log.e("HomeFragment", "Unable fetch last thesis for user: " + e.getMessage()));
    }

    public void loadThesesAmbito(List<String> ambiti) {

        if (ambiti == null || ambiti.isEmpty()) {
            thesesAmbito.setValue(null);
            return;
        }

        Query queryThesisAmbiti = tesiRef.whereIn("ambito", ambiti).orderBy("created_at", Query.Direction.DESCENDING).limit(3);

        queryThesisAmbiti.get().addOnSuccessListener(thesesAmbito::setValue)
                .addOnFailureListener(e -> Log.e("HomeFragment", "Unable fetch thesis for ambiti: " + e.getMessage()));
    }

    public MutableLiveData<List<Task>> getTasks() {
        return tasks;
    }

    public MutableLiveData<QuerySnapshot> getLastTheses() {
        return lastTheses;
    }

    public MutableLiveData<LoggedInUser> getUser() {
        return user;
    }

    public void setUser(LoggedInUser user) {
        this.user.setValue(user);
    }

    public String getIdUser() {
        return idUser;
    }

    public String getFileToOpen() {
        return fileToOpen;
    }

    public void setFileToOpen(String fileToOpen) {
        this.fileToOpen = fileToOpen;
    }

    public Long getDownloadReference() {
        return downloadReference;
    }

    public void setDownloadReference(Long downloadReference) {
        this.downloadReference = downloadReference;
    }

    public MutableLiveData<QuerySnapshot> getThesesRole() {
        return thesesRole;
    }

    public MutableLiveData<QuerySnapshot> getThesesAmbito() {
        return thesesAmbito;
    }

    public MutableLiveData<Result> getEditUserResult() {
        return editUserResult;
    }
}
