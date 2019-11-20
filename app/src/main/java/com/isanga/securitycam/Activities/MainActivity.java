package com.isanga.securitycam.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;
import com.isanga.securitycam.Fragments.Camera;
import com.isanga.securitycam.Fragments.Clips;
import com.isanga.securitycam.Fragments.Home;
import com.isanga.securitycam.Fragments.User;
import com.isanga.securitycam.R;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout; //The menu layout

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Needed to overwrite the default actionbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Sets up menu click events
        NavigationView navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Initializes menu layout
        drawerLayout = findViewById(R.id.menu_drawer);

        //Opens Home fragment when app is first opened
        if(savedInstanceState==null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new Home())
                    .commit();
        }
    }

    //Opens a fragment corresponding to the menu item
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.gesture_menu_home:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new Home())
                        .commit();
                break;
            case R.id.gesture_menu_camera:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new Camera())
                        .commit();
                break;
            case R.id.gesture_menu_clips:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new Clips())
                        .commit();
                break;

            case R.id.gesture_menu_user:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new User())
                        .commit();
                break;
        }
        //Closes menu when an item is clicked
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}