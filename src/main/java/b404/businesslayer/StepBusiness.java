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
}
