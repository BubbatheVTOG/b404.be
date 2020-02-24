package blink.datalayer;

import blink.utility.objects.Step;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StepDB {

    private static final String STEPID = "stepID";
    private static final String ORDERNUMBER = "orderNumber";
    private static final String DESCRIPTION = "description";
    private static final String PARENTSTEPID = "parentStepID";
    private static final String UUID = "UUID";
    private static final String VERBID = "verbID";
    private static final String FILEID = "fileID";
    private static final String WORKFLOWID = "workflowID";
    private static final String COMPLETED = "completed";

    private DBConn dbConn;

    /**
     * Retrieves all higher level steps from the database
     * @param workflowID - workflowID of higher level steps to retrieve
     * @return list of higher level steps containing lists of lower level steps
     * @throws SQLException - Error connecting to the database or executing query
     */
    public List<Step> getHigherLevelSteps(int workflowID) throws SQLException {
        try(Connection conn = this.dbConn.connect()) {

            //Prepare sql statement
            String query = "SELECT * FROM step WHERE parentStepID = 0 AND workflowID = ? ORDER BY step.orderNumber;";

            try (PreparedStatement preparedStatement = conn.prepareStatement(query)) {
                //Execute query
                preparedStatement.setInt(1, workflowID);

                try (ResultSet result = preparedStatement.executeQuery()) {
                    List<Step> steps = new ArrayList<>();
                    Step step;

                    while (result.next()) {
                        step = new Step.StepBuilder(
                                result.getInt(VERBID),
                                result.getInt(FILEID),
                                result.getInt(WORKFLOWID),
                                result.getBoolean(COMPLETED))
                                .stepID(result.getInt(STEPID))
                                .orderNumber(result.getInt(ORDERNUMBER))
                                .description(result.getString(DESCRIPTION))
                                .parentStep(result.getInt(PARENTSTEPID))
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
     * @return Return a list of steps
     * @throws SQLException - Error connecting to database or executing query
     */
    public List<Step> getRelatedSteps(int stepID) throws SQLException {
        try(Connection conn = this.dbConn.connect()) {

            //Prepare sql statement
            String query = "SELECT * FROM step WHERE relatedStep = ? ORDER BY step.orderNumber;";

            try (PreparedStatement preparedStatement = conn.prepareStatement(query)) {
                //Set parameters and execute query
                preparedStatement.setInt(1, stepID);

                try (ResultSet result = preparedStatement.executeQuery()) {
                    ArrayList<Step> steps = new ArrayList<>();
                    Step step;

                    while (result.next()) {
                        step = new Step.StepBuilder(
                                result.getInt(VERBID),
                                result.getInt(FILEID),
                                result.getInt(WORKFLOWID),
                                result.getBoolean(COMPLETED))
                                .stepID(result.getInt(STEPID))
                                .orderNumber(result.getInt(ORDERNUMBER))
                                .description(result.getString(DESCRIPTION))
                                .parentStep(result.getInt(PARENTSTEPID))
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
     * @param steps list of steps to insert into the database
     * @throws SQLException Error connecting to database or executing update
     */
    public int insertSteps(List<Step> steps) throws SQLException {
        try(Connection conn = this.dbConn.connect()) {
            conn.setAutoCommit(false);

            int numInsertedSteps = 0;

            String query = "INSERT INTO step (orderNumber, description, parentStepID, UUID, verbID, fileID, workflowID, completed) VALUES (?, ?, ?, ?, ?, ?, ?, ?);";

            try (PreparedStatement preparedStatement = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
                int counter = 1;
                for (Step step : steps) {
                    preparedStatement.setInt(1, counter);
                    preparedStatement.setString(2, step.getDescription());
                    preparedStatement.setInt(3, 0);
                    preparedStatement.setInt(4, step.getUUID());
                    preparedStatement.setInt(5, step.getVerbID());
                    preparedStatement.setInt(6, step.getFileID());
                    preparedStatement.setInt(7, step.getWorkflowID());
                    preparedStatement.setBoolean(8, step.getCompleted());
                    numInsertedSteps += preparedStatement.executeUpdate();

                    if (step.getParentStepID() != 0) {
                        try (ResultSet insertedKeys = preparedStatement.getGeneratedKeys()) {
                            insertedKeys.next();
                            numInsertedSteps += insertChildSteps(step.getChildSteps(), preparedStatement, insertedKeys.getInt(1), numInsertedSteps);
                        }
                    }
                    counter++;
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
     * @param steps steps to add to the database
     * @param preparedStatement preparedStatement created in insertSteps
     * @throws SQLException Error connecting to the database or executing update
     */
    public int insertChildSteps(List<Step> steps, PreparedStatement preparedStatement, int parentStepID, int numInsertedSteps) throws SQLException {

        int counter = 1;
        for (Step step : steps) {
            preparedStatement.setInt(1, counter);
            preparedStatement.setString(2, step.getDescription());
            preparedStatement.setInt(3, parentStepID);
            preparedStatement.setInt(4, step.getUUID());
            preparedStatement.setInt(5, step.getVerbID());
            preparedStatement.setInt(6, step.getFileID());
            preparedStatement.setInt(7, step.getWorkflowID());
            preparedStatement.setBoolean(8, step.getCompleted());
            numInsertedSteps += preparedStatement.executeUpdate();

            if (step.getParentStepID() != 0) {
                try (ResultSet insertedKeys = preparedStatement.getGeneratedKeys()) {
                    insertedKeys.next();
                    numInsertedSteps += insertChildSteps(step.getChildSteps(), preparedStatement, insertedKeys.getInt(1), numInsertedSteps);
                }
            }
            counter++;
        }
        return numInsertedSteps;
    }

    /**
     * Connect to the database and updateSteps
     * @param steps list of steps to insert into database
     * @param workflowID workflowID to delete steps by before inserting updated list
     * @throws SQLException Error connecting to the database or executing update
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

                query = "INSERT INTO step (orderNumber, description, parentStepID, UUID, verbID, fileID, workflowID, completed) VALUES (?, ?, ?, ?, ?, ?, ?, ?);";

                try (PreparedStatement preparedStatement = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
                    int counter = 1;
                    for (Step step : steps) {
                        preparedStatement.setInt(1, counter);
                        preparedStatement.setString(2, step.getDescription());
                        preparedStatement.setInt(3, 0);
                        preparedStatement.setInt(4, step.getUUID());
                        preparedStatement.setInt(5, step.getVerbID());
                        preparedStatement.setInt(6, step.getFileID());
                        preparedStatement.setInt(7, step.getWorkflowID());
                        preparedStatement.setBoolean(8, step.getCompleted());
                        numUpdatedSteps += preparedStatement.executeUpdate();

                        if (step.getParentStepID() != 0) {
                            try (ResultSet insertedKeys = preparedStatement.getGeneratedKeys()) {
                                insertedKeys.next();
                                numUpdatedSteps += insertChildSteps(step.getChildSteps(), preparedStatement, insertedKeys.getInt(1), numUpdatedSteps);
                            }
                        }
                        counter++;
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
     * @param workflowID to delete from database
     * @return number of deleted steps
     * @throws SQLException Error connecting to database or executing update
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
