package com.usi.hikemap;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.usi.hikemap.databinding.ActivityMainBinding;
import com.usi.hikemap.ui.fragment.GoFragment;
import com.usi.hikemap.ui.fragment.ProfileFragment;
import com.usi.hikemap.ui.fragment.SearchFragment;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    private GoogleMap mMap;

    MeowBottomNavigation bottomNavigation;

    String userId;

    FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        bottomNavigation = findViewById(R.id.bottomNavigation);
        bottomNavigation.show(2, true); // show go fragment first

        bottomNavigation.add(new MeowBottomNavigation.Model(1, R.drawable.icon_search));
        bottomNavigation.add(new MeowBottomNavigation.Model(2, R.drawable.icon_go));
        bottomNavigation.add(new MeowBottomNavigation.Model(3, R.drawable.icon_profile));

        meowBottomNavigation();

        //userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        replaceFragment(new GoFragment());
        //onMapReady(mMap);

    }



    private void meowBottomNavigation() {
        bottomNavigation.setOnClickMenuListener(model -> {
            switch (model.getId()) {
                case 1:
                    replaceFragment(new SearchFragment());
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

    /**@Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            replaceFragment(new GoFragment());
        }
    }*/


    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }



    //@Override
    /**public void onMapReady(GoogleMap googleMap) {
        // Assign the GoogleMap instance to the local variable mMap
        mMap = googleMap;

        // Enable the location layer. Request permission if needed.
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            // Enable the "My Location" layer on the map
            mMap.setMyLocationEnabled(true);
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
            zoomToUserLocation();

            // Request location updates from the GPS provider
            LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            if (locationManager != null) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000L, 10F, (LocationListener) this);
            }
        } else {
            // Request permission if it's not granted
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    1);
        }
    }

    private void zoomToUserLocation() {
        @SuppressLint("MissingPermission") Task<Location> locationTask = fusedLocationProviderClient.getLastLocation();
        locationTask.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 20));
                //mMap.addMarker(new MarkerOptions().position(latLng));
            }
        });
    }*/

}