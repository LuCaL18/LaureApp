package it.uniba.dib.sms222327.laureapp.ui.chat;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import it.uniba.dib.sms222327.laureapp.data.model.ChatData;
import it.uniba.dib.sms222327.laureapp.data.model.LoggedInUser;

import java.util.ArrayList;

/**
 * ViewModel utilizzato per comunicare i dati dalla lista chat al fragment della chat. Si occupa anche del
 * recupero dei dati della chat da firebase
 */
public class ChatViewModel extends ViewModel {

    private final MutableLiveData<LoggedInUser[]> members = new MutableLiveData<>();
    private String nameChat;

    @Nullable
    private String idChat;

    private final FirebaseFirestore database = FirebaseFirestore.getInstance();
    private final ArrayList<LoggedInUser> membersTemp = new ArrayList<>();

    public ChatViewModel() {}

    public void init(String id) {

        if(idChat != null) {
            return;
        }
        idChat = id;

        DocumentReference ref = database.collection("chats").document(id);
        ref.get().addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot result = (DocumentSnapshot) task.getResult();
                    fetchData(result.toObject(ChatData.class));
                }
            }
        });
    }

    private void fetchData(@Nullable ChatData data) {

        if(data == null) {
            Log.w("fetchData", "data is null");
            return;
        }

        nameChat = data.getName();
        for (String member : data.getMembers()) {
            DocumentReference ref = database.collection("users").document(member);

            ref.get().addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot result = (DocumentSnapshot) task.getResult();

                        if(result.exists()) {
                            membersTemp.add(result.toObject(LoggedInUser.class));
                        }

                        if(membersTemp.size() == data.getMembers().size()) {
                            members.setValue(membersTemp.toArray(new LoggedInUser[0]));
                            membersTemp.clear();
                        }
                    }
                }
            });
        }
    }

    public MutableLiveData<LoggedInUser[]> getMembers() {
        return members;
    }

    public String getIdChat() {
        return idChat;
    }

    public void setIdChat(String idChat) {
        this.idChat = idChat;
    }

    public String getNameChat() {
        return nameChat;
    }
}
