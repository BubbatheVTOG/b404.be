package b404.businesslayer;

import java.sql.SQLException;

import b404.securitylayer.PasswordEncryption;
import b404.datalayer.VentureCreationsDB;
import b404.utility.BadRequestException;
import b404.utility.InternalServerErrorException;
import b404.utility.Person;
import b404.utility.env.EnvManager;

/**
 * Business layer for functionality related to person
 * Includes login as well as business operations for people
 */
public class PersonBusiness {
    private EnvManager env = new EnvManager();
    private VentureCreationsDB ventureCreationsDB = new VentureCreationsDB(env);

    public String login(String username, String password) throws BadRequestException, InternalServerErrorException{
        //prepare person object for return values
        Person person;

        //Initial parameter validation; throws BadRequestException if there is an issue
        if(username.isEmpty()){ throw new BadRequestException("Invalid username"); }
        if(password.isEmpty()){ throw new BadRequestException("Invalid password"); }

        try{
            //Retrieve the person from the database by name
            person = ventureCreationsDB.getPersonByName(username);

            //Encrypt password that was passed in and compare to hash stored in database
            //Throw BadRequestException if they do not match
            String encryptedPassword = PasswordEncryption.encrypt(password);

            if(!person.getPasswordHash().equals(encryptedPassword)){
                throw new BadRequestException("Invalid login credentials");
            }
        }
        //If the data layer throws an SQLException; throw a custom Internal Server Error
        catch(SQLException sqle){
            throw new InternalServerErrorException("Sorry, could not process your request at this time");
        }

        //Reaching this indicates no issues have been met and the userID is returned in string format
        return String.valueOf(person.getUserID());
    }
}
