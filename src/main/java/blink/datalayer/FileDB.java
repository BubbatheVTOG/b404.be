package blink.datalayer;

import blink.utility.objects.File;

import java.sql.*;

public class FileDB {
    private DBConn dbConn;

    /**
     * Get file information based on the fileID
     * @param fileID - fileID to retrieve file from
     * @return file object or null if not found
     * @throws SQLException - Error connecting to database or executing query
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
                            result.getBlob("file"),
                            result.getInt("stepID"));
                }

                //Return file
                return file;
            }
        }
    }

    /**
     * Connect to database and add
     * @param fileID - fileID of new file to be added
     * @param name - name of new file to be added
     * @param file - file of new file to be added
     * @param stepID - stepID of new file to be added
     * @throws SQLException - Error connecting to database or executing update
     */
    public void insertFile(final int fileID, final String name, final Blob file, final int stepID) throws SQLException {
        //Prepare sql statement
        String query = "INSERT INTO file (fileID, name, file, stepID) VALUES (?, ?, ?, ?);";

        try(Connection conn = this.dbConn.connect();
            PreparedStatement preparedStatement = conn.prepareStatement(query)) {

            //Set parameters and execute update
            preparedStatement.setInt(1, fileID);
            preparedStatement.setString(2, name);
            preparedStatement.setBlob(3, file);
            preparedStatement.setInt(4, stepID);

            preparedStatement.executeUpdate();
        }
    }

    /**
     * Connect to database and delete a file by fileID
     * @param fileID - fileID to delete from database
     * @return the number of deleted rows
     * @throws SQLException - Error connecting to database or executing update
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