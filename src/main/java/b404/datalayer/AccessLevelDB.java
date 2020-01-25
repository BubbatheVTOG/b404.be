package b404.datalayer;

import b404.utility.objects.AccessLevel;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AccessLevelDB {
    private DBConn dbConn;

    public AccessLevelDB(){
        this.dbConn = new DBConn();
    }

    /**
     * Get access level information based on accessLevelID
     * @param accessLevelID - accessLevelID to search database for
     * @return AccessLevel object or null if not found
     * @throws SQLException
     */
    public AccessLevel getAccessLevel(int accessLevelID) throws SQLException {
        this.dbConn.connect();

        //Prepare sql statement
        String query = "SELECT * FROM accessLevel WHERE accessLevel.accessLevelID = ?";
        PreparedStatement preparedStatement = this.dbConn.conn.prepareStatement(query);

        //Set parameters and execute query
        preparedStatement.setInt(1, accessLevelID);
        ResultSet result = preparedStatement.executeQuery();


        AccessLevel accessLevel = null;

        while(result.next()) {

            //Pull response content and map into a Person object
            accessLevel = new AccessLevel(result.getInt("accessLevelID"),
                                          result.getString("accessLevelName"));
        }

        //Close the database
        this.dbConn.close();

        return accessLevel;
    }

    /**
     * Get access level information based on the accessLevelName
     * @param name - name to search database for
     * @return AccessLevel object or null if not found
     * @throws SQLException - Error connecting to database or executing query
     */
    public AccessLevel getAccessLevel(String name) throws SQLException {
        this.dbConn.connect();

        //Prepare sql statement
        String query = "SELECT * FROM accessLevel WHERE accessLevel.name = ?;";
        PreparedStatement preparedStatement = this.dbConn.conn.prepareStatement(query);

        //Set parameters and execute query
        preparedStatement.setString(1, name);
        ResultSet result = preparedStatement.executeQuery();

        AccessLevel access = null;

        while(result.next()) {

            //Pull response content and map into an Access object
            access = new AccessLevel(result.getInt("accessLevelID"),
                                     result.getString("name"));
        }

        //Close the database
        this.dbConn.close();

        //Return access
        return access;
    }

    /**
     * Connect to database and add a new access level
     * @param accessLevelID - accessLevelID of new accessLevel to be added
     * @param name - name of new accessLevel to be added
     * @throws SQLException - Error connecting to database or executing update
     */
    public void insertAccessLevel(int accessLevelID, String name) throws SQLException {
        this.dbConn.connect();

        //Prepare sql statement
        String query = "INSERT INTO accessLevel (accessLevelID, name) VALUES (?, ?);";
        PreparedStatement preparedStatement = this.dbConn.conn.prepareStatement(query);

        //Set parameters and execute query
        preparedStatement.setInt(1, accessLevelID);
        preparedStatement.setString(2, name);

        preparedStatement.executeUpdate();

        //Close the database
        this.dbConn.close();
    }

    /**
     * Connect to database and delete an accessLevel by accessLevelID
     * @param accessLevelID - accessLevelID to delete from database
     * @return number of rows deleted
     * @throws SQLException - Error connecting to database or executing update
     */
    public int deleteAccessLevel(int accessLevelID) throws SQLException {
        this.dbConn.connect();

        //Prepare sql statement
        String query = "DELETE FROM accessLevel WHERE accessLevel.accessLevelID = ?;";
        PreparedStatement preparedStatement = this.dbConn.conn.prepareStatement(query);

        //Set parameters and execute query
        preparedStatement.setInt(1, accessLevelID);
        int numRowsDeleted = preparedStatement.executeUpdate();

        //Close the database
        this.dbConn.close();

        //Return the number of deleted rows
        return numRowsDeleted;
    }

    /**
     * Connect to database and delete an accessLevel by name
     * @param name - name to delete from database
     * @return number of rows deleted
     * @throws SQLException - Error connecting to database or executing update
     */
    public int deleteAccessLevel(String name) throws SQLException {
        this.dbConn.connect();

        //Prepare sql statement
        String query = "DELETE FROM accessLevel WHERE accessLevel.name = ?;";
        PreparedStatement preparedStatement = this.dbConn.conn.prepareStatement(query);

        //Set parameters and execute query
        preparedStatement.setString(1, name);
        int numRowsDeleted = preparedStatement.executeUpdate();

        //Close the database
        this.dbConn.close();

        //Return the number of deleted rows
        return numRowsDeleted;
    }
}
