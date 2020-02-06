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
     * @param steps - list of steps to insert into the database
     * @throws SQLException - Error connecting to database or executing update
     */
    public void insertSteps(List<Step> steps) throws SQLException {
        try {
            this.dbConn.connect();
            this.dbConn.conn.setAutoCommit(false);

            String query = "INSERT INTO step (orderNumber, isHighestLevel, description, relatedStep, UUID, verbID, fileID, workflowID) VALUES (?, ?, ?, ?, ?, ?, ?);";
            PreparedStatement preparedStatement = this.dbConn.conn.prepareStatement(query);

            for(Step step : steps) {
                preparedStatement.setInt(1, step.getOrderNumber());
                preparedStatement.setBoolean(2, step.getIsHighestLevel());
                preparedStatement.setString(3, step.getDescription());
                preparedStatement.setInt(4, step.getRelatedStep());
                preparedStatement.setInt(5, step.getUUID());
                preparedStatement.setInt(6, step.getVerbID());
                preparedStatement.setInt(7, step.getFileID());
                preparedStatement.setInt(8, step.getWorkflowID());
                preparedStatement.executeUpdate();
            }

            this.dbConn.conn.commit();

        } catch(SQLException sqle) {
            this.dbConn.conn.rollback();
            throw new SQLException(sqle.getMessage());
        } finally {
            this.dbConn.close();
            this.dbConn.conn.setAutoCommit(true);
        }
    }

    public void updateSteps(List<Step> steps, int workflowID) throws SQLException {
        try {
            this.dbConn.connect();
            this.dbConn.conn.setAutoCommit(false);

            //Prepare sql statement
            String query = "DELETE FROM step WHERE step.workflowID = ?;";
            PreparedStatement preparedStatement = this.dbConn.conn.prepareStatement(query);

            preparedStatement.setInt(1, workflowID);
            preparedStatement.executeUpdate();

            query = "INSERT INTO step (orderNumber, isHighestLevel, description, relatedStep, UUID, verbID, fileID, workflowID) VALUES (?, ?, ?, ?, ?, ?, ?);";
            preparedStatement = this.dbConn.conn.prepareStatement(query);

            for(Step step : steps) {
                preparedStatement.setInt(1, step.getOrderNumber());
                preparedStatement.setBoolean(2, step.getIsHighestLevel());
                preparedStatement.setString(3, step.getDescription());
                preparedStatement.setInt(4, step.getRelatedStep());
                preparedStatement.setInt(5, step.getUUID());
                preparedStatement.setInt(6, step.getVerbID());
                preparedStatement.setInt(7, step.getFileID());
                preparedStatement.setInt(8, step.getWorkflowID());
                preparedStatement.executeUpdate();
            }

            this.dbConn.conn.commit();

        } catch(SQLException sqle) {
            this.dbConn.conn.rollback();
            throw new SQLException(sqle.getMessage());
        } finally {
            this.dbConn.close();
            this.dbConn.conn.setAutoCommit(true);
        }
    }

    /**
     * Conncect to database and delete step by UUID
     * @param workflowID - workflowID to delete from database
     * @return number of deleted steps
     * @throws SQLException - Error connecting to database or executing update
     */
    public int deleteStepsByWorkflowID(int workflowID) throws SQLException {
        try {
            this.dbConn.connect();
            this.dbConn.conn.setAutoCommit(false);

            //Prepare sql statement
            String query = "DELETE FROM step WHERE step.workflowID = ?;";
            PreparedStatement preparedStatement = this.dbConn.conn.prepareStatement(query);

            preparedStatement.setInt(1, workflowID);
            int numRowsDeleted = preparedStatement.executeUpdate();

            this.dbConn.conn.commit();

            return numRowsDeleted;
        } catch(SQLException sqle) {
            this.dbConn.conn.rollback();
            throw new SQLException(sqle.getMessage());
        } finally {
            this.dbConn.close();
            this.dbConn.conn.setAutoCommit(true);
        }
    }
}
