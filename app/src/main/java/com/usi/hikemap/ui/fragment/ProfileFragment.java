package com.usi.hikemap.ui.fragment;

import static com.usi.hikemap.utils.Constants.DEFAULT_WEB_CLIENT_ID;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuHost;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import com.usi.hikemap.model.User;
import com.usi.hikemap.ui.authentication.AuthenticationActivity;
import com.usi.hikemap.ui.viewmodel.ProfileViewModel;

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
    TextView mDeleteAccount, mLogout;
    BottomSheetDialog profile_option_show;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize ViewModel
        mProfileViewModel = new ViewModelProvider(requireActivity()).get(ProfileViewModel.class);

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

        // Log user's UID
        Log.d(TAG, "onCreateView: " + FirebaseAuth.getInstance().getCurrentUser().getUid());

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
                }
                return true;
            }
        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);
    }

}
