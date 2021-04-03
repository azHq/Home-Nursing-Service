package com.example.homenursingservice.Patient;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.homenursingservice.AdminContact;
import com.example.homenursingservice.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class PatientContact extends Fragment implements BottomNavigationView.OnNavigationItemSelectedListener {

    public FragmentTransaction ft;
    public FragmentManager fragmentManager;
    Fragment fragment;
    BottomNavigationView bottomNavigationView;
    ActionBar actionBar;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.activity_teacher__contact, container, false);
        bottomNavigationView=view.findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        changeFragmentView(new All_Doctors());
        return view;
    }
    public void changeFragmentView(Fragment fragment){


        fragmentManager =getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout,fragment).commit();

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()){

            case R.id.doctor:
                actionBar.setTitle("All Caregivers");
                changeFragmentView(new All_Doctors());
                return true;
            case R.id.admin:
                actionBar.setTitle("Admin Contact");
                changeFragmentView(new AdminContact());
                return true;

        }
        return true;
    }


}
