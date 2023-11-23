package com.usi.hikemap.ui.authentication;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.firebase.auth.ActionCodeSettings;
import com.usi.hikemap.R;
import com.usi.hikemap.databinding.FragmentRegistrationBinding;
import com.usi.hikemap.model.User;
import com.usi.hikemap.ui.viewmodel.UserViewModel;
import com.usi.hikemap.utils.Constants;

import java.util.regex.Pattern;


public class RegistrationFragment extends Fragment {
    private FragmentRegistrationBinding binding;
    EditText mName, mSurname, mUsername, mAuth, mPassword, mConfirmedPassword;
    Button mContinue;
    ImageView mReturnStartPage;

    private UserViewModel mUserViewModel;
    public static User user;
    private final String TAG = "RegistrationFragment";

    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^" +
                    //"(?=.*[0-9])" +         //at least 1 digit
                    //"(?=.*[a-z])" +         //at least 1 lower case letter
                    //"(?=.*[A-Z])" +         //at least 1 upper case letter
                    "(?=.*[a-zA-Z])" +      //any letter
                    "(?=.*[@#$%^&+=!?])" +    //at least 1 special character
                    "(?=\\S+$)" +           //no white spaces
                    ".{8,}" +               //at least 8 characters
                    "$");


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mUserViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentRegistrationBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        mName = root.findViewById(R.id.name_editText);
        mSurname = root.findViewById(R.id.surname_editText);
        mUsername = root.findViewById(R.id.username_editText);
        mAuth = root.findViewById(R.id.mAuth_editText);
        mPassword = root.findViewById(R.id.password_editText);
        mConfirmedPassword = root.findViewById(R.id.repassword_editText);

        //************************
        mReturnStartPage = root.findViewById(R.id.return_to_startPage_from_registration);
        mReturnStartPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(RegistrationFragment.this).navigate(R.id.registration_to_startpage);
            }
        });

        //************************
        mContinue = root.findViewById(R.id.confirmed_registration_button);
        mContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String name, surname, username, auth, password, confirmPassword;
                name = mName.getText().toString().trim();
                surname = mSurname.getText().toString();
                username = mUsername.getText().toString();
                auth = mAuth.getText().toString().trim();
                password = mPassword.getText().toString();
                confirmPassword = mConfirmedPassword.getText().toString();

                Log.d(TAG, "onClick: email:" + auth);

                user = new User(name, surname, username, auth, password, null, null, null, null);


                if (TextUtils.isEmpty(password)) {
                    mPassword.setError("Password is required");
                    return;

                }
                else if (!PASSWORD_PATTERN.matcher(password).matches())  {
                    mPassword.setError("Password too weak");
                    return;
                }
                else if(password.length() > 32 || password.length() < 8) {
                    mPassword.setError("Length invalid");
                    return;
                }
                else {
                    mPassword.setError(null);
                }
                if (TextUtils.isEmpty(confirmPassword)) {
                    mConfirmedPassword.setError("Password is required");
                    return;

                }
                if(!confirmPassword.equals(password)) {
                    mConfirmedPassword.setError("Password are not the same");
                    return;
                }

                if (auth.matches("-?\\d+")) {
                    NavHostFragment.findNavController(RegistrationFragment.this).navigate(R.id.registration_to_phone);
                }
                else {
                    if (TextUtils.isEmpty(auth)) {
                        mAuth.setError("Email Address is required");
                        return;
                    } else if (!Patterns.EMAIL_ADDRESS.matcher(auth).matches()) {
                        mAuth.setError("Please enter a valid Address");
                        return;
                    } else {
                        mAuth.setError(null);
                    }

                    ActionCodeSettings actionCodeSettings =
                            ActionCodeSettings.newBuilder()
                                    .setUrl(Constants.URL_LINK_EMAIL)
                                    .setHandleCodeInApp(true)
                                    .build();

                    mUserViewModel.sendSignInLinkToEmail(auth, actionCodeSettings).observe(getViewLifecycleOwner(), authenticationResponse -> {
                        if (authenticationResponse != null) {
                            if (authenticationResponse.isSuccess()) {
                                Log.d(TAG, "onClick: Email send");
                                NavHostFragment.findNavController(RegistrationFragment.this).navigate(R.id.registration_to_waiting);
                            } else {
                                Log.d(TAG, "onClick: Error don't send email");
                            }
                        }
                    });
                }
            }
        });

        return root;
    }
}










