package com.usi.hikemap.repository;



import static com.usi.hikemap.utils.Constants.FIREBASE_DATABASE_URL;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.usi.hikemap.model.AuthenticationResponse;

public class UserRepository implements IUserRepository {

    private final FirebaseAuth fAuth;
    private static FirebaseDatabase fDatabase;
    private static DatabaseReference fReference;
    private final Application mApplication;

    private final MutableLiveData<AuthenticationResponse> mAuthenticationResponse;
    private static final String TAG = "UserRepository";
    private String userId;

    public UserRepository(Application application) {
        fAuth = FirebaseAuth.getInstance();
        fDatabase = FirebaseDatabase.getInstance(FIREBASE_DATABASE_URL);
        mApplication = application;
        mAuthenticationResponse = new MutableLiveData<>();
    }

    @Override
    public MutableLiveData<AuthenticationResponse> sendSignInLinkToEmail(String auth, ActionCodeSettings actionCodeSettings) {
        fAuth.sendSignInLinkToEmail(auth, actionCodeSettings).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                AuthenticationResponse authenticationResponse = new AuthenticationResponse();

                if (task.isSuccessful()) {
                    authenticationResponse.setSuccess(true);
                    Log.d(TAG, "onComplete: Send email with links");
                }
                else {
                    authenticationResponse.setMessage(task.getException().getLocalizedMessage());
                    Log.d(TAG, "onComplete: " + authenticationResponse.getMessage());
                    authenticationResponse.setSuccess(false);
                    Log.d(TAG, "onComplete: Don't send email");
                }
                mAuthenticationResponse.postValue(authenticationResponse);
            }
        });
        return mAuthenticationResponse;
    }

    @Override
    public MutableLiveData<AuthenticationResponse> sendPasswordResetEmail(String auth) {
        fAuth.sendPasswordResetEmail(auth).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                AuthenticationResponse authenticationResponse = new AuthenticationResponse();

                if (task.isSuccessful()) {
                    authenticationResponse.setSuccess(true);
                    Log.d(TAG, "onComplete: Send email with links");
                }
                else {
                    authenticationResponse.setMessage(task.getException().getLocalizedMessage());
                    Log.d(TAG, "onComplete: " + authenticationResponse.getMessage());
                    authenticationResponse.setSuccess(false);
                    Log.d(TAG, "onComplete: Don't send email");
                }
                mAuthenticationResponse.postValue(authenticationResponse);
            }
        });
        return mAuthenticationResponse;
    }

    @Override
    public MutableLiveData<AuthenticationResponse> updatePassword(String password) {
        fAuth.getCurrentUser().updatePassword(password).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                AuthenticationResponse authenticationResponse = new AuthenticationResponse();
                if(task.isSuccessful()){
                    authenticationResponse.setSuccess(true);
                    Log.d(TAG, "onComplete: Password update");
                }
                else {
                    authenticationResponse.setSuccess(false);
                    Log.d(TAG, "onComplete: Error password not update");
                }
            }
        });
        return mAuthenticationResponse;
    }

}
