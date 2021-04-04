package com.example.homenursingservice.Patient;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.homenursingservice.DateTimeConverter;
import com.example.homenursingservice.R;
import com.example.homenursingservice.RequestedService;
import com.example.homenursingservice.SharedPrefManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class RequestedServiceList extends Fragment {

    View view;
    RecyclerView recyclerView;
    TextView empty,header;
    FirebaseFirestore db=FirebaseFirestore.getInstance();
    ArrayList<RequestedService> requestedServiceArrayList=new ArrayList<>();
    RecycleAdapter recycleAdapter;
    public RequestedServiceList() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_requested_service_list, container, false);
        recyclerView=view.findViewById(R.id.recycle);
        recycleAdapter=new RecycleAdapter(requestedServiceArrayList);
        recyclerView.setAdapter(recycleAdapter);
        empty=view.findViewById(R.id.empty);
        get_all_request();
        return view;
    }
    public void get_all_request(){
       Query query= db.collection("AllServices").whereEqualTo("user_id", SharedPrefManager.getInstance(getContext()).getUser().user_id);
       query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
           @Override
           public void onComplete(@NonNull Task<QuerySnapshot> task) {
               if(task.isComplete()){
                   QuerySnapshot queryDocumentSnapshots=task.getResult();
                   for(DocumentSnapshot documentSnapshot:queryDocumentSnapshots){
                       Map<String,Object> data=documentSnapshot.getData();
                       RequestedService requestedService=new RequestedService(data.get("service_id").toString(),data.get("patient_name").toString(),data.get("user_id").toString(),data.get("phone_number").toString(),data.get("service_type").toString(),data.get("charge").toString(),data.get("location").toString(),(GeoPoint) data.get("geo_point"),data.get("patient_age").toString(), DateTimeConverter.getInstance().toDateStr2(((Timestamp)data.get("date_time")).getSeconds()*1000),data.get("create_at").toString(),data.get("doctor_id").toString(),data.get("doctor_name").toString(),data.get("image_path").toString(),data.get("doctor_phone_number").toString(),data.get("doctor_image_path").toString(),data.get("status").toString(),Float.parseFloat(data.get("payment").toString()));
                       requestedServiceArrayList.add(requestedService);
                   }

                   recycleAdapter.notifyDataSetChanged();
               }
           }
       });
    }
    public class RecycleAdapter extends RecyclerView.Adapter<RecycleAdapter.ViewAdapter>{

        ArrayList<RequestedService> requestedServices;
        public RecycleAdapter(ArrayList<RequestedService> requestedServices){
            this.requestedServices=requestedServices;
        }
        public  class ViewAdapter extends RecyclerView.ViewHolder implements View.OnClickListener {

            View mView;
            Button details;
            TextView patient_name,age,service_charge,service_type,location,date,status,doctor_name,phone_number;
            CircleImageView doctor_profile;
            public ViewAdapter(View itemView) {
                super(itemView);
                mView=itemView;
                mView.setOnClickListener(this);
                patient_name=mView.findViewById(R.id.patient_name);
                age=mView.findViewById(R.id.age);
                service_charge=mView.findViewById(R.id.charge);
                service_type=mView.findViewById(R.id.service_type);
                location=mView.findViewById(R.id.location);
                date=mView.findViewById(R.id.date);
                status=mView.findViewById(R.id.status);
                doctor_name=mView.findViewById(R.id.doctor_name);
                phone_number=mView.findViewById(R.id.phone_number);
                details=mView.findViewById(R.id.payment);

            }


            @Override
            public void onClick(View v) {

                int position=getLayoutPosition();
                RequestedService requestedService=requestedServices.get(position);
                Intent tnt=new Intent(getContext(),ServiceDetails.class);
                tnt.putExtra("service_id",requestedService.service_id);
                startActivity(tnt);

            }
        }
        @NonNull
        @Override
        public ViewAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view= LayoutInflater.from(getContext()).inflate(R.layout.request_service_item,parent,false);
            return new ViewAdapter(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewAdapter holder, final int position) {


            final RequestedService requestedService=requestedServices.get(position);
            if(requestedService.doctor_image_path.length()>0){
                Picasso.get().load(requestedService.doctor_image_path).into(holder.doctor_profile);
            }
            holder.patient_name.setText("Patient Name: "+requestedService.patient_name);
            holder.patient_name.setSelected(true);
            holder.age.setText("Patient Age: "+requestedService.patient_age);
            holder.age.setSelected(true);
            holder.service_type.setText("Service Type: "+requestedService.service_type);
            holder.service_type.setSelected(true);
            holder.service_charge.setText("Charge: "+requestedService.service_charge+" Tk");
            holder.service_charge.setSelected(true);
            holder.location.setText("Location: "+requestedService.location_name);
            holder.location.setSelected(true);
            holder.date.setText("Date: "+requestedService.date_time);
            holder.date.setSelected(true);
            holder.status.setText("Request Status: "+requestedService.status);
            if(requestedService.doctor_name.length()>0){
                holder.doctor_name.setText(requestedService.doctor_name);
                holder.phone_number.setText(requestedService.doctor_phone_number);
            }
            else{
                holder.doctor_name.setText("Yet Not Assigned");
                holder.phone_number.setText(requestedService.doctor_phone_number);
            }


            holder.status.setSelected(true);
            if(requestedService.payment>0){
                holder.details.setVisibility(View.GONE);
            }
            holder.details.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Payment payment=new Payment(getContext());
                    System.out.println(requestedService.service_charge+""+requestedService.service_id);
                    payment.sendPayment(requestedService.service_charge+"",requestedService.service_id,false);

                }
            });



        }

        @Override
        public int getItemCount() {
            return requestedServices.size();
        }



    }

}