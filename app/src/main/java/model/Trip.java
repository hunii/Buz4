package model;

/**
 * Created by James on 6/08/2016.
 */
public class Trip {
    private String trip_Id;
    private Route trip_Route;
    private String service_Id;
    private String trip_headSign;
    private Shape trip_Shape;

    public Trip(String trip_Id, Route trip_Route, String service_Id, String trip_headSign, Shape trip_Shape) {
        this.trip_Id = trip_Id;
        this.trip_Route = trip_Route;
        this.service_Id = service_Id;
        this.trip_headSign = trip_headSign;
        this.trip_Shape = trip_Shape;
    }

    public String getTrip_Id() {
        return trip_Id;
    }

    public void setTrip_Id(String trip_Id) {
        this.trip_Id = trip_Id;
    }

    public Route getTrip_Route() {
        return trip_Route;
    }

    public void setTrip_Route(Route trip_Route) {
        this.trip_Route = trip_Route;
    }

    public String getService_Id() {
        return service_Id;
    }

    public void setService_Id(String service_Id) {
        this.service_Id = service_Id;
    }

    public String getTrip_headSign() {
        return trip_headSign;
    }

    public void setTrip_headSign(String trip_headSign) {
        this.trip_headSign = trip_headSign;
    }

    public Shape getTrip_Shape() {
        return trip_Shape;
    }

    public void setTrip_Shape(Shape trip_Shape) {
        this.trip_Shape = trip_Shape;
    }
}
