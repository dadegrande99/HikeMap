package com.usi.hikemap.ui.fragment;

import static com.usi.hikemap.HikeMapOpenHelper.COLUMN_ALTITUDE;
import static com.usi.hikemap.HikeMapOpenHelper.COLUMN_LATITUDE;
import static com.usi.hikemap.HikeMapOpenHelper.COLUMN_LONGITUDE;
import static com.usi.hikemap.HikeMapOpenHelper.COLUMN_TIMESTAMP;
import static com.usi.hikemap.HikeMapOpenHelper.TABLE_NAME;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.usi.hikemap.HikeMapOpenHelper;
import com.usi.hikemap.R;
import com.usi.hikemap.model.Route;
import com.usi.hikemap.ui.viewmodel.GoViewModel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

/**
 * Fragment class for displaying a Google Map and tracking the user's location with a polyline.
 */
public class GoFragment extends Fragment implements OnMapReadyCallback, LocationListener {

    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private GoogleMap mMap;
    private PolylineOptions polylineOptions;
    private Polyline polyline;
    private Location lastLocation;


    private FloatingActionButton fStartButton, fStopButton, fPauseButton, fResumeButton;
    FrameLayout infoContainer;
    LinearLayout pauseLayout, playLayout;


    HikeMapOpenHelper databaseOpenHelper;
    SQLiteDatabase database;

    SensorManager sensorManager;
    Sensor accSensor;
    Sensor stepDetectorSensor;
    StepCounterListener sensorListener;


    TextView steps, km, calories, up, down;
    Chronometer chronometer;
    long tmillisec, tStart, tPauseDelta, tPauseStart = 0L;
    int sec, min;


    Handler handler;


    FusedLocationProviderClient fusedLocationProviderClient;


    private static String TAG = "GoFragment";

