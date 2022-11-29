package com.wojtech.drinonator;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Toast;

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
            int nav_current = bottomNavigationView.getSelectedItemId();
            int nav_next = item.getItemId();
            if(nav_next != nav_current && nav_next == R.id.search) {
                setFragment(SearchFragment.newInstance());
                return true;
            }else if(nav_next == R.id.home) {
                setFragment(DrinkFragment.newInstance(null));
                return true;
            }else if(nav_next != nav_current && nav_next == R.id.favourites) {
                setFragment(FavouriteFragment.newInstance());
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
        FragmentManager fragmentManager = this.getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.main_container, fragment)
                .addToBackStack("main_stack")
                .commit();
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            getSupportFragmentManager().popBackStackImmediate();
        } else {
            Toast.makeText(getApplicationContext(), getResources().getText(R.string.confirm_exit), Toast.LENGTH_SHORT).show();
            super.onBackPressed();
        }
    }
}