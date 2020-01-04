package main.java.b404.businesslayer;

import main.java.b404.datalayer.PersonDB;
import main.java.b404.utility.*;

import java.sql.SQLException;

/**
 * Business layer for functionality related to person
 * Includes login as well as business operations for people
 */
public class PersonBusiness {
    PersonDB personDB = new PersonDB();

    public String login(String user, String password) throws BadRequestException, InternalServerErrorException{
        if(user == "" || user == null){ throw new BadRequestException("Invalid username"); }
        if(password == "" || password == null){ throw new BadRequestException("Invalid password"); }

        try{
            Person person = personDB.getPersonByName(user);

            password = PasswordEncryption.encrypt(password);

            if(!person.getPasswordHash().equals(password)){
                throw new BadRequestException("Invalid login credentials");
            }
        }
        catch(SQLException sqle){
            throw new InternalServerErrorException("Sorry, could not process your request at this time");
        }

        return "You have logged in!";
    }
}