    GoViewModel mGoViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_go, container, false);
        infoContainer = rootView.findViewById(R.id.info);

        mGoViewModel = new ViewModelProvider(requireActivity()).get(GoViewModel.class);


        // Step counter sensor

        sensorManager = (SensorManager) getContext().getSystemService(Context.SENSOR_SERVICE);
        accSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);

        stepDetectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);

        // call database
        databaseOpenHelper = new HikeMapOpenHelper(this.getContext());
        database = databaseOpenHelper.getWritableDatabase();

        // Layouts for pause and play buttons
        pauseLayout = rootView.findViewById(R.id.pause_layout);
        playLayout = rootView.findViewById(R.id.play_layout);

        // Values for time, steps, km, calories, up and down
        steps = rootView.findViewById(R.id.steps_value);
        km = rootView.findViewById(R.id.km_value);
        calories = rootView.findViewById(R.id.kal_value);
        down = rootView.findViewById(R.id.down_value);
        up = rootView.findViewById(R.id.up_value);
        chronometer = rootView.findViewById(R.id.chronometer);

        handler = new Handler();

        // Set the visibility of the info container to GONE
        infoContainer.setVisibility(View.GONE);

        // Buttons for pause and play
        fStartButton = rootView.findViewById(R.id.startButton);
        fPauseButton = rootView.findViewById(R.id.pauseButton);
        fResumeButton = rootView.findViewById(R.id.resumeButton);
        fStopButton = rootView.findViewById(R.id.stopButton);


        fStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Start stats", Toast.LENGTH_SHORT).show();

                infoContainer.setVisibility(View.VISIBLE);
                infoContainer.requestLayout();
                fStartButton.setVisibility(View.GONE);

                steps.setText("0");
                km.setText("0");
                calories.setText("0");
                down.setText("0");
                up.setText("0");


                tStart = System.currentTimeMillis();
                handler.postDelayed(runnable, 0);
                chronometer.start();

                if (accSensor != null) {
                    sensorListener = new StepCounterListener(steps, database, lastLocation);
                    sensorManager.registerListener(sensorListener, accSensor, SensorManager.SENSOR_DELAY_NORMAL);
                    Toast.makeText(getContext(), R.string.start_text, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getContext(), R.string.acc_sensor_not_available, Toast.LENGTH_LONG).show();
                }

                if (stepDetectorSensor != null) {
                    sensorListener = new StepCounterListener(steps, lastLocation);
                    sensorManager.registerListener(sensorListener, stepDetectorSensor, SensorManager.SENSOR_DELAY_NORMAL);
                    Toast.makeText(getContext(), R.string.start_text, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getContext(), R.string.step_detector_sensor_not_available, Toast.LENGTH_LONG).show();
                }
            }
        });

        fPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Pause stats", Toast.LENGTH_SHORT).show();
                playLayout.setVisibility(View.GONE);
                pauseLayout.setVisibility(View.VISIBLE);
                tPauseStart = System.currentTimeMillis();
                chronometer.stop();
                handler.removeCallbacks(runnable);
                sensorListener.pauseCounter();
                sensorManager.unregisterListener(sensorListener, accSensor);
                sensorManager.unregisterListener(sensorListener, stepDetectorSensor);
            }
        });

        fResumeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Resume stats", Toast.LENGTH_SHORT).show();
                pauseLayout.setVisibility(View.GONE);
                playLayout.setVisibility(View.VISIBLE);
                tPauseDelta += System.currentTimeMillis() - tPauseStart;
                chronometer.start();
                handler.postDelayed(runnable, 0);
                //sensorListener.playCounter();
                if (accSensor != null) {
                    sensorListener = new StepCounterListener(steps, database, lastLocation);
                    sensorListener.playCounter();
                    sensorManager.registerListener(sensorListener, accSensor, SensorManager.SENSOR_DELAY_NORMAL);
                    Toast.makeText(getContext(), R.string.start_text, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getContext(), R.string.acc_sensor_not_available, Toast.LENGTH_LONG).show();
                }

                if (stepDetectorSensor != null) {
                    sensorListener = new StepCounterListener(steps, lastLocation);
                    sensorManager.registerListener(sensorListener, stepDetectorSensor, SensorManager.SENSOR_DELAY_NORMAL);
                    Toast.makeText(getContext(), R.string.start_text, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getContext(), R.string.step_detector_sensor_not_available, Toast.LENGTH_LONG).show();
                }
            }
        });

        fStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Stop stats", Toast.LENGTH_SHORT).show();
                playLayout.setVisibility(View.VISIBLE);
                pauseLayout.setVisibility(View.GONE);
                infoContainer.setVisibility(View.GONE);
                fStartButton.setVisibility(View.VISIBLE);
                tPauseDelta = 0L;
                chronometer.stop();
                handler.removeCallbacks(runnable);
                sensorManager.unregisterListener(sensorListener, accSensor);
                sensorManager.unregisterListener(sensorListener, stepDetectorSensor);

                // 3. Define the query to get the data
                // Definisci la query per ottenere le colonne di latitudine e longitudine
                String query = "SELECT " + COLUMN_LATITUDE + ", " + COLUMN_LONGITUDE + ", " + COLUMN_ALTITUDE + ", " + COLUMN_TIMESTAMP + " FROM " + TABLE_NAME;

                List<Route> routes = new ArrayList<>();

                // Esegui la query per ottenere i dati
                Cursor cursor = database.rawQuery(query, null);
                if(cursor != null) {
                    while (cursor.moveToNext()) {

                        // Estrai i valori dalle colonne
                        @SuppressLint("Range") double latitude = cursor.getDouble(cursor.getColumnIndex(COLUMN_LATITUDE));
                        @SuppressLint("Range") double longitude = cursor.getDouble(cursor.getColumnIndex(COLUMN_LONGITUDE));
                        @SuppressLint("Range") double altitude = cursor.getDouble(cursor.getColumnIndex(COLUMN_ALTITUDE));
                        @SuppressLint("Range") String timestamp = cursor.getString(cursor.getColumnIndex(COLUMN_TIMESTAMP));

                        Route route = new Route(
                                0,
                                0,
                                timestamp,
                                altitude,
                                longitude,
                                latitude
                        );

                        routes.add(route);
                        Log.d(TAG, "Route: " + routes.toString());

                    }

                    // Chiudi il cursore dopo aver finito di utilizzarlo
                    cursor.close();

                    // 4. Update the route
                    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    mGoViewModel.updateRoute(userId, routes).observe(getViewLifecycleOwner(), authenticationResponse -> {
                        if (authenticationResponse != null) {
                            if (authenticationResponse.isSuccess()) {
                                Log.d(TAG, "Success upload coordinate");
                            } else {
                                Log.d(TAG, "Error don't success");
                            }
                        }
                    });
                }
                /*
                // Assicurati che il cursore sia valido
                if (cursor != null) {
                    // Verifica se ci sono almeno un risultato
                    if (cursor.moveToFirst()) {
                        do {
                            // Estrai i valori dalle colonne
                            @SuppressLint("Range") double latitude = cursor.getDouble(cursor.getColumnIndex(COLUMN_LATITUDE));
                            @SuppressLint("Range") double longitude = cursor.getDouble(cursor.getColumnIndex(COLUMN_LONGITUDE));
                            @SuppressLint("Range") double altitude = cursor.getDouble(cursor.getColumnIndex(COLUMN_ALTITUDE));
                            @SuppressLint("Range") String timestamp = cursor.getString(cursor.getColumnIndex(COLUMN_TIMESTAMP));
                            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                            if (userId != null) {

                                List<Route> routes;


                                for (routes = new ArrayList<>(); cursor.moveToNext(); routes.add(new Route(
                                        0,
                                        0,
                                        timestamp,
                                        altitude,
                                        longitude,
                                        latitude
                                )));

                                mGoViewModel.updateRoute(userId, routes).observe(getViewLifecycleOwner(), authenticationResponse -> {
                                    if (authenticationResponse != null) {
                                        if (authenticationResponse.isSuccess()) {
                                            Log.d(TAG, "Success upload coordinate");
                                        } else {
                                            Log.d(TAG, "Error don't success");
                                        }
                                    }
                                });

                            }

                            // Stampa i valori sulla console usando Log
                            Log.d("TAG", "Latitude: " + latitude + ", Longitude: " + longitude);
                        } while (cursor.moveToNext());
                    }

                    // Chiudi il cursore dopo aver finito di utilizzarlo
                    cursor.close();
                } else {
                    Log.e("TAG", "Cursor is null");
                }

                 */
            }
        });


        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment == null) {
            mapFragment = SupportMapFragment.newInstance();
            getChildFragmentManager().beginTransaction().replace(R.id.map, mapFragment).commit();
        }

        mapFragment.getMapAsync(this);

        return rootView;
    }

    public Runnable runnable = new Runnable() {
        @Override
        public void run() {
            tmillisec = System.currentTimeMillis() - tStart - tPauseDelta;
            sec = (int) (tmillisec / 1000);
            min = sec / 60;
            sec = sec % 60;
            chronometer.setText(String.format("%02d", min) + ":" + String.format("%02d", sec));
            handler.postDelayed(this, 60);
        }
    };

    @Override
    public void onMapReady(GoogleMap googleMap) {
        // Assign the GoogleMap instance to the local variable mMap
        mMap = googleMap;

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // I permessi sono già concessi
            enableMyLocation();
        } else {
            // Richiedi i permessi all'utente
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        }
    }

    private void enableMyLocation() {
        // Verifica nuovamente i permessi (questo è solo un controllo aggiuntivo)
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);

            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext());
            Task<Location> locationTask = fusedLocationProviderClient.getLastLocation();
            locationTask.addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));
                        lastLocation = location;
                    }
                }
            });

            // Request location updates from the GPS provider
            LocationManager locationManager = (LocationManager) requireContext().getSystemService(Context.LOCATION_SERVICE);
            if (locationManager != null) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10, this);
            }
        } else {
            // L'utente ha negato il permesso, gestisci di conseguenza
            // Mostra un messaggio o guida l'utente verso le impostazioni dell'app per concedere i permessi manualmente
            Toast.makeText(requireContext(), "Permission denied. Please grant location permission in app settings.", Toast.LENGTH_LONG).show();
        }
    }


    /**
     * Callback method invoked when the user's location has changed.
     * This method is called when a new location update is received.
     *
     * @param location The new Location representing the user's current location.
     */
    @Override
    public void onLocationChanged(Location location) {
        // Center the camera on the user's location when the first location is received
        if (location == null) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 15));
        }

        // Draw polyline on the map
        if (polylineOptions == null) {
            // Initialize PolylineOptions with specified color and width
            polylineOptions = new PolylineOptions().color(Color.BLUE).width(10);
        }

        // Add the new location to the polyline
        polylineOptions.add(new LatLng(location.getLatitude(), location.getLongitude()));

        // Remove the previous polyline if it exists
        if (polyline != null) {
            polyline.remove();
        }

        // Add the updated polyline to the map
        polyline = mMap.addPolyline(polylineOptions);

        // Update the last known location
        lastLocation = location;
    }

    /**
     * Callback method invoked when the user responds to a permission request.
     * This method is called when the user grants or denies a requested permission.
     *
     * @param requestCode The request code passed when requesting permissions.
     * @param permissions The requested permissions.
     * @param grantResults The results of the permission requests.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_LOCATION_PERMISSION && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Permesso concesso, abilita la posizione
            enableMyLocation();
        } else {
            // Permesso negato, gestisci di conseguenza
            // Mostra un messaggio o guida l'utente verso le impostazioni dell'app per concedere i permessi manualmente
            Toast.makeText(requireContext(), "Permission denied. Please grant location permission in app settings.", Toast.LENGTH_LONG).show();
        }
    }
}



class  StepCounterListener implements SensorEventListener {

    private long lastSensorUpdate = 0;
    public static int accStepCounter = 0;
    public static int stepDetectorCounter = 0;
    ArrayList<Integer> accSeries = new ArrayList<Integer>();
    ArrayList<String> timestampsSeries = new ArrayList<String>();
    private double accMag = 0;
    private int lastAddedIndex = 1;
    int stepThreshold = 6;

    private int step = 1;

    TextView stepCountsView;

    private SQLiteDatabase database;

    private String timestamp;
    private String day;
    private String hour;

    private Location location;


    public StepCounterListener(TextView stepCountsView, SQLiteDatabase databse, Location location) {
        this.stepCountsView = stepCountsView;
        this.database = databse;
        this.location = location;
    }

    public StepCounterListener(TextView stepCountsView, Location location) {
        this.stepCountsView = stepCountsView;
        this.location = location;

        //Log.d("Prova", "ProvaLocation" + this.location.toString());
    }

    public void pauseCounter() {
        this.step = 0;
        Log.d("PAUSE", Integer.toString(this.step));

    }

    public void playCounter() {
        this.step = 1;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        switch (sensorEvent.sensor.getType()) {
            case Sensor.TYPE_LINEAR_ACCELERATION:

                float x = sensorEvent.values[0];
                float y = sensorEvent.values[1];
                float z = sensorEvent.values[2];

                long currentTimeInMilliSecond = System.currentTimeMillis();

                long timeInMillis = currentTimeInMilliSecond + (sensorEvent.timestamp - SystemClock.elapsedRealtimeNanos()) / 1000000;

                // Convert the timestamp to date
                SimpleDateFormat jdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
                jdf.setTimeZone(TimeZone.getTimeZone("GMT+1"));
                String sensorEventDate = jdf.format(timeInMillis);


                if ((currentTimeInMilliSecond - lastSensorUpdate) > 1000) {
                    lastSensorUpdate = currentTimeInMilliSecond;
                    String sensorRawValues = "  x = " + String.valueOf(x) + "  y = " + String.valueOf(y) + "  z = " + String.valueOf(z);
                    Log.d("Acc. Event", "last sensor update at " + String.valueOf(sensorEventDate) + sensorRawValues);
                }

                accMag = Math.sqrt(x * x + y * y + z * z);


                accSeries.add((int) accMag);

                // Get the date, the day and the hour
                timestamp = sensorEventDate;
                day = sensorEventDate.substring(0, 10);
                hour = sensorEventDate.substring(11, 13);

                Log.d("SensorEventTimestampInMilliSecond", timestamp);


                timestampsSeries.add(timestamp);
                peakDetection();

                break;

            case Sensor.TYPE_STEP_DETECTOR:
                countSteps(sensorEvent.values[0]);

                break;

        }


    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    private void peakDetection() {

        int windowSize = 20;
        /* Peak detection algorithm derived from: A Step Counter Service for Java-Enabled Devices Using a Built-In Accelerometer Mladenov et al.
         */
        int currentSize = accSeries.size(); // get the length of the series
        if (currentSize - lastAddedIndex < windowSize) { // if the segment is smaller than the processing window size skip it
            return;
        }

        List<Integer> valuesInWindow = accSeries.subList(lastAddedIndex, currentSize);
        List<String> timePointList = timestampsSeries.subList(lastAddedIndex, currentSize);
        lastAddedIndex = currentSize;

        for (int i = 1; i < valuesInWindow.size() - 1; i++) {
            int forwardSlope = valuesInWindow.get(i + 1) - valuesInWindow.get(i);
            int downwardSlope = valuesInWindow.get(i) - valuesInWindow.get(i - 1);

            if (forwardSlope < 0 && downwardSlope > 0 && valuesInWindow.get(i) > stepThreshold) {
                accStepCounter += step;
                Log.d("ACC STEPS: ", String.valueOf(accStepCounter));
                Log.d("PAUSE", Integer.toString(this.step));
                stepCountsView.setText(String.valueOf(accStepCounter));

                ContentValues databaseEntry = new ContentValues();
                databaseEntry.put(HikeMapOpenHelper.COLUMN_TIMESTAMP, timePointList.get(i));

                databaseEntry.put(HikeMapOpenHelper.COLUMN_DAY, this.day);
                databaseEntry.put(HikeMapOpenHelper.COLUMN_HOUR, this.hour);

                // TODO: add latitude, longitude and altitude
                databaseEntry.put(COLUMN_LATITUDE, this.location.getLatitude());
                databaseEntry.put(COLUMN_LONGITUDE, this.location.getLongitude());
                databaseEntry.put(HikeMapOpenHelper.COLUMN_ALTITUDE, location.getAltitude());

                database.insert(TABLE_NAME, null, databaseEntry);

            }
        }
    }

    private void countSteps(float step) {
        stepDetectorCounter += step;

        Log.d("STEP_DETECTOR STEPS: ", String.valueOf(stepDetectorCounter));

    }
}
