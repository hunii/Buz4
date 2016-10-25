package com.example.james.buz4;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import model.Stop;

/**
 * This class represent activity to show the bus stops near by user's current location.
 *
 * Developed by Taehyun Kim
 * Created on 09/08/2016.
 * Modified version 0.9.0 on 18/oct/2016 (Using text file due to AT web problem)
 */
public class FindStopsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnInfoWindowClickListener,
        GoogleMap.OnCameraChangeListener {

    public double curLat, curLog;
    int sDistance = 300;
    public static final String TAG = "FindStopsActivity";
    private String fileNameStops = "at_bus_stops.txt";

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Location mLastLocation;
    public LocationManager mLocationManager;
    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;

    private InputStream iStream = null;
    private InputStreamReader iStreamReader = null;
    private BufferedReader buffReader = null;
    public String search_type, tripId;

    /**
     * Init map fragment connected to xmp file.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_findstops);

        //To get MapFragment reference from xml layout
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        //To get map object
        mapFragment.getMapAsync(this);

        handlePermissionsAndGetLocation();
    }

    /**
     * load current location and enable location button
     * @param googleMap
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnCameraChangeListener(this);
        mMap.setOnInfoWindowClickListener(this);
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);

        // Init location
        LatLng initPoint = new LatLng(-36.8524, 174.7644); // AUT WT Building
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(initPoint, 17));
    }

    /**
     * Enable infomation window on the bus stop
     * @param marker
     * @return busStopNo, busStopAddress to timeTableActivity
     */
    @Override
    public void onInfoWindowClick (Marker marker) {
        marker.showInfoWindow();
        Intent intent = new Intent(FindStopsActivity.this, TimeTableActivity.class); // Bus stop list
        intent.putExtra("busStopNo", marker.getTitle());
        intent.putExtra("busStopAddr", marker.getSnippet());

        startActivity(intent);
    }

    protected synchronized void buildGoogleApiClient() {

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        /*if (ContextCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }*/

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    /**
     * init location listener to retrieve current location
     */
    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            mLastLocation = location;
            curLat = location.getLatitude();
            curLog = location.getLongitude();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    /**
     * init menu bar
     * @param menu
     * @return selected menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                // If request is cancelled, the result arrays are empty.
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getLocation();
                } else {
                // Permission denied, Disable the functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void handlePermissionsAndGetLocation() {
        Log.w(TAG, "===========handlePermissionsAndGetLocation=========");
        int hasWriteContactsPermission = checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION);
        if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_CODE_ASK_PERMISSIONS);
            return;
        }
        getLocation();//if already has permission
    }

    protected void getLocation() {
        int LOCATION_REFRESH_TIME = 3000;
        int LOCATION_REFRESH_DISTANCE = 5;

        if (!(checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_REFRESH_TIME,LOCATION_REFRESH_DISTANCE, mLocationListener);
        }else{
            Log.w(TAG, "===========getLocation========= Does not have permission!");
        }
    }

    /**
     * get current screen position
     * @param cameraPosition
     */
    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        if(mMap.getCameraPosition().zoom > 15.5) {
            curLat = mMap.getCameraPosition().target.latitude;
            curLog = mMap.getCameraPosition().target.longitude;

            // Connect AT API and retrieve bus stops
            //new stopsSearchByPosition().execute();

            // Read AT bus stops text file
            new stopsByTripIdFromText().execute();
        }else{
            mMap.clear();
        }
    }

    /**
     * Call Auckland Transport API
     * Receive and display bus stops around current location on Google map
     * (**Since 17/Oct/2016, this api is not provided from AT web
     * Changed this method to stopsByTripIdFromText)
     */
    class stopsSearchByPosition extends AsyncTask<String, Void, Void> {
        ArrayList<Stop> listStop = new ArrayList<>();

        @Override
        protected void onPreExecute(){

        }

        @Override
        protected Void doInBackground(String... params) {
            StringBuilder strJson = new StringBuilder();
            HttpURLConnection conn;

            try {
                URL url = new URL("https://api.at.govt.nz/v1/gtfs/stops/geosearch?lat="+curLat+"&lng="+curLog+"&distance="+sDistance+"&api_key="+getResources().getString(R.string.at_apis_key));
                conn = (HttpURLConnection) url.openConnection();
                conn.connect();
                InputStream stream = new BufferedInputStream(conn.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
                String line;
                while ((line = reader.readLine()) != null) {
                    strJson.append(line);
                }

                JSONObject jsonRootObject = new JSONObject(strJson.toString());

                //Get the instance of JSONArray that contains JSONObjects
                JSONArray jsonArray = jsonRootObject.optJSONArray("response");

                //Iterate the jsonArray and print the info of JSONObjects
                for(int i=0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    int stopId = Integer.parseInt(jsonObject.optString("stop_id").toString());
                    String stopName = jsonObject.optString("stop_name").toString();
                    double stopLat = Double.parseDouble(jsonObject.optString("stop_lat").toString());
                    double stoplong = Double.parseDouble(jsonObject.optString("stop_lon").toString());

                    Stop aStop = new Stop(stopId, stopName, stopLat, stoplong);
                    listStop.add(aStop);
                    publishProgress();
                }

            }catch(Exception e){
                Log.w(TAG, "doInBackground) ------no network---------------"+e+"; "+e.getCause());
            }finally{
                //conn.disconnect();
            }
            return null;
        }

        protected void onProgressUpdate(){

        }

        @Override
        protected void onPostExecute(Void v){
            // Add bus stops
            for(int i=0; i < listStop.size(); i++) {
                LatLng vBusStops = new LatLng(listStop.get(i).getStop_Lat(), listStop.get(i).getStop_Lon());
                if(listStop.get(i).getStop_Id() < 10000) {
                    mMap.addMarker(new MarkerOptions()
                            .title(Integer.toString(listStop.get(i).getStop_Id()))
                            .snippet(listStop.get(i).getStop_Name())
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.at_bus_stop_2))
                            .position(vBusStops));
                }
            } // for
            findViewById(R.id.loadingPanel).setVisibility(View.GONE);
        }

    } // class

    /**
     * Read and display bus stops from text file provided Auckland Transport
     * Use AT bus stop text file
     * @Param Trip ID Travel information of a selected bus number
     */
    public class stopsByTripIdFromText extends AsyncTask<String, Void, Void> {
        ArrayList<Stop> listStop = new ArrayList<>();

        @Override
        protected void onPreExecute(){}

        /**
         * Bufferreading bus stop file to get all bus stops location around current location
         */
        @Override
        protected Void doInBackground(String... params) {

            try {
                iStream = getResources().getAssets().open(fileNameStops);
                iStreamReader = new InputStreamReader(iStream);
                buffReader = new BufferedReader(iStreamReader);
                String rLine;

                while ((rLine = buffReader.readLine()) != null) {
                    try {
                        String[] lines = rLine.split(",");
                        double stop_lat = Double.parseDouble(lines[0]);
                        double stop_lon = Double.parseDouble(lines[2]);
                        int stop_no = Integer.parseInt(lines[3]);
                        String stop_name = lines[6];

                        if(1000 < stop_no && stop_no < 10000) {
                            Stop aStop = new Stop(stop_no, stop_name, stop_lat, stop_lon);
                            listStop.add(aStop);
                            publishProgress();
                        }
                    }catch(NumberFormatException nfe){
                        Log.w(TAG, nfe);
                    }
                }
                buffReader.close();

            }catch(Exception e){e.getCause();}
            return null;
        }

        /**
         * Push completed list of Trip objects to the view(XML) adapter to output a list of Trip object accordingly.
         */
        @Override
        protected void onPostExecute(Void v){
            // Add bus stops
            if (mMap != null) {
                //This is the current user-viewable region of the map
                LatLngBounds bounds = mMap.getProjection().getVisibleRegion().latLngBounds;

                for (int i = 0; i < listStop.size(); i++) {
                    LatLng vBusStops = new LatLng(listStop.get(i).getStop_Lat(), listStop.get(i).getStop_Lon());

                    if(bounds.contains(new LatLng(listStop.get(i).getStop_Lat(), listStop.get(i).getStop_Lon()))) {
                        mMap.addMarker(new MarkerOptions()
                                .title(Integer.toString(listStop.get(i).getStop_Id()))
                                .snippet(listStop.get(i).getStop_Name())
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                                //.icon(BitmapDescriptorFactory.fromResource(R.drawable.at_bus_stop_2))
                                .position(vBusStops));
                    }

                } // for
            }
            findViewById(R.id.loadingPanel).setVisibility(View.GONE);
        }
    } // stopsByTripIdFromText ends

}
