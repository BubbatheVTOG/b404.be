package blink.businesslayer;

import blink.datalayer.FileDB;
import blink.utility.objects.*;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import eu.medsea.mimeutil.MimeUtil;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.NotFoundException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Parameter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class FileBusiness {
    private FileDB fileDB;
    private PersonBusiness personBusiness;
    private CompanyBusiness companyBusiness;
    private MilestoneBusiness milestoneBusiness;
    private StepBusiness stepBusiness;


    public FileBusiness() {
        this.fileDB = new FileDB();
        this.personBusiness = new PersonBusiness();
        this.companyBusiness = new CompanyBusiness();
        this.milestoneBusiness = new MilestoneBusiness();
        this.stepBusiness = new StepBusiness();
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
     * Get all files relevant to a user
     * @param uuid id of requester
     * @return list of files
     */
    public List<File> getAllConcreteFiles(String uuid) {
        try {
            Person requester = this.personBusiness.getPersonByUUID(uuid);

            //Check that user has access to this milestone
            if(!Authorization.INTERNAL_USER_LEVELS.contains(requester.getAccessLevelID())){
                return this.fileDB.getAllConcreteFiles(uuid);
            }
            else{
                return this.fileDB.getAllConcreteFiles();
            }

        } catch(SQLException sqle) {
            throw new InternalServerErrorException(sqle.getMessage());
        }
    }

    /**
     * Get all template files
     * @param uuid of the requester
     * @return List<File>
     */
    public List<File> getAllTemplateFiles(String uuid) {
        try {
            Person requester = this.personBusiness.getPersonByUUID(uuid);

            //Check that user has access to this milestone
            if(!Authorization.INTERNAL_USER_LEVELS.contains(requester.getAccessLevelID())){
                throw new NotAuthorizedException("You do not have access to these files.");
            }

            return fileDB.getAllTemplateFiles();

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
            Milestone milestone = milestoneBusiness.getMilestoneByID(milestoneID);

                //Check that user has access to this milestone
            if(!Authorization.INTERNAL_USER_LEVELS.contains(requester.getAccessLevelID())){
                List<Integer> companyIDList = requester.getCompanies().stream().map(Company::getCompanyID).collect(Collectors.toList());
                if(!companyIDList.contains(milestone.getCompany().getCompanyID())){
                    throw new NotAuthorizedException("You do not have access to this file.");
                }
            }

            return fileDB.getAllFilesByMilestone(Integer.parseInt(milestoneID));

        } catch(SQLException sqle) {
            throw new InternalServerErrorException(sqle.getMessage());
        }
    }

    /**
     * Get all files by companyID
     * @param companyID to retrieve files by
     * @param uuid id of requester
     * @return list of files
     */
    public List<File> getAllFilesByCompany(String companyID, String uuid) {
        try {
            Person requester = this.personBusiness.getPersonByUUID(uuid);
            Company company = this.companyBusiness.getCompanyByID(companyID);


            //Check that user has access to this company
            if(!Authorization.INTERNAL_USER_LEVELS.contains(requester.getAccessLevelID())){
                List<Integer> companyIDList = requester.getCompanies().stream().map(Company::getCompanyID).collect(Collectors.toList());
                if(!companyIDList.contains(company.getCompanyID())){
                    throw new NotAuthorizedException("You do not have access to files from these companies.");
                }
            }

            return fileDB.getAllFilesByCompany(Integer.parseInt(companyID));

        } catch(SQLException sqle) {
            throw new InternalServerErrorException(sqle.getMessage());
        }
    }


    /**
     * Insert a new file into the database
     * @param jsonObject containing all file elements
     * @return generated fileID
     */
    public File insertFile(JsonObject jsonObject) {
        try {
            File file = jsonObjectToFileObject(jsonObject);

            Step step = null;

            if(jsonObject.has("stepID")) {
                String stepID = jsonObject.get("stepID").getAsString();
                if (!(stepID == null || stepID.isEmpty())) {
                    step = stepBusiness.getStep(stepID);
                }
            }

            int fileID = fileDB.insertFile(file);

            if(step != null) {
                int oldFileID = step.getFileID();
                step.setFileID(fileID);
                stepBusiness.updateStep(step);
                if(oldFileID != 0) {
                    fileDB.deleteFileByFileID(oldFileID);
                }
            }

            return this.getFile(Integer.toString(fileID));
        } catch(SQLException sqle) {
            throw new InternalServerErrorException(sqle.getMessage());
        }
    }

    /**
     * Update an existing file
     * @param jsonObject contains all file elements except stepID
     * @param uuid id of requester
     * @return fileID of the updated file
     */
    public File updateFile(JsonObject jsonObject, String uuid) {
        try {
            if(uuid == null){
                throw new BadRequestException("uuid is null");
            }
            if(!jsonObject.has("fileID")){
                throw new BadRequestException("A file ID must be provided.");
            }

            File file = jsonObjectToFileObject(jsonObject);

            //Check that file exists and user has access to file
            this.getFile(Integer.toString(file.getFileID()), uuid);

            fileDB.updateFile(file);

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
            if(!jsonObject.has("name")){
                throw new BadRequestException("File name must be provided");
            }

            boolean confidential = false;
            if(jsonObject.has("confidential")){
                confidential = jsonObject.get("confidential").getAsBoolean();
            }

            if(!jsonObject.has("file")){
                throw new BadRequestException("File data must be provided");
            }

            String base64String = jsonObject.get("file").getAsString();
            if(base64String == null || base64String.isEmpty()){
                throw new BadRequestException("A file must be provided.");
            }

            Boolean form = false;
            if(jsonObject.has("form")){
                form = jsonObject.get("form").getAsBoolean();
            }

            if(jsonObject.has("fileID")){
                return new File(jsonObject.get("fileID").getAsInt(), jsonObject.get("name").getAsString(), base64String, confidential, form);
            }
            else {
                return new File(jsonObject.get("name").getAsString(), base64String, confidential, form);
            }
        }
        catch(NumberFormatException nfe){
            throw new BadRequestException("fileID must be a valid Integer");
        }
        catch(Exception e) {
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
    }

    public File zipAllFilesByMilestone(String milestoneID, String uuid) {
        //Retrieves all files to be zipped
        List<File> files = this.getAllFilesByMilestone(milestoneID, uuid);

        //Processes each file and adds it to the archive
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {

            ZipOutputStream zipOutputStream = new ZipOutputStream(byteArrayOutputStream);

            for(File file: files) {
                //Removes the MimeType from the file for archiving
                String split = file.getDecodedString().split("base64,")[1];

                //Converts the file into bytes
                byte[] bytes = Base64.getDecoder().decode(split.getBytes());

                //Creates a new entry for the file we are archiving
                zipOutputStream.putNextEntry(new ZipEntry(file.getName()));
                zipOutputStream.write(bytes);
                zipOutputStream.closeEntry();
            }
            //Closes the zip stream to finalize the archive
            zipOutputStream.close();

            //Converts the archive into a base64 format for the front end, appending the MimeType
            String base64 = new String(Base64.getEncoder().encode(byteArrayOutputStream.toByteArray()));
            base64 = new String("data:" + MimeUtil.getMimeTypes(base64) + ";base64," + base64);

            return new File("Milestone_" + milestoneID + "_Archive.zip", base64, true, false);
        } catch(IOException ioException) {
            throw new InternalServerErrorException(ioException.getMessage());
        }
    }

}