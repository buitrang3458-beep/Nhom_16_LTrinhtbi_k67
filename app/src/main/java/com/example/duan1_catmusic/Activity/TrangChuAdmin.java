package com.example.duan1_catmusic.Activity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.duan1_catmusic.R;
import com.example.duan1_catmusic.fragment.LoainhacAdmin_fm;
import com.example.duan1_catmusic.fragment.NghesiAdmin_fm;
import com.example.duan1_catmusic.fragment.Premium_fm;
import com.example.duan1_catmusic.fragment.Thuvien_fm;
import com.example.duan1_catmusic.fragment.Tim_kiem_fm;
import com.example.duan1_catmusic.fragment.TrangchuAdmin_fm;
import com.example.duan1_catmusic.fragment.Trangchu_fm;
import com.example.duan1_catmusic.fragment.UngdungAdmin_fm;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class TrangChuAdmin extends AppCompatActivity {

    BottomNavigationView bottomNavigationViewAdmin;
    FrameLayout frameLayoutAdmin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trangchu_admin);

        bottomNavigationViewAdmin = findViewById(R.id.bottom_nav_view_admin);
        frameLayoutAdmin = findViewById(R.id.frameLayoutAdmin);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);


        bottomNavigationViewAdmin.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.navHomeAdmin) {
                    loadFragment(new TrangchuAdmin_fm(), false);
                } else if (itemId == R.id.navNghesi) {
                    loadFragment(new NghesiAdmin_fm(), false);
                } else if (itemId == R.id.navLoainhac) {
                    loadFragment(new LoainhacAdmin_fm(), false);
                } else {
                    loadFragment(new UngdungAdmin_fm(), false);
                }
                return true;
            }
        });
        loadFragment(new TrangchuAdmin_fm(), true);
    }
    private void loadFragment(Fragment fragment, boolean isAppInitialized) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (isAppInitialized) {
            fragmentTransaction.add(R.id.frameLayoutAdmin, fragment);
        } else {
            fragmentTransaction.replace(R.id.frameLayoutAdmin, fragment);
        }
        fragmentTransaction.commit();



    }
}