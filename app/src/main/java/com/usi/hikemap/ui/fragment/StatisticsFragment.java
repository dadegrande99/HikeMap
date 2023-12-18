package com.usi.hikemap.ui.fragment;

import android.os.Bundle;
import androidx.fragment.app.Fragment;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.usi.hikemap.R;
import com.usi.hikemap.adapter.StatisticsRecycleViewAdapter;
import com.usi.hikemap.model.Route;
import com.usi.hikemap.ui.viewmodel.ProfileViewModel;
import com.usi.hikemap.utils.ParcelableRouteList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StatisticsFragment extends Fragment {

    private RecyclerView recyclerView;
    private StatisticsRecycleViewAdapter adapter;
    private static final String ARG_ROUTE_LIST = "List<Object>";
    public FirebaseAuth fAuth;
    public String TAG = "StatisticsFragment";
    private String userId;
    private ProfileViewModel mProfileViewModel;

    public StatisticsFragment() {
        // Required empty public constructor
    }

    public static StatisticsFragment newInstance(ParcelableRouteList parcelableRouteList) {
        StatisticsFragment fragment = new StatisticsFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_ROUTE_LIST, parcelableRouteList);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mProfileViewModel = new ViewModelProvider(requireActivity()).get(ProfileViewModel.class);
        fAuth = FirebaseAuth.getInstance();
        userId = fAuth.getCurrentUser().getUid();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_statistics, container, false);

        recyclerView = root.findViewById(R.id.searchRecyclerView);
        Log.d(TAG, "onCreateView: " + userId.toString());
        mProfileViewModel.readRoutes(userId).observe(getViewLifecycleOwner(), route -> {
            //Log.d(TAG, "onCreateView: " + route.toString());
            if (route != null && !route.isEmpty()) {

                // Sort the list of routes by date
                Collections.sort(route, (r1, r2) -> r2.getTimestamp().compareTo(r1.getTimestamp()));
                Log.d("ProvaHikeDetails", "onCreateView RouteId: " + route.toString());

                // Create a new list that contains only the first occurrence of each idRoute
                List<Route> distinctRoutes = new ArrayList<>();
                List<String> addedRoutes = new ArrayList<>();

                // List with the same getIdRoute
                List<Route> singleRoute = new ArrayList<>();
                for (Route r : route) {
                    if (r.getIdRoute().equals(route.get(0).getIdRoute())) {
                        singleRoute.add(r);
                    }
                }

                for (Route r : route) {
                    if (!addedRoutes.contains(r.getIdRoute())) {
                        distinctRoutes.add(r);
                        addedRoutes.add(r.getIdRoute());
                    }
                }
                Log.d("ProvaHikeDetails", "onCreateView Distinct: " + distinctRoutes.toString());

                // Do something with the receivedRoutes
                adapter = new StatisticsRecycleViewAdapter(requireContext(), distinctRoutes, new StatisticsRecycleViewAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(Route route) {

                        Log.d(TAG, "onItemClick: Enter");
                        Log.d("ProvaHikeDetails", "onCreateView Single: " + route.getIdRoute().toString());

                        //ParcelableRouteList parcelableRouteList = new ParcelableRouteList(singleRoute);
                        HikeDetailsFragment hikeDetails = HikeDetailsFragment.newInstance(route.getIdRoute().toString());

                        getActivity().getSupportFragmentManager().beginTransaction()
                                .replace(R.id.frame_layout, hikeDetails)
                                .commit();
                    }
                });

                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
            }
        });

        return root;
    }
}