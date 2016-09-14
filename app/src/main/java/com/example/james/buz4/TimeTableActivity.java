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

import model.Route;
import model.Trip;
import model.TripStop;
import model.TripStopAdapter;


public class TimeTableActivity extends AppCompatActivity {
    public static ArrayList<TripStop> StopList = new ArrayList<TripStop>();
    public static ArrayList<Route> RouteList = new ArrayList<Route>();
    private static boolean go = false;
    private static boolean goTask2 = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //String value = "7531";
        new retrieveDatafromAPI().execute(getIntent().getSerializableExtra("busStopNo").toString());
        //new retrieveDatafromAPI2().execute(value);
    }

    EditText edit1;

    public void onClickFindBtn(View v){
        //edit1 = (EditText)findViewById(R.id.editText);

        if(edit1.getText().toString() != null || !edit1.getText().toString().isEmpty()) {
            new retrieveDatafromAPI().execute(edit1.getText().toString());
        }else {

        }
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(edit1.getWindowToken(), 0);
    }



    private class retrieveDatafromAPI extends AsyncTask<String, Void, Void> {
        ArrayList<TripStop> listStop = new ArrayList<TripStop>();

        @Override
        protected void onPreExecute(){

        }

        @Override
        protected Void doInBackground(String... params) {
            StringBuilder strJson = new StringBuilder();
            HttpURLConnection conn;
            Log.w("myApp", "----------------START OF API CALL");


            try {
                Log.w("myApp", "----------------Start first conn");
                URL url = new URL("https://api.at.govt.nz/v1/gtfs/stopTimes/stopId/"+params[0].toString()+"?api_key="+getResources().getString(R.string.at_apis_key));
                conn = (HttpURLConnection) url.openConnection();
                conn.connect();
                InputStream stream = new BufferedInputStream(conn.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
                String line;
                while ((line = reader.readLine()) != null) {
                    strJson.append(line);
                }
                Log.w("myApp", "----------------after first conn");

                JSONObject jsonRootObject = new JSONObject(strJson.toString());

                //Get the instance of JSONArray that contains JSONObjects
                JSONArray jsonArray = jsonRootObject.optJSONArray("response");

                long currentTime = System.currentTimeMillis();
                //Date dateForm = new Date(currentTime);
                //int currentTimeinMilli = (dateForm.getHours()*3600)+(dateForm.getMinutes()*60)+(dateForm.getSeconds());
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(currentTime);
                int currentTimeinMilli = (calendar.get(Calendar.HOUR_OF_DAY)*3600) + (calendar.get(Calendar.MINUTE)*60) + calendar.get(Calendar.SECOND);

                //Iterate the jsonArray and print the info of JSONObjects
                for(int i=0; i < jsonArray.length(); i++){
                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    String trip = jsonObject.optString("trip_id").toString();
                    int id = Integer.parseInt(jsonObject.optString("stop_id").toString());

                    int arr = Integer.parseInt(jsonObject.optString("arrival_time_seconds").toString());
                    int dep = Integer.parseInt(jsonObject.optString("departure_time_seconds").toString());

                    int seq = Integer.parseInt(jsonObject.optString("stop_sequence").toString());

                    if((arr >= currentTimeinMilli) && (arr <= currentTimeinMilli+7200)) {
                        TripStop aStop = new TripStop(trip, id, arr, dep, seq);
                        listStop.add(aStop);
                    }
                }
                Collections.sort(listStop);

            }catch(Exception e){
                Log.w("myApp", "-------------------no network---------------"+e+e.getCause());
            }finally{
                //conn.disconnect();
            }
            return null;
        }

        protected void onProgressUpdate(){
        }

        @Override
        protected void onPostExecute(Void v){
            //Return to global variable
            StopList = listStop;
            Log.d("myApp777777777777777"+listStop.size(), ""+goTask2 );
            goTask2 = true;
            //Log.d("myApp777777777777777", ""+goTask2 );
            new retrieveDatafromAPI2().execute(listStop);
            while(!go){
                try
                {
                    Log.d("myApp", "Waiting for Task2");
                    Thread.sleep(1000);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }

            }

//            ListView listView = (ListView)findViewById(R.id.listview_tripstop);
//            TripStopAdapter tripAdap = new TripStopAdapter(getApplicationContext(),listStop);
//
//            listView.setAdapter(tripAdap);
//            listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
//
//                @Override
//                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                    Toast.makeText(getApplicationContext(),"Clicked id of "+ view.getTag(), Toast.LENGTH_SHORT).show();
//                }
//            });

            Log.w("myApp", "---------------F   I   N    A    L---------------"+listStop.size());

        }

    }


    private class retrieveDatafromAPI2 extends AsyncTask<ArrayList<TripStop>, Void, Void> {
        ArrayList<Trip> listRoute = new ArrayList<Trip>();
        HashMap<String,Trip> listRouteHash = new HashMap<String, Trip>();
        ArrayList<TripStop> listTrip = new ArrayList<TripStop>();

        @Override
        protected void onPreExecute(){

        }

        @Override
        protected Void doInBackground(ArrayList<TripStop>... params) {
            listTrip = params[0];

            while(!goTask2){
                try
                {
                    Log.d("myApp", "Waiting for Task1 all the data");
                    Thread.sleep(1000);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }

            HttpURLConnection conn;
            StringBuilder strJson5 = new StringBuilder();
            Log.w("myApp", "----------------START OF API 2 CALL");


            try {
                long startTime = System.currentTimeMillis();

                URL url23 = new URL("https://api.at.govt.nz/v1/gtfs/trips/?api_key="+getResources().getString(R.string.at_apis_key));
                conn = (HttpURLConnection) url23.openConnection();
                conn.connect();
                InputStream stream23 = new BufferedInputStream(conn.getInputStream());
                BufferedReader reader22 = new BufferedReader(new InputStreamReader(stream23));
                String line12;
                while ((line12 = reader22.readLine()) != null) {
                    strJson5.append(line12);
                }
                long endTime = System.currentTimeMillis();
                long duration = (endTime - startTime);
                Log.d("-->> Time for first", "&&&&&&&&&&&&&&&&&&&&&&&&&&"+(duration/1000.0) );

                JSONObject jsonRootObject = new JSONObject(strJson5.toString());

                //Get the instance of JSONArray that contains JSONObjects
                JSONArray jsonArray = jsonRootObject.optJSONArray("response");

                for(int i=0; i < jsonArray.length(); i++){
                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    String routeid = jsonObject.optString("route_id").toString();
                    String tripid = jsonObject.optString("trip_id").toString();
                    String destination = jsonObject.optString("trip_headsign").toString();
                    String shapeid = jsonObject.optString("shape_id").toString();
                    String serviceid = jsonObject.optString("service_id").toString();
                    //Log.w("SECOND call", "------"+i+"--------"+routeid+"   "+tripid+"   "+destination+"------------");

                        Trip route = new Trip(tripid, routeid, serviceid, destination, shapeid);
                        listRoute.add(route);
                        listRouteHash.put(tripid, route);
                }

                long startTime2 = System.currentTimeMillis();
                for(int i=0; i < listTrip.size(); i++){
                    Trip temp = listRouteHash.get(listTrip.get(i).getTrip());
                    String route = temp.getTrip_Route();

                    listTrip.get(i).setRoute(route);
                    listTrip.get(i).setDestination(temp.getTrip_headSign());
                    listTrip.get(i).setBusNo(Integer.parseInt(route.substring(0, 3)));
                }
                long endTime2 = System.currentTimeMillis();
                long duration2 = (endTime2 - startTime2);
                Log.d("-->> Time for Second", "&&&&&&&&&&&&&&&"+(duration2/1000.0) + "   Trip:" + listTrip.size() + "   Route:" + listRoute.size() );

                StopList = listTrip;
                go = true;

            }catch(Exception e){
                Log.w("myApp", "-------------------no 2 network---------------"+e+e.getCause());
            }finally{
                //conn.disconnect();
            }
            return null;
        }

        protected void onProgressUpdate(){
        }

        @Override
        protected void onPostExecute(Void v){
            //Return to global variable
            Log.d("myApp777777777777777", ""+go );
            //RouteList = listRoute;
            StopList = listTrip;
            go = true;

            ListView listView = (ListView)findViewById(R.id.listview_tripstop);
            TripStopAdapter tripAdap = new TripStopAdapter(getApplicationContext(),StopList);

            listView.setAdapter(tripAdap);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){

                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Intent intent = new Intent(TimeTableActivity.this, RouteActivity.class); // Bus stop list
                    intent.putExtra("trip_id", listTrip.get(i).getTrip());
                    startActivity(intent);
                    //Toast.makeText(getApplicationContext(),"Clicked id of "+ view.getTag(), Toast.LENGTH_SHORT).show();
                }
            });

            Log.d("myApp777777777777777", ""+go );
            Log.w("myApp", "---------------F   I   N    A    L   2 ---------------");

        }

    }

}
