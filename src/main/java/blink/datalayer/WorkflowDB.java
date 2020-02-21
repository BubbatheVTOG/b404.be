package blink.datalayer;

import blink.utility.objects.Step;
import blink.utility.objects.Workflow;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WorkflowDB {
    private DBConn dbConn;
    private StepDB stepDB;

    public WorkflowDB(){
        this.dbConn = new DBConn();
        this.stepDB = new StepDB();
    }

    /**
     * Get all workflows
     * @return List of workflow objects
     * @throws SQLException - Error connecting to database or executing query
     */
    public List<Workflow> getAllWorkflows() throws SQLException {
        try(Connection conn = this.dbConn.connect()) {

            //Prepare sql statement
            String query = "SELECT * FROM workflow" +
                                "JOIN milestone ON (workflow.milestoneID = milestone.milestoneID);";

            try (PreparedStatement preparedStatement = conn.prepareStatement(query)) {

                //Set parameters and execute query
                try (ResultSet result = preparedStatement.executeQuery()) {

                    List<Workflow> workflowList = new ArrayList<>();
                    while (result.next()) {
                        int workflowID = result.getInt("workflowID");
                        List<Step> stepList = this.stepDB.getHigherLevelSteps(workflowID);
                        workflowList.add(new Workflow(result.getInt("workflowID"),
                                result.getString("name"),
                                result.getString("workflow.description"),
                                result.getDate("workflow.createdDate"),
                                result.getDate("workflow.lastUpdatedDate"),
                                result.getDate("workflow.startDate"),
                                result.getDate("workflow.deliveryDate"),
                                result.getDate("workflow.completedDate"),
                                result.getBoolean("workflow.archived"),
                                result.getInt("companyID"),
                                result.getInt("milestoneID"),
                                stepList));
                    }

                    //Return milestone
                    return workflowList;
                }
            }
        }
    }

    /**
     * Get all workflows by CompanyID
     * @param companyIDList - List of company IDs to retrieve workflows by
     * @return List of workflow objects assigned to company with companyID
     * @throws SQLException - Error connecting to database or executing query
     */
    public List<Workflow> getAllWorkflows(List<Integer> companyIDList) throws SQLException {
        try(Connection conn = this.dbConn.connect()) {

            //Prepare sql statement
            String query = "SELECT * FROM workflow " +
                                "JOIN milestone ON (workflow.milestoneID = milestone.milestoneID) " +
                                "WHERE companyID IN ?;";

            try (PreparedStatement preparedStatement = conn.prepareStatement(query)) {
                preparedStatement.setArray(1, (java.sql.Array)companyIDList);

                //Set parameters and execute query
                try (ResultSet result = preparedStatement.executeQuery()) {

                    List<Workflow> workflowList = new ArrayList<>();
                    while (result.next()) {
                        int workflowID = result.getInt("workflowID");
                        List<Step> stepList = this.stepDB.getHigherLevelSteps(workflowID);
                        workflowList.add(new Workflow(workflowID,
                                result.getString("workflow.name"),
                                result.getString("workflow.description"),
                                result.getDate("workflow.createdDate"),
                                result.getDate("workflow.lastUpdatedDate"),
                                result.getDate("workflow.startDate"),
                                result.getDate("workflow.deliveryDate"),
                                result.getDate("workflow.completedDate"),
                                result.getBoolean("workflow.archived"),
                                result.getInt("companyID"),
                                result.getInt("milestoneID"),
                                stepList));
                    }

                    //Return workflow
                    return workflowList;
                }
            }
        }
    }

    /**
     * Get all workflows based on archived or not archived
     * @param archived - Search for either archived or unarchived workflows
     * @return List of workflow objects
     * @throws SQLException - Error connecting to database or executing query
     */
    public List<Workflow> getAllWorkflows(boolean archived) throws SQLException {
        try(Connection conn = this.dbConn.connect()) {

            //Prepare sql statement
            String query = "SELECT * FROM workflow " +
                                "JOIN milestone ON (workflow.milestoneID = milestone.milestoneID)" +
                                "WHERE archived = ?;";

            try (PreparedStatement preparedStatement = conn.prepareStatement(query)) {
                preparedStatement.setBoolean(1, archived);

                //Set parameters and execute query
                try (ResultSet result = preparedStatement.executeQuery()) {

                    List<Workflow> workflowList = new ArrayList<>();
                    while (result.next()) {
                        int workflowID = result.getInt("workflowID");
                        List<Step> stepList = this.stepDB.getHigherLevelSteps(workflowID);
                        workflowList.add(new Workflow(result.getInt("workflowID"),
                                result.getString("workflow.name"),
                                result.getString("workflow.description"),
                                result.getDate("workflow.createdDate"),
                                result.getDate("workflow.lastUpdatedDate"),
                                result.getDate("workflow.startDate"),
                                result.getDate("workflow.deliveryDate"),
                                result.getDate("workflow.completedDate"),
                                result.getBoolean("workflow.archived"),
                                result.getInt("companyID"),
                                result.getInt("milestoneID"),
                                stepList));
                    }

                    //Return workflow
                    return workflowList;
                }
            }
        }
    }

    /**
     * Get all workflows based on archived or not archived
     * @param companyIDList - List of company IDs to retrieve workflows by
     * @param archived - Search for either archived or unarchived workflows
     * @return List of workflow objects
     * @throws SQLException - Error connecting to database or executing query
     */
    public List<Workflow> getAllWorkflows(List<Integer> companyIDList, boolean archived) throws SQLException {
        try(Connection conn = this.dbConn.connect()) {

            //Prepare sql statement
            String query = "SELECT * FROM workflow" +
                                "JOIN milestone ON (workflow.milestoneID = milestone.milestoneID) " +
                                "WHERE workflow.archived = ?" +
                                "AND companyID IN ?;";

            try (PreparedStatement preparedStatement = conn.prepareStatement(query)) {
                preparedStatement.setBoolean(1, archived);
                preparedStatement.setArray(2, (java.sql.Array)companyIDList);

                //Set parameters and execute query
                try (ResultSet result = preparedStatement.executeQuery()) {

                    List<Workflow> workflowList = new ArrayList<>();
                    while (result.next()) {
                        int workflowID = result.getInt("workflowID");
                        List<Step> stepList = this.stepDB.getHigherLevelSteps(workflowID);
                        workflowList.add(new Workflow(workflowID,
                                result.getString("workflow.name"),
                                result.getString("workflow.description"),
                                result.getDate("workflow.createdDate"),
                                result.getDate("workflow.lastUpdatedDate"),
                                result.getDate("workflow.startDate"),
                                result.getDate("workflow.deliveryDate"),
                                result.getDate("workflow.completedDate"),
                                result.getBoolean("workflow.archived"),
                                result.getInt("companyID"),
                                result.getInt("milestoneID"),
                                stepList));
                    }

                    //Return workflow
                    return workflowList;
                }
            }
        }
    }

    /**
     * Get workflow information based on the workflowID
     * @param workflowID - workflowID to retrieve workflow from
     * return workflow object or null if not found
     * @throws SQLException - Error connecting to database or executing query
     */
    public Workflow getWorkflowByID(final int workflowID) throws SQLException {
        //Prepare sql statement
        String query = "SELECT * FROM workflow " +
                            "JOIN milestone ON (workflow.milestoneID = milestone.milestoneID) " +
                            "WHERE workflow.workflowID = ?;";

        try (Connection conn = this.dbConn.connect();
             PreparedStatement preparedStatement = conn.prepareStatement(query)) {

            //Set parameters and execute query
            preparedStatement.setInt(1, workflowID);

            try (ResultSet result = preparedStatement.executeQuery()) {

                Workflow workflow = null;

                while (result.next()) {
                    List<Step> stepList = this.stepDB.getHigherLevelSteps(workflowID);
                    workflow = new Workflow(result.getInt("workflowID"),
                            result.getString("workflow.name"),
                            result.getString("workflow.description"),
                            result.getDate("workflow.createdDate"),
                            result.getDate("workflow.lastUpdatedDate"),
                            result.getDate("workflow.startDate"),
                            result.getDate("workflow.deliveryDate"),
                            result.getDate("workflow.completedDate"),
                            result.getBoolean("workflow.archived"),
                            result.getInt("companyID"),
                            result.getInt("milestoneID"),
                            stepList);
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
    public int deleteWorkflowByID(final int workflowID) throws SQLException {
        //Prepare sql statement
        String query = "DELETE FROM workflow WHERE workflow.workflowID = ?;";

        try (Connection conn = this.dbConn.connect();
             PreparedStatement preparedStatement = conn.prepareStatement(query)) {

            //Set parameters and execute update
            preparedStatement.setInt(1, workflowID);
            return preparedStatement.executeUpdate();
        }
    }

    /**
     * Update archive status of an existing workflow
     * @param workflowID - ID of workflow to update
     * @param archiveStatus - boolean to set archive status to
     * @throws SQLException - Error connecting to database or executing statement
     */
    public void updateWorkflowArchiveStatus(final int workflowID, boolean archiveStatus) throws SQLException {
        //Prepare sql statement
        String query = "UPDATE workflow SET archived = ? WHERE workflowID = ?;";

        try(Connection conn = this.dbConn.connect();
            PreparedStatement preparedStatement = conn.prepareStatement(query)) {

            //Set parameters and execute update
            preparedStatement.setBoolean(1, archiveStatus);
            preparedStatement.setInt(2, workflowID);

            preparedStatement.executeUpdate();
        }
    }
}