package com.wojtech.drinonator;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.annotation.SuppressLint;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

// icons author: Freepik
// icons https://www.flaticon.com/packs/cocktails-32?word=alcohol

public class MainActivity extends AppCompatActivity {

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView bottomNavigationView;
        try {
            ActionBar action_bar = this.getSupportActionBar();
            assert action_bar != null;
            action_bar.hide();
        }catch (NullPointerException e){
            e.printStackTrace();
        }
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch(item.getItemId()){
                case R.id.search:
                    setFragment(new SearchFragment());
                    return true;
                case R.id.home:
                    setFragment(new HomeFragment());
                    return true;
                case R.id.favourites:
                    setFragment(new FavouriteFragment());
                    return true;
            }
            return false;
        });
        bottomNavigationView.setSelectedItemId(R.id.home);
    }

    /**
     * Set fragment to be displayed in main container
     *
     * @param fragment fragment to be displayed
     *
     */
    public void setFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.main_container, fragment)
                .commit();
    }
}