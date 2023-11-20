/*
package com.usi.hikemap.ui.fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.mapbox.maps.CameraOptions;
import com.mapbox.maps.MapView;
import com.mapbox.maps.Style;
import com.mapbox.maps.extension.style.expressions.dsl.generated.interpolate;
import com.mapbox.maps.plugin.LocationPuck2D;
import com.mapbox.maps.plugin.gestures.OnMoveListener;
import com.mapbox.maps.plugin.gestures.gestures;
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorBearingChangedListener;
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorPositionChangedListener;
import com.mapbox.maps.plugin.locationcomponent.location;
import com.usi.hikemap.R;
import com.usi.hikemap.databinding.FragmentGoBinding;
import com.usi.hikemap.ui.viewmodel.GoViewModel;
import com.usi.hikemap.utils.LocationPermissionHelper;

import java.lang.ref.WeakReference;

public class GoFragment extends Fragment {

    private LocationPermissionHelper locationPermissionHelper;
    private OnIndicatorBearingChangedListener onIndicatorBearingChangedListener;
    private OnIndicatorPositionChangedListener onIndicatorPositionChangedListener;
    private OnMoveListener onMoveListener;
    private FragmentGoBinding binding;
    private MapView mapView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                     ViewGroup container, Bundle savedInstanceState) {

        GoViewModel goViewModel = new ViewModelProvider(this).get(GoViewModel.class);

        binding = FragmentGoBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        mapView = new MapView(requireContext());

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        locationPermissionHelper = new LocationPermissionHelper(new WeakReference<>(requireActivity()));
        locationPermissionHelper.checkPermissions(this::onMapReady);
    }

    private void onMapReady() {
        mapView.getMapboxMap().setCamera(
            CameraOptions.Builder()
                    .zoom(14.0)
                    .build()
        );
        mapView.getMapboxMap().loadStyleUri(
            Style.MAPBOX_STREETS,
            style -> {
                initLocationComponent();
                setupGesturesListener();
            });
    }

    private void setupGesturesListener() {
        mapView.gestures.addOnMoveListener(onMoveListener);
    }

    private void initLocationComponent() {
        LocationPuck2D locationPuck2D = new LocationPuck2D(
        AppCompatResources.getDrawable(requireContext(), R.drawable.mapbox_user_puck_icon),
        AppCompatResources.getDrawable(requireContext(), R.drawable.mapbox_user_icon_shadow),
        new Interpolate[]{
                Interpolate.linear(),
                Interpolate.zoom(),
                Interpolate.stop(0.0, 0.6),
                Interpolate.stop(20.0, 1.0)
            }
        );

        mapView.location.applySettings(locationSettings -> {
            locationSettings.enabled(true);
            locationSettings.locationPuck(locationPuck2D);
        });

        mapView.location.addOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener);
        mapView.location.addOnIndicatorBearingChangedListener(onIndicatorBearingChangedListener);
    }

    private void onCameraTrackingDismissed() {
        Toast.makeText(requireContext(), "onCameraTrackingDismissed", Toast.LENGTH_SHORT).show();
        mapView.location.removeOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener);
        mapView.location.removeOnIndicatorBearingChangedListener(onIndicatorBearingChangedListener);
        mapView.gestures.removeOnMoveListener(onMoveListener);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mapView.location.removeOnIndicatorBearingChangedListener(onIndicatorBearingChangedListener);
        mapView.location.removeOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener);
        mapView.gestures.removeOnMoveListener(onMoveListener);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        locationPermissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
*/

package com.usi.hikemap.ui.fragment;

import android.Manifest;
import android.content.pm.PackageManager;
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

    private MapView mapView;
    private FloatingActionButton floatingActionButton;

    private final ActivityResultLauncher<String> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
        @Override
        public void onActivityResult(Boolean result) {
            if (result) {
                Toast.makeText(requireContext(), "Permission granted!", Toast.LENGTH_SHORT).show();
            }
        }
    });

    private final OnIndicatorBearingChangedListener onIndicatorBearingChangedListener = new OnIndicatorBearingChangedListener() {
        @Override
        public void onIndicatorBearingChanged(double v) {
            mapView.getMapboxMap().setCamera(new CameraOptions.Builder().bearing(v).build());
        }
    };

    private final OnIndicatorPositionChangedListener onIndicatorPositionChangedListener = new OnIndicatorPositionChangedListener() {
        @Override
        public void onIndicatorPositionChanged(@NonNull Point point) {
            mapView.getMapboxMap().setCamera(new CameraOptions.Builder().center(point).zoom(20.0).build());
            GesturesUtils.getGestures(mapView).setFocalPoint(mapView.getMapboxMap().pixelForCoordinate(point));
        }
    };

    private final OnMoveListener onMoveListener = new OnMoveListener() {
        @Override
        public void onMoveBegin(@NonNull MoveGestureDetector moveGestureDetector) {
            LocationComponentUtils.getLocationComponent(mapView).removeOnIndicatorBearingChangedListener(onIndicatorBearingChangedListener);
            LocationComponentUtils.getLocationComponent(mapView).removeOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener);
            GesturesUtils.getGestures(mapView).removeOnMoveListener(onMoveListener);
            floatingActionButton.show();
        }

        @Override
        public boolean onMove(@NonNull MoveGestureDetector moveGestureDetector) {
            return false;
        }

        @Override
        public void onMoveEnd(@NonNull MoveGestureDetector moveGestureDetector) {

        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_go, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mapView = view.findViewById(R.id.mapView);
        floatingActionButton = view.findViewById(R.id.focusLocation);

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            activityResultLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        }

        floatingActionButton.hide();
        mapView.getMapboxMap().loadStyleUri(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {
                mapView.getMapboxMap().setCamera(new CameraOptions.Builder().zoom(20.0).build());
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
    }
}





























