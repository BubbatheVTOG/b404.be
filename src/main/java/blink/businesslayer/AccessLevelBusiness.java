package blink.businesslayer;

import blink.datalayer.AccessLevelDB;
import blink.utility.objects.AccessLevel;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotFoundException;
import java.sql.SQLException;

/**
 * Business layer service for handling access level logic
 */
class AccessLevelBusiness {
    private AccessLevelDB accessLevelDB;

    AccessLevelBusiness(){
        this.accessLevelDB = new AccessLevelDB();
    }

    /**
     * Retrieves access level information from the database
     * @param accessLevelID - ID to search database for
     * @return - AccessLevel object containing information from the database
     * @throws NotFoundException - Access Level ID does not exist in the database
     * @throws BadRequestException - Access Level ID is not a valid integer format
     * @throws InternalServerErrorException - Error connecting to database or executing query
     */
    AccessLevel getAccessLevelByID(String accessLevelID){
        try{
            AccessLevel accessLevel = accessLevelDB.getAccessLevel(Integer.parseInt(accessLevelID));

            if(accessLevel == null){
                throw new NotFoundException("Access Level does not exist");
            }

            return accessLevel;
        }
        catch(NumberFormatException nfe){
            throw new BadRequestException("AccessLevelID must be a valid integer");
        }
        catch(SQLException sqle){
            throw new InternalServerErrorException(sqle.getMessage());
        }
    }
}
