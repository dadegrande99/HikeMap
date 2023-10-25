package com.usi.hikemap.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.usi.hikemap.databinding.FragmentGoBinding;
import com.usi.hikemap.ui.viewmodel.GoViewModel;

public class GoFragment extends Fragment {

    private FragmentGoBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        GoViewModel goViewModel =
                new ViewModelProvider(this).get(GoViewModel.class);

        binding = FragmentGoBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textGo;
        goViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}