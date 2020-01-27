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
    public ArrayList<Step> getHigherLevelSteps() throws InternalServerErrorException {
        try {
            return stepDB.getHigherLevelSteps();
        } catch(SQLException sqle) {
            throw new InternalServerErrorException(sqle.getMessage());
        }
    }

    /**
     * Gets a step from the database by UUID
     * @param UUID - UUID to search the database for
     * @return Step object containing data from the database
     * @throws BadRequestException - UUID was an invalid integer format
     * @throws NotFoundException - No step with provided UUID was found
     * @throws InternalServerErrorException - Error connecting to database or executing query
     */
    public Step getStepByUUID(int UUID) throws BadRequestException, NotFoundException, InternalServerErrorException {
        try {
            Step step = stepDB.getStepByUUID(UUID);

            if(step == null) {
                throw new NotFoundException("No step with that UUID exists.");
            } else {
                return step;
            }
        } catch(NumberFormatException nfe){
            throw new BadRequestException("UUID must be a valid integer.");
        } catch(SQLException sqle){
            throw new InternalServerErrorException(sqle.getMessage());
        }
    }

    /**
     * Inserts a new company into the database
     * @param UUID - UUID of the step
     * @param orderNumber - orderNumber of the higher level step
     * @param description - description of the step
     * @param relatedStep - relatedStep to the current step
     * @param workflowID - workflowID of the step
     * @return Success string
     * @throws BadRequestException - UUID was an invalid integer format
     * @throws NotFoundException - No step with provided UUID was found
     * @throws InternalServerErrorException - Error connecting to database or executing query
     */
    public Step insertStep(int UUID, int orderNumber, String description, int relatedStep, int workflowID) throws BadRequestException, ConflictException, NotFoundException, InternalServerErrorException{
        try {
            if(Integer.toString(UUID) == null || Integer.toString(UUID).isEmpty()) {
                throw new BadRequestException("A UUID must be provided for the step.");
            }

            if(description == null || description.isEmpty()) {
                throw new BadRequestException("You must provide a description for this step.");
            }

            if(Integer.toString(workflowID) == null || Integer.toString(workflowID).isEmpty()) {
                throw new BadRequestException("You must provide a workflowID for this step.");
            }

            if(stepDB.getStepByUUID(UUID) != null){
                throw new ConflictException("A step with that UUID already exists.");
            }

            stepDB.insertStep(UUID, orderNumber, description, relatedStep, workflowID);

            return stepDB.getStepByUUID(UUID);
        }
        catch(SQLException sqle){
            throw new InternalServerErrorException(sqle.getMessage());
        }
    }
}
