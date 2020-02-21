package blink.businesslayer;

import blink.datalayer.MilestoneDB;
import blink.datalayer.WorkflowDB;
import blink.utility.objects.*;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.NotFoundException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class WorkflowBusiness {
    private WorkflowDB workflowDB;
    private PersonBusiness personBusiness;
    private CompanyBusiness companyBusiness;
    private SimpleDateFormat dateParser;

    public WorkflowBusiness(){
        this.workflowDB = new WorkflowDB();
        this.personBusiness = new PersonBusiness();
        this.companyBusiness = new CompanyBusiness();
        this.dateParser = new SimpleDateFormat("yyyy-MM-dd");
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
}
