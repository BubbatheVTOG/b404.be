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
                                result.getInt("orderNumber"),
                                result.getString("name"),
                                result.getString("description"),
                                result.getDate("deliveryDate"),
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
                                result.getInt("orderNumber"),
                                result.getString("name"),
                                result.getString("description"),
                                result.getDate("deliveryDate"),
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
                                result.getInt("orderNumber"),
                                result.getString("name"),
                                result.getString("description"),
                                result.getDate("deliveryDate"),
                                result.getInt("companyID"));
                    }

                    //Return milestone
                    return milestone;
                }
            }
        }
    }

    /**
     * Connect to database and add
     * @param milestoneID - milestoneID of new milestone to be added
     * @param orderNumber - orderNumber of new milestone to be added
     * @param name - name of new milestone to be added
     * @param description - description of new milestone to be added
     * @param deliveryDate - deliveryDate of new milestone to be added
     * @param companyID - companyID of new milestone to be added
     * @throws SQLException - Error connecting to database or executing update
     */
    public void insertMilestone(final int milestoneID, final int orderNumber, final String name, final String description, final Date deliveryDate, final int companyID) throws SQLException {
        //Prepare sql statement
        String query = "INSERT INTO milestone (milestoneID, orderNumber, name, description, deliveryDate, companyID) VALUES (?, ?, ?, ?, ?, ?);";

        try(Connection conn = this.dbConn.connect();
            PreparedStatement preparedStatement = conn.prepareStatement(query)) {

            //Set parameters and execute update
            preparedStatement.setInt(1, milestoneID);
            preparedStatement.setInt(2, orderNumber);
            preparedStatement.setString(3, name);
            preparedStatement.setString(4, description);
            preparedStatement.setDate(5, deliveryDate);
            preparedStatement.setInt(6, companyID);

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