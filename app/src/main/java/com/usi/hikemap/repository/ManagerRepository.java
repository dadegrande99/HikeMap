package com.usi.hikemap.repository;

import static com.usi.hikemap.utils.Constants.FIREBASE_DATABASE_URL;
import static com.usi.hikemap.utils.Constants.USER_COLLECTION;

import android.app.Application;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.usi.hikemap.model.AuthenticationResponse;
import com.usi.hikemap.model.User;

import java.util.HashMap;
import java.util.Map;

public class ManagerRepository implements IManagerRepository {

    private static FirebaseAuth fAuth;
    private static FirebaseDatabase fDatabase;
    private static DatabaseReference fReference;
    private static StorageReference fPhotoReference, fStorageReference;
    private static FirebaseStorage fStore;

    private final MutableLiveData<AuthenticationResponse> mAuthenticationResponse;
    private final MutableLiveData<User> mUserLiveData;
    private String userId, path;

    private static final String TAG = "ManageRepository";

    public ManagerRepository(Application application) {
        fAuth = FirebaseAuth.getInstance();
        fDatabase = FirebaseDatabase.getInstance(FIREBASE_DATABASE_URL);

        fStore = FirebaseStorage.getInstance();
        //fStorageRe = FirebaseStorage.getInstance().getReference();

        mAuthenticationResponse = new MutableLiveData<>();
        mUserLiveData = new MutableLiveData<>();
    }


    @Override
    public MutableLiveData<User> readUser(String userId) {
        fReference = fDatabase.getReference();
        fReference.child(USER_COLLECTION).child(userId).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    User user = new User(task.getResult().child("name").getValue(String.class),task.getResult().child("surname").getValue(String.class),
                            task.getResult().child("username").getValue(String.class), task.getResult().child("auth").getValue(String.class),
                            task.getResult().child("password").getValue(String.class), task.getResult().child("userId").getValue(String.class),
                            task.getResult().child("account provider").getValue(String.class), task.getResult().child("image").getValue(String.class),
                            task.getResult().child("path").getValue(String.class), task.getResult().child("height").getValue(String.class),
                            task.getResult().child("weight").getValue(String.class), task.getResult().child("birthdate").getValue(String.class), task.getResult().child("gendre").getValue(String.class));

                    Log.d(TAG, "onComplete: readUser: " + task.getResult().getValue(User.class));
                    mUserLiveData.postValue(user);
                    //mUserLiveData.postValue(task.getResult().getValue(User.class)); //bisogna avere un costruttore vuoto nella classe user
                }
                else {
                    Log.d(TAG, "Error getting data", task.getException());
                    mUserLiveData.postValue(null);
                }
            }
        });

        return mUserLiveData;
    }


    @Override
    public MutableLiveData<AuthenticationResponse> readImage(String uId) {
        fPhotoReference = fStore.getReference().child("profile_picture/" + uId);
        fPhotoReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {

                path = uri.toString();
                Map<String, Object> data = new HashMap<>();
                data.put("path", path);

                updateData(data);

                AuthenticationResponse authenticationResponse = new AuthenticationResponse();
                authenticationResponse.setSuccess(true);
                mAuthenticationResponse.postValue(authenticationResponse);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
        return mAuthenticationResponse;
    }

    @Override
    public MutableLiveData<AuthenticationResponse> writeImage(Uri profileUri) {

        fStorageReference = fStore.getReference().child("profile_picture").child(FirebaseAuth.getInstance().getUid());
        fStorageReference.putFile(profileUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                AuthenticationResponse authenticationResponse = new AuthenticationResponse();
                authenticationResponse.setMessage(taskSnapshot.toString());
                mAuthenticationResponse.postValue(authenticationResponse);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
        return mAuthenticationResponse;
    }

    @Override
    public MutableLiveData<AuthenticationResponse> updateData(Map<String, Object> data) {
        userId = fAuth.getCurrentUser().getUid();
        fReference = fDatabase.getReference().child(USER_COLLECTION).child(userId);
        fReference.updateChildren(data).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                AuthenticationResponse authenticationResponse = new AuthenticationResponse();
                authenticationResponse.setSuccess(true);
                mAuthenticationResponse.postValue(authenticationResponse);
                Log.d(TAG, "data update " + userId);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                AuthenticationResponse authenticationResponse = new AuthenticationResponse();
                authenticationResponse.setSuccess(false);
                mAuthenticationResponse.postValue(authenticationResponse);
                Log.d(TAG, "onFailure: data not update");
            }
        });
        return mAuthenticationResponse;
    }

    @Override
    public MutableLiveData<AuthenticationResponse> deleteAccount(String userId) {

        fReference = fDatabase.getReference().child(USER_COLLECTION).child(userId);

        // Remove the user from the database
        fReference.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                fAuth.getCurrentUser().delete();
                AuthenticationResponse authenticationResponse = new AuthenticationResponse();
                authenticationResponse.setSuccess(true);
                mAuthenticationResponse.postValue(authenticationResponse);
            }
        }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    AuthenticationResponse authenticationResponse = new AuthenticationResponse();
                    authenticationResponse.setSuccess(true);
                    mAuthenticationResponse.postValue(authenticationResponse);
                }
            });

        return mAuthenticationResponse;

    }
}
