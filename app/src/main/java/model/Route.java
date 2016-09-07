package model;

/**
 * Created by Taehyun on 3/09/2016.
 */
public class Route {
    private String shapeId;
    private double shapeLat;
    private double shapelon;
    private int shapeSequence;

    public Route(String sId, double sLat, double sLon, int s_sequence) {
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
