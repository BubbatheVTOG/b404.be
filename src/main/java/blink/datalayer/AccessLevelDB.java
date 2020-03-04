package blink.datalayer;

import blink.utility.objects.AccessLevel;

import java.sql.Connection;
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
     * @param accessLevelID accessLevelID to search database for
     * @return AccessLevel object or null if not found
     * @throws SQLException Issue with connecting to database or executing statement
     */
    public AccessLevel getAccessLevel(final int accessLevelID) throws SQLException {
        //Prepare sql statement
        String query = "SELECT * FROM accessLevel WHERE accessLevel.accessLevelID = ?";

        try(Connection conn = this.dbConn.connect();
            PreparedStatement preparedStatement = conn.prepareStatement(query)) {

            //Set parameters and execute query
            preparedStatement.setInt(1, accessLevelID);

            try (ResultSet result = preparedStatement.executeQuery()) {
                AccessLevel accessLevel = null;

                while (result.next()) {

                    //Pull response content and map into a Person object
                    accessLevel = new AccessLevel(result.getInt("accessLevelID"),
                            result.getString("accessLevelName"));
                }
                return accessLevel;
            }
        }
    }

    /**
     * Get access level information based on the accessLevelName
     * @param name name to search database for
     * @return AccessLevel object or null if not found
     * @throws SQLException Error connecting to database or executing query
     */
    public AccessLevel getAccessLevel(final String name) throws SQLException {
        //Prepare sql statement
        String query = "SELECT * FROM accessLevel WHERE accessLevel.name = ?;";

        try(Connection conn = this.dbConn.connect();
            PreparedStatement preparedStatement = conn.prepareStatement(query)) {

            //Set parameters and execute query
            preparedStatement.setString(1, name);
            try (ResultSet result = preparedStatement.executeQuery()) {

                AccessLevel access = null;

                while (result.next()) {

                    //Pull response content and map into an Access object
                    access = new AccessLevel(result.getInt("accessLevelID"),
                            result.getString("name"));
                }

                //Return access
                return access;
            }
        }
    }

    /**
     * Connect to database and add a new access level
     * @param accessLevelID accessLevelID of new accessLevel to be added
     * @param name name of new accessLevel to be added
     * @throws SQLException Error connecting to database or executing update
     */
    public void insertAccessLevel(final int accessLevelID, final String name) throws SQLException {
        //Prepare sql statement
        String query = "INSERT INTO accessLevel (accessLevelID, name) VALUES (?, ?);";

        try(Connection conn = this.dbConn.connect();
            PreparedStatement preparedStatement = conn.prepareStatement(query)) {

            //Set parameters and execute query
            preparedStatement.setInt(1, accessLevelID);
            preparedStatement.setString(2, name);
            preparedStatement.executeUpdate();
        }
    }

    /**
     * Connect to database and delete an accessLevel by accessLevelID
     * @param accessLevelID accessLevelID to delete from database
     * @return number of rows deleted
     * @throws SQLException Error connecting to database or executing update
     */
    public int deleteAccessLevel(final int accessLevelID) throws SQLException {
        //Prepare sql statement
        String query = "DELETE FROM accessLevel WHERE accessLevel.accessLevelID = ?;";

        try(Connection conn = this.dbConn.connect();
            PreparedStatement preparedStatement = conn.prepareStatement(query)) {

            //Set parameters and execute query
            preparedStatement.setInt(1, accessLevelID);

            return preparedStatement.executeUpdate();
        }
    }

    /**
     * Connect to database and delete an accessLevel by name
     * @param name name to delete from database
     * @return number of rows deleted
     * @throws SQLException Error connecting to database or executing update
     */
    public int deleteAccessLevel(final String name) throws SQLException {
        //Prepare sql statement
        String query = "DELETE FROM accessLevel WHERE accessLevel.name = ?;";

        try(Connection conn = this.dbConn.connect();
            PreparedStatement preparedStatement = conn.prepareStatement(query)) {

            //Set parameters and execute query
            preparedStatement.setString(1, name);

            return preparedStatement.executeUpdate();
        }
    }
}
