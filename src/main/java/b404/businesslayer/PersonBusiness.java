package b404.businesslayer;

import java.sql.SQLException;

import b404.datalayer.PersonDB;
import b404.utility.customexceptions.BadRequestException;
import b404.utility.customexceptions.InternalServerErrorException;
import b404.securitylayer.PasswordEncryption;
import b404.utility.customexceptions.UnauthorizedException;
import b404.utility.objects.Person;
import b404.utility.env.EnvManager;

/**
 * Business layer for functionality related to person
 * Includes login as well as business operations for people
 */
public class PersonBusiness {
    private EnvManager env = new EnvManager();
    private PersonDB personDB = new PersonDB(env);

    public String login(String user, String password) throws UnauthorizedException, BadRequestException, InternalServerErrorException{

        //TODO: This returns a person with password -> password when name -> user for front-end testing; remove once DB connectivity is functional
        if(user.equals("admin")){
            return user + ":" + password + "-> logged in without database connection";
        }

        //Initial parameter validation; throws BadRequestException if there is an issue
        if(user.isEmpty() || user == null){ throw new BadRequestException("Invalid username syntax"); }
        if(password.isEmpty() || password == null){ throw new BadRequestException("Invalid password syntax"); }

        try{
            //Retrieve the person from the database by name
            Person person = personDB.getPersonByName(user);

            //Encrypt password that was passed in and compare to hash stored in database
            //Throw BadRequestException if they do not match

            String encryptedPassword = PasswordEncryption.encrypt(password);

            if(!person.getPasswordHash().equals(encryptedPassword)){
                throw new UnauthorizedException("Invalid login credentials");
            }
        }
        //If the data layer throws an SQLException; throw a custom Internal Server Error
        catch(SQLException sqle){
            throw new InternalServerErrorException("Sorry, could not process your request at this time");
        }

        //Reaching this indicates no issues have been met and a success message can be returned
        return "You have logged in!";
    }
}
