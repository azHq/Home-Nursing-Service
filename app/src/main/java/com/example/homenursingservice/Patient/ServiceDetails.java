package com.example.homenursingservice.Patient;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.homenursingservice.R;

public class ServiceDetails extends AppCompatActivity {

    String name,price,details;
    int logo;
    TextView name_tv,price_tv,details_tv;
    ImageView logoImage;
    Button book_service;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_details);
        name_tv=findViewById(R.id.name);
        price_tv=findViewById(R.id.price);
        details_tv=findViewById(R.id.details);
        logoImage=findViewById(R.id.logo);
        book_service=findViewById(R.id.book_service);
        name=getIntent().getStringExtra("name");
        price=getIntent().getStringExtra("price");
        details=getIntent().getStringExtra("details");
        logo=getIntent().getIntExtra("logo",0);
        logoImage.setImageResource(logo);
        book_service.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent tnt=new Intent(getApplicationContext(),BookServiceForm.class);
                tnt.putExtra("name",name);
                tnt.putExtra("price",price+"");
                startActivity(tnt);
            }
        });
        name_tv.setText(name);
        price_tv.setText(price+" TK");
        details_tv.setText(details);
    }
}