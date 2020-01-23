package b404.utility.objects;

public class Access {
    private int accessLevelID;
    private String accessLevelName;

    public Access(int accessLevelID, String accessLevelName) {
        this.accessLevelID = accessLevelID;
        this.accessLevelName = accessLevelName;
    }

    public Access(String accessLevelName) { this.accessLevelName = accessLevelName; }

    public int getAccessLevelID() { return accessLevelID; }

    public void setAccessLevelID(int accessLevelID) { this.accessLevelID = accessLevelID; }

    public String getAccessLevelName() { return accessLevelName; }

    public void setAccessLevelName(String accessLevelName) { this.accessLevelName = accessLevelName; }
}
