package blink.datalayer;

import blink.businesslayer.CompanyBusiness;
import blink.utility.objects.Milestone;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MilestoneDB {
    private DBConn dbConn;
    private CompanyBusiness companyBusiness;

    public MilestoneDB(){
        this.dbConn = new DBConn();
        this.companyBusiness = new CompanyBusiness();
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
    }

    /**
     * Get all milestones by CompanyID
     * @param companyIDList List of company IDs to retrieve milestones by
     * @return List of milestone objects assigned to company with companyID
     * @throws SQLException Error connecting to database or executing query
     */
    public List<Milestone> getAllMilestones(List<Integer> companyIDList) throws SQLException {
        if(companyIDList.isEmpty()){
            return new ArrayList();
        }

        try(Connection conn = this.dbConn.connect()) {

            //Prepare sql statement
            StringBuilder query = new StringBuilder();
            query.append("SELECT * FROM milestone " +
                            "WHERE milestone.companyID IN (");
            for(int x = 0; x < companyIDList.size(); x++){
                if(x == companyIDList.size()-1){ query.append("?);"); }
                else{ query.append("?,"); }
            }

            try (PreparedStatement preparedStatement = conn.prepareStatement(query.toString())) {
                for(int x = 0; x < companyIDList.size(); x++){
                    preparedStatement.setInt(x+1, companyIDList.get(x));
                }

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
            StringBuilder query = new StringBuilder();
            query.append("SELECT * FROM milestone " +
                    "WHERE archived = ? " +
                    "AND milestone.companyID IN (");
            for(int x = 0; x < companyIDList.size(); x++){
                if(x == companyIDList.size()-1){ query.append("?);"); }
                else{ query.append("?,"); }
            }

            try (PreparedStatement preparedStatement = conn.prepareStatement(query.toString())) {
                preparedStatement.setBoolean(1, archived);
                for(int x = 0; x < companyIDList.size(); x++){
                    preparedStatement.setInt(x+2, companyIDList.get(x));
                }

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
            preparedStatement.setTimestamp(3, new java.sql.Timestamp(createdDate.getTime()));
            preparedStatement.setTimestamp(4, new java.sql.Timestamp(createdDate.getTime()));
            preparedStatement.setTimestamp(5, new java.sql.Timestamp(startDate.getTime()));
            preparedStatement.setTimestamp(6, new java.sql.Timestamp(deliveryDate.getTime()));
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
            preparedStatement.setTimestamp(3, new java.sql.Timestamp(lastUpdatedDate.getTime()));
            preparedStatement.setTimestamp(4, new java.sql.Timestamp(startDate.getTime()));
            preparedStatement.setTimestamp(5, new java.sql.Timestamp(deliveryDate.getTime()));
            preparedStatement.setInt(6, companyID);
            preparedStatement.setInt(7, milestoneID);

            preparedStatement.executeUpdate();
        }
    }

    /**
     * Update archive status of an existing milestone and all relevant workflows
     * @param milestoneID ID of milestone to update
     * @param archiveStatus boolean to set archive status to
     * @throws SQLException Error connecting to database or executing statement
     */
    public void updateMilestoneArchiveStatus(final int milestoneID, boolean archiveStatus) throws SQLException {
        //Prepare sql statement
        String milestoneQuery = "UPDATE milestone SET archived = ? WHERE milestoneID = ?;";
        String workflowQuery = "UPDATE workflow SET archived = ? WHERE milestoneID = ?;";

        try(Connection conn = this.dbConn.connect();
            PreparedStatement milestonePS = conn.prepareStatement(milestoneQuery);
            PreparedStatement workflowPS = conn.prepareStatement(workflowQuery)) {
            conn.setAutoCommit(false);

            //Set parameters and execute update
            milestonePS.setBoolean(1, archiveStatus);
            milestonePS.setInt(2, milestoneID);

            workflowPS.setBoolean(1, archiveStatus);
            workflowPS.setInt(2, milestoneID);

            milestonePS.executeUpdate();
            workflowPS.executeUpdate();

            conn.commit();
            conn.setAutoCommit(true);
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

    /**
     * Connect to db and update milestone completion date
     * @param completeDate
     * @param milestoneID
     * @throws SQLException
     */
    public void markMilestoneComplete(Date completeDate, int milestoneID) throws SQLException {
        //Prepare sql statement
        String query = "UPDATE milestone SET milestone.completedDate = ? WHERE milestone.milestoneID = ?;";

        try(Connection conn = this.dbConn.connect();
            PreparedStatement preparedStatement = conn.prepareStatement(query)) {

            //Set parameters and execute update
            preparedStatement.setTimestamp(1, new java.sql.Timestamp(completeDate.getTime()));
            preparedStatement.setInt(2, milestoneID);

            preparedStatement.executeUpdate();
        }
    }
}