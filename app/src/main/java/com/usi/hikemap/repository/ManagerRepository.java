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
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.usi.hikemap.model.AuthenticationResponse;
import com.usi.hikemap.model.Route;
import com.usi.hikemap.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ManagerRepository implements IManagerRepository {

    private static FirebaseAuth fAuth;
    private static FirebaseDatabase fDatabase;
    private static DatabaseReference fReference;
    private static StorageReference fPhotoReference, fStorageReference;
    private static FirebaseStorage fStore;

    private final MutableLiveData<AuthenticationResponse> mAuthenticationResponse;
    private final MutableLiveData<User> mUserLiveData;
    private final MutableLiveData<Route> mRouteLiveData;
    private final MutableLiveData<List<Route>> mRouteLiveDataL;
    private String userId, path;

    private static final String TAG = "ManageRepository";

    public ManagerRepository(Application application) {
        fAuth = FirebaseAuth.getInstance();
        fDatabase = FirebaseDatabase.getInstance(FIREBASE_DATABASE_URL);
        fStore = FirebaseStorage.getInstance();

        mAuthenticationResponse = new MutableLiveData<>();
        mUserLiveData = new MutableLiveData<>();
        mRouteLiveData = new MutableLiveData<>();
        mRouteLiveDataL = new MutableLiveData<>();
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

    @Override
    public MutableLiveData<AuthenticationResponse> updateRoute(String userId, List<Route> route) {

        Map<String, Object> data = new HashMap();
        data.put(route.get(0).getIdRoute(), route);
        
        fReference = fDatabase.getReference().child(USER_COLLECTION).child(userId).child("routes");

        fReference.updateChildren(data).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                AuthenticationResponse authenticationResponse = new AuthenticationResponse();
                authenticationResponse.setSuccess(true);
                mAuthenticationResponse.postValue(authenticationResponse);
                Log.d(TAG, "onCreate: Data update " + userId);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                AuthenticationResponse authenticationResponse = new AuthenticationResponse();
                authenticationResponse.setSuccess(false);
                mAuthenticationResponse.postValue(authenticationResponse);
                Log.d(TAG, "onFailure: Data not update");
            }
        });
        return mAuthenticationResponse;
    }

    @Override
    public MutableLiveData<List<Route>> readRoutes(String userId) {
        fReference = fDatabase.getReference();

        // Assuming that "routes" is a child node under the user's ID
        DatabaseReference routesReference = fReference.child(USER_COLLECTION).child(userId).child("routes");

        routesReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Route> routes = new ArrayList<>();

                for (DataSnapshot routeSnapshot : dataSnapshot.getChildren()) {
                    // Iterate through the children of "routes"
                    for (DataSnapshot subRouteSnapshot : routeSnapshot.getChildren()) {
                        Integer id = subRouteSnapshot.child("id").getValue(Integer.class);
                        String idRoute = subRouteSnapshot.child("idRoute").getValue(String.class);
                        Integer subRoute = subRouteSnapshot.child("subRoute").getValue(Integer.class);
                        String timestamp = subRouteSnapshot.child("timestamp").getValue(String.class);
                        Double altitude = subRouteSnapshot.child("altitude").getValue(Double.class);
                        Double latitude = subRouteSnapshot.child("latitude").getValue(Double.class);
                        Double longitude = subRouteSnapshot.child("longitude").getValue(Double.class);

                        // Check for null values before invoking methods
                        if (id != null && idRoute != null && subRoute != null && timestamp != null
                                && altitude != null && latitude != null && longitude != null) {

                            Route route = new Route(id, idRoute, subRoute, timestamp, altitude, latitude, longitude);
                            routes.add(route);
                            Log.d(TAG, "onDataChange: Route added: " + route.toString());
                        } else {
                            // Handle the case when any value is null
                            Log.d(TAG, "One or more values are null");
                        }
                    }
                }

                Log.d(TAG, "onDataChange: readRoutes: " + routes);
                // Post the list of routes to the LiveData
                mRouteLiveDataL.postValue(routes);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "Error getting data", databaseError.toException());
                mRouteLiveDataL.postValue(null);
            }
        });

        return mRouteLiveDataL;
    }

}
