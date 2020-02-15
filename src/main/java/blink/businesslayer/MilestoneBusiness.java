package blink.businesslayer;

import blink.datalayer.MilestoneDB;
import blink.utility.objects.Milestone;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotFoundException;
import java.sql.SQLException;
import java.util.List;

public class MilestoneBusiness {
    private MilestoneDB milestoneDB;

    public MilestoneBusiness(){
        this.milestoneDB = new MilestoneDB();
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
}
