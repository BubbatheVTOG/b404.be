package b404.datalayer;

import java.sql.*;

import b404.utility.objects.Person;
import b404.utility.env.EnvManager;

public class PersonDB {
    private Connection conn;

    private EnvManager env;

    private String driver;
    private String url;
    private String user;
    private String password;

    public PersonDB(){
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
            throw new SQLException("Mariadb driver not found");
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
