package com.example.homenursingservice;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class User_Login extends AppCompatActivity {

    EditText et_user_name;
    EditText et_phone_number;
    String user_name,phone_number;
    Spinner sp_user_type;
    String user_type="";
    ArrayList<String> user_types=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user__login);

        et_user_name=findViewById(R.id.user_name);
        et_phone_number=findViewById(R.id.phone_number);
        sp_user_type=findViewById(R.id.user_type);
        user_types.add("Choose User Type");
        user_types.add(Constants.user1);
        user_types.add(Constants.user2);
        sp_user_type.setAdapter(new CustomAdapter(getApplicationContext(),0,user_types));
        sp_user_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                user_type=user_types.get(position);
                if(user_type.equalsIgnoreCase("caregiver"))
                {
                    user_type="Doctor";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }

    public void log_in(View view)
    {


        user_name=et_user_name.getText().toString();
        phone_number=et_phone_number.getText().toString().trim();

        if(user_name.length()>3&&phone_number.length()==11&&!user_type.equalsIgnoreCase("Choose User Type")){

            Intent tnt=new Intent(getApplicationContext(),Verification_Code_Activity.class);
            tnt.putExtra("user_name",user_name);
            tnt.putExtra("phone_number",phone_number);
            tnt.putExtra("user_type",user_type);
            startActivity(tnt);
            finish();
        }
        else{


            error_message();
        }




    }

    public void error_message(){

        if(user_name.length()<2){

            show_dialog("Invalid User Name","Please Enter Minimum 3 Character Length User Name");
        }
        else if(phone_number.length()!=11){

            show_dialog("Invalid Phone Number","Please Enter 11 Digit Length Phone Number");
        }
        else if(user_type.equalsIgnoreCase("Choose User Type")){

            show_dialog("User Type Missing","Please Choose A User Type");
        }
    }

    AlertDialog alertDialog;
    public void show_dialog(String title, String message){

        AlertDialog.Builder builder=new AlertDialog.Builder(User_Login.this);
        View view= LayoutInflater.from(getApplicationContext()).inflate(R.layout.dialogview1,null);
        builder.setView(view);
        TextView tv_title=view.findViewById(R.id.title);
        TextView tv_message=view.findViewById(R.id.message);
        tv_message.setText(message);
        tv_title.setText(title);
        Button ok=view.findViewById(R.id.ok);
        Button cancel=view.findViewById(R.id.cancel);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                alertDialog.dismiss();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });

        alertDialog=builder.show();
    }

    public static class CustomAdapter extends BaseAdapter {
        Context context;
        ArrayList<String> user_types;
        LayoutInflater inflter;
        int flag;

        public CustomAdapter(Context applicationContext,int flag,ArrayList<String> user_types) {
            this.context = applicationContext;
            this.flag = flag;
            this.user_types=user_types;
            inflter = (LayoutInflater.from(applicationContext));
        }

        @Override
        public int getCount() {
            return user_types.size();
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
            view = inflter.inflate(R.layout.user_type_layout, null);
            CircleImageView circleImageView=view.findViewById(R.id.user_icon);
            final TextView names = (TextView) view.findViewById(R.id.user_type);
            names.setText(user_types.get(i));

            if(user_types.get(i).equalsIgnoreCase("Patient")){

                circleImageView.setImageResource(R.drawable.patient);
            }

            return view;
        }
    }



}
