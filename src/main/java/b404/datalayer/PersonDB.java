package b404.datalayer;

import java.sql.*;

import b404.utility.objects.Person;
import b404.utility.env.EnvManager;

public class PersonDB {
    DBConn dbConn;

    public PersonDB(){
        this.dbConn = new DBConn();
    }

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

}
