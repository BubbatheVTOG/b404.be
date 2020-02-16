package blink.businesslayer;

import blink.datalayer.CompanyDB;
import blink.datalayer.MilestoneDB;
import blink.utility.objects.Milestone;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotFoundException;
import java.util.Date;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

public class MilestoneBusiness {
    private MilestoneDB milestoneDB;
    private CompanyDB companyDB;
    private SimpleDateFormat dateParser;

    public MilestoneBusiness(){
        this.milestoneDB = new MilestoneDB();
        this.companyDB = new CompanyDB();
        this.dateParser = new SimpleDateFormat("YYYY-MM-DD");
    }


    /**
     * Get all active milestones
     * @return List of active milestones
     * @throws InternalServerErrorException - Error in data layer
     */
    public List<Milestone> getAllMilestones() throws InternalServerErrorException {
        try{
            return milestoneDB.getAllMilestones();
        }
        //SQLException - If the data layer throws an SQLException; throw a custom Internal Server Error
        catch(SQLException ex){
            throw new InternalServerErrorException(ex.getMessage());
        }
    }

    /**
     * Get all active milestones
     * @return List of active milestones
     * @throws InternalServerErrorException - Error in data layer
     */
    public List<Milestone> getActiveMilestones() throws InternalServerErrorException {
        try{
            return milestoneDB.getAllMilestones(0);
        }
        //SQLException - If the data layer throws an SQLException; throw a custom Internal Server Error
        catch(SQLException ex){
            throw new InternalServerErrorException(ex.getMessage());
        }
    }

    /**
     * Get all archived milestones
     * @return List of archived milestones
     * @throws InternalServerErrorException - Error in data layer
     */
    public List<Milestone> getArchivedMilestones() throws InternalServerErrorException {
        try{
            return milestoneDB.getAllMilestones(1);
        }
        //SQLException - If the data layer throws an SQLException; throw a custom Internal Server Error
        catch(SQLException ex){
            throw new InternalServerErrorException(ex.getMessage());
        }
    }

    /**
     * Get a milestone from the database by milestoneID
     * @param id - milestoneID must be convertible to integer
     * @return Milestone object with matching id
     * @throws NotFoundException - MilestoneID does not exist in database
     * @throws BadRequestException - MilestoneID was either null or invalid integer
     * @throws InternalServerErrorException - Error in data layer
     */
    public Milestone getMilestoneByID(String id) throws NotFoundException, BadRequestException, InternalServerErrorException {
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
        //SQLException - If the data layer throws an SQLException; throw a custom Internal Server Error
        catch(SQLException ex){
            throw new InternalServerErrorException(ex.getMessage());
        }
    }

    /**
     * Insert a new milestone
     * @param name - Name for the new milestone
     * @param description - Description for the new milestone
     * @param startDate - Start date for the new milestone
     * @param deliveryDate - Delivery date for the new milestone
     * @param companyID - Company ID of company to assign milestone to
     * @return Inserted Milestone object
     * @throws NotFoundException - companyID does not exist in the database
     * @throws BadRequestException - One of the parameters was not in the expected format
     * @throws InternalServerErrorException - Error in the data layer
     */
    public Milestone insertMilestone(String name, String description, String startDate, String deliveryDate, String companyID) throws NotFoundException, BadRequestException, InternalServerErrorException {
        try{
            //Initial parameter validation; throws BadRequestException if there is an issue
            if(name == null || name.isEmpty()){ throw new BadRequestException("A company name must be provided"); }

            Date parsedStartDate = this.validateDate(startDate);
            if(parsedStartDate == null){throw new BadRequestException("Start date is an invalid format.");}
            Date parsedDeliveryDate = this.validateDate(deliveryDate);
            if(parsedDeliveryDate == null){throw new BadRequestException("Delivery date is an invalid format.");}

            int companyIDInteger;
            try{companyIDInteger = Integer.parseInt(companyID);}
            catch(NumberFormatException nfe){throw new BadRequestException("Company ID must be a valid integer");}

            //Check that company does not already exist in the database
            if(companyDB.getCompanyByID(companyIDInteger) == null){
                throw new NotFoundException("No company with that companyID exists.");
            }

            //Retrieve the person from the database by UUID
            Date today = new Date();
            int insertedMilestoneID = milestoneDB.insertMilestone(name, description, today, parsedStartDate, parsedDeliveryDate, companyIDInteger);

            //Reaching this indicates no issues have been met and a success message can be returned
            return new Milestone(insertedMilestoneID, name, description, today, today, parsedStartDate, parsedDeliveryDate, null, false, companyIDInteger);
        }
        //SQLException - If the data layer throws an SQLException; throw a custom Internal Server Error
        catch(SQLException ex){
            throw new InternalServerErrorException(ex.getMessage());
        }
    }

