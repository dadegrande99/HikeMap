package com.usi.hikemap.repository;

import static com.usi.hikemap.utils.Constants.FIREBASE_DATABASE_URL;
import static com.usi.hikemap.utils.Constants.USER_COLLECTION;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthProvider;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.usi.hikemap.model.AuthenticationResponse;
import com.usi.hikemap.model.User;

import java.util.HashMap;
import java.util.Map;

public class AuthenticationRepository implements IAuthenticationRepository{

    private final FirebaseAuth fAuth;
    private static FirebaseDatabase fDatabase;
    private static DatabaseReference fReference;
    private final Application mApplication;

    private final MutableLiveData<AuthenticationResponse> mAuthenticationResponse;
    private static final String TAG = "UserRepository";
    private String userId;

    public AuthenticationRepository(Application application) {
        fAuth = FirebaseAuth.getInstance();
        fDatabase = FirebaseDatabase.getInstance(FIREBASE_DATABASE_URL);
        mApplication = application;
        mAuthenticationResponse = new MutableLiveData<>();

    }

    @Override
    public MutableLiveData<AuthenticationResponse> createUserWithEmail(User user) {
        fAuth.createUserWithEmailAndPassword(user.getAuth(), user.getPassword()).addOnCompleteListener(ContextCompat.getMainExecutor(mApplication), new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                AuthenticationResponse authenticationResponse;
                if (task.isSuccessful()) {

                    Log.d(TAG, "onComplete: Connect for Authentication");
                    userId = fAuth.getCurrentUser().getUid();
                    authenticationResponse = new AuthenticationResponse();
                    authenticationResponse.setSuccess(true);

                    fReference = fDatabase.getReference().child(USER_COLLECTION).child(userId);

                    Map<String, Object> fUser = new HashMap<>();

                    fUser.put("name", user.getName());
                    fUser.put("surname", user.getSurname());
                    fUser.put("username", user.getUsername());
                    fUser.put("auth", user.getAuth());
                    fUser.put("account provider", FirebaseAuthProvider.PROVIDER_ID);
                    fUser.put("userId", userId);


                    fReference.setValue(fUser).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Log.d(TAG, "onSuccess: User profile is created for " + userId);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "onFailure: User profile is not created");
                        }
                    });
                    mAuthenticationResponse.postValue(authenticationResponse);
                }
            }
        });

        return mAuthenticationResponse;
    }

    @Override
    public MutableLiveData<AuthenticationResponse> loginWithEmail(String email, String password){
        if (email != null && !email.isEmpty() && password != null && !password.isEmpty())  {
            fAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(ContextCompat.getMainExecutor(mApplication), new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    AuthenticationResponse authenticationResponse;
                    if (task.isSuccessful()) {

                        Log.d(TAG, "onComplete: Login for Authentication");
                        authenticationResponse = new AuthenticationResponse();
                        authenticationResponse.setSuccess(true);

                    } else {

                        Log.d(TAG, "onComplete: Login failure", task.getException());
                        authenticationResponse = new AuthenticationResponse();
                        authenticationResponse.setSuccess(false);
                    }
                    mAuthenticationResponse.postValue(authenticationResponse);
                }
            });
        }
        return mAuthenticationResponse;
    }

    @Override
    public MutableLiveData<AuthenticationResponse> createUserWithPhone(User user, PhoneAuthCredential credential) {
        fAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                AuthenticationResponse authenticationResponse = new AuthenticationResponse();
                if (task.isSuccessful()) {

                    Log.d(TAG, "onComplete: Connect for Authentication with phone");

                    userId = fAuth.getCurrentUser().getUid();
                    authenticationResponse.setSuccess(true);

                    fReference = fDatabase.getReference().child(USER_COLLECTION).child(userId);

                    Map<String, Object> fUser = new HashMap<>();

                    fUser.put("name", user.getName());
                    fUser.put("surname", user.getSurname());
                    fUser.put("username", user.getUsername());
                    fUser.put("auth", user.getAuth());
                    fUser.put("account provider", PhoneAuthProvider.PROVIDER_ID);
                    fUser.put("userId", userId);

                    fReference.setValue(fUser).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Log.d(TAG, "onSuccess: User profile is created for " + userId);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "onFailure: User profile is not created");
                        }
                    });
                }
                mAuthenticationResponse.postValue(authenticationResponse);
            }
        });
        return mAuthenticationResponse;
    }

    @Override
    public MutableLiveData<AuthenticationResponse> createUserWithGoogle(GoogleSignInAccount account) {

        if (account != null) {
            AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
            fAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    AuthenticationResponse authenticationResponse = new AuthenticationResponse();
                    if (task.isSuccessful()) {

                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "onComplete: Connect for Authentication with google");
                        FirebaseUser userGoogle = fAuth.getCurrentUser();

                        //User userC = new User();
                        userId = userGoogle.getUid();
                        authenticationResponse.setSuccess(true);

                        fReference = fDatabase.getReference().child(USER_COLLECTION).child(userId);

                        Map<String, Object> user = new HashMap<>();

                        user.put("name", account.getGivenName());
                        user.put("surname", account.getFamilyName());
                        user.put("username", account.getEmail());
                        user.put("auth", account.getEmail());
                        user.put("account provider", GoogleAuthProvider.PROVIDER_ID);
                        user.put("userId", userId);
                        //user.put("Height", 0);
                        //user.put("Weight", 0);





                        fReference.setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                //Log.d(TAG, "onSuccess: User profile is created for " + userId);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, "onFailure: User profile is not created");
                            }
                        });
                    }
                    mAuthenticationResponse.postValue(authenticationResponse);
                }
            });
        }

        return mAuthenticationResponse;
    }
}
