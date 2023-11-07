package com.usi.hikemap.ui.authentication.chooselink;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.usi.hikemap.R;
import com.usi.hikemap.databinding.FragmentConfirmForgotPasswordBinding;

public class ConfirmForgotPasswordFragment extends Fragment {

    private FragmentConfirmForgotPasswordBinding binding;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding  = FragmentConfirmForgotPasswordBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        return root;

    }
}