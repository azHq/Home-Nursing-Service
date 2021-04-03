package com.example.homenursingservice;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class Profile_View extends AppCompatActivity {

    CircleImageView circleImageView;
    TextView tv_name,tv_id,tv_email,tv_phone_number;
    ImageView edit_name,edit_phone,edit_password,edit_email;
    ProgressDialog progressDialog;
    private int PICK_IMAGE_REQUEST = 1;
    String id="",user_name="",phone_number="",image_path="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile__view);
        if(getSupportActionBar()!=null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        id=getIntent().getStringExtra("user_id");
        user_name=getIntent().getStringExtra("user_name");
        phone_number=getIntent().getStringExtra("phone_number");
        image_path=getIntent().getStringExtra("image_path");
        tv_name=findViewById(R.id.name);
        tv_id=findViewById(R.id.id);
        tv_phone_number=findViewById(R.id.phone_number);
        tv_name.setText(user_name);
        tv_id.setText(id);
        tv_phone_number.setText(phone_number);
        circleImageView=findViewById(R.id.profile_image);
        progressDialog=new ProgressDialog(Profile_View.this);
        progressDialog.setMessage("Please Wait");
        if(image_path.length()>0&&!image_path.equalsIgnoreCase("null")){
            Picasso.get().load(image_path).placeholder(R.drawable.profile10).into(circleImageView);
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
        }
        return super.onOptionsItemSelected(item);
    }


}
