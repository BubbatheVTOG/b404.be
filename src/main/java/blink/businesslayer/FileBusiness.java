package blink.businesslayer;

import blink.datalayer.FileDB;
import blink.utility.objects.Company;
import blink.utility.objects.File;
import blink.utility.objects.Person;
import com.google.gson.JsonObject;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.NotFoundException;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class FileBusiness {
    private FileDB fileDB;
    private PersonBusiness personBusiness;
    private MilestoneBusiness milestoneBusiness;

    public FileBusiness() {
        this.fileDB = new FileDB();
        this.milestoneBusiness = new MilestoneBusiness();
    }

    /**
     * Get file by fileID
     * @param fileID to retrieve from the database
     * @param uuid id of requester
     * @return a file object
     */
    public File getFile(String fileID, String uuid) {
        try {
            int fileIDInteger = Integer.parseInt(fileID);

            File file = fileDB.getFileByID(fileIDInteger);
            if(file == null){
                throw new NotFoundException("No file with that ID exists");
            }

            this.checkRequesterAccess(fileIDInteger, uuid);

            return file;
        }
        //Error converting milestone to integer
        catch(NumberFormatException nfe){
            throw new BadRequestException("File ID must be a valid integer");
        }
        catch(SQLException sqle) {
            throw new InternalServerErrorException(sqle.getMessage());
        }
    }

    /**
     * Get file by fileID
     * @param fileID to retrieve from the database
     * @return a file object
     */
    private File getFile(String fileID) {
        try {
            File file = fileDB.getFileByID(Integer.parseInt(fileID));
            if(file == null){
                throw new NotFoundException("No file with that ID exists");
            }

            return file;
        } catch(SQLException sqle) {
            throw new InternalServerErrorException(sqle.getMessage());
        }
    }

    /**
     * Get all files by milestoneID
     * @param milestoneID to retrieve files by
     * @param uuid id of requester
     * @return list of files
     */
    public List<File> getAllFilesByMilestone(String milestoneID, String uuid) {
        try {
            Person requester = this.personBusiness.getPersonByUUID(uuid);

            //Check that user has access to this milestone
            if(!Authorization.INTERNAL_USER_LEVELS.contains(requester.getAccessLevelID())){
                List<Integer> companyIDList = requester.getCompanies().stream().map(Company::getCompanyID).collect(Collectors.toList());
                if(!companyIDList.contains(milestoneBusiness.getMilestoneByID(milestoneID).getMileStoneID())){
                    throw new NotAuthorizedException("You do not have access to this file.");
                }
            }

            return fileDB.getAllFilesByMilestone(Integer.parseInt(milestoneID));

        } catch(SQLException sqle) {
            throw new InternalServerErrorException(sqle.getMessage());
        }
    }

    /**
     * Insert a new file into the database
     * @param jsonObject containing all file elements
     * @param uuid id of requester
     * @return generated fileID
     */
    public File insertFile(JsonObject jsonObject, String uuid) {
        try {
            File file = jsonObjectToFileObject(jsonObject);

            //Check that file exists and user has access to file
            this.getFile(Integer.toString(file.getFileID()), uuid);

            fileDB.insertFile(file);

            return this.getFile(Integer.toString(file.getFileID()));
        } catch(SQLException sqle) {
            throw new InternalServerErrorException(sqle.getMessage());
        }
    }

    /**
     * Update an existing file
     * @param jsonObject contains all file elements except stepID
     * @param fileID of the file to update
     * @param uuid id of requester
     * @return fileID of the updated file
     */
    public File updateFile(JsonObject jsonObject, String fileID, String uuid) {
        try {
            File file = jsonObjectToFileObject(jsonObject);

            //Check that file exists and user has access to file
            this.getFile(Integer.toString(file.getFileID()), uuid);

            fileDB.updateFile(file, Integer.parseInt(fileID));

            return this.getFile(Integer.toString(file.getFileID()));
        } catch(SQLException sqle) {
            throw new InternalServerErrorException(sqle.getMessage());
        }
    }

    /**
     * Deletes a file by fileID
     * @param fileID to delete from database
     * @return successfully or unsuccessfully deletion string
     */
    public int deleteFile(String fileID, String uuid) {
        try {

            //Check that file exists and user has access to file
            this.getFile(fileID, uuid);

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
    private File jsonObjectToFileObject(JsonObject jsonObject) {
        try {
            byte[] byteFile = jsonObject.get("file").getAsString().getBytes();
            return new File(jsonObject.get("name").getAsString(), byteFile, jsonObject.get("confidential").getAsBoolean());
        }
        catch(Exception e){
            throw new BadRequestException("File json in incorrect format.");
        }
    }

    /**
     * Checks that a user has access to a file
     * @param fileID fileID to be modified
     * @param uuid id of requester
     */
    private void checkRequesterAccess(int fileID, String uuid) throws SQLException{
        Person requester = this.personBusiness.getPersonByUUID(uuid);
        try {

            //If internal user, they always have access
            if (!Authorization.INTERNAL_USER_LEVELS.contains(requester.getAccessLevelID())) {

                int companyID = this.fileDB.getFileCompanyID(fileID);
                List<Integer> companyIDList = requester.getCompanies().stream().map(Company::getCompanyID).collect(Collectors.toList());

                //If user is not part of the company which the file belongs to they do not have access
                //If companyID was 0 then file is a template and can be downloaded for filling out after completion
                if (companyID == 0 || !companyIDList.contains(companyID)) {
                    throw new NotAuthorizedException("You do not have access to this file.");
                }
            }
        }catch(Exception e){
            throw new InternalServerErrorException(Integer.toString(requester.getAccessLevelID()));
        }
    }
}