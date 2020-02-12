package blink.businesslayer;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import blink.datalayer.PersonDB;

import javax.ws.rs.*;

import blink.utility.ConflictException;
import blink.utility.objects.AccessLevel;
import blink.utility.objects.Person;
import blink.utility.security.PasswordEncryption;

/**
 * Business layer for functionality related to person
 * Includes login as well as business operations for people
 */
public class PersonBusiness {
    private PersonDB personDB = new PersonDB();
    private AccessLevelBusiness accessLevelBusiness = new AccessLevelBusiness();

    /**
     * Checks that a username and password matches an entry in the database
     * @param user - username for login attempt
     * @param password - plaintext password for login attempt
     * @return Person object that matches username and password input
     * @throws NotAuthorizedException - if username is not found or password does not match database entry
     * @throws BadRequestException - username or password passed in was empty or null
     * @throws InternalServerErrorException - Error in password encryption or database connectivity process
     */
    public Person login(String user, String password) throws NotAuthorizedException, BadRequestException, InternalServerErrorException{
        //Initial parameter validation; throws BadRequestException if there is an issue
        if(user == null || user.isEmpty()){ throw new BadRequestException("Invalid username syntax"); }
        if(password == null || password.isEmpty()){ throw new BadRequestException("Invalid password syntax"); }

        try{
            //Retrieve the person from the database by name
            Person person = personDB.getPersonByUsername(user);

            if(person == null){
                throw new NotAuthorizedException("Invalid login credentials.");
            }
          
            //Encrypt password that was passed in and compare to hash stored in database
            //Throw UnauthorizedException if they do not match
            String encryptedPassword = PasswordEncryption.hash(password, person.getSalt());

            if(!person.getPasswordHash().equals(encryptedPassword)){
                throw new NotAuthorizedException("Invalid login credentials.");
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
     * Get all people from the database
     * @return ArrayList of people found in database
     * @throws InternalServerErrorException - Error in data layer
     */
    public List<Person> getAllPeople() throws InternalServerErrorException {
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
     * @param uuid - String must be convertible to integer
     * @return Person object of person found in database
     * @throws NotFoundException - UUID does not exist in database
     * @throws BadRequestException - UUID was either null or invalid integer
     * @throws InternalServerErrorException - Error in data layer
     */
    public Person getPersonByUUID(String uuid) throws NotFoundException, BadRequestException, InternalServerErrorException {
        try{
            //Initial parameter validation; throws BadRequestException if there is an issue
            if(uuid == null || uuid.isEmpty()){ throw new BadRequestException("A user ID must be provided"); }

            //Retrieve the person from the database by String
            Person person = personDB.getPersonByUUID(uuid);

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
     * @param fName - new person's first name
     * @param lName - new person's last name
     * @param email - new person's email; can be null
     * @param title - new person's title; can be null
     * @param accessLevelID - new person's accessLevelID
     * @return Person object containing inserted data
     * @throws NotFoundException - company name does not exist in the database
     * @throws BadRequestException - paramaters are null, empty or inconvertible into integer
     * @throws InternalServerErrorException - error creating a salt, hashing password or connecting to database
     */
    public Person insertPerson(String username, String password, String fName, String lName, String email, String title, String accessLevelID) throws ConflictException, NotFoundException, BadRequestException, InternalServerErrorException {
        try{
            //Initial parameter validation; throws BadRequestException if there is an issue
            if(username == null || username.isEmpty()){ throw new BadRequestException("A username must be provided"); }
            if(password == null || password.isEmpty()){ throw new BadRequestException("A password must be provided"); }
            if(fName == null || fName.isEmpty()){ throw new BadRequestException("A first name must be provided"); }
            if(lName == null || lName.isEmpty()){ throw new BadRequestException("A last name must be provided"); }

            //Generate a new UUID for the new person
            String uuid = UUID.randomUUID().toString();

            //Check that username does not already exist in the database
            if(personDB.getPersonByUsername(username) != null){
                throw new ConflictException("A user with that username already exists.");
            }

            //Ensure that accessLevel exists in database; accessLevelBusiness will throw relevant custom exceptions
            AccessLevel accessLevel = accessLevelBusiness.getAccessLevelByID(accessLevelID);

            //Get new salt and hash password with new salt
            String salt = PasswordEncryption.getSalt();
            String passwordHash = PasswordEncryption.hash(password, salt);

            //Retrieve the person from the database by UUID
            personDB.insertPerson(uuid, username, passwordHash, salt, fName, lName, email, title, accessLevel.getAccessLevelID());

            //Reaching this indicates no issues have been met and a success message can be returned
            return new Person(uuid, username, passwordHash, salt, fName, lName, email, title, accessLevel.getAccessLevelID());
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
     * @param fName - updated first name; can be null
     * @param lName - updated last name; can be null
     * @param email - updated email; can be null
     * @param title - updated title; can be null
     * @param accessLevelID - updated accessLevelID; can be null
     * @return Person object containing updated data
     * @throws NotFoundException - company name does not exist in the database
     * @throws BadRequestException - paramaters are null, empty or inconvertible into integer
     * @throws InternalServerErrorException - error creating a salt, hashing password or connecting to database
     */
    public Person updatePerson(String uuid, String username, String password, String fName, String lName, String email, String title, String accessLevelID) throws ConflictException, NotFoundException, BadRequestException, InternalServerErrorException {
        try{
            if(uuid == null || uuid.isEmpty()) { throw new BadRequestException("Must provide a valid UUID for updating a person."); }

            Person person = personDB.getPersonByUUID(uuid);
            if(person == null){
                throw new NotFoundException("No user with that id exists.");
            }

            //Check if new username is unique or use old username if new username is null
            if(username == null || username.isEmpty()){
                username = person.getUsername();
            }
            else{
                Person usernameCheck = personDB.getPersonByUsername(username);
                //If username was found and person is not the same as person to update throw conflict
                if(usernameCheck != null && !usernameCheck.getUUID().equals(person.getUUID())){
                    throw new ConflictException("A user with that username already exists.");
                }
            }

            //Hash new password or use old password if new password is null
            if(password == null || password.isEmpty()){ password = person.getPasswordHash(); }
            else{ password = PasswordEncryption.hash(password, person.getSalt()); }

            //Alter less impactful data
            if(fName == null || fName.isEmpty()){ fName = person.getFName(); }
            if(lName == null || lName.isEmpty()){ lName = person.getLName(); }
            if(email == null || email.isEmpty()){ email = person.getEmail(); }
            if(title == null || title.isEmpty()){ title = person.getTitle(); }

            //Set accessLevelIDInteger to pre-existing if null or get ID from database if not null
            //AccessLevelBusiness handles exceptions with invalid accessLevels
            int accessLevelIDInteger = person.getAccessLevelID();
            if(accessLevelID != null && !accessLevelID.isEmpty()) {
                accessLevelIDInteger = accessLevelBusiness.getAccessLevelByID(accessLevelID).getAccessLevelID();
            }

            //Retrieve the person from the database by UUID
            personDB.updatePerson(uuid, username, password, fName, lName, email, title, accessLevelIDInteger);

            //Reaching this indicates no issues have been met and a success message can be returned
            return new Person(uuid, username, password, person.getSalt(), fName, lName, email, title, accessLevelIDInteger);
        }
        //SQLException - If the data layer throws an SQLException; throw a custom Internal Server Error
        catch(SQLException ex){
            throw new InternalServerErrorException(ex.getMessage());
        }
    }

    /**
     * Delete a person from the database by UUID
     * @param uuid String must be convertible to integer
     * @return Success string
     * @throws NotFoundException - UUID does not exist in database
     * @throws BadRequestException - UUID was either null or invalid integer
     * @throws InternalServerErrorException - Error in data layer
     */
    public String deletePersonByUUID(String uuid) throws NotFoundException, BadRequestException, InternalServerErrorException {
        try{
            //Initial parameter validation; throws BadRequestException if there is an issue
            if(uuid == null || uuid.isEmpty()){ throw new BadRequestException("A user ID must be provided"); }

            //Retrieve the person from the database by UUID
            int numRowsDeleted = personDB.deletePersonByUUID(uuid);

            //If null is returned, no user was found with given UUID
            if(numRowsDeleted == 0){
                throw new NotFoundException("No user with that id exists.");
            }

            //Reaching this indicates no issues have been met and a success message can be returned
            return "Successfully deleted person.";
        }
        //SQLException - If the data layer throws an SQLException; throw a custom Internal Server Error
        //ArithmeticException - If the password encryption process fails
        catch(SQLException ex){
            throw new InternalServerErrorException(ex.getMessage());
        }
    }
}