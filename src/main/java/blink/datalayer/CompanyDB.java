package blink.datalayer;

import blink.utility.objects.Company;
import blink.utility.objects.Person;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CompanyDB {
    private DBConn dbConn;
    private PersonDB personDB;

    public CompanyDB(){
        this.dbConn = new DBConn();
        this.personDB = new PersonDB();
    }

    /**
     * Connect to database and retrieve all content of person table
     * @return ArrayList of all Company objects in database
     * @throws SQLException - Error connecting to database or executing query
     */
    public List<Company> getAllCompanies() throws SQLException {
        this.dbConn.connect();

        //Prepare sql statement
        String query = "SELECT * FROM company;";

        try (PreparedStatement preparedStatement = this.dbConn.conn.prepareStatement(query)) {

            //Set parameters and execute query
            try (ResultSet result = preparedStatement.executeQuery()) {

                ArrayList<Company> companyList = new ArrayList<>();

                while (result.next()) {
                    companyList.add(new Company(result.getInt("companyID"),
                            result.getString("name")));
                }

                return companyList;
            }
        }
    }

    /**
     * Connect to database and retrieve all people associated with a specific company
     * @return ArrayList of all Company objects in database
     * @throws SQLException - Error connecting to database or executing query
     */
    public List<Person> getAllPeopleByCompany(final int companyID) throws SQLException {
        this.dbConn.connect();

        //Prepare sql statement
        String query = "SELECT * FROM personCompany WHERE companyID = ?;";

        try (PreparedStatement preparedStatement = this.dbConn.conn.prepareStatement(query)) {
            preparedStatement.setInt(1, companyID);

            //Set parameters and execute query
            try (ResultSet result = preparedStatement.executeQuery()) {

                ArrayList<Person> personList = new ArrayList<>();

                while (result.next()) {
                    personList.add(personDB.getPersonByUUID(result.getString("UUID")));
                }

                return personList;
            }
        }
    }

    /**
     * Get a companies information by companyID
     * @param companyID - companyID to search database for
     * @return company object or null if not found
     * @throws SQLException - error connecting to database or executing query
     */
    public Company getCompanyByID(final int companyID) throws SQLException {
        this.dbConn.connect();

        //Prepare sql statement
        String query = "SELECT * FROM company WHERE company.companyID = ?";

        try (PreparedStatement preparedStatement = this.dbConn.conn.prepareStatement(query)) {

            //Set parameters and execute query
            preparedStatement.setInt(1, companyID);

            try (ResultSet result = preparedStatement.executeQuery()) {

                Company company = null;

                while (result.next()) {

                    //Pull response content and map into a Person object
                    company = new Company(result.getInt("companyID"),
                            result.getString("name"));
                }

                return company;
            }
        }
    }

    /**
     * Gets a company from the database by company name
     * @param companyName - name to search database for
     * @return - Company with matching Company Name
     * @throws SQLException - Error connecting to database or executing query
     */
    public Company getCompanyByName(final String companyName) throws SQLException {
        this.dbConn.connect();

        //Prepare sql statement
        String query = "SELECT * FROM company WHERE company.name = ?";

        try (PreparedStatement preparedStatement = this.dbConn.conn.prepareStatement(query)) {

            //Set parameters and execute query
            preparedStatement.setString(1, companyName);

            try (ResultSet result = preparedStatement.executeQuery()) {

                Company company = null;

                while (result.next()) {

                    //Pull response content and map into a Person object
                    company = new Company(result.getInt("companyID"),
                            result.getString("name"));
                }

                return company;
            }
        }
    }

    /**
     * Connect to database and add
     * @param name - name of new company to be added
     * @throws SQLException - error connecting to database or executing
     */
    public void insertCompany(final String name) throws SQLException {
        this.dbConn.connect();

        //Prepare sql statement
        String query = "INSERT INTO company (name) VALUES (?);";

        try (PreparedStatement preparedStatement = this.dbConn.conn.prepareStatement(query)) {

            //Set parameters and execute query
            preparedStatement.setString(1, name);

            preparedStatement.executeUpdate();
        }
    }

    /**
     * Connect to database and add
     * @param name - name of new company to be added
     * @throws SQLException - error connecting to database or executing
     */
    public void updateCompany(final int companyID, final String name) throws SQLException {
        this.dbConn.connect();

        //Prepare sql statement
        String query = "UPDATE company SET company.name = ? WHERE company.companyID = ?;";

        try (PreparedStatement preparedStatement = this.dbConn.conn.prepareStatement(query)) {

            //Set parameters and execute query
            preparedStatement.setString(1, name);
            preparedStatement.setInt(2, companyID);

            preparedStatement.executeUpdate();
        }
    }

    /**
     * Connect to database and delete a company by companyID
     * @param companyID - comapanyID of company to delete from the database
     * @return number of rows deleted
     * @throws SQLException - Error connecting to database or executing query
     */
    public int deleteCompany(final int companyID) throws SQLException {
        this.dbConn.connect();

        //Prepare sql statement
        String query = "DELETE FROM company WHERE company.companyID = ?;";

        try (PreparedStatement preparedStatement = this.dbConn.conn.prepareStatement(query)) {

            //Set parameters and execute query
            preparedStatement.setInt(1, companyID);
            return preparedStatement.executeUpdate();
        }
    }

    /**
     * Connect to database and delete a company by company name
     * @param companyName - company name of company to delete from the database
     * @return number of rows deleted
     * @throws SQLException - Error connecting to database or executing query
     */
    public int deleteCompany(final String companyName) throws SQLException {
        this.dbConn.connect();

        //Prepare sql statement
        String query = "DELETE FROM company WHERE company.name = ?;";

        try (PreparedStatement preparedStatement = this.dbConn.conn.prepareStatement(query)) {

            //Set parameters and execute query
            preparedStatement.setString(1, companyName);
            return preparedStatement.executeUpdate();
        }
    }

    /**
     * Add a person to a company by adding a row into personCompany
     * @param companyID - companyID of company to add person to
     * @param UUID - UUID of person to add to company
     * @return number of rows deleted
     * @throws SQLException - Error connecting to database or executing query
     */
    public void addPersonToCompany(final int companyID, final String UUID) throws SQLException {
        this.dbConn.connect();

        //Prepare sql statement
        String query = "INSERT INTO personCompany (companyID, UUID) VALUES (?,?);";

        try (PreparedStatement preparedStatement = this.dbConn.conn.prepareStatement(query)) {

            //Set parameters and execute query
            preparedStatement.setInt(1, companyID);
            preparedStatement.setString(2, UUID);

            preparedStatement.executeUpdate();
        }
    }

    /**
     * Delete a person from a company by removing a row in personCompany
     * @param companyID - companyID of company to add person to
     * @param UUID - UUID of person to add to company
     * @return number of rows deleted
     * @throws SQLException - Error connecting to database or executing query
     */
    public int removePersonFromCompany(final int companyID, final String UUID) throws SQLException {
        this.dbConn.connect();

        //Prepare sql statement
        String query = "DELETE FROM personCompany WHERE companyID = ? AND UUID = ?;";

        try (PreparedStatement preparedStatement = this.dbConn.conn.prepareStatement(query)) {

            //Set parameters and execute query
            preparedStatement.setInt(1, companyID);
            preparedStatement.setString(2, UUID);

            return preparedStatement.executeUpdate();
        }
    }
}
