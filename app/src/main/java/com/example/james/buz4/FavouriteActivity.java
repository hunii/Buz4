package com.example.james.buz4;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
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
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import model.Favourite;
import model.FavouriteAdapter;

/**
 * Created by Joshua Kim on 9/09/2016.
 */
public class FavouriteActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final String TAG = "FavouriteActivity";

    final Context context = this;
    public static FavouriteAdapter favAdapStatic;

    private EditText searchText;
    private Button searchButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite);

        initSlideBar();

        searchText = (EditText) findViewById(R.id.searchText);
        searchButton = (Button) findViewById(R.id.searchButton);

        searchText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });
        new favouriteFetch().execute("");

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

    }

    public void initSlideBar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_favourite);
        setSupportActionBar(toolbar);
        getSupportActionBar().setSubtitle("Favourites");

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_favourite);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_favourite);
        navigationView.setNavigationItemSelectedListener(this);

    }

    @Override
    public void onResume(){
        super.onResume();
        new favouriteFetch().execute("");
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_favourite);
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
        int id = item.getItemId();

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
        } else if (id == R.id.mAtHop) { // AT HOP
            Intent intent = new Intent(FavouriteActivity.this, WebserviceActivity.class);
            startActivity(intent);
        } else if (id == R.id.mReference) { // References
            Intent intent = new Intent(FavouriteActivity.this, ReferenceActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_favourite);
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

        Intent intent = new Intent(FavouriteActivity.this, TimeTableActivity.class); // Bus stop list
        try {
            intent.putExtra("busStopNo", Integer.parseInt(searchText.getText().toString()));
            intent.putExtra("busStopAddr", "");
        }catch(Exception e){
            Log.w("%%%%%%%%%%%%", ""+e+"         "+searchButton.getText());
        }
        startActivity(intent);
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    public class favouriteFetch extends AsyncTask<String, Void, Void> {

        ArrayList<Favourite> listFav = new ArrayList<Favourite>();
        FavouriteAdapter favAdap = new FavouriteAdapter(getApplicationContext());
        ;

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Void doInBackground(String... params) {
            //favAdap.deleteTexts();
            favAdap.fetchNewFavouriteList();
            listFav = favAdap.getFavList();
            favAdapStatic = favAdap;
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            Log.w(TAG, "----------------INEJCT FAVOURITE !!!!" + listFav.size());
            ListView favouriteListView = (ListView) findViewById(R.id.listview_favourite);
            ;

            favouriteListView.setAdapter(favAdap);
            favouriteListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Intent intent = new Intent(FavouriteActivity.this, TimeTableActivity.class); // Bus stop list

                    intent.putExtra("busStopNo", listFav.get(i).getBusStopNo().toString());
                    intent.putExtra("busStopAddr", "");
                    startActivity(intent);
                }
            });
            favouriteListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> arg0, View v, int i, long l) {
                    Log.e("LONGCLICK Test", "in onLongClick:: " + i);

                    String name = listFav.get(i).getViewName();
                    final String busNo = listFav.get(i).getBusStopNo();

                    // get prompts.xml views
                    LayoutInflater li = LayoutInflater.from(context);
                    View editView = li.inflate(R.layout.favourite_edit, null);
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                    // set prompts.xml to alertdialog builder
                    alertDialogBuilder.setView(editView);
                    final TextView textView = (TextView) editView.findViewById(R.id.edit_fav4);
                    textView.setText(busNo);

                    final EditText userInput = (EditText) editView.findViewById(R.id.edit_favName);
                    userInput.setText(name);

                    final CheckBox delCheckBox = (CheckBox) editView.findViewById(R.id.delCheckBox);
                    // set dialog message
                    alertDialogBuilder
                            .setCancelable(true)
                            .setPositiveButton("Save",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            favAdap.editFavourite(userInput.getText().toString(), busNo);
                                            Toast.makeText(getApplicationContext(), "Updated !", Toast.LENGTH_SHORT).show();
                                            dialog.cancel();
                                            new favouriteFetch().execute("");
                                        }
                                    })
                            .setNeutralButton("Delete",
                                    null
                            )
                            .setNegativeButton("Cancel",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    });
                    // create alert dialog
                    final AlertDialog alertDialog = alertDialogBuilder.create();

                    // show it
                    alertDialog.show();
                    alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            System.out.println(delCheckBox.isChecked());
                            if (delCheckBox.isChecked()) {
                                favAdap.deleteFavourite(busNo);
                                Toast.makeText(getApplicationContext(), "Deleted !", Toast.LENGTH_SHORT).show();
                                alertDialog.cancel();
                                new favouriteFetch().execute("");
                            } else {
                                delCheckBox.setText("Confirm");
                            }
                        }
                    });

                    return true;
                }
            });
        }
    }

}