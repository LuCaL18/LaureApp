package it.uniba.dib.sms222327.laureapp.util;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import it.uniba.dib.sms222327.laureapp.data.NotificationType;
import it.uniba.dib.sms222327.laureapp.data.model.LoggedInUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Classe per inviare notifiche al servizio https://fcm.googleapis.com/fcm/send
 */
public class BaseRequestNotification {

    public final static String KEY_TYPE = "type";
    private final static String FCM_URL = "https://fcm.googleapis.com/fcm/send";
    private final static String SERVER_KEY = "AAAAnH_RsIk:APA91bFL7ux-XVLy0ZQKTtZm5kxmQSLs6oGkBScKp3usiwJGCCQVGSq7AQo_TdDrTzxnnndsbX24MCW8tLp-O1ySZciPw4c37fZKJedVpusF1R6v84O6e5DQlR59G933dITJqVcVI9-E";


    private JSONObject data = new JSONObject();
    private final JSONObject notification = new JSONObject();
    private final String receiveId;
    private final NotificationType type;

    public BaseRequestNotification(String receiveId, NotificationType type) {
        this.receiveId = receiveId;
        this.type = type;
    }

    public JSONObject getData() {
        return data;
    }

    public void addData(Map<String, Object> dataToAdd) {
        for (String key : dataToAdd.keySet()) {
            try {
                data.put(key, dataToAdd.get(key));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public JSONObject getNotification() {
        return notification;
    }

    public String getReceiveId() {
        return receiveId;
    }

    public NotificationType getType() {
        return type;
    }

    public void setNotification(String title, String body) {
        try {
            notification.put("title", title);
            notification.put("body", body);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private JSONObject createJson(String token) {
        JSONObject json = new JSONObject();
        try {
            json.put("to", token);
            json.put("notification", notification);
            json.put("data", data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    public void sendRequest(int method, Context context) {
        FirebaseFirestore.getInstance().collection("users").document(receiveId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                LoggedInUser user = ((DocumentSnapshot) task.getResult()).toObject(LoggedInUser.class);

                JsonObjectRequest request = createJsonRequest(method, user.getToken());

                Volley.newRequestQueue(context).add(request);

                clearData();
            }
        });
    }

    private JsonObjectRequest createJsonRequest(int method, String token) {

        JSONObject json = createJson(token);

        Log.i("JSON", json.toString());

        return new JsonObjectRequest(method, BaseRequestNotification.FCM_URL, json,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("TAG", "onResponse: " + response);
                        saveInDatabase(json);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("TAG", "onError: " + error);
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "key=" + BaseRequestNotification.SERVER_KEY);
                return headers;
            }
        };
    }


    private void saveInDatabase(JSONObject json) {
        NotificationDB notificationDB = new NotificationDB();
        notificationDB.addNotification(json);
    }

    private void clearData() {
        data = null;
        data = new JSONObject();
    }
}
