package main.java.b404.datalayer;

import main.java.b404.utility.Person;

import javax.ws.rs.InternalServerErrorException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PersonDB {
    private Connection conn;

    /**
     * Connect to the database
     * @return status Returns true if connected to the database, otherwise false.
     */
    public boolean connect(){
        conn = null;

        //TODO: communicate on what these values should be and how best to store them
        String userName = "b404";
        String password = "b404";
        String url = null;

        try{
            conn = DriverManager.getConnection(url, userName, password);
        }
        //return false on error connecting
        catch(SQLException sqle){
            return false;
        }

        //return true or false based on whether a connection was successfully established
        return (conn != null);
    }

    public Person getPersonByName(String name) throws SQLException {
        if(!this.connect()){
            throw new SQLException("Error connecting to database");
        }

        String query = "SELECT * FROM person WHERE person.name = ?";
        PreparedStatement preparedStatement = this.conn.prepareStatement(query);

        preparedStatement.setString(1, name);
        preparedStatement.executeQuery();

        //TODO: map database response to a Person object

        return new Person();
    }
}
