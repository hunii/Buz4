package com.example.james.buz4;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;

/**
 *This class represent activity to browse AT website within webview.
 * So that users can browse AT website for information and use top up feature in AT website
 *
 * Developed by James Joung
 * Version Updated: 6 Oct 2016
 */
public class WebserviceActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public static final String TAG = "WebserviceActivity";
    private WebView web;
    private String urlMain = "https://at.govt.nz/";
    private String urlTopup = "https://federation.aucklandtransport.govt.nz/adfs/ls/?wa=wsignin1.0&wtrealm=https://at.govt.nz&wctx=https://at.govt.nz&wreply=https://at.govt.nz/myat";

    final Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webservice);

        initSlideBar();

        web = (WebView)findViewById(R.id.webView);
        web.getSettings().setJavaScriptEnabled(true);
        web.getSettings().setAppCacheEnabled(false);
        web.getSettings().setDomStorageEnabled(true);
        web.loadUrl(urlTopup);
        web.setWebViewClient(new AucklandTransportWebClient());

    }

    public void initSlideBar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_webservice);
        setSupportActionBar(toolbar);
        getSupportActionBar().setSubtitle("AT HOP");

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_webservice);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_webservice);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_webservice);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        //int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        //if (id == R.id.action_settings) {
            return true;
        //}

        //return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.mFavourites) { // Favourite
            Intent intent = new Intent(WebserviceActivity.this, FavouriteActivity.class);
            startActivity(intent);
        } else if (id == R.id.mFindStop) { // Find a Stop
            Intent intent = new Intent(WebserviceActivity.this, FindStopsActivity.class);
            startActivity(intent);
        } else if (id == R.id.mViewRoutes) { // View routes
            routeDialog();
        } else if (id == R.id.mFeedback) { // Feedback
            Intent intent = new Intent(WebserviceActivity.this, FeedbackActivity.class);
            startActivity(intent);
        } else if (id == R.id.mAtHop) { // AT HOP
            Intent intent = new Intent(WebserviceActivity.this, WebserviceActivity.class);
            startActivity(intent);
        } else if (id == R.id.mReference) { // References
            Intent intent = new Intent(WebserviceActivity.this, ReferenceActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_webservice);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void routeDialog(){
        // get prompts.xml view
        LayoutInflater li = LayoutInflater.from(context);
        View routeView = li.inflate(R.layout.route_search, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(routeView);

        final EditText userInput = (EditText) routeView.findViewById(R.id.routeSearchInput);

        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                // get user input and set it to result
                                Intent intent = new Intent(WebserviceActivity.this, RouteActivity.class);
                                intent.putExtra("search_type", "menuRoute");
                                intent.putExtra("trip_id", userInput.getText().toString());
                                startActivity(intent);
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    /**
     * Web client that will override url loading within the web.
     * Without this client, any interaction within the web will open the default web browser
     */
    private class AucklandTransportWebClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView web,String url){
            web.loadUrl(url);
            System.out.println(web.getUrl());
            return true;
        }
    }

    /**
     * This method takes the user to main AT website
     * @param v current View
     */
    public void onClickMain(View v){
        web.loadUrl(urlMain);
    }

    /**
     * This method takes the user to Top up page on AT website
     * @param v current View
     */
    public void onClickTopup(View v){
        web.loadUrl(urlTopup);
    }

    /**
     * This method takes the user to the previous URL
     * @param v current View
     */
    public void onClickBackbtn(View v){
        web.goBack();
    }

    /**
     * This method takes the user to the forward URL
     * @param v current View
     */
    public void onClickForwardbtn(View v){
        web.goForward();
    }

}
