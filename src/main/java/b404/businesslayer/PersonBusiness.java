package b404.businesslayer;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;

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
     * Get a person from the database by UUID
     * @return ArrayList of people object of person found in database
     * @throws InternalServerErrorException - Error in data layer
     */
    public ArrayList<Person> getAllPeople() throws InternalServerErrorException {
        try{
            //Return response from getAllPeople process
            return personDB.getAllPeople();
        }
        //SQLException - If the data layer throws an SQLException; throw a custom Internal Server Error
        catch(SQLException ex){
            throw new InternalServerErrorException(ex.getMessage());
        }
    }

    /**
     * Get a person from the database by UUID
     * @param UUID - String must be convertible to integer
     * @return Person object of person found in database
     * @throws NotFoundException - UUID does not exist in database
     * @throws BadRequestException - UUID was either null or invalid integer
     * @throws InternalServerErrorException - Error in data layer
     */
    public Person getPersonByUUID(String UUID) throws NotFoundException, BadRequestException, InternalServerErrorException {
        try{
            //Initial parameter validation; throws BadRequestException if there is an issue
            if(UUID == null || UUID.isEmpty()){ throw new BadRequestException("A user ID must be provided"); }

            //Retrieve the person from the database by String
            Person person = personDB.getPersonByUUID(UUID);

            //If null is returned, no user was found with given UUID
            if(person == null){
                throw new NotFoundException("No user with that UUID exists.");
            }

            //Reaching this indicates no issues have been met and a success message can be returned
            return person;
        }
        //SQLException - If the data layer throws an SQLException; throw a custom Internal Server Error
        catch(SQLException ex){
            throw new InternalServerErrorException(ex.getMessage());
        }
    }

    /**
     * Insert a new person into the database
     * @param username - new person's username
     * @param password - new person's password
     * @param email - new person's email; can be null
     * @param title - new person's title; can be null
     * @param companyName - new person's companyName
     * @param accessLevelID - new person's accessLevelID
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
            if(accessLevelID == null){ throw new BadRequestException("An accessLevelID name must be provided"); }
            int accessLevelIDInteger = Integer.parseInt(accessLevelID);

            //Generate a new UUID for the new person
            String uuid = UUID.randomUUID().toString();

            //Get company name by using companyID
            Company company = companyDB.getCompanyByName(companyName);
            if(company == null){
                throw new NotFoundException("No company with that name exists.");
            }
            int companyID = company.getCompanyID();

            //Get new salt and hash password with new salt
            String salt = PasswordEncryption.getSalt();
            String passwordHash = PasswordEncryption.hash(password, salt);

            //Retrieve the person from the database by UUID
            personDB.insertPerson(uuid, username, passwordHash, salt, email, title, companyID, accessLevelIDInteger);

            //Reaching this indicates no issues have been met and a success message can be returned
            return new Person(uuid, username, passwordHash, salt, email, title, companyID, accessLevelIDInteger);
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
     * Update an existing person in the database
     * @param username - updated username; can be null
     * @param password - updated password; can be null
     * @param email - updated email; can be null
     * @param title - updated title; can be null
     * @param companyName - updated companyName; can be null
     * @param accessLevelID - updated accessLevelID; can be null
     * @return Person object containing updated data
     * @throws NotFoundException - company name does not exist in the database
     * @throws BadRequestException - paramaters are null, empty or inconvertible into integer
     * @throws InternalServerErrorException - error creating a salt, hashing password or connecting to database
     */
    public Person updatePerson(String UUID, String username, String password, String email, String title, String companyName, String accessLevelID) throws NotFoundException, BadRequestException, InternalServerErrorException {
        try{
            Person person = personDB.getPersonByUUID(UUID);
            if(person == null){
                throw new NotFoundException("No user with that id exists.");
            }

            //Initial parameter validation; Sets value to value from database if not passed in
            if(username == null || username.isEmpty()){ username = person.getName(); }
            if(password == null || password.isEmpty()){ password = PasswordEncryption.hash(password, person.getSalt()); }
            if(email == null || email.isEmpty()){ email = person.getEmail(); }
            if(title == null || title.isEmpty()){ title = person.getTitle(); }

            int companyID = person.getCompanyID();
            if(companyName != null && !companyName.isEmpty()){
                Company company = companyDB.getCompanyByName(companyName);
                if(company == null){
                    throw new NotFoundException("No company with that name exists.");
                }
                companyID = company.getCompanyID();
            }

            int accessLevelIDInteger = person.getAccessLevelID();
            if(accessLevelID != null){
                accessLevelIDInteger = Integer.parseInt(accessLevelID);
            }

            //Retrieve the person from the database by UUID
            personDB.updatePerson(UUID, username, password, person.getSalt(), email, title, companyID, accessLevelIDInteger);

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
     * Delete a person from the database by UUID
     * @param UUID String must be convertible to integer
     * @return Success string
     * @throws NotFoundException - UUID does not exist in database
     * @throws BadRequestException - UUID was either null or invalid integer
     * @throws InternalServerErrorException - Error in data layer
     */
    public String deletePersonByUUID(String UUID) throws NotFoundException, BadRequestException, InternalServerErrorException {
        try{
            //Initial parameter validation; throws BadRequestException if there is an issue
            if(UUID == null || UUID.isEmpty()){ throw new BadRequestException("A UUID must be provided"); }

            //Retrieve the person from the database by UUID
            int numRowsDeleted = personDB.deletePersonByUUID(UUID);

            //If null is returned, no user was found with given UUID
            if(numRowsDeleted == 0){
                throw new NotFoundException("No user with that id exists.");
            }

            //Reaching this indicates no issues have been met and a success message can be returned
            return "Successfully deleted person.";
        }
        //Catch an error converting UUID to an integer
        catch(NumberFormatException nfe){
            throw new BadRequestException("UUID must be an integer.");
        }
        //SQLException - If the data layer throws an SQLException; throw a custom Internal Server Error
        //ArithmeticException - If the password encryption process fails
        catch(SQLException ex){
            throw new InternalServerErrorException(ex.getMessage());
        }
    }
}
