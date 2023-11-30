package com.usi.hikemap.ui.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.usi.hikemap.R;
import com.usi.hikemap.ui.viewmodel.GoViewModel;

import android.view.animation.TranslateAnimation;


/**
 * Fragment class for displaying a Google Map and tracking the user's location with a polyline.
 */
public class GoFragment extends Fragment implements OnMapReadyCallback, LocationListener {

    private GoogleMap mMap;
    private PolylineOptions polylineOptions;
    private Polyline polyline;
    private Location lastLocation;
    private FloatingActionButton fStartButton;

    //private GoViewModel viewModel;

    FrameLayout infoContainer;

    TextView steps;
    TextView km;
    TextView calories;
    TextView up;
    TextView down;

    Chronometer chronometer;
    Handler handler;
    private boolean isResume;
    long tmillisec, tStart, tBuff, tUpdate = 0L;
    int sec, min, millisec;


    FusedLocationProviderClient fusedLocationProviderClient;

    @SuppressLint("MissingPermission")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_go, container, false);
        infoContainer = rootView.findViewById(R.id.info);

        steps = rootView.findViewById(R.id.steps_value);
        km = rootView.findViewById(R.id.km_value);
        calories = rootView.findViewById(R.id.kal_value);
        down = rootView.findViewById(R.id.down_value);
        up = rootView.findViewById(R.id.up_value);
        chronometer = rootView.findViewById(R.id.chronometer);

        handler = new Handler();

        // Set the visibility of the info container to GONE
        infoContainer.setVisibility(View.GONE);

        fStartButton = rootView.findViewById(R.id.startButton);
        Log.d("GoFragment", "onCreateView: ");
        fStartButton.setOnClickListener(new View.OnClickListener() {


            // TODO 1: create environment for stats
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Start stats", Toast.LENGTH_SHORT).show();


                infoContainer.setVisibility(View.VISIBLE);
                infoContainer.requestLayout();
                fStartButton.setVisibility(View.GONE);

                steps.setText("0");
                km.setText("0");
                calories.setText("0");
                down.setText("0");
                up.setText("0");

                tStart = System.currentTimeMillis();
                handler.postDelayed(runnable, 5000);
                chronometer.start();
            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment == null) {
            mapFragment = SupportMapFragment.newInstance();
            getChildFragmentManager().beginTransaction().replace(R.id.map, mapFragment).commit();
        }

        mapFragment.getMapAsync(this);


        return rootView;
    }

    public Runnable runnable = new Runnable() {
        @Override
        public void run() {
            tmillisec = System.currentTimeMillis() - tStart;
            tUpdate = tBuff + tmillisec;
            sec = (int) (tUpdate / 1000);
            min = sec / 60;
            sec = sec % 60;
            chronometer.setText(String.format("%02d", min) + ":" + String.format("%02d", sec));
            handler.postDelayed(this, 60);
        }
    };


    /**
     * Callback method invoked when the map is ready to be used.
     * This method is called when the GoogleMap instance is available.
     *
     * @param googleMap The GoogleMap instance representing the map.
     */
    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        // Assign the GoogleMap instance to the local variable mMap
        mMap = googleMap;

        // Enable the location layer. Request permission if needed.
        /**if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {*/
            // Enable the "My Location" layer on the map
        mMap.setMyLocationEnabled(true);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());
        zoomToUserLocation();

        // Request location updates from the GPS provider
        LocationManager locationManager = (LocationManager) requireContext().getSystemService(Context.LOCATION_SERVICE);
        if (locationManager != null) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10, this);
        }
        /**} else {
            // Request permission if it's not granted
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    1);
        }*/
    }


    private void zoomToUserLocation() {
        @SuppressLint("MissingPermission") Task<Location> locationTask = fusedLocationProviderClient.getLastLocation();
        locationTask.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));
                //mMap.addMarker(new MarkerOptions().position(latLng));
            }
        });
    }




    /**
     * Callback method invoked when the user's location has changed.
     * This method is called when a new location update is received.
     *
     * @param location The new Location representing the user's current location.
     */
    @Override
    public void onLocationChanged(Location location) {
        // Center the camera on the user's location when the first location is received
        //@SuppressLint("MissingPermission") Task<Location> locationTask = fusedLocationProviderClient.getLastLocation();
        if (location == null) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 15));
        }

        // Draw polyline on the map
        if (polylineOptions == null) {
            // Initialize PolylineOptions with specified color and width
            polylineOptions = new PolylineOptions().color(Color.BLUE).width(10);
        }

        // Add the new location to the polyline
        polylineOptions.add(new LatLng(location.getLatitude(), location.getLongitude()));

        // Remove the previous polyline if it exists
        if (polyline != null) {
            polyline.remove();
        }

        // Add the updated polyline to the map
        polyline = mMap.addPolyline(polylineOptions);

        // Update the last known location
        lastLocation = location;
    }


    /**
     * Callback method invoked when the user responds to a permission request.
     * This method is called when the user grants or denies a requested permission.
     *
     * @param requestCode The request code passed when requesting permissions.
     * @param permissions The requested permissions.
     * @param grantResults The results of the permission requests.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // Check if the request code matches the one used when requesting the permission
        if (requestCode == 1 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Permission granted, initialize the map
            onMapReady(mMap);
        }
    }


}
