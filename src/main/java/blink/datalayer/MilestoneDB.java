package blink.datalayer;

import blink.businesslayer.CompanyBusiness;
import blink.businesslayer.WorkflowBusiness;
import blink.utility.objects.Milestone;

import javax.ws.rs.BadRequestException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MilestoneDB {
    private DBConn dbConn;
    private CompanyBusiness companyBusiness;

    public MilestoneDB(){
        this.dbConn = new DBConn();
    }

    /**
     * Get all milestones
     * @return List of milestone objects
     * @throws SQLException Error connecting to database or executing query
     */
    public List<Milestone> getAllMilestones() throws SQLException {
        try(Connection conn = this.dbConn.connect()) {

            //Prepare sql statement
            String query = "SELECT * FROM milestone;";

            try (PreparedStatement preparedStatement = conn.prepareStatement(query)) {

                //Set parameters and execute query
                try (ResultSet result = preparedStatement.executeQuery()) {

                    List<Milestone> milestoneList = new ArrayList<>();
                    while (result.next()) {
                        milestoneList.add(new Milestone(result.getInt("milestoneID"),
                                                        result.getString("name"),
                                                        result.getString("description"),
                                                        result.getDate("createdDate"),
                                                        result.getDate("lastUpdatedDate"),
                                                        result.getDate("startDate"),
                                                        result.getDate("deliveryDate"),
                                                        result.getDate("completedDate"),
                                                        result.getBoolean("archived"),
                                                        this.companyBusiness.getCompanyByID(result.getString("companyID")))
                        );
                    }

                    //Return milestone
                    return milestoneList;
                }
            }
        }
        catch(NullPointerException npe){
            throw new BadRequestException("Null pointer in data layer");
        }
    }

    /**
     * Get all milestones by CompanyID
     * @param companyIDList List of company IDs to retrieve milestones by
     * @return List of milestone objects assigned to company with companyID
     * @throws SQLException Error connecting to database or executing query
     */
    public List<Milestone> getAllMilestones(List<Integer> companyIDList) throws SQLException {
        try(Connection conn = this.dbConn.connect()) {

            //Prepare sql statement
            String query = "SELECT * FROM milestone WHERE companyID IN ?;";

            try (PreparedStatement preparedStatement = conn.prepareStatement(query)) {
                preparedStatement.setArray(1, (java.sql.Array)companyIDList);

                //Set parameters and execute query
                try (ResultSet result = preparedStatement.executeQuery()) {

                    List<Milestone> milestoneList = new ArrayList<>();
                    while (result.next()) {
                        milestoneList.add(new Milestone(result.getInt("milestoneID"),
                                result.getString("name"),
                                result.getString("description"),
                                result.getDate("createdDate"),
                                result.getDate("lastUpdatedDate"),
                                result.getDate("startDate"),
                                result.getDate("deliveryDate"),
                                result.getDate("completedDate"),
                                result.getBoolean("archived"),
                                this.companyBusiness.getCompanyByID(result.getString("companyID")))
                        );
                    }

                    //Return milestone
                    return milestoneList;
                }
            }
        }
    }

    /**
     * Get all milestones based on archived or not archived
     * @param archived Search for either archived or unarchived milestones
     * @return List of milestone objects
     * @throws SQLException Error connecting to database or executing query
     */
    public List<Milestone> getAllMilestones(boolean archived) throws SQLException {
        try(Connection conn = this.dbConn.connect()) {

            //Prepare sql statement
            String query = "SELECT * FROM milestone WHERE archived = ?;";

            try (PreparedStatement preparedStatement = conn.prepareStatement(query)) {
                preparedStatement.setBoolean(1, archived);

                //Set parameters and execute query
                try (ResultSet result = preparedStatement.executeQuery()) {

                    List<Milestone> milestoneList = new ArrayList<>();
                    while (result.next()) {
                        milestoneList.add(new Milestone(result.getInt("milestoneID"),
                                result.getString("name"),
                                result.getString("description"),
                                result.getDate("createdDate"),
                                result.getDate("lastUpdatedDate"),
                                result.getDate("startDate"),
                                result.getDate("deliveryDate"),
                                result.getDate("completedDate"),
                                result.getBoolean("archived"),
                                this.companyBusiness.getCompanyByID(result.getString("companyID")))
                        );
                    }

                    //Return milestone
                    return milestoneList;
                }
            }
        }
    }

    /**
     * Get all milestones based on archived or not archived
     * @param companyIDList List of company IDs to retrieve milestones by
     * @param archived Search for either archived or unarchived milestones
     * @return List of milestone objects
     * @throws SQLException Error connecting to database or executing query
     */
    public List<Milestone> getAllMilestones(List<Integer> companyIDList, boolean archived) throws SQLException {
        try(Connection conn = this.dbConn.connect()) {

            //Prepare sql statement
            String query = "SELECT * FROM milestone WHERE archived = ? AND companyID IN ?;";

            try (PreparedStatement preparedStatement = conn.prepareStatement(query)) {
                preparedStatement.setBoolean(1, archived);
                preparedStatement.setArray(2, (java.sql.Array)companyIDList);

                //Set parameters and execute query
                try (ResultSet result = preparedStatement.executeQuery()) {

                    List<Milestone> milestoneList = new ArrayList<>();
                    while (result.next()) {
                        milestoneList.add(new Milestone(result.getInt("milestoneID"),
                                result.getString("name"),
                                result.getString("description"),
                                result.getDate("createdDate"),
                                result.getDate("lastUpdatedDate"),
                                result.getDate("startDate"),
                                result.getDate("deliveryDate"),
                                result.getDate("completedDate"),
                                result.getBoolean("archived"),
                                this.companyBusiness.getCompanyByID(result.getString("companyID")))
                        );
                    }

                    //Return milestone
                    return milestoneList;
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
    public Milestone getMilestoneByID(final int milestoneID) throws SQLException {
        try(Connection conn = this.dbConn.connect()) {

            //Prepare sql statement
            String query = "SELECT * FROM milestone WHERE milestone.milestoneID = ?;";

            try (PreparedStatement preparedStatement = conn.prepareStatement(query)) {

                //Set parameters and execute query
                preparedStatement.setInt(1, milestoneID);
                try (ResultSet result = preparedStatement.executeQuery()) {

                    Milestone milestone = null;

                    while (result.next()) {
                        milestone = new Milestone(result.getInt("milestoneID"),
                                result.getString("name"),
                                result.getString("description"),
                                result.getDate("createdDate"),
                                result.getDate("lastUpdatedDate"),
                                result.getDate("startDate"),
                                result.getDate("deliveryDate"),
                                result.getDate("completedDate"),
                                result.getBoolean("archived"),
                                this.companyBusiness.getCompanyByID(result.getString("companyID"))
                        );
                    }

                    //Return milestone
                    return milestone;
                }
            }
        }
    }

    /**
     * Connect to database and add a new concrete milestone
     * @param name name of new milestone to be added
     * @param description description of new milestone to be added
     * @param deliveryDate deliveryDate of new milestone to be added
     * @param companyID companyID of new milestone to be added
     * @return inserted milestones generated milestoneID
     * @throws SQLException Error connecting to database or executing update
     */
    public int insertMilestone(final String name, final String description, final Date createdDate, final Date startDate, final Date deliveryDate, final int companyID) throws SQLException {
        //Prepare sql statement
        String query = "INSERT INTO milestone (name, description, createdDate, lastUpdatedDate, startDate, deliveryDate, companyID) VALUES (?, ?, ?, ?, ?, ?, ?);";

        try(Connection conn = this.dbConn.connect();
            PreparedStatement preparedStatement = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            //Set parameters and execute update
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, description);
            preparedStatement.setDate(3, new java.sql.Date(createdDate.getTime()));
            preparedStatement.setDate(4, new java.sql.Date(createdDate.getTime()));
            preparedStatement.setDate(5, new java.sql.Date(startDate.getTime()));
            preparedStatement.setDate(6, new java.sql.Date(deliveryDate.getTime()));
            preparedStatement.setInt(7, companyID);

            preparedStatement.executeUpdate();

            try(ResultSet insertedKeys = preparedStatement.getGeneratedKeys()) {
                insertedKeys.next();
                return insertedKeys.getInt(1);
            }
        }
    }

    /**
     * Update an existing milestone
     * @param milestoneID ID of milestone to update
     * @param name Updated name for the milestone
     * @param description Updated description for the new milestone
     * @param startDate Updated start date for the new milestone
     * @param deliveryDate Updated delivery date for the new milestone
     * @param companyID Updated company ID of company to assign milestone to
     * @throws SQLException Error connecting to database or executing statement
     */
    public void updateMilestone(final int milestoneID, final String name, final String description, final Date lastUpdatedDate, final Date startDate, final Date deliveryDate, final int companyID) throws SQLException {
        //Prepare sql statement
        String query = "UPDATE milestone SET name = ?, description = ?, lastUpdatedDate = ?, startDate = ?, deliveryDate = ?, companyID = ? WHERE milestoneID = ?;";

        try(Connection conn = this.dbConn.connect();
            PreparedStatement preparedStatement = conn.prepareStatement(query)) {

            //Set parameters and execute update
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, description);
            preparedStatement.setDate(3, new java.sql.Date(lastUpdatedDate.getTime()));
            preparedStatement.setDate(4, new java.sql.Date(startDate.getTime()));
            preparedStatement.setDate(5, new java.sql.Date(deliveryDate.getTime()));
            preparedStatement.setInt(6, companyID);
            preparedStatement.setInt(7, milestoneID);

            preparedStatement.executeUpdate();
        }
    }

    /**
     * Update archive status of an existing milestone
     * @param milestoneID ID of milestone to update
     * @param archiveStatus boolean to set archive status to
     * @throws SQLException Error connecting to database or executing statement
     */
    public void updateMilestoneArchiveStatus(final int milestoneID, boolean archiveStatus) throws SQLException {
        //Prepare sql statement
        String query = "UPDATE milestone SET archived = ? WHERE milestoneID = ?;";

        try(Connection conn = this.dbConn.connect();
            PreparedStatement preparedStatement = conn.prepareStatement(query)) {

            //Set parameters and execute update
            preparedStatement.setBoolean(1, archiveStatus);
            preparedStatement.setInt(2, milestoneID);

            preparedStatement.executeUpdate();
        }
    }

    /**
     * Connect to database and delete a milestone by milestoneID
     * @param milestoneID milestoneID to delete from database
     * @return number of deleted rows
     * @throws SQLException Error connecting to database or executing update
     */
    public int deleteMilestoneByID(final int milestoneID) throws SQLException {
        //Prepare sql statement
        String query = "DELETE FROM milestone WHERE milestone.milestoneID = ?;";

        try(Connection conn = this.dbConn.connect();
            PreparedStatement preparedStatement = conn.prepareStatement(query)) {

            //Set parameters and execute update
            preparedStatement.setInt(1, milestoneID);
            return preparedStatement.executeUpdate();
        }
    }
}