package com.example.james.buz4;

/**
 * Created by Taehyun Kim on 09/08/2016.
 */

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

public class FindStopsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnInfoWindowClickListener,
        GoogleMap.OnCameraChangeListener {

    public double curLat, curLog;
    int sDistance = 300;
    public static final String TAG = "FindStopsActivity";

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Location mLastLocation;
    public LocationManager mLocationManager;
    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;

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

    @Override
    public void onInfoWindowClick (Marker marker) {
        Log.w(TAG, "onInfoWindowClick===================================");
        marker.showInfoWindow();
        Intent intent = new Intent(FindStopsActivity.this, TimeTableActivity.class); // Bus stop list
        //Intent intent = new Intent(FindStopsActivity.this, RouteActivity.class); // Bus route
        //Intent intent = new Intent(FindStopsActivity.this, ArActivity.class); // AR
        intent.putExtra("busStopNo", marker.getTitle());
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
        Log.w(TAG, "========== onConnected ==========");
/*        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
*/
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Log.w(TAG, "====================onCreateOptionsMenu===================");
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

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        if(mMap.getCameraPosition().zoom > 15.5) {
            curLat = mMap.getCameraPosition().target.latitude;
            curLog = mMap.getCameraPosition().target.longitude;

            // Connect AT API and retrieve bus stops
            new stopsSearchByPosition().execute();
        }else{
            mMap.clear();
        }
    }

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
        }

    } // class

}
