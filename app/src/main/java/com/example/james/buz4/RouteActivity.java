package com.example.james.buz4;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
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

import model.Shape;
import model.Stop;

/**
 *This class represent activity to show a particular bus route on the map.
 *
 * Developed by Taehyun Kim
 * Version Updated: 03 Sep 2016
 */
public class RouteActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleMap.OnInfoWindowClickListener,
        GoogleApiClient.OnConnectionFailedListener {

    protected double curLat, curLog;
    public static final String TAG = "RouteActivity";
    private String fileNameTrip = "at_bus_trips.txt";

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Location mLastLocation;
    public LocationManager mLocationManager;
    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;
    LatLngBounds.Builder builder;
    CameraUpdate cUpdate;

    private InputStream iStream = null;
    private InputStreamReader iStreamReader = null;
    private BufferedReader buffReader = null;

    public String search_type, tripId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);
        search_type = getIntent().getSerializableExtra("search_type").toString();
        if(search_type.equals("timeTableRoute")){
            tripId = getIntent().getSerializableExtra("trip_id").toString();
        } else if(search_type.equals("menuRoute")){
            tripId = findTripNo(getIntent().getSerializableExtra("trip_id").toString());
        }
        //Log.w(TAG, "============search_type======="+search_type);
        //Log.w(TAG, "============tripId======="+tripId);

        // Make line of the bus route
        new shapeByTripId().execute(tripId);

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
        mMap.setOnInfoWindowClickListener(this);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);

    }

    @Override
    public void onInfoWindowClick (Marker marker) {
        marker.showInfoWindow();
        Intent intent = new Intent(RouteActivity.this, TimeTableActivity.class); // Bus stop list
        intent.putExtra("busStopNo", marker.getTitle());
        intent.putExtra("busStopAddr", marker.getSnippet());
        startActivity(intent);
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
        ArrayList<Shape> tripShape = new ArrayList<>();

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

                    Shape aShape = new Shape(shapeId,shapeLat,shapelon,shapeSequence);
                    tripShape.add(aShape);
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

            new stopByTripId().execute();
            //new realTimeVehicleLocations().execute();
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
                findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                if(tripStops.size() == 0){
                    findViewById(R.id.map).setVisibility(View.INVISIBLE);
                    //findViewById(R.id.errorMessageLayout).setVisibility(View.VISIBLE);
                }
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

    public void getMidPoint(double lat1, double lon1, double lat2, double lon2){

        double dLon = Math.toRadians(lon2 - lon1);

        //convert to radians
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);
        lon1 = Math.toRadians(lon1);

        double midXmath = Math.cos(lat2) * Math.cos(dLon);
        double midYmath = Math.cos(lat2) * Math.sin(dLon);
        double lat3 = Math.atan2(Math.sin(lat1) + Math.sin(lat2), Math.sqrt((Math.cos(lat1) + midXmath) * (Math.cos(lat1) + midXmath) + midYmath * midYmath));
        double lon3 = lon1 + Math.atan2(midYmath, Math.cos(lat1) + midXmath);

        LatLng gMidLatLon = new LatLng(Math.toDegrees(lat3), Math.toDegrees(lon3));
        // Move to Mid-Point
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(gMidLatLon, 10));
    }

    /**
     * Find one of a Trip ID in Route using bus number
     * @return Trip ID
     */
    public String findTripNo(String tBusNo){
        String fTripNo = "";
        boolean fTripStop = false;

        try{
            iStream = getResources().getAssets().open(fileNameTrip);
            iStreamReader = new InputStreamReader(iStream);
            buffReader = new BufferedReader(iStreamReader);
            String rLine;

            while ((rLine = buffReader.readLine()) != null && !fTripStop) {
                String[] lines = rLine.split(",");
                String bus_num = lines[1].substring(0, 3);
                try {
                    if (bus_num.equals(tBusNo)) {
                        fTripNo = lines[6];
                        fTripStop = true;
                    }
                }catch(Exception e){
                    Log.w(TAG, "================no route founded==========="+e.getCause());
                    showErrMessage();
                }
            }
            buffReader.close();

        }catch(Exception e){
            Log.w(TAG, e.getCause());
        }finally{
            //conn.disconnect();
        }

        return fTripNo;
    }

    public void showErrMessage(){
        AlertDialog.Builder builder = new AlertDialog.Builder(RouteActivity.this, R.style.AppCompatAlertDialogStyle);
        builder.setTitle(R.string.error_title);
        builder.setMessage(R.string.error_message);
        builder.setPositiveButton(android.R.string.ok, null);
        builder.show();
    }

}
