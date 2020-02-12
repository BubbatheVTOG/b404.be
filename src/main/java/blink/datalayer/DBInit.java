package blink.datalayer;

import blink.utility.env.EnvManager;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DBInit {
    private DBConn dBconn;

    private static final EnvManager env = new EnvManager();

    public DBInit() {
        this.dBconn = new DBConn();
    }

    public void initialize() {
        if (!this.tableExists()) {

        }
    }

    private boolean tableExists() throws SQLException {
        boolean retVal = false;
        String databaseName = "";
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
}
