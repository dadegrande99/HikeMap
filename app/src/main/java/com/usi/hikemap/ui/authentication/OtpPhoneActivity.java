package com.usi.hikemap.ui.authentication;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.usi.hikemap.MainActivity;
import com.usi.hikemap.R;
import com.usi.hikemap.ui.viewmodel.AuthViewModel;

import java.util.concurrent.TimeUnit;

public class OtpPhoneActivity extends AppCompatActivity {

    AuthViewModel mAuthViewModel;
    EditText mCode;
    TextView mResendCode;
    Button mButtonSubmit;

    public static final String TAG = "OtpPhoneFragment";

    private String phoneIntent, flagIntent;
    public static PhoneAuthCredential credential;


    public OtpPhoneActivity() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_phone);

        if(savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                phoneIntent = null;
                flagIntent = null;
            }
            else {
                phoneIntent = extras.getString("phone");
                flagIntent = extras.getString("flag");
            }
        }
        else {
            phoneIntent = (String) savedInstanceState.getSerializable("phone");
            flagIntent = (String) savedInstanceState.getSerializable("flag");
        }


        mAuthViewModel = new ViewModelProvider(OtpPhoneActivity.this).get(AuthViewModel.class);

        mCode = findViewById(R.id.codePhone_editText);
        mResendCode = findViewById(R.id.resendCode);

        mButtonSubmit = findViewById(R.id.codeSubmit);
        mButtonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phone = phoneIntent;
                String code = mCode.getText().toString().trim();

                if(TextUtils.isEmpty(phone)) {
                    Toast.makeText(OtpPhoneActivity.this, "Phone is required", Toast.LENGTH_SHORT).show();
                }
                if(TextUtils.isEmpty(code)) {
                    Toast.makeText(OtpPhoneActivity.this, "Code is required", Toast.LENGTH_SHORT).show();
                }
                else {
                    verifyPhoneNumberWithCode(PhoneAuthFragment.mVerificationId, code);
                }
            }
        });

        mResendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phone = phoneIntent;
                String phoneNumberCCP = flagIntent + phone;
                if(TextUtils.isEmpty(phoneNumberCCP)) {
                    Toast.makeText(OtpPhoneActivity.this, "Phone is required", Toast.LENGTH_SHORT).show();
                }
                else {
                    resendVerificationCode(phoneNumberCCP, PhoneAuthFragment.mResendToken);
                }
            }
        });

    }

    private void resendVerificationCode(String phoneNumber, PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(phoneNumber, 60, TimeUnit.SECONDS, OtpPhoneActivity.this, PhoneAuthFragment.mCallbacks, token);
    }


    public void verifyPhoneNumberWithCode(String verificationId, String code) {
        credential = PhoneAuthProvider.getCredential(verificationId, code);
        mAuthViewModel.createUserWithPhone(RegistrationFragment.user, credential).observe(OtpPhoneActivity.this, authenticationResponse -> {
            if (authenticationResponse != null) {
                if (authenticationResponse.isSuccess()) {
                    Log.d(TAG, "onClick: success");
                    startActivity(new Intent(OtpPhoneActivity.this, MainActivity.class));
                }
                else {
                    Log.d(TAG, "onClick: Error don't create profile with phone");
                }
            }
        });

    }
}

