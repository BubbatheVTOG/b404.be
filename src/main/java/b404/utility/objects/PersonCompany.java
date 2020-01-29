package b404.utility.objects;

public class PersonCompany {
    private int UUID;
    private int companyID;

    public PersonCompany(int UUID, int companyID){
        this.UUID = companyID;
        this.companyID = companyID;
    }

    public int getUUID() {
        return UUID;
    }

    public void setUUID(int UUID) {
        this.UUID = UUID;
    }

    public int getCompanyID() {
        return companyID;
    }

    public void setCompanyID(int companyID) {
        this.companyID = companyID;
    }
}
