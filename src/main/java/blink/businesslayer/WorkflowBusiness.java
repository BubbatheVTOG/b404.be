package blink.businesslayer;

import blink.datalayer.WorkflowDB;
import blink.utility.objects.*;
import com.google.common.reflect.TypeToken;
import com.google.gson.*;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.NotFoundException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class WorkflowBusiness {
    private WorkflowDB workflowDB;
    private PersonBusiness personBusiness;
    private MilestoneBusiness milestoneBusiness;
    private StepBusiness stepBusiness;
    private Gson gson;
    private SimpleDateFormat dateParser;

    public WorkflowBusiness(){
        this.workflowDB = new WorkflowDB();
        this.personBusiness = new PersonBusiness();
        this.milestoneBusiness = new MilestoneBusiness();
        this.stepBusiness = new StepBusiness();
        this.gson = new GsonBuilder().serializeNulls().create();
        this.dateParser = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    }

    /**
     * Get all workflows
     * @param uuid Uuid of the requesting user
     * @return List of workflows
     * @throws NotAuthorizedException Requester uuid was not found in the database
     * @throws InternalServerErrorException Error in data layer
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
        //SQLException If the data layer throws an SQLException; throw a custom Internal Server Error
        catch(SQLException ex){
            throw new InternalServerErrorException(ex.getMessage());
        }
    }

    /**
     * Get all template workflows
     * @return List of template workflows
     * @throws InternalServerErrorException Error in data layer
     */
    public List<Workflow> getTemplateWorkflows(String uuid) throws InternalServerErrorException {
        try{
            //Ensure requester still exists
            personBusiness.getPersonByUUID(uuid);

            return this.workflowDB.getTemplateWorkflows();
        }
        //If requester uuid does not exist then they were deleted and should not have access anymore
        catch(NotFoundException nfe){
            throw new NotAuthorizedException("Requesting UUID was not found.");
        }
        //SQLException If the data layer throws an SQLException; throw a custom Internal Server Error
        catch(SQLException sqle){
            throw new InternalServerErrorException(sqle.getMessage());
        }
    }

    /**
     * Get all workflows by archive status
     * @param uuid Uuid of the requesting user
     * @return List of active workflows
     * @throws InternalServerErrorException Error in data layer
     */
    private List<Workflow> getAllWorkflows(String uuid, boolean archived) throws InternalServerErrorException {
        try{
            Person requester = personBusiness.getPersonByUUID(uuid);

            List<Workflow> workflowList = new ArrayList<>();
            if(Authorization.INTERNAL_USER_LEVELS.contains(requester.getAccessLevelID())){
                workflowList = workflowDB.getConcreteWorkflows(archived);
            }
            else{
                List<Integer> companyIDList = requester.getCompanies().stream().map(Company::getCompanyID).collect(Collectors.toList());
                workflowList.addAll(workflowDB.getConcreteWorkflows(companyIDList, archived));
            }

            return workflowList;
        }
        //If requester uuid does not exist then they were deleted and should not have access anymore
        catch(NotFoundException nfe){
            throw new NotAuthorizedException("Requesting UUID was not found.");
        }
        //SQLException If the data layer throws an SQLException; throw a custom Internal Server Error
        catch(SQLException sqle){
            throw new InternalServerErrorException(sqle.getMessage());
        }
    }

    /**
     * Wrapper function of getConcreteWorkflows that gets all active workflows
     * @param uuid Requester's UUID
     * @return List of all workflows relevant to the user
     */
    public List<Workflow> getActiveWorkflows(String uuid){
        return this.getAllWorkflows(uuid, false);
    }

    /**
     * Wrapper function of getConcreteWorkflows that gets all archived workflows
     * @param uuid Requester's UUID
     * @return List of all workflows relevant to the user
     */
    public List<Workflow> getArchivedWorkflows(String uuid){
        return this.getAllWorkflows(uuid, true);
    }

    /**
     * Get a workflow from the database by workflowID
     * Also checks that a user has the credentials for retrieving this workflow
     * @param uuid UUID of requester
     * @param workflowID WorkflowID must be convertible to integer
     * @return Workflow object with matching id
     * @throws NotAuthorizedException Requester is either not internal or not part of the relevant company
     * @throws NotFoundException WorkflowID does not exist in database
     * @throws BadRequestException WorkflowID was either null or invalid integer
     * @throws InternalServerErrorException Error in data layer
     */
    public Workflow getWorkflowByID(String uuid, String workflowID) throws NotFoundException, BadRequestException, InternalServerErrorException {
        Workflow workflow = this.getWorkflowByID(workflowID);

        Person requester = personBusiness.getPersonByUUID(uuid);
        if(Authorization.INTERNAL_USER_LEVELS.contains(requester.getAccessLevelID())){
            return workflow;
        }
        else{
            if(requester.getCompanies().stream().anyMatch(company -> company.getCompanyID() == workflow.getCompany().getCompanyID())){
                return workflow;
            }
            else{
                throw new NotAuthorizedException("You do not have the authorization to get this workflow");
            }
        }
    }

    /**
     * Get a workflow from the database by workflowID
     * @param workflowID WorkflowID must be convertible to integer
     * @return Workflow object with matching id
     * @throws NotFoundException WorkflowID does not exist in database
     * @throws BadRequestException WorkflowID was either null or invalid integer
     * @throws InternalServerErrorException Error in data layer
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
        //SQLException If the data layer throws an SQLException; throw a custom Internal Server Error
        catch(SQLException sqle){
            throw new InternalServerErrorException(sqle.getMessage());
        }
    }

    /**
     * Get a milestone from the database by milestoneID
     * @param uuid id of requesting user
     * @param milestoneID MilestoneID must be convertible to integer
     * @return Milestone object with matching id
     * @throws NotFoundException MilestoneID does not exist in database
     * @throws BadRequestException MilestoneID was either null or invalid integer
     * @throws InternalServerErrorException Error in data layer
     */
    public List<Workflow> getWorkflowsByMilestoneID(String uuid, String milestoneID) throws NotFoundException, BadRequestException, InternalServerErrorException {
        try{
            //Checks that milestoneID exists and user has access
            Integer milestoneIDInteger = this.milestoneBusiness.getMilestoneByID(uuid, milestoneID).getMileStoneID();

            //Retrieve a list of workflow ID's belonging to this milestone
            List<Workflow> workflowList = this.workflowDB.getWorkflowsByMilestoneID(milestoneIDInteger);

            //Reaching this indicates no issues have been met and a success message can be returned
            return workflowList;
        }
        //SQLException If the data layer throws an SQLException; throw a custom Internal Server Error
        catch(SQLException ex){
            throw new InternalServerErrorException(ex.getMessage());
        }
    }

    /**
     * Inserts a template workflow
     * @param workflowJsonString String representation of workflow json object
     * @return Inserted workflow
     */
    public Workflow insertTemplateWorkflow(String workflowJsonString){
        try{
            JsonObject workflowJson = gson.fromJson(workflowJsonString, JsonObject.class);
            //Ensure name is present and retrieve it
            if(!workflowJson.has("name") || workflowJson.get("name").isJsonNull()){
                throw new BadRequestException("A workflow name is required");
            }
            String name = workflowJson.get("name").getAsString();

            //Get description if present, otherwise null
            String description = null;
            if(workflowJson.has("description")) {
                description = workflowJson.get("description").getAsString();
            }

            //Get current date for created and lastUpdated
            Date today = new Date();

            //Ensure steps are present and not empty
            if(!workflowJson.has("steps") || workflowJson.get("steps").isJsonNull()){
                throw new BadRequestException("A workflow must contain steps");
            }
            JsonArray stepsJson = workflowJson.getAsJsonArray("steps");
            if(stepsJson.size() == 0){
                throw new BadRequestException("Steps must be provided.");
            }

            //Insert workflow template
            int workflowID = this.workflowDB.insertWorkflow(name, description, today, today, stepsJson);

            return this.getWorkflowByID(Integer.toString(workflowID));
        }
        catch(JsonSyntaxException jse){
            throw new BadRequestException("Json String invalid format.");
        }
        catch(SQLException sqle){
            throw new InternalServerErrorException(sqle.getMessage());
        }
    }

    /**
     * Inserts a concrete workflow
     * @param workflowJsonString Name of workflow
     * @return Inserted workflow
     */
    public Workflow insertWorkflow(String workflowJsonString){
        try{
            JsonObject workflowJson = gson.fromJson(workflowJsonString, JsonObject.class);

            //Ensure name is present and retrieve it
            if(!workflowJson.has("name") || workflowJson.get("name").isJsonNull()){
                throw new BadRequestException("A workflow name is required");
            }
            String name = workflowJson.get("name").getAsString();

            //Get description if present, otherwise null
            String description = null;
            if(workflowJson.has("description")){
                description = workflowJson.get("description").getAsString();
            }

            //Ensure start date and delivery date are present and retrieve them
            if(!workflowJson.has("startDate") || workflowJson.get("startDate").isJsonNull()){
                throw new BadRequestException("A start date is required");
            }
            Date parsedStartDate = this.parseDate(workflowJson.get("startDate").getAsString());
            if(!workflowJson.has("deliveryDate") || workflowJson.get("deliveryDate").isJsonNull()){
                throw new BadRequestException("A delivery date is required");
            }
            Date parsedDeliveryDate = this.parseDate(workflowJson.get("deliveryDate").getAsString());

            //Ensure milestoneID is provided and ensure that milestone exists
            if(!workflowJson.has("milestoneID") || workflowJson.get("milestoneID").isJsonNull()){
                throw new BadRequestException("A workflow must be assigned to a milestone");
            }
            int milestoneIDInteger = this.milestoneBusiness.getMilestoneByID(workflowJson.get("milestoneID").getAsString()).getMileStoneID();

            //Ensure steps are provided and not empty
            if(!workflowJson.has("steps") || workflowJson.get("steps").isJsonNull()){
                throw new BadRequestException("A workflow must contain steps");
            }
            JsonArray stepsJson = workflowJson.getAsJsonArray("steps");
            if(stepsJson.size() == 0){
                throw new BadRequestException("Steps must be provided.");
            }

            //Get current date for created and lastUpdated
            Date today = new Date();

            //Insert workflow
            int workflowID = this.workflowDB.insertWorkflow(name, description, today, today, parsedStartDate, parsedDeliveryDate, milestoneIDInteger, stepsJson);

            return this.getWorkflowByID(Integer.toString(workflowID));
        }
        catch(JsonSyntaxException jse){
            throw new BadRequestException("Json String invalid format.");
        }
        catch(SQLException sqle){
            throw new InternalServerErrorException(sqle.getMessage());
        }
    }

    /**
     * Update a template workflow
     * @param workflowJsonString String representation of workflow json object
     * @return Inserted workflow
     */
    public Workflow updateTemplateWorkflow(String workflowJsonString){
        try{
            JsonObject workflowJson = gson.fromJson(workflowJsonString, JsonObject.class);

            //Ensure workflowID is provided and retrieve existing workflow
            if(!workflowJson.has("workflowID") || workflowJson.get("workflowID").isJsonNull()){
                throw new BadRequestException("A workflowID must be provided");
            }
            Workflow existingWorkflow = this.getWorkflowByID(workflowJson.get("workflowID").getAsString());
            int workflowID = existingWorkflow.getWorkflowID();

            //If name present, retrieve it
            String name = existingWorkflow.getName();
            if(workflowJson.has("name") && !workflowJson.get("name").isJsonNull()){
                name = workflowJson.get("name").getAsString();
            }

            //Get description if present, otherwise null
            String description = existingWorkflow.getDescription();
            if(workflowJson.has("description")) {
                description = workflowJson.get("description").getAsString();
            }

            //Get current date for created and lastUpdated
            Date today = new Date();

            //Get passed in steps or pre-existing steps
            List<Step> steps = existingWorkflow.getSteps();
            if(workflowJson.has("steps") && !workflowJson.get("steps").isJsonNull()){
                steps = stepBusiness.jsonToStepList(workflowJson.getAsJsonArray("steps"), workflowID);
            }

            //Insert workflow template
            this.workflowDB.updateWorkflow(workflowID, name, description, today, steps);

            return this.getWorkflowByID(Integer.toString(workflowID));
        }
        catch(JsonSyntaxException jse){
            throw new BadRequestException("Json String invalid format.");
        }
        catch(SQLException sqle){
            throw new InternalServerErrorException(sqle.getMessage());
        }
    }

    /**
     * Update a template workflow
     * @param workflowJsonString String representation of workflow json object
     * @return Inserted workflow
     */
    public Workflow updateConcreteWorkflow(String workflowJsonString){
        try{
            JsonObject workflowJson = gson.fromJson(workflowJsonString, JsonObject.class);

            //Ensure workflowID is provided and retrieve existing workflow
            if(!workflowJson.has("workflowID") || workflowJson.get("workflowID").isJsonNull()){
                throw new BadRequestException("A workflowID must be provided");
            }
            Workflow existingWorkflow = this.getWorkflowByID(workflowJson.get("workflowID").getAsString());
            int workflowID = existingWorkflow.getWorkflowID();

            //If name present, retrieve it
            String name = existingWorkflow.getName();
            if(workflowJson.has("name") && !workflowJson.get("name").isJsonNull()){
                name = workflowJson.get("name").getAsString();
            }

            //Get description if present, otherwise use existing value
            String description = existingWorkflow.getDescription();
            if(workflowJson.has("description")) {
                description = workflowJson.get("description").getAsString();
            }

            //Get current date for created and lastUpdated
            Date today = new Date();

            //Get start date if present, otherwise use existing values
            Date startDate = existingWorkflow.getStartDate();
            if(workflowJson.has("startDate")) {
                startDate = this.parseDate(workflowJson.get("startDate").getAsString());
            }
            Date deliveryDate = existingWorkflow.getDeliveryDate();
            if(workflowJson.has("deliveryDate")) {
                deliveryDate = this.parseDate(workflowJson.get("deliveryDate").getAsString());
            }

            //Get passed in steps or pre-existing steps
            List<Step> steps = existingWorkflow.getSteps();
            if(workflowJson.has("steps") && !workflowJson.get("steps").isJsonNull()){
                steps = stepBusiness.jsonToStepList(workflowJson.getAsJsonArray("steps"), workflowID);
            }

            //Insert workflow template
            this.workflowDB.updateWorkflow(workflowID, name, description, today, startDate, deliveryDate, existingWorkflow.getCompletedDate(), steps);

            return this.getWorkflowByID(Integer.toString(workflowID));
        }
        catch(JsonSyntaxException jse){
            throw new BadRequestException("Json String invalid format.");
        }
        catch(SQLException sqle){
            throw new InternalServerErrorException(sqle.getMessage());
        }
    }

    /**
     * Delete a workflow from the database by UUID
     * @param workflowID ID of workflow to delete
     * @return Success string
     * @throws NotFoundException WorkflowID does not exist in database
     * @throws BadRequestException WorkflowID was either null or invalid integer
     * @throws InternalServerErrorException Error in data layer
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
        //SQLException If the data layer throws an SQLException; throw a custom Internal Server Error
        catch(SQLException sqle){
            throw new InternalServerErrorException(sqle.getMessage());
        }
    }

    /**
     * Get a person's pending tasks
     * @param uuid String must be convertible to integer
     * @return Success string
     * @throws NotFoundException - UUID does not exist in database
     * @throws BadRequestException - UUID was either null or invalid integer
     * @throws InternalServerErrorException - Error in data layer
     */
    public List<Step> getPendingTasks(String uuid) throws NotFoundException, BadRequestException, InternalServerErrorException {
        try{
            //Initial parameter validation; throws BadRequestException if there is an issue
            if(uuid == null || uuid.isEmpty()){ throw new BadRequestException("A user ID must be provided"); }

            List<Workflow> userWorkflows = this.getActiveWorkflows(uuid);

            List<Step> pendingSteps = new ArrayList<>();
            for(Workflow workflow : userWorkflows){
                pendingSteps.addAll(this.findPendingSteps(workflow.getSteps(), uuid));
            }

            //Reaching this indicates no issues have been met and a success message can be returned
            return pendingSteps;
        }
        //SQLException - If the data layer throws an SQLException; throw a custom Internal Server Error
        //ArithmeticException - If the password encryption process fails
        catch(Exception ex){
            throw new InternalServerErrorException(ex.getMessage());
        }
    }

    /**
     * Helper function for searching step list and finding tasks which are ready to be worked on
     * @param steps - step list to search through
     * @param uuid - user to identify tasks from
     * @return list of pending steps for this user in this workflow
     */
    private List<Step> findPendingSteps(List<Step> steps, String uuid){
        List<Step> pendingSteps = new ArrayList<>();

        for(int i = 0; i < steps.size(); i++){
            Step currStep = steps.get(i);
            //If step is not already complete, asynchronous, first step or first incomplete step then it is pending
            if(!currStep.getCompleted() && (currStep.getAsynchronous() || currStep.getOrderNumber() == 1 || steps.get(i - 1).getCompleted())){
                //If leaf step assigned to requester, add to list
                if ((currStep.getChildren() == null || currStep.getChildren().isEmpty()) && currStep.getUUID().equals(uuid)) {
                    pendingSteps.add(currStep);
                }
                //If composite step, check children
                else{
                    pendingSteps.addAll(this.findPendingSteps(currStep.getChildren(), uuid));
                }
            }
        }

        return pendingSteps;
    }

    /**
     * Archive or unarchive an existing workflow
     * @param workflowID ID of workflow to archive
     * @throws NotFoundException WorkflowID does not exist
     * @throws BadRequestException WorkflowID is formatted improperly
     * @throws InternalServerErrorException Error in data layer
     */
    private void updateWorkflowArchiveStatus(String workflowID, boolean status) throws NotFoundException, BadRequestException, InternalServerErrorException {
        try{

            //Check that workflow exists
            Workflow workflow = this.getWorkflowByID(workflowID);

            workflowDB.updateWorkflowArchiveStatus(workflow.getWorkflowID(), status);
        }
        //SQLException If the data layer throws an SQLException; throw a custom Internal Server Error
        catch(SQLException sqle){
            throw new InternalServerErrorException(sqle.getMessage());
        }
    }

    /**
     * Wrapper function of updateWorkflowArchiveStatus to archive a workflow
     * @param workflowID ID of workflow to archive
     * @return Success string
     */
    public String archiveWorkflow(String workflowID){
        this.updateWorkflowArchiveStatus(workflowID, true);
        return "Successfully archived workflow.";
    }

    /**
     * Wrapper function of updateWorkflowArchiveStatus to unarchive a workflow
     * @param workflowID ID of workflow to archive
     * @return Success string
     */
    public String unarchiveWorkflow(String workflowID){
        this.updateWorkflowArchiveStatus(workflowID, false);
        return "Successfully unarchived workflow.";
    }

    /**
     * Marks a given step as complete and updates the workflow accordingly
     * @param stepID ID of step to mark as complete
     * @return
     */
    public String markStepComplete(String stepID, String uuid){
        Workflow workflow = null;
        try {
            //Mark the step as complete
            Person requester = this.personBusiness.getPersonByUUID(uuid);
            Step step = this.stepBusiness.getStep(stepID);

            if(step.hasChildren()){
                throw new BadRequestException("This is a composite step and cannot be marked complete.");
            }
            if(!step.getUUID().equals(requester.getUuid())){
                throw new NotAuthorizedException("This step is not assigned to you and cannot be marked as completed");
            }

            workflow = this.getWorkflowByID(Integer.toString(step.getWorkflowID()));
            if(workflow.getCompany() == null){
                throw new BadRequestException("This step belongs to a template workflow and cannot be marked as complete");
            }

            //Get workflow and find nested completions that may have occurred
            workflow = new Workflow(workflow.getWorkflowID(),
                    workflow.getName(),
                    workflow.getDescription(),
                    workflow.getCreatedDate(),
                    workflow.getLastUpdatedDate(),
                    workflow.getStartDate(),
                    workflow.getDeliveryDate(),
                    workflow.getCompletedDate(),
                    workflow.isArchived(),
                    workflow.getCompany(),
                    workflow.getMilestoneID(),
                    findStepCompletions(workflow.getSteps(), step.getStepID()));

            //Update changes in the step completion
            this.workflowDB.updateWorkflow(workflow.getWorkflowID(),
                    workflow.getName(),
                    workflow.getDescription(),
                    workflow.getLastUpdatedDate(),
                    workflow.getStartDate(),
                    workflow.getDeliveryDate(),
                    workflow.getCompletedDate(),
                    workflow.getSteps());

            return "Step successfully completed.";
        }
        catch(SQLException sqle){
            throw new InternalServerErrorException(gson.toJson(workflow));
        }
    }

    private List<Step> findStepCompletions(List<Step> steps, int stepID){
        for(Step step : steps) {
            if(step.getStepID() == stepID){
                step.setCompleted(true);
            }
            if (step.hasChildren()){
                step.setChildren(findStepCompletions(step.getChildren(), stepID));

                boolean completed = true;
                for(Step child : step.getChildren()){
                    completed = completed && child.getCompleted();
                }
                step.setCompleted(completed);
            }
        }

        return steps;
    }

    /**
     * Utility for parsing date objects and detecting that format is valid
     * @param dateString String of date; must be in format 'yyyy-MM-dd hh:mm:ss'
     * @return Date object
     * @throws BadRequestException if date in invalid format
     */
    private Date parseDate(String dateString){
        try{
            return dateParser.parse(dateString);
        }
        catch(ParseException pe){
            throw new BadRequestException("Dates must be formatted as yyyy-MM-dd hh:mm:ss");
        }
    }
}
