package model;

/**
 * Created by James on 10/10/2016.
 */
public class Favourite {
    private String viewName;
    private String busStopNo;

    public Favourite(String viewName, String busStopNo) {
        this.viewName = viewName;
        this.busStopNo = busStopNo;
    }

    public String getViewName() {
        return viewName;
    }

    public void setViewName(String viewName) {
        this.viewName = viewName;
    }

    public String getBusStopNo() {
        return busStopNo;
    }

    public void setBusStopNo(String busStopNo) {
        this.busStopNo = busStopNo;
    }




}
