package b404.datalayer;

import java.sql.*;
import java.util.Properties;

import b404.utility.Person;
import b404.utility.env.EnvManager;

public class VentureCreationsDB {
    private Connection conn;
    private String driver;
    private String url;
    private Properties properties = new Properties();
    private String user;
    private String password;


    public VentureCreationsDB(EnvManager env){

        // Get the db host name from the environment.
        this.url = "jdbc:mariadb://"+env.getValue("DB_NAME")+":3306/venture_creations";
        this.driver = "org.mariadb.jdbc.Driver";

        //TODO: communicate on what these values should be and how best to store them
        this.user = "b404";
        this.password = "b404";
    }

    /**
     * Opens a connection to the database
     * Throws a custom SQLException on error
     */
    private void connect() throws SQLException{
        conn = null;

        try{
            Class.forName(this.driver);
            conn = DriverManager.getConnection(this.url, this.user, this.password);
        }
        //return false on error connecting
        catch(SQLException sqle){
            throw new SQLException("Error opening connection to the database");
        }
        catch(ClassNotFoundException cnfe){
            throw new SQLException("Error opening connection to the database");
        }
    }

    /**
     * Closes the database connection
     * Throws a custom SQLException on error
     */
    private void close() throws SQLException{
        try{
            this.conn.close();
        }
        catch(SQLException e){
            throw new SQLException("Error closing the database connection");
        }
    }

    public Person getPersonByName(String name) throws SQLException {
        //TODO: This returns a person with password -> password when name -> user for front-end testing; remove once DB connectivity is functional
        if(name.equals("admin")){
            return new Person(1, "admin", "password", 1, 1);
        }

        this.connect();

        //Prepare sql statement
        String query = "SELECT * FROM person WHERE person.name = ?";
        PreparedStatement preparedStatement = this.conn.prepareStatement(query);

        //Set parameters and execute query
        preparedStatement.setString(1, name);
        ResultSet result = preparedStatement.executeQuery();

        //Pull response content and map into a Person object
        Person person = new Person(result.getInt("userID"),
                                   result.getString("name"),
                                   result.getString("passwordHash"),
                                   result.getInt("companyID"),
                                   result.getInt("accessLevelID"));

        //Close the database
        this.close();

        //return person;
        return person;
    }
}
