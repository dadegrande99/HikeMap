package com.usi.hikemap.ui.fragment;

import static com.usi.hikemap.utils.Constants.DEFAULT_WEB_CLIENT_ID;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuHost;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthProvider;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.PhoneAuthProvider;
import com.usi.hikemap.MainActivity;
import com.usi.hikemap.R;
import com.usi.hikemap.model.User;
import com.usi.hikemap.ui.authentication.AuthenticationActivity;
import com.usi.hikemap.ui.viewmodel.ProfileViewModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class UpdateProfileFragment extends Fragment {


    MeowBottomNavigation bottomNavigation;
    TextView Height, Weight, Sex, birthdate, mLogout, mDeleteAccount;
    String userId;
    Button date_button;
    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
    private Calendar myCalendar;
    private Spinner genderSpinner;
    GoogleSignInClient mGoogleSignInClient;
    CircleImageView profilePic;
    Date dataDiNascita = null;
    private ProfileViewModel ProfileViewModel;
    public User User;
    private ProfileViewModel mProfileViewModel;
    private FirebaseAuth fAuth;

    private String TAG = "UpdateProfileFragment";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);


        mProfileViewModel = new ViewModelProvider(requireActivity()).get(ProfileViewModel.class);
        fAuth = FirebaseAuth.getInstance();

        setHasOptionsMenu(true);

        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.arrow_left);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Perform the redirection to the main fragment here
                getActivity().onBackPressed();
                bottomNavigation.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_update_profile, container, false);

        bottomNavigation = getActivity().findViewById(R.id.bottomNavigation);
        bottomNavigation.setVisibility(View.GONE);

        ProfileViewModel = new ViewModelProvider(requireActivity()).get(ProfileViewModel.class);
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(DEFAULT_WEB_CLIENT_ID)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), googleSignInOptions);

        mLogout = root.findViewById(R.id.logout_textView);
        mDeleteAccount = root.findViewById(R.id.delete_textView);

        Height = root.findViewById(R.id.editTextHeight);
        Weight = root.findViewById(R.id.editTextWeight);
        profilePic = root.findViewById(R.id.profile_pic);


        birthdate = root.findViewById(R.id.birthdate);
        myCalendar = Calendar.getInstance();

        date_button = root.findViewById(R.id.date_button);
        Drawable icon = getResources().getDrawable(R.drawable.baseline_edit_calendar_24);
        date_button.setCompoundDrawablesWithIntrinsicBounds(null, icon, null, null);

        Sex = root.findViewById(R.id.sex);
        genderSpinner = root.findViewById(R.id.genderSpinner);


        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                someActivityResultLauncher.launch(intent);
                Log.d("Update Profile", " clicked on img");
            }
        });

        date_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                openDialog();
            }

        });


        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                getContext(), R.array.gender_options, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(adapter);

        genderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedGender = parentView.getItemAtPosition(position).toString();
                Log.d(TAG, selectedGender.toString());
                Sex.setText(selectedGender);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                Toast.makeText(getContext(), "Sex not provided", Toast.LENGTH_SHORT);
            }
        });


        getActivity().setTitle("Update Profile");

        //TRY TO DELETE IF
        if (userId != null) {
            ProfileViewModel.readUser(userId).observe(getViewLifecycleOwner(), user -> {
                if (user != null) {

                    getActivity().setTitle(user.getName());
                    User = user;
                    Height.setText(String.valueOf(User.getHeight()));
                    if (String.valueOf(User.getHeight()).equals(null)){
                        Height.setText("Height");
                    }
                    Weight.setText(String.valueOf(User.getWeight()));
                    birthdate.setText(String.valueOf(User.getBirthdate()));

                    //if(String.valueOf(User.getSex()).equals(null)){
                    Sex.setText(String.valueOf(User.getSex()));


                    ProfileViewModel.readImage(userId).observe(getViewLifecycleOwner(), authenticationResponse-> {
                        if (authenticationResponse != null) {
                            if (authenticationResponse.isSuccess() && User.getPath() != null) {
                                Glide.with(getContext())
                                        .load(User.getPath())
                                        //.signature(new ObjectKey(String.valueOf(System.currentTimeMillis())))
                                        .into(profilePic);
                                //Log.d("Update Profile", "onClick: Updated image");
                            }
                            else {
                                Log.d("Update Profile", "onClick: Error don't update image");
                            }
                        }
                    });
                }
            });
        }

        mLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mProfileViewModel.readUser(userId).observe(getViewLifecycleOwner(), user -> {

                    if (user != null) {
                        User = user;
                        if (User.getProvider().equals(GoogleAuthProvider.PROVIDER_ID)) {
                            Log.d(TAG, "onOptionsItemSelected: " + GoogleAuthProvider.PROVIDER_ID);
                            mGoogleSignInClient.signOut();
                        } else if (User.getProvider().equals(FirebaseAuthProvider.PROVIDER_ID)) {
                            Log.d(TAG, "onOptionsItemSelected: " + FirebaseAuthProvider.PROVIDER_ID);
                            FirebaseAuth.getInstance().signOut();
                        } else if (User.getProvider().equals(PhoneAuthProvider.PROVIDER_ID)) {
                            Log.d(TAG, "onOptionsItemSelected: " + PhoneAuthProvider.PROVIDER_ID);
                            FirebaseAuth.getInstance().signOut();
                        } else {
                            Log.d(TAG, "onComplete: Fatal");
                            return;
                        }
                    }
                });

                startActivity(new Intent(getActivity(), AuthenticationActivity.class));
            }

        });
        mDeleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Observe user data changes using ViewModel
                mProfileViewModel.readUser(userId).observe(getViewLifecycleOwner(), user -> {
                    if (user != null) {
                        User = user;
                        if (User.getProvider().equals(GoogleAuthProvider.PROVIDER_ID)) {
                            // If the user is signed in with Google, revoke access
                            mGoogleSignInClient.revokeAccess().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    // Delete the account in the database
                                    mProfileViewModel.deleteAccount(userId).observe(getViewLifecycleOwner(), authenticationResponse -> {
                                        if (authenticationResponse != null) {
                                            if (authenticationResponse.isSuccess()) {
                                                Log.d(TAG, "Delete account");
                                            } else {
                                                Log.d(TAG, "Error: Account not deleted");
                                            }
                                        }
                                    });
                                }
                            });
                        } else if (User.getProvider().equals(FirebaseAuthProvider.PROVIDER_ID) || User.getProvider().equals(PhoneAuthProvider.PROVIDER_ID)) {
                            // If the user is signed in with Firebase or Phone, delete the account
                            fAuth.getCurrentUser().delete();
                            // Delete the account in the database
                            mProfileViewModel.deleteAccount(userId).observe(getViewLifecycleOwner(), authenticationResponse -> {
                                if (authenticationResponse != null) {
                                    if (authenticationResponse.isSuccess()) {
                                        Log.d(TAG, "Delete account");
                                    } else {
                                        Log.d(TAG, "Error: Account not deleted");
                                    }
                                }
                            });
                        } else {
                            Log.d(TAG, "onComplete: Fatal");
                        }
                    }
                });

                // Navigate to the authentication activity after account deletion
                startActivity(new Intent(getActivity(), AuthenticationActivity.class));
            }
        });


        return root;
    }


    private void openDialog(){

        DatePickerDialog dialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener(){
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day){
                birthdate.setText(String.valueOf(day)+ "-" + String.valueOf(month + 1)+ "-" + String.valueOf(year));
            }

        }, 2022, 07, 7);

        dialog.show();
    }


    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // There are no request codes
                        Intent data = result.getData();

                        if(data.getData() != null) {
                            Uri profileUri = data.getData();
                            profilePic.setImageURI(profileUri);
                            Log.d("Update Profile", "onClick: UPDATE");
                            ProfileViewModel.writeImage(profileUri).observe(getViewLifecycleOwner(), authenticationResponse -> {
                                if (authenticationResponse.isSuccess()) {
                                    Log.d("Update Profile", "onClick: Image update");
                                }
                                else {
                                    Log.d("Update Profile", "onClick: Error don't update image");
                                }
                            });
                        }
                    }
                }
            });

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_update_profile, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.comeback){
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_layout, new ProfileFragment())
                    .commit();

            User.setHeight(Height.getText().toString());
            User.setWeight(Weight.getText().toString());

            User.setSex(Sex.getText().toString());
            User.setBirthdate(birthdate.getText().toString());

            Log.d(TAG, "Prova height: " + User.getHeight());

            String bd = birthdate.getText().toString();


            try {
                dataDiNascita = sdf.parse(bd);
            } catch (ParseException e) {
                Toast.makeText(getContext(), "Birthdate not provided", Toast.LENGTH_SHORT);
            }

            Log.d("Update Profile", "after try catch");
            int age = 0;
            if (dataDiNascita != null) {
                Calendar calNascita = Calendar.getInstance();
                calNascita.setTime(dataDiNascita);

                Calendar calCorrente = Calendar.getInstance();

                age = calCorrente.get(Calendar.YEAR) - calNascita.get(Calendar.YEAR);

                // Verifica se il compleanno è già passato quest'anno
                if (calCorrente.get(Calendar.DAY_OF_YEAR) < calNascita.get(Calendar.DAY_OF_YEAR)) {
                    age--;
                }

                // 'anni' contiene ora l'età dell'utente

            } else {
                Log.e("Errore", "Impossibile convertire la data di nascita.");
            }

            User.setSex(Sex.getText().toString());

            Map<String, Object> data = new HashMap<>();
            data.put("height", User.getHeight());
            data.put("weight", User.getWeight());
            data.put("birthdate", User.getBirthdate());
            data.put("age", age);
            data.put("gendre", User.getSex());
            //Map<String, Object> dataWeight = new HashMap<>();

            //update image
            //update birthdate
            ProfileViewModel.updateProfile(data).observe(getViewLifecycleOwner(), authenticationResponse -> {
                if (authenticationResponse != null) {
                    if (authenticationResponse.isSuccess()) {
                        Toast.makeText(getActivity(), "Saved", Toast.LENGTH_SHORT).show();
                        getActivity().getSupportFragmentManager().beginTransaction()
                                .replace(R.id.frame_layout, new ProfileFragment())
                                .commit();
                        bottomNavigation.setVisibility(View.VISIBLE);
                    } else {
                        //Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}