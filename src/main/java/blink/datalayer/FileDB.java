package blink.datalayer;

import blink.utility.objects.File;

import javax.validation.constraints.Null;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.InternalServerErrorException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FileDB {
    private DBConn dbConn;

    public FileDB(){
        this.dbConn = new DBConn();
    }

    /**
     * Get file information based on the fileID
     * @param fileID fileID to retrieve file from
     * @return file object or null if not found
     * @throws SQLException Error connecting to database or executing query
     */
    public File getFileByID(int fileID) throws SQLException {
        //Prepare sql statement
        String query = "SELECT * FROM file WHERE file.fileID = ?;";

        try (Connection conn = this.dbConn.connect();
             PreparedStatement preparedStatement = conn.prepareStatement(query)) {

            //Set parameters and execute query
            preparedStatement.setInt(1, fileID);
            try (ResultSet result = preparedStatement.executeQuery()) {

                File file = null;
                while (result.next()) {
                    file = new File(result.getInt("fileID"),
                            result.getString("name"),
                            result.getBlob("file"),
                            result.getBoolean("confidential"));
                }
                return file;
            }
        }
    }

    public List<File> getAllFilesByMilestone(int milestoneID) throws SQLException {
        List<File> files = new ArrayList<>();

        String query = "SELECT DISTINCT file.fileID, file.name, file.file, file.confidential FROM file " +
                          "JOIN step on step.fileID = file.fileID " +
                          "JOIN workflow on workflow.workflowID = step.workflowID " +
                          "JOIN milestone on workflow.milestoneID = milestone.milestoneID " +
                          "WHERE milestone.milestoneID = ? " +
                          "AND file.fileID > 0;";

        try(Connection conn = this.dbConn.connect();
            PreparedStatement preparedStatement = conn.prepareStatement(query)) {

            preparedStatement.setInt(1, milestoneID);

            try(ResultSet result = preparedStatement.executeQuery()) {
                while(result.next()) {
                    files.add(new File(result.getInt("fileID"),
                                    result.getString("name"),
                                    result.getBlob("file"),
                                    result.getBoolean("confidential")));
                }
                return files;
            }
        }
    }

    public List<File> getAllFilesByCompany(int companyID) throws SQLException {
        List<File> files = new ArrayList<>();

        String query = "SELECT DISTINCT file.fileID, file.name, file.file, file.confidential FROM file " +
                "JOIN step on step.fileID = file.fileID " +
                "JOIN workflow on workflow.workflowID = step.workflowID " +
                "JOIN milestone on workflow.milestoneID = milestone.milestoneID " +
                "JOIN company on company.companyID = milestone.companyID " +
                "WHERE company.companyID = ?;";

        try(Connection conn = this.dbConn.connect();
            PreparedStatement preparedStatement = conn.prepareStatement(query)) {

            preparedStatement.setInt(1, companyID);

            try(ResultSet result = preparedStatement.executeQuery()) {
                while(result.next()) {
                    files.add(new File(result.getInt("fileID"),
                            result.getString("name"),
                            result.getBlob("file"),
                            result.getBoolean("confidential")));
                }
                return files;
            }
        }
    }

    /**
     * Connect to database and add
     * @param file file to insert into the database
     * @throws SQLException Error connecting to database or executing update
     */
    public int insertFile(File file) throws SQLException {
        //Prepare sql statement
        String query = "INSERT INTO file (name, file, confidential) VALUES (?, ?, ?);";

        try(Connection conn = this.dbConn.connect();
            PreparedStatement preparedStatement = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            //Set parameters and execute update
            preparedStatement.setString(1, file.getName());
            preparedStatement.setBlob(2, file.getBlobFile());
            preparedStatement.setBoolean(3, file.getConfidential());

            preparedStatement.executeUpdate();

            try(ResultSet resultSet = preparedStatement.getGeneratedKeys()) {
                resultSet.next();
                return resultSet.getInt(1);
            }
        }
    }


    /**
     * Connect to database and add
     * @param file existing file to update into the database
     * @throws SQLException Error connecting to database or executing update
     */
    public void updateFile(File file) throws SQLException {
        try {
            //Prepare sql statement
            String query = "UPDATE file SET file.name = ?, file.file = ?, file.confidential = ? WHERE file.fileID = ?;";

            try (Connection conn = this.dbConn.connect();
                 PreparedStatement preparedStatement = conn.prepareStatement(query)) {

                //Set parameters and execute update
                preparedStatement.setString(1, file.getName());
                preparedStatement.setBlob(2, file.getBlobFile());
                preparedStatement.setBoolean(3, file.getConfidential());
                preparedStatement.setInt(4, file.getFileID());

                preparedStatement.executeUpdate();
            }
        }
        catch(NullPointerException npe){
            throw new BadRequestException("Error in file data layer");
        }
    }

    /**
     * Connect to database and delete a file by fileID
     * @param fileID fileID to delete from database
     * @return the number of deleted rows
     * @throws SQLException Error connecting to database or executing update
     */
    public int deleteFileByFileID(final int fileID) throws SQLException {
        //Prepare the sql statement
        String query = "DELETE FROM file WHERE file.fileID = ?;";

        try(Connection conn = this.dbConn.connect();
            PreparedStatement preparedStatement = conn.prepareStatement(query)) {

            //Set parameters and execute update
            preparedStatement.setInt(1, fileID);
            return preparedStatement.executeUpdate();
        }
    }

    /**
     * Gets the company which the file belongs to
     * @param fileID id of file to search for
     * @return companyID of company to which file belongs
     * @throws SQLException error connecting to database or executing query
     */
    public int getFileCompanyID(int fileID) throws SQLException {
        //Prepare sql statement
        int companyID = 0;
        String query = "SELECT milestone.companyID FROM file " +
                            "JOIN step ON (file.fileID = step.fileID) " +
                            "JOIN workflow ON (workflow.workflowID = step.workflowID) " +
                            "JOIN milestone ON (milestone.milestoneID = workflow.milestoneID) " +
                            "WHERE file.fileID = ?;";

        try(Connection conn = this.dbConn.connect();
            PreparedStatement preparedStatement = conn.prepareStatement(query)) {

            //Set parameters and execute query
            preparedStatement.setInt(1, fileID);
            try (ResultSet result = preparedStatement.executeQuery()) {

                while (result.next()) {
                    companyID = result.getInt("companyID");
                }
            }
        }
        return companyID;
    }
}