package blink.businesslayer;

import blink.datalayer.StepDB;
import blink.utility.objects.Step;
import com.google.gson.*;

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
     * @param workflowID WorkflowID of steps to retrieve from database
     * @return Step objects containing data from the database
     * @throws InternalServerErrorException Error connecting to database or executing query
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
            step.setChildSteps(relatedSteps);
            for(Step relatedStep : step.getChildSteps()){
                relatedStep.setChildSteps(this.getRelatedSteps(relatedStep));
            }
        }
        return relatedSteps;
    }

    /**
     * Insert a list of steps into the database
     * @param steps List of steps to insert into the database
     * @return Success Message
     */
    int insertSteps(List<Step> steps) {
        int numInsertedSteps;
        try {
            numInsertedSteps = stepDB.insertSteps(steps);
        } catch(SQLException ex) {
            throw new InternalServerErrorException(ex.getMessage());
        }
        return numInsertedSteps;
    }

    /**
     * Deletes existing steps by workflowID and adds updated step list
     * @param steps Updated list of steps
     * @param workflowID WorkflowID to delete existing steps by
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
     * @param workflowID WorkflowID to delete steps by
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

    public List<Step> jsonToStepList(JsonArray children, int workflowID) {
        List<Step> steps = new ArrayList<>();

        for(JsonElement jsonElement : children) {
            JsonObject jsonArrayObject = jsonElement.getAsJsonObject();
            Step step;
            if(jsonArrayObject.get("children").isJsonNull()) {
                step = new Step.StepBuilder(
                        workflowID,
                        jsonArrayObject.get("completed").getAsBoolean(),
                        jsonArrayObject.get("asynchronous").getAsBoolean())
                        .description(jsonArrayObject.get("subtitle").getAsString())
                        .uuid(jsonArrayObject.get("uuid").getAsInt())
                        .verbID(jsonArrayObject.get("title").getAsInt())
                        .fileID(jsonArrayObject.get("fileID").getAsInt())
                        .build();
            } else {
                step = new Step.StepBuilder(
                        workflowID,
                        jsonArrayObject.get("completed").getAsBoolean(),
                        jsonArrayObject.get("asynchronous").getAsBoolean())
                        .childSteps(jsonToStepList(jsonArrayObject.get("children").getAsJsonArray(), workflowID))
                        .build();
            }
            steps.add(step);
        }
        return steps;
    }
}
