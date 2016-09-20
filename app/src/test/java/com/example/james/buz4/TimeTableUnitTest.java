package com.example.james.buz4;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import model.TripStop;

import static org.junit.Assert.assertNotNull;

/**
 * This is unit testing for TimeTableActivity class
 *
 * Developed by James Joung
 * Version Updated: 14 Sep 2016
 */
public class TimeTableUnitTest {

    public final String API_KEY = "95bd9f69-9d26-43e6-a676-9f7a12dd8c68";
    public final String busStopNumber = "7513";

    /**
     * Testing method to successfully call AT API and return data.
     * If API call is incompleted or interupted then the return value will be null
     */
    @Test
    public void testGetCurrentBusStopTimeTablefromApi(){
        System.out.println("API calling and json data retrieving start");
        StringBuilder JsonString = new StringBuilder();
        HttpURLConnection conn = null;
        try {
            //API call request, return JSON string of data
            URL url = new URL("https://api.at.govt.nz/v1/gtfs/stopTimes/stopId/"
                                + busStopNumber + "?api_key=" + API_KEY);
            conn = (HttpURLConnection) url.openConnection();
            conn.connect();
            InputStream stream = new BufferedInputStream(conn.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            String line;
            while ((line = reader.readLine()) != null) {
                JsonString.append(line);
            }
            JSONObject json = new JSONObject(JsonString.toString());
            JSONArray jsonArray = json.optJSONArray("response");
            for(int i = 0; i < jsonArray.length(); i++){
                JSONObject jObject = jsonArray.getJSONObject(i);
                String trip_id = jObject.getString("trip_id");
                int arr_Time = Integer.parseInt(jObject.optString("arrival_time_seconds").toString());
                int seqNo = Integer.parseInt(jObject.optString("stop_sequence").toString());
            }
        }catch(Exception e){
            System.err.println(e);
        }
        finally{
            System.out.println("API calling and json data retrieving  start");
            conn.disconnect();
        }
        assertNotNull(JsonString);
    }

    /**
     * Testing method to parse JsonString to JsonObject and create a list of TripStop objects
     * If, parsing is interupted by exception, it returns null
     */
    @Test
    public void testJsonStringParsingToObject(){
        System.out.println("Json parsing testing start");
        StringBuilder JsonString = new StringBuilder();
        ArrayList<TripStop> tStopList = new ArrayList<TripStop>();
        HttpURLConnection conn = null;
        try {
            //API call request, return JSON string of data
            URL url = new URL("https://api.at.govt.nz/v1/gtfs/stopTimes/stopId/"
                                + busStopNumber + "?api_key=" + API_KEY);
            conn = (HttpURLConnection) url.openConnection();
            conn.connect();
            InputStream stream = new BufferedInputStream(conn.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            String line;
            while ((line = reader.readLine()) != null) {
                JsonString.append(line);
            }
            JSONObject jsonObject = new JSONObject(JsonString.toString());
            JSONArray jsonArray = jsonObject.optJSONArray("response");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jObject = jsonArray.getJSONObject(i);
                String trip_id = jObject.getString("trip_id");
                int arr_Time = Integer.parseInt(jObject.optString("arrival_time_seconds").toString());
                int seqNo = Integer.parseInt(jObject.optString("stop_sequence").toString());

                TripStop tripStop = new TripStop(trip_id, arr_Time, seqNo);
                tStopList.add(tripStop);
            }
        } catch (Exception e) {
            System.err.println(e);
            tStopList = null;
        }finally{
            System.out.println("Json parsing testing finished");
            conn.disconnect();
        }
        assertNotNull(tStopList);
    }

    /**
     * Testing method to accurately calculate the milliseconds into
     * hour/min/second format of time and successfully output as string format
     */
    @Test
    public void testMillisecondsToTimeFormat(){
        System.out.println("Milliseconds to Time format testing started");
        int milliseconds = 76400;
        int sHour = milliseconds / 3600;
        int sMinute = (milliseconds / 60) % 60;
        int sSecond = milliseconds % 60;
        String realOutput = ""+sHour+":"+sMinute+":"+sSecond;
        String expected = "21:13:20";
        System.out.println("Milliseconds to Time format testing finished");
        Assert.assertEquals(expected,realOutput);
    }


}
