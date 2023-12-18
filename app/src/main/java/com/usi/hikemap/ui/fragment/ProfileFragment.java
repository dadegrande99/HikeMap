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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
    TextView mDeleteAccount, mLogout, mName, mUsername, changeSettings, mHeight, mWeight;
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize ViewModel

        // Initialize FirebaseAuth and GoogleSignInClient
        fAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(DEFAULT_WEB_CLIENT_ID)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), googleSignInOptions);

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


                    if (String.valueOf(mUser.getHeight()).equals(null) | String.valueOf(mUser.getHeight()).equals("") | String.valueOf(mUser.getHeight()).equals(" ")| String.valueOf(mUser.getHeight()).isEmpty()) {
                        mHeight.setText("-");
                        Log.d("ProfileFragment", "check hight null " );
                    }else{
                        mHeight.setText(String.valueOf(mUser.getHeight() + " cm"));
                    }


                    if (String.valueOf(mUser.getWeight()).equals(null) | String.valueOf(mUser.getWeight()).equals("") | String.valueOf(mUser.getWeight()).equals(" ")| String.valueOf(mUser.getWeight()).isEmpty()) {
                        mWeight.setText("-");
                        Log.d("ProfileFragment", "check hight null " );
                    }else{
                        mWeight.setText(String.valueOf(mUser.getWeight() + " kg"));
                    }





                    mProfileViewModel.readImage(userId).observe(getViewLifecycleOwner(), authenticationResponse-> {
                        if (authenticationResponse != null) {
                            if (authenticationResponse.isSuccess() && mUser.getPath() != null) {
                                Glide.with(getContext())
                                        .load(mUser.getPath())
                                        //.signature(new ObjectKey(String.valueOf(System.currentTimeMillis())))
                                        .into(profilePic);
                                //UPDATE PROFILE PIC VIEW IN UPDATE FRAGMENT

                            }
                            else {
                                Log.d(TAG, "onClick: Error don't update image");
                            }
                        }
                    });

                }else{
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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        MenuHost menuHost = requireActivity();
        menuHost.addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                // Add menu items here
                menuInflater.inflate(R.menu.profile_menu, menu);

                profile_option_show = new BottomSheetDialog(getActivity(), R.style.BottomSheetDialogTheme);

                View bottomSheetView = LayoutInflater.from(getActivity())
                        .inflate(R.layout.profile_option_menu, (LinearLayout) getView().findViewById(R.id.profile_option));

                profile_option_show.setContentView(bottomSheetView);
                changeSettings = profile_option_show.findViewById(R.id.changeSettingsTextView);
                mLogout = profile_option_show.findViewById(R.id.logout_textView);

                mDeleteAccount = profile_option_show.findViewById(R.id.delete_account_textView);
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.profile_button) {

                    profile_option_show.show();

                    // Do something for Menu Item 1
                    mDeleteAccount.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Observe user data changes using ViewModel
                            mProfileViewModel.readUser(userId).observe(getViewLifecycleOwner(), user -> {
                                if (user != null) {
                                    mUser = user;
                                    if (mUser.getProvider().equals(GoogleAuthProvider.PROVIDER_ID)) {
                                        // If the user is signed in with Google, revoke access
                                        mGoogleSignInClient.revokeAccess().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                // Delete the account in the database
                                                mProfileViewModel.deleteAccount(userId).observe(getViewLifecycleOwner(), authenticationResponse -> {
                                                    if (authenticationResponse != null) {
                                                        if (authenticationResponse.isSuccess()) {
                                                            Log.d(TAG, "Delete account");
                                                        } else {
                                                            Log.d(TAG, "Error: Account not deleted");
                                                        }
                                                    }
                                                });
                                            }
                                        });
                                    } else if (mUser.getProvider().equals(FirebaseAuthProvider.PROVIDER_ID) || mUser.getProvider().equals(PhoneAuthProvider.PROVIDER_ID)) {
                                        // If the user is signed in with Firebase or Phone, delete the account
                                        fAuth.getCurrentUser().delete();
                                        // Delete the account in the database
                                        mProfileViewModel.deleteAccount(userId).observe(getViewLifecycleOwner(), authenticationResponse -> {
                                            if (authenticationResponse != null) {
                                                if (authenticationResponse.isSuccess()) {
                                                    Log.d(TAG, "Delete account");
                                                } else {
                                                    Log.d(TAG, "Error: Account not deleted");
                                                }
                                            }
                                        });
                                    } else {
                                        Log.d(TAG, "onComplete: Fatal");
                                    }
                                }
                            });

                            // Navigate to the authentication activity after account deletion
                            startActivity(new Intent(getActivity(), AuthenticationActivity.class));
                        }
                    });


                    mLogout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mProfileViewModel.readUser(userId).observe(getViewLifecycleOwner(), user -> {

                                if (user != null) {
                                    mUser = user;
                                    if (mUser.getProvider().equals(GoogleAuthProvider.PROVIDER_ID)) {
                                        Log.d(TAG, "onOptionsItemSelected: " + GoogleAuthProvider.PROVIDER_ID);
                                        mGoogleSignInClient.signOut();
                                    } else if (mUser.getProvider().equals(FirebaseAuthProvider.PROVIDER_ID)) {
                                        Log.d(TAG, "onOptionsItemSelected: " + FirebaseAuthProvider.PROVIDER_ID);
                                        FirebaseAuth.getInstance().signOut();
                                    } else if (mUser.getProvider().equals(PhoneAuthProvider.PROVIDER_ID)) {
                                        Log.d(TAG, "onOptionsItemSelected: " + PhoneAuthProvider.PROVIDER_ID);
                                        FirebaseAuth.getInstance().signOut();
                                    } else {
                                        Log.d(TAG, "onComplete: Fatal");
                                        return;
                                    }
                                }
                            });

                            startActivity(new Intent(getActivity(), AuthenticationActivity.class));
                        }

                    });

                    changeSettings.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Navigate to the update profile fragment
                            getActivity().getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.frame_layout, new UpdateProfileFragment())
                                    .commit();
                            profile_option_show.hide();
                        }
                    });
                }
                return true;
            }
        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);
    }
}
