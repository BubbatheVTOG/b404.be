package blink.businesslayer;

import blink.datalayer.FileDB;
import blink.utility.objects.File;
import com.google.gson.JsonObject;
import javax.ws.rs.InternalServerErrorException;
import java.sql.SQLException;
import java.util.List;

public class FileBusiness {
    private FileDB fileDB;
    private MilestoneBusiness milestoneBusiness;

    public FileBusiness() {
        this.fileDB = new FileDB();
        this.milestoneBusiness = new MilestoneBusiness();
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
            milestoneBusiness.getMilestoneByID(milestoneID);

            return fileDB.getAllFilesByMilestone(Integer.parseInt(milestoneID));
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
            return fileDB.insertFile(file);
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
            return fileDB.updateFile(file, Integer.parseInt(fileID));
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
     * Converts json file object into file object
     * @param jsonObject to convert into file object
     * @return file object
     */
    public File jsonObjectToFileObject(JsonObject jsonObject) {
        byte[] byteFile = jsonObject.get("file").getAsString().getBytes();
        return new File(jsonObject.get("name").getAsString(), byteFile, jsonObject.get("confidential").getAsBoolean());
    }
}