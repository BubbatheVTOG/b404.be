package b404.utility;

/**
 * Represents a singular Person instance
 */
public class Person {
    private int userID;
    private String passwordHash;
    private String name;
    private String email;
    private String title;
    private int companyID;
    private int accessLevelID;

    //Basic constructor
    public Person() {
    }

    //Constructor for all necessary information
    public Person(int userID, String passwordHash, String name, int companyID, int accessLevelID) {
        this.userID = userID;
        this.passwordHash = passwordHash;
        this.name = name;
        this.companyID = companyID;
        this.accessLevelID = accessLevelID;
    }

    //Constructor for all information a Person can contain
    public Person(int userID, String passwordHash, String name, String email, String title, int companyID, int accessLevelID) {
        this.userID = userID;
        this.passwordHash = passwordHash;
        this.name = name;
        this.email = email;
        this.title = title;
        this.companyID = companyID;
        this.accessLevelID = accessLevelID;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getCompanyID() {
        return companyID;
    }

    public void setCompanyID(int companyID) {
        this.companyID = companyID;
    }

    public int getAccessLevelID() {
        return accessLevelID;
    }

    public void setAccessLevelID(int accessLevelID) {
        this.accessLevelID = accessLevelID;
    }
}
