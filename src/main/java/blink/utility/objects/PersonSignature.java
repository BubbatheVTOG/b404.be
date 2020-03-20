package blink.utility.objects;
import javax.sql.rowset.serial.SerialBlob;
import javax.ws.rs.InternalServerErrorException;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.Base64;

/**
 * Utility class for representing a Person interacting with this system
 */
public class PersonSignature extends Person{
    private Blob signature;
    private String signatureFont;

    //Fully parameterised constructor
    public PersonSignature(String uuid, String username, String passwordHash, String salt, String signature, String signatureFont, String fName, String lName, String email, String title, int accessLevelID) {
        super(uuid, username, passwordHash, salt, fName, lName, email, title, accessLevelID);
        this.setSignature(signature);
        this.signatureFont = signatureFont;
    }

    public Blob getSignature() {
        return signature;
    }

    public void setSignature(String fileString) {
        byte [] blobAsByteArray = Base64.getMimeDecoder().decode(fileString);
        try {
            this.signature = new SerialBlob(blobAsByteArray);
        } catch(SQLException sqle) {
            throw new InternalServerErrorException(sqle.getMessage());
        }
    }

    public String getSignatureFont() {
        return signatureFont;
    }
}