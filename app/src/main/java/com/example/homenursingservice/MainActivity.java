package com.example.homenursingservice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.homenursingservice.Doctor.DoctorDashboard;
import com.example.homenursingservice.Patient.MapActivity;
import com.example.homenursingservice.Patient.User_Dashboard;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.squareup.picasso.Picasso;

import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Location mLastKnownLocation;
    private LocationCallback locationCallback;
    String TAG="Main";
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    GeoPoint geoPoint;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressDialog=new ProgressDialog(MainActivity.this);
        progressDialog.setMessage("Please Wait...");
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseUser=firebaseAuth.getCurrentUser();
        //progressDialog.show();
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);
        new CheckNetWorkConnection(MainActivity.this).execute();

    }
    class CheckNetWorkConnection extends AsyncTask<String, Void,Boolean> {
        Context activity;
        public CheckNetWorkConnection(Context activity) {
            this.activity= activity;
        }
        @Override
        protected Boolean doInBackground(String... strings) {
            boolean networkAvalaible;
            try {
                URL myUrl = new URL("https://www.google.com");
                URLConnection connection = myUrl.openConnection();
                connection.setConnectTimeout(600);
                connection.connect();
                networkAvalaible = true;
            } catch (Exception e) {
                e.printStackTrace();
                networkAvalaible = false;
            }
            return networkAvalaible;
        }

        @Override
        protected void onPostExecute(final Boolean aBoolean) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (aBoolean){
                        if(firebaseUser!=null) get_user_data();
                        else {

                            startActivity(new Intent(getApplicationContext(), User_Login.class));
                            finish();
                        }
                    }else {
                       CustomAlertDialog.getInstance().show_error_dialog(getApplicationContext(),"Home Nursing Service","Please Check Your Internet Connection");
                    }
                }
            },600);
            super.onPostExecute(aBoolean);
        }
    }
    public void move_target_activity(){
        progressDialog.dismiss();
        if(firebaseUser!=null){

            if(SharedPrefManager.getInstance(getApplicationContext()).getUser().user_type.equals("patient")){
                startActivity(new Intent(getApplicationContext(), User_Dashboard.class));
                finish();
            }
            else{
                startActivity(new Intent(getApplicationContext(), DoctorDashboard.class));
                finish();
            }

        }
        else {

            startActivity(new Intent(getApplicationContext(), User_Login.class));
            finish();
        }
    }
    public void get_user_data() {
        String user_collection="AllDoctors";
        if(SharedPrefManager.getInstance(getApplicationContext()).getUser().user_type.equalsIgnoreCase("patient")){
            user_collection="AllUsers";
        }
        DocumentReference documentReference = db.collection(user_collection).document(SharedPrefManager.getInstance(getApplicationContext()).getUser().user_id);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {

                        Map<String, Object> map = document.getData();
                        User user=new User(map.get("user_id").toString(),map.get("user_name").toString(),map.get("user_type").toString(),map.get("phone_number").toString(),map.get("image_path").toString(),map.get("device_id").toString(),(GeoPoint)map.get("geoPoint"),map.get("account_status").toString(),map.get("online_status").toString(),true);
                        SharedPrefManager.getInstance(getApplicationContext()).userLogin(user);
                        if(!user.account_status.equalsIgnoreCase("disable")){
                            move_target_activity();
                        }
                        else {
                            CustomAlertDialog.getInstance().show_error_dialog(getApplicationContext(),"Home Nursing Service","Your Account Has Been Disable.");
                        }
                    }
                } else {
                    Toast.makeText(getApplicationContext(), R.string.connection_problem, Toast.LENGTH_LONG).show();
                }
            }
        });

    }
}
