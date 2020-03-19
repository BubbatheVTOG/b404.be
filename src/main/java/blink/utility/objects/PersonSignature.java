package blink.utility.objects;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for representing a Person interacting with this system
 */
public class PersonSignature {

    private String uuid;
    private String username;
    private transient String passwordHash;
    private transient String salt;
    private Blob signature;
    private String signatureFont;
    private String fName;
    private String lName;
    private String email;
    private String title;
    private List<Company> companies;
    private int accessLevelID;

    //Fully parameterised constructor
    public PersonSignature(String uuid, String username, String passwordHash, String salt, Blob signature, String signatureFont, String fName, String lName, String email, String title, int accessLevelID) {
        this.uuid = uuid;
        this.username = username;
        this.passwordHash = passwordHash;
        this.salt = salt;
        this.signature = signature;
        this.signatureFont = signatureFont;
        this.fName = fName;
        this.lName = lName;
        this.email = email;
        this.title = title;
        this.accessLevelID = accessLevelID;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public Blob getSignature() {
        return signature;
    }

    public void setSignature(Blob signature) {
        this.signature = signature;
    }

    public String getSignatureFont() {
        return signatureFont;
    }

    public void setSignatureFont(String signatureFont) {
        this.signatureFont = signatureFont;
    }

    public String getfName() {
        return fName;
    }

    public void setfName(String fName) {
        this.fName = fName;
    }

    public String getlName() {
        return lName;
    }

    public void setlName(String lName) {
        this.lName = lName;
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

    public List<Company> getCompanies() {
        return companies;
    }

    public void setCompanies(ArrayList<Company> companies) {
        this.companies = companies;
    }

    public int getAccessLevelID() {
        return accessLevelID;
    }

    public void setAccessLevelID(int accessLevelID) {
        this.accessLevelID = accessLevelID;
    }
}