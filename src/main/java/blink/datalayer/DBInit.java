package blink.datalayer;

import blink.utility.ResourceReader;

import java.io.IOException;
import java.sql.*;

public class DBInit {

    private DBConn dBconn;

    public DBInit() {
        this.dBconn = new DBConn();
    }

    public void initializeDB() throws IOException, SQLException {
        try {
            if (this.tablesExist()) {
            } else {
                this.createDB();
            }
        } catch (SQLException sqle) {
            throw new SQLException("FATAL - Could not initialize the database!");
        } catch (IOException ioe) {
            throw new IOException("FATAL - Could not read needed resource files!");
        }
    }

    private boolean tablesExist() throws SQLException {
        boolean retVal = false;
        String query = "SHOW TABLES;";

        try (Connection conn = this.dBconn.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            rs.last();
            if (rs.getRow() > 0) {
                retVal = true;
            }
        }
        return retVal;
    }

    private void createDB() throws IOException, SQLException {
        ResourceReader rectReader = new ResourceReader("create.sql");
        String dbCreateString = rectReader.getResourceAsString();

        try (Connection conn = this.dBconn.connect()) {
            try (Statement st = conn.createStatement()) {
                for (String s : dbCreateString.split(";")) {
                    // Ensure that there is no spaces before or after the request string
                    // in order to not execute empty statements.
                    if (!s.trim().equals("")) {
                        st.executeUpdate(s);
                    }
                }
            }
        }
    }

    public boolean tryDBConnect() {
        try (Connection conn = this.dBconn.connect()) {
            return true;
        } catch (SQLException sqle) {
            return false;
        }
    }
}
