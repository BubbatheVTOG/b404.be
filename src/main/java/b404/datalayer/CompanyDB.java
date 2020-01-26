package b404.datalayer;

import b404.utility.objects.Company;
import b404.utility.objects.Person;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class CompanyDB {
    private DBConn dbConn;

    public CompanyDB(){
        this.dbConn = new DBConn();
    }

    /**
     * Connect to database and retrieve all content of person table
     * @return ArrayList of all Company objects in database
     * @throws SQLException - Error connecting to database or executing query
     */
    public ArrayList<Company> getAllCompanies() throws SQLException {
        this.dbConn.connect();

        //Prepare sql statement
        String query = "SELECT * FROM company;";
        PreparedStatement preparedStatement = this.dbConn.conn.prepareStatement(query);

        //Set parameters and execute query
        ResultSet result = preparedStatement.executeQuery();

        ArrayList<Company> companyList = new ArrayList<>();

        while(result.next()) {
            companyList.add(new Company(result.getInt("companyID"),
                    result.getString("name")));
        }

        //Close the database
        this.dbConn.close();

        return companyList;
    }

    /**
     * Connect to database and retrieve all people associated with a specific company
     * @return ArrayList of all Company objects in database
     * @throws SQLException - Error connecting to database or executing query
     */
    public ArrayList<Person> getAllPeopleByCompany(int companyID) throws SQLException {
        this.dbConn.connect();

        //Prepare sql statement
        String query = "SELECT * FROM personCompany\n" +
                "        JOIN person ON (personCompany.UUID= person.UUID)\n" +
                "        WHERE companyID = ?;";
        PreparedStatement preparedStatement = this.dbConn.conn.prepareStatement(query);
        preparedStatement.setInt(1, companyID);

        //Set parameters and execute query
        ResultSet result = preparedStatement.executeQuery();

        ArrayList<Person> personList = new ArrayList<>();

        while(result.next()) {
            personList.add(new Person(result.getString("UUID"),
                                      result.getString("username"),
                                      result.getString("passwordHash"),
                                      result.getString("salt"),
                                      result.getString("fName"),
                                      result.getString("lName"),
                                      result.getString("email"),
                                      result.getString("title"),
                                      result.getInt("accessLevelID")));
        }

        //Close the database
        this.dbConn.close();

        return personList;
    }

    /**
     * Get a companies information by companyID
     * @param companyID - companyID to search database for
     * @return company object or null if not found
     * @throws SQLException - error connecting to database or executing query
     */
    public Company getCompanyByID(int companyID) throws SQLException {
        this.dbConn.connect();

        //Prepare sql statement
        String query = "SELECT * FROM company WHERE company.companyID = ?";
        PreparedStatement preparedStatement = this.dbConn.conn.prepareStatement(query);

        //Set parameters and execute query
        preparedStatement.setInt(1, companyID);
        ResultSet result = preparedStatement.executeQuery();

        Company company = null;

        while(result.next()) {

            //Pull response content and map into a Person object
            company = new Company(result.getInt("companyID"),
                                  result.getString("name"));
        }

        //Close the database
        this.dbConn.close();

        return company;
    }

    /**
     * Gets a company from the database by company name
     * @param companyName - name to search database for
     * @return - Company with matching Company Name
     * @throws SQLException - Error connecting to database or executing query
     */
    public Company getCompanyByName(String companyName) throws SQLException {
        this.dbConn.connect();

        //Prepare sql statement
        String query = "SELECT * FROM company WHERE company.name = ?";
        PreparedStatement preparedStatement = this.dbConn.conn.prepareStatement(query);

        //Set parameters and execute query
        preparedStatement.setString(1, companyName);
        ResultSet result = preparedStatement.executeQuery();

        Company company = null;

        while(result.next()) {

            //Pull response content and map into a Person object
            company = new Company(result.getInt("companyID"),
                    result.getString("name"));
        }

        //Close the database
        this.dbConn.close();

        return company;
    }

    /**
     * Connect to database and add
     * @param name - name of new company to be added
     * @throws SQLException - error connecting to database or executing
     */
    public void insertCompany(String name) throws SQLException {
        this.dbConn.connect();

        //Prepare sql statement
        String query = "INSERT INTO company (name) VALUES (?);";
        PreparedStatement preparedStatement = this.dbConn.conn.prepareStatement(query);

        //Set parameters and execute query
        preparedStatement.setString(1, name);

        preparedStatement.executeUpdate();

        //Close the database
        dbConn.close();
    }

    /**
     * Connect to database and add
     * @param name - name of new company to be added
     * @throws SQLException - error connecting to database or executing
     */
    public void updateCompany(int companyID, String name) throws SQLException {
        this.dbConn.connect();

        //Prepare sql statement
        String query = "UPDATE company SET company.name = ? WHERE company.companyID = ?;";
        PreparedStatement preparedStatement = this.dbConn.conn.prepareStatement(query);

        //Set parameters and execute query
        preparedStatement.setString(1, name);
        preparedStatement.setInt(2, companyID);

        preparedStatement.executeUpdate();

        //Close the database
        dbConn.close();
    }

    /**
     * Connect to database and delete a company by companyID
     * @param companyID - comapanyID of company to delete from the database
     * @return number of rows deleted
     * @throws SQLException - Error connecting to database or executing query
     */
    public int deleteCompany(int companyID) throws SQLException {
        this.dbConn.connect();

        //Prepare sql statement
        String query = "DELETE FROM company WHERE company.companyID = ?;";
        PreparedStatement preparedStatement = this.dbConn.conn.prepareStatement(query);

        //Set parameters and execute query
        preparedStatement.setInt(1, companyID);
        int numRowsDeleted = preparedStatement.executeUpdate();

        //Close the database
        this.dbConn.close();

        //Return deleted rows
        return numRowsDeleted;
    }

    /**
     * Connect to database and delete a company by company name
     * @param companyName - company name of company to delete from the database
     * @return number of rows deleted
     * @throws SQLException - Error connecting to database or executing query
     */
    public int deleteCompany(String companyName) throws SQLException {
        this.dbConn.connect();

        //Prepare sql statement
        String query = "DELETE FROM company WHERE company.name = ?;";
        PreparedStatement preparedStatement = this.dbConn.conn.prepareStatement(query);

        //Set parameters and execute query
        preparedStatement.setString(1, companyName);
        int numRowsDeleted = preparedStatement.executeUpdate();

        //Close the database
        this.dbConn.close();

        //Return deleted rows
        return numRowsDeleted;
    }

    /**
     * Add a person to a company by adding a row into personCompany
     * @param companyID - companyID of company to add person to
     * @param UUID - UUID of person to add to company
     * @return number of rows deleted
     * @throws SQLException - Error connecting to database or executing query
     */
    public void addPersonToCompany(int companyID, String UUID) throws SQLException {
        this.dbConn.connect();

        //Prepare sql statement
        String query = "INSERT INTO personCompany (companyID, UUID) VALUES (?,?);";
        PreparedStatement preparedStatement = this.dbConn.conn.prepareStatement(query);

        //Set parameters and execute query
        preparedStatement.setInt(1, companyID);
        preparedStatement.setString(2, UUID);

        preparedStatement.executeUpdate();

        //Close the database
        this.dbConn.close();
    }

    /**
     * Delete a person from a company by removing a row in personCompany
     * @param companyID - companyID of company to add person to
     * @param UUID - UUID of person to add to company
     * @return number of rows deleted
     * @throws SQLException - Error connecting to database or executing query
     */
    public int removePersonFromCompany(int companyID, String UUID) throws SQLException {
        this.dbConn.connect();

        //Prepare sql statement
        String query = "DELETE FROM personCompany WHERE companyID = ? AND UUID = ?;";
        PreparedStatement preparedStatement = this.dbConn.conn.prepareStatement(query);

        //Set parameters and execute query
        preparedStatement.setInt(1, companyID);
        preparedStatement.setString(2, UUID);

        int numRowsAffected = preparedStatement.executeUpdate();

        //Close the database
        this.dbConn.close();

        return numRowsAffected;
    }
}
