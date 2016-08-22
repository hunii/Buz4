package com.example.james.buz4;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import model.Stop;


public class MainActivity extends AppCompatActivity {
    private final String api_key = "94c2af270e2b43ac8a496b8f5bd4a9b4";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }



    public void onClickFindBtn(View v){
        EditText edit1 = (EditText)findViewById(R.id.editText);

        if(edit1.getText().toString() != null || !edit1.getText().toString().isEmpty())
            new retrieveDatafromAPI().execute(edit1.getText().toString());
    }




    class retrieveDatafromAPI extends AsyncTask<String, Void, Void> {
        String json="#";
        ArrayList<Stop> listStop = new ArrayList<Stop>();

        @Override
        protected void onPreExecute(){

        }

        @Override
        protected Void doInBackground(String... params) {
            StringBuilder strJson = new StringBuilder();
            HttpURLConnection conn;
            Log.w("myApp", "----------------"+params[0].toString());


            try {
                URL url = new URL("https://api.at.govt.nz/v1/gtfs/stops/stopId/"+params[0].toString()+"?api_key="+api_key);
                conn = (HttpURLConnection) url.openConnection();
                conn.connect();
                InputStream stream = new BufferedInputStream(conn.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
                String line;
                while ((line = reader.readLine()) != null) {
                    strJson.append(line);
                }
                String data = strJson.toString();


                JSONObject jsonRootObject = new JSONObject(strJson.toString());

                //Get the instance of JSONArray that contains JSONObjects
                JSONArray jsonArray = jsonRootObject.optJSONArray("response");

                //Iterate the jsonArray and print the info of JSONObjects
                for(int i=0; i < jsonArray.length(); i++){
                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    int id = Integer.parseInt(jsonObject.optString("stop_id").toString());
                    String name = jsonObject.optString("stop_name").toString();
                    double lat = Double.parseDouble(jsonObject.optString("stop_lat").toString());
                    double lon = Double.parseDouble(jsonObject.optString("stop_lon").toString());
                    int locationType = Integer.parseInt(jsonObject.optString("location_type").toString());
                    int parent=0;
                    try {
                        parent = Integer.parseInt(jsonObject.optString("parent_station").toString());
                    }catch(Exception e){
                        parent = 0;
                    }

                    Stop aStop = new Stop(id,name,lat,lon,locationType,parent);
                    listStop.add(aStop);

                }

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
            TextView tt = (TextView)findViewById(R.id.textView3);
            String output = listStop.get(0).getStop_Id()+" "+listStop.get(0).getStop_Name()+" "+listStop.get(0).getStop_Lat()+" "+listStop.get(0).getStop_Lon()+" ";

            tt.setText(output);
            Log.w("myApp", "---------------F   I   N    A    L---------------");
        }

    }



}
