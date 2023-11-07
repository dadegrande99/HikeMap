package com.usi.hikemap.ui.authentication;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.usi.hikemap.MainActivity;
import com.usi.hikemap.R;
import com.usi.hikemap.databinding.FragmentLoginBinding;
import com.usi.hikemap.ui.viewmodel.AuthViewModel;
import com.usi.hikemap.utils.Constants;

public class LoginFragment extends Fragment {

    EditText mEmailEditText;
    EditText mPasswordEditText;
    Button mLoginButton;
    ImageView mGoogleButton, mReturnStartPage;
    private GoogleSignInClient mGoogleSignInClient;
    AuthViewModel mAuthViewModel;
    private final String TAG = "LoginFragment";
    private FragmentLoginBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuthViewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        mEmailEditText = root.findViewById(R.id.email_log_editText);
        mPasswordEditText = root.findViewById(R.id.password_log_editText);
        mLoginButton = root.findViewById(R.id.access_button);


        //***********************
        mReturnStartPage = root.findViewById(R.id.return_to_startPage_from_login);
        mReturnStartPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(LoginFragment.this).navigate(R.id.login_to_startpage);
            }
        });

        //***********************
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email, password;
                email = mEmailEditText.getText().toString().trim();
                password = mPasswordEditText.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    mEmailEditText.setError("Email is required");
                    return;
                }

                if (TextUtils.isEmpty(password) || password.length() < 8) {
                    mEmailEditText.setError("Password is required");
                    return;
                }

                mAuthViewModel.loginWithEmail(email, password).observe(getViewLifecycleOwner(), authenticationResponse -> {
                    if (authenticationResponse != null) {
                        if (authenticationResponse.isSuccess()) {
                            Log.d(TAG, "onClick: Access to main Activity");
                            Intent intent = new Intent(getActivity(), MainActivity.class);
                            startActivity(intent);
                        } else {
                            Log.d(TAG, "onClick: Error didn't access to main");
                            updateUIForFailure(authenticationResponse.getMessage());
                        }
                    }
                });
            }
        });

        mGoogleButton = root.findViewById(R.id.google_access_button);
        mGoogleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(Constants.DEFAULT_WEB_CLIENT_ID)
                        .requestEmail()
                        .build();

                mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso);
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, Constants.RC_SIGN_IN);
            }
        });

        return root;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...)
        if(requestCode == Constants.RC_SIGN_IN){
            Log.d(TAG, "onActivityResult: Google Signin intent result");
            Task<GoogleSignInAccount> accountTask = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = accountTask.getResult();
                mAuthViewModel.createUserWithGoogle(account);
                //google sing in success, new auth firebase
                startActivity(new Intent(getActivity(), MainActivity.class));

            }catch (Exception e){
                Log.d(TAG, "onActivityResult: " + e.getMessage());
            }
        }
    }

    private void updateUIForFailure(String message) {
        Snackbar.make(requireActivity().findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT).show();
    }
}