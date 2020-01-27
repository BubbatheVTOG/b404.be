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
     * @return Step objects containing data from the database
     * @throws InternalServerErrorException - Error connecting to database or executing query
     */
    public ArrayList<Step> getSteps() throws InternalServerErrorException {
        try {
            ArrayList<Step> steps = stepDB.getHigherLevelSteps();
            for(Step step : steps) {
                step.setChildSteps(this.getRelatedSteps(step));
            }
            return steps;
        } catch(SQLException sqle) {
            throw new InternalServerErrorException(sqle.getMessage());
        }
    }

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
    public Step getStepByStepID(int stepID) throws BadRequestException, NotFoundException, InternalServerErrorException {
        try {
            Step step = stepDB.getStepByStepID(stepID);

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
    public Step insertStep(int stepID, int orderNumber, boolean isHighestLevel, String description, int relatedStep,
                           int UUID, int verbID, int fileID, int workflowID) throws BadRequestException, ConflictException, NotFoundException, InternalServerErrorException{
        try {
            if(Integer.toString(stepID) == null || Integer.toString(stepID).isEmpty()) {
                throw new BadRequestException("A UUID must be provided for the step.");
            }

            if(description == null || description.isEmpty()) {
                throw new BadRequestException("You must provide a description for this step.");
            }

            if(Integer.toString(workflowID) == null || Integer.toString(workflowID).isEmpty()) {
                throw new BadRequestException("You must provide a workflowID for this step.");
            }

            if(stepDB.getStepByStepID(UUID) != null){
                throw new ConflictException("A step with that stepID already exists.");
            }

            stepDB.insertStep(stepID, orderNumber, isHighestLevel, description, relatedStep, UUID, verbID, fileID, workflowID);

            return stepDB.getStepByStepID(UUID);
        }
        catch(SQLException sqle){
            throw new InternalServerErrorException(sqle.getMessage());
        }
    }
}
