package b404.utility.objects;

import java.sql.Blob;

public class File {
    private int fileID;
    private String name;
    private Blob file;
    private int stepID;

    public File(int fileID, String name, Blob file, int stepID) {
        this.fileID = fileID;
        this.name = name;
        this.file = file;
        this.stepID = stepID;
    }

    public File(String name) { this.name = name; }

    public int getFileID() { return fileID; }

    public void setFileID(int fileID) { this.fileID = fileID; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public Blob getFile() { return file; }

    public void setFile(Blob file) { this.file = file; }

    public int getStepID() { return stepID; }

    public void setStepID(int stepID) { this.stepID = stepID; }
}
