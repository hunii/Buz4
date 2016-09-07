package model;

/**
 * Created by James on 6/08/2016.
 */
public class Stop {
    private int stop_Id;
    private String stop_Name;
    private double stop_Lat;
    private double stop_Lon;

    public Stop(int stop_Id, String stop_Name, double stop_Lat, double stop_Lon) {
        this.stop_Id = stop_Id;
        this.stop_Name = stop_Name;
        this.stop_Lat = stop_Lat;
        this.stop_Lon = stop_Lon;
    }

    public int getStop_Id() {
        return stop_Id;
    }

    public void setStop_Id(int stop_Id) {
        this.stop_Id = stop_Id;
    }

    public String getStop_Name() {
        return stop_Name;
    }

    public void setStop_Name(String stop_Name) {
        this.stop_Name = stop_Name;
    }

    public double getStop_Lat() {
        return stop_Lat;
    }

    public void setStop_Lat(double stop_Lat) {
        this.stop_Lat = stop_Lat;
    }

    public double getStop_Lon() {
        return stop_Lon;
    }

    public void setStop_Lon(double stop_Lon) {
        this.stop_Lon = stop_Lon;
    }

}
