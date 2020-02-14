package blink.datalayer;

import blink.utility.ResourceReader;
import blink.utility.env.EnvKeyValues;
import blink.utility.env.EnvManager;

import java.io.IOException;
import java.sql.*;

/**
 * This is used for determining the initial state of the database and creating the desired state if needed.
 */
public class DBInit {

    private DBConn dBconn;
    private EnvManager env = new EnvManager();
    public static final int MAX_ATTEMPTS = 100;

    /**
     * Creates a DBInit object.
     */
    public DBInit() {
        this.dBconn = new DBConn();
    }

    /**
     * This will attempt to initialize the database.
     * @throws IOException Thrown if we can connect to the database, but the resource file needed to provision
     * the database cannot be found.
     * @throws SQLException Thrown if the connection made to the database is invalid.
     */
    public void initializeDB() throws IOException, SQLException {
        try {
            if (!this.tablesExist()) {
                System.out.println("DB tables did not exist. Making them...");
                this.createDB();
                System.out.println("DB tables created.");
            } else {
                System.out.println("DB tables already exist.");
            }
        } catch (SQLException sqle) {
            throw new SQLException("FATAL - Could not initialize the database!");
        } catch (IOException ioe) {
            throw new IOException("FATAL - Could not read needed resource files!");
        }
    }

    /**
     * Checks if the required tables are in the database.
     * @return Returns true if the tables needed are found.
     * @throws SQLException Thrown if the SQL connection is invalid.
     */
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

    /**
     * Creates database tables.
     * @throws IOException Thrown if the resource file needed cannot be found.
     * @throws SQLException Thrown if the SQL connection is invalid.
     */
    private void createDB() throws IOException, SQLException {
        ResourceReader rectReader = new ResourceReader("create.sql");

        String dbCreateString = rectReader.getResourceAsString();
        dbCreateString = dbCreateString.replace(EnvKeyValues.DB_DATABASE, env.getValue(EnvKeyValues.DB_DATABASE));

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

    /**
     * Attempts to connect to the database.
     * @return Returns true if we can connect. Returns false if we cannot connect to the database.
     */
    public boolean canConnect() {
        try (Connection conn = this.dBconn.connect()) {
            return true;
        } catch (SQLException sqle) {
            return false;
        }
    }
}
