package com.example.ap2;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.ap2.R;
import com.google.android.material.navigation.NavigationView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/*import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;*/


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    DrawerLayout drawer;
    NavigationView navigationView;
    ActionBarDrawerToggle toggle;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer);
        navigationView = findViewById(R.id.nested);
        navigationView.setNavigationItemSelectedListener(this);

        //getSupportActionBar().setTitle("Dashboard");

        toggle = new ActionBarDrawerToggle(this,drawer,toolbar,R.string.open,R.string.close);
        drawer.addDrawerListener(toggle);
        toggle.setDrawerIndicatorEnabled(true);
        toggle.syncState();

        //mainFragment = new MainFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.container_fragment, new com.example.ap2.AboutF());
        fragmentTransaction.commit();// add the fragment*/



    }

   /* @Override
    public void onButtonSelected() {
        Toast.makeText(this, "Start New Activity. (Static Fragment are used)", Toast.LENGTH_SHORT).show();
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentContainer,new SecondFragment());
        fragmentTransaction.commit();

    }*/






    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        drawer.closeDrawer(GravityCompat.START);
        if(menuItem.getItemId() == R.id.count){
            loadFragment(new com.example.ap2.CountF());
        }
        if(menuItem.getItemId() == R.id.about){
            loadFragment(new com.example.ap2.AboutF());
        }
        if(menuItem.getItemId() == R.id.symptoms){
            loadFragment(new com.example.ap2.SymptomsF());
        }
        if(menuItem.getItemId() == R.id.donate){
            loadFragment(new com.example.ap2.DonateF());
        }
        if(menuItem.getItemId() == R.id.precautions){
            loadFragment(new com.example.ap2.PrecautionsF());
        }
        if(menuItem.getItemId() == R.id.ref){
            loadFragment(new com.example.ap2.RefrencesF());
        }
        if(menuItem.getItemId() == R.id.tollfree){
            loadFragment(new com.example.ap2.TollfnF());
        }

        return true;
    }

    private void loadFragment(Fragment secondFragment) {
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container_fragment,secondFragment);
        fragmentTransaction.commit();
    }


}