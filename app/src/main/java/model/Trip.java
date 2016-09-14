package model;

/**
 * Created by James on 6/08/2016.
 */
public class Trip {
    private String trip_Id;
    private String trip_Route;
    private String bus_num;
    private String service_Id;
    private String trip_headSign;
    private String trip_Shape;

    public Trip(String trip_Id, String trip_Route, String service_Id, String trip_headSign, String trip_Shape) {
        this.trip_Id = trip_Id;
        this.trip_Route = trip_Route;
        this.service_Id = service_Id;
        this.trip_headSign = trip_headSign;
        this.trip_Shape = trip_Shape;
    }

    public Trip(String trip_Id, String trip_Route, String bus_num, String trip_headSign) {
        this.trip_Id = trip_Id;
        this.trip_Route = trip_Route;
        this.bus_num = bus_num;
        this.trip_headSign = trip_headSign;
    }

    public String getTrip_Id() {
        return trip_Id;
    }

    public void setTrip_Id(String trip_Id) {
        this.trip_Id = trip_Id;
    }

    public String getTrip_Route() { return trip_Route; }

    public void setTrip_Route(String trip_Route) {
        this.trip_Route = trip_Route;
    }

    public String getBus_num() { return bus_num; }

    public void setBus_num(String trip_Route) {
        this.bus_num = bus_num;
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

    public String getTrip_Shape() {
        return trip_Shape;
    }

    public void setTrip_Shape(String trip_Shape) {
        this.trip_Shape = trip_Shape;
    }
}
