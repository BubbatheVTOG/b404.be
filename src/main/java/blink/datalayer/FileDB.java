package blink.datalayer;

import blink.utility.objects.File;

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

        try(Connection conn = this.dbConn.connect();
            PreparedStatement preparedStatement = conn.prepareStatement(query)) {

            //Set parameters and execute query
            preparedStatement.setInt(1, fileID);
            try (ResultSet result = preparedStatement.executeQuery()) {

                File file = null;

                while (result.next()) {
                    file = new File(result.getInt("fileID"),
                            result.getString("name"),
                            file.convertFileToBase64(result.getBlob("file")),
                            result.getBoolean("confidential"),
                            result.getInt("stepID"));
                }
                return file;
            }
        }
    }

    /**
     * Return all files in the database
     * @return List<files> containing all files in the database
     * @throws SQLException error connecting to database or executing query
     */
    public List<File> getAllFiles() throws SQLException {
        // List of files
        List<File> files = new ArrayList<>();

        //Prepare sql statement
        String query = "SELECT * FROM file";

        try(Connection conn = this.dbConn.connect();
            PreparedStatement preparedStatement = conn.prepareStatement(query)) {

            //Execute Query
            try (ResultSet result = preparedStatement.executeQuery()) {
                File file = null;

                while(result.next()) {
                    file = new File(result.getInt("fileID"),
                            result.getString("name"),
                            file.convertFileToBase64(result.getBlob("file")),
                            result.getBoolean("confidential"),
                            result.getInt("stepID"));

                    files.add(file);
                }
                return files;
            }
        }
    }

    public List<File> getAllFilesByMilestone(int milestoneID) throws SQLException {
        List<File> files = new ArrayList<>();

        String query = "SELECT * FROM file join step on step.fileID = file.fileID " +
                                          "join workflow on workflow.workflowID = step.workflowID " +
                                          "join milestone on workflow.milestoneID = milestone.milestoneID " +
                                          "WHERE milestone.milestoneID = ?;";

        try(Connection conn = this.dbConn.connect();
            PreparedStatement preparedStatement = conn.prepareStatement(query)) {

            preparedStatement.setInt(1, milestoneID);

            try(ResultSet result = preparedStatement.executeQuery()) {
                File file = null;

                while(result.next()) {
                    file = new File(result.getInt("fileID"),
                            result.getString("name"),
                            file.convertFileToBase64(result.getBlob("file")),
                            result.getBoolean("confidential"),
                            result.getInt("stepID"));

                    files.add(file);
                }
                return files;
            }
        }
    }


    /**
     * Connect to database and add
     * @param name name of new file to be added
     * @param file file of new file to be added
     * @param confidential of new file to be added
     * @param stepID stepID of new file to be added
     * @throws SQLException Error connecting to database or executing update
     */
    public int insertFile(final String name, final Blob file, final boolean confidential, final int stepID) throws SQLException {
        //Prepare sql statement
        String query = "INSERT INTO file (name, file, confidential, stepID) VALUES (?, ?, ?, ?);";

        try(Connection conn = this.dbConn.connect();
            PreparedStatement preparedStatement = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            //Set parameters and execute update
            preparedStatement.setString(1, name);
            preparedStatement.setBlob(2, file);
            preparedStatement.setBoolean(3, confidential);
            preparedStatement.setInt(4, stepID);

            preparedStatement.executeUpdate();

            try(ResultSet resultSet = preparedStatement.getGeneratedKeys()) {
                return resultSet.getInt(1);
            }
        }
    }

    /**
     * Connect to database and add
     * @param name name of new file to be added
     * @param file file of new file to be added
     * @param confidential of new file to be added
     * @throws SQLException Error connecting to database or executing update
     */
    public int insertFile(final String name, final Blob file, final boolean confidential) throws SQLException {
        //Prepare sql statement
        String query = "INSERT INTO file (name, file, confidential, stepID) VALUES (?, ?, ?);";

        try(Connection conn = this.dbConn.connect();
            PreparedStatement preparedStatement = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            //Set parameters and execute update
            preparedStatement.setString(1, name);
            preparedStatement.setBlob(2, file);
            preparedStatement.setBoolean(3, confidential);

            preparedStatement.executeUpdate();

            try(ResultSet resultSet = preparedStatement.getGeneratedKeys()) {
                return resultSet.getInt(1);
            }
        }
    }

    public int updateFile(final String name, final Blob file, final boolean confidential, final int stepID, final int fileID) throws SQLException {
        //Prepare sql statement
        String query = "UPDATE file SET file.name = ?, file.file = ?, file.confidential = ?, file.stepID = ? WHERE file.filedID = ?;";

        try(Connection conn = this.dbConn.connect();
            PreparedStatement preparedStatement = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            //Set parameters and execute update
            preparedStatement.setString(1, name);
            preparedStatement.setBlob(2, file);
            preparedStatement.setBoolean(3, confidential);
            preparedStatement.setInt(4, stepID);
            preparedStatement.setInt(5, fileID);

            preparedStatement.executeUpdate();

            try(ResultSet resultSet = preparedStatement.getGeneratedKeys()) {
                return resultSet.getInt(1);
            }
        }
    }

    public int updateFile(final String name, final Blob file, final boolean confidential, final int fileID) throws SQLException {
        //Prepare sql statement
        String query = "UPDATE file SET file.name = ?, file.file = ?, file.confidential = ? WHERE file.filedID = ?;";

        try(Connection conn = this.dbConn.connect();
            PreparedStatement preparedStatement = conn.prepareStatement(query)) {

            //Set parameters and execute update
            preparedStatement.setString(1, name);
            preparedStatement.setBlob(2, file);
            preparedStatement.setBoolean(3, confidential);
            preparedStatement.setInt(4, fileID);

            preparedStatement.executeUpdate();

            try(ResultSet resultSet = preparedStatement.getGeneratedKeys()) {
                return resultSet.getInt(1);
            }
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
}