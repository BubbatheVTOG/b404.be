package b404.datalayer;

import b404.utility.env.EnvManager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

class DBConn {
    Connection conn;

    private EnvManager env;

    private String driver;
    private String url;
    private String user;
    private String password;

    DBConn(){
        this.env = new EnvManager();

        this.driver = "org.mariadb.jdbc.Driver";
        this.url = "jdbc:mariadb://" + env.getValue("DB_NAME") + ":3306/venture_creations?allowPublicKeyRetrieval=true";

        //TODO: communicate on what these values should be and how best to store them
        this.user = "b404";
        this.password = "b404";
    }

    /**
     * Opens a connection to the database
     * Throws a custom SQLException on error
     */
    void connect() throws SQLException {
        conn = null;

        try{
            Class.forName(this.driver);
            conn = DriverManager.getConnection(this.url, this.user, this.password);
        }
        //return false on error connecting
        catch(SQLException sqle){
            throw new SQLException("Could not connect to database");
        }
        catch(ClassNotFoundException cnfe){
            throw new SQLException("Mariadb driver not found");
        }
    }

    /**
     * Closes the database connection
     * Throws a custom SQLException on error
     */
    void close() throws SQLException{
        try{
            this.conn.close();
        }
        catch(SQLException e){
            throw new SQLException("Error closing the database connection");
        }
    }
}
