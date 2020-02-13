package blink.datalayer;

import blink.utility.objects.Step;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StepDB {

    private static final String STEPID = "stepID";
    private static final String ORDERNUMBER = "orderNumber";
    private static final String ISHIGHESTLEVEL = "isHighestLevel";
    private static final String DESCRIPTION = "description";
    private static final String RELATEDSTEP = "relatedStep";
    private static final String UUID = "UUID";
    private static final String VERBID = "verbID";
    private static final String FILEID = "fileID";
    private static final String WORKFLOWID = "workflowID";
    private static final String COMPLETED = "completed";

    private DBConn dbConn;

    /**
     * Get a Steps information by stepID
     * @param stepID - stepID to search database for
     * @return step object or null if not found
     * @throws SQLException - error connecting to database or executing query
     */
    public Step getStepByStepID(int stepID) throws SQLException {
        try (Connection conn = this.dbConn.connect()) {

            //Prepare sql statement
            String query = "SELECT * FROM step WHERE step.stepID = ?";

            try (PreparedStatement preparedStatement = conn.prepareStatement(query)) {
                //Set parameters and execute query
                preparedStatement.setInt(1, stepID);

                try (ResultSet result = preparedStatement.executeQuery()) {
                    Step step = null;

                    while (result.next()) {
                        step = new Step.StepBuilder(result.getInt(STEPID),
                                result.getInt(ORDERNUMBER),
                                result.getBoolean(ISHIGHESTLEVEL),
                                result.getInt(VERBID),
                                result.getInt(FILEID),
                                result.getInt(WORKFLOWID),
                                result.getBoolean(COMPLETED))
                                .description(result.getString(DESCRIPTION))
                                .relatedStep(result.getInt(RELATEDSTEP))
                                .uuid(result.getInt(UUID))
                                .build();
                    }
                    return step;
                }
            }
        }
    }

    /**
     * Retrieves all higher level steps from the database
     * @param workflowID - workflowID of higher level steps to retrieve
     * @return - list of higher level steps containing lists of lower level steps
     * @throws SQLException - Error connecting to the database or executing query
     */
    public List<Step> getHigherLevelSteps(int workflowID) throws SQLException {
        try(Connection conn = this.dbConn.connect()) {

            //Prepare sql statement
            String query = "SELECT * FROM step WHERE isHighestLevel = true AND workflowID ?;";

            try (PreparedStatement preparedStatement = conn.prepareStatement(query)) {
                //Execute query
                preparedStatement.setInt(1, workflowID);

                try (ResultSet result = preparedStatement.executeQuery()) {
                    List<Step> steps = new ArrayList<>();
                    Step step;

                    while (result.next()) {
                        step = new Step.StepBuilder(result.getInt(STEPID),
                                result.getInt(ORDERNUMBER),
                                result.getBoolean(ISHIGHESTLEVEL),
                                result.getInt(VERBID),
                                result.getInt(FILEID),
                                result.getInt(WORKFLOWID),
                                result.getBoolean(COMPLETED))
                                .description(result.getString(DESCRIPTION))
                                .relatedStep(result.getInt(RELATEDSTEP))
                                .uuid(result.getInt(UUID))
                                .build();

                        steps.add(step);
                    }
                    //Return higher level steps
                    return steps;
                }
            }
        }
    }

    /**
     * Recursive function to getRelatedSteps to the stepID provided
     * @param stepID - stepID to check against relatedStep
     * @return - Return a list of steps
     * @throws SQLException - Error connecting to database or executing query
     */
    public List<Step> getRelatedSteps(int stepID) throws SQLException {
        try(Connection conn = this.dbConn.connect()) {

            //Prepare sql statement
            String query = "SELECT * FROM step WHERE relatedStep = ?;";

            try (PreparedStatement preparedStatement = conn.prepareStatement(query)) {
                //Set parameters and execute query
                preparedStatement.setInt(1, stepID);

                try (ResultSet result = preparedStatement.executeQuery()) {
                    ArrayList<Step> steps = new ArrayList<>();
                    Step step;

                    while (result.next()) {
                        step = new Step.StepBuilder(result.getInt(STEPID),
                                result.getInt(ORDERNUMBER),
                                result.getBoolean(ISHIGHESTLEVEL),
                                result.getInt(VERBID),
                                result.getInt(FILEID),
                                result.getInt(WORKFLOWID),
                                result.getBoolean(COMPLETED))
                                .description(result.getString(DESCRIPTION))
                                .relatedStep(result.getInt(RELATEDSTEP))
                                .uuid(result.getInt(UUID))
                                .build();

                        steps.add(step);
                    }
                    //Return higher level steps
                    return steps;
                }
            }
        }
    }

    /**
     * Connect to database and add steps
     * @param steps - list of steps to insert into the database
     * @throws SQLException - Error connecting to database or executing update
     */
    public int insertSteps(List<Step> steps) throws SQLException {
        try(Connection conn = this.dbConn.connect()) {
            conn.setAutoCommit(false);

            int numInsertedSteps = 0;

            String query = "INSERT INTO step (orderNumber, isHighestLevel, description, relatedStep, UUID, verbID, fileID, workflowID) VALUES (?, ?, ?, ?, ?, ?, ?, ?);";

            try (PreparedStatement preparedStatement = conn.prepareStatement(query)) {
                for (Step step : steps) {
                    preparedStatement.setInt(1, step.getOrderNumber());
                    preparedStatement.setBoolean(2, step.getIsHighestLevel());
                    preparedStatement.setString(3, step.getDescription());
                    preparedStatement.setInt(4, step.getRelatedStep());
                    preparedStatement.setInt(5, step.getUUID());
                    preparedStatement.setInt(6, step.getVerbID());
                    preparedStatement.setInt(7, step.getFileID());
                    preparedStatement.setInt(8, step.getWorkflowID());
                    numInsertedSteps += preparedStatement.executeUpdate();

                    if (Integer.toString(step.getRelatedStep()) != null || !Integer.toString(step.getRelatedStep()).isEmpty()) {
                        numInsertedSteps += insertChildSteps(step.getChildSteps(), preparedStatement, numInsertedSteps);
                    }
                }
                conn.commit();
            } finally {
                conn.setAutoCommit(true);
            }
            return numInsertedSteps;
        }
    }

    /**
     * Recursive method to add child steps into the database
     * @param steps - steps to add to the database
     * @param preparedStatement - preparedStatement created in insertSteps
     * @throws SQLException - Error connecting to the database or executing update
     */
    public int insertChildSteps(List<Step> steps, PreparedStatement preparedStatement, int numInsertedSteps) throws SQLException {

        for (Step step : steps) {
            preparedStatement.setInt(1, step.getOrderNumber());
            preparedStatement.setBoolean(2, step.getIsHighestLevel());
            preparedStatement.setString(3, step.getDescription());
            preparedStatement.setInt(4, step.getRelatedStep());
            preparedStatement.setInt(5, step.getUUID());
            preparedStatement.setInt(6, step.getVerbID());
            preparedStatement.setInt(7, step.getFileID());
            preparedStatement.setInt(8, step.getWorkflowID());
            numInsertedSteps += preparedStatement.executeUpdate();

            if (Integer.toString(step.getRelatedStep()) != null || !Integer.toString(step.getRelatedStep()).isEmpty()) {
                numInsertedSteps = insertChildSteps(step.getChildSteps(), preparedStatement, numInsertedSteps);
            }
        }
        return numInsertedSteps;
    }

    /**
     * Connect to the database and updateSteps
     * @param steps - list of steps to insert into database
     * @param workflowID - workflowID to delete steps by before inserting updated list
     * @throws SQLException - Error connecting to the database or executing update
     */
    public int updateSteps(List<Step> steps, String workflowID) throws SQLException {
        try(Connection conn = this.dbConn.connect()) {
            conn.setAutoCommit(false);

            int numUpdatedSteps = 0;

            //Prepare sql statement
            String query = "DELETE FROM step WHERE step.workflowID = ?;";

            try (PreparedStatement deleteStatement = conn.prepareStatement(query)) {
                deleteStatement.setInt(1, Integer.parseInt(workflowID));
                deleteStatement.executeUpdate();

                query = "INSERT INTO step (orderNumber, isHighestLevel, description, relatedStep, UUID, verbID, fileID, workflowID) VALUES (?, ?, ?, ?, ?, ?, ?, ?);";

                try (PreparedStatement preparedStatement = conn.prepareStatement(query)) {
                    for (Step step : steps) {
                        preparedStatement.setInt(1, step.getOrderNumber());
                        preparedStatement.setBoolean(2, step.getIsHighestLevel());
                        preparedStatement.setString(3, step.getDescription());
                        preparedStatement.setInt(4, step.getRelatedStep());
                        preparedStatement.setInt(5, step.getUUID());
                        preparedStatement.setInt(6, step.getVerbID());
                        preparedStatement.setInt(7, step.getFileID());
                        preparedStatement.setInt(8, step.getWorkflowID());
                        numUpdatedSteps += preparedStatement.executeUpdate();

                        if (Integer.toString(step.getRelatedStep()) != null || !Integer.toString(step.getRelatedStep()).isEmpty()) {
                            numUpdatedSteps = insertChildSteps(step.getChildSteps(), preparedStatement, numUpdatedSteps);
                        }
                    }
                    conn.commit();
                }
            } catch (SQLException ex) {
                conn.rollback();
                throw new SQLException(ex.getMessage());
            } finally {
                conn.setAutoCommit(true);
            }
            return numUpdatedSteps;
        }
    }

    /**
     * Conncect to database and delete step by UUID
     * @param workflowID - workflowID to delete from database
     * @return number of deleted steps
     * @throws SQLException - Error connecting to database or executing update
     */
    public int deleteStepsByWorkflowID(int workflowID) throws SQLException {
        try (Connection conn = this.dbConn.connect()) {

            //Prepare sql statement
            String query = "DELETE FROM step WHERE step.workflowID = ?;";
            try (PreparedStatement preparedStatement = conn.prepareStatement(query)) {
                preparedStatement.setInt(1, workflowID);
                return preparedStatement.executeUpdate();
            }
        }
    }
}
