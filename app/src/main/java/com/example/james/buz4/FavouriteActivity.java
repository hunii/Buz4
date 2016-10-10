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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by Joshua Kim on 9/09/2016.
 */
public class FavouriteActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final String TAG = "FavouriteActivity";

    final Context context = this;
    private Button button;
    private EditText result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setSubtitle("Favourites");

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        //drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.mFavourites) { // Favourite
            Intent intent = new Intent(FavouriteActivity.this, FavouriteActivity.class);
            startActivity(intent);
        } else if (id == R.id.mFindStop) { // Find a Stop
            Intent intent = new Intent(FavouriteActivity.this, FindStopsActivity.class);
            startActivity(intent);
        } else if (id == R.id.mViewRoutes) { // View routes
            routeDialog();
        } else if (id == R.id.mFeedback) { // Feedback
            Intent intent = new Intent(FavouriteActivity.this, FeedbackActivity.class);
            startActivity(intent);
        } else if (id == R.id.mReference) { // References
            Intent intent = new Intent(FavouriteActivity.this, ReferenceActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
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
                            Intent intent = new Intent(FavouriteActivity.this, RouteActivity.class);
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

    public void searchButtonOnClick(View V){
        EditText searchText = (EditText) findViewById(R.id.searchText);
        Button searchButton = (Button) findViewById(R.id.searchButton);

        Intent intent = new Intent(FavouriteActivity.this, TimeTableActivity.class); // Bus stop list
        try {
            intent.putExtra("busStopNo", Integer.parseInt(searchText.getText().toString()));
            intent.putExtra("busStopAddr", "");
        }catch(Exception e){
            Log.w("%%%%%%%%%%%%", ""+e+"         "+searchButton.getText());
        }
        startActivity(intent);
    }
}