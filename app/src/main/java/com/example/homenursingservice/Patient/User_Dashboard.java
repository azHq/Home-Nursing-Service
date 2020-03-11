package com.example.homenursingservice.Patient;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.homenursingservice.ClusterMarker;
import com.example.homenursingservice.MainActivity;
import com.example.homenursingservice.MyClusterManagerRenderer;
import com.example.homenursingservice.Patient.Dashboard;
import com.example.homenursingservice.R;
import com.example.homenursingservice.SharedPrefManager;
import com.example.homenursingservice.User;
import com.example.homenursingservice.User_Login;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.maps.android.clustering.ClusterManager;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class User_Dashboard extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Location mLastKnownLocation;
    private LocationCallback locationCallback;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String TAG="MAin";
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    GeoPoint geoPoint;
    DrawerLayout drawer;
    ImageView menu,menu2;
    ActionBarDrawerToggle toggle;
    Toolbar toolbar;
    FrameLayout frameLayout;
    FragmentManager fragmentManager;
    private static String POPUP_CONSTANT = "mPopup";
    private static String POPUP_FORCE_SHOW_ICON = "setForceShowIcon";
    Button vaccine;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_dashboard);

        menu=findViewById(R.id.menu);
        menu2=findViewById(R.id.menu_icon2);
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(User_Dashboard.this);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               if(drawer.isDrawerOpen(GravityCompat.START)){
                   drawer.closeDrawer(GravityCompat.START);
               }
               else {
                   drawer.openDrawer(GravityCompat.START);
               }
            }
        });
        menu2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showPopup(menu2);

            }
        });
        frameLayout=findViewById(R.id.frame_layout);
        vaccine=findViewById(R.id.vaccine);
        vaccine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(getApplicationContext(),MapActivity.class));
            }
        });
        getLocationPermission();

    }
    public void changeFragmentView(Fragment fragment){

        fragmentManager =getSupportFragmentManager();
        fragmentManager.popBackStack();
        int count = fragmentManager.getBackStackEntryCount();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout,fragment)
                .addToBackStack(null).commit();
        drawer.closeDrawer(GravityCompat.START);
    }

    public void showPopup(View view) {
        PopupMenu popup = new PopupMenu(User_Dashboard.this, view);

        try {
            // Reflection apis to enforce show icon
            Field[] fields = popup.getClass().getDeclaredFields();
            for (Field field : fields) {
                if (field.getName().equals(POPUP_CONSTANT)) {
                    field.setAccessible(true);
                    Object menuPopupHelper = field.get(popup);
                    Class<?> classPopupHelper = Class.forName(menuPopupHelper.getClass().getName());
                    Method setForceIcons = classPopupHelper.getMethod(POPUP_FORCE_SHOW_ICON, boolean.class);
                    setForceIcons.invoke(menuPopupHelper, true);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        popup.getMenuInflater().inflate(R.menu.user_dashboard_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {

                        if(item.getItemId()==R.id.log_out){
                            SharedPrefManager.getInstance(getApplicationContext()).logout();
                            FirebaseAuth.getInstance().signOut();
                            finish();
                            startActivity(new Intent(getApplicationContext(), User_Login.class));
                        }
                        else if(item.getItemId()==R.id.settings){

                            Toast.makeText(getApplicationContext(),"Settings",Toast.LENGTH_LONG).show();
                        }

                        return true;
                    }
                });
        popup.show();
    }




    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: called.");

        switch(requestCode){
            case LOCATION_PERMISSION_REQUEST_CODE:{
                if(grantResults.length > 0){
                    for(int i = 0; i < grantResults.length; i++){
                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                            Log.d(TAG, "onRequestPermissionsResult: permission failed");
                            return;
                        }
                    }
                    Log.d(TAG, "onRequestPermissionsResult: permission granted");
                    //initialize our map
                    getDeviceLocation();
                }
            }
        }
    }
    private void getLocationPermission(){
        Log.d(TAG, "getLocationPermission: getting location permissions");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                getDeviceLocation();
            }else{
                ActivityCompat.requestPermissions(this,
                        permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        }else{
            ActivityCompat.requestPermissions(this,
                    permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    public void upload_location(GeoPoint geoPoint){

        if(db!=null&&firebaseUser!=null){

            Map<String,GeoPoint> map=new HashMap<>();
            map.put("loaction",geoPoint);
            db.collection("clients").document(firebaseUser.getUid()).set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {


                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Toast.makeText(getApplicationContext(),e.toString(),Toast.LENGTH_LONG).show();
                }
            });
        }

    }

    @SuppressLint("MissingPermission")
    private void getDeviceLocation() {


        if(mFusedLocationProviderClient!=null){

            mFusedLocationProviderClient.getLastLocation()
                    .addOnCompleteListener(new OnCompleteListener<Location>() {
                        @Override
                        public void onComplete(@NonNull Task<Location> task) {
                            if (task.isSuccessful()) {


                                mLastKnownLocation = task.getResult();

                                if (mLastKnownLocation != null) {

                                    GeoPoint geoPoint=new GeoPoint(mLastKnownLocation.getLatitude(),mLastKnownLocation.getLongitude());
                                    upload_location(geoPoint);
                                } else {
                                    final LocationRequest locationRequest = LocationRequest.create();
                                    locationRequest.setInterval(10000);
                                    locationRequest.setFastestInterval(5000);
                                    locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                                    locationCallback = new LocationCallback() {
                                        @Override
                                        public void onLocationResult(LocationResult locationResult) {
                                            super.onLocationResult(locationResult);
                                            if (locationResult == null) {
                                                return;
                                            }
                                            mLastKnownLocation = locationResult.getLastLocation();
                                            GeoPoint geoPoint=new GeoPoint(mLastKnownLocation.getLatitude(),mLastKnownLocation.getLongitude());
                                            upload_location(geoPoint);
                                            mFusedLocationProviderClient.removeLocationUpdates(locationCallback);
                                        }
                                    };
                                    mFusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);

                                }
                            } else {
                                Toast.makeText(User_Dashboard.this, "unable to get last location", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }

    }



}
