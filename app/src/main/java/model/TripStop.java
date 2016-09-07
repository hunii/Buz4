package model;

/**
 * Created by James on 6/08/2016.
 */
public class TripStop implements Comparable{
    private String trip;
    private int stop;
    private int arrival_Time;
    private int depart_Time;
    private int stop_Sequence;

    public TripStop(String trip, int stop, int arrival_Time, int depart_Time, int stop_Sequence) {
        this.trip = trip;
        this.stop = stop;
        this.arrival_Time = arrival_Time;
        this.depart_Time = depart_Time;
        this.stop_Sequence = stop_Sequence;
    }

    public String getTrip() {
        return trip;
    }

    public void setTrip(String trip) {
        this.trip = trip;
    }

    public int getStop() {
        return stop;
    }

    public void setStop(int stop) {
        this.stop = stop;
    }

    public int getArrival_Time() {
        return arrival_Time;
    }

    public void setArrival_Time(int arrival_Time) {
        this.arrival_Time = arrival_Time;
    }

    public int getDepart_Time() {
        return depart_Time;
    }

    public void setDepart_Time(int depart_Time) {
        this.depart_Time = depart_Time;
    }

    public int getStop_Sequence() {
        return stop_Sequence;
    }

    public void setStop_Sequence(int stop_Sequence) {
        this.stop_Sequence = stop_Sequence;
    }

    @Override
    public int compareTo(Object t) {
        int compareTime = ((TripStop)t).getArrival_Time();
        return this.arrival_Time - compareTime;
    }

}
