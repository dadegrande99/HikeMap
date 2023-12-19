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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HikeDetailsFragment extends Fragment {

    private static final String ARG_ROUTE_LIST = "List<Object>";
    private static String TAG = "HikeDetailsFragment";
    private String userId;
    private String routeId;
    private HikeDetailsViewModel mHikeDetailsViewModel;
    MeowBottomNavigation bottomNavigation;

    TextView kms, up, down, time, calories, steps, speed, intervals;


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
        speed = root.findViewById(R.id.speedValue);
        intervals = root.findViewById(R.id.intervalsValue);

        steps.setText("0");
        kms.setText("0");
        time.setText("00:00");
        calories.setText("0");
        down.setText("0");
        up.setText("0");
        speed.setText("0");
        intervals.setText("0");


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

                    int subroute = route.get(0).getSubRoute();
                    int subrouteCount = 0;
                    double distance = 0.0;
                    double uphill = 0.0;
                    double downhill = 0.0;
                    SimpleDateFormat sdf  = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS", Locale.getDefault());
                    Date date1 = null;
                    try {
                        date1 = sdf.parse(route.get(0).getTimestamp());
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                    int totaltime = 0;
                    Route previousRoute = route.get(0);

                    for (int i = 1; i < route.size() - 1; i++){
                        Route currentRoute = route.get(i);


                        if (subroute != currentRoute.getSubRoute()){
                            subroute = currentRoute.getSubRoute();
                            subrouteCount++;
                            Date date2 = null;
                            try {
                                date2 = sdf.parse(previousRoute.getTimestamp());
                            } catch (ParseException e) {
                                throw new RuntimeException(e);
                            }
                            totaltime += date2.getTime() - date1.getTime();
                            date1 = date2;
                        }else{
                            Location currentLocation = new Location("currentLocation");
                            currentLocation.setLatitude(currentRoute.getLatitude());
                            currentLocation.setLongitude(currentRoute.getLongitude());
                            currentLocation.setAltitude(currentRoute.getAltitude());

                            Location previousLocation = new Location("previousLocation");
                            previousLocation.setLatitude(previousRoute.getLatitude());
                            previousLocation.setLongitude(previousRoute.getLongitude());
                            previousLocation.setAltitude(previousRoute.getAltitude());

                            distance += previousLocation.distanceTo(currentLocation);

                            if (currentRoute.getAltitude() > previousRoute.getAltitude()){
                                uphill += currentRoute.getAltitude() - previousRoute.getAltitude();
                            } else if (currentRoute.getAltitude() < previousRoute.getAltitude()) {
                                downhill += previousRoute.getAltitude() - currentRoute.getAltitude();
                            }
                        }
                        previousRoute = currentRoute;
                    }
                    Date date2 = null;
                    try {
                        date2 = sdf.parse(previousRoute.getTimestamp());
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                    totaltime += date2.getTime() - date1.getTime();

                    int seconds = (int) (totaltime / 1000) % 60 ;
                    int minutes = (int) ((totaltime / (1000*60)));

                    this.time.setText(String.format("%02d:%02d", minutes, seconds));
                    this.intervals.setText(String.valueOf(subrouteCount));
                    this.kms.setText(String.valueOf(Math.round(distance) / 1000.0));
                    this.speed.setText(String.valueOf(Math.round(((distance) / (totaltime/(1000))) * 10.0*3.6) / 10.0));
                    this.up.setText(String.valueOf(Math.round(uphill * 10.0) / 10.0));
                    this.down.setText(String.valueOf(Math.round(downhill * 10.0) / 10.0));
                    this.steps.setText(String.valueOf(route.size()));

                    // calories computation
                    this.calories.setText(String.valueOf(Math.round(50.0 * (distance/1000) * 10.0) / 10.0));



                }
            });


        }

        return root;
    }


}