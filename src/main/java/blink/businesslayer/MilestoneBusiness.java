package blink.businesslayer;

import blink.datalayer.MilestoneDB;
import blink.utility.objects.Company;
import blink.utility.objects.Milestone;
import blink.utility.objects.Person;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.NotFoundException;
import java.util.ArrayList;
import java.util.Date;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.stream.Collectors;

public class MilestoneBusiness {
    private MilestoneDB milestoneDB;
    private PersonBusiness personBusiness;
    private CompanyBusiness companyBusiness;
    private SimpleDateFormat dateParser;

    public MilestoneBusiness(){
        this.milestoneDB = new MilestoneDB();
        this.personBusiness = new PersonBusiness();
        this.companyBusiness = new CompanyBusiness();
        this.dateParser = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    }


    /**
     * Get all milestones
     * @param uuid uuid of the requesting user
     * @return List of milestones
     * @throws NotAuthorizedException requester uuid was not found in the database
     * @throws InternalServerErrorException Error in data layer
     */
    public List<Milestone> getAllMilestones(String uuid) throws NotAuthorizedException, InternalServerErrorException {
        try{
            Person requester = personBusiness.getPersonByUUID(uuid);

            List<Milestone> milestoneList = new ArrayList<>();
            if(Authorization.INTERNAL_USER_LEVELS.contains(requester.getAccessLevelID())){
                milestoneList = milestoneDB.getAllMilestones();
            }
            else{
                List<Integer> companyIDList = requester.getCompanies().stream().map(Company::getCompanyID).collect(Collectors.toList());
                milestoneList.addAll(milestoneDB.getAllMilestones(companyIDList));
            }

            return milestoneList;
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
     * Get all active milestones
     * @param uuid uuid of the requesting user
     * @return List of active milestones
     * @throws InternalServerErrorException Error in data layer
     */
    public List<Milestone> getActiveMilestones(String uuid) throws InternalServerErrorException {
        try{
            Person requester = personBusiness.getPersonByUUID(uuid);

            List<Milestone> milestoneList = new ArrayList<>();
            if(Authorization.INTERNAL_USER_LEVELS.contains(requester.getAccessLevelID())){
                milestoneList = milestoneDB.getAllMilestones(false);
            }
            else{
                List<Integer> companyIDList = requester.getCompanies().stream().map(Company::getCompanyID).collect(Collectors.toList());
                milestoneList.addAll(milestoneDB.getAllMilestones(companyIDList, false));
            }

            return milestoneList;
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
     * Get all archived milestones
     * @return List of archived milestones
     * @throws InternalServerErrorException Error in data layer
     */
    public List<Milestone> getArchivedMilestones(String uuid) throws InternalServerErrorException {
        try{
            Person requester = personBusiness.getPersonByUUID(uuid);

            List<Milestone> milestoneList = new ArrayList<>();
            if(Authorization.INTERNAL_USER_LEVELS.contains(requester.getAccessLevelID())){
                milestoneList = milestoneDB.getAllMilestones(true);
            }
            else{
                List<Integer> companyIDList = requester.getCompanies().stream().map(Company::getCompanyID).collect(Collectors.toList());
                milestoneList.addAll(milestoneDB.getAllMilestones(companyIDList, true));
            }

            return milestoneList;
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
     * Get a milestone from the database by milestoneID
     * Also checks that a user has the credentials for retrieving this milestone
     * @param uuid UUID of requester
     * @param milestoneID milestoneID must be convertible to integer
     * @return Milestone object with matching id
     * @throws NotAuthorizedException Requester is either not internal or not part of the relevant company
     * @throws NotFoundException MilestoneID does not exist in database
     * @throws BadRequestException MilestoneID was either null or invalid integer
     * @throws InternalServerErrorException Error in data layer
     */
    public Milestone getMilestoneByID(String uuid, String milestoneID) throws NotFoundException, BadRequestException, InternalServerErrorException {
        Milestone milestone = this.getMilestoneByID(milestoneID);

        Person requester = personBusiness.getPersonByUUID(uuid);
        if(Authorization.INTERNAL_USER_LEVELS.contains(requester.getAccessLevelID())){
            return milestone;
        }
        else{
            if(requester.getCompanies().stream().anyMatch(company -> company.getCompanyID() == milestone.getCompany().getCompanyID())){
                return milestone;
            }
            else{
                throw new NotAuthorizedException("You do not have the authorization to get this milestone");
            }
        }
    }

    /**
     * Get a milestone from the database by milestoneID
     * @param id MilestoneID must be convertible to integer
     * @return Milestone object with matching id
     * @throws NotFoundException MilestoneID does not exist in database
     * @throws BadRequestException MilestoneID was either null or invalid integer
     * @throws InternalServerErrorException Error in data layer
     */
    Milestone getMilestoneByID(String id) throws NotFoundException, BadRequestException, InternalServerErrorException {
        try{
            //Initial parameter validation; throws BadRequestException if there is an issue
            if(id == null || id.isEmpty()){ throw new BadRequestException("A milestone ID must be provided"); }

            Integer idInteger = Integer.parseInt(id);

            //Retrieve the milestone from the database
            Milestone milestone = milestoneDB.getMilestoneByID(idInteger);

            //If null is returned, no milestone was found with given id
            if(milestone == null){
                throw new NotFoundException("No milestone with that ID exists.");
            }

            //Reaching this indicates no issues have been met and a success message can be returned
            return milestone;
        }
        //Error converting milestone to integer
        catch(NumberFormatException nfe){
            throw new BadRequestException("Milestone ID must be a valid integer");
        }
        //SQLException If the data layer throws an SQLException; throw a custom Internal Server Error
        catch(SQLException ex){
            throw new InternalServerErrorException(ex.getMessage());
        }
    }

    /**
     * Insert a new milestone
     * @param name Name for the new milestone
     * @param description Description for the new milestone
     * @param startDate Start date for the new milestone
     * @param deliveryDate Delivery date for the new milestone
     * @param companyID Company ID of company to assign milestone to
     * @return Inserted Milestone object
     * @throws NotFoundException companyID does not exist in the database
     * @throws BadRequestException One of the parameters was not in the expected format
     * @throws InternalServerErrorException Error in the data layer
     */
    public Milestone insertMilestone(String name, String description, String startDate, String deliveryDate, String companyID) throws NotFoundException, BadRequestException, InternalServerErrorException {
        try{
            //Initial parameter validation; throws BadRequestException if there is an issue
            if(name == null || name.isEmpty()){ throw new BadRequestException("A company name must be provided"); }

            Date parsedStartDate = this.parseDate(startDate);
            Date parsedDeliveryDate = this.parseDate(deliveryDate);


            //Check that company does not already exist in the database
            Company company = companyBusiness.getCompanyByID(companyID);

            //Retrieve the person from the database by UUID
            Date today = new Date();
            int insertedMilestoneID = milestoneDB.insertMilestone(name, description, today, parsedStartDate, parsedDeliveryDate, company.getCompanyID());

            //Reaching this indicates no issues have been met and a success message can be returned
            return new Milestone(insertedMilestoneID, name, description, today, today, parsedStartDate, parsedDeliveryDate, null, false, company);
        }
        catch(NumberFormatException nfe){
            throw new BadRequestException("Company ID must be a valid integer");
        }
        //SQLException If the data layer throws an SQLException; throw a custom Internal Server Error
        catch(SQLException ex){
            throw new InternalServerErrorException(ex.getMessage());
        }
    }

    /**
     * Update an existing milestone
     * @param name Updated name for the milestone; cannot be null
     * @param description Updated description for the new milestone; can be null
     * @param startDate Updated start date for the new milestone; can be null
     * @param deliveryDate Updated delivery date for the new milestone; can be null
     * @param companyID Updated company ID of company to assign milestone to; can be null
     * @return Updated Milestone object
     * @throws NotFoundException companyID does not exist in the database
     * @throws BadRequestException One of the parameters was not in the expected format
     * @throws InternalServerErrorException Error in the data layer
     */
    public Milestone updateMilestone(String milestoneID, String name, String description, String startDate, String deliveryDate, String companyID) throws NotFoundException, BadRequestException, InternalServerErrorException {
        try{
            //Retrieve existing milestone from database
            Milestone existingMilestone = this.getMilestoneByID(milestoneID);

            if(name == null || name.isEmpty()){
                name = existingMilestone.getName();
            }

            if(description == null || description.isEmpty()){
                description = existingMilestone.getDescription();
            }

            //If start date is null, set to existing value; otherwise, validate start date
            Date parsedStartDate;
            if(startDate == null || startDate.isEmpty()){
                parsedStartDate = existingMilestone.getStartDate();
            }
            else {
                parsedStartDate = this.parseDate(startDate);
            }

            //If delivery date is null, set to existing value; otherwise, validate delivery date
            Date parsedDeliveryDate;
            if(deliveryDate == null || deliveryDate.isEmpty()){
                parsedDeliveryDate = existingMilestone.getDeliveryDate();
            }
            else {
                parsedDeliveryDate = this.parseDate(deliveryDate);
            }

            Company company;
            if(companyID == null || companyID.isEmpty()){
                company = existingMilestone.getCompany();
            }
            else {
                company = companyBusiness.getCompanyByID(companyID);
            }

            Date today = new Date();
            milestoneDB.updateMilestone(existingMilestone.getMileStoneID(), name, description, today, parsedStartDate, parsedDeliveryDate, company.getCompanyID());

            //Reaching this indicates no issues have been met and a success message can be returned
            return new Milestone(existingMilestone.getMileStoneID(), name, description, existingMilestone.getCreatedDate(), today, parsedStartDate, parsedDeliveryDate, existingMilestone.getCompletedDate(), existingMilestone.isArchived(), company);
        }
        catch (NumberFormatException nfe) {
            throw new BadRequestException("Company ID must be a valid integer");
        }
        //SQLException If the data layer throws an SQLException; throw a custom Internal Server Error
        catch(SQLException ex){
            throw new InternalServerErrorException(ex.getMessage());
        }
    }

    /**
     * Archive or unarchive an existing milestone
     * @param milestoneID ID of milestone to archive
     * @return Success string
     * @throws NotFoundException Milestone ID does not exist
     * @throws BadRequestException MilestoneID is formatted improperly
     * @throws InternalServerErrorException Error in data layer
     */
    private String updateMilestoneArchiveStatus(String milestoneID, boolean status) throws NotFoundException, BadRequestException, InternalServerErrorException {
        try{

            //Check that milestone exists
            Milestone milestone = this.getMilestoneByID(milestoneID);

            milestoneDB.updateMilestoneArchiveStatus(milestone.getMileStoneID(), status);

            //Reaching this indicates no issues have been met and a success message can be returned
            return (status) ? "Successfully archived milestone." : "Successfully unarchived milestone.";
        }
        //SQLException If the data layer throws an SQLException; throw a custom Internal Server Error
        catch(SQLException ex){
            throw new InternalServerErrorException(ex.getMessage());
        }
    }

    /**
     * Wrapper function of updateMilestoneArchiveStatus to archive a milestone
     * @param milestoneID ID of milestone to archive
     * @return Success string
     */
    public String archiveMilestone(String milestoneID){
        return updateMilestoneArchiveStatus(milestoneID, true);
    }

    /**
     * Wrapper function of updateMilestoneArchiveStatus to unarchive a milestone
     * @param milestoneID ID of milestone to archive
     * @return Success string
     */
    public String unarchiveMilestone(String milestoneID){
        return updateMilestoneArchiveStatus(milestoneID, false);
    }

    /**
     * Delete a person from the database by UUID
     * @param milestoneID ID of milestone to delete
     * @return Success string
     * @throws NotFoundException UUID does not exist in database
     * @throws BadRequestException UUID was either null or invalid integer
     * @throws InternalServerErrorException Error in data layer
     */
    public String deleteMilestoneByID(String milestoneID) throws NotFoundException, BadRequestException, InternalServerErrorException {
        try{
            //Validate milestone ID
            int milestoneIDInteger;
            milestoneIDInteger = Integer.parseInt(milestoneID);


            //Retrieve the person from the database by UUID
            int numRowsDeleted = milestoneDB.deleteMilestoneByID(milestoneIDInteger);

            //If null is returned, no user was found with given UUID
            if(numRowsDeleted == 0){
                throw new NotFoundException("No milestone with that id exists.");
            }

            //Reaching this indicates no issues have been met and a success message can be returned
            return "Successfully deleted milestone.";
        }
        catch(NumberFormatException nfe){
            throw new BadRequestException("Milestone ID must be a valid integer");
        }
        //SQLException If the data layer throws an SQLException; throw a custom Internal Server Error
        catch(SQLException ex){
            throw new InternalServerErrorException(ex.getMessage());
        }
    }

    /**
     * Utility for parsing date objects and detecting that format is valid
     * @param dateString String of date; must be in format 'YYYY-MM-DD'
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
