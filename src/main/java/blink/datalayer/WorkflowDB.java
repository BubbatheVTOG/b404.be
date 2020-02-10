package blink.datalayer;

import blink.utility.objects.Workflow;

import java.sql.*;

public class WorkflowDB {
    private DBConn dbConn;

    public WorkflowDB() {
        this.dbConn = new DBConn();
    }

    /**
     * Get workflow information based on the workflowID
     * @param workflowID - workflowID to retrieve workflow from
     * return workflow object or null if not found
     * @throws SQLException - Error connecting to database or executing query
     */
    public Workflow getWorkflowByID(final int workflowID) throws SQLException {
        //Prepare sql statement
        String query = "SELECT * FROM workflow WHERE workflow.workflowID = ?;";

        try (Connection conn = this.dbConn.connect();
             PreparedStatement preparedStatement = conn.prepareStatement(query)) {

            //Set parameters and execute query
            preparedStatement.setInt(1, workflowID);

            try (ResultSet result = preparedStatement.executeQuery()) {

                Workflow workflow = null;

                while (result.next()) {
                    workflow = new Workflow(result.getInt("workflowID"),
                            result.getString("name"),
                            result.getDate("startDate"),
                            result.getDate("endDate"),
                            result.getInt("milestoneID"));
                }

                //Return workflow
                return workflow;
            }
        }
    }

    /**
     * Connect to database and add
     * @param workflowID - workflowID of new workflow to be added
     * @param name - name of new workflow to be added
     * @param startDate - startDate of new workflow to be added
     * @param endDate - endDate of new workflow to be added
     * @param milestoneID - milestoneID of new workflow to be added
     * @throws SQLException - Error connecting to database or executing update
     */
    public void insertWorkflow(final int workflowID, final String name, final Date startDate, final Date endDate, final int milestoneID) throws SQLException {
        //Prepare sql statement
        String query = "INSERT INTO workflow (workflowID, name, startDate, endDate, milestoneID) VALUES (?, ?, ?, ?, ?);";

        try (Connection conn = this.dbConn.connect();
             PreparedStatement preparedStatement = conn.prepareStatement(query)) {

            //Set parameters and execute update
            preparedStatement.setInt(1, workflowID);
            preparedStatement.setString(2, name);
            preparedStatement.setDate(3, startDate);
            preparedStatement.setDate(4, endDate);
            preparedStatement.setInt(5, milestoneID);

            preparedStatement.executeUpdate();
        }
    }

    /**
     * Connect to database and delete a workflow by workflowID
     * @param workflowID - workflowID to delete from database
     * @return number of rows deleted
     * @throws SQLException - Error connecting to database or executing update
     */
    public int deleteWorkflowByWorkflowID(final int workflowID) throws SQLException {
        //Prepare sql statement
        String query = "DELETE FROM workflow WHERE workflow.workflowID = ?;";

        try (Connection conn = this.dbConn.connect();
             PreparedStatement preparedStatement = conn.prepareStatement(query)) {

            //Set parameters and execute update
            preparedStatement.setInt(1, workflowID);
            return preparedStatement.executeUpdate();
        }
    }
}