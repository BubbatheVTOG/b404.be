package blink.datalayer;

import blink.utility.ResourceReader;
import blink.utility.env.EnvManager;
import blink.utility.exceptions.DBInitException;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DBInit {
    private DBConn dBconn;

    public DBInit() {
        this.dBconn = new DBConn();
    }

    public void initializeDB() throws DBInitException {
        try {
            if (!this.tableExists()) {
                this.createDB();
            }
        } catch (SQLException sqle) {
            throw new DBInitException("Could not initialize the database!");
        } catch (IOException ioe) {
            throw new DBInitException("Could not read needed resource files.");
        }
    }

    private boolean tableExists() throws SQLException {
        boolean retVal = false;
        String databaseName = "";
        EnvManager env = new EnvManager();

        try (Connection conn = this.dBconn.connect();
            ResultSet resultSet = conn.getMetaData().getCatalogs()) {

            //iterate each catalog in the ResultSet
            while (resultSet.next()) {
                // Get the database name, which is at position 1
                databaseName = resultSet.getString(1);
            }

            if (databaseName.equals(env.getValue("DB_TABLE"))) {
                retVal = true;
            }
        }
        return retVal;
    }

    private void createDB() throws IOException, SQLException {
        ResourceReader rectReader = new ResourceReader("create.sql");
        String dbCreateString = rectReader.getResourceAsString();

        try (Connection conn = this.dBconn.connect();
            PreparedStatement statement = conn.prepareStatement(dbCreateString)) {
            statement.execute();
        }
    }
}
