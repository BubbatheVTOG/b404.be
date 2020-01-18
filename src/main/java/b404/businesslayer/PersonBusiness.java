package b404.businesslayer;

import java.sql.SQLException;

import b404.datalayer.PersonDB;
import b404.utility.customexceptions.BadRequestException;
import b404.utility.customexceptions.InternalServerErrorException;
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

            //Check that username was found in the database
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

    public Person getPersonByUserID(String userID) throws UnauthorizedException, BadRequestException, InternalServerErrorException {
        return new Person();
    }
    /*
        //Initial parameter validation; throws BadRequestException if there is an issue
        if(userID == null || user.isEmpty()){ throw new BadRequestException("Invalid username syntax"); }

        try{
            //Retrieve the person from the database by name
            person = personDB.getPersonByName(userID);

            if(person == null){
                throw new UnauthorizedException("Invalid login credentials.");
            }

            //Check that username was found in the database
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
        }
        //SQLException - If the data layer throws an SQLException; throw a custom Internal Server Error
        //ArithmeticException - If the password encryption process fails
        catch(SQLException | ArithmeticException ex){
            throw new InternalServerErrorException(ex.getMessage());
        }

        //Reaching this indicates no issues have been met and a success message can be returned
        return person;
    }
    */
}
