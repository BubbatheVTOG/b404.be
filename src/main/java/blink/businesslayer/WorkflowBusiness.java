package blink.businesslayer;

import blink.datalayer.WorkflowDB;
import blink.utility.objects.*;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.NotFoundException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class WorkflowBusiness {
    private WorkflowDB workflowDB;
    private StepBusiness stepBusiness;
    private PersonBusiness personBusiness;
    private CompanyBusiness companyBusiness;
    private MilestoneBusiness milestoneBusiness;
    private SimpleDateFormat dateParser;

    public WorkflowBusiness(){
        this.workflowDB = new WorkflowDB();
        this.stepBusiness = new StepBusiness();
        this.personBusiness = new PersonBusiness();
        this.companyBusiness = new CompanyBusiness();
        this.milestoneBusiness = new MilestoneBusiness();
        this.dateParser = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    }

    /**
     * Get all workflows
     * @param uuid - uuid of the requesting user
     * @return List of workflows
     * @throws NotAuthorizedException - requester uuid was not found in the database
     * @throws InternalServerErrorException - Error in data layer
     */
    public List<Workflow> getAllWorkflows(String uuid) throws NotAuthorizedException, InternalServerErrorException {
        try{
            Person requester = personBusiness.getPersonByUUID(uuid);

            List<Workflow> workflowList = new ArrayList<>();
            if(Authorization.INTERNAL_USER_LEVELS.contains(requester.getAccessLevelID())){
                workflowList = workflowDB.getAllWorkflows();
            }
            else{
                List<Integer> companyIDList = requester.getCompanies().stream().map(Company::getCompanyID).collect(Collectors.toList());
                workflowList.addAll(workflowDB.getAllWorkflows(companyIDList));
            }

            return workflowList;
        }
        //If requester uuid does not exist then they were deleted and should not have access anymore
        catch(NotFoundException nfe){
            throw new NotAuthorizedException("Requesting UUID was not found.");
        }
        //SQLException - If the data layer throws an SQLException; throw a custom Internal Server Error
        catch(SQLException ex){
            throw new InternalServerErrorException(ex.getMessage());
        }
    }

    /**
     * Get all workflows by archive status
     * @param uuid - uuid of the requesting user
     * @return List of active workflows
     * @throws InternalServerErrorException - Error in data layer
     */
    private List<Workflow> getAllWorkflows(String uuid, boolean archived) throws InternalServerErrorException {
        try{
            Person requester = personBusiness.getPersonByUUID(uuid);

            List<Workflow> workflowList = new ArrayList<>();
            if(Authorization.INTERNAL_USER_LEVELS.contains(requester.getAccessLevelID())){
                workflowList = workflowDB.getAllWorkflows(archived);
            }
            else{
                List<Integer> companyIDList = requester.getCompanies().stream().map(Company::getCompanyID).collect(Collectors.toList());
                workflowList.addAll(workflowDB.getAllWorkflows(companyIDList, archived));
            }

            return workflowList;
        }
        //If requester uuid does not exist then they were deleted and should not have access anymore
        catch(NotFoundException nfe){
            throw new NotAuthorizedException("Requesting UUID was not found.");
        }
        //SQLException - If the data layer throws an SQLException; throw a custom Internal Server Error
        catch(SQLException ex){
            throw new InternalServerErrorException(ex.getMessage());
        }
    }

    /**
     * Wrapper function of getAllWorkflows that gets all active workflows
     * @param uuid - requester's UUID
     * @return List of all workflows relevant to the user
     */
    public List<Workflow> getActiveWorkflows(String uuid){
        return this.getAllWorkflows(uuid, false);
    }

    /**
     * Wrapper function of getAllWorkflows that gets all archived workflows
     * @param uuid - requester's UUID
     * @return List of all workflows relevant to the user
     */
    public List<Workflow> getArchivedWorkflows(String uuid){
        return this.getAllWorkflows(uuid, true);
    }

    /**
     * Get a workflow from the database by workflowID
     * Also checks that a user has the credentials for retrieving this workflow
     * @param uuid - UUID of requester
     * @param workflowID - workflowID must be convertible to integer
     * @return Workflow object with matching id
     * @throws NotAuthorizedException - Requester is either not internal or not part of the relevant company
     * @throws NotFoundException - WorkflowID does not exist in database
     * @throws BadRequestException - WorkflowID was either null or invalid integer
     * @throws InternalServerErrorException - Error in data layer
     */
    public Workflow getWorkflowByID(String uuid, String workflowID) throws NotFoundException, BadRequestException, InternalServerErrorException {
        Workflow workflow = this.getWorkflowByID(workflowID);

        Person requester = personBusiness.getPersonByUUID(uuid);
        if(Authorization.INTERNAL_USER_LEVELS.contains(requester.getAccessLevelID())){
            return workflow;
        }
        else{
            if(requester.getCompanies().stream().anyMatch(company -> company.getCompanyID() == workflow.getCompanyID())){
                return workflow;
            }
            else{
                throw new NotAuthorizedException("You do not have the authorization to get this workflow");
            }
        }
    }

    /**
     * Get a workflow from the database by workflowID
     * @param workflowID - WorkflowID must be convertible to integer
     * @return Workflow object with matching id
     * @throws NotFoundException - WorkflowID does not exist in database
     * @throws BadRequestException - WorkflowID was either null or invalid integer
     * @throws InternalServerErrorException - Error in data layer
     */
    private Workflow getWorkflowByID(String workflowID) throws NotFoundException, BadRequestException, InternalServerErrorException {
        try{
            //Initial parameter validation; throws BadRequestException if there is an issue
            if(workflowID == null || workflowID.isEmpty()){ throw new BadRequestException("A workflow ID must be provided"); }

            Integer idInteger = Integer.parseInt(workflowID);

            //Retrieve the workflow from the database
            Workflow workflow = workflowDB.getWorkflowByID(idInteger);

            //If null is returned, no workflow was found with given id
            if(workflow == null){
                throw new NotFoundException("No workflow with that ID exists.");
            }

            //Reaching this indicates no issues have been met and a success message can be returned
            return workflow;
        }
        //Error converting workflow to integer
        catch(NumberFormatException nfe){
            throw new BadRequestException("Workflow ID must be a valid integer");
        }
        //SQLException - If the data layer throws an SQLException; throw a custom Internal Server Error
        catch(SQLException ex){
            throw new InternalServerErrorException(ex.getMessage());
        }
    }

    /**
     * Inserts a template workflow
     * @param name - name of template
     * @param description - description of template
     * @param stepsJson - json string of steps to relate to this template
     * @return Inserted workflow
     */
    public Workflow insertWorkflow(String name, String description, String stepsJson){
        try{
            if(name == null || name.isEmpty()){
                throw new BadRequestException("A name must be provided.");
            }

            if(stepsJson == null || stepsJson.isEmpty()){
                throw new BadRequestException("Steps must be provided.");
            }

            Date today = new Date();
            int workflowID = this.workflowDB.insertWorkflow(name, description, today, today);

            //TODO: update this line once the step parsing system has been added
            List<Step> steps = new ArrayList<>(); //this.stepBusiness.parseSteps(stepsJson, workflowID);
            this.stepBusiness.insertSteps(steps);

            return new Workflow(workflowID, name, description, null, null, null, null, null, false, 0, 0, steps);
        }
        catch(SQLException sqle){
            throw new InternalServerErrorException();
        }
    }

    /**
     * Inserts a concrete workflow
     * @param name - name of workflow
     * @param description - description of workflow
     * @param stepsJson - json string of steps to relate to this workflow
     * @return Inserted workflow
     */
    public Workflow insertWorkflow(String name, String description, String startDate, String deliveryDate, String companyID, String milestoneID, String stepsJson){
        try{
            if(name == null || name.isEmpty()){
                throw new BadRequestException("A name must be provided.");
            }

            Date parsedStartDate = this.parseDate(startDate);
            Date parsedDeliveryDate = this.parseDate(deliveryDate);

            int companyIDInteger = this.companyBusiness.getCompanyByID(companyID).getCompanyID();
            int milestoneIDInteger = this.milestoneBusiness.getMilestoneByID(milestoneID).getMileStoneID();

            if(stepsJson == null || stepsJson.isEmpty()){
                throw new BadRequestException("Steps must be provided.");
            }

            Date today = new Date();
            int workflowID = this.workflowDB.insertWorkflow(name, description, today, today, parsedStartDate, parsedDeliveryDate, companyIDInteger, milestoneIDInteger);

            //TODO: update this line once the step parsing system has been added
            List<Step> steps = new ArrayList<>(); //this.stepBusiness.parseSteps(stepsJson, workflowID);
            this.stepBusiness.insertSteps(steps);

            return new Workflow(workflowID, name, description, null, null, null, null, null, false, 0, 0, steps);
        }
        catch(SQLException sqle){
            throw new InternalServerErrorException();
        }
    }

    //TODO: implement insert template, create concrete and update workflow

    /**
     * Delete a workflow from the database by UUID
     * @param workflowID - ID of workflow to delete
     * @return Success string
     * @throws NotFoundException - WorkflowID does not exist in database
     * @throws BadRequestException - WorkflowID was either null or invalid integer
     * @throws InternalServerErrorException - Error in data layer
     */
    public String deleteWorkflowByID(String workflowID) throws NotFoundException, BadRequestException, InternalServerErrorException {
        try{
            //Validate workflow ID
            int workflowIDInteger;
            workflowIDInteger = Integer.parseInt(workflowID);


            //Retrieve the person from the database by UUID
            int numRowsDeleted = workflowDB.deleteWorkflowByID(workflowIDInteger);

            //If null is returned, no user was found with given UUID
            if(numRowsDeleted == 0){
                throw new NotFoundException("No workflow with that id exists.");
            }

            //Reaching this indicates no issues have been met and a success message can be returned
            return "Successfully deleted workflow.";
        }
        catch(NumberFormatException nfe){
            throw new BadRequestException("Workflow ID must be a valid integer");
        }
        //SQLException - If the data layer throws an SQLException; throw a custom Internal Server Error
        catch(SQLException ex){
            throw new InternalServerErrorException(ex.getMessage());
        }
    }

    /**
     * Archive or unarchive an existing workflow
     * @param workflowID - ID of workflow to archive
     * @throws NotFoundException - WorkflowID does not exist
     * @throws BadRequestException - WorkflowID is formatted improperly
     * @throws InternalServerErrorException - Error in data layer
     */
    private void updateWorkflowArchiveStatus(String workflowID, boolean status) throws NotFoundException, BadRequestException, InternalServerErrorException {
        try{

            //Check that workflow exists
            Workflow workflow = this.getWorkflowByID(workflowID);

            workflowDB.updateWorkflowArchiveStatus(workflow.getWorkflowID(), status);
        }
        //SQLException - If the data layer throws an SQLException; throw a custom Internal Server Error
        catch(SQLException ex){
            throw new InternalServerErrorException(ex.getMessage());
        }
    }

    /**
     * Wrapper function of updateWorkflowArchiveStatus to archive a workflow
     * @param workflowID - ID of workflow to archive
     * @return Success string
     */
    public String archiveWorkflow(String workflowID){
        this.updateWorkflowArchiveStatus(workflowID, true);
        return "Successfully archived workflow.";
    }

    /**
     * Wrapper function of updateWorkflowArchiveStatus to unarchive a workflow
     * @param workflowID - ID of workflow to archive
     * @return Success string
     */
    public String unarchiveWorkflow(String workflowID){
        this.updateWorkflowArchiveStatus(workflowID, false);
        return "Successfully unarchived workflow.";
    }

    /**
     * Function to convert jsonArray into List<Step>
     * @param jsonObject is the top level object passed in
     * @return children which is the list of steps
     */
    public List<Step> jsonToStepList(JsonObject jsonObject) {
        List<Step> children = new ArrayList<>();

        int workflowID = jsonObject.get("workflowID").getAsInt();
        children = jsonArrayToStepList(jsonObject.get("list").getAsJsonArray(), workflowID);

        return children;
    }

    public List<Step> jsonArrayToStepList(JsonArray jsArray, int workflowID) {
        List<Step> children = new ArrayList<>();

        for(JsonElement jsonElement : jsArray) {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            Step step;
            if(jsonObject.get("children").isJsonNull()) {
                step = new Step.StepBuilder(
                        jsonObject.get("title").getAsInt(),
                        jsonObject.get("fileID").getAsInt(),
                        workflowID,
                        jsonObject.get("completed").getAsBoolean())
                        .description(jsonObject.get("subtitle").getAsString())
                        .uuid(jsonObject.get("uuid").getAsInt())
                        .build();
            } else {
                step = new Step.StepBuilder(
                        jsonObject.get("title").getAsInt(),
                        jsonObject.get("fileID").getAsInt(),
                        workflowID,
                        jsonObject.get("completed").getAsBoolean())
                        .description(jsonObject.get("subtitle").getAsString())
                        .uuid(jsonObject.get("uuid").getAsInt())
                        .childSteps(jsonArrayToStepList(jsonObject.get("children").getAsJsonArray(), workflowID))
                        .build();
            }
            children.add(step);
        }
        return children;
    }

    /**
     * Utility for parsing date objects and detecting that format is valid
     * @param dateString - String of date; must be in format 'YYYY-MM-DD'
     * @return Date object
     * @throws BadRequestException if date in invalid format
     */
    private Date parseDate(String dateString){
        try{
            return dateParser.parse(dateString);
        }
        catch(ParseException pe){
            throw new BadRequestException("Dates must be formatted as YYYY-MM-DD");
        }
    }
}
