package com.usi.hikemap.ui.authentication.chooselink;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.usi.hikemap.R;
import com.usi.hikemap.databinding.FragmentChooseLinkBinding;


public class ChooseLinkFragment extends Fragment {

    private FragmentChooseLinkBinding binding;
    private static String TAG = "ChooseLinkFragment";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentChooseLinkBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        Intent intent = getActivity().getIntent();
        String action = intent.getAction();
        Uri data = intent.getData();

        data.getQueryParameter("mode");
        Log.d(TAG, "onCreate: URI parameter" + data.getQueryParameter("mode"));

        if(data.getQueryParameter("mode").equals("signIn")) {
            Log.d(TAG, "onCreate: verify email");
            NavHostFragment.findNavController(ChooseLinkFragment.this).navigate(R.id.choose_to_registration);
            //startActivity(new Intent(ChooseLinkActivity.this, RegistrationConfirmActivity.class));
        }
        else if(data.getQueryParameter("mode").equals("resetPassword")) {
            Log.d(TAG, "onCreate: change password");
            NavHostFragment.findNavController(ChooseLinkFragment.this).navigate(R.id.choose_to_forgot);
            //startActivity(new Intent(ChooseLinkActivity.this, ForgotConfirmActivity.class));
        }

        return root;
    }
}