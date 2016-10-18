package com.example.james.buz4;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;

import model.FavouriteAdapter;
import model.ServiceCalendar;
import model.Trip;
import model.TripStop;
import model.TripStopAdapter;

/**
 *This class represent activity to show the lists of bus time table for a particular bus stop.
 *
 * Developed by James Joung
 * Version Updated: 06 Oct 2016
 */

public class TimeTableActivity extends AppCompatActivity
        implements SwipeRefreshLayout.OnRefreshListener{

    private static final String TAG = "TimeTableActivity";
    private String fileNameTrip = "at_bus_trips.txt";
    private String fileNameCalendar = "calendar.txt";
    private InputStream iStream = null;
    private InputStreamReader iStreamReader = null;
    private BufferedReader buffReader = null;

    private ArrayList<TripStop> CurrentListOfTimes = new ArrayList<TripStop>();
    private String weekofToday;

    private SwipeRefreshLayout swipeContainer;
    private ListView listView;
    private RelativeLayout errorMessageLayout;

    private RatingBar favouriteStar;
    private boolean favouriteOn;

    private FavouriteAdapter favAdap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timetable);

        initSlideBar();

        listView = (ListView) findViewById(R.id.listview_tripstop);
        errorMessageLayout = (RelativeLayout)findViewById(R.id.errorMessageLayout);

        swipeContainer = (SwipeRefreshLayout)findViewById(R.id.swipeContainer);
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        swipeContainer.setOnRefreshListener(this);

        //default
        favouriteStar = (RatingBar)findViewById(R.id.ratingBarFav);
        favAdap = new FavouriteAdapter(this);
        favouriteOn = favAdap.checkIsFavourite(getIntent().getSerializableExtra("busStopNo").toString());
        if(favouriteOn){
            Log.e("check",favouriteOn+".....................");
            favouriteStar.setIsIndicator(true);
            favouriteStar.setRating(1);
        }else{
            Log.e("check",favouriteOn+".....................");
            favouriteStar.setIsIndicator(false);
            favouriteStar.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent event) {
                    if(favouriteOn){//on red to null
                    }else{//null to red
                        if(event.getAction() == MotionEvent.ACTION_UP){
                            Log.e("Favourite Butn","Clicked Saving on favourite!");
                            favouriteStar.setRating(1);
                            favouriteStar.setIsIndicator(true);
                            favouriteOn = true;
                            favAdap.addFavourite(getIntent().getSerializableExtra("busStopNo").toString());
                        }
                    }
                    return false;
                }
            });
        }

        //Log.w("Intent value checking: ",""+getIntent().getSerializableExtra("busStopNo").toString());
        new stopTimesByStopId().execute(getIntent().getSerializableExtra("busStopNo").toString());
    }

    public void initSlideBar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_timetable);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Stop No. "+getIntent().getSerializableExtra("busStopNo").toString());
        getSupportActionBar().setSubtitle(getIntent().getSerializableExtra("busStopAddr").toString());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Handler that handle the changes during the refresh of list content and inject new list of contents to list view
     */
    Handler handler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            if(CurrentListOfTimes.size() != 0) {
                int currentTimeinMilli = getCurrentTime();

                for (int i = 0; i < CurrentListOfTimes.size(); i++)
                    if (CurrentListOfTimes.get(i).getArrival_Time() < currentTimeinMilli)
                        CurrentListOfTimes.remove(i);

                TripStopAdapter tripAdap = new TripStopAdapter(getApplicationContext(), CurrentListOfTimes);
                listView.setAdapter(tripAdap);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        Intent intent = new Intent(TimeTableActivity.this, RouteActivity.class); // Bus stop list
                        intent.putExtra("search_type", "timeTableRoute");
                        intent.putExtra("trip_id", CurrentListOfTimes.get(i).getTrip());
                        startActivity(intent);
                    }
                });
            }else{
                setErrorMsg();
            }

            swipeContainer.postDelayed(new Runnable() {

                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(),"Refreshed", Toast.LENGTH_SHORT).show();
                    swipeContainer.setRefreshing(false);
                }
            }, 1000);
        };
    };

    /**
     * Override the SwipeRefresh onFresh method
     */
    @Override
    public void onRefresh() {
        swipeContainer.postDelayed(new Runnable() {

            @Override
            public void run() {
                swipeContainer.setRefreshing(true);
                handler.sendEmptyMessage(0);
            }
        }, 1000);
    }


    /**
     * This inner class is a thread that runs a network connection to call an API for Json date
     */
    public class stopTimesByStopId extends AsyncTask<String, Void, Void> {
        ArrayList<TripStop> tStopList = new ArrayList<TripStop>();
        HashMap<String, ServiceCalendar> serviceHash = new HashMap<String, ServiceCalendar>();

        @Override
        protected void onPreExecute(){}
        /**
         * This method establishes HTTP connection to request Json data from API
         * @param params represnts string value of bus stop number
         * @return it returns a list of Trip object with trip_id, arrival_time and sequence number.
         */
        @Override
        protected Void doInBackground(String... params) {
            String JsonString;
            Log.w(TAG, "----------------BUS WEEKLY ROSTER UPDATE");
            try {

                //Bus weekly service update
                iStream = getResources().getAssets().open(fileNameCalendar);
                iStreamReader = new InputStreamReader(iStream);
                buffReader = new BufferedReader(iStreamReader);
                String rLine;
                while ((rLine = buffReader.readLine()) != null) {
                    String[] lines = rLine.split(",");
                    ServiceCalendar sc = new ServiceCalendar(lines[0]);
                    if(lines[3].equals("1"))
                        sc.Mon = true;
                    if(lines[4].equals("1"))
                        sc.Tue = true;
                    if(lines[5].equals("1"))
                        sc.Wed = true;
                    if(lines[6].equals("1"))
                        sc.Thu = true;
                    if(lines[7].equals("1"))
                        sc.Fri = true;
                    if(lines[8].equals("1"))
                        sc.Sat = true;
                    if(lines[9].equals("1"))
                        sc.Sun = true;
                    serviceHash.put(lines[0], sc);
                }
                buffReader.close();

                Log.w(TAG, "----------------START OF API CALL");
                //API calling Method
                JsonString = stopTimesByStopIdAPI(params[0].toString());
                Log.w(TAG, "----------------END of API CALL");

                JSONObject jsonRootObject = new JSONObject(JsonString);

                //Get the instance of JSONArray that contains JSONObjects
                JSONArray jsonArray = jsonRootObject.optJSONArray("response");

                int currentTimeinMilli = getCurrentTime();
                //Iterate the jsonArray and print the info of JSONObjects
                for(int i=0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    String tId = jsonObject.optString("trip_id").toString();
                    int arrTime = Integer.parseInt(jsonObject.optString("arrival_time_seconds").toString());
                    int seqNo = Integer.parseInt(jsonObject.optString("stop_sequence").toString());
                    String[] charindex = tId.split("_");
                    if((arrTime >= currentTimeinMilli) && (arrTime <= currentTimeinMilli+7200)) {
                        if (charindex[1].equals("v46.25")) {
                            boolean today = false;
                            //System.out.println(week+ "   "+serviceHash.size());
                            switch (weekofToday) {
                                case "Mon":
                                    if (serviceHash.get(tId).Mon)
                                        today = true;
                                    break;
                                case "Tue":
                                    if (serviceHash.get(tId).Tue)
                                        today = true;
                                    break;
                                case "Wed":
                                    if (serviceHash.get(tId).Wed)
                                        today = true;
                                    break;
                                case "Thu":
                                    if (serviceHash.get(tId).Thu)
                                        today = true;
                                    break;
                                case "Fri":
                                    if (serviceHash.get(tId).Fri)
                                        today = true;
                                    break;
                                case "Sat":
                                    if (serviceHash.get(tId).Sat)
                                        today = true;
                                    break;
                                case "Sun":
                                    if (serviceHash.get(tId).Sun)
                                        today = true;
                                    break;
                            }
                            if (today) {
                                tStopList.add(new TripStop(tId, arrTime, seqNo));
                            }
                        }
                    }
                }
                Collections.sort(tStopList);
            }catch(Exception e){Log.w(TAG, "-------------------no network---------------"+e+e.getCause());}
            return null;
        }

        /**
         * On onPostExecute, it executes an another thread to handle
         * received data and match it against the data file to add Bus number and destination to the list of Trip objects.
         */
        @Override
        protected void onPostExecute(Void v) {
            new readTripTxt().execute(tStopList);
        }
    }

    /**
     * This inner class is a thread running after the API call, it will read preloaded text file to match trip_id to add bus number and destination
     * to the list of Trip objects.
     */
    public class readTripTxt extends AsyncTask<ArrayList<TripStop>, Void, Void> {
        HashMap<String,Trip> findTripInfo = new HashMap<String, Trip>();
        ArrayList<TripStop> tStopList2 = new ArrayList<TripStop>();
        ArrayList<Trip> tripList = new ArrayList<Trip>();
        ArrayList<String> tripInfo = new ArrayList<>();


        @Override
        protected void onPreExecute(){}

        /**
         * Bufferreading trip_file to match with the returned list of Trip objects to add Bus number and destination
         * @param params list of Trip objects
         * @return
         */
        @Override
        protected Void doInBackground(ArrayList<TripStop>... params) {
            tStopList2 = params[0];

            try {
                iStream = getResources().getAssets().open(fileNameTrip);
                iStreamReader = new InputStreamReader(iStream);
                buffReader = new BufferedReader(iStreamReader);
                String rLine;

                while ((rLine = buffReader.readLine()) != null) {
                    String[] lines = rLine.split(",");
                    String route_id = lines[1];
                    String bus_num = route_id.substring(0, 3);
                    String trip_headsign = lines[3];
                    String trip_id = lines[6];
                    Trip route = new Trip(trip_id,route_id,bus_num,trip_headsign);
                    tripList.add(route);
                    findTripInfo.put(trip_id, route);
                }
                buffReader.close();

                for(int x=0; x < tStopList2.size(); x++) {
                    Trip tmpTripStop = findTripInfo.get(tStopList2.get(x).getTrip());
                    String roudId = tmpTripStop.getTrip_Route();
                    tStopList2.get(x).setRoute(roudId);
                    tStopList2.get(x).setDestination(tmpTripStop.getTrip_headSign());
                    tStopList2.get(x).setBusNo(roudId.substring(0, 3));
                }
            }catch(Exception e){e.getCause();}
            return null;
        }

        /**
         * Push completed list of Trip objects to the view(XML) adapter to output a list of Trip object accordingly.
         */
        @Override
        protected void onPostExecute(Void v){
            CurrentListOfTimes = tStopList2;
            findViewById(R.id.loadingPanel).setVisibility(View.GONE);
            //Inject list data into list view
            if(tStopList2.size() != 0) {
                TripStopAdapter tripAdap = new TripStopAdapter(getApplicationContext(), tStopList2);

                listView.setAdapter(tripAdap);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        Intent intent = new Intent(TimeTableActivity.this, RouteActivity.class); // Bus stop list
                        intent.putExtra("search_type", "timeTableRoute");
                        intent.putExtra("trip_id", tStopList2.get(i).getTrip());
                        startActivity(intent);
                    }
                });
            }else{
                setErrorMsg();
            }
        }
    }


    public void setErrorMsg(){
        swipeContainer.setVisibility(View.GONE);
        listView.setVisibility(View.GONE);
        errorMessageLayout.setVisibility(View.VISIBLE);
    }

    /**
     * This method is called to establish HTTP request to StopTimesByStopId API for a list of bus times table of a particular bus stop number
     * @param busStopNo String value of bus stop number
     * @return return a string value of Json data
     */
    public String stopTimesByStopIdAPI(String busStopNo){
        StringBuilder JsonString = new StringBuilder();
        HttpURLConnection conn;
        try {
            //API call request, return JSON string of data
            URL url = new URL("https://api.at.govt.nz/v1/gtfs/stopTimes/stopId/" + busStopNo + "?api_key=" + getResources().getString(R.string.at_apis_key));
            conn = (HttpURLConnection) url.openConnection();
            conn.connect();
            InputStream stream = new BufferedInputStream(conn.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            String line;

            while ((line = reader.readLine()) != null) {
                JsonString.append(line);
            }
            Log.e("sysoutLOG", JsonString.toString());
        }catch(Exception e){return null;}

        return JsonString.toString();
    }

    /**
     * Get current time in milliseconds
     * @return integer value of current time in milliseconds
     */
    public int getCurrentTime(){
        long currentTime = System.currentTimeMillis();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(currentTime);
        weekofToday =  calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.US);
        int currentTimeinMilli = (calendar.get(Calendar.HOUR_OF_DAY)*3600) + (calendar.get(Calendar.MINUTE)*60) + calendar.get(Calendar.SECOND);
        System.out.println("CURRENT TIME NOW :  "+ currentTimeinMilli);

        return currentTimeinMilli;
    }
}
