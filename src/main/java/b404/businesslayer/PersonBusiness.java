package b404.businesslayer;

import b404.datalayer.VentureCreationsDB;
import b404.utility.BadRequestException;
import b404.utility.InternalServerErrorException;
import b404.security.PasswordEncryption;
import b404.utility.Person;

import java.sql.SQLException;

/**
 * Business layer for functionality related to person
 * Includes login as well as business operations for people
 */
public class PersonBusiness {
    private VentureCreationsDB ventureCreationsDB = new VentureCreationsDB();

    public String login(String user, String password) throws BadRequestException, InternalServerErrorException{

        //Initial parameter validation; throws BadRequestException if there is an issue
        if(user.isEmpty() || user == null){ throw new BadRequestException("Invalid username"); }
        if(password.isEmpty() || password == null){ throw new BadRequestException("Invalid password"); }

        try{
            //Retrieve the person from the database by name
            Person person = ventureCreationsDB.getPersonByName(user);

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

        //Reaching this indicates no issues have been met and a success message can be returned
        return "You have logged in!";
    }
}
