package com.example.homenursingservice.Patient;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.homenursingservice.Patient.Dashboard;
import com.example.homenursingservice.R;
import com.example.homenursingservice.SharedPrefManager;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class User_Dashboard extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    DrawerLayout drawer;
    ImageView menu,menu2;
    ActionBarDrawerToggle toggle;
    Toolbar toolbar;
    FrameLayout frameLayout;
    FragmentManager fragmentManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_dashboard);

        menu=findViewById(R.id.menu);
        menu2=findViewById(R.id.menu_icon2);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               if(drawer.isDrawerOpen(GravityCompat.START)){
                   drawer.closeDrawer(GravityCompat.START);
               }
               else {
                   drawer.openDrawer(GravityCompat.START);
               }
            }
        });
        menu2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PopupMenu popup = new PopupMenu(User_Dashboard.this, menu2);
                popup.getMenuInflater().inflate(R.menu.user_dashboard_menu, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {

                        if(item.getItemId()==R.id.log_out){
                            SharedPrefManager.getInstance(getApplicationContext()).logout();
                            FirebaseAuth.getInstance().signOut();
                            finish();
                        }

                        return true;
                    }
                });
                popup.show();
            }
        });

        changeFragmentView(new Dashboard());


    }
    public void changeFragmentView(Fragment fragment){

        fragmentManager =getSupportFragmentManager();
        fragmentManager.popBackStack();
        int count = fragmentManager.getBackStackEntryCount();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout,fragment)
                .addToBackStack(null).commit();
        drawer.closeDrawer(GravityCompat.START);
    }




    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        return false;
    }
}
