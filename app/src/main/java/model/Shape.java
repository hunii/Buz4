package model;

/**
 * Created by James on 6/08/2016.
 */
public class Shape {
    private String shapeId;
    private double shapeLat;
    private double shapelon;
    private int shapeSequence;

    public Shape(String sId, double sLat, double sLon, int s_sequence) {
        shapeId = sId;
        shapeLat = sLat;
        shapelon = sLon;
        shapeSequence = s_sequence;
    }

    public String getShape_Id() {
        return shapeId;
    }

    public double getShape_Latitude() {
        return shapeLat;
    }

    public double getShape_Longitude() {
        return shapelon;
    }

    public int getShape_Sequence() {
        return shapeSequence;
    }
}
