package model;

/**
 * Created by James on 6/08/2016.
 */
public class Agency {
    private String agency_Id;
    private String agency_Name;

    public Agency (String agencyId, String agencyName){
        this.agency_Id = agencyId;
        this.agency_Name = agencyName;
    }

    public String getAgency_Id() {
        return agency_Id;
    }

    public void setAgency_Id(String agency_Id) {
        this.agency_Id = agency_Id;
    }

    public String getAgency_Name() {
        return agency_Name;
    }

    public void setAgency_Name(String agency_Name) {
        this.agency_Name = agency_Name;
    }
}
