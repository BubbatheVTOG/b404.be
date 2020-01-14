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

        //Initial parameter validation; throws BadRequestException if there is an issue
        if(user == null || user.isEmpty()){ throw new BadRequestException("Invalid username syntax"); }
        if(password == null || password.isEmpty()){ throw new BadRequestException("Invalid password syntax"); }

        //TODO: This returns a person with password -> password when name -> user for front-end testing; remove once DB connectivity is functional
        if(user.equals("admin")){
            return user + ":" + password + "-> logged in without database connection";
        }

        try{
            //Retrieve the person from the database by name
            Person person = personDB.getPersonByName(user);

            //Encrypt password that was passed in and compare to hash stored in database
            //Throw BadRequestException if they do not match
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
        return "You have logged in!";
    }
}
