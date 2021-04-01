package com.example.homenursingservice.Patient;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.homenursingservice.CustomAlertDialog;
import com.example.homenursingservice.DateTimeConverter;
import com.example.homenursingservice.NotificationSender;
import com.example.homenursingservice.R;
import com.example.homenursingservice.SharedPrefManager;
import com.example.homenursingservice.SpinnerAdapter;
import com.example.homenursingservice.User;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class BookServiceForm extends AppCompatActivity {

    Spinner spinner;
    EditText service_charge_et,patient_age_et,location_name_et,patient_name_et;
    TextView time_tv;
    String service_type="",service_charge="",patient_age="",time="",location_name="";
    GeoPoint location_geopoint=new GeoPoint(0,0);
    ArrayList<Object> service_list=new ArrayList<>();
    List<String> service_charge_list= Arrays.asList("","200","300","400","500");
    DatePickerDialog datePicker;
    Calendar calendar;
    int day,month,year,hour,minute;
    String date_time="",user_id="";
    boolean data_uploaded=true;
    FirebaseFirestore db=FirebaseFirestore.getInstance();
    ProgressDialog progressDialog;
    private String user_name,image_path,device_id,phone_number,patient_name="";
    Date date;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_service_form);
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
        spinner.setAdapter(new SpinnerAdapter(getApplicationContext(),1,service_list));
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
                CustomAlertDialog.getInstance().success_message(BookServiceForm.this,"Home Nursing Service","Your Request Submitted Successfully",true);
            }
        }).addOnCanceledListener(new OnCanceledListener() {
            @Override
            public void onCanceled() {
                data_uploaded=true;
            }
        });
    }
    public void create_notification(String service_id){
        NotificationSender.getInstance(getApplicationContext()).set_notification_data2("Patient Send Service Request",user_name+" want to take "+service_type+" Service.",user_id,user_name,image_path,"Patient","",service_id,device_id,"","service_request");
    }
}