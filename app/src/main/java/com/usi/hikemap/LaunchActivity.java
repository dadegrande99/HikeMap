package com.usi.hikemap;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;

public class LaunchActivity extends AppCompatActivity {

    FirebaseAuth fAuth;

    String TAG = "LaunchActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        fAuth = FirebaseAuth.getInstance();

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container_view_launch);
        NavController navController = navHostFragment.getNavController();

        if(fAuth.getCurrentUser() != null) {
            navController.navigate(R.id.action_global_mainActivity);
            //startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }
        else{
            navController.navigate(R.id.action_global_authenticationActivity);
            //startActivity(new Intent(getApplicationContext(), AuthenticationActivity.class));
            finish();
        }
    }
}