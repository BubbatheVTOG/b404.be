package b404.businesslayer;

import b404.datalayer.AccessLevelDB;
import b404.utility.objects.AccessLevel;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotFoundException;
import java.sql.SQLException;

/**
 * Business layer service for handling access level logic
 */
public class AccessLevelBusiness {
    private AccessLevelDB accessLevelDB = new AccessLevelDB();

    /**
     * Retrieves access level information from the database
     * @param accessLevelID - ID to search database for
     * @return - AccessLevel object containing information from the database
     * @throws NotFoundException - Access Level ID does not exist in the database
     * @throws BadRequestException - Access Level ID is not a valid integer format
     * @throws InternalServerErrorException - Error connecting to database or executing query
     */
    public AccessLevel getAccessLevelByID(String accessLevelID) throws NotFoundException, BadRequestException, InternalServerErrorException{
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
