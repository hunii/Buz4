package model;

/**
 * Created by James on 6/08/2016.
 */
public class Route {
    private String route_Id;
    private String route_Agency;
    private String route_Short_Name;
    private String route_Long_Name;
    private int route_Type;

    public Route(String R_id, String A_id, String shortName, String longName, int R_type){
        route_Id = R_id;
        route_Agency = A_id;
        route_Short_Name = shortName;
        route_Long_Name = longName;
        route_Type = R_type;
    }

    public String getRoute_Id() {
        return route_Id;
    }

    public void setRoute_Id(String route_Id) {
        this.route_Id = route_Id;
    }

    public String getRoute_Agency() {
        return route_Agency;
    }

    public void setRoute_Agency(String route_Agency) {
        this.route_Agency = route_Agency;
    }

    public String getRoute_Short_Name() {
        return route_Short_Name;
    }

    public void setRoute_Short_Name(String route_Short_Name) {
        this.route_Short_Name = route_Short_Name;
    }

    public String getRoute_Long_Name() {
        return route_Long_Name;
    }

    public void setRoute_Long_Name(String route_Long_Name) {
        this.route_Long_Name = route_Long_Name;
    }

    public int getRoute_Type() {
        return route_Type;
    }

    public void setRoute_Type(int route_Type) {
        this.route_Type = route_Type;
    }
}
