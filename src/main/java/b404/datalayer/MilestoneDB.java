package b404.datalayer;

import b404.utility.objects.Milestone;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MilestoneDB {
    DBConn dbConn;

    /**
     * Get milestone information based on the milestoneID
     * @param milestoneID - milestoneID to retrieve milestone from
     * @return milestone object or null if not found
     * @throws SQLException - Error connecting to database or executing query
     */
    public Milestone getMilestoneByID(int milestoneID) throws SQLException {
        this.dbConn.connect();

        //Prepare sql statement
        String query = "SELECT * FROM milestone WHERE milestone.milestoneID = ?;";
        PreparedStatement preparedStatement = this.dbConn.conn.prepareStatement(query);

        //Set parameters and execute query
        preparedStatement.setInt(1, milestoneID);
        ResultSet result = preparedStatement.executeQuery();

        Milestone milestone = null;

        while(result.next()) {
            milestone = new Milestone(result.getInt("milestoneID"),
                                      result.getInt("orderNumber"),
                                      result.getString("name"),
                                      result.getString("description"),
                                      result.getDate("deliveryDate"),
                                      result.getInt("companyID"));
        }

        //Close the database
        this.dbConn.close();

        //Return milestone
        return milestone;
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
    public void insertMilestone(int milestoneID, int orderNumber, String name, String description, Date deliveryDate, int companyID) throws SQLException {
        this.dbConn.connect();

        //Prepare sql statement
        String query = "INSERT INTO milestone (milestoneID, orderNumber, name, description, deliveryDate, companyID) VALUES (?, ?, ?, ?, ?, ?);";
        PreparedStatement preparedStatement = this.dbConn.conn.prepareStatement(query);

        //Set parameters and execute update
        preparedStatement.setInt(1, milestoneID);
        preparedStatement.setInt(2, orderNumber);
        preparedStatement.setString(3, name);
        preparedStatement.setString(4, description);
        preparedStatement.setDate(5, deliveryDate);
        preparedStatement.setInt(6, companyID);

        preparedStatement.executeUpdate();

        //Close the database
        this.dbConn.close();
    }

    /**
     * Connect to database and delete a milestone by milestoneID
     * @param milestoneID - milestoneID to delete from database
     * @return number of deleted rows
     * @throws SQLException - Error connecting to database or executing update
     */
    public int deleteMilestoneByID(int milestoneID) throws SQLException {
        this.dbConn.connect();

        //Prepare sql statement
        String query = "DELETE FROM milestone WHERE milestone.milestoneID = ?;";
        PreparedStatement preparedStatement = this.dbConn.conn.prepareStatement(query);

        //Set parameters and execute update
        preparedStatement.setInt(1, milestoneID);
        int numRowsDeleted = preparedStatement.executeUpdate();

        //Close the database
        this.dbConn.close();

        //Return the number of rows deleted
        return numRowsDeleted;
    }
}
