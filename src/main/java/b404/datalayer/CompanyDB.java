package b404.datalayer;

import b404.utility.objects.Company;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CompanyDB {
    DBConn dbConn;

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
        String query = "SELECT * FROM company WHERE company.companyID = ?;";
        PreparedStatement preparedStatement = this.dbConn.conn.prepareStatement(query);

        //Set parameters and execute query
        preparedStatement.setInt(1, companyID);
        ResultSet result = preparedStatement.executeQuery();

        Company company = null;

        while(result.next()) {

            //Pull response content and map into a Company object
            company = new Company(result.getInt("companyID"),
                                  result.getString("name"));
        }

        //Close the database
        this.dbConn.close();

        //return person;
        return company;
    }

    public Company getCompanyByName(String companyName) throws SQLException {
        this.dbConn.connect();

        //Prepare sql statement
        String query = "SELECT * FROM company WHERE company.name = ?;";
        PreparedStatement preparedStatement = this.dbConn.conn.prepareStatement(query);

        //Set parameters and execute query
        preparedStatement.setString(1, companyName);
        ResultSet result = preparedStatement.executeQuery();

        Company company = null;

        while(result.next()) {

            //Pull response content and map into a Company object
            company = new Company(result.getInt("companyID"),
                    result.getString("name"));
        }

        //Close the database
        this.dbConn.close();

        //return person;
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
     * @param companyID - comapnyID to delete from the database
     * @return number of rows deleted
     * @throws SQLException - Error connecting to database or executing query
     */
    public int deleteCompanyByCompanyID(int companyID) throws SQLException {
        this.dbConn.connect();

        //Prepare sql statement
        String query = "DELETE FROM company WHERE company.companyID = ?;";
        PreparedStatement preparedStatement = dbConn.conn.prepareStatement(query);

        //Set parameters and execute query
        preparedStatement.setInt(1, companyID);
        int numRowsDeleted = preparedStatement.executeUpdate();

        //Close the database
        dbConn.close();

        //Return deleted rows
        return numRowsDeleted;
    }
}
