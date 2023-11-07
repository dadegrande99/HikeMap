package com.usi.hikemap.ui.authentication;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import com.hbb20.CountryCodePicker;
import com.usi.hikemap.R;
import com.usi.hikemap.databinding.FragmentPhoneAuthBinding;
import com.usi.hikemap.databinding.FragmentRegistrationBinding;
import com.usi.hikemap.ui.viewmodel.AuthViewModel;

import java.util.concurrent.TimeUnit;

public class PhoneAuthFragment extends Fragment {

    AuthViewModel mAuthViewModel;

    Button mButtonContinue;
    TextView mPhone;

    CountryCodePicker countryCodePicker;

    private String userId, phone, flag;

    private final String TAG = "PhoneAuthFragment";

    public static PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    public static String mVerificationId;
    public static PhoneAuthProvider.ForceResendingToken mResendToken;
    private FragmentPhoneAuthBinding binding;

    public PhoneAuthFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuthViewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentPhoneAuthBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        mButtonContinue = root.findViewById(R.id.phoneContinueBtn);

        mPhone = root.findViewById(R.id.phone_editText);
        mPhone.setText(RegistrationFragment.user.getAuth());

        phone = mPhone.getText().toString().trim();

        countryCodePicker = (CountryCodePicker) root.findViewById(R.id.ccp);
        countryCodePicker.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {
                flag = countryCodePicker.getSelectedCountryCodeWithPlus();
                Toast.makeText(getActivity(), "Updated " + countryCodePicker.getSelectedCountryName(), Toast.LENGTH_SHORT).show();
            }
        });

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                //signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Toast.makeText(getActivity(), "onVerificationFailed" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken token) {
                super.onCodeSent(verificationId, token);
                Log.d(TAG, "onCodeSent:" + verificationId);

                mVerificationId = verificationId;
                mResendToken = token;
            }
        };

        mButtonContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phone = mPhone.getText().toString().trim();
                String phoneNumberCCP = flag + phone;
                if(TextUtils.isEmpty(phone)) {
                    Toast.makeText(getActivity(), "Phone is required", Toast.LENGTH_SHORT).show();
                }
                else {
                    startPhoneNumberVerification(phoneNumberCCP);
                    Intent intent = new Intent(getActivity(), OtpPhoneActivity.class);
                    intent.putExtra("phone", phone);
                    intent.putExtra("flag", flag);
                    startActivity(intent);
                }
            }
        });
        return root;
    }

    private void startPhoneNumberVerification(String phoneNumber) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(phoneNumber, 60, TimeUnit.SECONDS, getActivity(), mCallbacks);
    }

}