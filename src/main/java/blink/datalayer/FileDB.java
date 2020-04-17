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

        try (Connection conn = this.dbConn.connect();
             PreparedStatement preparedStatement = conn.prepareStatement(query)) {

            //Set parameters and execute query
            preparedStatement.setInt(1, fileID);
            try (ResultSet result = preparedStatement.executeQuery()) {

                File file = null;
                while (result.next()) {
                    int id = result.getInt("fileID");
                    String name = result.getString("name");
                    Blob blob;
                    if(result.getBlob("file") == null) {
                        blob = conn.createBlob();
                    } else {
                        blob = result.getBlob("file");
                    }

                    boolean confidential = result.getBoolean("confidential");
                    boolean form = result.getBoolean("form");
                    file = new File(id, name, blob, confidential, form);
                }
                return file;
            }
        }
    }

    /**
     * Returns all concrete files
     * @return List<File> files
     * @throws SQLException Error connecting to the database or executing the query
     */
    public List<File> getAllConcreteFiles() throws SQLException {
        List<File> files = new ArrayList<>();

        String query = "SELECT DISTINCT file.fileID, file.name, file.file, file.confidential, file.form FROM file JOIN step ON step.fileID = file.fileID JOIN workflow ON workflow.workflowID = step.workflowID JOIN milestone ON milestone.milestoneID = workflow.milestoneID AND file.fileID != 0;";

        try(Connection conn = this.dbConn.connect();
            PreparedStatement preparedStatement = conn.prepareStatement(query)) {

            try(ResultSet result = preparedStatement.executeQuery()) {
                while(result.next()) {
                    int id = result.getInt("fileID");
                    String name = result.getString("name");
                    Blob blob;
                    if(result.getBlob("file") == null) {
                        blob = conn.createBlob();
                    } else {
                        blob = result.getBlob("file");
                    }
                    boolean confidential = result.getBoolean("confidential");
                    boolean form = result.getBoolean("form");
                    files.add(new File(id, name, blob, confidential, form));
                }
                return files;
            }
        }
    }

    /**
     *
     * Returns all concrete files
     * @param uuid
     * @return List<File> files
     * @throws SQLException Error connecting to the database or executing the query
     */
    public List<File> getAllConcreteFiles(String uuid) throws SQLException {
        List<File> files = new ArrayList<>();

        String query = "SELECT DISTINCT file.fileID, file.name, file.file, file.confidential, file.form FROM file JOIN step ON step.fileID = file.fileID JOIN workflow ON workflow.workflowID = step.workflowID JOIN milestone ON milestone.milestoneID = workflow.milestoneID JOIN personCompany ON personCompany.companyID = milestone.companyID WHERE personCompany.uuid = ? AND file.fileID != 0;";

        try(Connection conn = this.dbConn.connect();
            PreparedStatement preparedStatement = conn.prepareStatement(query)) {
            preparedStatement.setString(1, uuid);

            try(ResultSet result = preparedStatement.executeQuery()) {
                while(result.next()) {
                    int id = result.getInt("fileID");
                    String name = result.getString("name");
                    Blob blob;
                    if(result.getBlob("file") == null) {
                        blob = conn.createBlob();
                    } else {
                        blob = result.getBlob("file");
                    }
                    boolean confidential = result.getBoolean("confidential");
                    boolean form = result.getBoolean("form");
                    files.add(new File(id, name, blob, confidential, form));
                }
                return files;
            }
        }
    }

    /**
     * Returns all template files without an ID of 0
     * @return List<File> files
     * @throws SQLException Error connecting to the database or executing the query
     */
    public List<File> getAllTemplateFiles() throws SQLException {
        List<File> files = new ArrayList<>();

        String query = "SELECT DISTINCT * FROM file WHERE file.fileID NOT IN " +
                            "(SELECT step.fileID FROM step " +
                                "JOIN workflow ON (step.workflowID = workflow.workflowID) " +
                                "JOIN milestone ON (workflow.milestoneID = milestone.milestoneID));";

        try(Connection conn = this.dbConn.connect();
            PreparedStatement preparedStatement = conn.prepareStatement(query)) {

            try(ResultSet result = preparedStatement.executeQuery()) {
                while(result.next()) {
                    int id = result.getInt("fileID");
                    String name = result.getString("name");
                    Blob blob;
                    if(result.getBlob("file") == null) {
                        blob = conn.createBlob();
                    } else {
                        blob = result.getBlob("file");
                    }
                    boolean confidential = result.getBoolean("confidential");
                    boolean form = result.getBoolean("form");
                    files.add(new File(id, name, blob, confidential, form));
                }
                return files;
            }
        }
    }

    public List<File> getAllFilesByMilestone(int milestoneID) throws SQLException {
        List<File> files = new ArrayList<>();

        String query = "SELECT DISTINCT file.fileID, file.name, file.file, file.confidential, file.form FROM file " +
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
                                    result.getBoolean("confidential"),
                                    result.getBoolean("form")));
                }
                return files;
            }
        }
    }

    public List<File> getAllFilesByCompany(int companyID) throws SQLException {
        List<File> files = new ArrayList<>();

        String query = "SELECT DISTINCT file.fileID, file.name, file.file, file.confidential, file.form FROM file " +
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
                            result.getBoolean("confidential"),
                            result.getBoolean("form")));
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
        String query = "INSERT INTO file (name, file, confidential, form) VALUES (?, ?, ?, ?);";

        try(Connection conn = this.dbConn.connect();
            PreparedStatement preparedStatement = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            Blob blob = conn.createBlob();
            blob.setBytes(1, file.getEncodedString().getBytes());

            //Set parameters and execute update
            preparedStatement.setString(1, file.getName());
            preparedStatement.setBlob(2, blob);
            preparedStatement.setBoolean(3, file.getConfidential());
            preparedStatement.setBoolean(4, file.getForm());

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
        //Prepare sql statement
        String query = "UPDATE file SET file.name = ?, file.file = ?, file.confidential = ?, file.form = ? WHERE file.fileID = ?;";

        try (Connection conn = this.dbConn.connect();
             PreparedStatement preparedStatement = conn.prepareStatement(query)) {

            Blob blob = conn.createBlob();
            blob.setBytes(1, file.getEncodedString().getBytes());

            //Set parameters and execute update
            preparedStatement.setString(1, file.getName());
            preparedStatement.setBlob(2, blob);
            preparedStatement.setBoolean(3, file.getConfidential());
            preparedStatement.setBoolean(4, file.getForm());
            preparedStatement.setInt(5, file.getFileID());

            preparedStatement.executeUpdate();
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