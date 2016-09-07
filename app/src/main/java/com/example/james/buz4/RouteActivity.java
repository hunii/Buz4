package com.example.james.buz4;

import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import model.Route;
import model.Stop;

/**
 * Created by Joshua Kim on 3/09/2016.
 */
public class RouteActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    protected double curLat, curLog;
    public static final String TAG = "RouteActivity";

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Location mLastLocation;
    public LocationManager mLocationManager;
    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;
    LatLngBounds.Builder builder;
    CameraUpdate cUpdate;

    public String tripId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);
        tripId = "15454038806-20160803113210_v43.25";//getIntent().getSerializableExtra("trip_id").toString();
        new shapeByTripId().execute(tripId);
        Log.w(TAG, "=============tripId==========="+tripId);

        //To get MapFragment reference from xml layout
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        //To get map object
        mapFragment.getMapAsync(this);

        handlePermissionsAndGetLocation();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.getUiSettings().setTiltGesturesEnabled(false);
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);

        new stopByTripId().execute();
        //new realTimeVehicleLocations().execute();

    }

    protected synchronized void buildGoogleApiClient() {
        Log.w(TAG, "========== buildGoogleApiClient ==========");
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
            //Log.w(TAG, "========== onLocationChanged curLat==========" + curLat);
            //Log.w(TAG, "========== onLocationChanged curLog==========" + curLog);

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
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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
        int LOCATION_REFRESH_TIME = 2000;
        int LOCATION_REFRESH_DISTANCE = 5;

        if (!(checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_REFRESH_TIME,LOCATION_REFRESH_DISTANCE, mLocationListener);
        }else{
            Log.w(TAG, "===========getLocation========= Does not have permission!");
        }
    }

    class shapeByTripId extends AsyncTask<String, Void, Void> {
        ArrayList<Route> tripShape = new ArrayList<>();

        @Override
        protected void onPreExecute(){

        }

        @Override
        protected Void doInBackground(String... params) {
            StringBuilder strJson = new StringBuilder();
            HttpURLConnection conn;

            try {
                URL url = new URL("https://api.at.govt.nz/v1/gtfs/shapes/tripId/"+tripId+"?api_key="+getResources().getString(R.string.at_apis_key));
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
                for(int i=0; i < jsonArray.length(); i++){
                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    String shapeId = jsonObject.optString("shape_id").toString();
                    double shapeLat = Double.parseDouble(jsonObject.optString("shape_pt_lat").toString());
                    double shapelon = Double.parseDouble(jsonObject.optString("shape_pt_lon").toString());
                    int shapeSequence = Integer.parseInt(jsonObject.optString("shape_pt_sequence").toString());

                    Route aRoute = new Route(shapeId,shapeLat,shapelon,shapeSequence);
                    tripShape.add(aRoute);
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
            mMap.clear();
            PolylineOptions lineOptions = new PolylineOptions();
            double sLat = 0.00, sLon = 0.00, eLat = 0.00, eLon = 0.00;

            // View Bus Route
            for(int i=0; i < tripShape.size(); i++) {
                LatLng vBusRoute = new LatLng(tripShape.get(i).getShape_Latitude(), tripShape.get(i).getShape_Longitude());

                if(i == 0) {
                    sLat = tripShape.get(i).getShape_Latitude();
                    sLon = tripShape.get(i).getShape_Longitude();
                }else if(i == tripShape.size()-1) {
                    eLat = tripShape.get(i).getShape_Latitude();
                    eLon = tripShape.get(i).getShape_Longitude();
                }

                // Adding all the points in the route to LineOptions
                lineOptions.add(vBusRoute);
                lineOptions.width(20);
                lineOptions.color(Color.BLUE);
            }

            // Drawing polyline in the Google Map for the i-th route
            if(lineOptions != null) {
                mMap.addPolyline(lineOptions);
            }else {
                Log.w(TAG,"onPostExecute=========without Polylines drawn");
            }
            // get Mid-Point
            getMidPoint(sLat, sLon, eLat, eLon);
        }

    } // shapeByTripId

    class stopByTripId extends AsyncTask<String, Void, Void> {
        ArrayList<Stop> tripStops = new ArrayList<>();

        @Override
        protected void onPreExecute(){

        }

        @Override
        protected Void doInBackground(String... params) {
            StringBuilder strJson = new StringBuilder();
            HttpURLConnection conn;

            try {
                URL url = new URL("https://api.at.govt.nz/v1/gtfs/stops/tripId/"+tripId+"?api_key="+getResources().getString(R.string.at_apis_key));
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
                for(int i=0; i < jsonArray.length(); i++){
                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    int stopId = Integer.parseInt(jsonObject.optString("stop_id").toString());
                    String stopName = jsonObject.optString("stop_name").toString();
                    double stopLat = Double.parseDouble(jsonObject.optString("stop_lat").toString());
                    double stoplon = Double.parseDouble(jsonObject.optString("stop_lon").toString());

                    Stop bStop = new Stop(stopId,stopName,stopLat,stoplon);
                    tripStops.add(bStop);
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

            List<Marker> markersList = new ArrayList<Marker>();
            // View Bus Route
            for(int i=0; i < tripStops.size(); i++) {
                LatLng pTripStops = new LatLng(tripStops.get(i).getStop_Lat(), tripStops.get(i).getStop_Lon());

                Marker showAllMarkers = mMap.addMarker(new MarkerOptions()
                        .title(Integer.toString(tripStops.get(i).getStop_Id()))
                        .snippet(tripStops.get(i).getStop_Name())
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.at_bus_stop_2))
                        .position(pTripStops));

                /**Put all the markers into arraylist*/
                markersList.add(showAllMarkers);
            }

            /**create for loop for get the latLngbuilder from the marker list*/
            builder = new LatLngBounds.Builder();
            for (Marker m : markersList) {
                builder.include(m.getPosition());
            }
            /**initialize the padding for map boundary*/
            int padding = 50;
            /**create the bounds from latlngBuilder to set into map camera*/
            LatLngBounds bounds = builder.build();
            /**create the camera with bounds and padding to set into map*/
            cUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding);
            /**call the map call back to know map is loaded or not*/
            mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                @Override
                public void onMapLoaded() {
                    /**set animated zoom camera into map*/
                    mMap.animateCamera(cUpdate);

                }
            });
        }

    } // stopByTripId
