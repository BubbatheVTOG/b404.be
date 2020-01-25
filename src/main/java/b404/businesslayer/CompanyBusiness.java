package b404.businesslayer;

import b404.datalayer.CompanyDB;
import b404.utility.objects.Company;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotFoundException;
import java.sql.SQLException;

/**
 * Business layer service for company related logic
 * Enforces business rules and leverages datalayer to manipulate database
 */
public class CompanyBusiness {
    private CompanyDB companyDB = new CompanyDB();

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
}
