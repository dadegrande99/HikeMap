package com.usi.hikemap.ui.authentication;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.usi.hikemap.R;
import com.usi.hikemap.databinding.FragmentStartPageBinding;


public class StartPageFragment extends Fragment {
    Button mButtonRegistration;
    TextView mButtonLogin;
    private FragmentStartPageBinding binding;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentStartPageBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        mButtonRegistration = root.findViewById(R.id.registration_button);
        mButtonLogin = root.findViewById(R.id.login_button);

        mButtonRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(StartPageFragment.this).
                        navigate(R.id.start_to_registration);
            }
        });

        mButtonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(StartPageFragment.this).
                        navigate(R.id.start_to_login);
            }
        });

        return root;
    }
}