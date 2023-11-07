package com.usi.hikemap.ui.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.usi.hikemap.R;
import com.usi.hikemap.databinding.FragmentGoBinding;

public class ProfileFragment extends Fragment {

    private FragmentGoBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentGoBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        return root;
    }
}