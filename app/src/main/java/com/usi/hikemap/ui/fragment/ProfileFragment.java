package com.usi.hikemap.ui.fragment;

import static com.usi.hikemap.utils.Constants.DEFAULT_WEB_CLIENT_ID;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
    FloatingActionButton mDeleteAccount;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize ViewModel
        mProfileViewModel = new ViewModelProvider(requireActivity()).get(ProfileViewModel.class);

        // Initialize FirebaseAuth and GoogleSignInClient
        fAuth = FirebaseAuth.getInstance();
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(DEFAULT_WEB_CLIENT_ID)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), googleSignInOptions);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize UI elements
        mUser = new User();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Log user's UID
        Log.d(TAG, "onCreateView: " + FirebaseAuth.getInstance().getCurrentUser().getUid());

        // Find and set up the delete account button
        mDeleteAccount = root.findViewById(R.id.deleteButtom);
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

        return root;
    }
}
