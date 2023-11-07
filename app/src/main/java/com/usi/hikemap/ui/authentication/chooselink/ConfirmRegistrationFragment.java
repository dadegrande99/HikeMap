package com.usi.hikemap.ui.authentication.chooselink;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.usi.hikemap.MainActivity;
import com.usi.hikemap.R;
import com.usi.hikemap.databinding.FragmentConfirmRegistrationBinding;
import com.usi.hikemap.ui.authentication.RegistrationFragment;
import com.usi.hikemap.ui.viewmodel.AuthViewModel;

public class ConfirmRegistrationFragment extends Fragment {

    AuthViewModel mAuthViewModel;
    private static String TAG = "ConfirmRegistrationFragment";

    Button mConfirmRegitration;
    TextView mConfirmName, mConfirmSurname, mConfirmUsername, mConfirmEmail;
    private FragmentConfirmRegistrationBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuthViewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentConfirmRegistrationBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        mConfirmName = root.findViewById(R.id.confirmed_name);
        mConfirmSurname = root.findViewById(R.id.confirmed_surname);
        mConfirmUsername = root.findViewById(R.id.confirmed_username);
        mConfirmEmail = root.findViewById(R.id.confirmed_email);

        mConfirmName.setText(RegistrationFragment.user.getName());
        mConfirmSurname.setText(RegistrationFragment.user.getSurname());
        mConfirmUsername.setText(RegistrationFragment.user.getUsername());
        mConfirmEmail.setText(RegistrationFragment.user.getAuth());

        mConfirmRegitration = root.findViewById(R.id.confirm_registration_button);
        mConfirmRegitration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuthViewModel.createUserWithEmail(RegistrationFragment.user).observe(getViewLifecycleOwner(), authenticationResponse -> {
                    if(authenticationResponse != null) {
                        if(authenticationResponse.isSuccess()) {
                            Log.d(TAG, "onClick: success");
                            startActivity(new Intent(getActivity(), MainActivity.class));
                        }
                    }
                });
            }
        });

        return root;
    }
}







