package blink.utility.objects;

import com.google.gson.Gson;

public class Company {
    private int companyID;
    private String companyName;
    private transient Gson gson;

    public Company(int companyID, String companyName){
        this.companyID = companyID;
        this.companyName = companyName;
        this.gson = new Gson();
    }

    public Company(String companyName){
        this.companyName = companyName;
    }

    public int getCompanyID() {
        return companyID;
    }

    public void setCompanyID(int companyID) {
        this.companyID = companyID;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }
}
