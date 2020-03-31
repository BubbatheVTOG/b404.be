package blink.utility.objects;

import com.google.gson.annotations.SerializedName;

import javax.sql.rowset.serial.SerialBlob;
import javax.ws.rs.InternalServerErrorException;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.Base64;

public class File {
    private int fileID;
    private String name;
    private transient byte[] byteFile;
    @SerializedName("file")
    private String base64File;
    private boolean confidential;
    private String mimeType;

    public File(int fileID, String name, Blob blobFile, boolean confidential) throws SQLException {
        this.fileID = fileID;
        this.name = name;
        this.byteFile = blobFile.getBytes(1, (int) blobFile.length());
        this.base64File = File.encodeBase64(this.byteFile);
        this.confidential = confidential;
    }

    public File(String name, String base64String, boolean confidential) {
        this.name = name;
        this.byteFile = File.decodeBase64(base64String);
        this.base64File = File.encodeBase64(byteFile);
        this.confidential = confidential;
    }

    public File(int fileID, String name, String base64String, boolean confidential) {
        this.fileID = fileID;
        this.name = name;
        this.byteFile = File.decodeBase64(base64String);
        this.base64File = File.encodeBase64(byteFile);
        this.confidential = confidential;
    }

    public File(String name) { this.name = name; }

    public int getFileID() { return fileID; }

    public void setFileID(int fileID) { this.fileID = fileID; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public Blob getBlobFile() {
        return this.byteFile.length == 0 ? null : this.convertFileToBlob(byteFile);
    }

    public String getBase64File() { return base64File; }

    public void setBase64File(String base64File) { this.base64File = base64File; }

    public byte[] getByteFile() { return byteFile; }

    public void setByteFile(byte[] byteFile) { this.byteFile = byteFile; }

    public boolean getConfidential() { return confidential; }

    public void setConfidential(boolean confidential) { this.confidential = confidential; }

    /**
     * Converts byte[] File to blob
     * @param byteFile the byte array to convert to a blob
     * @return blob
     */
    private Blob convertFileToBlob(byte[] byteFile) {
        Blob blob = null;
        try {
            blob = new SerialBlob(byteFile);
            return blob;
        } catch(SQLException sqle) {
            throw new InternalServerErrorException(sqle.getMessage());
        }
    }
    /**
     * Converts base 64 String to byte array
     * @param base64String base64 string representation of a file
     * @return base64 string
     */
    public static byte[] decodeBase64(String base64String) {
        return Base64.getMimeDecoder().decode(base64String);
    }

    /**
     * Converts byte[] File into Base64 string
     * @param byteFile byte array to convert to base64
     * @return base64 string
     */
    public static String encodeBase64(byte[] byteFile) {
        try{
            return byteFile.length == 0 ? null : Base64.getUrlEncoder().encodeToString(byteFile);
        }
        catch(Exception e){
            throw new InternalServerErrorException(e.getMessage());
        }
    }
}