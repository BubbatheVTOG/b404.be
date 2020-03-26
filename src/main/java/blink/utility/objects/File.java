package blink.utility.objects;

import javax.ws.rs.InternalServerErrorException;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.Base64;

public class File {
    private int fileID;
    private String name;
    private Blob blobFile;
    private String base64File;
    private boolean confidential;
    private int stepID;

    public File(int fileID, String name, Blob blobFile, boolean confidential, int stepID) {
        this.fileID = fileID;
        this.name = name;
        this.blobFile = blobFile;
        this.confidential = confidential;
        this.stepID = stepID;
    }

    public File(int fileID, String name, String base64File, boolean confidential, int stepID) {
        this.fileID = fileID;
        this.name = name;
        this.base64File = base64File;
        this.confidential = confidential;
        this.stepID = stepID;
    }

    public File(int fileID, String name, Blob blobFile, boolean confidential) {
        this.fileID = fileID;
        this.name = name;
        this.blobFile = blobFile;
        this.confidential = confidential;
    }

    public File(String name, Blob blobFile, boolean confidential, int stepID) {
        this.name = name;
        this.blobFile = blobFile;
        this.confidential = confidential;
        this.stepID = stepID;
    }

    public File(String name, Blob blobFile, boolean confidential) {
        this.name = name;
        this.blobFile = blobFile;
        this.confidential = confidential;
    }


    public File(String name) { this.name = name; }

    public int getFileID() { return fileID; }

    public void setFileID(int fileID) { this.fileID = fileID; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public Blob getBlobFile() { return blobFile; }

    public void setBlobFile(Blob blobFile) { this.blobFile = blobFile; }

    public String getBase64File() { return base64File; }

    public void setBase64File(String base64File) { this.base64File = base64File; }

    public boolean getConfidential() { return confidential; }

    public void setConfidential(boolean confidential) { this.confidential = confidential; }

    public int getStepID() { return stepID; }

    public void setStepID(int stepID) { this.stepID = stepID; }

    /**
     * Converts blob into Base64 string
     * @param blobFile
     * @return base64 string
     * @throws SQLException
     */
    public String convertFileToBase64(Blob blobFile) throws SQLException {
        try {
            byte [] blobAsByteArray = blobFile.getBytes(1l, (int)blobFile.length());
            return Base64.getEncoder().encodeToString(blobAsByteArray);
        } catch(SQLException sqle) {
            throw new InternalServerErrorException(sqle.getMessage());
        } catch(NullPointerException npe) {
            throw new InternalServerErrorException(blobFile.toString());
        }
    }
}