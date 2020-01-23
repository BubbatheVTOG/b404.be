package b404.datalayer;

import b404.utility.objects.Access;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AccessDB {
    DBConn dbConn;

    public AccessDB() {
        this.dbConn = new DBConn();
    }

    /**
     * Get access level information based on the accessLevelID
     * @param accessLevelID - accessLevelID to retrieve accessLevel from
     * @return access object or null if not found
     * @throws SQLException - Error connecting to database or executing query
     */
    public Access getAccessByID(int accessLevelID) throws SQLException {
        this.dbConn.connect();

        //Prepare sql statement
        String query = "SELECT * FROM accessLevel WHERE accessLevel.accessLevelID = ?;";
        PreparedStatement preparedStatement = this.dbConn.conn.prepareStatement(query);

        //Set parameters and execute query
        preparedStatement.setInt(1, accessLevelID);
        ResultSet result = preparedStatement.executeQuery();

        Access access = null;

        while(result.next()) {

            //Pull response content and map to an access object
            access = new Access(result.getInt("accessLevelID"),
                                result.getString("name"));
        }

        //Close the database
        this.dbConn.close();

        //Return access
        return access;
    }

    /**
     * Get access level information based on the accessLevelID
     * @param name - name to retrieve accessLevel from
     * @return access object or null if not found
     * @throws SQLException - Error connecting to database or executing query
     */
    public Access getAccessByName(String name) throws SQLException {
        this.dbConn.connect();

        //Prepare sql statement
        String query = "SELECT * FROM accessLevel WHERE accessLevel.name = ?;";
        PreparedStatement preparedStatement = this.dbConn.conn.prepareStatement(query);

        //Set parameters and execute query
        preparedStatement.setString(1, name);
        ResultSet result = preparedStatement.executeQuery();

        Access access = null;

        while(result.next()) {

            //Pull response content and map into an Access object
            access = new Access(result.getInt("accessLevelID"),
                                result.getString("name"));
        }

        //Close the database
        this.dbConn.close();

        //Return access
        return access;
    }

    /**
     * Connect to database and add
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
    public int deleteAccessLevelByAccessLevelID(int accessLevelID) throws SQLException {
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
    public int deleteAccessLevelByAccessLevelID(String name) throws SQLException {
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
