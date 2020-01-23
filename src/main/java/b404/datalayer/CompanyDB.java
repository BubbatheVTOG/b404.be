package b404.datalayer;

import b404.utility.objects.Company;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CompanyDB {
    private DBConn dbConn;

    public CompanyDB(){
        this.dbConn = new DBConn();
    }

    /**
     * Get a companies information by companyID
     * @param companyID
     * @return company object or null if not found
     * @throws SQLException
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
     * @param companyID - companyID of new company to be added
     * @param name - name of new company to be added
     * @throws SQLException - error connecting to database or executing
     */
    public void insertCompany(int companyID, String name) throws SQLException {
        this.dbConn.connect();

        //Prepare sql statement
        String query = "INSERT INTO company (companyID, name) VALUES (?, ?);";
        PreparedStatement preparedStatement = this.dbConn.conn.prepareStatement(query);

        //Set parameters and execute query
        preparedStatement.setInt(1, companyID);
        preparedStatement.setString(2, name);

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
}
