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
}
