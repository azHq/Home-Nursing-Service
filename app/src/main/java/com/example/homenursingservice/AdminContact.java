package com.example.homenursingservice;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class AdminContact extends Fragment {
    public String document_id="",seller_id="",seller_device_id,user_id;
    FirebaseFirestore db;
    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;
    RecyclerView price_history_recycle;
    LinearLayout price_history_layout;
    TextView confirm_message,bkash_acc_number,rocket_acc_number,nagad_acc_number,mail_tv,phone_number_tv;
    Button submit_btn,call_btn,mail_btn,message_btn;
    EditText referral_code_et;
    String admin_device_id="",mail,phone_number="",admin_id="";
    CardView referral_code_card_view;
    public final  int PHONE_CALL_PERMISSION=1;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.activity_contact_for_seller, container, false);
        firebaseAuth=FirebaseAuth.getInstance();
        user_id=firebaseAuth.getCurrentUser().getUid();
        progressDialog=new ProgressDialog(getContext());
        progressDialog.setMessage("Please wait....");
        db=FirebaseFirestore.getInstance();
        mail_tv=view.findViewById(R.id.mail);
        phone_number_tv=view.findViewById(R.id.phone_number);
        mail_btn=view.findViewById(R.id.mail_btn);
        call_btn=view.findViewById(R.id.call_btn);
        message_btn=view.findViewById(R.id.message_btn);
        call_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPermission();

            }
        });
        mail_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.setType("plain/text");
                emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{mail});
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "");
                emailIntent.putExtra(Intent.EXTRA_TEXT, "");
                startActivity(Intent.createChooser(emailIntent, "Send mail..."));
            }
        });
        message_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent tnt=new Intent(getContext(), Messenger.class);
                tnt.putExtra("sender_id", user_id);
                tnt.putExtra("receiver_id",admin_id);
                tnt.putExtra("sender_type",SharedPrefManager.getInstance(getContext()).getUser().getUser_type());
                tnt.putExtra("receiver_type","Admin");
                tnt.putExtra("sender_device_id",SharedPrefManager.getInstance(getContext()).getUser().getDevice_id());
                tnt.putExtra("receiver_device_id",admin_device_id);
                startActivity(tnt);
            }
        });
        get_AppConfigurationData();
        return view;
    }
    public void get_AppConfigurationData(){
        progressDialog.show();
        DocumentReference documentReference=db.collection("AppConfiguration").document("AppConfiguration");
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isComplete()){
                    progressDialog.dismiss();
                    DocumentSnapshot documentSnapshot=task.getResult();
                    if(documentSnapshot.exists())
                    {

                        Map<String,Object> data=documentSnapshot.getData();
                        String minimum_payment=data.get("minimum_payment").toString();
                        String expire_time=data.get("confirmation_expire_time").toString();
                        admin_id=data.get("admin_id").toString();
                        String str=data.get("bkash_account_number").toString()+"("+data.get("bkash_account_number_status")+")";
                        str=data.get("rocket_account_number").toString()+"("+data.get("rocket_account_number_status")+")";
                        str=data.get("nagad_account_number").toString()+"("+data.get("nagad_account_number_status")+")";
                        mail=data.get("email").toString();
                        mail_tv.setText(mail);
                        phone_number=data.get("phone_number").toString();
                        phone_number_tv.setText(EngToBanConverter.getInstance().convert(phone_number));
                        get_device_id(admin_id);
                    }

                }
            }
        });
    }

    public void get_device_id(String user_id){
        DocumentReference documentReference = db.collection("Users").document(user_id);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    progressDialog.dismiss();
                    if (document.exists()) {

                        Map<String, Object> map = document.getData();
                        if (map.containsKey("device_id")) {

                             admin_device_id=map.get("device_id").toString();
                        }
                    }
                }
            }
        });
    }
    public void start_call(){
        Intent phoneIntent = new Intent(Intent.ACTION_CALL);
        Toast.makeText(getContext(),"call sending",Toast.LENGTH_LONG).show();
        phoneIntent.setData(Uri.parse("tel:+88"+phone_number));
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PHONE_CALL_PERMISSION);
            } else {
                startActivity(phoneIntent);
            }
        }
        else{
            startActivity(phoneIntent);
        }

    }
    private void requestPermission() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PHONE_CALL_PERMISSION);
            } else {
                start_call();
            }
        } else {
            start_call();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PHONE_CALL_PERMISSION && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            start_call();
        } else {
            Toast.makeText(getContext(), "Please Give Permission For Phone Call", Toast.LENGTH_LONG).show();
        }
    }
}
