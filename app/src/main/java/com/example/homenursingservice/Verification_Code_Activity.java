package com.example.homenursingservice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.homenursingservice.Doctor.DoctorDashboard;
import com.example.homenursingservice.Patient.User_Dashboard;
import com.goodiebag.pinview.Pinview;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.concurrent.TimeUnit;

public class Verification_Code_Activity extends AppCompatActivity {

    String mVerificationId;
    PhoneAuthProvider.ForceResendingToken mResendToken;
    Pinview pinview;
    String verification_code;
    String user_name="",phone_number="",user_type="",device_id="",image_path="",user_id="";
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    AlertDialog alertDialog;
    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;
    GeoPoint geoPoint=new GeoPoint(0,0);
    TextView resend;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification_code);
        firebaseAuth=FirebaseAuth.getInstance();
        user_name=getIntent().getStringExtra("user_name");
        user_type=getIntent().getStringExtra("user_type");
        phone_number="+88"+getIntent().getStringExtra("phone_number");
        pinview = (Pinview) findViewById(R.id.pinview);
        resend=findViewById(R.id.resend);
        progressDialog=new ProgressDialog(Verification_Code_Activity.this);
        progressDialog.setMessage("Please Wait...");
        pinview.setPinViewEventListener(new Pinview.PinViewEventListener() {
            @Override
            public void onDataEntered(Pinview pinview, boolean fromUser) {


                    verification_code=pinview.getValue();


            }
        });
        resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resendVerificationCode(phone_number,mResendToken);
            }
        });

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {

                progressDialog.show();
                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {


                Toast.makeText(getApplicationContext(),"Fail To Send Code",Toast.LENGTH_LONG).show();
                if (e instanceof FirebaseAuthInvalidCredentialsException) {

                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    // ...
                }


            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {


                mVerificationId = verificationId;
                mResendToken = token;

            }
        };

        send_code(mCallbacks,phone_number);

    }
    private void resendVerificationCode(String phoneNumber,
                                        PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks,         // OnVerificationStateChangedCallbacks
                token);             // ForceResendingToken from callbacks
    }

    public void verify(View view)
    {

        if(pinview.getValue().length()==6) {

            progressDialog.show();
            if(mVerificationId!=null&&mVerificationId.length()>0){
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, verification_code);
                signInWithPhoneAuthCredential(credential);
            }
            else{
                Toast.makeText(getApplicationContext(),"Please wait for code send",Toast.LENGTH_LONG).show();
            }

        }
        else {

            Toast.makeText(getApplicationContext(),"Please enter verification code",Toast.LENGTH_LONG).show();
        }


    }
    public void send_code(PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks,String phone_number){

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phone_number,
                60,
                TimeUnit.SECONDS,
                this,
                mCallbacks);

    }



    public void signInWithPhoneAuthCredential(PhoneAuthCredential credential)
    {


        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {


                            FirebaseUser user = task.getResult().getUser();
                            user_id=user.getUid();
                            getDeviceId();

                            // ...
                        } else {
                            progressDialog.dismiss();
                            show_dialog();
                        }
                    }
                });


    }

    public void upload_user_data(){

        String account_status="approved";
        if(user_type.equals(Constants.user2)){
            account_status="pending";
        }
        User user=new User(user_id,user_name,user_type,phone_number,image_path,device_id,geoPoint,"active","true",true);
        user.create_at= FieldValue.serverTimestamp();
        SharedPrefManager.getInstance(getApplicationContext()).userLogin(user);
        FirebaseFirestore db=FirebaseFirestore.getInstance();
        if(user_type.equalsIgnoreCase("Patient")){
            db.collection("AllUsers").document(user_id).set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {

                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(),"Log In Successfully",Toast.LENGTH_LONG).show();
                    startActivity(new Intent(getApplicationContext(), DoctorDashboard.class));
                    finish();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(),"Failed To Log In",Toast.LENGTH_LONG).show();
                    finish();
                }
            });
        }
        else {

            db.collection("AllDoctors").document(user_id).set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(),"Log In Successfully",Toast.LENGTH_LONG).show();
                    startActivity(new Intent(getApplicationContext(), DoctorDashboard.class));
                    finish();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(),"Failed To Log In",Toast.LENGTH_LONG).show();
                    finish();
                }
            });
        }

    }

    public void getDeviceId(){

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {

                            Toast.makeText(getApplicationContext(),"Please Check Your Internet Connection",Toast.LENGTH_LONG).show();
                            return;
                        }

                        device_id = task.getResult().getToken();
                        upload_user_data();


                    }
                });

    }

    public void show_dialog()
    {
        AlertDialog.Builder builder=new AlertDialog.Builder(Verification_Code_Activity.this);
        View view= LayoutInflater.from(getApplicationContext()).inflate(R.layout.dialogview1,null);
        builder.setView(view);
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
}
