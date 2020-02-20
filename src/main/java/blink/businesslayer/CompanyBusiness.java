package blink.businesslayer;

import blink.datalayer.CompanyDB;
import blink.utility.exceptions.ConflictException;
import blink.utility.objects.Company;
import blink.utility.objects.Person;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotFoundException;
import java.sql.SQLException;
import java.util.List;

/**
 * Business layer service for company related logic
 * Enforces business rules and leverages datalayer to manipulate database
 */
public class CompanyBusiness {
    private CompanyDB companyDB;
    private PersonBusiness personBusiness;

    public CompanyBusiness(){
        this.companyDB = new CompanyDB();
        this.personBusiness = new PersonBusiness();
    }

    /**
     * Gets all companies from the database
     * @return Company object containing data from the database\
     * @throws InternalServerErrorException - Error connecting to database or executing query
     */
    public List<Company> getAllCompanies() throws InternalServerErrorException{
        try {
            return companyDB.getAllCompanies();
        }
        catch(SQLException sqle){
            throw new InternalServerErrorException(sqle.getMessage());
        }
    }

    /**
     * Gets a company from the database by companyID
     * @param companyID - companyId to search the database for
     * @return Company object containing data from the database
     * @throws BadRequestException - CompanyID was an invalid integer format
     * @throws NotFoundException - No company with provided CompanyId was found
     * @throws InternalServerErrorException - Error connecting to database or executing query
     */
    public Company getCompanyByID(String companyID) throws BadRequestException, NotFoundException, InternalServerErrorException{
        try {
            Company company = companyDB.getCompanyByID(Integer.parseInt(companyID));

            if(company == null){
                throw new NotFoundException("No company with that id exists");
            }

            return company;
        }
        catch(NumberFormatException nfe){
            throw new BadRequestException("CompanyID must be a valid integer");
        }
        catch(SQLException sqle){
            throw new InternalServerErrorException(sqle.getMessage());
        }
    }

    /**
     * Gets a company from the database by company name
     * @param companyName - Company name to search the database for
     * @return Company object containing company information found in database
     * @throws BadRequestException - Invalid format for company name; null or empty
     * @throws NotFoundException - No company with provided company name exists
     * @throws InternalServerErrorException - Error connecting to database or executing query
     */
    public Company getCompanyByName(String companyName) throws BadRequestException, NotFoundException, InternalServerErrorException{
        try {
            if(companyName == null || companyName.isEmpty()){
                throw new BadRequestException("Invalid company name provided");
            }

            Company company = companyDB.getCompanyByName(companyName);

            if(company == null){
                throw new NotFoundException("No company with that name exists");
            }

            return company;
        }
        catch(SQLException sqle){
            throw new InternalServerErrorException(sqle.getMessage());
        }
    }

    /**
     * Gets all people for a given company from the database
     * @return List of Person objects
     * @throws BadRequestException - CompanyID is not a valid integer
     * @throws NotFoundException - Company does not exist in the database
     * @throws InternalServerErrorException - Error connecting to database or executing query
     */
    public List<Person> getAllPeopleByCompany(String companyID) throws BadRequestException, NotFoundException, InternalServerErrorException{
        try {
            Company storedCompany = this.getCompanyByID(companyID);

            return companyDB.getAllPeopleByCompany(storedCompany.getCompanyID());
        }
        catch(SQLException sqle){
            throw new InternalServerErrorException(sqle.getMessage());
        }
    }

    /**
     * Inserts a new company into the database
     * @param companyName - New company's name
     * @return Success string
     * @throws BadRequestException - CompanyID was an invalid integer format
     * @throws NotFoundException - No company with provided CompanyId was found
     * @throws InternalServerErrorException - Error connecting to database or executing query
     */
    public Company insertCompany(String companyName) throws BadRequestException, ConflictException, NotFoundException, InternalServerErrorException{
        try {
            if(companyName == null || companyName.isEmpty()){ throw new BadRequestException("A company name must be provided.");}

            if(companyDB.getCompanyByName(companyName) != null){
                throw new ConflictException("A company with that name already exists.");
            }

            companyDB.insertCompany(companyName);

            return companyDB.getCompanyByName(companyName);
        }
        catch(SQLException sqle){
            throw new InternalServerErrorException(sqle.getMessage());
        }
    }

