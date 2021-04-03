package com.example.homenursingservice.Patient;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.example.homenursingservice.DateTimeConverter;
import com.example.homenursingservice.Doctor.DoctorProfile;
import com.example.homenursingservice.Messenger;
import com.example.homenursingservice.Profile_View;
import com.example.homenursingservice.R;
import com.example.homenursingservice.RequestedService;
import com.example.homenursingservice.SharedPrefManager;
import com.example.homenursingservice.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class All_Doctors extends Fragment {


    ArrayList<User> memberInfos=new ArrayList<>();
    RecyclerView recyclerView;
    ProgressDialog progressDialog;
    RecycleAdapter recycleAdapter;
    ArrayList<Object> classes=new ArrayList<>();
    ArrayList<Object> sections=new ArrayList<>();
    ArrayList<Object> mediums=new ArrayList<>();
    AlertDialog alertDialog2,alertDialog;

    float dX;
    float dY;
    int lastAction;
    Spinner sp_medium,sp_class,sp_section;
    String medium="Bangla",class_id="",section_id="";
    TextView no_item,tv_total_student;
    Button delete;
    CheckBox select_all;
    boolean show_checkbox=false,checked_all=false;
    EditText et_search;
    FirebaseFirestore db=FirebaseFirestore.getInstance();
    ArrayList<String> student_ids=new ArrayList<>();
    ArrayList<User> search_list=new ArrayList<>();
    Button search_btn;
    String search_string;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.fragment_all__students, container, false);
        progressDialog=new ProgressDialog(getContext());
        recyclerView=view.findViewById(R.id.recycle);
        recycleAdapter=new RecycleAdapter(memberInfos);
        no_item=view.findViewById(R.id.no_item);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(recycleAdapter);
        et_search=view.findViewById(R.id.search);
        search_btn=view.findViewById(R.id.search_btn);
        et_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {


                search_string=charSequence.toString();
                set_search_match_item(charSequence.toString(),1);

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                set_search_match_item(search_string,2);

            }
        });
        loadAllDoctor();
        return view;
    }


    public void set_search_match_item(String search_string, int condition){

        search_string=search_string.toLowerCase();
        search_list.clear();
        for(User student2:memberInfos){

            String id=student2.user_id.toLowerCase();
            String name=student2.user_name.toLowerCase();

            if(condition==1){
                if(id.contains(search_string)||name.contains(search_string)){

                    search_list.add(student2);
                }
            }
            else if(condition==2){
                if(id.equalsIgnoreCase(search_string)||name.equalsIgnoreCase(search_string)){

                    search_list.add(student2);
                }
            }


        }
        recycleAdapter=new RecycleAdapter(search_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(recycleAdapter);


    }

    public void loadAllDoctor()
    {
        Query query=db.collection("AllDoctors");
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isComplete()){
                    QuerySnapshot queryDocumentSnapshots=task.getResult();
                    for(DocumentSnapshot documentSnapshot:queryDocumentSnapshots){
                        Map<String,Object> data=documentSnapshot.getData();
                        memberInfos.add(new User(data.get("user_id").toString(),data.get("user_name").toString(),data.get("user_type").toString(),data.get("phone_number").toString(),data.get("image_path").toString(),data.get("device_id").toString(),"","",true));
                     }
                    Toast.makeText(getContext(),""+memberInfos.size(),Toast.LENGTH_LONG).show();
                    if(memberInfos.size()==0){
                        recyclerView.setVisibility(View.GONE);
                    }
                    else{
                        no_item.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                        recycleAdapter.notifyDataSetChanged();
                    }

                }
            }
        });
    }

    public class RecycleAdapter extends RecyclerView.Adapter<RecycleAdapter.ViewAdapter>{

        ArrayList<User> memberInfos;
        public RecycleAdapter(ArrayList<User> memberInfos){
            this.memberInfos=memberInfos;
        }
        public  class ViewAdapter extends RecyclerView.ViewHolder implements View.OnLongClickListener, View.OnClickListener {


            View mView;
            Button view_profile,sendMessage;
            LinearLayout linearLayout;
            TextView id,name,email;
            CircleImageView profile_image;
            CheckBox select;
            public ViewAdapter(View itemView) {
                super(itemView);
                itemView.setOnLongClickListener(this);
                mView=itemView;
                mView.setOnClickListener(this);
                sendMessage=mView.findViewById(R.id.message);
                view_profile=mView.findViewById(R.id.view_profile);
                select=mView.findViewById(R.id.select);
                id=mView.findViewById(R.id.id);
                name=mView.findViewById(R.id.name);
                email=mView.findViewById(R.id.email);
                profile_image=mView.findViewById(R.id.profile);

            }

            @Override
            public boolean onLongClick(View view) {


                int position =getLayoutPosition();
                User memberInfo=memberInfos.get(position);
                return true;
            }

            @Override
            public void onClick(View view) {

                if(select.isChecked()&&show_checkbox){
                    student_ids.remove(memberInfos.get(getLayoutPosition()).user_id);
                    select.setChecked(false);
                }
                else if(show_checkbox) {
                    student_ids.add(memberInfos.get(getLayoutPosition()).user_id);
                    select.setChecked(true);
                }
            }



        }


        @NonNull
        @Override
        public ViewAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.student_rec_list_item4,parent,false);
            return new ViewAdapter(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewAdapter holder, final int position) {


            final  User memberInfo=memberInfos.get(position);
            holder.id.setText(memberInfo.user_id);
            holder.name.setText(memberInfo.user_name);
            holder.name.setSelected(true);
            holder.email.setText(memberInfo.phone_number);
            holder.email.setSelected(true);

            if(show_checkbox){
                holder.select.setVisibility(View.VISIBLE);

                if(checked_all){

                    holder.select.setVisibility(View.VISIBLE);
                    holder.select.setChecked(true);
                }
                else {

                    holder.select.setChecked(false);
                }

            }
            else{

                holder.select.setVisibility(View.GONE);
            }





            if(memberInfo.image_path.length()>0&&!memberInfo.image_path.equalsIgnoreCase("null")){
                Picasso.get().load(memberInfo.image_path).placeholder(R.drawable.profile10).into(holder.profile_image);
            }
            holder.sendMessage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent tnt=new Intent(getContext(), Messenger.class);
                    tnt.putExtra("sender_id", SharedPrefManager.getInstance(getContext()).getUser().user_id);
                    tnt.putExtra("receiver_id",memberInfo.user_id);
                    tnt.putExtra("sender_type",SharedPrefManager.getInstance(getContext()).getUser().getUser_type());
                    tnt.putExtra("receiver_type","Student");
                    tnt.putExtra("sender_device_id",SharedPrefManager.getInstance(getContext()).getUser().getDevice_id());
                    tnt.putExtra("receiver_device_id",memberInfo.device_id);
                    tnt.putExtra("image_path",memberInfo.image_path);
                    startActivity(tnt);
                }
            });

            holder.view_profile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    Intent tnt=new Intent(getContext(), Profile_View.class);
                    tnt.putExtra("user_id",memberInfo.user_id);
                    tnt.putExtra("user_name",memberInfo.user_name);
                    tnt.putExtra("phone_number",memberInfo.phone_number);
                    tnt.putExtra("device_id",memberInfo.device_id);
                    tnt.putExtra("user_type",memberInfo.user_type);
                    tnt.putExtra("image_path",memberInfo.image_path);
                    startActivity(tnt);
                }
            });



        }

        @Override
        public int getItemCount() {
            return memberInfos.size();
        }



    }

}
