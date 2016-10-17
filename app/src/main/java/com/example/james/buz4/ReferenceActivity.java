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
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 *This class represent activity to show open source licensing and description of developer.
 *
 * Developed by James Joung
 * Version Updated: 3 Oct 2016
 */

public class ReferenceActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public static final String TAG = "ReferenceActivity";
    final Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reference);
        initSlideBar();

        String source = getReferencings();

        TextView reference = (TextView)findViewById(R.id.references);
        reference.setMovementMethod(new ScrollingMovementMethod());
        reference.setText(source);
    }

    public void initSlideBar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_reference);
        setSupportActionBar(toolbar);
        getSupportActionBar().setSubtitle("References");

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_reference);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_reference);
        navigationView.setNavigationItemSelectedListener(this);
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
            Intent intent = new Intent(ReferenceActivity.this, FavouriteActivity.class);
            startActivity(intent);
        } else if (id == R.id.mFindStop) { // Find a Stop
            Intent intent = new Intent(ReferenceActivity.this, FindStopsActivity.class);
            startActivity(intent);
        } else if (id == R.id.mViewRoutes) { // View routes
            routeDialog();
        } else if (id == R.id.mFeedback) { // Feedback
            Intent intent = new Intent(ReferenceActivity.this, FeedbackActivity.class);
            startActivity(intent);
        } else if (id == R.id.mAtHop) { // AT HOP
            Intent intent = new Intent(ReferenceActivity.this, WebserviceActivity.class);
            startActivity(intent);
        } else if (id == R.id.mReference) { // References
            Intent intent = new Intent(ReferenceActivity.this, ReferenceActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_reference);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    /**
     * Read text file that contain open source licensing description
     * and outputs onto the TextView in Reference XML
     * @return String value of open source licensing document
     */
    public String getReferencings(){
        String sources = "";
        try {
            InputStream iStream = getResources().getAssets().open("open_source.txt");
            InputStreamReader iStreamReader = new InputStreamReader(iStream);
            BufferedReader buffReader = new BufferedReader(iStreamReader);
            String line;
            while ((line = buffReader.readLine()) != null)
                sources += line+"\n";
        }catch(Exception e){sources += "Failed to load open source description";}

        return sources;
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
                                Intent intent = new Intent(ReferenceActivity.this, RouteActivity.class);
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

}
