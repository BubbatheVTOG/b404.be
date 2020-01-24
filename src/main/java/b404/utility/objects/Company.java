package b404.utility.objects;

public class Company {
    private int companyID;
    private String companyName;

    public Company(int companyID, String companyName){
        this.companyID = companyID;
        this.companyName = companyName;
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

    public String toJSON(){
        return "{" +
                "\"companyID\":" + this.companyID + "," +
                "\"companyName\":\"" + this.companyName + "\"," +
                "}";
    }
}
