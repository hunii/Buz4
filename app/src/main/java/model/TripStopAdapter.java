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

        long currentTime = System.currentTimeMillis();
//        Date dateForm = new Date(currentTime);
//        int currentTimeinMilli = (dateForm.getHours() * 3600) + (dateForm.getMinutes() * 60) + (dateForm.getSeconds());
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(currentTime);
        int currentTimeinMilli = (calendar.get(Calendar.HOUR_OF_DAY)*3600) + (calendar.get(Calendar.MINUTE)*60) + calendar.get(Calendar.SECOND);

        //Log.w("TripStopAdapter", "======Current Hour===" + calendar.get(Calendar.HOUR_OF_DAY) + ", Min=====" + calendar.get(Calendar.MINUTE) + ", Sec=====" + calendar.get(Calendar.SECOND));
        //Log.w("TripStopAdapter", "======currentTimeinMilli===" +currentTimeinMilli);

        int timeinsec = mTripStopList.get(i).getArrival_Time();
        String bNo = mTripStopList.get(i).getBusNo();
        String dest = mTripStopList.get(i).getDestination();


        int sHour = timeinsec / 3600;
        int sMinute = (timeinsec / 60) % 60;
        int sSecond = timeinsec % 60;
        //Log.w("TripStopAdapter", "======Bus arrive time)))   sHour===" +sHour+ ", sMinute=====" +sMinute+ ", sSecond=====" +sSecond);

        //Log.w("+++++++++" + timeinsec, "" + h + ":" + m + ":" + s);
        if (sSecond >= 30) {
            sMinute += 1;
        }

        String vDueTime = "";
        String vArriveTime = String.format("%02d:%02d", sHour, sMinute);
        //busTime.setText(bNo+"  "+dest+"  "+String.format("%02d:%02d",h,m)+"  "+(m-dateForm.getMinutes())+" mins");
        if (sHour == calendar.get(Calendar.HOUR_OF_DAY)) {
            vDueTime = (sMinute - calendar.get(Calendar.MINUTE)) + " mins";
        } else if ((sMinute - calendar.get(Calendar.MINUTE)) < 1) {
            vDueTime = "    *";
        }
        //Log.w("TripStopAdapter", "======bNo===" + bNo + ", dest=====" + dest + ", vArriveTime=====" + vArriveTime + ", vDueTime======" + vDueTime);
        //if(i == 0 || (i > 0 && mTripStopList.get(i).getBusNo() != mTripStopList.get(i-1).getBusNo() && mTripStopList.get(i).getArrival_Time() != mTripStopList.get(i-1).getArrival_Time())) {
            busTime.setText(bNo + "  " + dest + "  " + vArriveTime + "  " + vDueTime);
        //}else{
            //busTime.setText("Duplicated data!");
        //}
        v.setTag(i);
        return v;
    }
}
