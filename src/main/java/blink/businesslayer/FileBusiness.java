package blink.businesslayer;

import blink.datalayer.FileDB;
import blink.utility.objects.File;
import com.fasterxml.jackson.databind.ser.Serializers;
import com.google.gson.JsonObject;

import javax.sql.rowset.serial.SerialBlob;
import javax.ws.rs.InternalServerErrorException;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.Base64;
import java.util.List;

public class FileBusiness {
    private FileDB fileDB;

    public FileBusiness() {
        this.fileDB = new FileDB();
    }

    /**
     * Get file by fileID
     * @param fileID to retrieve from the database
     * @return a file object
     */
    public File getFile(String fileID) {
        try {
            return fileDB.getFileByID(Integer.parseInt(fileID));
        } catch(SQLException sqle) {
            throw new InternalServerErrorException(sqle.getMessage());
        }
    }

    /**
     * Get all files by milestoneID
     * @param milestoneID to retrieve files by
     * @return list of files
     */
    public List<File> getAllFilesByMilestone(String milestoneID) {
        try {
            return fileDB.getAllFilesByMilestone(Integer.parseInt(milestoneID));
        } catch(SQLException sqle) {
            throw new InternalServerErrorException(sqle.getMessage());
        }
    }

    /**
     * Return all files from the database
     * @return List<File> containing all files in the database
     */
    public List<File> getAllFiles() {
        try {
            return fileDB.getAllFiles();
        } catch(SQLException sqle) {
            throw new InternalServerErrorException(sqle.getMessage());
        }
    }

    /**
     * Insert a new file into the database
     * @param jsonObject containing all file elements
     * @return generated fileID
     */
    public int insertFile(JsonObject jsonObject) {
        try {
            File file = jsonObjectToFileObject(jsonObject);
            return fileDB.insertFile(file.getName(), file.getBlobFile(), file.getConfidential());
        } catch(SQLException sqle) {
            throw new InternalServerErrorException(sqle.getMessage());
        }
    }

    /**
     * Insert a new file with a stepID
     * @param jsonObject that contains name, file and confidential
     * @param stepID of the file
     * @return generated fileID
     */
    public int insertFile(JsonObject jsonObject, String stepID) {
        try {
            File file = jsonObjectToFileObject(jsonObject, stepID);
            return fileDB.insertFile(file.getName(), file.getBlobFile(), file.getConfidential(), file.getStepID());
        } catch(SQLException sqle) {
            throw new InternalServerErrorException(sqle.getMessage());
        }
    }

    /**
     * Update an existing file
     * @param jsonObject contains all file elements except stepID
     * @param fileID of the file to update
     * @return fileID of the updated file
     */
    public int updateFile(JsonObject jsonObject, String fileID) {
        try {
            File file = jsonObjectToFileObject(jsonObject);
            return fileDB.updateFile(file.getName(), file.getBlobFile(), file.getConfidential(), Integer.parseInt(fileID));
        } catch(SQLException sqle) {
            throw new InternalServerErrorException(sqle.getMessage());
        }
    }

    /**
     * Updates a file using a json object, optional stepID and fileID
     * @param jsonObject containing name, file and confidential
     * @param stepID to insert into file
     * @param fileID to update
     * @return fileID of the updated file
     */
    public int updateFile(JsonObject jsonObject, String stepID, String fileID) {
        try {
            File file = jsonObjectToFileObject(jsonObject, stepID);
            return fileDB.updateFile(file.getName(), file.getBlobFile(), file.getConfidential(), file.getStepID(), Integer.parseInt(fileID));
        } catch(SQLException sqle) {
            throw new InternalServerErrorException(sqle.getMessage());
        }
    }

    /**
     * Deletes a file by fileID
     * @param fileID to delete from database
     * @return successfully or unsuccessfully deletion string
     */
    public int deleteFile(String fileID) {
        try {
            return fileDB.deleteFileByFileID(Integer.parseInt(fileID));
        } catch(SQLException sqle) {
            throw new InternalServerErrorException(sqle.getMessage());
        }
    }

    /**
     * Converts json String to blob
     * @param file is the string value of the file as base64
     * @return blob format of base64 file
     */
    public Blob convertFileToBlob(String file) {
        byte [] blobAsByteArray = Base64.getMimeDecoder().decode(file);
        try {
            return new SerialBlob(blobAsByteArray);
        } catch(SQLException sqle) {
            throw new InternalServerErrorException(sqle.getMessage());
        }
    }

    /**
     * Converts json file object into file object
     * @param jsonObject to convert into file object
     * @return file object
     */
    public File jsonObjectToFileObject(JsonObject jsonObject) {
        return new File(jsonObject.get("name").getAsString(), convertFileToBlob(jsonObject.get("file").getAsString()), jsonObject.get("confidential").getAsBoolean());
    }

    /**
     * Converts json file object into file object
     * @param jsonObject to convert into file object
     * @param stepID of the file
     * @return file object
     */
    public File jsonObjectToFileObject(JsonObject jsonObject, String stepID) {
        return new File(jsonObject.get("name").getAsString(), convertFileToBlob(jsonObject.get("file").getAsString()), jsonObject.get("confidential").getAsBoolean(), Integer.parseInt(stepID));
    }
}