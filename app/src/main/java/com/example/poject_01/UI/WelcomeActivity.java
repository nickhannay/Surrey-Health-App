package com.example.poject_01.UI;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;


import android.content.Context;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;


import com.example.poject_01.R;
import com.example.poject_01.model.Data;
import com.example.poject_01.model.DownloadRequest;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Displays background screen for dialog to show up
 * Handles methods for downloading/updating and reading data
 */
public class WelcomeActivity extends AppCompatActivity {
    private static Context mContext;
    private SharedPreferences prefs;
    private String restaurantsURL = "https://data.surrey.ca/api/3/action/package_show?id=restaurants";
    private String inspectionsURL = "https://data.surrey.ca/api/3/action/package_show?id=fraser-health-restaurant-inspection-reports";
    private FragmentManager downloadFrag ;
    private DownloadRequest restaurantsRequest;
    private DownloadRequest inspectionsRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mContext = WelcomeActivity.this;
        prefs = mContext.getSharedPreferences("startup_logic", MODE_PRIVATE);
        downloadFrag = getSupportFragmentManager();


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        boolean initial_update = prefs.getBoolean("initial_update", false);

        if (!initial_update) {
            // reads initial data set - ap/res/raw
            readWriteInitialData();

        }
        else {                   // reads data from internal storage:
            updateRestaurants(); // data/restaurantData/restaurants.csv
            updateInspections(); // data/restaurantData/inspections.csv
        }

        // comparing current time to last_update time
        long diffInHours = hoursSinceUpdate();
        if (diffInHours >= 20) {
            // 20 hours since last update
            makeDataGetRequest();
        }
        else{
            Intent intent = MapsActivity.getIntent(WelcomeActivity.this);
            startActivity(intent);
            finish();
        }
    }

    private void readWriteInitialData() {
        // reads data from data_restaurants.csv
        InputStream restaurantStream = getResources().openRawResource(R.raw.data_restaurants);
        BufferedReader restaurantReader = new BufferedReader(new InputStreamReader(restaurantStream, StandardCharsets.UTF_8));
        // reads data from data_inspections.csv
        InputStream inspectionStream = getResources().openRawResource(R.raw.data_inspections);
        BufferedReader inspectionReader = new BufferedReader(new InputStreamReader(inspectionStream, StandardCharsets.UTF_8));
        // the data is set using private setters in the Data class
        Data restaurantData = new Data( restaurantReader , mContext);
        Data inspectionData = new Data( inspectionReader,  mContext);
        restaurantData.readRestaurantData();
        inspectionData.readInspectionData();

    }
    public void updateInspections() {
        try {
            String fileName = this.getFilesDir() + "/"+ "restaurantData" + "/" + "inspections.csv";
            InputStream fis = new FileInputStream(new File(fileName));
            BufferedReader inspectionReader = new BufferedReader(new InputStreamReader(fis, StandardCharsets.UTF_8));
            Data inspectionDataUpdate = new Data( inspectionReader,  mContext);
            inspectionDataUpdate.readUpdatedInspectionData();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void updateRestaurants() {
        try {
            String fileName = this.getFilesDir() + "/"+ "restaurantData" + "/" + "restaurants.csv";
            InputStream fis = new FileInputStream(new File(fileName));
            BufferedReader restaurantReader = new BufferedReader(new InputStreamReader(fis, StandardCharsets.UTF_8));
            Data restaurantDataUpdate = new Data(restaurantReader,  mContext);
            restaurantDataUpdate.readUpdatedRestaurantData();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private long hoursSinceUpdate() {
        Date currentDate = new Date(System.currentTimeMillis());
        Date last_update = new Date( prefs.getLong("last_update", 0));
        long diffInMiles = currentDate.getTime() - last_update.getTime();
        return TimeUnit.HOURS.convert(diffInMiles, TimeUnit.MILLISECONDS);
    }



    private void makeDataGetRequest() {
        restaurantsRequest = new DownloadRequest(restaurantsURL, WelcomeActivity.this, "restaurants.csv" , 0);
        inspectionsRequest = new DownloadRequest(inspectionsURL, WelcomeActivity.this, "inspections.csv", 1 );
        restaurantsRequest.getURL(new DownloadRequest.VolleyCallBack() {
            @Override
            public void onSuccess() {
                inspectionsRequest.getURL( new DownloadRequest.VolleyCallBack() {
                    @Override
                    public void onSuccess() {
                        if (restaurantsRequest.dataModified() || inspectionsRequest.dataModified()){
                            Log.d("Welcome Activity", "restaurants modified: " + restaurantsRequest.dataModified());
                            Log.d("Welcme Activity", "inspections modified: " + inspectionsRequest.dataModified());
                            downloadOptionFragment();
                        }
                        else{
                            Intent intent = MapsActivity.getIntent(WelcomeActivity.this);
                            startActivity(intent);
                            finish();
                        }
                    }
                });
            }
        });
    }

    private void downloadOptionFragment(){
        DownloadFragment dialog = new DownloadFragment();
        dialog.setCancelable(false);
        dialog.show(downloadFrag, "MessageDialog");
    }

    public DownloadRequest getRestaurantsRequest(){
        return restaurantsRequest;
    }

    public DownloadRequest getInspectionsRequest(){
        return inspectionsRequest;
    }

    public static Context getContext() {
        return mContext;
    }

}