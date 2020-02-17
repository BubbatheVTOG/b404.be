package blink.businesslayer;

import blink.datalayer.VerbDB;
import blink.utility.objects.Verb;

import javax.ws.rs.InternalServerErrorException;
import java.sql.SQLException;
import java.util.List;

public class VerbBusiness {
    private VerbDB verbDB;

    public VerbBusiness(){
        this.verbDB = new VerbDB();
    }

    /**
     * Uses data layer to get all verbs from the database
     * @return Generic list of verbs
     */
    public List<Verb> getAllVerbs(){
        try{
            return this.verbDB.getAllVerbs();
        }
        catch(SQLException sqle){
            throw new InternalServerErrorException(sqle.getMessage());
        }
    }
}
