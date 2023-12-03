package com.usi.hikemap.ui.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuHost;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
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

import java.util.HashMap;
import java.util.Map;

public class UpdateProfileFragment extends Fragment {

    //TODO2: ADD 2 buttons, save

    MeowBottomNavigation bottomNavigation;

    TextView Height, Weight;
    String userId;

    private ProfileViewModel ProfileViewModel;
    public User User;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_update_profile, container, false);
        bottomNavigation = getActivity().findViewById(R.id.bottomNavigation);
        bottomNavigation.setVisibility(View.GONE);
        ProfileViewModel = new ViewModelProvider(requireActivity()).get(ProfileViewModel.class);
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Height = root.findViewById(R.id.editTextHeight);
        Weight = root.findViewById(R.id.editTextWeight);

        getActivity().setTitle("Update Profile");

        if (userId != null) {
            ProfileViewModel.readUser(userId).observe(getViewLifecycleOwner(), user -> {
                if (user != null) {

                    getActivity().setTitle(user.getName());
                    User = user;
                    Height.setText(String.valueOf(User.getHeight()));
                    Weight.setText(String.valueOf(User.getWeight()));


                }
            });
        }


        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        MenuHost menuHost = requireActivity();
        menuHost.addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                // Add menu items here
                menuInflater.inflate(R.menu.menu_update_profile, menu);
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {

                if (menuItem.getItemId() == R.id.spunta) {


                    //set new values to user
                    User.setHeight(Integer.parseInt(Height.getText().toString()));
                    User.setWeight(Integer.parseInt(Weight.getText().toString()));

                    Map<String, Object> data = new HashMap<>();
                    data.put("height", User.getHeight());
                    data.put("weight", User.getWeight());
                    //Map<String, Object> dataWeight = new HashMap<>();

                    //
                    ProfileViewModel.updateProfile(data).observe(getViewLifecycleOwner(), authenticationResponse -> {
                        if (authenticationResponse != null) {
                            if (authenticationResponse.isSuccess()) {
                                Toast.makeText(getActivity(), "Saved", Toast.LENGTH_SHORT).show();
                                getActivity().getSupportFragmentManager().beginTransaction()
                                        .replace(R.id.frame_layout, new ProfileFragment())
                                        .commit();
                                bottomNavigation.setVisibility(View.VISIBLE);
                            } else {
                                Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    Toast.makeText(getActivity(), "Saved", Toast.LENGTH_SHORT).show();
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.frame_layout, new ProfileFragment())
                            .commit();
                    bottomNavigation.setVisibility(View.VISIBLE);
                }

                if (menuItem.getItemId() == R.id.cross) {
                    Toast.makeText(getActivity(), "Abort change", Toast.LENGTH_SHORT).show();
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.frame_layout, new ProfileFragment())
                            .commit();
                    bottomNavigation.setVisibility(View.VISIBLE);
                }

                return true;
            }
        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);
    }
}