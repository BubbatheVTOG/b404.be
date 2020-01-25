package b404.datalayer;

import java.sql.*;
import java.util.ArrayList;

import b404.utility.objects.Person;

public class PersonDB {
    private DBConn dbConn;
    private CompanyDB companyDB;

    public PersonDB(){
        this.dbConn = new DBConn();
    }

    /**
     * Connect to database and retrieve all content of person table
     * @return ArrayList of all Person objects
     * @throws SQLException - Error connecting to database or executing query
     */
    public ArrayList<Person> getAllPeople() throws SQLException {
        this.dbConn.connect();

        //Prepare sql statement
        String query = "SELECT * FROM person;";
        PreparedStatement preparedStatement = this.dbConn.conn.prepareStatement(query);

        //Set parameters and execute query
        ResultSet result = preparedStatement.executeQuery();

        ArrayList<Person> people = new ArrayList<>();

        while(result.next()) {

            //Pull response content and map into a Person object
            Person person = new Person(result.getString("UUID"),
                    result.getString("username"),
                    result.getString("passwordHash"),
                    result.getString("salt"),
                    result.getString("fName"),
                    result.getString("lName"),
                    result.getString("email"),
                    result.getString("title"),
                    result.getInt("accessLevelID"));

            people.add(person);
        }

        //Close the database
        this.dbConn.close();

        return people;
    }

    /**
     * Connect to database and retrieve entry by username
     * @param username - Username to search database for
     * @return Person object or null if not found
     * @throws SQLException - Error connecting to database or executing query
     */
    public Person getPersonByUsername(String username) throws SQLException {
        this.dbConn.connect();

        //Prepare sql statement
        String query = "SELECT * FROM person WHERE person.username = ?";
        PreparedStatement preparedStatement = this.dbConn.conn.prepareStatement(query);

        //Set parameters and execute query
        preparedStatement.setString(1, username);
        ResultSet result = preparedStatement.executeQuery();

        Person person = null;

        while(result.next()) {

            //Pull response content and map into a Person object
            person = new Person(result.getString("UUID"),
                    result.getString("username"),
                    result.getString("passwordHash"),
                    result.getString("salt"),
                    result.getString("fName"),
                    result.getString("lName"),
                    result.getString("email"),
                    result.getString("title"),
                    result.getInt("accessLevelID"));
        }

        //Close the database
        this.dbConn.close();

        return person;
    }

    /**
     * Connect to database and retrieve entry by UUID
     * @param UUID - UUID to search database for
     * @return Person object or null if not found
     * @throws SQLException - Error connecting to database or executing query
     */
    public Person getPersonByUUID(String UUID) throws SQLException {
        this.dbConn.connect();

        //Prepare sql statement
        String query = "SELECT * FROM person WHERE person.UUID = ?;";
        PreparedStatement preparedStatement = this.dbConn.conn.prepareStatement(query);

        //Set parameters and execute query
        preparedStatement.setString(1, UUID);
        ResultSet result = preparedStatement.executeQuery();

        Person person = null;

        while(result.next()) {

            //Pull response content and map into a Person object
            person = new Person(result.getString("UUID"),
                    result.getString("username"),
                    result.getString("passwordHash"),
                    result.getString("salt"),
                    result.getString("fName"),
                    result.getString("lName"),
                    result.getString("email"),
                    result.getString("title"),
                    result.getInt("accessLevelID"));
        }

        //Close the database
        this.dbConn.close();

        return person;
    }

    /**
     * Connect to database and add a new person
     * @param UUID - new Person UUID
     * @param username - new person username
     * @param password - new person password
     * @param salt - new person salt
     * @param email - new person email
     * @param title - new person title
     * @param accessLevelID - new person accessLevelID
     * @throws SQLException - error connecting to database or executing query
     */
    public void insertPerson(String UUID, String username, String password, String salt, String fName, String lName, String email, String title, int accessLevelID) throws SQLException {
        this.dbConn.connect();

        //Prepare sql statement
        String query = "INSERT INTO person (UUID, username, passwordHash, salt, fName, lName, email, title, accessLevelID) VALUES (?,?,?,?,?,?,?,?,?);";
        PreparedStatement preparedStatement = this.dbConn.conn.prepareStatement(query);

        //Set parameters and execute query
        preparedStatement.setString(1, UUID);
        preparedStatement.setString(2, username);
        preparedStatement.setString(3, password);
        preparedStatement.setString(4, salt);
        preparedStatement.setString(4, fName);
        preparedStatement.setString(4, lName);
        preparedStatement.setString(5, email);
        preparedStatement.setString(6, title);
        preparedStatement.setInt(8, accessLevelID);

        preparedStatement.executeUpdate();

        //Close the database
        this.dbConn.close();
    }

    /**
     * Connect to database and update person using UUID
     * @param UUID - UUID of person to update
     * @param username - new person username
     * @param password - new person password
     * @param salt - new person salt
     * @param email - new person email
     * @param title - new person title
     * @param accessLevelID - new person accessLevelID
     * @throws SQLException - error connecting to database or executing query
     */
    public void updatePerson(String UUID, String username, String password, String salt, String fName, String lName, String email, String title, int accessLevelID) throws SQLException {
        this.dbConn.connect();

        //Prepare sql statement
        String query = "UPDATE person SET username = ?, passwordHash = ?, salt = ?,fNme = ?, lName = ?,  email = ?, title = ?, accessLevelID = ? WHERE UUID = ?;";
        PreparedStatement preparedStatement = this.dbConn.conn.prepareStatement(query);

        //Set parameters and execute query
        preparedStatement.setString(1, username);
        preparedStatement.setString(2, password);
        preparedStatement.setString(3, salt);
        preparedStatement.setString(3, fName);
        preparedStatement.setString(3, lName);
        preparedStatement.setString(4, email);
        preparedStatement.setString(5, title);
        preparedStatement.setInt(7, accessLevelID);
        preparedStatement.setString(8, UUID);

        preparedStatement.executeUpdate();

        //Close the database
        this.dbConn.close();
    }

    /**
     * Connect to database and delete a person by UUID
     * @param UUID - UUID to delete from database
     * @return number of rows deleted
     * @throws SQLException - Error connecting to database or executing query
     */
    public int deletePersonByUUID(String UUID) throws SQLException {
        this.dbConn.connect();

        //Prepare sql statement
        String query = "DELETE FROM person WHERE person.UUID = ?;";
        PreparedStatement preparedStatement = this.dbConn.conn.prepareStatement(query);

        //Set parameters and execute query
        preparedStatement.setString(1, UUID);
        int numRowsDeleted = preparedStatement.executeUpdate();

        //Close the database
        this.dbConn.close();

        return numRowsDeleted;
    }

}
