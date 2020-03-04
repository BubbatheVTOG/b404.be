package blink.businesslayer;

import blink.datalayer.StepDB;
import blink.utility.objects.Step;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import io.swagger.util.Json;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotFoundException;
import java.io.BufferedReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
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
                step.setChildSteps(this.getRelatedSteps(step));
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
            step.setChildSteps(relatedSteps);
            for(Step relatedStep : step.getChildSteps()){
                relatedStep.setChildSteps(this.getRelatedSteps(relatedStep));
            }
        }
        return relatedSteps;
    }

    /**
     * Insert a list of ste[s into the database
     * @param steps - list of steps to insert into the database
     * @return Success Message
     */
    public int insertSteps(List<Step> steps, Connection conn) {
        int numInsertedSteps;
        try {
            numInsertedSteps = stepDB.insertSteps(steps, conn);
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
        int numUpdatedSteps;
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

    /**
     * Convert jsonToStepList so backend can use it
     * @param jsonObject which is the top level object containing workflowID and children[]
     * @return ArrayList<Step>
     */
    public List<Step> jsonToStepList(JsonObject jsonObject) {
        Collection<Step> steps = null;

        Gson gson = new GsonBuilder().serializeNulls().create();
        steps = gson.fromJson(jsonObject.get("children"), new TypeToken<List<Step>>(){}.getType());

        return new ArrayList<>(steps);
    }

    /**
     * Check for step completion
     * @param steps is a list of all steps
     * @return boolean flag
     */
    public boolean checkCompletion(List<Step> steps) {
        boolean flag = true;

        for(Step step: steps) {
            if (step.hasChildren()) {
                flag = checkCompletion(step.getChildSteps());
                step.setCompleted(flag);
            }
            flag = flag && step.isCompleted();
        }
        return flag;
    }
}
