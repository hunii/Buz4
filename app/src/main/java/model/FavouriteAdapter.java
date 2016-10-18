package model;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.james.buz4.R;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

/**
 * Created by James on 10/10/2016.
 */
public class FavouriteAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Favourite> mFavouriteList;
    private String fileNameFavourite = "favourite.txt";

    public FavouriteAdapter(Context context) {
        this.context = context;
        mFavouriteList = new ArrayList<Favourite>();
    }

    public boolean fetchNewFavouriteList(){
        ArrayList<Favourite> oldList = mFavouriteList;
        mFavouriteList = new ArrayList<Favourite>();
        try {
            InputStream inputStream = context.openFileInput(fileNameFavourite);
            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String all = "";
                String receiveString = "";

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    all += receiveString+"\n";
                    String[] lines = receiveString.split("//");
                    mFavouriteList.add(new Favourite(lines[0],lines[1]));
                    Log.e("fectching...", ""+lines[0]+" // "+lines[1]);
                }

                Log.e("fetching testing2...", ""+all);
                inputStream.close();
            }
        }catch(Exception e){
            mFavouriteList = oldList;
            return false;
        }
        return true;
    }


    public ArrayList<Favourite> getFavList(){
        return mFavouriteList;
    }

    public boolean checkIsFavourite(String busNo){
        fetchNewFavouriteList();
        Log.e("check in the list","start checking"+busNo);
        for(int i = 0 ; i<mFavouriteList.size(); i++)
            if(mFavouriteList.get(i).getBusStopNo().equals(busNo)) {
                Log.e("check in the list","its in the list");
                return true;
            }
        Log.e("check in the list","list nothing");
        return false;
    }

    public boolean addFavourite(String busNo){
        try{
            if(busNo == null || !busNo.isEmpty()) {
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(fileNameFavourite, Context.MODE_APPEND));
                outputStreamWriter.write(""+busNo+"//"+busNo+"\n");
                outputStreamWriter.close();
                Log.e("AddFavourite","Added"+busNo+" to file");
            }else{
                Log.e("AddFavourite","FAILED because busNo is empty or null");
            }
        }catch(Exception e){
            return false;
        }
        return true;
    }

    //Testing purpose ONLY
    public boolean deleteTexts(){
        try{
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(fileNameFavourite, Context.MODE_PRIVATE));
            outputStreamWriter.write("");
            outputStreamWriter.close();
        }catch(Exception e){
            return false;
        }
        return true;
    }

    public boolean editFavourite(String newTitle, String busNo){
        try {
            //Read file and EDIT busNO
            InputStream inputStream = context.openFileInput(fileNameFavourite);
            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String allTexts = "";
                String receiveString = "";

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    String[] lines = receiveString.split("//");
                    if(lines[1].equals(busNo)){
                        allTexts += newTitle+"//"+busNo+"\n";
                    }else {
                        allTexts += receiveString + "\n";
                    }
                }
                inputStream.close();

                //Rewrite to File
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(fileNameFavourite, Context.MODE_PRIVATE));
                outputStreamWriter.write(allTexts);
                outputStreamWriter.close();

            }
        }catch(Exception e){
            return false;
        }
        return true;
    }

    public boolean deleteFavourite(String busNo){

        try {
            //Read file and remove busNO
            InputStream inputStream = context.openFileInput(fileNameFavourite);
            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String allTexts = "";
                String receiveString = "";

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    String[] lines = receiveString.split("//");
                    if(!lines[1].equals(busNo))
                        allTexts += receiveString+"\n";
                }
                inputStream.close();

                //Rewrite to File
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(fileNameFavourite, Context.MODE_PRIVATE));
                outputStreamWriter.write(allTexts);
                outputStreamWriter.close();

            }
        }catch(Exception e){
            return false;
        }
        return true;

    }


    @Override
    public int getCount() {
        return mFavouriteList.size();
    }

    @Override
    public Object getItem(int i) {
        return mFavouriteList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return mFavouriteList.size();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v = View.inflate(context, R.layout.list_favourite, null);

        TextView favouriteBusNo = (TextView) v.findViewById(R.id.favou_busNo);
        TextView favourite = (TextView) v.findViewById(R.id.favourite_list);

        //Display bus time table
        favouriteBusNo.setText(mFavouriteList.get(i).getBusStopNo());
        favourite.setText(mFavouriteList.get(i).getViewName());

        v.setTag(i);
        return v;
    }

}
