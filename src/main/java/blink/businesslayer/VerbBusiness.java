package blink.businesslayer;

import blink.datalayer.VerbDB;
import blink.utility.objects.Verb;

import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotFoundException;
import java.sql.SQLException;
import java.util.List;

public class VerbBusiness {
    private VerbDB verbDB;

    public VerbBusiness(){
        this.verbDB = new VerbDB();
    }

    /**
     * Uses data layer to get all verbs from the database
     * @throws InternalServerErrorException Error in the data layer
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

    /**
     * Uses data layer to get all verbs from the database
     * @throws InternalServerErrorException Error in the data layer
     * @return Generic list of verbs
     */
    public Verb getVerb(int verbID){
        try{
            Verb verb = this.verbDB.getVerb(verbID);
            if(verb == null){
                throw new NotFoundException("No verb with that ID exists.");
            }
            return verb;
        }
        catch(SQLException sqle){
            throw new InternalServerErrorException(sqle.getMessage());
        }
    }
}
