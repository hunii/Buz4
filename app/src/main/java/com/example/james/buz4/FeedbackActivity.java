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
import android.widget.EditText;
import android.widget.TextView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
/**
 * This class represent activity to send a feedback of the application to developer
 * by using okhttp api, google form api and google spreadsheet api.
 *
 * Developed & updated by Jiwon Lee on 2016-10-15.
 */
public class FeedbackActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public static final String TAG = "FeedbackActivity";

    //The Feedback page get two inputs which are app user name and feedback content.
    private EditText mFeedback;
    private TextView nameInputField;
    final Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        initSlideBar();

        //The page calls FeedbackSpreadsheetWebService interfaces and uses okhttp library.
        Retrofit retrofit = new Retrofit.Builder().baseUrl("https://docs.google.com/forms/d/e/").build();
        final FeedbackSpreadsheetWebService spreadsheetWebService = retrofit.create(FeedbackSpreadsheetWebService.class);

        mFeedback = (EditText)findViewById(R.id.feedback_content);
        nameInputField=(TextView)findViewById(R.id.feedback_name_input);

        //Set onClickListener to the send button. The button makes editText, TextView to String and sends the strings to google form.
        findViewById(R.id.send_button).setOnClickListener(
                new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        findViewById(R.id.send_button).setEnabled(false);
                        String nameInput = nameInputField.getText().toString();
                        String feedMemo = mFeedback.getText().toString();
                        Call<Void> completeFeedbackCall = spreadsheetWebService.completeFeedback(nameInput,feedMemo);
                        completeFeedbackCall.enqueue(callCallback);
                    }
                }
        );

    }

    public void initSlideBar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_feedback);
        setSupportActionBar(toolbar);
        getSupportActionBar().setSubtitle("Feedback");

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_feedback);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_feedback);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_feedback);
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
            Intent intent = new Intent(FeedbackActivity.this, FavouriteActivity.class);
            startActivity(intent);
        } else if (id == R.id.mFindStop) { // Find a Stop
            Intent intent = new Intent(FeedbackActivity.this, FindStopsActivity.class);
            startActivity(intent);
        } else if (id == R.id.mViewRoutes) { // View routes
            routeDialog();
        } else if (id == R.id.mFeedback) { // Feedback
            Intent intent = new Intent(FeedbackActivity.this, FeedbackActivity.class);
            startActivity(intent);
        } else if (id == R.id.mAtHop) { // AT HOP
            Intent intent = new Intent(FeedbackActivity.this, WebserviceActivity.class);
            startActivity(intent);
        } else if (id == R.id.mReference) { // References
            Intent intent = new Intent(FeedbackActivity.this, ReferenceActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_feedback);
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
                                Intent intent = new Intent(FeedbackActivity.this, RouteActivity.class);
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

    private  final Callback<Void> callCallback = new Callback<Void>() {
        @Override
        //The response of Callback, it shows alert dialog and reset the feedback content.
        public void onResponse(Response<Void> response) {

            Log.d("XXX","Submitted. "+response);
            AlertDialog.Builder builder = new AlertDialog.Builder(FeedbackActivity.this, R.style.AppCompatAlertDialogStyle);
            builder.setTitle(R.string.post_feedback_title);
            builder.setMessage(R.string.post_feedback_success);
            builder.setPositiveButton(android.R.string.ok,null);
            builder.show();

            mFeedback.setText(null);
            nameInputField.setText(null);
        }

        @Override
        public void onFailure(Throwable t) {
            Log.e("XXX","Failied", t);
            AlertDialog.Builder builder = new AlertDialog.Builder(FeedbackActivity.this, R.style.AppCompatAlertDialogStyle);
            builder.setTitle(R.string.post_feedback_title);
            builder.setMessage(R.string.post_feedback_failed);
            builder.setPositiveButton(android.R.string.ok, null);
            builder.show();

        }
    };
}
