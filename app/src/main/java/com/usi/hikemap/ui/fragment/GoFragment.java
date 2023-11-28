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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

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


/**
 * Fragment class for displaying a Google Map and tracking the user's location with a polyline.
 */
public class GoFragment extends Fragment implements OnMapReadyCallback, LocationListener {

    private GoogleMap mMap;
    private PolylineOptions polylineOptions;
    private Polyline polyline;
    private Location lastLocation;
    private FloatingActionButton fStartButton;

    FusedLocationProviderClient fusedLocationProviderClient;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_go, container, false);

        fStartButton = rootView.findViewById(R.id.startButton);
        fStartButton.setOnClickListener(new View.OnClickListener() {

            // TODO 1: create environment for stats
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Start stats", Toast.LENGTH_SHORT).show();
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


    /**
     * Callback method invoked when the map is ready to be used.
     * This method is called when the GoogleMap instance is available.
     *
     * @param googleMap The GoogleMap instance representing the map.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        // Assign the GoogleMap instance to the local variable mMap
        mMap = googleMap;

        // Enable the location layer. Request permission if needed.
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            // Enable the "My Location" layer on the map
            mMap.setMyLocationEnabled(true);
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());
            zoomToUserLocation();

            // Request location updates from the GPS provider
            LocationManager locationManager = (LocationManager) requireContext().getSystemService(Context.LOCATION_SERVICE);
            if (locationManager != null) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10, this);
            }
        } else {
            // Request permission if it's not granted
            ActivityCompat.requestPermissions(requireActivity(),
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
        if (lastLocation == null) {
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

/*
map:cameraBearing="112.5"
    map:cameraTilt="30"
    map:cameraZoom="13"
    map:mapType="normal"
    map:uiCompass="false"
    map:uiRotateGestures="true"
    map:uiScrollGestures="true"
    map:uiTiltGestures="true"
    map:uiZoomControls="false"
    map:uiZoomGestures="true"
 */




