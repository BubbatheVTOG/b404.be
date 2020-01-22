package b404.businesslayer;

import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

import b404.datalayer.CompanyDB;
import b404.datalayer.PersonDB;
import b404.utility.customexceptions.BadRequestException;
import b404.utility.customexceptions.InternalServerErrorException;
import b404.utility.customexceptions.NotFoundException;
import b404.utility.customexceptions.UnauthorizedException;
import b404.utility.objects.Company;
import b404.utility.objects.Person;
import b404.utility.security.PasswordEncryption;

/**
 * Business layer for functionality related to person
 * Includes login as well as business operations for people
 */
public class PersonBusiness {
    private PersonDB personDB = new PersonDB();
    private CompanyDB companyDB = new CompanyDB();

    /**
     * Checks that a username and password matches an entry in the database
     * @param user - username for login attempt
     * @param password - plaintext password for login attempt
     * @return Person object that matches username and password input
     * @throws UnauthorizedException - if username is not found or password does not match database entry
     * @throws BadRequestException - username or password passed in was empty or null
     * @throws InternalServerErrorException - Error in password encryption or database connectivity process
     */
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

    /**
     * Insert a new person into the database
     * @param username
     * @param password
     * @param email
     * @param title
     * @param companyName
     * @param accessLevelID
     * @return Person object containing inserted data
     * @throws NotFoundException - company name does not exist in the database
     * @throws BadRequestException - paramaters are null, empty or inconvertible into integer
     * @throws InternalServerErrorException - error creating a salt, hashing password or connecting to database
     */
    public Person insertPerson(String username, String password, String email, String title, String companyName, String accessLevelID) throws NotFoundException, BadRequestException, InternalServerErrorException {
        try{
            //Initial parameter validation; throws BadRequestException if there is an issue
            if(username == null || username.isEmpty()){ throw new BadRequestException("A username must be provided"); }
            if(password == null || password.isEmpty()){ throw new BadRequestException("A password must be provided"); }
            if(companyName == null || companyName.isEmpty()){ throw new BadRequestException("A company name must be provided"); }
            int accessLevelIDInteger = Integer.parseInt(accessLevelID);

            //Get company name by using companyID
            Company company = companyDB.getCompanyByName(companyName);
            if(company == null){
                throw new NotFoundException("No company with that name exists.");
            }
            int companyID = company.getCompanyID();

            String salt = PasswordEncryption.getSalt();
            String passwordHash = PasswordEncryption.hash(password, salt);

            //Retrieve the person from the database by userID
            personDB.insertPerson(username, passwordHash, salt, email, title, companyID, accessLevelIDInteger);

            Person person = new Person(0, username, email, passwordHash, salt, title, companyID, accessLevelIDInteger);

            //Reaching this indicates no issues have been met and a success message can be returned
            return person;
        }
        //Catch an error converting parameters to an integer
        catch(NumberFormatException nfe){
            throw new BadRequestException("accessLevelID must be an integer.");
        }
        //SQLException - If the data layer throws an SQLException; throw a custom Internal Server Error
        catch(SQLException ex){
            throw new InternalServerErrorException(ex.getMessage());
        }
    }

    /**
     * Delete a person from the database by userID
     * @param userID String must be convertible to integer
     * @return Success string
     * @throws NotFoundException - UserID does not exist in database
     * @throws BadRequestException - UserID was either null or invalid integer
     * @throws InternalServerErrorException - Error in data layer
     */
    public String deletePersonByUserID(String userID) throws NotFoundException, BadRequestException, InternalServerErrorException {
        try{
            //Initial parameter validation; throws BadRequestException if there is an issue
            if(userID == null){ throw new BadRequestException("A userID must be provided"); }
            int userIDInteger = Integer.parseInt(userID);

            //Retrieve the person from the database by userID
            int numRowsDeleted = personDB.deletePersonByUserID(userIDInteger);

            //If null is returned, no user was found with given userID
            if(numRowsDeleted == 0){
                throw new NotFoundException("No user with that userID exists.");
            }

            //Reaching this indicates no issues have been met and a success message can be returned
            return "Successfully deleted person.";
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
