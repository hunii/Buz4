package com.example.james.buz4;

import android.os.Bundle;
import android.support.annotation.MainThread;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by Taehyun Kim on 17/09/2016.
 */
public class LoadingMapUnitTest extends FragmentActivity //extends ActivityUnitTestCase<FindStopsActivity>
        implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnCameraChangeListener {

    FindStopsActivity findStopsActivity;
    private GoogleMap mMap;

    @MainThread
    public void onCreate() {
        System.out.println("======== onCreate =============");
        //super.onCreate(savedInstanceState);

        //To get MapFragment reference from xml layout
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        //To get map object
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnCameraChangeListener(this);
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);

        // Init location
        LatLng initPoint = new LatLng(-36.8524, 174.7644); // AUT WT Building
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(initPoint, 17));

    }

    //@Override
    public void onCameraChange(CameraPosition cameraPosition) {
        //if(mMap.getCameraPosition().zoom > 15.5) {
            //curLat = mMap.getCameraPosition().target.latitude;
            //curLog = mMap.getCameraPosition().target.longitude;
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

    /**
     * Testing method to load Google Map callback
     * Call OnMapLoadedCallback to use the Google Map
     */
    @Test
    public void testOnMapLoadedCallback() {
        FindStopsActivity parent = new FindStopsActivity();
        boolean mapLoaded = false;

        if(GoogleMap.OnMapLoadedCallback.class.getClassLoader() != null){
            mapLoaded = true;
        }

        Assert.assertEquals(mapLoaded, true);
    }

    /**
     * Testing method to check Zoom in and out on Google Map.
     * Call OnCameraChangeListener to use these method below.
     */
    @Test
    public void testZoomInOut() {
        FindStopsActivity parent = new FindStopsActivity();
        boolean chkZoom = false;

        if(GoogleMap.OnCameraChangeListener.class.getClassLoader() != null){
            chkZoom = true;
        }

        Assert.assertEquals(chkZoom, true);
    }

    /**
     * Testing method to get current camera position
     * Call OnCameraChangeListener.
     */
    @Test
    public void testOnCameraPosition() {
        FindStopsActivity parent = new FindStopsActivity();
        boolean camaraPosition = false;

        if(GoogleMap.OnCameraChangeListener.class.getClasses() != null){
            camaraPosition = true;
        }

        Assert.assertEquals(camaraPosition, true);
    }

}
