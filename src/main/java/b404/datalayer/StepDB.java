package b404.datalayer;

import b404.utility.objects.Step;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class StepDB {
    private DBConn dbConn;
    private List<Step> steps;

    public StepDB() {
        this.dbConn = new DBConn();
    }

    /**
     * Get a Steps information by UUID
     * @param UUID - UUID to search database for
     * @return step object or null if not found
     * @throws SQLException - error connecting to database or executing query
     */
    public Step getStepByUUID(int UUID) throws SQLException {
        this.dbConn.connect();

        //Prepare sql statement
        String query = "SELECT * FROM step WHERE step.UUID = ?";
        PreparedStatement preparedStatement = this.dbConn.conn.prepareStatement(query);

        //Set parameters and execute query
        preparedStatement.setInt(1, UUID);
        ResultSet result = preparedStatement.executeQuery();

        Step step = null;

        while(result.next()) {

            //Pull response content and map into a Person object
            step = new Step(result.getInt("UUID"),
                    result.getInt("orderNumber"),
                    result.getString("description"),
                    result.getInt("relatedStep"),
                    result.getInt("workflowID"));
        }

        //Close the database
        this.dbConn.close();

        return step;
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
    public void insertStep(int UUID, int orderNumber, String description, int relatedStep, int workflowID) throws SQLException {
        this.dbConn.connect();

        //Prepare sql statement
        String query = "INSERT INTO step (UUID, orderNumber, description, relatedStep, workflowID) VALUES (?, ?, ?, ?, ?);";
        PreparedStatement preparedStatement = this.dbConn.conn.prepareStatement(query);

        //Set parameters and execute update
        preparedStatement.setInt(1, UUID);
        preparedStatement.setInt(2, orderNumber);
        preparedStatement.setString(3, description);
        preparedStatement.setInt(4, relatedStep);
        preparedStatement.setInt(5, workflowID);

        preparedStatement.executeUpdate();

        //Close the database
        this.dbConn.close();
    }

    /**
     * Conncect to database and delete step by UUID
     * @param UUID - UUID to delete from database
     * @return number of deleted steps
     * @throws SQLException - Error connecting to database or executing update
     */
    public int deleteStepByUUID(int UUID) throws SQLException {
        this.dbConn.connect();

        //Prepare sql statement
        String query = "DELETE FROM step WHERE step.UUID = ?;";
        PreparedStatement preparedStatement = this.dbConn.conn.prepareStatement(query);

        //Set parameters and execute update
        preparedStatement.setInt(1, UUID);
        int numRowsDeleted = preparedStatement.executeUpdate();

        //Close the database
        this.dbConn.close();

        //Return number of deleted steps
        return numRowsDeleted;
    }
}
