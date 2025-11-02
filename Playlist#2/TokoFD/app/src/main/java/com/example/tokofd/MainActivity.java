package com.example.tokofd;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    // Map item ID ke Fragment
    private final Map<Integer, Fragment> fragmentMap = new HashMap<>();

    // Simpan fragment aktif
    private Fragment activeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        // Inisialisasi map
        fragmentMap.put(R.id.nav_home, new HomeFragment());
        fragmentMap.put(R.id.nav_search, new SearchFragment());
        fragmentMap.put(R.id.nav_notifications, new NotificationsFragment());
        fragmentMap.put(R.id.nav_profile, new ProfileFragment());

        // Set fragment default
        if (savedInstanceState == null) {
            activeFragment = fragmentMap.get(R.id.nav_home);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.nav_host_fragment, activeFragment)
                    .commit();
        }

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            // Tangani tombol cart sebagai pengecualian (karena activity)
            if (itemId == R.id.nav_cart) {
                startActivity(new Intent(MainActivity.this, CartActivity.class));
                return true;
            }

            Fragment selectedFragment = fragmentMap.get(itemId);

            if (selectedFragment != null && selectedFragment != activeFragment) {
                getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                        .replace(R.id.nav_host_fragment, selectedFragment)
                        .commit();
                activeFragment = selectedFragment;
            }

            return true;
        });
    }
}
