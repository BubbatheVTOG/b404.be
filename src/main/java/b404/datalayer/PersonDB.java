package b404.datalayer;

import java.sql.*;

import b404.utility.objects.Person;

public class PersonDB {
    private DBConn dbConn;
    private CompanyDB companyDB;

    public PersonDB(){
        this.dbConn = new DBConn();
        this.companyDB = new CompanyDB();
    }

    /**
     * Connect to database and retrieve entry by name
     * @param name - Username to search database for
     * @return Person object or null if not found
     * @throws SQLException - Error connecting to database or executing query
     */
    public Person getPersonByName(String name) throws SQLException {
        this.dbConn.connect();

        //Prepare sql statement
        String query = "SELECT * FROM person WHERE person.name = ?";
        PreparedStatement preparedStatement = this.dbConn.conn.prepareStatement(query);

        //Set parameters and execute query
        preparedStatement.setString(1, name);
        ResultSet result = preparedStatement.executeQuery();

        Person person = null;

        while(result.next()) {

            //Pull response content and map into a Person object
            person = new Person(result.getInt("userID"),
                    result.getString("name"),
                    result.getString("email"),
                    result.getString("passwordHash"),
                    result.getString("salt"),
                    result.getString("title"),
                    result.getInt("companyID"),
                    result.getInt("accessLevelID"));
        }

        //Close the database
        this.dbConn.close();

        //return person;
        return person;
    }

    /**
     * Connect to database and retrieve entry by userID
     * @param userID - UserID to search database for
     * @return Person object or null if not found
     * @throws SQLException - Error connecting to database or executing query
     */
    public Person getPersonByUserID(int userID) throws SQLException {
        this.dbConn.connect();

        //Prepare sql statement
        String query = "SELECT * FROM person WHERE person.userID = ?";
        PreparedStatement preparedStatement = this.dbConn.conn.prepareStatement(query);

        //Set parameters and execute query
        preparedStatement.setInt(1, userID);
        ResultSet result = preparedStatement.executeQuery();

        Person person = null;

        while(result.next()) {

            //Pull response content and map into a Person object
            person = new Person(result.getInt("userID"),
                    result.getString("name"),
                    result.getString("email"),
                    result.getString("passwordHash"),
                    result.getString("salt"),
                    result.getString("title"),
                    result.getInt("companyID"),
                    result.getInt("accessLevelID"));
        }

        //Close the database
        this.dbConn.close();

        //return person;
        return person;
    }

    /**
     * Connect to database and add
     * @param name
     * @param password
     * @param salt
     * @param email
     * @param title
     * @param companyID
     * @param accessLevelID
     * @throws SQLException - error connecting to database or executing query
     */
    public void insertPerson(String name, String password, String salt, String email, String title, int companyID, int accessLevelID) throws SQLException {
        this.dbConn.connect();

        //Prepare sql statement
        String query = "INSERT INTO person (name, email, passwordHash, salt, title, companyID, accessLevelID) VALUES (?,?,?,?,?,?,?)";
        PreparedStatement preparedStatement = this.dbConn.conn.prepareStatement(query);

        //Set parameters and execute query
        preparedStatement.setString(1, name);
        preparedStatement.setString(2, email);
        preparedStatement.setString(3, password);
        preparedStatement.setString(4, salt);
        preparedStatement.setString(5, title);
        preparedStatement.setInt(6, companyID);
        preparedStatement.setInt(7, accessLevelID);

        preparedStatement.executeUpdate();

        //Close the database
        this.dbConn.close();
    }

    /**
     * Connect to database and delete a person by userID
     * @param userID - UserID to delete from database
     * @return number of rows deleted
     * @throws SQLException - Error connecting to database or executing query
     */
    public int deletePersonByUserID(int userID) throws SQLException {
        this.dbConn.connect();

        //Prepare sql statement
        String query = "DELETE FROM person WHERE person.userID = ?";
        PreparedStatement preparedStatement = this.dbConn.conn.prepareStatement(query);

        //Set parameters and execute query
        preparedStatement.setInt(1, userID);
        int numRowsDeleted = preparedStatement.executeUpdate();

        //Close the database
        this.dbConn.close();

        //return person;
        return numRowsDeleted;
    }

}
