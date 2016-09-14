package com.example.james.buz4;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

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
import java.util.StringTokenizer;

import model.Trip;
import model.TripStop;
import model.TripStopAdapter;


public class TimeTableActivity extends AppCompatActivity {
    public static final String TAG = "TimeTableActivity";
    public String fileName = "at_bus_trips.txt";
    StringBuilder sBuilder = new StringBuilder();
    InputStream iStream = null;
    InputStreamReader iStreamReader = null;
    BufferedReader buffReader = null;
    StringTokenizer sTokenizer;
    String rLine = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timetable);
        new stopTimesByStopId().execute(getIntent().getSerializableExtra("busStopNo").toString());
    }

    EditText edit1;

    public void onClickFindBtn(View v){

        if(edit1.getText().toString() != null || !edit1.getText().toString().isEmpty()) {
            new stopTimesByStopId().execute(edit1.getText().toString());
        }else {

        }
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(edit1.getWindowToken(), 0);
    }



    private class stopTimesByStopId extends AsyncTask<String, Void, Void> {
        ArrayList<TripStop> tStopList = new ArrayList<TripStop>();

        @Override
        protected void onPreExecute(){

        }

        @Override
        protected Void doInBackground(String... params) {
            StringBuilder strJson = new StringBuilder();
            HttpURLConnection conn;
            Log.w(TAG, "----------------START OF API CALL");


            try {
                Log.w(TAG, "----------------Start first conn");
                URL url = new URL("https://api.at.govt.nz/v1/gtfs/stopTimes/stopId/"+params[0].toString()+"?api_key="+getResources().getString(R.string.at_apis_key));
                conn = (HttpURLConnection) url.openConnection();
                conn.connect();
                InputStream stream = new BufferedInputStream(conn.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
                String line;

                while ((line = reader.readLine()) != null) {
                    strJson.append(line);
                }
                Log.w(TAG, "----------------after first conn");

                JSONObject jsonRootObject = new JSONObject(strJson.toString());

                //Get the instance of JSONArray that contains JSONObjects
                JSONArray jsonArray = jsonRootObject.optJSONArray("response");

                long currentTime = System.currentTimeMillis();
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(currentTime);
                int currentTimeinMilli = (calendar.get(Calendar.HOUR_OF_DAY)*3600) + (calendar.get(Calendar.MINUTE)*60) + calendar.get(Calendar.SECOND);
                //Log.w(TAG, "==============Current Time========"+calendar.get(Calendar.HOUR_OF_DAY)+" : "+calendar.get(Calendar.MINUTE)+" : "+calendar.get(Calendar.SECOND));
                //Log.w(TAG, "==============currentTimeinMilli========"+currentTimeinMilli);

                //Iterate the jsonArray and print the info of JSONObjects
                for(int i=0; i < jsonArray.length(); i++){
                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    String tId = jsonObject.optString("trip_id").toString();
                    int arrTime = Integer.parseInt(jsonObject.optString("arrival_time_seconds").toString());
                    int seqNo = Integer.parseInt(jsonObject.optString("stop_sequence").toString());

                    if((arrTime >= currentTimeinMilli) && (arrTime <= currentTimeinMilli+1800)) {
                        //Log.w(TAG, "==============Trip ID========"+tId);
                        //Log.w(TAG, "==============Arrive Time========"+arrTime / 3600+" : "+(arrTime / 60) % 60+" : "+arrTime % 60);
                        TripStop tripStop = new TripStop(tId, arrTime, seqNo);
                        tStopList.add(tripStop);
                    }
                }
                Collections.sort(tStopList);

            }catch(Exception e){
                Log.w(TAG, "-------------------no network---------------"+e+e.getCause());
            }finally{
                //conn.disconnect();
            }
            return null;
        }

        protected void onProgressUpdate(){

        }

        @Override
        protected void onPostExecute(Void v) {

            new readTripTxt().execute(tStopList);

        }
    }

    private class readTripTxt extends AsyncTask<ArrayList<TripStop>, Void, Void> {
        HashMap<String,Trip> findTripInfo = new HashMap<String, Trip>();
        ArrayList<TripStop> tStopList2 = new ArrayList<TripStop>();
        ArrayList<Trip> tripList = new ArrayList<Trip>();
        ArrayList<String> tripInfo = new ArrayList<>();

        @Override
        protected void onPreExecute(){

        }

        @Override
        protected Void doInBackground(ArrayList<TripStop>... params) {
            tStopList2 = params[0];

            try {

                iStream = getResources().getAssets().open(fileName);
                iStreamReader = new InputStreamReader(iStream);
                buffReader = new BufferedReader(iStreamReader);
                rLine = buffReader.readLine();

                while ((rLine = buffReader.readLine()) != null) {
                    tripInfo.add(rLine);
                }
                buffReader.close();

                for(int t=0; t < tripInfo.size(); t++) {
                    sTokenizer = new StringTokenizer(tripInfo.get(t), ",");
                    //String block_id = sTokenizer.nextToken();
                    String route_id = sTokenizer.nextToken();
                    String bus_num = route_id.substring(0, 3);
                    String direction_id = sTokenizer.nextToken();
                    String trip_headsign = sTokenizer.nextToken();
                    String shape_id = sTokenizer.nextToken();
                    String service_id = sTokenizer.nextToken();
                    String trip_id = sTokenizer.nextToken();
                    Trip route = new Trip(trip_id,route_id,bus_num,trip_headsign);
                    tripList.add(route);
                    findTripInfo.put(trip_id, route);
                }

                for(int x=0; x < tStopList2.size(); x++) {
                    Trip tmpTripStop = findTripInfo.get(tStopList2.get(x).getTrip());
                    String roudId = tmpTripStop.getTrip_Route();
                    tStopList2.get(x).setRoute(roudId);
                    tStopList2.get(x).setDestination(tmpTripStop.getTrip_headSign());
                    tStopList2.get(x).setBusNo(roudId.substring(0, 3));
                }


            }catch(Exception e){
                e.getCause();
            }
            return null;
        }

        protected void onProgressUpdate(){
        }

        @Override
        protected void onPostExecute(Void v){

            ListView listView = (ListView) findViewById(R.id.listview_tripstop);
            TripStopAdapter tripAdap = new TripStopAdapter(getApplicationContext(), tStopList2);

            listView.setAdapter(tripAdap);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Intent intent = new Intent(TimeTableActivity.this, RouteActivity.class); // Bus stop list
                    intent.putExtra("trip_id", tStopList2.get(i).getTrip());
                    startActivity(intent);
                    //Toast.makeText(getApplicationContext(),"Clicked id of "+ view.getTag(), Toast.LENGTH_SHORT).show();
                }
            });

        }

    }

}
