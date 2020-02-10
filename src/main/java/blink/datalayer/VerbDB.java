package blink.datalayer;

import blink.utility.objects.Verb;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class VerbDB {
    private DBConn dbConn;

    /**
     * Get verb information based on verbID
     * @param verbID - verbID to retrieve verb from
     * @return verb object or null if not found
     * @throws SQLException - Error connecting to database or executing query
     */
    public Verb getVerbByID(final int verbID) throws SQLException {
        //Prepare sql statement
        String query = "SELECT * FROM verb WHERE verb.verbID = ?;";

        try (Connection conn = this.dbConn.connect();
             PreparedStatement preparedStatement = conn.prepareStatement(query)) {

            //Set parameters and execute query
            preparedStatement.setInt(1, verbID);

            try (ResultSet result = preparedStatement.executeQuery()) {

                Verb verb = null;

                while (result.next()) {
                    verb = new Verb(result.getInt("verbID"),
                            result.getString("name"),
                            result.getString("description"));
                }

                //Return verb
                return verb;
            }
        }
    }

    /**
     * Connect to database and add
     * @param verbID - verbID of new verb to be added
     * @param name - name of new verb to be added
     * @param description - description of new verb to be added
     * @throws SQLException - Error connecting to database or executing update
     */
    public void insertVerb(final int verbID, final String name, final String description) throws SQLException {
        //Prepare sql statement
        String query = "INSERT INTO verb (verbID, name, description) VALUES (?, ?, ?);";

        try (Connection conn = this.dbConn.connect();
             PreparedStatement preparedStatement = conn.prepareStatement(query)) {

            //Set parameters and execute update
            preparedStatement.setInt(1, verbID);
            preparedStatement.setString(2, name);
            preparedStatement.setString(3, description);

            preparedStatement.executeUpdate();
        }
    }

    /**
     * Conncect to database and delete verb by verbID
     * @param verbID - verbID to delete from database
     * @return number of deleted verbs
     * @throws SQLException - Error connecting to database or executing update
     */
    public int deleteVerbByID(final int verbID) throws SQLException {
        //Prepare sql statement
        String query = "DELETE FROM verb WHERE verb.verbID = ?;";

        try (Connection conn = this.dbConn.connect();
             PreparedStatement preparedStatement = conn.prepareStatement(query)) {

            //Set parameters and execute update
            preparedStatement.setInt(1, verbID);
            return preparedStatement.executeUpdate();
        }
    }
}