/*
    // Real-Time Bus Tracking
    class realTimeVehicleLocations extends AsyncTask<String, Void, Void> {
        ArrayList<Stop> realTimeLocation = new ArrayList<>();

        @Override
        protected void onPreExecute(){

        }

        @Override
        protected Void doInBackground(String... params) {
            StringBuilder strJson = new StringBuilder();
            HttpURLConnection conn;

            try {
                URL url = new URL("https://api.at.govt.nz/v1/public/realtime/vehiclelocations?api_key="+getResources().getString(R.string.at_apis_key));
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
                JSONObject responseObj = jsonRootObject.getJSONObject("response");
                JSONArray jsonArray = responseObj.optJSONArray("entity");

                //Iterate the jsonArray and print the info of JSONObjects
                for(int i=0; i < jsonArray.length(); i++){
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    JSONObject vehicleObj = jsonObject.getJSONObject("vehicle");
                    JSONObject tripObj = vehicleObj.getJSONObject("trip");
                    JSONObject positionObj = vehicleObj.getJSONObject("position");

                        int stopId = 123;//Integer.parseInt(jsonObject.optString("stop_id").toString());
                        String stopName = tripObj.optString("trip_id").toString();
                        double stopLat = Double.parseDouble(positionObj.optString("latitude").toString());
                        double stoplon = Double.parseDouble(positionObj.optString("longitude").toString());
                        Log.w(TAG, "================aaaaa================="+stopName);
                        Log.w(TAG, "===============aaaaa==================" + stopLat);
                        Log.w(TAG, "=================aaaaa================" + stoplon);
                        Stop bStop = new Stop(stopId,stopName,stopLat,stoplon);
                        realTimeLocation.add(bStop);
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
            Log.w(TAG, "=================bbbbbbb================");
            // View Bus Route
            for(int i=0; i < realTimeLocation.size(); i++) {
                if(realTimeLocation.get(i).getStop_Name().equals("13135037533-20160803113210_v43.25")) {
                    LatLng aaaBus = new LatLng(realTimeLocation.get(i).getStop_Lat(), realTimeLocation.get(i).getStop_Lon());
                    Log.w(TAG, "=================bbbbbbb================" + aaaBus);
                    mMap.addMarker(new MarkerOptions()
                            .title(Integer.toString(realTimeLocation.get(i).getStop_Id()))
                            .snippet(realTimeLocation.get(i).getStop_Name())
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.at_bus_stop_2))
                            .position(aaaBus));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(aaaBus, 20));
                }
            }
        }

    } // realTimeVehicleLocations
*/
    public void getMidPoint(double lat1, double lon1, double lat2, double lon2){

        double dLon = Math.toRadians(lon2 - lon1);

        //convert to radians
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);
        lon1 = Math.toRadians(lon1);

        double Bx = Math.cos(lat2) * Math.cos(dLon);
        double By = Math.cos(lat2) * Math.sin(dLon);
        double lat3 = Math.atan2(Math.sin(lat1) + Math.sin(lat2), Math.sqrt((Math.cos(lat1) + Bx) * (Math.cos(lat1) + Bx) + By * By));
        double lon3 = lon1 + Math.atan2(By, Math.cos(lat1) + Bx);

        LatLng gMidLatLon = new LatLng(Math.toDegrees(lat3), Math.toDegrees(lon3));
        // Move to Mid-Point
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(gMidLatLon, 12));
    }

    public LatLng moveToMidLocation(double mLat, double mLon){
        LatLng midLocation = new LatLng(mLat, mLon);

        return midLocation;
    }

}
