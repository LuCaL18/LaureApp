package com.uniba.mobile.cddgl.laureapp.ui.chat;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.uniba.mobile.cddgl.laureapp.data.model.ChatData;
import com.uniba.mobile.cddgl.laureapp.data.model.LoggedInUser;

import java.util.ArrayList;


public class ChatViewModel extends ViewModel {

    private final MutableLiveData<LoggedInUser[]> members = new MutableLiveData<>();
    private String nameChat;

    @Nullable
    private String idChat;

    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private final ArrayList<LoggedInUser> membersTemp = new ArrayList<>();

    public ChatViewModel() {}

    public void init(String id) {

        if(idChat != null) {
            return;
        }
        idChat = id;

        DatabaseReference ref = database.getReference().child("chats").child(id);
        ref.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    DataSnapshot dataSnapshot = task.getResult();
                    fetchData(dataSnapshot.getValue(ChatData.class));
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
        for (String member : data.getMembers().keySet()) {
            DatabaseReference ref = database.getReference("users/" + member);

            ref.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if (task.isSuccessful()) {
                        DataSnapshot dataSnapshot = task.getResult();
                        membersTemp.add(dataSnapshot.getValue(LoggedInUser.class));

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