    /**
     * Update an existing milestone
     * @param name - Updated name for the milestone; cannot be null
     * @param description - Updated description for the new milestone; can be null
     * @param startDate - Updated start date for the new milestone; can be null
     * @param deliveryDate - Updated delivery date for the new milestone; can be null
     * @param companyID - Updated company ID of company to assign milestone to; can be null
     * @return Updated Milestone object
     * @throws NotFoundException - companyID does not exist in the database
     * @throws BadRequestException - One of the parameters was not in the expected format
     * @throws InternalServerErrorException - Error in the data layer
     */
    public Milestone updateMilestone(String milestoneID, String name, String description, String startDate, String deliveryDate, String companyID) throws NotFoundException, BadRequestException, InternalServerErrorException {
        try{
            //Retrieve existing milestone from database
            Milestone existingMilestone = this.getMilestoneByID(milestoneID);

            if(name == null || name.isEmpty()){ name = existingMilestone.getName(); }

            //If start date is null, set to existing value; otherwise, validate start date
            Date parsedStartDate;
            if(startDate == null || startDate.isEmpty()){
                parsedStartDate = existingMilestone.getStartDate();
            }
            else {
                parsedStartDate = this.validateDate(startDate);
                if (parsedStartDate == null) {
                    throw new BadRequestException("Start date is an invalid format.");
                }
            }

            //If delivery date is null, set to existing value; otherwise, validate delivery date
            Date parsedDeliveryDate;
            if(deliveryDate == null || deliveryDate.isEmpty()){
                parsedDeliveryDate = existingMilestone.getDeliveryDate();
            }
            else {
                parsedDeliveryDate = this.validateDate(startDate);
                if (parsedDeliveryDate == null) {
                    throw new BadRequestException("Start date is an invalid format.");
                }
            }

            int companyIDInteger;
            if(companyID == null || companyID.isEmpty()){
                companyIDInteger = existingMilestone.getCompanyID();
            }
            else {
                try {
                    companyIDInteger = Integer.parseInt(companyID);
                } catch (NumberFormatException nfe) {
                    throw new BadRequestException("Company ID must be a valid integer");
                }
            }

            //Check that company exists in the database
            if(companyDB.getCompanyByID(companyIDInteger) == null){
                throw new NotFoundException("No company with that companyID exists.");
            }

            //Retrieve the person from the database by UUID
            Date today = new Date();
            milestoneDB.updateMilestone(existingMilestone.getMileStoneID(), name, description, today, parsedStartDate, parsedDeliveryDate, companyIDInteger);

            //Reaching this indicates no issues have been met and a success message can be returned
            return new Milestone(existingMilestone.getMileStoneID(), name, description, today, today, parsedStartDate, parsedDeliveryDate, null, false, companyIDInteger);
        }
        //SQLException - If the data layer throws an SQLException; throw a custom Internal Server Error
        catch(SQLException ex){
            throw new InternalServerErrorException(ex.getMessage());
        }
    }

    /**
     * Archive or unarchive an existing milestone
     * @param milestoneID - ID of milestone to archive
     * @return Success string
     * @throws NotFoundException - Milestone ID does not exist
     * @throws BadRequestException - MilestoneID is formatted improperly
     * @throws InternalServerErrorException - Error in data layer
     */
    public String updateMilestoneArchiveStatus(String milestoneID, boolean status) throws NotFoundException, BadRequestException, InternalServerErrorException {
        try{

            //Check that milestone exists
            Milestone milestone = this.getMilestoneByID(milestoneID);
            if(milestone == null){
                throw new NotFoundException("No milestone with that ID exists");
            }

            milestoneDB.updateMilestoneArchiveStatus(milestone.getMileStoneID(), status);

            //Reaching this indicates no issues have been met and a success message can be returned
            return (status) ? "Successfully archived milestone." : "Successfully unarchived milestone.";
        }
        //SQLException - If the data layer throws an SQLException; throw a custom Internal Server Error
        catch(SQLException ex){
            throw new InternalServerErrorException(ex.getMessage());
        }
    }

    /**
     * Delete a person from the database by UUID
     * @param milestoneID - ID of milestone to delete
     * @return Success string
     * @throws NotFoundException - UUID does not exist in database
     * @throws BadRequestException - UUID was either null or invalid integer
     * @throws InternalServerErrorException - Error in data layer
     */
    public String deleteMilestoneByID(String milestoneID) throws NotFoundException, BadRequestException, InternalServerErrorException {
        try{
            //Validate milestone ID
            int milestoneIDInteger;
            try{milestoneIDInteger = Integer.parseInt(milestoneID);}
            catch(NumberFormatException nfe){throw new BadRequestException("Milestone ID must be a valid integer");}

            //Retrieve the person from the database by UUID
            int numRowsDeleted = milestoneDB.deleteMilestoneByID(milestoneIDInteger);

            //If null is returned, no user was found with given UUID
            if(numRowsDeleted == 0){
                throw new NotFoundException("No milestone with that id exists.");
            }

            //Reaching this indicates no issues have been met and a success message can be returned
            return "Successfully deleted milestone.";
        }
        //SQLException - If the data layer throws an SQLException; throw a custom Internal Server Error
        catch(SQLException ex){
            throw new InternalServerErrorException(ex.getMessage());
        }
    }

    private Date validateDate(String dateString){
        try{
            return dateParser.parse(dateString);
        }
        catch(ParseException pe){
            return null;
        }
    }
}
