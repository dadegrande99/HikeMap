package com.usi.hikemap.ui.fragment;

import static com.usi.hikemap.utils.Constants.DEFAULT_WEB_CLIENT_ID;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuHost;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthProvider;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.PhoneAuthProvider;
import com.usi.hikemap.R;
import com.usi.hikemap.adapter.ProfileRecycleViewAdapter;
import com.usi.hikemap.utils.ParcelableRouteList;
import com.usi.hikemap.model.Route;
import com.usi.hikemap.model.User;
import com.usi.hikemap.ui.authentication.AuthenticationActivity;
import com.usi.hikemap.ui.viewmodel.ProfileViewModel;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;



/**
 * Fragment class for displaying user profile information and handling account deletion.
 */
public class ProfileFragment extends Fragment {

    private ProfileViewModel mProfileViewModel;
    public User mUser;
    public FirebaseAuth fAuth;
    GoogleSignInClient mGoogleSignInClient;
    String TAG = "ProfileFragment";
    String userId;
    TextView mDeleteAccount, mLogout, mName, mUsername, updateProfile, mHeight, mWeight;
    BottomSheetDialog profile_option_show;
    CircleImageView profilePic;
    private RecyclerView recyclerView;
    private ProfileRecycleViewAdapter adapter;
    View root;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mProfileViewModel = new ViewModelProvider(requireActivity()).get(ProfileViewModel.class);
        fAuth = FirebaseAuth.getInstance();

        setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize ViewModel

        // Initialize FirebaseAuth and GoogleSignInClient
        fAuth = FirebaseAuth.getInstance();

        // Initialize UI elements
        mUser = new User();

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mName = root.findViewById(R.id.name_user);
        mUsername = root.findViewById(R.id.username_user);
        profilePic = root.findViewById(R.id.profile_picture);

        mHeight = root.findViewById(R.id.heightValue);
        mWeight = root.findViewById(R.id.weightValue);

        Button viewMoreButton = root.findViewById(R.id.viewMoreButton);

        // Log user's UID
        Log.d(TAG, "onCreateView: " + FirebaseAuth.getInstance().getCurrentUser().getUid());

        if (userId != null) {
            Log.d("ProfileFragment", "in first if");

            mProfileViewModel.readUser(userId).observe(getViewLifecycleOwner(), user -> {
                Log.d("ProfileFragment", "in profile view model");
                if (user != null) {
                    Log.d("ProfileFragment", "in second if");
                    getActivity().setTitle(user.getName());

                    mUser = user;
                    mName.setText(mUser.getName().concat(" ").concat(mUser.getSurname()));
                    mUsername.setText(mUser.getUsername());


                    if (mUser.getHeight() == null || String.valueOf(mUser.getHeight()).equals("") || String.valueOf(mUser.getHeight()).equals(" ") || String.valueOf(mUser.getHeight()).isEmpty()) {
                        mHeight.setText("-");
                        Log.d("ProfileFragment", "check hight null ");
                    } else {
                        mHeight.setText(String.valueOf(mUser.getHeight() + " cm"));
                    }


                    if (mUser.getWeight() == null || String.valueOf(mUser.getWeight()).equals("") || String.valueOf(mUser.getWeight()).equals(" ") || String.valueOf(mUser.getWeight()).isEmpty()) {
                        mWeight.setText("-");
                        Log.d("ProfileFragment", "check hight null ");
                    } else {
                        mWeight.setText(String.valueOf(mUser.getWeight() + " kg"));
                    }


                    mProfileViewModel.readImage(userId).observe(getViewLifecycleOwner(), authenticationResponse -> {
                        if (authenticationResponse != null) {
                            if (authenticationResponse.isSuccess() && mUser.getPath() != null) {
                                Glide.with(getContext())
                                        .load(mUser.getPath())
                                        //.signature(new ObjectKey(String.valueOf(System.currentTimeMillis())))
                                        .into(profilePic);
                                //UPDATE PROFILE PIC VIEW IN UPDATE FRAGMENT

                            } else {
                                Log.d(TAG, "onClick: Error don't update image");
                            }
                        }
                    });

                } else {
                    Log.d(TAG, "non trova l'user");
                }
            });
        }

        recyclerView = root.findViewById(R.id.result_list_route);
        Log.d(TAG, "onCreateView: " + userId);
        mProfileViewModel.readRoutes(userId).observe(getViewLifecycleOwner(), route -> {
            Log.d(TAG, "onCreateView: " + route.toString());
            if (route != null && !route.isEmpty()) {

                // Sort the list of routes by date
                Collections.sort(route, (r1, r2) -> r2.getTimestamp().compareTo(r1.getTimestamp()));

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


                // print the list
                for (Route r : singleRoute) {
                    Log.d(TAG, "onCreateView: " + r.getId());
                }

                for (Route r : route) {
                    if (!addedRoutes.contains(r.getIdRoute())) {
                        distinctRoutes.add(r);
                        addedRoutes.add(r.getIdRoute());
                    }
                }

                viewMoreButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Load all items when the button is pressed
                        //adapter.loadAllItems();
                        ParcelableRouteList parcelableRouteList = new ParcelableRouteList(distinctRoutes);
                        StatisticsFragment statisticsFragment = StatisticsFragment.newInstance(parcelableRouteList);
                        getActivity().getSupportFragmentManager().beginTransaction()
                                .replace(R.id.frame_layout, statisticsFragment)
                                .addToBackStack(null)
                                .commit();

                        MeowBottomNavigation bottomNavigation = getActivity().findViewById(R.id.bottomNavigation);
                        bottomNavigation.show(1, true);

                    }
                });

                adapter = new ProfileRecycleViewAdapter(requireContext(), distinctRoutes, new ProfileRecycleViewAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(Route route) {
                        Log.d(TAG, "onItemClick: Enter");

                        ParcelableRouteList parcelableRouteList = new ParcelableRouteList(singleRoute);
                        HikeDetailsFragment hikeDetails = HikeDetailsFragment.newInstance(parcelableRouteList);

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

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.profile_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.profile_button){
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_layout, new UpdateProfileFragment())
                    .commit();

            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
