package blink.datalayer;

import blink.utility.objects.Milestone;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MilestoneDB {
    private DBConn dbConn;

    /**
     * Get all milestones
     * @return List of milestone objects
     * @throws SQLException - Error connecting to database or executing query
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
                                                        result.getInt("companyID")));
                    }

                    //Return milestone
                    return milestoneList;
                }
            }
        }
    }

    /**
     * Get all milestones based on archived or not archived
     * 1 - archived milestones
     * 0 - active milestones
     * @return List of milestone objects
     * @throws SQLException - Error connecting to database or executing query
     */
    public List<Milestone> getAllMilestones(int archived) throws SQLException {
        try(Connection conn = this.dbConn.connect()) {

            //Prepare sql statement
            String query = "SELECT * FROM milestone WHERE archived = ?;";

            try (PreparedStatement preparedStatement = conn.prepareStatement(query)) {
                preparedStatement.setInt(1, archived);

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
                                result.getInt("companyID")));
                    }

                    //Return milestone
                    return milestoneList;
                }
            }
        }
    }

    /**
     * Get milestone information based on the milestoneID
     * @param milestoneID - milestoneID to retrieve milestone from
     * @return milestone object or null if not found
     * @throws SQLException - Error connecting to database or executing query
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
                                result.getInt("companyID"));
                    }

                    //Return milestone
                    return milestone;
                }
            }
        }
    }

    /**
     * Connect to database and add a new milestone template
     * @param milestoneID - milestoneID of new milestone to be added
     * @param name - name of new milestone to be added
     * @param description - description of new milestone to be added
     * @param createdDate - The date that this milestone has been created
     * @throws SQLException - Error connecting to database or executing update
     */
    public void insertMilestone(final int milestoneID, final String name, final String description, final Date createdDate) throws SQLException {
        //Prepare sql statement
        String query = "INSERT INTO milestone (milestoneID, name, description, createdDate) VALUES (?, ?, ?, ?);";

        try(Connection conn = this.dbConn.connect();
            PreparedStatement preparedStatement = conn.prepareStatement(query)) {

            //Set parameters and execute update
            preparedStatement.setInt(1, milestoneID);
            preparedStatement.setString(2, name);
            preparedStatement.setString(3, description);
            preparedStatement.setDate(4, createdDate);

            preparedStatement.executeUpdate();
        }
    }

    /**
     * Connect to database and add a new concrete milestone
     * @param milestoneID - milestoneID of new milestone to be added
     * @param name - name of new milestone to be added
     * @param description - description of new milestone to be added
     * @param deliveryDate - deliveryDate of new milestone to be added
     * @param companyID - companyID of new milestone to be added
     * @throws SQLException - Error connecting to database or executing update
     */
    public void insertMilestone(final int milestoneID, final String name, final String description, final Date createdDate, final Date startDate, final Date deliveryDate, final int companyID) throws SQLException {
        //Prepare sql statement
        String query = "INSERT INTO milestone (milestoneID, name, description, createdDate, lastUpdatedDate, startDate, deliveryDate, companyID) VALUES (?, ?, ?, ?, ?, ?, ?, ?);";

        try(Connection conn = this.dbConn.connect();
            PreparedStatement preparedStatement = conn.prepareStatement(query)) {

            //Set parameters and execute update
            preparedStatement.setInt(1, milestoneID);
            preparedStatement.setString(2, name);
            preparedStatement.setString(3, description);
            preparedStatement.setDate(4, createdDate);
            preparedStatement.setDate(5, createdDate);
            preparedStatement.setDate(6, startDate);
            preparedStatement.setDate(7, deliveryDate);
            preparedStatement.setInt(8, companyID);

            preparedStatement.executeUpdate();
        }
    }

    /**
     * Connect to database and delete a milestone by milestoneID
     * @param milestoneID - milestoneID to delete from database
     * @return number of deleted rows
     * @throws SQLException - Error connecting to database or executing update
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