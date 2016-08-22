package model;

/**
 * Created by James on 6/08/2016.
 */
public class Version {
    private String verions_Id;
    private String version_Start_Date;
    private String version_End_Date;

    public Version(String verions_Id, String version_Start_Date, String version_End_Date) {
        this.verions_Id = verions_Id;
        this.version_Start_Date = version_Start_Date;
        this.version_End_Date = version_End_Date;
    }

    public String getVerions_Id() {
        return verions_Id;
    }

    public void setVerions_Id(String verions_Id) {
        this.verions_Id = verions_Id;
    }

    public String getVersion_Start_Date() {
        return version_Start_Date;
    }

    public void setVersion_Start_Date(String version_Start_Date) {
        this.version_Start_Date = version_Start_Date;
    }

    public String getVersion_End_Date() {
        return version_End_Date;
    }

    public void setVersion_End_Date(String version_End_Date) {
        this.version_End_Date = version_End_Date;
    }
}
