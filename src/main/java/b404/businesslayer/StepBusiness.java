package b404.businesslayer;

import b404.datalayer.StepDB;
import b404.utility.ConflictException;
import b404.utility.objects.Step;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotFoundException;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Business layer service for step related logic
 * Enforces business rules and leverages datalayer to manipulate database
 */
public class StepBusiness {
    private StepDB stepDB = new StepDB();

    /**
     * Gets higher level steps from the database
     * @param workflowID - workflowID of steps to retrieve from database
     * @return Step objects containing data from the database
     * @throws InternalServerErrorException - Error connecting to database or executing query
     */
    public ArrayList<Step> getSteps(String workflowID) throws InternalServerErrorException {
        try {
            ArrayList<Step> steps = stepDB.getHigherLevelSteps(Integer.parseInt(workflowID));
            for (Step step : steps) {
                step.setChildSteps(this.getRelatedSteps(step));
            }
            return steps;
        } catch(NumberFormatException nfe) {
            throw new BadRequestException("workflowID must be a valid integer.");
        } catch(SQLException sqle) {
            throw new InternalServerErrorException(sqle.getMessage());
        }
    }

    /**
     * Get related steps from higher level step
     * @param step - higher level step to retrieve stepID from
     * @return array of steps containing related steps
     * @throws InternalServerErrorException - Error connecting to database or executing query
     */
    public ArrayList<Step> getRelatedSteps(Step step) throws InternalServerErrorException {
        ArrayList<Step> relatedSteps;
        try {
            relatedSteps = stepDB.getRelatedSteps(step.getStepID());
        } catch(SQLException sqle) {
            throw new InternalServerErrorException("Problem returning related steps.");
        }
        if (relatedSteps != null) {
            step.setChildSteps(relatedSteps);
            for(Step relatedStep : step.getChildSteps()){
                relatedStep.setChildSteps(this.getRelatedSteps(relatedStep));
            }
        }
        return relatedSteps;
    }

    /**
     * Gets a step from the database by UUID
     * @param stepID - stepID to search the database for
     * @return Step object containing data from the database
     * @throws BadRequestException - UUID was an invalid integer format
     * @throws NotFoundException - No step with provided UUID was found
     * @throws InternalServerErrorException - Error connecting to database or executing query
     */
    public Step getStepByStepID(String stepID) throws BadRequestException, NotFoundException, InternalServerErrorException {
        try {
            Step step = stepDB.getStepByStepID(Integer.parseInt(stepID));

            if(step == null) {
                throw new NotFoundException("No step with that stepID exists.");
            } else {
                return step;
            }
        } catch(NumberFormatException nfe){
            throw new BadRequestException("stepID must be a valid integer.");
        } catch(SQLException sqle){
            throw new InternalServerErrorException(sqle.getMessage());
        }
    }

    /**
     * Inserts a new company into the database
     * @param stepID - stepID of the step
     * @param orderNumber - orderNumber of the higher level step
     * @param description - description of the step
     * @param relatedStep - relatedStep to the current step
     * @param UUID - UUID of the step
     * @param verbID - verbID of the step
     * @param fileID - fileID of the step
     * @param workflowID - workflowID of the step
     * @return Success string
     * @throws BadRequestException - UUID was an invalid integer format
     * @throws NotFoundException - No step with provided UUID was found
     * @throws InternalServerErrorException - Error connecting to database or executing query
     */
    public Step insertStep(String stepID, String orderNumber, String isHighestLevel, String description, String relatedStep,
                           String UUID, String verbID, String fileID, String workflowID) throws BadRequestException, ConflictException, NotFoundException, InternalServerErrorException{

        int stepIDInteger = Integer.parseInt(stepID);
        int orderNumberInteger = Integer.parseInt(orderNumber);
        boolean isHighestLevelBoolean = Boolean.parseBoolean(isHighestLevel);
        int relatedStepInteger = Integer.parseInt(relatedStep);
        int UUIDInteger = Integer.parseInt(UUID);
        int verbIDInteger = Integer.parseInt(verbID);
        int fileIDInteger = Integer.parseInt(verbID);
        int workflowIDInteger = Integer.parseInt(workflowID);


        try {
            if(stepID == null || stepID.isEmpty()) {
                throw new BadRequestException("A UUID must be provided for the step.");
            }

            if(isHighestLevel == null || isHighestLevel.isEmpty()) {
                throw new BadRequestException("You must provide a truth value for this step.");
            }

            if(description == null || description.isEmpty()) {
                throw new BadRequestException("You must provide a description for this step.");
            }

            if(verbID == null || verbID.isEmpty()) {
                throw new BadRequestException("You must provide a verbID for this step.");
            }

            if(fileID == null || fileID.isEmpty()) {
                throw new BadRequestException("You must provide a fileID for this step.");
            }

            if(workflowID == null || workflowID.isEmpty()) {
                throw new BadRequestException("You must provide a workflowID for this step.");
            }

            if(stepDB.getStepByStepID(Integer.parseInt(stepID)) != null){
                throw new ConflictException("A step with that stepID already exists.");
            }

            stepDB.insertStep(stepIDInteger, orderNumberInteger, isHighestLevelBoolean,
                              description, relatedStepInteger, UUIDInteger, verbIDInteger, fileIDInteger, workflowIDInteger);

            return stepDB.getStepByStepID(UUIDInteger);
        }
        catch(SQLException sqle){
            throw new InternalServerErrorException(sqle.getMessage());
        }
    }

    /**
     * Deletes a step from the database by stepID
     * @param stepID - step ID to delete from the database
     * @return Success string
     * @throws BadRequestException - stepID was a null or empty
     * @throws NotFoundException - No step with provided stepID was found
     * @throws InternalServerErrorException - Error connecting to database or executing query
     */
    public String deleteStepByStepID(String stepID) {
        int stepIDInteger = Integer.parseInt(stepID);
        try {
            int numRowsAffected = stepDB.deleteStepByStepID(stepIDInteger);

            if(numRowsAffected == 0){
                throw new NotFoundException("No step with that ID exists");
            }

            return "Successfully deleted step.";
        }
        catch(NumberFormatException nfe){
            throw new BadRequestException("stepID must be a valid integer.");
        }
        catch(SQLException sqle){
            throw new InternalServerErrorException(sqle.getMessage());
        }
    }
}
