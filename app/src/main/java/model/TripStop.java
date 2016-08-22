package model;

/**
 * Created by James on 6/08/2016.
 */
public class TripStop {
    private Trip trip;
    private Stop stop;
    private String arrival_Time;
    private String depart_Time;
    private int stop_Sequence;

    public TripStop(Trip trip, Stop stop, String arrival_Time, String depart_Time, int stop_Sequence) {
        this.trip = trip;
        this.stop = stop;
        this.arrival_Time = arrival_Time;
        this.depart_Time = depart_Time;
        this.stop_Sequence = stop_Sequence;
    }

    public Trip getTrip() {
        return trip;
    }

    public void setTrip(Trip trip) {
        this.trip = trip;
    }

    public Stop getStop() {
        return stop;
    }

    public void setStop(Stop stop) {
        this.stop = stop;
    }

    public String getArrival_Time() {
        return arrival_Time;
    }

    public void setArrival_Time(String arrival_Time) {
        this.arrival_Time = arrival_Time;
    }

    public String getDepart_Time() {
        return depart_Time;
    }

    public void setDepart_Time(String depart_Time) {
        this.depart_Time = depart_Time;
    }

    public int getStop_Sequence() {
        return stop_Sequence;
    }

    public void setStop_Sequence(int stop_Sequence) {
        this.stop_Sequence = stop_Sequence;
    }
}
