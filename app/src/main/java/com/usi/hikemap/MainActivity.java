package com.usi.hikemap;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.usi.hikemap.databinding.ActivityMainBinding;
import com.usi.hikemap.ui.fragment.GoFragment;
import com.usi.hikemap.ui.fragment.ProfileFragment;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    MeowBottomNavigation bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        bottomNavigation = findViewById(R.id.bottomNavigation);
        bottomNavigation.show(2, true); // show go fragment first

        bottomNavigation.add(new MeowBottomNavigation.Model(1, R.drawable.icon_search));
        bottomNavigation.add(new MeowBottomNavigation.Model(2, R.drawable.icon_profile));
        bottomNavigation.add(new MeowBottomNavigation.Model(3, R.drawable.icon_go));

        meowBottomNavigation();
    }

    private void meowBottomNavigation() {
        bottomNavigation.setOnClickMenuListener(model -> {
            switch (model.getId()) {
                case 1:
                    replaceFragment(new ProfileFragment());
                    break;
                case 2:
                    replaceFragment(new GoFragment());
                    break;
                case 3:
                    replaceFragment(new ProfileFragment());
                    break;
            }
            return null;
        });
    }


    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }

}