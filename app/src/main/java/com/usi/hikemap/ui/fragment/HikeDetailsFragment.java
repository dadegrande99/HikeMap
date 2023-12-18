package com.usi.hikemap.ui.fragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuHost;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import android.app.ActionBar;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.usi.hikemap.R;
import com.usi.hikemap.utils.ParcelableRouteList;
import com.usi.hikemap.model.Route;
import com.usi.hikemap.ui.viewmodel.HikeDetailsViewModel;

import java.util.List;

public class HikeDetailsFragment extends Fragment implements OnMapReadyCallback {

    private static final String ARG_ROUTE_LIST = "List<Object>";
    private static String TAG = "HikeDetailsFragment";
    private String userId;
    private HikeDetailsViewModel mGoDetailsViewModel;
    MeowBottomNavigation bottomNavigation;

    private GoogleMap mMap;
    private PolylineOptions polylineOptions;
    private Polyline polyline;

    TextView kms, up, down, time, calories, steps;


    public HikeDetailsFragment() {
        // Required empty public constructor
    }

    public static HikeDetailsFragment newInstance(ParcelableRouteList parcelableRouteList) {
        HikeDetailsFragment fragment = new HikeDetailsFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_ROUTE_LIST, parcelableRouteList);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mGoDetailsViewModel = new ViewModelProvider(requireActivity()).get(HikeDetailsViewModel.class);


        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.arrow_left);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Perform the redirection to the main fragment here
                getActivity().onBackPressed();
            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_hike_details, container, false);

        bottomNavigation = getActivity().findViewById(R.id.bottomNavigation);
        bottomNavigation.setVisibility(View.GONE);


        kms = root.findViewById(R.id.km_value_hikeDetails);
        down = root.findViewById(R.id.down_value_hikeDetails);
        up = root.findViewById(R.id.up_value_hikeDetails);
        time = root.findViewById(R.id.chronometer_hikeDetails);
        calories = root.findViewById(R.id.kal_value_hikeDetails);
        steps = root.findViewById(R.id.steps_value_hikeDetails);

        steps.setText("0");
        kms.setText("0");
        time.setText("00:00");
        calories.setText("0");
        down.setText("0");
        up.setText("0");

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapHike);
        if (mapFragment == null) {
            mapFragment = SupportMapFragment.newInstance();
            getChildFragmentManager().beginTransaction().replace(R.id.mapHike, mapFragment).commit();
        }


        mapFragment.getMapAsync(this);

        return root;
    }


    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        mMap.getUiSettings().setAllGesturesEnabled(false);

        // Initialize PolylineOptions
        polylineOptions = new PolylineOptions();
        polylineOptions.color(Color.RED);

        // Get the receivedRoutes from the arguments
        Bundle args = getArguments();
        if (args != null) {
            ParcelableRouteList parcelableRouteList = args.getParcelable(ARG_ROUTE_LIST);
            if (parcelableRouteList != null) {
                List<Route> receivedRoutes = parcelableRouteList.getRouteList();

                // Loop through the receivedRoutes
                for (int i = 0; i < receivedRoutes.size() - 1; i++) {

                    Route currentRoute = receivedRoutes.get(i);
                    Route nextRoute = receivedRoutes.get(i + 1);

                    Location currentLocation = new Location("currentLocation");
                    currentLocation.setLatitude(currentRoute.getLatitude());
                    currentLocation.setLongitude(currentRoute.getLongitude());
                    currentLocation.setAltitude(currentRoute.getAltitude());

                    Location nextLocation = new Location("nextLocation");
                    nextLocation.setLatitude(nextRoute.getLatitude());
                    nextLocation.setLongitude(nextRoute.getLongitude());
                    nextLocation.setAltitude(nextRoute.getAltitude());

                    double distance = currentLocation.distanceTo(nextLocation) / 1000;
                    distance += Double.parseDouble(this.kms.getText().toString());
                    this.kms.setText(String.valueOf(Math.round(distance * 1000.0) / 1000.0));


                    double elevation = nextLocation.getAltitude() - currentLocation.getAltitude();
                    if (elevation > 0) {
                        double tmpUp = Double.parseDouble(this.up.getText().toString());
                        tmpUp += elevation;
                        this.up.setText(String.valueOf(Math.round(tmpUp * 10.0) / 10.0));
                    } else if (elevation < 0) {
                        double tmpDown = Double.parseDouble(this.down.getText().toString());
                        tmpDown -= elevation;
                        this.down.setText(String.valueOf(Math.round(tmpDown * 10.0) / 10.0));
                    }

                    int steps = receivedRoutes.size();
                    this.steps.setText(String.valueOf(steps));

                    // Add a new point to the PolylineOptions
                    polylineOptions.add(new LatLng(receivedRoutes.get(i).getLongitude(), receivedRoutes.get(i).getLatitude()));
                }
            }
        }

        // Add the PolylineOptions to the GoogleMap
        polyline = mMap.addPolyline(polylineOptions);

        // Move the camera to the first point of the PolylineOptions
        if (polylineOptions.getPoints().size() > 0) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(polylineOptions.getPoints().get(0), 18));
        }
    }

}