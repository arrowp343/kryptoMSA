import enums.Type;
import hsqldb.HSQLDB;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TestHSQLDB {
    @Test
    public void testShowParticipants(){
        HSQLDB.instance.setupDatabase();
        try{
            String[] expect = new String[] {"branch_hkg\t\tnormal",
                                            "branch_cpt\t\tnormal",
                                            "branch_sfo\t\tnormal",
                                            "branch_syd\t\tnormal",
                                            "branch_wuh\t\tnormal",
                                            "msa\t\tintruder"};
            List<String> participants = HSQLDB.instance.showParticipants();
            for(int i = 0; i < participants.size(); i++){
                assertEquals(expect[i], participants.get(i));
            }
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
        HSQLDB.instance.shutdown();
    }

    @Test
    public void testRegisterParticipant(){
        HSQLDB.instance.setupDatabase();
        try{
            HSQLDB.instance.registerParticipant("TestParticipant", Type.normal);
            List<String> participants = HSQLDB.instance.showParticipants();
            assertEquals("TestParticipant\t\tnormal", participants.get(participants.size() - 1));
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
        HSQLDB.instance.shutdown();
    }
}
