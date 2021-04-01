package com.example.homenursingservice;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.homenursingservice.Doctor.DoctorDashboard;
import com.example.homenursingservice.Patient.User_Dashboard;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class All_Notification_Service extends Service {

    public static boolean inside_messenger=false;
    String title="",body="",activity_type="",sender_type="",sender_id="",receiver_id="",document_id="",sender_device_id="",notification_id="";
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        title=(String)intent.getExtras().get("title");
        body=(String)intent.getExtras().get("body");
        sender_id=(String)intent.getExtras().get("sender_id");
        receiver_id=(String)intent.getExtras().get("receiver_id");
        document_id=(String)intent.getExtras().get("document_id");
        sender_device_id=(String)intent.getExtras().get("sender_device_id");
        activity_type=(String)intent.getExtras().get("activity_type");
        sender_type=(String)intent.getExtras().get("sender_type");
        notification_id=(String)intent.getExtras().get("notification_id");
        System.out.println(activity_type+","+document_id);
        generateNotification();

        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void generateNotification(){

        NotificationManager mNotificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        int notificationId=(int) System.currentTimeMillis();
        String channelId = "channel-01";
        String channelName = "Channel Name";
        int importance = NotificationManager.IMPORTANCE_HIGH;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(
                    channelId, channelName, importance);
            mNotificationManager.createNotificationChannel(mChannel);
        }

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(),channelId)
                .setSmallIcon(R.drawable.logo)
                .setContentTitle(title)
                .setContentText(body);
        Intent resultIntent=null;
        mBuilder.setAutoCancel(true);

        if(activity_type.length()>0&&activity_type.equalsIgnoreCase("new message")){

            if(!Messenger.isMessaging){
                resultIntent= new Intent(this, Messenger.class);
                resultIntent.putExtra("sender_id",sender_id);
                resultIntent.putExtra("receiver_id",receiver_id);
                resultIntent.putExtra("document_id",document_id);
                resultIntent.putExtra("sender_type",SharedPrefManager.getInstance(getApplicationContext()).getUser().getUser_type());
                resultIntent.putExtra("receiver_type",sender_type);
                resultIntent.putExtra("notification_id",notification_id);
                resultIntent.putExtra("sender_device_id",SharedPrefManager.getInstance(getApplicationContext()).getUser().device_id);
                resultIntent.putExtra("receiver_device_id",sender_device_id);

                PendingIntent resultPendingIntent = PendingIntent.getActivity(getApplicationContext(),0,resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                mBuilder.setContentIntent(resultPendingIntent);
                mBuilder.setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 });
                Notification note = mBuilder.build();
                mNotificationManager.notify(notificationId, note);
                get_all_notifications();
            }
            else{
                update_notification_status2( notification_id);
            }

        }
        else if(activity_type.length()>0&&activity_type.equalsIgnoreCase("new price")){

            if(SharedPrefManager.getInstance(getApplicationContext()).getUser().user_type.equalsIgnoreCase("Admin")){
               // resultIntent= new Intent(this, ViewPriceRequest.class);
            }
            else if(SharedPrefManager.getInstance(getApplicationContext()).getUser().user_type!=null&&SharedPrefManager.getInstance(getApplicationContext()).getUser().user_type.equalsIgnoreCase("seller")){
               // resultIntent= new Intent(this, NewPriceRequest.class);
            }
            resultIntent.putExtra("sender_id",sender_id);
            resultIntent.putExtra("document_id",document_id);
            resultIntent.putExtra("notification_id",notification_id);
            resultIntent.putExtra("sender_device_id",sender_device_id);
            PendingIntent resultPendingIntent =PendingIntent.getActivity(getApplicationContext(),0,resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            mBuilder.setContentIntent(resultPendingIntent);
            mBuilder.setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 });
            Notification note = mBuilder.build();
            mNotificationManager.notify(notificationId, note);
            get_all_notifications();
        }
        else if(activity_type.length()>0&&activity_type.equalsIgnoreCase("seller_want_to_sell")){

            if(SharedPrefManager.getInstance(getApplicationContext()).getUser().user_type.equalsIgnoreCase("Admin")){
                //resultIntent= new Intent(this, ViewConfirmationRequestForBuy.class);
            }
            else if(SharedPrefManager.getInstance(getApplicationContext()).getUser().user_type!=null&&SharedPrefManager.getInstance(getApplicationContext()).getUser().user_type.equalsIgnoreCase("buyer")){
                //resultIntent= new Intent(this, RequestConfirmation.class);
            }
            resultIntent.putExtra("sender_id",sender_id);
            resultIntent.putExtra("document_id",document_id);
            resultIntent.putExtra("notification_id",notification_id);
            resultIntent.putExtra("sender_device_id",sender_device_id);
            PendingIntent resultPendingIntent = PendingIntent.getActivity(getApplicationContext(),0,resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(resultPendingIntent);
            mBuilder.setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 });
            Notification note = mBuilder.build();
            mNotificationManager.notify(notificationId, note);
            get_all_notifications();
        }
        else if(activity_type.length()>0&&activity_type.equalsIgnoreCase("confirm")) {

            mBuilder.setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 });
            Notification note = mBuilder.build();
            mNotificationManager.notify(notificationId, note);
            get_all_notifications();
        }
    }
    public void update_notification_status2(String document_id){
        FirebaseFirestore db= FirebaseFirestore.getInstance();
        Map<String, Object> map =new HashMap<>();
        map.put("seen_status","seen");
        DocumentReference documentReference1=db.collection("AllNotifications").document(document_id);
        documentReference1.update(map);

    }
    public void get_all_notifications(){

        FirebaseFirestore db= FirebaseFirestore.getInstance();
        Query documentReference=db.collection("AllNotifications").whereEqualTo("receiver_id",receiver_id).whereEqualTo("seen_status","unseen");
        documentReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isComplete()){

                    QuerySnapshot querySnapshot=task.getResult();
                    if(querySnapshot!=null&&querySnapshot.size()>0){

                        TextView textView=null;
                        if(  User_Dashboard.message_unseen!=null){
                            textView=User_Dashboard.message_unseen;
                        }
                        else if(DoctorDashboard.message_unseen!=null){
                            textView=User_Dashboard.message_unseen;
                        }
                        if(textView!=null){
                            textView.setVisibility(View.VISIBLE);
                            if(querySnapshot.size()<100){
                                textView.setText(querySnapshot.size()+"");
                            }
                            else{
                                textView.setText("99+");
                            }
                        }

                    }


                }


            }


        });
    }
}
