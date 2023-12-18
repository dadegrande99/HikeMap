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

public class HikeDetailsFragment extends Fragment {

    private static final String ARG_ROUTE_LIST = "List<Object>";
    private static String TAG = "HikeDetailsFragment";
    private String userId;
    private String routeId;
    private HikeDetailsViewModel mHikeDetailsViewModel;
    MeowBottomNavigation bottomNavigation;

    TextView kms, up, down, time, calories, steps;


    public HikeDetailsFragment() {
        // Required empty public constructor
    }

    public static HikeDetailsFragment newInstance(String routeId) {
        HikeDetailsFragment fragment = new HikeDetailsFragment();
        Bundle args = new Bundle();
        //args.putParcelable(ARG_ROUTE_LIST, parcelableRouteList);
        args.putString("routeId", routeId);
        //this.routeId = routeId;
        Log.d("ProvaHikeDetails", "Args: " + args.toString());
        fragment.setArguments(args);
        Log.d("ProvaHikeDetails", "onCreateView4: ");
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Log.d("ProvaHikeDetails", "onCreateView1: ");
        mHikeDetailsViewModel = new ViewModelProvider(requireActivity()).get(HikeDetailsViewModel.class);

        Log.d("ProvaHikeDetails", "onCreateView2: ");
        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.arrow_left);
        Log.d("ProvaHikeDetails", "onCreateView3: ");
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

        Bundle args = getArguments();

        Log.d("ProvaHikeDetails", "Args2: " + args.toString());
        if (args != null) {
            routeId = args.getString("routeId");
            Log.d("ProvaHikeDetails", "onMapReady: " + routeId);
            // recupera i dati della route dal db remoto
            mHikeDetailsViewModel.readRoute(routeId).observe(getViewLifecycleOwner(), route -> {
                //Log.d(TAG, "onCreateView: " + route.toString());
                if (route != null) {
                    // Do something with the receivedRoutes
                    Log.d("ProvaHikeDetails", "onCreateView RouteId: " + route.toString());

                    // Loop through the receivedRoutes
                    for (int i = 0; i < route.size() - 1; i++) {

                        Route currentRoute = route.get(i);
                        Route nextRoute = route.get(i + 1);

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

                        int steps = route.size();
                        this.steps.setText(String.valueOf(steps));

                        // timing


                    }




                }
            });


        }

        return root;
    }


}