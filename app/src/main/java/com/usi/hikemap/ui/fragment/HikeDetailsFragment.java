package com.usi.hikemap.ui.fragment;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.usi.hikemap.R;
import com.usi.hikemap.utils.ParcelableRouteList;
import com.usi.hikemap.model.Route;
import com.usi.hikemap.ui.viewmodel.GoDetailsViewModel;

import java.util.List;

public class HikeDetailsFragment extends Fragment {

    private static final String ARG_ROUTE_LIST = "List<Object>";
    private static String TAG = "HikeDetailsFragment";
    private String userId;
    private GoDetailsViewModel mGoDetailsViewModel;



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
        mGoDetailsViewModel = new ViewModelProvider(requireActivity()).get(GoDetailsViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_go_details, container, false);


        Bundle args = getArguments();
        if (args != null) {
            ParcelableRouteList parcelableRouteList = args.getParcelable(ARG_ROUTE_LIST);
            if (parcelableRouteList != null) {
                List<Route> receivedRoutes = parcelableRouteList.getRouteList();
                // Do something with the receivedRoutes
                Log.d(TAG, "onCreateView: " + receivedRoutes.toString());
            }
        }



        return root;
    }
}