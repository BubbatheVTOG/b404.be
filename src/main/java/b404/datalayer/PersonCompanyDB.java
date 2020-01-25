package b404.datalayer;

import b404.utility.objects.PersonCompany;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PersonCompanyDB {
    private DBConn dbConn;

    public PersonCompanyDB(){
        this.dbConn = new DBConn();
    }

    /**
     * Get a PersonCompany information by UUID and companyID
     * @param UUID
     * @param companyID
     * @return company object or null if not found
     * @throws SQLException - Error connecting to database or executing query
     */
    public PersonCompany getPersonCompany(int UUID, int companyID) throws SQLException {
        this.dbConn.connect();

        //Prepare sql statement
        String query = "SELECT * FROM company WHERE personCompany.UUID = ? AND personCompany.companyID = ?;";
        PreparedStatement preparedStatement = this.dbConn.conn.prepareStatement(query);

        //Set parameters and execute query
        preparedStatement.setInt(1, UUID);
        preparedStatement.setInt(2, companyID);
        ResultSet result = preparedStatement.executeQuery();

        PersonCompany personCompany = null;

        while(result.next()) {

            //Pull response content and map into a Person object
            personCompany = new PersonCompany(result.getInt("UUID"),
                    result.getInt("companyID"));
        }

        //Close the database
        this.dbConn.close();

        return personCompany;
    }

    /**
     * Connect to database and add
     * @param parameters - array of UUIDs and companyIDs
     * @throws SQLException - error connecting to database or executing
     */
    public void insertPersonCompany(int... parameters) throws SQLException {
        this.dbConn.connect();

        for(int i = 0; i < (parameters.length/2); i=i+2) {
            //Prepare sql statement
            String query = "INSERT INTO personCompany (UUID, companyID) VALUES (?, ?);";
            PreparedStatement preparedStatement = this.dbConn.conn.prepareStatement(query);

            //Set parameters for reference
            int UUID = parameters[i];
            int companyID = parameters[++i];

            //Set parameters and execute update
            preparedStatement.setInt(1,UUID);
            preparedStatement.setInt(2, companyID);
            preparedStatement.executeUpdate();
        }
        //Close the database
        dbConn.close();
    }

    /**
     * Connect to database and delete a PersonCompany by UUID and companyID
     * @param UUID - UUID of personCompany to delete from the database
     * @param companyID
     * @return number of rows deleted
     * @throws SQLException - Error connecting to database or executing query
     */
    public int deletePersonCompany(int UUID, int companyID) throws SQLException {
        this.dbConn.connect();

        //Prepare sql statement
        String query = "DELETE FROM personCompany WHERE personCompany.UUID = ? AND personCompany.companyID = ?;";
        PreparedStatement preparedStatement = this.dbConn.conn.prepareStatement(query);

        //Set parameters and execute query
        preparedStatement.setInt(1, UUID);
        preparedStatement.setInt(2, companyID);
        int numRowsDeleted = preparedStatement.executeUpdate();

        //Close the database
        this.dbConn.close();

        //Return deleted rows
        return numRowsDeleted;
    }
}
