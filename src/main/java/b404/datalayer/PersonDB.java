package b404.datalayer;

import b404.utility.Person;

import java.sql.*;

public class PersonDB {
    private Connection conn;

    /**
     * Opens a connection to the database
     * Throws a custom SQLException on error
     */
    public void connect() throws SQLException{
        conn = null;

        //TODO: communicate on what these values should be and how best to store them
        String userName = "b404";
        String password = "b404";
        String url = "jdbc:mysql://db:3306:b404";

        try{
            conn = DriverManager.getConnection(url, userName, password);
        }
        //return false on error connecting
        catch(SQLException sqle){
            throw new SQLException("Error opening connection to the database");
        }
    }

    /**
     * Closes the database connection
     * Throws a custom SQLException on error
     */
    public void close() throws SQLException{
        try{
            this.conn.close();
        }
        catch(SQLException e){
            throw new SQLException("Error closing the database connection");
        }
    }

    public Person getPersonByName(String name) throws SQLException {
        this.connect();

        //Prepare sql statement
        String query = "SELECT * FROM person WHERE person.name = ?";
        PreparedStatement preparedStatement = this.conn.prepareStatement(query);

        //Set parameters and execute query
        preparedStatement.setString(1, name);
        ResultSet resultset = preparedStatement.executeQuery();

        //Pull response content and map into a Person object
        //TODO: map database response to a Person object
        Person person = new Person(resultset.getInt("userID"),
                                   resultset.getString("name"),
                                   resultset.getString("passwordHash"),
                                   resultset.getInt("companyID"),
                                   resultset.getInt("accessLevelID"));

        //Close the database
        this.close();

        return person;

    }
}
