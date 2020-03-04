package blink.datalayer;

import blink.utility.objects.Verb;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class VerbDB {
    private DBConn dbConn;

    public VerbDB(){
        this.dbConn = new DBConn();
    }

    /**
     * Get all verbs
     * @return list of all verb objects
     * @throws SQLException Error connecting to database or executing query
     */
    public List<Verb> getAllVerbs() throws SQLException {
        //Prepare sql statement
        String query = "SELECT * FROM verb;";

        try (Connection conn = this.dbConn.connect();
             PreparedStatement preparedStatement = conn.prepareStatement(query)) {

            try (ResultSet result = preparedStatement.executeQuery()) {

                List<Verb> verbList = new ArrayList<>();

                while (result.next()) {
                    verbList.add(new Verb(result.getInt("verbID"),
                            result.getString("name"),
                            result.getString("description")));
                }

                //Return verb
                return verbList;
            }
        }
    }
}