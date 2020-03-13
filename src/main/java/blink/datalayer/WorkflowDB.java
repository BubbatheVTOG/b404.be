package blink.datalayer;

import blink.businesslayer.CompanyBusiness;
import blink.businesslayer.StepBusiness;
import blink.utility.objects.Step;
import blink.utility.objects.Workflow;
import com.google.gson.JsonArray;

import javax.ws.rs.InternalServerErrorException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class WorkflowDB {
    private DBConn dbConn;
    private StepBusiness stepBusiness;
    private CompanyBusiness companyBusiness;
    private String joinStatement;
    private String leftJoinStatement;

    public WorkflowDB(){
        this.dbConn = new DBConn();
        this.stepBusiness = new StepBusiness();
        this.companyBusiness = new CompanyBusiness();

        this.joinStatement = "SELECT * FROM workflow " +
                                        "JOIN milestone ON (workflow.milestoneID = milestone.milestoneID) ";
        this.leftJoinStatement = "SELECT * FROM workflow " +
                                    "LEFT JOIN milestone ON (workflow.milestoneID = milestone.milestoneID) ";
    }

    /**
     * Get all workflows
     * @return List of workflow objects
     * @throws SQLException Error connecting to database or executing query
     */
    public List<Workflow> getAllWorkflows() throws SQLException {
        try(Connection conn = this.dbConn.connect()) {

            //Prepare sql statement
            String query = this.leftJoinStatement;

            try (PreparedStatement preparedStatement = conn.prepareStatement(query)) {

                //Set parameters and execute query
                try (ResultSet result = preparedStatement.executeQuery()) {

                    List<Workflow> workflowList = new ArrayList<>();
                    while (result.next()) {
                        workflowList.add(this.parseWorkflow(result));
                    }

                    //Return milestone
                    return workflowList;
                }
            }
        }
    }

    /**
     * Get all workflows pertaining to a list of CompanyID's
     * @param companyIDList List of company IDs to retrieve workflows by
     * @return List of workflow objects assigned to company with companyID
     * @throws SQLException Error connecting to database or executing query
     */
    public List<Workflow> getAllWorkflows(final List<Integer> companyIDList) throws SQLException {
        try(Connection conn = this.dbConn.connect()) {

            //Prepare sql statement
            String query = this.leftJoinStatement +
                            "WHERE milestone.companyID IN ?;";

            try (PreparedStatement preparedStatement = conn.prepareStatement(query)) {

                //Set parameters and execute query
                preparedStatement.setArray(1, conn.createArrayOf("INTEGER", companyIDList.toArray()));
                try (ResultSet result = preparedStatement.executeQuery()) {

                    List<Workflow> workflowList = new ArrayList<>();
                    while (result.next()) {
                        workflowList.add(this.parseWorkflow(result));
                    }

                    //Return workflow
                    return workflowList;
                }
            }
        }
    }

    /**
     * Get all workflows
     * @return List of workflow objects
     * @throws SQLException Error connecting to database or executing query
     */
    public List<Workflow> getTemplateWorkflows() throws SQLException {
        try(Connection conn = this.dbConn.connect()) {

            //Prepare sql statement
            String query = this.leftJoinStatement +
                                "WHERE workflow.milestoneID IS NULL;";

            try (PreparedStatement preparedStatement = conn.prepareStatement(query)) {

                //Execute query
                try (ResultSet result = preparedStatement.executeQuery()) {

                    List<Workflow> workflowList = new ArrayList<>();
                    while (result.next()) {
                        workflowList.add(this.parseWorkflow(result));
                    }

                    //Return milestone
                    return workflowList;
                }
            }
        }
    }

    /**
     * Get all workflows based on archived or not archived
     * @param archived Search for either archived or unarchived workflows
     * @return List of workflow objects
     * @throws SQLException Error connecting to database or executing query
     */
    public List<Workflow> getConcreteWorkflows(final boolean archived) throws SQLException {
        try(Connection conn = this.dbConn.connect()) {

            //Prepare sql statement
            String query = this.joinStatement +
                            "WHERE workflow.archived = ?;";

            try (PreparedStatement preparedStatement = conn.prepareStatement(query)) {

                //Set parameters and execute query
                preparedStatement.setBoolean(1, archived);
                try (ResultSet result = preparedStatement.executeQuery()) {

                    List<Workflow> workflowList = new ArrayList<>();
                    while (result.next()) {
                        workflowList.add(this.parseWorkflow(result));
                    }

                    //Return workflow
                    return workflowList;
                }
            }
        }
    }

    /**
     * Get all workflows based on archived or not archived pertaining to a set of companies
     * @param companyIDList List of company IDs to retrieve workflows by
     * @param archived Search for either archived or unarchived workflows
     * @return List of workflow objects
     * @throws SQLException Error connecting to database or executing query
     */
    public List<Workflow> getConcreteWorkflows(final List<Integer> companyIDList, final boolean archived) throws SQLException {
        try(Connection conn = this.dbConn.connect()) {

            //Prepare sql statement
            String query = this.joinStatement +
                            "WHERE workflow.archived = ? " +
                            "AND milestone.companyID IN ?;";

            try (PreparedStatement preparedStatement = conn.prepareStatement(query)) {

                //Set parameters and execute query
                preparedStatement.setBoolean(1, archived);
                preparedStatement.setArray(2, (java.sql.Array)companyIDList);
                try (ResultSet result = preparedStatement.executeQuery()) {

                    List<Workflow> workflowList = new ArrayList<>();
                    while (result.next()) {
                        workflowList.add(this.parseWorkflow(result));
                    }

                    //Return workflow
                    return workflowList;
                }
            }
        }
    }

    /**
     * Get milestone information based on the milestoneID
     * @param milestoneID milestoneID to retrieve milestone from
     * @return milestone object or null if not found
     * @throws SQLException Error connecting to database or executing query
     */
    public List<Workflow> getWorkflowsByMilestoneID(final int milestoneID) throws SQLException {
        try(Connection conn = this.dbConn.connect()) {

            //Prepare sql statement
            String query = this.joinStatement +
                            "WHERE milestone.milestoneID = ?;";

            try (PreparedStatement preparedStatement = conn.prepareStatement(query)) {

                //Set parameters and execute query
                preparedStatement.setInt(1, milestoneID);
                try (ResultSet result = preparedStatement.executeQuery()) {

                    List<Workflow> workflowList = new ArrayList<>();

                    while (result.next()) {
                        workflowList.add(this.parseWorkflow(result));
                    }

                    //Return milestone
                    return workflowList;
                }
            }
        }
    }

    /**
     * Get workflow information based on the workflowID
     * @param workflowID workflowID to retrieve workflow from
     * return workflow object or null if not found
     * @throws SQLException Error connecting to database or executing query
     */
    public Workflow getWorkflowByID(final int workflowID) throws SQLException {
        //Prepare sql statement
        String query = this.leftJoinStatement +
                        "WHERE workflow.workflowID = ?;";

        try (Connection conn = this.dbConn.connect();
             PreparedStatement preparedStatement = conn.prepareStatement(query)) {

            //Set parameters and execute query
            preparedStatement.setInt(1, workflowID);
            try (ResultSet result = preparedStatement.executeQuery()) {

                Workflow workflow = null;

                while (result.next()) {
                    workflow = this.parseWorkflow(result);
                }

                //Return workflow
                return workflow;
            }
        }
    }

    /**
     * Connect to database and add a template workflow
     * @param name name of template workflow to be added
     * @param description description of new workflow to be added
     * @throws SQLException Error connecting to database or executing update
     */
    public int insertWorkflow(final String name, final String description, Date createdDate, Date lastUpdatedDate, JsonArray steps) throws SQLException {
        //Prepare sql statement
        String query = "INSERT INTO workflow (name, description, createdDate, lastUpdatedDate) " +
                            "VALUES (?, ?, ?, ?);";

        try {
            Connection conn = this.dbConn.connect();

            try(PreparedStatement preparedStatement = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

                //Set parameters and execute update
                preparedStatement.setString(1, name);
                preparedStatement.setString(2, description);
                preparedStatement.setDate(3, new java.sql.Date(createdDate.getTime()));
                preparedStatement.setDate(4, new java.sql.Date(lastUpdatedDate.getTime()));

                preparedStatement.executeUpdate();

                try (ResultSet insertedKeys = preparedStatement.getGeneratedKeys()) {
                    insertedKeys.next();

                    int workflowID = insertedKeys.getInt(1);

                    //Convert step json to jsonArray using new workflowID
                    this.stepBusiness.insertSteps(steps, workflowID, conn);

                    return workflowID;
                }
            }
            catch (Exception e) {
                conn.rollback();
                conn.close();
                throw new SQLException(e.getMessage());
            }
            finally {
                conn.commit();
                conn.close();
            }
        }
        catch(SQLException sqle){
            throw new SQLException(sqle.getMessage());
        }
    }

    /**
     * Connect to database and add a concrete workflow
     * @param name name of template workflow to be added
     * @param description description of new workflow to be added
     * @throws SQLException Error connecting to database or executing update
     */
    public int insertWorkflow(final String name, final String description, final Date createdDate, final Date lastUpdatedDate, final Date startDate, final Date deliveryDate, final int milestoneID, JsonArray steps) throws SQLException {
        //Prepare sql statement
        String query = "INSERT INTO workflow (name, description, createdDate, lastUpdatedDate, startDate, deliveryDate, milestoneID) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?);";

        try {
            Connection conn = this.dbConn.connect();
            conn.setAutoCommit(false);

            try(PreparedStatement preparedStatement = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

                //Set parameters and execute update
                preparedStatement.setString(1, name);
                preparedStatement.setString(2, description);
                preparedStatement.setDate(3, new java.sql.Date(createdDate.getTime()));
                preparedStatement.setDate(4, new java.sql.Date(lastUpdatedDate.getTime()));
                preparedStatement.setDate(5, new java.sql.Date(startDate.getTime()));
                preparedStatement.setDate(6, new java.sql.Date(deliveryDate.getTime()));
                preparedStatement.setInt(7, milestoneID);

                preparedStatement.executeUpdate();

                try (ResultSet insertedKeys = preparedStatement.getGeneratedKeys()) {
                    insertedKeys.next();

                    int workflowID = insertedKeys.getInt(1);

                    //Convert step json to jsonArray using new workflowID
                    this.stepBusiness.insertSteps(steps, workflowID, conn);

                    return workflowID;
                }
                catch (Exception e) {
                    conn.rollback();
                    conn.close();
                    throw new SQLException(e.getMessage());
                }
                finally {
                    conn.commit();
                    conn.close();
                }
            }
        }
        catch(SQLException sqle){
            throw new SQLException(sqle.getMessage());
        }
    }

    /**
     * Connect to database and update a template workflow
     * @param workflowID ID of workflow to update
     * @param name name of template workflow to be added
     * @param description description of new workflow to be added
     * @param lastUpdatedDate The date to set the lastUpdatedDate to
     * @throws SQLException Error connecting to database or executing update
     */
    public void updateWorkflow(final int workflowID, final String name, final String description, final Date lastUpdatedDate, JsonArray steps) throws SQLException {
        //Prepare sql statement
        String query = "UPDATE workflow " +
                       "SET name = ?, description = ?, lastUpdatedDate = ? " +
                       "WHERE workflowID = ?;";

        try {
            Connection conn = this.dbConn.connect();

            try(PreparedStatement preparedStatement = conn.prepareStatement(query)) {

                //Set parameters and execute update
                preparedStatement.setString(1, name);
                preparedStatement.setString(2, description);
                preparedStatement.setDate(3, new java.sql.Date(lastUpdatedDate.getTime()));
                preparedStatement.setInt(4, workflowID);

                preparedStatement.executeUpdate();

                this.stepBusiness.updateSteps(steps, workflowID, conn);
            }
            catch (Exception e) {
                conn.rollback();
                conn.close();
                throw new SQLException(e.getMessage());
            }
            finally {
                conn.commit();
                conn.close();
            }
        }
        catch(SQLException sqle){
            throw new SQLException(sqle.getMessage());
        }
    }

    /**
     * Connect to database and update a concrete workflow
     * @param workflowID ID of workflow to update
     * @param name name of template workflow to be added
     * @param description description of new workflow to be added
     * @param lastUpdatedDate The date to set the lastUpdatedDate to
     * @param startDate The updated start date
     * @param deliveryDate The updated delivery date
     * @param completedDate The updated completed date
     * @throws SQLException Error connecting to database or executing update
     */
    public void updateWorkflow(final int workflowID, final String name, final String description, final Date lastUpdatedDate, final Date startDate, final Date deliveryDate, final Date completedDate, JsonArray steps) throws SQLException {
        //Prepare sql statement
        String query = "UPDATE workflow " +
                       "SET name = ?, description = ?, lastUpdatedDate = ?, startDate = ?, deliveryDate = ?, completedDate = ? " +
                       "WHERE workflowID = ?;";

        try {
            Connection conn = this.dbConn.connect();
            try(PreparedStatement preparedStatement = conn.prepareStatement(query)) {

                //Set parameters and execute update
                preparedStatement.setString(1, name);
                preparedStatement.setString(2, description);
                preparedStatement.setDate(3, new java.sql.Date(lastUpdatedDate.getTime()));
                preparedStatement.setDate(4, new java.sql.Date(startDate.getTime()));
                preparedStatement.setDate(5, new java.sql.Date(deliveryDate.getTime()));
                preparedStatement.setDate(6, new java.sql.Date(completedDate.getTime()));
                preparedStatement.setInt(7, workflowID);

                preparedStatement.executeUpdate();

                this.stepBusiness.updateSteps(steps, workflowID, conn);
            }
            catch (Exception e) {
                conn.rollback();
                conn.close();
                throw new SQLException(e.getMessage());
            }
            finally {
                conn.commit();
                conn.close();
            }
        }
        catch(SQLException sqle){
            throw new SQLException(sqle.getMessage());
        }
    }

    /**
     * Connect to database and delete a workflow by workflowID
     * @param workflowID workflowID to delete from database
     * @return number of rows deleted
     * @throws SQLException Error connecting to database or executing update
     */
    public int deleteWorkflowByID(final int workflowID) throws SQLException {
        int numDeletedRows;
        //Prepare sql statement
        String query = "DELETE FROM workflow " +
                            "WHERE workflow.workflowID = ?;";

        try {
            Connection conn = this.dbConn.connect();

            try(PreparedStatement preparedStatement = conn.prepareStatement(query)) {

                //Set parameters and execute update
                preparedStatement.setInt(1, workflowID);
                numDeletedRows = preparedStatement.executeUpdate();

                this.stepBusiness.deleteStepsByWorkflowID(Integer.toString(workflowID), conn);
            }
            catch (Exception e) {
                conn.rollback();
                conn.close();
                throw new SQLException(e.getMessage());
            }
            finally {
                conn.commit();
                conn.close();
            }
        }
        catch(SQLException sqle){
            throw new SQLException(sqle.getMessage());
        }

        return numDeletedRows;
    }

    /**
     * Get active workflows by userID
     * @param uuid - ID of user to search by
     * @return List of workflow objects with steps assigned to user
     * @throws SQLException - Error connecting to database or executing query
     */
    public List<Workflow> getActiveUserWorkflows(String uuid) throws SQLException {
        try(Connection conn = this.dbConn.connect()) {

            //Prepare sql statement
            String query = "SELECT * FROM workflow " +
                    "JOIN step ON (workflow.workflowID = step.workflowID) " +
                    "WHERE step.uuid = ?";

            try (PreparedStatement preparedStatement = conn.prepareStatement(query)) {
                preparedStatement.setString(1, uuid);

                //Set parameters and execute query
                try (ResultSet result = preparedStatement.executeQuery()) {

                    List<Workflow> workflowList = new ArrayList<>();
                    while (result.next()) {
                        int workflowID = result.getInt("workflowID");
                        List<Step> stepList = this.stepBusiness.getSteps(Integer.toString(workflowID));
                        workflowList.add(new Workflow(workflowID,
                                result.getString("workflow.name"),
                                result.getString("workflow.description"),
                                result.getDate("workflow.createdDate"),
                                result.getDate("workflow.lastUpdatedDate"),
                                result.getDate("workflow.startDate"),
                                result.getDate("workflow.deliveryDate"),
                                result.getDate("workflow.completedDate"),
                                result.getBoolean("workflow.archived"),
                                this.companyBusiness.getCompanyByID(result.getString("companyID")),
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
     * Update archive status of an existing workflow
     * @param workflowID ID of workflow to update
     * @param archiveStatus boolean to set archive status to
     * @throws SQLException Error connecting to database or executing statement
     */
    public void updateWorkflowArchiveStatus(final int workflowID, boolean archiveStatus) throws SQLException {
        //Prepare sql statement
        String query = "UPDATE workflow SET archived = ? " +
                            "WHERE workflowID = ?;";

        try(Connection conn = this.dbConn.connect();
            PreparedStatement preparedStatement = conn.prepareStatement(query)) {

            //Set parameters and execute update
            preparedStatement.setBoolean(1, archiveStatus);
            preparedStatement.setInt(2, workflowID);

            preparedStatement.executeUpdate();
        }
    }

    private Workflow parseWorkflow(ResultSet result) throws SQLException{
        try {
            String workflowID = result.getString("workflowID");
            return new Workflow(Integer.parseInt(workflowID),
                    result.getString("workflow.name"),
                    result.getString("workflow.description"),
                    result.getDate("workflow.createdDate"),
                    result.getDate("workflow.lastUpdatedDate"),
                    result.getDate("workflow.startDate"),
                    result.getDate("workflow.deliveryDate"),
                    result.getDate("workflow.completedDate"),
                    result.getBoolean("workflow.archived"),
                    result.getString("milestone.companyID") == null ? null : this.companyBusiness.getCompanyByID(result.getString("milestone.companyID")),
                    result.getString("workflow.milestoneID") == null ? 0 : result.getInt("workflow.milestoneID"),
                    this.stepBusiness.getSteps(workflowID)
            );
        }catch(Exception e){
            throw new InternalServerErrorException(e.getMessage());
        }
    }
}