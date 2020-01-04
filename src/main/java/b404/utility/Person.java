package main.java.b404.utility;

/**
 * Represents a singular Person instance
 */
public class Person {
    private Integer userID;
    private String passwordHash;
    private String name;
    private String email;
    private String title;
    private Integer companyID;
    private Integer accessLevelID;

    //Basic constructor
    public Person() {
    }

    //Constructor for all necessary information
    public Person(Integer userID, String passwordHash, String name, Integer companyID, Integer accessLevelID) {
        this.userID = userID;
        this.passwordHash = passwordHash;
        this.name = name;
        this.companyID = companyID;
        this.accessLevelID = accessLevelID;
    }

    //Constructor for all information a Person can contain
    public Person(Integer userID, String passwordHash, String name, String email, String title, Integer companyID, Integer accessLevelID) {
        this.userID = userID;
        this.passwordHash = passwordHash;
        this.name = name;
        this.email = email;
        this.title = title;
        this.companyID = companyID;
        this.accessLevelID = accessLevelID;
    }

    public Integer getUserID() {
        return userID;
    }

    public void setUserID(Integer userID) {
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

    public Integer getCompanyID() {
        return companyID;
    }

    public void setCompanyID(Integer companyID) {
        this.companyID = companyID;
    }

    public Integer getAccessLevelID() {
        return accessLevelID;
    }

    public void setAccessLevelID(Integer accessLevelID) {
        this.accessLevelID = accessLevelID;
    }
}
