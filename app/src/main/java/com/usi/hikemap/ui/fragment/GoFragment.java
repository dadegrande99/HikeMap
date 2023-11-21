package com.usi.hikemap.ui.fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mapbox.android.gestures.MoveGestureDetector;
import com.mapbox.geojson.Point;
import com.mapbox.maps.CameraOptions;
import com.mapbox.maps.MapView;
import com.mapbox.maps.Style;
import com.mapbox.maps.plugin.LocationPuck2D;
import com.mapbox.maps.plugin.gestures.GesturesUtils;
import com.mapbox.maps.plugin.gestures.OnMoveListener;
import com.mapbox.maps.plugin.locationcomponent.LocationComponentPlugin;
import com.mapbox.maps.plugin.locationcomponent.LocationComponentUtils;
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorBearingChangedListener;
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorPositionChangedListener;
import com.usi.hikemap.R;

public class GoFragment extends Fragment {

    private MapView mapView; //map
    private FloatingActionButton floatingActionButton; // focus location

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_go, container, false);

        mapView = root.findViewById(R.id.mapView);
        floatingActionButton = root.findViewById(R.id.focusLocation);

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            activityResultLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        }

        floatingActionButton.hide();

        //TODO 1: Modify the map style
        mapView.getMapboxMap().loadStyleUri(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {
                mapView.getMapboxMap().setCamera(new CameraOptions.Builder().zoom(17.0).build());
                LocationComponentPlugin locationComponentPlugin = LocationComponentUtils.getLocationComponent(mapView);
                locationComponentPlugin.setEnabled(true);
                LocationPuck2D locationPuck2D = new LocationPuck2D();
                locationPuck2D.setBearingImage(AppCompatResources.getDrawable(requireContext(), R.drawable.baseline_location_on_24));
                locationComponentPlugin.setLocationPuck(locationPuck2D);
                locationComponentPlugin.addOnIndicatorBearingChangedListener(onIndicatorBearingChangedListener);
                locationComponentPlugin.addOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener);
                GesturesUtils.getGestures(mapView).addOnMoveListener(onMoveListener);

                floatingActionButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        locationComponentPlugin.addOnIndicatorBearingChangedListener(onIndicatorBearingChangedListener);
                        locationComponentPlugin.addOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener);
                        GesturesUtils.getGestures(mapView).addOnMoveListener(onMoveListener);
                        floatingActionButton.hide();
                    }
                });
            }
        });

        return root;
    }

    /**
     * ActivityResultLauncher for handling the result of a permission request.
     *
     * This launcher is used to request a specific permission and responds to the result
     * by displaying a toast message if the permission is granted.
     */
    private final ActivityResultLauncher<String> activityResultLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {

                /**
                 * Called when the result of a permission request is received.
                 *
                 * - Displays a toast message if the permission is granted.
                 *
                 * @param result A Boolean indicating whether the permission is granted or not.
                 */
                @Override
                public void onActivityResult(Boolean result) {
                    if (result) {
                        // Display a toast message if the permission is granted
                        Toast.makeText(requireContext(), "Permission granted!", Toast.LENGTH_SHORT).show();
                    }
                }
            });

    /**
     * OnIndicatorBearingChangedListener implementation for handling changes in indicator bearing.
     *
     * This listener is responsible for updating the Mapbox Map camera when the indicator bearing changes.
     *
     * @param v The new bearing value for the indicator.
     */
    private final OnIndicatorBearingChangedListener onIndicatorBearingChangedListener =
            new OnIndicatorBearingChangedListener() {

                /**
                 * Called when the bearing of the indicator changes.
                 *
                 * - Updates the Mapbox Map camera to the new bearing value.
                 *
                 * @param v The new bearing value for the indicator.
                 */
                @Override
                public void onIndicatorBearingChanged(double v) {
                    // Update the Mapbox Map camera with the new bearing value
                    mapView.getMapboxMap().setCamera(new CameraOptions.Builder().bearing(v).build());
                }
            };

    /**
     * Listener for handling changes in the position of the indicator.
     *
     * This listener is responsible for updating the Mapbox Map camera and handling gestures
     * when the indicator position changes.
     *
     * @param point The new position of the indicator on the map.
     *              The camera is centered around this point, and the zoom level is set to 17.0.
     */
    private final OnIndicatorPositionChangedListener onIndicatorPositionChangedListener =
            new OnIndicatorPositionChangedListener() {
                @Override
                public void onIndicatorPositionChanged(@NonNull Point point) {
                    // Update the Mapbox Map camera
                    mapView.getMapboxMap().setCamera(
                            new CameraOptions.Builder().center(point).zoom(17.0).build());

                    // Handle gestures by setting the focal point to the pixel coordinates of the indicator position
                    GesturesUtils.getGestures(mapView)
                            .setFocalPoint(mapView.getMapboxMap().pixelForCoordinate(point));
                }
            };

    /**
     * OnMoveListener implementation for handling move gestures on the map.
     *
     * This listener is responsible for responding to the beginning of move gestures,
     * updating the UI and removing certain location-related listeners temporarily.
     *
     * @param moveGestureDetector The detector for move gestures on the map.
     */
    private final OnMoveListener onMoveListener = new OnMoveListener() {

        /**
         * Called when a move gesture begins.
         *
         * - Removes the indicator-bearing changed listener.
         * - Removes the indicator-position changed listener.
         * - Removes itself as a move listener.
         * - Shows a floating action button.
         *
         * @param moveGestureDetector The detector for move gestures on the map.
         */
        @Override
        public void onMoveBegin(@NonNull MoveGestureDetector moveGestureDetector) {
            // Remove certain location-related listeners during move gesture
            LocationComponentUtils.getLocationComponent(mapView)
                    .removeOnIndicatorBearingChangedListener(onIndicatorBearingChangedListener);
            LocationComponentUtils.getLocationComponent(mapView)
                    .removeOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener);
            GesturesUtils.getGestures(mapView).removeOnMoveListener(onMoveListener);

            // Show the floating action button
            floatingActionButton.show();
        }

        /**
         * Called during an ongoing move gesture.
         *
         * @param moveGestureDetector The detector for move gestures on the map.
         * @return Always returns false, indicating that the default behavior should continue.
         */
        @Override
        public boolean onMove(@NonNull MoveGestureDetector moveGestureDetector) {
            // Get the current bearing of the map
            double currentBearing = mapView.getMapboxMap().getCameraState().getBearing();

            // Calculate the bearing change based on the horizontal movement
            double bearingChange = -moveGestureDetector.getLastDistanceX() * 0.05; // You can adjust the factor as needed

            // Update the map bearing
            mapView.getMapboxMap().setCamera(new CameraOptions.Builder().bearing(currentBearing + bearingChange).build());

            // Return true to consume the gesture
            return true;
        }


        /**
         * Called when a move gesture ends.
         *
         * This method is currently empty, and no specific actions are taken.
         *
         * @param moveGestureDetector The detector for move gestures on the map.
         */
        @Override
        public void onMoveEnd(@NonNull MoveGestureDetector moveGestureDetector) {
            // This method is currently empty
        }
    };
}





























