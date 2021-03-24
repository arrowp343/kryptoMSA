import enums.Algorithm;
import enums.Type;
import hsqldb.HSQLDB;
import org.junit.jupiter.api.Test;

import java.util.Date;
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
    @Test
    public void testGetTypeOfParticipant(){
        HSQLDB.instance.setupDatabase();
        Type expect = Type.intruder, actual = null;
        String testName = new Date().toString();
        try {
            HSQLDB.instance.registerParticipant(testName, expect);
            actual = HSQLDB.instance.getTypeOfParticipant(testName);
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
        assertNotNull(actual);
        assertEquals(expect, actual);
        HSQLDB.instance.shutdown();
    }
    @Test
    public void testConnectionAlreadyExists(){
        HSQLDB.instance.setupDatabase();
        try{
            assertTrue(HSQLDB.instance.doesChannelExist("branch_hkg", "branch_wuh"));
            assertTrue(HSQLDB.instance.doesChannelExist("branch_hkg", "branch_cpt"));
            assertTrue(HSQLDB.instance.doesChannelExist("branch_cpt", "branch_syd"));
            assertTrue(HSQLDB.instance.doesChannelExist("branch_syd", "branch_sfo"));
            assertFalse(HSQLDB.instance.doesChannelExist("branch_hkg", "branch_syd"));
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
        HSQLDB.instance.shutdown();
    }
    @Test
    public void testInsertMessage(){
        HSQLDB.instance.setupDatabase();
        String testP1 = "TestP1" + new Date().getTime();
        String testP2 = "TestP2" + new Date().getTime();
        String testChannel = "TestCh" + new Date().getTime();
        boolean err = false;
        try {
            HSQLDB.instance.registerParticipant(testP1, Type.normal);
            HSQLDB.instance.registerParticipant(testP2, Type.normal);
            HSQLDB.instance.createChannel(testChannel, testP1, testP2);
            HSQLDB.instance.sendMessage(testP1, testP2, "HalloWelt", "Verschlüsselt", Algorithm.Shift, "keyfile.txt");
        } catch (Exception e){
            System.out.println(e.getMessage());
            err = true;
        }
        assertFalse(err);
    }
}
