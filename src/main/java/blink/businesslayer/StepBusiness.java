package blink.businesslayer;

import blink.datalayer.FileDB;
import blink.datalayer.StepDB;
import blink.utility.objects.File;
import blink.utility.objects.Step;
import com.google.gson.*;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotFoundException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

/**
 * Business layer service for step related logic
 * Enforces business rules and leverages datalayer to manipulate database
 */
public class StepBusiness {

    private static final String STEPID_ERROR = "stepID must be a valid integer.";
    private static final String WORKFLOWID_ERROR = "workflowID must be a valid integer.";

    private StepDB stepDB;
    private FileDB fileDB;
    private PersonBusiness personBusiness;
    private VerbBusiness verbBusiness;

    public StepBusiness(){
        this.stepDB = new StepDB();
        this.fileDB = new FileDB();
        this.personBusiness = new PersonBusiness();
        this.verbBusiness = new VerbBusiness();
    }

    /**
     * Gets higher level steps from the database
     * @param stepID ID of step to retrieve from database
     * @return Step object containing data from the database
     * @throws NotFoundException stepID not present in the database
     * @throws BadRequestException stepID was an invalid integer
     * @throws InternalServerErrorException Error connecting to database or executing query
     */
    public Step getStep(String stepID) {
        try {
            Step step = stepDB.getStep(Integer.parseInt(stepID));

            if(step == null){
                throw new NotFoundException("No step with that stepID exists");
            }
            return step;
        } catch(NumberFormatException nfe) {
            throw new BadRequestException(STEPID_ERROR);
        } catch(SQLException sqle) {
            throw new InternalServerErrorException(sqle.getMessage());
        }
    }

    /**
     * Gets higher level steps from the database
     * @param workflowID WorkflowID of steps to retrieve from database
     * @return Step objects containing data from the database
     * @throws InternalServerErrorException Error connecting to database or executing query
     */
    public List<Step> getSteps(String workflowID) {
        try {
            List<Step> steps = stepDB.getHigherLevelSteps(Integer.parseInt(workflowID));
            for (Step step : steps) {
                step.setChildren(this.getRelatedSteps(step));
            }
            return steps;
        } catch(NumberFormatException nfe) {
            throw new BadRequestException(WORKFLOWID_ERROR);
        } catch(SQLException sqle) {
            throw new InternalServerErrorException(sqle.getMessage());
        }
    }

    /**
     * Get related steps from higher level step
     * @param step Higher level step to retrieve stepID from
     * @return Array of steps containing related steps
     * @throws InternalServerErrorException Error connecting to database or executing query
     */
    public List<Step> getRelatedSteps(Step step) {
        List<Step> relatedSteps;
        try {
            relatedSteps = stepDB.getRelatedSteps(step.getStepID());
        } catch(SQLException sqle) {
            throw new InternalServerErrorException("Problem returning related steps.");
        }
        if (relatedSteps != null) {
            step.setChildren(relatedSteps);
            for(Step relatedStep : step.getChildren()){
                relatedStep.setChildren(this.getRelatedSteps(relatedStep));
            }
        }
        return relatedSteps;
    }

    /**
     * Insert a list of steps into the database
     * @param steps JsonArray of steps to insert into the database
     * @return Success Message
     */
    public int insertSteps(JsonArray steps, int workflowID, Connection conn) {
        int numInsertedSteps;
        try {
            List<Step> stepList = this.jsonToStepList(steps, workflowID);

            this.validateSteps(stepList);

            numInsertedSteps = this.stepDB.insertSteps(stepList, conn);
        } catch(SQLException ex) {
            throw new InternalServerErrorException(ex.getMessage());
        }
        return numInsertedSteps;
    }

    /**
     * Deletes existing steps by workflowID and adds updated step list
     * @param stepList Updated list of step objects
     * @return Success Message
     */
    public int updateSteps(List<Step> stepList, Connection conn) {
        int numUpdatedSteps;
        try {
            this.validateSteps(stepList);

            numUpdatedSteps = this.stepDB.updateSteps(stepList, conn);

            if(numUpdatedSteps <= 0) {
                throw new NotFoundException("No records with that workflowID exist.");
            }
        } catch(NumberFormatException ex) {
            throw new BadRequestException(WORKFLOWID_ERROR);
        } catch(SQLException ex) {
            throw new InternalServerErrorException(ex.getMessage());
        }

        return numUpdatedSteps;
    }

    /**
     * Delete steps by workflowID
     * @param workflowID WorkflowID to delete steps by
     * @return Success Message
     */
    public int deleteStepsByWorkflowID(String workflowID, Connection conn) {
        int numDeletedSteps = 0;
        try {
            this.stepDB.deleteStepsByWorkflowID(Integer.parseInt(workflowID), conn);
        } catch (NumberFormatException ex) {
            throw new BadRequestException(WORKFLOWID_ERROR);
        } catch (SQLException ex) {
            throw new InternalServerErrorException(ex.getMessage());
        }
        return numDeletedSteps;
    }

    /**
     * Convert jsonToStepList so backend can use it
     * @param steps json array of all steps
     * @return ArrayList<Step>
     */
    public List<Step> jsonToStepList(JsonArray steps, int workflowID) {
        try {
            Gson gson = new GsonBuilder().serializeNulls().create();
            List<Step> stepList = Arrays.asList(gson.fromJson(steps, Step[].class));

            return insertWorkflowID(stepList, workflowID);
        }
        catch(Exception e){
            throw new BadRequestException("Step json invalidly formatted");
        }
    }

    /**
     * Add workflowID into StepList generated from json the front end sends to the backend
     * @param steps stepList retrieved from the conversion
     * @param workflowID to insert into the steps
     * @return list of steps
     */
    public List<Step> insertWorkflowID(List<Step> steps, int workflowID) {
        for(Step step: steps) {
            step.setWorkflowID(workflowID);
            if(step.hasChildren()) {
                insertWorkflowID(step.getChildren(), workflowID);
            }
        }
        return steps;
    }

    private void validateSteps(List<Step> steps) throws SQLException{
        try {
            for (Step step : steps) {

                //Validate template file exists and copy template file to link to step
                if (step.getFileID() != 0) {
                    //TODO add this check once file has workflowID added
                    //File oldFile = this.fileDB.getFileByID(step.getFileID());
                    //if(oldFile.getWorkflow() != 0) {
                    File newFile = this.fileDB.getFileByID(step.getFileID());
                    int newFileID = this.fileDB.insertFile(newFile);
                    step.setFileID(newFileID);
                    //}
                }

                //validate that person exists
                if (step.getUUID() != null) {
                    this.personBusiness.getPersonByUUID(step.getUUID());
                }

                if (step.getVerbID() != 0) {
                    this.verbBusiness.getVerb(step.getVerbID());
                }

                if (step.hasChildren()) {
                    this.validateSteps(step.getChildren());
                }
            }
        }
        catch(SQLException sqle){
            throw new InternalServerErrorException(sqle.getMessage());
        }
    }
}
