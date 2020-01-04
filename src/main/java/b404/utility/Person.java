package main.java.b404.utility;

public class Person {
    private Integer userID;
    private char passwordHash;
    private String name;
    private String email;
    private String title;
    private Integer companyID;
    private Integer accessLevelID;

    public Integer getUserID() {
        return userID;
    }

    public void setUserID(Integer userID) {
        this.userID = userID;
    }

    public char getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(char passwordHash) {
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
