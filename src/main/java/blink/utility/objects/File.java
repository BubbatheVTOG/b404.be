package blink.utility.objects;

import java.sql.Blob;

public class File {
    private int fileID;
    private String name;
    private Blob file;
    private boolean confidential;
    private int stepID;

    public File(int fileID, String name, Blob file, boolean confidential, int stepID) {
        this.fileID = fileID;
        this.name = name;
        this.file = file;
        this.confidential = confidential;
        this.stepID = stepID;
    }

    public File(int fileID, String name, Blob file, boolean confidential) {
        this.fileID = fileID;
        this.name = name;
        this.file = file;
        this.confidential = confidential;
    }

    public File(String name, Blob file, boolean confidential, int stepID) {
        this.name = name;
        this.file = file;
        this.confidential = confidential;
        this.stepID = stepID;
    }

    public File(String name, Blob file, boolean confidential) {
        this.name = name;
        this.file = file;
        this.confidential = confidential;
    }


    public File(String name) { this.name = name; }

    public int getFileID() { return fileID; }

    public void setFileID(int fileID) { this.fileID = fileID; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public Blob getFile() { return file; }

    public void setFile(Blob file) { this.file = file; }

    public boolean getConfidential() { return confidential; }

    public void setConfidential(boolean confidential) { this.confidential = confidential; }

    public int getStepID() { return stepID; }

    public void setStepID(int stepID) { this.stepID = stepID; }
}