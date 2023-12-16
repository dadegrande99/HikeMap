package com.usi.hikemap.ui.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.usi.hikemap.R;
import com.usi.hikemap.adapter.ProfileRecycleViewAdapter;
import com.usi.hikemap.model.Route;
import com.usi.hikemap.utils.ParcelableRouteList;

import java.util.ArrayList;
import java.util.List;

public class AllHikesFragment extends Fragment {

    private RecyclerView recyclerView;
    private ProfileRecycleViewAdapter adapter;

    private static final String ARG_ROUTE_LIST = "List<Object>";

    public AllHikesFragment() {
        // Required empty public constructor
    }

    public static AllHikesFragment newInstance(ParcelableRouteList parcelableRouteList) {
        AllHikesFragment fragment = new AllHikesFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_ROUTE_LIST, parcelableRouteList);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_all_hikes, container, false);

        recyclerView = root.findViewById(R.id.allHikesRecyclerView);


        Bundle args = getArguments();
        if (args != null) {
            ParcelableRouteList parcelableRouteList = args.getParcelable(ARG_ROUTE_LIST);
            if (parcelableRouteList != null) {
                List<Route> receivedRoutes = parcelableRouteList.getRouteList();

                // Do something with the receivedRoutes
                adapter = new ProfileRecycleViewAdapter(getContext(), receivedRoutes, new ProfileRecycleViewAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(Route route) {
                        // Handle item click if needed
                    }
                });
            }
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        return root;
    }
}