    /**
     * Updates an existing company in the database
     * @param companyName - Company's updated name
     * @return Success string
     * @throws BadRequestException - CompanyID was an invalid integer format
     * @throws NotFoundException - No company with provided CompanyId was found
     * @throws InternalServerErrorException - Error connecting to database or executing query
     */
    public Company updateCompany(String companyID, String companyName) throws BadRequestException, NotFoundException, ConflictException, InternalServerErrorException{
        try {
            Company storedCompany = this.getCompanyByID(companyID);

            if(companyName == null || companyName.isEmpty()){
                companyName = storedCompany.getCompanyName();
            }
            else {
                Company companyNameCheck = companyDB.getCompanyByName(companyName);
                if (companyNameCheck != null && companyNameCheck.getCompanyID() != storedCompany.getCompanyID()) {
                    throw new ConflictException("A company with that name already exists.");
                }
            }

            companyDB.updateCompany(storedCompany.getCompanyID(), companyName);

            return new Company(storedCompany.getCompanyID(), companyName);
        }
        catch(NumberFormatException nfe){
            throw new BadRequestException("A company ID must be provided.");
        }
        catch(SQLException sqle){
            throw new InternalServerErrorException(sqle.getMessage());
        }
    }

    /**
     * Deletes a company from the database by companyID
     * @param companyID - companyId to delete from the database
     * @return Success string
     * @throws BadRequestException - CompanyID was an invalid integer format
     * @throws NotFoundException - No company with provided CompanyId was found
     * @throws InternalServerErrorException - Error connecting to database or executing query
     */
    public String deleteCompanyByID(String companyID) throws BadRequestException, NotFoundException, InternalServerErrorException{
        try {
            int numRowsAffected = companyDB.deleteCompany(Integer.parseInt(companyID));

            if(numRowsAffected == 0){
                throw new NotFoundException("No company with that id exists");
            }

            return "Successfully deleted company.";
        }
        catch(NumberFormatException nfe){
            throw new BadRequestException("CompanyID must be a valid integer");
        }
        catch(SQLException sqle){
            throw new InternalServerErrorException(sqle.getMessage());
        }
    }

    /**
     * Adds a person to a company
     * @param companyID - ID of company to add person to
     * @param personID - ID of person to add to company
     * @return Success string
     * @throws BadRequestException - CompanyName was a null or empty
     * @throws NotFoundException - No company with provided Company name was found
     * @throws InternalServerErrorException - Error connecting to database or executing query
     */
    public String addPersonToCompany(String companyID, String personID) throws BadRequestException, NotFoundException, ConflictException, InternalServerErrorException{
        try {
            int companyIDInteger = Integer.parseInt(companyID);
            personID = personBusiness.getPersonByUUID(personID).getUuid();
            if(companyDB.getCompanyByID(companyIDInteger) == null){ throw new NotFoundException("No company with that ID exists. ");}

            for(Person person : this.getAllPeopleByCompany(companyID)){
                if(personID.equals(person.getUuid())) {
                    throw new ConflictException("That person is already a part of that company.");
                }
            }

            companyDB.addPersonToCompany(companyIDInteger, personID);

            return "Successfully added person to company.";
        }
        catch(NumberFormatException nfe){
            throw new BadRequestException("CompanyID must be a valid integer");
        }
        catch(SQLException sqle){
            throw new InternalServerErrorException(sqle.getMessage());
        }
    }

    /**
     * Removes a person from a company
     * @param companyID - ID of company to remove person from
     * @param personID - ID of person to remove from company
     * @return Success string
     * @throws BadRequestException - CompanyName was a null or empty
     * @throws NotFoundException - No company with provided Company name was found
     * @throws InternalServerErrorException - Error connecting to database or executing query
     */
    public String removePersonFromCompany(String companyID, String personID) throws BadRequestException, NotFoundException, InternalServerErrorException{
        try {
            int companyIDInteger = Integer.parseInt(companyID);
            if(companyDB.getCompanyByID(companyIDInteger) == null){ throw new NotFoundException("No company with that ID exists. ");}
            if(personBusiness.getPersonByUUID(personID) == null){ throw new NotFoundException("No person with that ID exists. ");}

            if(companyDB.removePersonFromCompany(companyIDInteger, personID) == 0){
                throw new NotFoundException("That person was not a part of that company");
            }

            return "Successfully removed person from company.";
        }
        catch(NumberFormatException nfe){
            throw new BadRequestException("CompanyID must be a valid integer");
        }
        catch(SQLException sqle){
            throw new InternalServerErrorException(sqle.getMessage());
        }
    }
}
