package model;

/**
 * Created by James on 6/08/2016.
 */
public class Shape {
    private String shape_Id;
    private double shape_Lat;
    private double shape_Lon;
    private int shape_seq;

    public Shape(int shape_seq, String shape_Id, double shape_Lat, double shape_Lon) {
        this.shape_seq = shape_seq;
        this.shape_Id = shape_Id;
        this.shape_Lat = shape_Lat;
        this.shape_Lon = shape_Lon;
    }

    public String getShape_Id() {
        return shape_Id;
    }

    public void setShape_Id(String shape_Id) {
        this.shape_Id = shape_Id;
    }

    public double getShape_Lat() {
        return shape_Lat;
    }

    public void setShape_Lat(double shape_Lat) {
        this.shape_Lat = shape_Lat;
    }

    public double getShape_Lon() {
        return shape_Lon;
    }

    public void setShape_Lon(double shape_Lon) {
        this.shape_Lon = shape_Lon;
    }

    public int getShape_seq() {
        return shape_seq;
    }

    public void setShape_seq(int shape_seq) {
        this.shape_seq = shape_seq;
    }
}
