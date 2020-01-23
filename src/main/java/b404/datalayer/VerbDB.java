package b404.datalayer;

import b404.utility.objects.Verb;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class VerbDB {
    DBConn dbConn;

    /**
     * Get verb information based on verbID
     * @param verbID - verbID to retrieve verb from
     * @return verb object or null if not found
     * @throws SQLException - Error connecting to database or executing query
     */
    public Verb getVerbByID(int verbID) throws SQLException {
        this.dbConn.connect();

        //Prepare sql statement
        String query = "SELECT * FROM verb WHERE verb.verbID = ?;";
        PreparedStatement preparedStatement = this.dbConn.conn.prepareStatement(query);

        //Set parameters and execute query
        preparedStatement.setInt(1, verbID);
        ResultSet result = preparedStatement.executeQuery();

        Verb verb = null;

        while(result.next()) {
            verb = new Verb(result.getInt("verbID"),
                            result.getString("name"),
                            result.getString("description"));
        }

        //Close the database
        this.dbConn.close();

        //Return verb
        return verb;
    }

    /**
     * Connect to database and add
     * @param verbID - verbID of new verb to be added
     * @param name - name of new verb to be added
     * @param description - description of new verb to be added
     * @throws SQLException - Error connecting to database or executing update
     */
    public void insertVerb(int verbID, String name, String description) throws SQLException {
        this.dbConn.connect();

        //Prepare sql statement
        String query = "INSERT INTO verb (verbID, name, description) VALUES (?, ?, ?);";
        PreparedStatement preparedStatement = this.dbConn.conn.prepareStatement(query);

        //Set parameters and execute update
        preparedStatement.setInt(1, verbID);
        preparedStatement.setString(2, name);
        preparedStatement.setString(3, description);

        preparedStatement.executeUpdate();

        //Close the database
        this.dbConn.close();
    }

    /**
     * Conncect to database and delete verb by verbID
     * @param verbID - verbID to delete from database
     * @return number of deleted verbs
     * @throws SQLException - Error connecting to database or executing update
     */
    public int deleteVerbByID(int verbID) throws SQLException {
        this.dbConn.connect();

        //Prepare sql statement
        String query = "DELETE FROM verb WHERE verb.verbID = ?;";
        PreparedStatement preparedStatement = this.dbConn.conn.prepareStatement(query);

        //Set parameters and execute update
        preparedStatement.setInt(1, verbID);
        int numRowsDeleted = preparedStatement.executeUpdate();

        //Close the database
        this.dbConn.close();

        //Return the number of deletes rows
        return numRowsDeleted;
    }
}
