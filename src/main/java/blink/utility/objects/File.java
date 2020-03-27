package blink.utility.objects;

import javax.sql.rowset.serial.SerialBlob;
import javax.ws.rs.InternalServerErrorException;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.Base64;

public class File {
    private int fileID;
    private String name;
    private transient byte[] byteFile;
    private transient Blob blobFile;
    private String base64File;
    private boolean confidential;

    public File(int fileID, String name, byte[] byteFile, boolean confidential) {
        this.fileID = fileID;
        this.name = name;
        this.byteFile = byteFile;
        this.blobFile = this.byteFile.length == 0 ? null : this.convertFileToBlob(byteFile);
        this.base64File = this.byteFile.length == 0 ? null : this.convertFileToBase64(byteFile);
        this.confidential = confidential;
    }

    public File(String name, byte[] byteFile, boolean confidential) {
        this.name = name;
        this.byteFile = byteFile;
        this.base64File = this.byteFile.length == 0 ? null : this.convertFileToBase64(byteFile);
        this.confidential = confidential;
    }

    public File(String name) { this.name = name; }

    public int getFileID() { return fileID; }

    public void setFileID(int fileID) { this.fileID = fileID; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public Blob getBlobFile() { return blobFile; }

    public void setBlobFile(Blob blob) { this.blobFile = blobFile; }

    public String getBase64File() { return base64File; }

    public void setBase64File(String base64File) { this.base64File = base64File; }

    public byte[] getByteFile() { return byteFile; }

    public void setByteFile(byte[] byteFile) { this.byteFile = byteFile; }

    public boolean getConfidential() { return confidential; }

    public void setConfidential(boolean confidential) { this.confidential = confidential; }

    /**
     * Converts byte[] File to blob
     * @param byteFile
     * @return blob
     * @throws SQLException
     */
    public Blob convertFileToBlob(byte[] byteFile) {
        try {
            return new SerialBlob(byteFile);
        } catch(SQLException sqle) {
            throw new InternalServerErrorException(sqle.getMessage());
        }
    }

    /**
     * Converts byte[] File into Base64 string
     * @param byteFile
     * @return base64 string
     * @throws SQLException
     */
    public String convertFileToBase64(byte[] byteFile) {
        return Base64.getEncoder().encodeToString(byteFile);
    }
}