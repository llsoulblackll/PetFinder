package com.petfinder.ui.activity;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import com.petfinder.R;
import com.petfinder.ui.fragment.DashboardFragment;
import com.petfinder.ui.fragment.NewPetFragment;
import com.petfinder.ui.fragment.SettingsFragment;
import com.petfinder.util.Util;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView navigationView;

    private FragmentManager fragmentManager;

    private BottomNavigationView.OnNavigationItemSelectedListener navigationListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch(item.getItemId()){
                case R.id.menuItemDashboard:
                    Util.FragmentHelper.navigate(DashboardFragment.class,
                            R.id.fragmentContainer,
                            getString(R.string.dashboard_frag), fragmentManager);
                    break;
                case R.id.menuItemNewPet:
                    Util.FragmentHelper.navigate(NewPetFragment.class,
                            R.id.fragmentContainer,
                            getString(R.string.new_pet_frag), fragmentManager);
                    break;
                case R.id.menuItemSettings:
                    //ID SINCE A LAYOUT RESOURCE IS NOT NECESSARILY PRESENT
                    Util.FragmentHelper.navigate(SettingsFragment.class,
                            R.id.fragmentContainer,
                            getString(R.string.settings_frag), fragmentManager);
                    break;
            }
            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragmentManager = getSupportFragmentManager();

        navigationView = findViewById(R.id.navigation);
        navigationView.setOnNavigationItemSelectedListener(navigationListener);
        Util.FragmentHelper.navigate(DashboardFragment.class,
                R.id.fragmentContainer,
                getString(R.string.dashboard_frag),
                fragmentManager);
    }
}
