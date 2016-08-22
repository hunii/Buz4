package com.example.james.buz4;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by Taehyun Kim on 09/08/2016.
 */
public class LoadingActivity extends FragmentActivity implements OnMapReadyCallback {
//public abstract class LoadingActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks {
//public abstract class LoadingActivity extends FragmentActivity implements OnMapReadyCallback, LocationManagerInterface {
//public abstract class LoadingActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener, OnMarkerClickListener {
//public class LoadingActivity extends AppCompatActivity {

    private GoogleMap mMap;
    private GoogleApiClient client;
    private Location location;
    private Location uCurLocation;
    private LocationManager locManager;
    private LocationListener locListener;
    public static final String TAG = LoadingActivity.class.getSimpleName();

    Intent intentThatCalled;
    public double latitude;
    public double longitude;
    public LocationManager locationManager;
    public Criteria criteria;
    public String bestProvider;

    //MapLocationManager mLocationManager;
    TextView mLocalTV, mLocationProviderTV, mlocationTimeTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        //To get MapFragment reference from xml layout
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        //To get map object
        mapFragment.getMapAsync(this);


        intentThatCalled = getIntent();
        String voice2text = intentThatCalled.getStringExtra("v2txt");
        getLocation(voice2text);
/*
        Thread welcomeThread = new Thread() {

            @Override
            public void run() {
                try {
                    super.run();
                    sleep(5000);  //Delay of 10 seconds
                } catch (Exception e) {
                } finally {
                    Intent mainview = new Intent(LoadingActivity.this, MainActivity.class);
                    startActivity(mainview);
                    finish();
                }
            }
        };
        welcomeThread.start();
*/

/*
        //to show current location in the map
        mMap.setMyLocationEnabled(true);

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng latLng) {

                Toast.makeText(getApplicationContext(), latLng.toString(), Toast.LENGTH_LONG).show();
            }
        });
*/
        Log.w("LoadingActivity", "--------------onCreate)---------Log Test");
        //To setup location manager
//        locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //To request location updates
        //locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, this)
        //locManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1, 1, this);


        locListener = new LocationListener() {
            //@Override
            public void onStatusChanged(String provider, int status,
                                        Bundle extras) {
                Log.w("LoadingActivity", "--------------onStatusChanged---------Log Test");
            }

            //@Override
            public void onProviderEnabled(String provider) {
                Log.w("LoadingActivity", "--------------onProviderEnabled---------Log Test");
            }

            //@Override
            public void onProviderDisabled(String provider) {
                Log.w("LoadingActivity", "--------------onProviderDisabled---------Log Test");
            }

            @Override
            public void onLocationChanged(Location location) {
                Log.w("LoadingActivity", "--------------onLocationChanged---------Log Test");
                //To clear map data
                mMap.clear();

                //To hold location
                LatLng curLocation = new LatLng(location.getLatitude(), location.getLongitude());

                //To create marker in map
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(curLocation);
                markerOptions.title("My Location");
                markerOptions.snippet("Find my current location.");
                //adding marker to the map
                mMap.addMarker(markerOptions);

                //opening position with some zoom level in the map
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(curLocation, 17.0f));
                //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(curLocation, 17));
            }
        };

        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();


/*        mLocationManager = new LocationManager(getApplicationContext(), this, this, LocationManager.ALL_PROVIDERS, LocationRequest.PRIORITY_HIGH_ACCURACY, 10 * 1000, 1 * 1000, MapLocationManager.LOCATION_PROVIDER_RESTRICTION_NONE); // init location manager
        mLocalTV = (TextView) findViewById(R.id.locationDisplayTV);
        mLocationProviderTV = (TextView) findViewById(R.id.locationProviderTV);
        mlocationTimeTV = (TextView) findViewById(R.id.locationTimeFetchedTV);
*/
    }

/*
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
*/

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Marker testBusStop;

        //setUpMap();

        //mMap.setOnMarkerClickListener(this);

        // Add a marker in Sydney and move the camera

        LatLng autUni = new LatLng(-36.853, 174.767);
//        LatLng testBusStop = new LatLng(-36.85266, 174.76563);
//        String testBusStopTitle = "7038";
//        String testBusStopDesc = "Mayoral Dr opp AUT";
//
        //setOnMarkerClickListener
        //onMarkerClicked(Marker);


        //mMap.setMyLocationEnabled(true);
        //myLat = mMap.getMyLocation().getLatitude();

        // Move to AUT
        mMap.addMarker(new MarkerOptions()
                .title("AUT University")
                .snippet("The most populous university in New Zealand.")
                .position(autUni));

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(autUni, 17));

        //Toast.makeText(getApplicationContext(), autUni.toString(), Toast.LENGTH_LONG).show();

        //LatLng curLocation = new LatLng(location.getLatitude(), location.getLongitude());
        //Log.w("LoadingActivity", "--------------onMapReady---------curLocation"+curLocation);
        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(curLocation, 17));
/*
        //To create marker in map
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(curLocation);
        markerOptions.title("My Location");
        markerOptions.snippet("Find my current location.");
        //adding marker to the map
        mMap.addMarker(markerOptions);
*/
        //opening position with some zoom level in the map
        //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(curLocation, 17.0f));

        //.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));

//
//        mMap.addMarker(new MarkerOptions()
//                .position(new LatLng(-36.85266, 174.76563))
//                .title("7038")
//                .snippet("Mayoral Dr opp AUT")
//                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));

