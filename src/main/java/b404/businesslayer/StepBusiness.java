package b404.businesslayer;

import b404.datalayer.StepDB;
import b404.utility.objects.Step;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotFoundException;
import java.sql.Array;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
    public List<Step> getSteps(String workflowID) throws InternalServerErrorException {
        try {
            List<Step> steps = stepDB.getHigherLevelSteps(Integer.parseInt(workflowID));
            for (Step step : steps) {
                step.setChildSteps((ArrayList<Step>) this.getRelatedSteps(step));
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
    public List<Step> getRelatedSteps(Step step) throws InternalServerErrorException {
        List<Step> relatedSteps;
        try {
            relatedSteps = stepDB.getRelatedSteps(step.getStepID());
        } catch(SQLException sqle) {
            throw new InternalServerErrorException("Problem returning related steps.");
        }
        if (relatedSteps != null) {
            step.setChildSteps((ArrayList<Step>) relatedSteps);
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
     * @param orderNumber - orderNumber of the higher level step
     * @param description - description of the step
     * @param relatedStep - relatedStep to the current step
     * @param uuid - UUID of the step
     * @param verbID - verbID of the step
     * @param fileID - fileID of the step
     * @param workflowID - workflowID of the step
     * @return Success string
     * @throws BadRequestException - UUID was an invalid integer format
     * @throws NotFoundException - No step with provided UUID was found
     * @throws InternalServerErrorException - Error connecting to database or executing query
     */
    public Step insertStep(String orderNumber, String isHighestLevel, String description, String relatedStep,
                           String uuid, String verbID, String fileID, String workflowID) throws BadRequestException, NotFoundException, InternalServerErrorException{

        boolean isHighestLevelBoolean;
        int orderNumberInteger;
        int relatedStepInteger;
        int UUIDInteger;
        int verbIDInteger;
        int fileIDInteger;
        int workflowIDInteger;

        try {
            isHighestLevelBoolean = Boolean.parseBoolean(isHighestLevel);
        } catch (NumberFormatException nfe) {
            throw new BadRequestException("You have not entered a valid isHighestLevel.");
        }

        try {
            orderNumberInteger = Integer.parseInt(orderNumber);
        } catch(NumberFormatException nfe) {
            throw new BadRequestException("You have not entered a valid orderNumber.");
        }

        try {
            relatedStepInteger = Integer.parseInt(relatedStep);
        } catch(NumberFormatException nfe) {
            throw new BadRequestException("You have not entered a valid relatedStep.");
        }

        try {
            UUIDInteger = Integer.parseInt(uuid);
        } catch(NumberFormatException nfe) {
            throw new BadRequestException("You have not entered a valid uuid.");
        }

        try {
            verbIDInteger = Integer.parseInt(verbID);
        } catch(NumberFormatException nfe) {
            throw new BadRequestException("You have not entered a valid verbID.");
        }

        try {
            fileIDInteger = Integer.parseInt(fileID);
        } catch(NumberFormatException nfe) {
            throw new BadRequestException("You have not entered a valid fileID.");
        }

        try {
            workflowIDInteger = Integer.parseInt(workflowID);
        } catch (NumberFormatException nfe) {
            throw new BadRequestException("You have not entered a valid workflowID.");
        }

        try {
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

            stepDB.insertStep(orderNumberInteger, isHighestLevelBoolean,
                              description, relatedStepInteger, UUIDInteger, verbIDInteger, fileIDInteger, workflowIDInteger);

            return stepDB.getStepByStepID(UUIDInteger);
        } catch(SQLException sqle) {
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

    /**
     * Swaps step attributes
     * @param stepOneID - step one to swap with step two
     * @param stepTwoID - step two to swap with step one
     * @return List of steps that have their attributes swapped
     */
    public List<Step> swapSteps(String stepOneID, String stepTwoID) {
        int stepOneIDInteger = Integer.parseInt(stepOneID);
        int stepTwoIDInteger = Integer.parseInt(stepTwoID);
        Step stepOne, stepTwo;

        try {
            int numRowsAffected = stepDB.swapSteps(stepOneIDInteger, stepTwoIDInteger);

            if(numRowsAffected == 0) {
                throw new NotFoundException("No step with that ID exists");
            }
            stepOne = stepDB.getStepByStepID(stepOneIDInteger);
            stepTwo = stepDB.getStepByStepID(stepTwoIDInteger);
            List<Step> steps = new ArrayList<>();
            steps.add(stepOne); steps.add(stepTwo);

            return steps;
        } catch(NumberFormatException nfe) {
            throw new BadRequestException("stepID must be a valid integer.");
        } catch(SQLException sqle) {
            throw new InternalServerErrorException(sqle.getMessage());
        }
    }
}
