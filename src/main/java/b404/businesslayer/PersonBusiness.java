package b404.businesslayer;

import b404.datalayer.PersonDB;
import b404.utility.BadRequestException;
import b404.utility.InternalServerErrorException;
import b404.utility.PasswordEncryption;
import b404.utility.Person;

import java.sql.SQLException;

/**
 * Business layer for functionality related to person
 * Includes login as well as business operations for people
 */
public class PersonBusiness {
    PersonDB personDB = new PersonDB();

    public String login(String user, String password) throws BadRequestException, InternalServerErrorException{

        //Initial parameter validation; throws BadRequestException if there is an issue
        if(user == "" || user == null){ throw new BadRequestException("Invalid username"); }
        if(password == "" || password == null){ throw new BadRequestException("Invalid password"); }

        try{
            //Retrieve the person from the database by name
            Person person = personDB.getPersonByName(user);

            //Encrypt password that was passed in and compare to hash stored in database
            //Throw BadRequestException if they do not match
            password = PasswordEncryption.encrypt(password);
            if(!person.getPasswordHash().equals(password)){
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