//        mMap.addMarker(new MarkerOptions()
//                .title(testBusStopTitle)
//                .snippet(testBusStopDesc)
//                .position(testBusStop));
//
        //onLocationChanged(location);
    }

    public void setUpMap() {

        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.setTrafficEnabled(true);
        //mMap.setIndoorEnabled(true);
        mMap.isIndoorEnabled();
        mMap.setBuildingsEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
    }

    /*
            @Override
            public boolean onMarkerClick(Marker arg0) {

                return false;
            }*/
/*
    protected void onStart() {
        super.onStart();
        mLocationManager.startLocationFetching();
    }

    protected void onStop() {
        super.onStop();
        mLocationManager.abortLocationFetching();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mLocationManager.pauseLocationFetching();
    }

    @Override
    public void locationFetched(Location mLocal, Location oldLocation, String time, String locationProvider) {
        Toast.makeText(getApplication(), "Lat : " + mLocal.getLatitude() + " Lng : " + mLocal.getLongitude(), Toast.LENGTH_LONG).show();
        mLocalTV.setText("Lat : " + mLocal.getLatitude() + " Lng : " + mLocal.getLongitude());
        mLocationProviderTV.setText(locationProvider);
        mlocationTimeTV.setText(time);
    }
*/
    public static boolean isLocationEnabled(Context context) {
        int locationMode = 0;
        String locationProviders;
        //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);
            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
            }
            return locationMode != Settings.Secure.LOCATION_MODE_OFF;
        } else {
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }
    }

    public void getLocation(String voice2txt) {
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        criteria = new Criteria();
        bestProvider = String.valueOf(locationManager.getBestProvider(criteria, false)).toString();
        Log.w("LoadingActivity", "getLocation]--------->bestProvider======="+bestProvider);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location location = locationManager.getLastKnownLocation(bestProvider);
        Log.w("LoadingActivity", "getLocation]--------->getLastKnownLocation======="+location);
        if (isLocationEnabled(LoadingActivity.this)) {
            Log.e("TAG", "GPS is on");
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            Log.w("LoadingActivity", "getLocation]--------->latitude======="+latitude);
            Log.w("LoadingActivity", "getLocation]--------->longitude======="+longitude);
            LatLng curLocation = new LatLng(latitude, longitude);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(curLocation, 17));
            Toast.makeText(LoadingActivity.this, "latitude:" + latitude + " longitude:" + longitude, Toast.LENGTH_SHORT).show();
            searchNearestPlace(voice2txt);

        }else {

            AlertDialog.Builder notifyLocationServices = new AlertDialog.Builder(LoadingActivity.this);
            notifyLocationServices.setTitle("Switch on Location Services");
            notifyLocationServices.setMessage("Location Services must be turned on to complete this action. Also please take note that if on a very weak network connection,  such as 'E' Mobile Data or 'Very weak Wifi-Connections' it may take even 15 mins to load. If on a very weak network connection as stated above, location returned to application may be null or nothing and cause the application to crash.");
            notifyLocationServices.setPositiveButton("Ok, Open Settings", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent openLocationSettings = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    LoadingActivity.this.startActivity(openLocationSettings);
                    finish();
                }
            });
            notifyLocationServices.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            notifyLocationServices.show();
        }
    }
    public void searchNearestPlace(String v2txt) {
        Log.e("TAG", "Started");
        v2txt = v2txt.toLowerCase();
        String[] placesS = {"accounting", "airport", "aquarium", "atm", "attraction", "bakery", "bakeries", "bank", "bar", "cafe", "campground", "casino", "cemetery", "cemeteries", "church", "courthouse", "dentist", "doctor", "electrician", "embassy", "embassies", "establishment", "finance", "florist", "food", "grocery", "groceries", "supermarket", "gym", "health", "hospital", "laundry", "laundries", "lawyer", "library", "libraries", "locksmith", "lodging", "mosque", "museum", "painter", "park", "parking", "pharmacy", "pharmacies", "physiotherapist", "plumber", "police", "restaurant", "school", "spa", "stadium", "storage", "store", "synagog", "synagogue", "university", "universities", "zoo"};
        String[] placesM = {"amusement park", "animal care", "animal care", "animal hospital", "art gallery", "art galleries", "beauty salon", "bicycle store", "book store", "bowling alley", "bus station", "car dealer", "car rental", "car repair", "car wash", "city hall", "clothing store", "convenience store", "department store", "electronics store", "electronic store", "fire station", "funeral home", "furniture store", "gas station", "general contractor", "hair care", "hardware store", "hindu temple", "home good store", "homes good store", "home goods store", "homes goods store", "insurance agency", "insurance agencies", "jewelry store", "liquor store", "local government office", "meal delivery", "meal deliveries", "meal takeaway", "movie rental", "movie theater", "moving company", "moving companies", "night club", "pet store", "place of worship", "places of worship", "post office", "real estate agency", "real estate agencies", "roofing contractor", "rv park", "shoe store", "shopping mall", "subway station", "taxi stand", "train station", "travel agency", "travel agencies", "veterinary care"};
        int index;
        for (int i = 0; i <= placesM.length - 1; i++) {
            Log.e("TAG", "forM");
            if (v2txt.contains(placesM[i])) {
                Log.e("TAG", "sensedM?!");
                index = i;
                Uri gmmIntentUri = Uri.parse("geo:" + latitude + "," + longitude + "?q=" + placesM[index]);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
                finish();
            }
        }
        for (int i = 0; i <= placesS.length - 1; i++) {
            Log.e("TAG", "forS");
            if (v2txt.contains(placesS[i])) {
                Log.e("TAG", "sensedS?!");
                index = i;
                Uri gmmIntentUri = Uri.parse("geo:" + latitude + "," + longitude + "?q=" + placesS[index]);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
                finish();
            }
        }
    }
}
