package model;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.james.buz4.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
        View v = View.inflate(context, R.layout.list_bustime,null);
        TextView busTime = (TextView)v.findViewById(R.id.bus_time);

        long currentTime = System.currentTimeMillis();
        Date dateForm = new Date(currentTime);
        int currentTimeinMilli = (dateForm.getHours()*3600)+(dateForm.getMinutes()*60)+(dateForm.getSeconds());

        int timeinsec = mTripStopList.get(i).getArrival_Time();

        int h = timeinsec/3600;
        int m = (timeinsec/60)%60;
        int s = timeinsec%60;
        Log.w("+++++++++"+timeinsec,""+h+":"+m+":"+s);
        if(s >= 30)
            m+=1;
        if(h == dateForm.getHours())
            busTime.setText(String.format("%02d:%02d",h,m)+"  "+(m-dateForm.getMinutes())+" mins");
        else
            busTime.setText(String.format("%02d:%02d",h,m));
        v.setTag(i);

        return v;
    }
}
