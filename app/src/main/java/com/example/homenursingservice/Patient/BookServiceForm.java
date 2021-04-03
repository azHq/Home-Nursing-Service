package com.example.homenursingservice.Patient;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.homenursingservice.Constants;
import com.example.homenursingservice.CustomAlertDialog;
import com.example.homenursingservice.DateTimeConverter;
import com.example.homenursingservice.NotificationSender;
import com.example.homenursingservice.R;
import com.example.homenursingservice.ServiceModel;
import com.example.homenursingservice.SharedPrefManager;
import com.example.homenursingservice.SpinnerAdapter;
import com.example.homenursingservice.User;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BookServiceForm extends AppCompatActivity {

    Spinner spinner;
    EditText service_charge_et,patient_age_et,location_name_et,patient_name_et;
    TextView time_tv;
    String service_type="",service_charge="",patient_age="",time="",location_name="";
    GeoPoint location_geopoint=new GeoPoint(0,0);
    ArrayList<Object> service_list=new ArrayList<>();
    List<String> service_charge_list=new ArrayList<>();
    DatePickerDialog datePicker;
    Calendar calendar;
    int day,month,year,hour,minute;
    String date_time="",user_id="";
    boolean data_uploaded=true;
    FirebaseFirestore db=FirebaseFirestore.getInstance();
    ProgressDialog progressDialog;
    private String user_name,image_path,device_id,phone_number,patient_name="";
    Date date;
    String admin_device_id="",admin_id="";
    AlertDialog alertDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_service_form);
        service_type=getIntent().getStringExtra("name");
        service_charge=getIntent().getStringExtra("price");
        if(getSupportActionBar()!=null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        service_list.add("Service Type");
        service_list.add("Insulin");
        service_list.add("Baby Care");
        service_list.add("Dialysis");
        progressDialog=new ProgressDialog(BookServiceForm.this);
        User user= SharedPrefManager.getInstance(getApplicationContext()).getUser();
        user_id=user.user_id;
        user_name=user.user_name;
        phone_number=user.phone_number;
        image_path=user.image_path;
        device_id=user.device_id;
        progressDialog.setMessage("Please Wait.....");
        spinner=findViewById(R.id.service_type);
        service_charge_et=findViewById(R.id.charge);
        patient_age_et=findViewById(R.id.age);
        time_tv=findViewById(R.id.time);
        patient_name_et=findViewById(R.id.patient_name);
        location_name_et=findViewById(R.id.location);
        calendar=Calendar.getInstance();
        date=new Date();
        calendar.setTime(date);
        day=calendar.get(Calendar.DAY_OF_MONTH);
        month=calendar.get(Calendar.MONTH);
        year=calendar.get(Calendar.YEAR);
        hour=calendar.get(Calendar.HOUR_OF_DAY);
        minute=calendar.get(Calendar.MINUTE);

        datePicker=new DatePickerDialog(this,new DatePickerDialog.OnDateSetListener(){

            @Override
            public void onDateSet(DatePicker view, int year2, int month2, final int dayOfMonth) {

                day=dayOfMonth;
                year=year2;
                month=month2;
                TimePickerDialog timePickerDialog = new TimePickerDialog(BookServiceForm.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute2) {
                        hour=hourOfDay;
                        minute=minute2;
                        calendar.set(Calendar.DAY_OF_MONTH,day);
                        calendar.set(Calendar.YEAR,year);
                        calendar.set(Calendar.MONTH,month);
                        calendar.set(Calendar.HOUR_OF_DAY,hour);
                        calendar.set(Calendar.MINUTE,minute);
                        date_time= DateTimeConverter.getInstance().toDateStr2(calendar.getTimeInMillis());
                        time_tv.setText(date_time);
                    }
                }, hour, minute, false);
                timePickerDialog.show();
            }
        },year,month,day);
        time_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePicker.show();
            }
        });
        location_name_et.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() >= (location_name_et.getRight() - location_name_et.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {

                        Toast.makeText(getApplicationContext(),"Click On Drawable",Toast.LENGTH_LONG).show();
                        return true;
                    }
                }
                return false;
            }
        });
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int li=parent.getChildCount();
                if(li>0) {
                    View linearLayout=parent.getChildAt(0);
                    LinearLayout linearLayout1=linearLayout.findViewById(R.id.linear1);
                    LinearLayout linearLayout2=(LinearLayout) linearLayout1.getChildAt(0);
                    TextView textView=(TextView) linearLayout2.getChildAt(1);
                    textView.setTextColor(Color.BLACK);
                    if(position>0){
                        service_type=(String) service_list.get(position);
                        service_charge=service_charge_list.get(position);
                        service_charge_et.setText(service_charge_list.get(position)+" TK");
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        getAllServices();
        getAdminData();
    }
    public void getAdminData()
    {
        DocumentReference documentReference = db.collection("AppConfiguration").document("HNS");
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    progressDialog.dismiss();
                    if (document.exists()) {
                        Map<String, Object> map = document.getData();
                        admin_device_id=map.get("device_id").toString();
                        admin_id=map.get("id").toString();
                    }
                }
            }
        });
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
                        service_list.add(data.get("service_name").toString());
                        service_charge_list.add(data.get("service_charge").toString());
                    }
                    spinner.setAdapter(new SpinnerAdapter(getApplicationContext(),1,service_list));
                    if(service_type!=null&&service_type.length()>0) spinner.setSelection(service_list.indexOf(service_type));
                }
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
        }
        return super.onOptionsItemSelected(item);
    }
    public void cancel(View view)
    {
        finish();
    }
    public void book(View view){
        patient_age=patient_age_et.getText().toString();
        time=time_tv.getText().toString();
        location_name=location_name_et.getText().toString();
        patient_name=patient_name_et.getText().toString();
        if(service_type.length()==0){
            CustomAlertDialog.getInstance().show_error_dialog(BookServiceForm.this,getString(R.string.home_nursing_service),getString(R.string.choose_service_name));
            return;
        }
        if(patient_name.length()==0){
            CustomAlertDialog.getInstance().show_error_dialog(BookServiceForm.this,getString(R.string.home_nursing_service),getString(R.string.patient_name));
            return;
        }
        if(patient_age.length()==0){
            CustomAlertDialog.getInstance().show_error_dialog(BookServiceForm.this,getString(R.string.home_nursing_service),getString(R.string.enter_patient_age));
            return;
        }

        if(time.length()==0){
            CustomAlertDialog.getInstance().show_error_dialog(BookServiceForm.this,getString(R.string.home_nursing_service),getString(R.string.choose_time));
            return;
        }
        if(location_name.length()==0){
            CustomAlertDialog.getInstance().show_error_dialog(BookServiceForm.this,getString(R.string.home_nursing_service),getString(R.string.choose_location));
            return;
        }
        calendar.set(Calendar.DAY_OF_MONTH,day);
        calendar.set(Calendar.YEAR,year);
        calendar.set(Calendar.MONTH,month);
        calendar.set(Calendar.HOUR_OF_DAY,hour);
        calendar.set(Calendar.MINUTE,minute);
        date=calendar.getTime();
        upload_data();
    }
    public void upload_data(){
        data_uploaded=false;
        progressDialog.show();
        DocumentReference documentReference=db.collection("AllServices").document();
        final String service_id=documentReference.getId();
        HashMap<String,Object> data=new HashMap<>();
        data.put("user_name",user_name);
        data.put("user_id",user_id);
        data.put("phone_number",phone_number);
        data.put("image_path",image_path);
        data.put("service_id",service_id);
        data.put("service_type",service_type);
        data.put("charge",Integer.parseInt(service_charge));
        data.put("location",location_name);
        data.put("geo_point",location_geopoint);
        data.put("patient_name",patient_name);
        data.put("patient_age",Integer.parseInt(patient_age));
        data.put("date_time",date);
        data.put("create_at", FieldValue.serverTimestamp());
        data.put("status","pending");
        data.put("doctor_id","");
        data.put("doctor_name","");
        data.put("doctor_phone_number","");
        data.put("doctor_image_path","");
        data.put("payment",0);
        documentReference.set(data).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                data_uploaded=true;
                time_tv.setText("");
                patient_age_et.setText("");
                service_charge_et.setText("");
                patient_age_et.setText("");
                location_name_et.setText("");
                progressDialog.dismiss();
                create_notification(service_id);
                success_message(service_id);
            }
        }).addOnCanceledListener(new OnCanceledListener() {
            @Override
            public void onCanceled() {
                data_uploaded=true;
            }
        });
    }
    public void success_message(final String id){
        AlertDialog.Builder alert=new AlertDialog.Builder(BookServiceForm.this);
        View view= LayoutInflater.from(BookServiceForm.this).inflate(R.layout.exit_panel,null);
        alert.setView(view);
        alertDialog=alert.show();;
        Button yes=view.findViewById(R.id.yes);
        Button no=view.findViewById(R.id.no);
        TextView title_tv=view.findViewById(R.id.title);
        title_tv.setText(R.string.app_name);
        TextView body_tv=view.findViewById(R.id.body);
        body_tv.setText("Your Request Submitted Successfully.Please Complete Your Payment.Otherwise Your Request Will Be Cancelled.");
        yes.setText("Pay Now");
        no.setText("Cancel");
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                Intent tnt=new Intent(getApplicationContext(),Payment.class);
                tnt.putExtra("service_charge",service_charge);
                tnt.putExtra("id",id);
                startActivity(tnt);
            }
        });
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                db.document(id).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        CustomAlertDialog.getInstance().success_message(BookServiceForm.this,R.string.app_name+"","Your Request Have Been Cancel",true);
                    }
                });
            }
        });
    }
    public void create_notification(String service_id){
        NotificationSender.getInstance(getApplicationContext()).set_notification_data2("Patient Send Service Request",user_name+" want to take "+service_type+" Service.",user_id,user_name,image_path,"Patient",admin_id,service_id,device_id,admin_device_id,"service_request");
    }
}