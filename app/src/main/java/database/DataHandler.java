package database;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.ContactsContract;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;

import model.Stop;


public class DataHandler extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "myDB.db";
    public static final String TABLE_stops = "stop";

    private HashMap hp;


    public DataHandler(Context context)
    {
        super(context, DATABASE_NAME , null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
//        db.execSQL(
//                "CREATE TABLE "+TABLE_stops +
//                        " (id integer primary key, stop_id integer,lat BLOB,lon BLOB, stop_name text,parent_stop text)"
//        );

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){

    }
    public void createTableStop(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(
                "CREATE TABLE "+TABLE_stops +
                        " (id integer primary key, stop_id integer,lat BLOB,lon BLOB, stop_name text,parent_stop text)"
        );

    }
    public void deleteTable(String tableName){
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.execSQL("DROP TABLE " + tableName + ";");
        }catch(Exception e){}
    }

    public boolean insertStop  (ArrayList<Stop> listStop)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        boolean success = false;
        int i=0;
        try {
            success = true;
            for (Stop aStop : listStop) {
                ContentValues contentValues = new ContentValues();
                contentValues.put("stop_id", aStop.getStop_Id());
                contentValues.put("lat", aStop.getStop_Lat());
                contentValues.put("lon", aStop.getStop_Lon());
                contentValues.put("stop_name", aStop.getStop_Name());
                contentValues.put("parent_stop", aStop.getParent_Stop());
                db.insert("stop", null, contentValues);
                Log.w("myApp", "-----------------DB - Stop--------------" + i);
                i++;
            }
        }catch(Exception e){success = false;}
        return success;
    }


    public ArrayList<String> getStop(){
        ArrayList<String> array_list = new ArrayList<String>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from stop", null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            array_list.add(res.getString(res.getColumnIndex("stop_id")));
            res.moveToNext();
        }
        return array_list;

    }



}
