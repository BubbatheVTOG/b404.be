package blink.businesslayer;

import blink.datalayer.StepDB;
import blink.utility.objects.Step;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotFoundException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Business layer service for step related logic
 * Enforces business rules and leverages datalayer to manipulate database
 */
public class StepBusiness {

    private static final String WORKFLOWID_ERROR = "workflowID must be a valid integer.";

    private StepDB stepDB = new StepDB();

    /**
     * Gets higher level steps from the database
     * @param workflowID - workflowID of steps to retrieve from database
     * @return Step objects containing data from the database
     * @throws InternalServerErrorException - Error connecting to database or executing query
     */
    public List<Step> getSteps(String workflowID) {
        try {
            List<Step> steps = stepDB.getHigherLevelSteps(Integer.parseInt(workflowID));
            for (Step step : steps) {
                step.setChildSteps((ArrayList<Step>) this.getRelatedSteps(step));
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
     * @param step - higher level step to retrieve stepID from
     * @return array of steps containing related steps
     * @throws InternalServerErrorException - Error connecting to database or executing query
     */
    public List<Step> getRelatedSteps(Step step) {
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
    public Step getStepByStepID(String stepID) {
        try {
            Step step = stepDB.getStepByStepID(Integer.parseInt(stepID));

            if(step == null) {
                throw new NotFoundException("No step with that stepID exists.");
            } else {
                return step;
            }
        } catch(NumberFormatException nfe){
            throw new BadRequestException("stepID must be a valid integer.");
        } catch(SQLException ex){
            throw new InternalServerErrorException(ex.getMessage());
        }
    }

    /**
     * Insert a list of ste[s into the database
     * @param steps - list of steps to insert into the database
     * @return Success Message
     */
    public int insertSteps(List<Step> steps) {
        int numInsertedSteps = 0;
        try {
            numInsertedSteps = stepDB.insertSteps(steps);
        } catch(SQLException ex) {
            throw new InternalServerErrorException(ex.getMessage());
        }
        return numInsertedSteps;
    }

    /**
     * Deletes existing steps by workflowID and adds updated step list
     * @param steps - updated list of steps
     * @param workflowID - workflowID to delete existing steps by
     * @return Success Message
     */
    public int updateSteps(List<Step> steps, String workflowID) {
        int numUpdatedSteps = 0;
        try {
            numUpdatedSteps = stepDB.updateSteps(steps, workflowID);

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
     * @param workflowID - workflowID to delete steps by
     * @return Success Message
     */
    public int deleteStepsByWorkflowID(String workflowID) {
        int numDeletedSteps = 0;
        try {
            if(stepDB.deleteStepsByWorkflowID(Integer.parseInt(workflowID)) <= 0) {
                throw new NotFoundException("No records with that workflowID exist.");
            }
        } catch (NumberFormatException ex) {
            throw new BadRequestException(WORKFLOWID_ERROR);
        } catch (SQLException ex) {
            throw new InternalServerErrorException(ex.getMessage());
        }
        return numDeletedSteps;
    }
}
