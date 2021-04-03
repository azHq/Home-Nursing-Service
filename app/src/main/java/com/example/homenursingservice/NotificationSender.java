package com.example.homenursingservice;

import android.content.Context;

import androidx.annotation.NonNull;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class NotificationSender {

    private static final String FCM_API = "https://fcm.googleapis.com/fcm/send";
    private static final String serverKey ="key="+"AAAAhG4MwZo:APA91bH7qjG4KlmsxPRr5Nu4-Jx8SJ5gQl9Dk5p1nhx38U3mUFiqWwrsqnAp2xiOPwh3J5bM373iNeZG0Unh9q3SFH0OVrpXNwqPF6l1jB5jGgBAnRO5Qd-D4SHslIBYyuc7TK3J_hV5";
    private static final String contentType = "application/json";
    public static NotificationSender instance;
    DocumentReference documentReferencefornotification;
    String notification_id;
    private Context context;
    private NotificationSender(Context context){
        this.context=context;
    }
    public static NotificationSender getInstance(Context context){
        if(instance==null){
            instance=new NotificationSender(context);
        }
        return instance;
    }

    public void createNotification(String title,String body,String sender_id,String sender_name,String image_path,String sender_type,String receiver_id,String document_id,String sender_device_id,String receiver_device_id,String activity_type){
        FirebaseFirestore db= FirebaseFirestore.getInstance();
        documentReferencefornotification=db.collection("AllNotifications").document();
        notification_id=documentReferencefornotification.getId();
        JSONObject notification_payload = new JSONObject();
        JSONObject notification = new JSONObject();
        JSONObject payload = new JSONObject();
        JSONObject data = new JSONObject();
        try {
            notification.put("body", body);
            notification.put("title", title);
            payload.put("sender_id", sender_id);
            payload.put("receiver_id", receiver_id);
            payload.put("sender_type",sender_type);
            payload.put("receiver_id",receiver_id);
            payload.put("document_id",document_id);
            payload.put("notification_id",notification_id);
            payload.put("sender_device_id",sender_device_id);
            payload.put("receiver_device_id",receiver_device_id);
            payload.put("activity_type",activity_type);
            payload.put("sender_type",SharedPrefManager.getInstance(context).getUser().user_type);
            data.put("notification",notification);
            data.put("data",payload);
            notification_payload.put("data",data);
            notification_payload.put("to", receiver_device_id);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        sendNotification(notification_payload);
        //set_notification_data(title, body, sender_id,sender_name,image_path,sender_type, receiver_id, document_id, sender_device_id, receiver_device_id, activity_type);


    }
    private void set_notification_data(String title,String body,String sender_id,String sender_name,String image_path,String sender_type,String receiver_id,String document_id,String sender_device_id,String receiver_device_id,String activity_type){

        Map<String,Object> data=new HashMap<>();
        data.put("title",title);
        data.put("body",body);
        data.put("sender_id",sender_id);
        data.put("sender_name",sender_name);
        data.put("sender_image_path",image_path);
        data.put("sender_location",SharedPrefManager.getInstance(context).getUser().geoPoint);
        data.put("receiver_id",receiver_id);
        data.put("document_id",document_id);
        data.put("sender_device_id",sender_device_id);
        data.put("receiver_device_id",receiver_device_id);
        data.put("activity_type",activity_type);
        data.put("sender_type",sender_type);
        data.put("seen_status","unseen");
        data.put("time", FieldValue.serverTimestamp());
        data.put("notification_id",notification_id);

        documentReferencefornotification.set(data).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

            }
        });
    }
    public void set_notification_data2(String title,String body,String sender_id,String sender_name,String image_path,String sender_type,String receiver_id,String document_id,String sender_device_id,String receiver_device_id,String activity_type){
        FirebaseFirestore db= FirebaseFirestore.getInstance();
        documentReferencefornotification=db.collection("AllNotifications").document();
        notification_id=documentReferencefornotification.getId();
        Map<String,Object> data=new HashMap<>();
        data.put("title",title);
        data.put("body",body);
        data.put("sender_id",sender_id);
        data.put("sender_name",sender_name);
        data.put("sender_image_path",image_path);
        data.put("sender_location",SharedPrefManager.getInstance(context).getUser().geoPoint);
        data.put("receiver_id",receiver_id);
        data.put("document_id",document_id);
        data.put("sender_device_id",sender_device_id);
        data.put("receiver_device_id",receiver_device_id);
        data.put("activity_type",activity_type);
        data.put("sender_type",sender_type);
        data.put("seen_status","unseen");
        data.put("time", FieldValue.serverTimestamp());
        data.put("notification_id",notification_id);

        documentReferencefornotification.set(data).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

            }
        });
        createNotification(title,body,sender_id,sender_name,image_path,sender_type,receiver_id,document_id,sender_device_id,receiver_device_id,activity_type);
    }
    private void sendNotification(JSONObject jsonObject) {

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(FCM_API, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        //Toast.makeText(getApplicationContext(),response.toString(),Toast.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                       // Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_LONG).show();

                    }
                }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", serverKey);
                params.put("Content-Type", contentType);
                return params;
            }
        };
        VolleySingleton.getInstance(context).addToRequestQueue(jsonObjectRequest);
    }


}
