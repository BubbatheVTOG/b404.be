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
            person = new Person(result.getString("UUID"),
                    result.getString("name"),
                    result.getString("passwordHash"),
                    result.getString("salt"),
                    result.getString("email"),
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
     * Connect to database and retrieve entry by UUID
     * @param UUID - UUID to search database for
     * @return Person object or null if not found
     * @throws SQLException - Error connecting to database or executing query
     */
    public Person getPersonByUUID(String UUID) throws SQLException {
        this.dbConn.connect();

        //Prepare sql statement
        String query = "SELECT * FROM person WHERE person.UUID = ?";
        PreparedStatement preparedStatement = this.dbConn.conn.prepareStatement(query);

        //Set parameters and execute query
        preparedStatement.setString(1, UUID);
        ResultSet result = preparedStatement.executeQuery();

        Person person = null;

        while(result.next()) {

            //Pull response content and map into a Person object
            person = new Person(result.getString("UUID"),
                    result.getString("name"),
                    result.getString("passwordHash"),
                    result.getString("salt"),
                    result.getString("email"),
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
     * @param UUID
     * @param name
     * @param password
     * @param salt
     * @param email
     * @param title
     * @param companyID
     * @param accessLevelID
     * @throws SQLException - error connecting to database or executing query
     */
    public void insertPerson(String UUID, String name, String password, String salt, String email, String title, int companyID, int accessLevelID) throws SQLException {
        this.dbConn.connect();

        //Prepare sql statement
        String query = "INSERT INTO person (UUID, passwordHash, salt, name, email, title, companyID, accessLevelID) VALUES (?,?,?,?,?,?,?,?)";
        PreparedStatement preparedStatement = this.dbConn.conn.prepareStatement(query);

        //Set parameters and execute query
        preparedStatement.setString(1, UUID);
        preparedStatement.setString(2, password);
        preparedStatement.setString(3, salt);
        preparedStatement.setString(4, name);
        preparedStatement.setString(5, email);
        preparedStatement.setString(6, title);
        preparedStatement.setInt(7, companyID);
        preparedStatement.setInt(8, accessLevelID);

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
        String query = "DELETE FROM person WHERE person.UUID = ?";
        PreparedStatement preparedStatement = this.dbConn.conn.prepareStatement(query);

        //Set parameters and execute query
        preparedStatement.setString(1, UUID);
        int numRowsDeleted = preparedStatement.executeUpdate();

        //Close the database
        this.dbConn.close();

        //return person;
        return numRowsDeleted;
    }

}
