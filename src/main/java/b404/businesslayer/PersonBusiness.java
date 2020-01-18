package b404.businesslayer;

import java.sql.SQLException;

import b404.datalayer.PersonDB;
import b404.utility.customexceptions.BadRequestException;
import b404.utility.customexceptions.InternalServerErrorException;
import b404.utility.customexceptions.NotFoundException;
import b404.utility.customexceptions.UnauthorizedException;
import b404.utility.objects.Person;
import b404.utility.security.PasswordEncryption;

import javax.validation.constraints.Null;

/**
 * Business layer for functionality related to person
 * Includes login as well as business operations for people
 */
public class PersonBusiness {
    private PersonDB personDB = new PersonDB();

    public Person login(String user, String password) throws UnauthorizedException, BadRequestException, InternalServerErrorException{

        //Initial parameter validation; throws BadRequestException if there is an issue
        if(user == null || user.isEmpty()){ throw new BadRequestException("Invalid username syntax"); }
        if(password == null || password.isEmpty()){ throw new BadRequestException("Invalid password syntax"); }

        try{
            //Retrieve the person from the database by name
            Person person = personDB.getPersonByName(user);

            if(person == null){
                throw new UnauthorizedException("Invalid login credentials.");
            }

            //Encrypt password that was passed in and compare to hash stored in database
            //Throw UnauthorizedException if they do not match
            String salt = person.getSalt();
            String encryptedPassword = PasswordEncryption.hash(password, salt);

            if(!person.getPasswordHash().equals(encryptedPassword)){
                throw new UnauthorizedException("Invalid login credentials.");
            }

            //Reaching this indicates no issues have been met and a success message can be returned
            return person;
        }
        //SQLException - If the data layer throws an SQLException; throw a custom Internal Server Error
        //ArithmeticException - If the password encryption process fails
        catch(SQLException | ArithmeticException ex){
            throw new InternalServerErrorException(ex.getMessage());
        }
    }

    /**
     * Get a person from the database by userID
     * @param userID String must be convertible to integer
     * @return Person object of person found in database
     * @throws NotFoundException - UserID does not exist in database
     * @throws BadRequestException - UserID was either null or invalid integer
     * @throws InternalServerErrorException - Error in data layer
     */
    public Person getPersonByUserID(String userID) throws NotFoundException, BadRequestException, InternalServerErrorException {
        try{
            //Initial parameter validation; throws BadRequestException if there is an issue
            if(userID == null){ throw new BadRequestException("A userID must be provided"); }

            int userIDInteger = Integer.parseInt(userID);

            //Retrieve the person from the database by userID
            Person person = personDB.getPersonByUserID(userIDInteger);

            //If null is returned, no user was found with given userID
            if(person == null){
                throw new NotFoundException("No user with that userID exists.");
            }

            //Reaching this indicates no issues have been met and a success message can be returned
            return person;
        }
        //Catch an error converting userID to an integer
        catch(NumberFormatException nfe){
            throw new BadRequestException("UserID must be an integer.");
        }
        //SQLException - If the data layer throws an SQLException; throw a custom Internal Server Error
        //ArithmeticException - If the password encryption process fails
        catch(SQLException ex){
            throw new InternalServerErrorException(ex.getMessage());
        }
    }
}
