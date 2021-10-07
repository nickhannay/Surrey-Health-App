package com.example.poject_01.UI;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.SearchView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.example.poject_01.R;
import com.example.poject_01.model.Restaurant;
import com.example.poject_01.model.RestaurantCluster;
import com.example.poject_01.model.RestaurantClusterRenderer;
import com.example.poject_01.model.RestaurantList;
import com.example.poject_01.model.Search;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.maps.android.clustering.ClusterManager;

/**
 * Handles user location, clusters, and etc needed for Map
 *
 */
public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback  {

    private GoogleMap mMap;
    private final RestaurantList restaurantList = RestaurantList.getInstance();
    private FusedLocationProviderClient currentLocation;
    boolean permissionGranted = false;
    LocationRequest locationRequest;
    private Toolbar toolbar;
    private double lat;
    private double lng;
    private String restaurantTrack;
    private ClusterManager<RestaurantCluster> clusterManager;
    private RestaurantClusterRenderer renderer;
    private boolean check;
    private static Context mContext;
    private SearchView searchView;
    private Search search = Search.getInstance();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        check = false;
        mContext = MapsActivity.this;


        extractMapsData();
        setupToolbar();

        if(getIntent().getBooleanExtra("EXIT",false)) {
            finish();
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            checkSettingAndStartLocationUpdates();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 12345);
        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        currentLocation = LocationServices.getFusedLocationProviderClient(this);
        createLocationRequest();
        searchView = findViewById(R.id.searchText);
        storeSearchBarText();
        getDataFromSearchList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //inflate menu
        getMenuInflater().inflate(R.menu.toggle_button, menu);
        return true;
    }

    public static Context getContext() {
        return mContext;
    }



    private void extractMapsData(){
        Intent intent = getIntent();
        lat = intent.getDoubleExtra("Latitude",0);
        lng = intent.getDoubleExtra("Longitude",0);
        check = intent.getBooleanExtra("FROM_REST",false);
    }

    private void setupToolbar(){
        toolbar = (Toolbar) findViewById(R.id.toolbar_map);
        toolbar.inflateMenu(R.menu.toggle_button);
        toolbar.setTitle(R.string.title_activity_maps);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent intent = MainActivity.getLaunchIntent(MapsActivity.this);
                if(item.getItemId() == R.id.switch_list) {
                    storeSearchBarText();
                    startActivity(intent);
                    return  true;
                }
                else
                    return false;
            }
        });
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
        setUpCluster();
        if(!check) {
            getDeviceLocation();
        }
        else {
            moveCamera(new LatLng(lat,lng),15f);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        setupSearch();
        mMap.setMyLocationEnabled(true);
    }

    private void setupSearch() {
        searchView.setSubmitButtonEnabled(false);
        if(search.getListSearch() != null) {
            searchView.clearFocus();
            search.setSearch(search.getListSearch());
            setUpCluster();
        }
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if(TextUtils.isEmpty(query)){
                    search.setSearch("");
                }
                else {
                    search.setSearch(query);
                    search.getSearch().toLowerCase();
                }
                searchView.clearFocus();
                setUpCluster();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(TextUtils.isEmpty(newText)) {
                    search.setSearch("");
                    setUpCluster();
                }
                return true;
            }
        });
    }

    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
        }
    };

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        currentLocation.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

    @Override
    protected void onStart() {
        super.onStart();
        startLocationUpdates();
    }

    @Override
    protected void onStop() {
        super.onStop();
        currentLocation.removeLocationUpdates(locationCallback);
    }
    private void createLocationRequest() {
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(500);
        locationRequest.setFastestInterval(500);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void getDeviceLocation() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION};
            ActivityCompat.requestPermissions(this,permissions,12345);
            return;
        }
        Task<Location> location = LocationServices.getFusedLocationProviderClient(this).getLastLocation();
        location.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                lat = location.getLatitude();
                lng = location.getLongitude();
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f));

                if (ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                mMap.setMyLocationEnabled(true);
            }
        });
    }

    private void checkSettingAndStartLocationUpdates() {
        LocationSettingsRequest request = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest).build();
        SettingsClient client = LocationServices.getSettingsClient(this);

        Task<LocationSettingsResponse> locationSettingsResponseTask = client.checkLocationSettings(request);
        locationSettingsResponseTask.addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                // Starts location updates
                if (ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                currentLocation.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
            }
        });
    }

    private void moveCamera (LatLng latLng, float zoom)
    {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,zoom));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void setUpCluster() {
        LatLng indexes;
        indexes = new LatLng(lat,lng);
        clusterManager = new ClusterManager<>(this,mMap);
        renderer = new RestaurantClusterRenderer(this, mMap,clusterManager, indexes);
        clusterManager.setRenderer(renderer);
        addItems();
        for( Restaurant r : restaurantList) {
            LatLng coordinates = new LatLng(r.getLatitude(),r.getLongitude());
            moveCamera(coordinates, 15f);
        }

        clusterManager.setOnClusterItemInfoWindowClickListener(new ClusterManager.OnClusterItemInfoWindowClickListener<RestaurantCluster>() {
            @Override
            public void onClusterItemInfoWindowClick(RestaurantCluster item) {
                for(int i = 0; i < restaurantList.getRestaurantListSize(); i++)
                {
                    Restaurant r = restaurantList.getRestaurantIndex(i);
                    LatLng coordinates = new LatLng(r.getLatitude(), r.getLongitude());
                    moveCamera(coordinates, 15f);
                    if(item.getPosition().equals(coordinates))
                    {
                        Intent intent = RestaurantDetailsActivity.makeIntent(MapsActivity.this, i,true,1);
                        intent = intent.putExtra("index", 1);
                        startActivity(intent);
                    }
                }
            }
        });
        mMap.setOnCameraIdleListener(clusterManager);
        mMap.setOnMarkerClickListener(clusterManager);
    }

    private void addItems() {
        mMap.clear();
        for(Restaurant r : restaurantList) {
            if(!search.filter(r)) {
                continue;
            }
            double latitude = r.getLatitude();
            double longitude = r.getLongitude();
            LatLng latitudeLongitude = new LatLng(latitude,longitude);
            RestaurantCluster cluster;

            BitmapDescriptor red = BitmapDescriptorFactory.fromResource(R.drawable.red);
            BitmapDescriptor yellow = BitmapDescriptorFactory.fromResource(R.drawable.yellow);
            BitmapDescriptor green = BitmapDescriptorFactory.fromResource(R.drawable.green);
            BitmapDescriptor imageId;
            if(r.numInspections()>0)
            {
                if(r.getLatestInspection().getHazardRating().equals("High"))
                {
                    imageId = red;
                }
                else if(r.getLatestInspection().getHazardRating().equals("Moderate"))
                {
                    imageId = yellow;
                }
                else {
                    imageId = green;
                }
            }
            else
            {
                imageId = BitmapDescriptorFactory.fromResource(R.drawable.green);
            }
            if(r.numInspections() > 0) {
                int hazardRatingDisplay;
                if(r.getLatestInspection().getHazardRating().equals("Low"))
                {
                    hazardRatingDisplay = R.string.low;
                }
                else if(r.getLatestInspection().getHazardRating().equals("Moderate"))
                {
                    hazardRatingDisplay = R.string.moderate;
                }
                else
                    hazardRatingDisplay = R.string.high;
                cluster = new RestaurantCluster(latitudeLongitude, r.getName(),
                        r.getAddress() + "," + r.getCity() +
                                getString(R.string.hazard_rating) + getString(hazardRatingDisplay), imageId);
            }
            else
                cluster = new RestaurantCluster(latitudeLongitude,r.getName(), r.getAddress()+getString(R.string.comma)+ r.getCity() + " " + getResources().getString(R.string.no_recent_inspections_main),imageId);

            clusterManager.addItem(cluster);

        }
    }

    public static  Intent makeLaunchIntent (Context c, double latitude, double longitude,Boolean load)
    {
        Intent intent = new Intent(c,MapsActivity.class);
        intent.putExtra("Latitude",latitude);
        intent.putExtra("Longitude",longitude);
        intent.putExtra("FROM_REST",load);
        return  intent;
    }



    public static Intent getIntent (Context c)
    {
        Intent intent = new Intent(c,MapsActivity.class);
        return intent;

    }

    private void storeSearchBarText() {
        String text = search.getSearch();
        SharedPreferences prefs = getSharedPreferences("searchBarText",MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("value", text);
        editor.apply();
    }

    private void getDataFromSearchList() {
        SharedPreferences sharedPreferences = getSharedPreferences("searchData",MODE_PRIVATE);
        String value = sharedPreferences.getString("value","");
        EditText editText = findViewById(R.id.searchMainList);
        searchView.setQuery(search.getListSearch(),true);

    }

    @Override
    protected void onResume() {
        super.onResume();
        getDataFromSearchList();
    }
}