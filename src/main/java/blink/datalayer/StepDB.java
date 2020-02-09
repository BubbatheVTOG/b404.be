package blink.datalayer;

import blink.utility.objects.Step;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StepDB {
    private DBConn dbConn;

    public StepDB() {
        this.dbConn = new DBConn();
    }

    /**
     * Get a Steps information by UUID
     * @param UUID - UUID to search database for
     * @return step object or null if not found
     * @throws SQLException - error connecting to database or executing query
     */
    public Step getStepByUUID(final int UUID) throws SQLException {
        this.dbConn.connect();

        //Prepare sql statement
        String query = "SELECT * FROM step WHERE step.UUID = ?";

        try (PreparedStatement preparedStatement = this.dbConn.conn.prepareStatement(query)) {

            //Set parameters and execute query
            preparedStatement.setInt(1, UUID);

            try (ResultSet result = preparedStatement.executeQuery()) {

                Step step = null;

                while (result.next()) {

                    //Pull response content and map into a Person object
                    step = new Step(result.getInt("UUID"),
                            result.getInt("orderNumber"),
                            result.getString("description"),
                            result.getInt("relatedStep"),
                            result.getInt("workflowID"));
                }

                return step;
            }
        }
    }

    /**
     * Connect to database and add
     * @param UUID - UUID of new step to be added
     * @param orderNumber - orderNumber of new step to be added
     * @param description - description of new step to be added
     * @param relatedStep - relatedStep of new step to be added
     * @param workflowID - workflowID of new step to be added
     * @throws SQLException - Error connecting to database or executing update
     */
    public void insertStep(final int UUID, final int orderNumber, final String description, final int relatedStep, final int workflowID) throws SQLException {
        this.dbConn.connect();

        //Prepare sql statement
        String query = "INSERT INTO step (UUID, orderNumber, description, relatedStep, workflowID) VALUES (?, ?, ?, ?, ?);";

        try (PreparedStatement preparedStatement = this.dbConn.conn.prepareStatement(query)) {

            //Set parameters and execute update
            preparedStatement.setInt(1, UUID);
            preparedStatement.setInt(2, orderNumber);
            preparedStatement.setString(3, description);
            preparedStatement.setInt(4, relatedStep);
            preparedStatement.setInt(5, workflowID);

            preparedStatement.executeUpdate();
        }
    }

    /**
     * Conncect to database and delete step by UUID
     * @param UUID - UUID to delete from database
     * @return number of deleted steps
     * @throws SQLException - Error connecting to database or executing update
     */
    public int deleteStepByUUID(final int UUID) throws SQLException {
        this.dbConn.connect();

        //Prepare sql statement
        String query = "DELETE FROM step WHERE step.UUID = ?;";

        try (PreparedStatement preparedStatement = this.dbConn.conn.prepareStatement(query)) {

            //Set parameters and execute update
            preparedStatement.setInt(1, UUID);
            return preparedStatement.executeUpdate();
        }
    }
}
