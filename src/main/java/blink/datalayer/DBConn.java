package blink.datalayer;

import blink.utility.env.EnvManager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

class DBConn {
    private EnvManager env;

    private String driver;
    private String url;
    private String user;
    private String password;

    DBConn(){
        this.env = new EnvManager();

        this.driver = "org.mariadb.jdbc.Driver";
        this.url = "jdbc:mariadb://" + env.getValue("DB_NAME") + ":3306/venture_creations?allowPublicKeyRetrieval=true";
        this.user = env.getValue("DB_USER_NAME");
        this.password = env.getValue("DB_USER_PASSWD");
    }

    /**
     * Opens a connection to the database
     * Throws a custom SQLException on error
     */
    Connection connect() throws SQLException {
        try{
            Class.forName(this.driver);
            return DriverManager.getConnection(this.url, this.user, this.password);
        }
        //return false on error connecting
        catch(SQLException sqle){
            throw new SQLException("Could not connect to database");
        }
        catch(ClassNotFoundException cnfe){
            throw new SQLException("Mariadb driver not found");
        }
    }
}
