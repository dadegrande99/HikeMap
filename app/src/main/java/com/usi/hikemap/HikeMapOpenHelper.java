package com.usi.hikemap;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import com.usi.hikemap.model.Route;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


public class HikeMapOpenHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "HikeMap";
    public static final String TABLE_NAME = "steps";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_SUBROUTE = "subroute";
    public static final String COLUMN_IDROUTE = "idRoute";
    public static final String COLUMN_TIMESTAMP = "timestamp";
    public static final String COLUMN_DAY = "day";
    public static final String COLUMN_HOUR = "hour";
    public static final String COLUMN_MINUTE = "minute";
    public static final String COLUMN_LATITUDE = "latitude";
    public static final String COLUMN_LONGITUDE = "longitude";
    public static final String COLUMN_ALTITUDE = "altitude";
    private static final String CREATE_TABLE_SQL =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    COLUMN_SUBROUTE + " INTEGER," +
                    COLUMN_IDROUTE + " INTEGER," +
                    COLUMN_TIMESTAMP + " INTEGER," +
                    COLUMN_DAY + " INTEGER," +
                    COLUMN_HOUR + " INTEGER," +
                    COLUMN_MINUTE + " INTEGER," +
                    COLUMN_LATITUDE + " REAL," +
                    COLUMN_LONGITUDE + " REAL," +
                    COLUMN_ALTITUDE + " REAL" +
                    ");";

    private static final String TAG = "HikeMapOpenHelper";



    public HikeMapOpenHelper(Context context) {
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        sqLiteDatabase.execSQL(CREATE_TABLE_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    /**
     * Loads the list of routes from the database.
     *
     * @param context The context of the calling activity or application.
     * @return A list of Route objects containing latitude, longitude, altitude, and timestamp.
     */
    public static String loadLastRouteID(Context context) {

        // Define the query to retrieve columns of latitude, longitude, altitude, and timestamp
        String query = "SELECT " + COLUMN_IDROUTE + " FROM " + TABLE_NAME + " ORDER BY " + COLUMN_IDROUTE + " DESC LIMIT 1";

        // Create an instance of the database helper to get a readable database
        HikeMapOpenHelper databaseHelper = new HikeMapOpenHelper(context);
        SQLiteDatabase database = databaseHelper.getReadableDatabase();

        // Execute the query to retrieve data
        Cursor cursor = database.rawQuery(query, null);
        String lastIdRoute = null;
        if (cursor != null) {
            while (cursor.moveToNext()) {

                // Extract values from columns
                @SuppressLint("Range") String tmpLastIdRoute = cursor.getString(cursor.getColumnIndex(COLUMN_IDROUTE));
                lastIdRoute = tmpLastIdRoute;
            }
            cursor.close();
        }
        // Close the database connection
        database.close();

        // Return the list of Route objects
        return lastIdRoute;
    }


    public static List<Route> loadRoutes(String idRoute, Context context) {

        // List to store Route objects
        List<Route> data = new ArrayList<>();

        // Define the query to retrieve columns of latitude, longitude, altitude, and timestamp
        String query = "SELECT  * FROM " + TABLE_NAME + " WHERE " + COLUMN_IDROUTE + " = " + idRoute;

        // Create an instance of the database helper to get a readable database
        HikeMapOpenHelper databaseHelper = new HikeMapOpenHelper(context);
        SQLiteDatabase database = databaseHelper.getReadableDatabase();

        // Execute the query to retrieve data
        Cursor cursor = database.rawQuery(query, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {

                // Extract values from columns
                @SuppressLint("Range") double latitude = cursor.getDouble(cursor.getColumnIndex(COLUMN_LATITUDE));
                @SuppressLint("Range") double longitude = cursor.getDouble(cursor.getColumnIndex(COLUMN_LONGITUDE));
                @SuppressLint("Range") double altitude = cursor.getDouble(cursor.getColumnIndex(COLUMN_ALTITUDE));
                @SuppressLint("Range") String timestamp = cursor.getString(cursor.getColumnIndex(COLUMN_TIMESTAMP));
                @SuppressLint("Range") int id = Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_ID)));

                // Create a Route object with extracted values
                Route route = new Route(
                        id,
                        idRoute,
                        0,
                        timestamp,
                        altitude,
                        longitude,
                        latitude
                );

                // Add the Route object to the list
                data.add(route);

                // Log the details of the added Route
                Log.d(TAG, "Route: " + data.toString());
            }
            cursor.close();
        }
        // Close the database connection
        database.close();

        // Return the list of Route objects
        return data;
    }



    // Load all records in the database
    public static void loadRecords(Context context){
        List<String> dates = new LinkedList<String>();
        HikeMapOpenHelper databaseHelper = new HikeMapOpenHelper(context);
        SQLiteDatabase database = databaseHelper.getReadableDatabase();

        String [] columns = new String [] {HikeMapOpenHelper.COLUMN_TIMESTAMP};
        Cursor cursor = database.query(HikeMapOpenHelper.TABLE_NAME, columns, null, null, HikeMapOpenHelper.COLUMN_TIMESTAMP,
                null, null );

        // iterate over returned elements
        cursor.moveToFirst();
        for (int index=0; index < cursor.getCount(); index++){
            dates.add(cursor.getString(0));
            cursor.moveToNext();
        }
        database.close();

        Log.d("STORED TIMESTAMPS: ", String.valueOf(dates));
    }

    // load records from a single day
    public static Integer loadSingleRecord(Context context, String date){
        List<String> steps = new LinkedList<String>();
        // Get the readable database
        HikeMapOpenHelper databaseHelper = new HikeMapOpenHelper(context);
        SQLiteDatabase database = databaseHelper.getReadableDatabase();

        String where = HikeMapOpenHelper.COLUMN_DAY + " = ?";
        String [] whereArgs = { date };

        Cursor cursor = database.query(HikeMapOpenHelper.TABLE_NAME, null, where, whereArgs, null,
                null, null );

        // iterate over returned elements
        cursor.moveToFirst();
        for (int index=0; index < cursor.getCount(); index++){
            steps.add(cursor.getString(0));
            cursor.moveToNext();
        }
        database.close();

        Integer numSteps = steps.size();
        Log.d("STORED STEPS TODAY: ", String.valueOf(numSteps));
        return numSteps;
    }

    public static void deleteRecords (Context context) {
        HikeMapOpenHelper databaseHelper = new HikeMapOpenHelper(context);
        SQLiteDatabase database = databaseHelper.getWritableDatabase();

        int numberDeletedRecords = 0;

        numberDeletedRecords = database.delete(HikeMapOpenHelper.TABLE_NAME, null, null);
        database.close();

        Toast.makeText(context, "Deleted + "+ String.valueOf(numberDeletedRecords) + " steps", Toast.LENGTH_LONG).show();

    }

    public static Map<String, Integer> loadStepsByDay(Context context, String date){
        // 1. Define a map to store the hour and number of steps as key-value pairs
        Map<String, Integer>  map = new TreeMap<>();

        // 2. Get the readable database
        HikeMapOpenHelper databaseHelper = new HikeMapOpenHelper(context);
        SQLiteDatabase database = databaseHelper.getReadableDatabase();


        //Manage Monday & Sunday
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate data = LocalDate.parse(date, formatter);
        DayOfWeek dayOfWeek = data.getDayOfWeek();
        // Calculates the difference in days between the current day of the week and Monday (DayOfWeek.MONDAY)
        int differenceDays = DayOfWeek.MONDAY.getValue() - dayOfWeek.getValue();
        // Get the Monday of the week by subtracting the difference of days from the date
        LocalDate monday = data.plusDays(differenceDays);
        // Get Sunday by adding 6 days to Monday
        LocalDate sunday = monday.plusDays(6);

        // 3. Define the query to get the data
        String query = "SELECT day, COUNT(*)  FROM num_steps " +
                "WHERE day BETWEEN \"" + monday.format(formatter) +
                "\"  AND \"" + sunday.format(formatter) +
                "\" GROUP BY day ORDER BY day ASC";
        Cursor cursor = database.rawQuery(query, new String[] {});

        String tmpKey;
        Integer tmpValue = 0;

        // Define the week
        for (int i = 0; i < 7; i++){
            tmpKey = monday.plusDays(i).format(formatter);
            map.put(tmpKey, tmpValue);
        }
        // 4. Iterate over returned elements on the cursor
        cursor.moveToFirst();

        for (int i = 0; i < cursor.getCount(); i++){
            tmpKey = cursor.getString(0);
            tmpValue = Integer.parseInt(cursor.getString(1));

            //2. Put the data from the database into the map
            map.put(tmpKey, tmpValue);
            cursor.moveToNext();
        }


        // 5. Close the cursor and database
        cursor.close();
        database.close();

        // 6. Return the map with hours and number of steps
        return map;
    }

    public static Map<Integer, Integer> loadStepsByHour(Context context, String date){
        // 1. Define a map to store the hour and number of steps as key-value pairs
        Map<Integer, Integer>  map = new HashMap<>();

        // 2. Get the readable database
        HikeMapOpenHelper databaseHelper = new HikeMapOpenHelper(context);
        SQLiteDatabase database = databaseHelper.getReadableDatabase();

        // 3. Define the query to get the data
        Cursor cursor = database.rawQuery("SELECT hour, COUNT(*)  FROM num_steps " +
                "WHERE day = ? GROUP BY hour ORDER BY  hour ASC ", new String [] {date});

        // 4. Iterate over returned elements on the cursor
        cursor.moveToFirst();
        for (int index=0; index < cursor.getCount(); index++){
            Integer tmpKey = Integer.parseInt(cursor.getString(0));
            Integer tmpValue = Integer.parseInt(cursor.getString(1));

            //2. Put the data from the database into the map
            map.put(tmpKey, tmpValue);


            cursor.moveToNext();
        }

        // 5. Close the cursor and database
        cursor.close();
        database.close();

        // 6. Return the map with hours and number of steps
        return map;
    }


}
