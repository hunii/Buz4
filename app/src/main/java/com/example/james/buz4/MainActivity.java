package com.example.james.buz4;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
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
import java.util.Collections;
import java.util.Date;

import model.TripStop;
import model.TripStopAdapter;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new stopTimesByStopId().execute(getIntent().getSerializableExtra("busStopNo").toString());
    }

    class stopTimesByStopId extends AsyncTask<String, Void, Void> {
        ArrayList<TripStop> listStop = new ArrayList<>();

        @Override
        protected void onPreExecute(){

        }

        @Override
        protected Void doInBackground(String... params) {
            StringBuilder strJson = new StringBuilder();
            HttpURLConnection conn;

            try {
                URL url = new URL("https://api.at.govt.nz/v1/gtfs/stopTimes/stopId/"+getIntent().getSerializableExtra("busStopNo").toString()+"?api_key="+getResources().getString(R.string.at_apis_key));
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

                long currentTime = System.currentTimeMillis();
                Date dateForm = new Date(currentTime);
                int currentTimeinMilli = (dateForm.getHours()*3600)+(dateForm.getMinutes()*60)+(dateForm.getSeconds());
                //Iterate the jsonArray and print the info of JSONObjects
                for(int i=0; i < jsonArray.length(); i++){
                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    String trip = jsonObject.optString("trip_id").toString();
                    int id = Integer.parseInt(jsonObject.optString("stop_id").toString());
                    int arr = Integer.parseInt(jsonObject.optString("arrival_time_seconds").toString());
                    int dep = Integer.parseInt(jsonObject.optString("departure_time_seconds").toString());
                    int seq = Integer.parseInt(jsonObject.optString("stop_sequence").toString());

                    if(arr >= currentTimeinMilli && arr < currentTimeinMilli+7200) {
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
            ListView listView = (ListView)findViewById(R.id.listview_tripstop);
            TripStopAdapter tripAdap = new TripStopAdapter(getApplicationContext(),listStop);

            listView.setAdapter(tripAdap);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){

                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Toast.makeText(getApplicationContext(),"Clicked id of "+ view.getTag(), Toast.LENGTH_SHORT).show();
                }
            });

        }

    }



}
