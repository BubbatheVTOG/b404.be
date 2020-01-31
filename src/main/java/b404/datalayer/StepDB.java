package b404.datalayer;

import b404.utility.objects.Step;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StepDB {
    private DBConn dbConn;

    public StepDB() {
        this.dbConn = new DBConn();
    }

    /**
     * Get a Steps information by stepID
     * @param stepID - stepID to search database for
     * @return step object or null if not found
     * @throws SQLException - error connecting to database or executing query
     */
    public Step getStepByStepID(int stepID) throws SQLException {
        this.dbConn.connect();

        //Prepare sql statement
        String query = "SELECT * FROM step WHERE step.stepID = ?";
        PreparedStatement preparedStatement = this.dbConn.conn.prepareStatement(query);

        //Set parameters and execute query
        preparedStatement.setInt(1, stepID);
        ResultSet result = preparedStatement.executeQuery();

        Step step = null;

        while(result.next()) {
            step = new Step(result.getInt("stepID"),
                    result.getInt("orderNumber"),
                    result.getBoolean("isHighestLevel"),
                    result.getString("description"),
                    result.getInt("relatedStep"),
                    result.getInt("UUID"),
                    result.getInt("verbID"),
                    result.getInt("fileID"),
                    result.getInt("workflowID"));
        }

        //Close the database
        this.dbConn.close();

        return step;
    }

    public List<Step> getHigherLevelSteps(int workflowID) throws SQLException {
        this.dbConn.connect();

        //Prepare sql statement
        String query = "SELECT * FROM step WHERE isHighestLevel = true AND workflowID ?;";
        PreparedStatement preparedStatement = this.dbConn.conn.prepareStatement(query);

        //Execute query
        preparedStatement.setInt(1, workflowID);
        ResultSet result = preparedStatement.executeQuery();

        List<Step> steps = new ArrayList<>();
        Step step = null;

        while(result.next()) {
            step = new Step(result.getInt("stepID"),
                    result.getInt("orderNumber"),
                    result.getBoolean("isHighestLevel"),
                    result.getString("description"),
                    result.getInt("relatedStep"),
                    result.getInt("UUID"),
                    result.getInt("verbID"),
                    result.getInt("fileID"),
                    result.getInt("workflowID"));

            steps.add(step);
        }

        //Close the database
        this.dbConn.close();;

        //Return higher level steps
        return steps;
    }

    public List<Step> getRelatedSteps(int stepID) throws SQLException {
        this.dbConn.connect();

        //Prepare sql statement
        String query = "SELECT * FROM step WHERE relatedStep = ?;";
        PreparedStatement preparedStatement = this.dbConn.conn.prepareStatement(query);

        //Set parameters and execute query
        preparedStatement.setInt(1, stepID);
        ResultSet result = preparedStatement.executeQuery();

        ArrayList<Step> steps = new ArrayList<>();
        Step step = null;

        while(result.next()) {
            step = new Step(result.getInt("stepID"),
                    result.getInt("orderNumber"),
                    result.getBoolean("isHighestLevel"),
                    result.getString("description"),
                    result.getInt("relatedStep"),
                    result.getInt("UUID"),
                    result.getInt("verbID"),
                    result.getInt("fileID"),
                    result.getInt("workflowID"));

            steps.add(step);
        }

        //Close the database
        this.dbConn.close();;

        //Return higher level steps
        return steps;
    }

    /**
     * Connect to database and add
     * @param orderNumber - orderNumber of new step to be added
     * @param description - description of new step to be added
     * @param relatedStep - relatedStep of new step to be added
     * @param UUID - UUID of new step to be added
     * @param verbID - verbID of new step to be added
     * @param fileID - fileID of new step to be added
     * @param workflowID - workflowID of new step to be added
     * @throws SQLException - Error connecting to database or executing update
     */
    public void insertStep(int orderNumber, boolean isHighestLevel, String description, int relatedStep,
                           int UUID, int verbID, int fileID, int workflowID) throws SQLException {
        this.dbConn.connect();

        //Prepare sql statement
        String query = "INSERT INTO step (orderNumber, isHighestLevel, description, relatedStep, UUID, verbID, fileID, workflowID) VALUES (?, ?, ?, ?, ?, ?, ?);";
        PreparedStatement preparedStatement = this.dbConn.conn.prepareStatement(query);

        //Set parameters and execute update
        preparedStatement.setInt(1, orderNumber);
        preparedStatement.setBoolean(2, isHighestLevel);
        preparedStatement.setString(3, description);
        preparedStatement.setInt(4, relatedStep);
        preparedStatement.setInt(5, UUID);
        preparedStatement.setInt(6, verbID);
        preparedStatement.setInt(7, fileID);
        preparedStatement.setInt(8, workflowID);

        preparedStatement.executeUpdate();

        //Close the database
        this.dbConn.close();
    }

    /**
     * Conncect to database and delete step by UUID
     * @param stepID - stepID to delete from database
     * @return number of deleted steps
     * @throws SQLException - Error connecting to database or executing update
     */
    public int deleteStepByStepID(int stepID) throws SQLException {
        this.dbConn.connect();

        //Prepare sql statement
        String query = "DELETE FROM step WHERE step.stepID = ?;";
        PreparedStatement preparedStatement = this.dbConn.conn.prepareStatement(query);

        //Set parameters and execute update
        preparedStatement.setInt(1, stepID);
        int numRowsDeleted = preparedStatement.executeUpdate();

        //Close the database
        this.dbConn.close();

        //Return number of deleted steps
        return numRowsDeleted;
    }
}
