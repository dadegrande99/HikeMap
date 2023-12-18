package com.usi.hikemap.ui.fragment;

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

    //TODO2: ADD 2 buttons, save

    MeowBottomNavigation bottomNavigation;

    TextView Height, Weight, Sex;
    String userId;

    TextView birthdate;

    Button date_button;

    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());

    private Calendar myCalendar;

    private Spinner genderSpinner;

    CircleImageView profilePic;

    Date dataDiNascita = null;

    private ProfileViewModel ProfileViewModel;
    public User User;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.arrow_left);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Perform the redirection to the main fragment here
                //getActivity().onBackPressed();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frame_layout, new ProfileFragment())
                        .commit();
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

        //
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                getContext(), R.array.gender_options, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(adapter);

        genderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedGender = parentView.getItemAtPosition(position).toString();
                Sex.setText("Result: " + selectedGender);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                Toast.makeText(getContext(), "Sex not provided", Toast.LENGTH_SHORT);
            }
        });
        //

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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        MenuHost menuHost = requireActivity();

        menuHost.addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                // Add menu items here
                menuInflater.inflate(R.menu.menu_update_profile, menu);
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {

                if (menuItem.getItemId() == R.id.spunta) {

                    //set new values to user
                    User.setHeight(Height.getText().toString());
                    User.setWeight(Weight.getText().toString());
                    User.setSex(Sex.getText().toString());
                    User.setBirthdate(birthdate.getText().toString());

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
                                Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    Toast.makeText(getActivity(), "Saved", Toast.LENGTH_SHORT).show();
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.frame_layout, new ProfileFragment())
                            .commit();
                    bottomNavigation.setVisibility(View.VISIBLE);
                }

                if (menuItem.getItemId() == R.id.cross) {
                    Toast.makeText(getActivity(), "Abort change", Toast.LENGTH_SHORT).show();
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.frame_layout, new ProfileFragment())
                            .commit();
                    bottomNavigation.setVisibility(View.VISIBLE);
                }

                return true;
            }
        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);
    }
}