package blink.utility.objects;

import com.google.gson.annotations.SerializedName;

import javax.ws.rs.BadRequestException;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.Base64;

public class File {
    private int fileID;
    private String name;
    @SerializedName("file")
    private String decodedString;
    private transient String encodedString;
    private boolean confidential;

    public File(int fileID, String name, Blob blob, boolean confidential) throws SQLException {
        this.fileID = fileID;
        this.name = name;
        this.encodedString = blobToEncodedString(blob);
        this.decodedString = decodeBase64(encodedString);
        this.confidential = confidential;
    }

    public File(int fileID, String name, String decodedString, boolean confidential) {
        this.fileID = fileID;
        this.name = name;
        this.decodedString = decodedString;
        this.encodedString = File.encodeBase64(decodedString);
        this.confidential = confidential;
    }

    public File(String name, String decodedString, boolean confidential) {
        this.name = name;
        this.decodedString = decodedString;
        this.encodedString = File.encodeBase64(decodedString);
        this.confidential = confidential;
    }

    public File(String name) { this.name = name; }

    public int getFileID() { return fileID; }

    public void setFileID(int fileID) { this.fileID = fileID; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getDecodedString() { return decodedString; }

    public void setDecodedString(String decodedString) { this.decodedString = decodedString; }

    public String getEncodedString() { return encodedString; }

    public void setEncodedString(String encodedString) { this.encodedString = encodedString; }

    public boolean getConfidential() { return confidential; }

    public void setConfidential(boolean confidential) { this.confidential = confidential; }

    /**
     * Converts byte[] File into Base64 string
     * @param decodedString byte array to convert to base64
     * @return base64 string
     */
    public static String encodeBase64(String decodedString) {
        try{
            return decodedString == null || decodedString.length() == 0  ? null : Base64.getEncoder().encodeToString(decodedString.getBytes());
        }
        catch(Exception e){
            throw new BadRequestException("File is invalid format.");
        }
    }

    /**
     * Converts base 64 String to byte array
     * @param encodedString base64 string representation of a file
     * @return base64 string
     */
    public static String decodeBase64(String encodedString) {
        try {
            return encodedString == null || encodedString.isEmpty() ? null : new String(Base64.getDecoder().decode(encodedString.getBytes()));
        }
        catch(Exception e){
            throw new BadRequestException("Base 64 file in incorrect format");
        }
    }

    /**
     * Converts a blob into an encoded base64 String
     * @param blob
     * @return
     * @throws SQLException
     */
    public static String blobToEncodedString(Blob blob) throws  SQLException {
        if(blob == null){
            return "";
        }
        String encodedString = new String(blob.getBytes(1, (int) blob.length()));
        return encodedString;
    }
}