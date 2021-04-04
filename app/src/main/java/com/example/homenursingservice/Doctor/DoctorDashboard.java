package com.example.homenursingservice.Doctor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
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
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.homenursingservice.About;
import com.example.homenursingservice.AllNotifications;
import com.example.homenursingservice.CustomAlertDialog;
import com.example.homenursingservice.Patient.All_Doctors;
import com.example.homenursingservice.Patient.BookServiceForm;
import com.example.homenursingservice.Patient.MapActivity;
import com.example.homenursingservice.Patient.PatientProfile;
import com.example.homenursingservice.Patient.RequestedServiceList;
import com.example.homenursingservice.Patient.ServiceDetails;
import com.example.homenursingservice.Patient.User_Dashboard;
import com.example.homenursingservice.R;
import com.example.homenursingservice.ServiceModel;
import com.example.homenursingservice.SharedPrefManager;
import com.example.homenursingservice.User_Login;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DoctorDashboard extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Location mLastKnownLocation;
    private LocationCallback locationCallback;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String TAG="MAin";
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
    TextView user_name;
    int fragment_number=1;
    public static TextView message_unseen;
    ActionBar actionBar;
    GridView gridView;
    ImageView notification;
    String user_id;
    Button request;
    FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();
    Button vaccine,insulin,baby_care,lactation,medicine,injection,cannula,oxygen_support,physio_therapy;
    ArrayList<ServiceModel> serviceModelArrayList=new ArrayList<>();
    ProgressDialog progressDialog;
    HashMap<String,Integer> logos=new HashMap<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_dashboard);
        user_id=firebaseAuth.getCurrentUser().getUid();
        logos.put("vaccine",R.drawable.vaccine);
        logos.put("injection",R.drawable.injection);
        logos.put("lactation",R.drawable.lactation);
        logos.put("insulin",R.drawable.insuline);
        logos.put("baby care",R.drawable.baby_care);
        logos.put("physio therapy",R.drawable.physio_therapy);
        logos.put("oxygen support",R.drawable.oxygen_support);
        logos.put("cannula",R.drawable.cannula);
        logos.put("medicine",R.drawable.medicine);
        progressDialog=new ProgressDialog(DoctorDashboard.this);
        progressDialog.setTitle("Please Wait....");
        progressDialog.show();;
        actionBar=getSupportActionBar();
        menu=findViewById(R.id.menu);
        menu2=findViewById(R.id.menu_icon2);
        user_name=findViewById(R.id.user_name);
        message_unseen=findViewById(R.id.message_unseen);
        notification=findViewById(R.id.notification);
        request=findViewById(R.id.request);
        request.setVisibility(View.GONE);
        user_name.setText(SharedPrefManager.getInstance(getApplicationContext()).getUser().user_name);
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(DoctorDashboard.this);
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
        gridView = (GridView) findViewById(R.id.grid_view); // init GridView
        // Create an object of CustomAdapter and set Adapter to GirdView
        // implement setOnItemClickListener event on GridView
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(DoctorDashboard.this, ServiceDetails.class);
                intent.putExtra("name", serviceModelArrayList.get(position).name);
                intent.putExtra("id", serviceModelArrayList.get(position).id);
                intent.putExtra("details", serviceModelArrayList.get(position).details);
                intent.putExtra("price", serviceModelArrayList.get(position).price);
                intent.putExtra("logo", serviceModelArrayList.get(position).logo);
                intent.putExtra("user_type","doctor");
                startActivity(intent); // start Intent
            }
        });

        getLocationPermission();
        LinearLayout profile=findViewById(R.id.profile);
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                changeFragmentView(new PatientProfile());

            }
        });
        LinearLayout dashboard=findViewById(R.id.dashboard);
        dashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                fragmentManager =getSupportFragmentManager();
                fragmentManager.popBackStack();
                drawer.closeDrawer(GravityCompat.START);
            }
        });
        LinearLayout service_list=findViewById(R.id.requested_service_list);
        service_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(actionBar!=null) actionBar.setTitle("All Requests");
                changeFragmentView(new RequestedServiceList());
            }
        });
        LinearLayout contact=findViewById(R.id.contact);
        contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(actionBar!=null) actionBar.setTitle("Contact");
                changeFragmentView(new All_Doctors());
            }
        });
        LinearLayout about=findViewById(R.id.about);
        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(actionBar!=null) actionBar.setTitle("About");
                changeFragmentView(new About());
            }
        });
        notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(actionBar!=null) actionBar.setTitle("Notification");
                startActivity(new Intent(getApplicationContext(), AllNotifications.class));
            }
        });
        getAllServices();
        get_all_notifications();

    }
    public void get_all_notifications(){
        progressDialog.show();
        Query documentReference=db.collection("AllNotifications").whereEqualTo("seen_status","unseen").whereEqualTo("receiver_id",user_id);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {


                if(task.isComplete()){

                    QuerySnapshot querySnapshot=task.getResult();
                    if(querySnapshot!=null&&querySnapshot.size()>0){
                        message_unseen.setVisibility(View.VISIBLE);
                        message_unseen.setText(querySnapshot.size()+"");
                    }
                    else{
                        message_unseen.setVisibility(View.GONE);

                    }

                }
                progressDialog.dismiss();

            }


        });
    }
    public void BookService(View view){
        Intent tnt=new Intent(getApplicationContext(), BookServiceForm.class);
        startActivity(tnt);
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
        PopupMenu popup = new PopupMenu(DoctorDashboard.this, view);

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

            Map<String,Object> map=new HashMap<>();
            map.put("location",geoPoint);
            String user_collection="AllDoctors";
            if(SharedPrefManager.getInstance(getApplicationContext()).getUser().user_type.equalsIgnoreCase("patient")){
                user_collection="AllUsers";
            }
            db.collection(user_collection).document(firebaseUser.getUid()).update(map).addOnSuccessListener(new OnSuccessListener<Void>() {
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
    public void getAllServices()
    {
        Query query= db.collection("ServiceList");
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                progressDialog.dismiss();
                if(task.isComplete()){
                    QuerySnapshot queryDocumentSnapshots=task.getResult();
                    for(DocumentSnapshot documentSnapshot:queryDocumentSnapshots){
                        Map<String,Object> data=documentSnapshot.getData();
                        System.out.println(logos.get(data.get("service_name").toString().toLowerCase()));
                        Integer integer=logos.get(data.get("service_name").toString().toLowerCase());
                        int logo=0;
                        boolean nameEnable=false;
                        if(integer!=null){
                            logo=logos.get(data.get("service_name").toString().toLowerCase());
                        }
                        else
                        {
                            nameEnable=true;
                            logo=R.drawable.service_logo;
                        }
                        ServiceModel requestedService=new ServiceModel(data.get("service_id").toString(),data.get("service_name").toString(),data.get("service_charge").toString(),data.get("description").toString(),logo,nameEnable);
                        serviceModelArrayList.add(requestedService);
                    }
                    User_Dashboard.CustomAdapter customAdapter = new User_Dashboard.CustomAdapter(getApplicationContext(), serviceModelArrayList);
                    gridView.setAdapter(customAdapter);
                }
            }
        });
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
                                Toast.makeText(DoctorDashboard.this, "unable to get last location", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }

    }
    @Override
    public void onBackPressed() {

        int count = fragmentManager.getBackStackEntryCount();
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else if(count==1){

            CustomAlertDialog.getInstance().show_exit_dialog(DoctorDashboard.this);
        }
        else {

            super.onBackPressed();
        }
    }

    public static class CustomAdapter extends BaseAdapter {
        Context context;
        ArrayList<ServiceModel> serviceModelArrayList;
        LayoutInflater inflter;
        public CustomAdapter(Context applicationContext, ArrayList<ServiceModel> serviceModelArrayList) {
            this.context = applicationContext;
            this.serviceModelArrayList = serviceModelArrayList;
            inflter = (LayoutInflater.from(applicationContext));
        }
        @Override
        public int getCount() {
            return serviceModelArrayList.size();
        }
        @Override
        public Object getItem(int i) {
            return null;
        }
        @Override
        public long getItemId(int i) {
            return 0;
        }
        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ServiceModel serviceModel=serviceModelArrayList.get(i);
            view = inflter.inflate(R.layout.service_item_layout, null); // inflate the layout
            ImageView icon = (ImageView) view.findViewById(R.id.logo); // get the reference of ImageView
            TextView textView=view.findViewById(R.id.name);
            if(!serviceModel.nameEnable)
            {
                textView.setText(serviceModel.name);
                textView.setVisibility(View.GONE);
            }
            icon.setImageResource(serviceModel.logo); // set logo images

            return view;
        }
    }

}
