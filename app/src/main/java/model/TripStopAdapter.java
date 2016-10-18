package model;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.james.buz4.R;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by James on 11/08/2016.
 */
public class TripStopAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<TripStop> mTripStopList;

    public TripStopAdapter(Context context, ArrayList<TripStop> mTripStopList) {
        this.context = context;
        this.mTripStopList = mTripStopList;
    }

    @Override
    public int getCount() {
        return mTripStopList.size();
    }

    @Override
    public Object getItem(int i) {
        return mTripStopList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v = View.inflate(context, R.layout.list_bustime, null);
        TextView busTime = (TextView) v.findViewById(R.id.bus_time);
        TextView busTime1 = (TextView) v.findViewById(R.id.bus_time1);
        TextView busTime2 = (TextView) v.findViewById(R.id.bus_time2);
        TextView busTime3 = (TextView) v.findViewById(R.id.bus_time3);

        //Get bus number and destination
        String bNo = mTripStopList.get(i).getBusNo();
        String dest = mTripStopList.get(i).getDestination();

        if(dest != null) {
            int dstLng = dest.length();
            System.out.println(dstLng+"              ,.,..,.,.,.,.,.,.");
            if (dstLng > 10 && dstLng <= 17) {// dest string size between 13 - 17
                System.out.println(dstLng+"              ,.,..,.,.,.,.,.,."+ busTime2.getTextSize());
                busTime2.setTextSize(18);
                System.out.println(dstLng+"              ,.,..,.,.,.,.,.,."+busTime2.getTextSize());
            }
        }
        //Bus arriving time into h/m/s format
        int busTimeinMilliSec = mTripStopList.get(i).getArrival_Time();
        int sHour = busTimeinMilliSec / 3600;
        int sMinute = (busTimeinMilliSec / 60) % 60;
        int sSecond = busTimeinMilliSec % 60;
        //Log.w("TripStopAdapter", "======Bus arrive time)))   sHour===" +sHour+ ", sMinute=====" +sMinute+ ", sSecond=====" +sSecond+ ", sTIME=====" +busTimeinMilliSec);

        //Current time into h/m/s format
        long currentTime = System.currentTimeMillis();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(currentTime);
        int currentHr = calendar.get(Calendar.HOUR_OF_DAY);
        int currentMin = calendar.get(Calendar.MINUTE);
        int currentSec = calendar.get(Calendar.SECOND);
        int currentTimeinMilliSeconds = currentHr * (60 * 60) + currentMin * 60 + currentSec * 1;
        //Log.w("TripStopAdapter", "======CUrrent arrive time)))   cHour===" +currentHr+ ", cMinute=====" +currentMin+ ", cSecond=====" +currentSec+ ", cTIME=====" +currentTimeinMilliSeconds);

        //Zero decimal placing of seconds to minutes
        if (sSecond >= 30)
            sMinute += 1;
        //Change 24:00 into 00:00
        if (sHour == 24)
            sHour = 0;
        String vDueTime = "";
        String vArriveTime = String.format("%02d:%02d", sHour, sMinute);

        //If bus times are within 1 hour, due time is shown
        if((busTimeinMilliSec - currentTimeinMilliSeconds) <= 3600){
            if((busTimeinMilliSec - currentTimeinMilliSeconds) <= 120){
                vDueTime = "[  *  ]";
            }else{
                //vDueTime = (sMinute - currentMin) + " m";
                vDueTime = ("["+((busTimeinMilliSec - currentTimeinMilliSeconds)/ 60) % 60) + " m]";
            }
        }else{//If bus times are over 1 hour, due time is blank
            vDueTime = "    ";
        }

        //Display bus time table
        busTime.setText(""+bNo);
        busTime1.setText(""+vArriveTime);
        busTime2.setText(""+dest);
        busTime3.setText(""+vDueTime);

        v.setTag(i);
        return v;
    }
}
