import enums.Action;
import enums.Algorithm;
import enums.Type;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestQueryParser {
    @Test
    public void testEncryptWithClassicQuery(){
        String query = "encrypt message \"HalloWelt\" using shift and key 5";
        Query q = null;
        try{
             q = new Query(query);
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
        assertNotNull(q);
        assertEquals(Action.encrypt, q.getAction());
        assertEquals("HalloWelt", q.getMessage());
        assertEquals(Algorithm.Shift, q.getAlgorithm());
        assertEquals(5, q.getKey());
    }
    @Test
    public void testDecryptWithClassicQuery(){
        String query = "decrypt message \"CiaoWelt\" using shift and key 7";
        Query q = null;
        try{
            q = new Query(query);
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
        assertNotNull(q);
        assertEquals(Action.decrypt, q.getAction());
        assertEquals("CiaoWelt", q.getMessage());
        assertEquals(Algorithm.Shift, q.getAlgorithm());
        assertEquals(7, q.getKey());
    }
    @Test
    public void testEncryptWithRSAQuery(){
        String query = "encrypt message \"HalloWelt\" using rsa and keyfile keyfile.txt";
        Query q = null;
        try{
            q = new Query(query);
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
        assertNotNull(q);
        assertEquals(Action.encrypt, q.getAction());
        assertEquals("HalloWelt", q.getMessage());
        assertEquals(Algorithm.RSA, q.getAlgorithm());
        assertEquals("keyfile.txt", q.getKeyFile());
    }
    @Test
    public void testDecryptWithRSAQuery(){
        String query = "decrypt message \"CiaoWelt\" using rsa and keyfile keyfile.txt";
        Query q = null;
        try{
            q = new Query(query);
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
        assertNotNull(q);
        assertEquals(Action.decrypt, q.getAction());
        assertEquals("CiaoWelt", q.getMessage());
        assertEquals(Algorithm.RSA, q.getAlgorithm());
        assertEquals("keyfile.txt", q.getKeyFile());
    }
    @Test
    public void testCrackClassicQuery(){
        String query = "crack encrypted message \"HalloWelt\" using shift";
        Query q = null;
        try{
            q = new Query(query);
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
        assertNotNull(q);
        assertEquals(Action.crack, q.getAction());
        assertEquals("HalloWelt", q.getMessage());
        assertEquals(Algorithm.Shift, q.getAlgorithm());
    }
    @Test
    public void testCrackRSAQuery(){
        String query = "crack encrypted message \"HalloWelt\" using rsa and keyfile keyfile.txt";
        Query q = null;
        try{
            q = new Query(query);
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
        assertNotNull(q);
        assertEquals(Action.crack, q.getAction());
        assertEquals("HalloWelt", q.getMessage());
        assertEquals(Algorithm.RSA, q.getAlgorithm());
        assertEquals("keyfile.txt", q.getKeyFile());
    }
    @Test
    public void testRegisterNormalQuery(){
        String query = "register participant MaxMustermann with type normal";
        Query q = null;
        try{
            q = new Query(query);
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
        assertNotNull(q);
        assertEquals(Action.register, q.getAction());
        assertEquals("MaxMustermann", q.getName());
        assertEquals(Type.normal, q.getType());
    }
    @Test
    public void testRegisterIntruderQuery(){
        String query = "register participant MaxMustermann with type intruder";
        Query q = null;
        try{
            q = new Query(query);
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
        assertNotNull(q);
        assertEquals(Action.register, q.getAction());
        assertEquals("MaxMustermann", q.getName());
        assertEquals(Type.intruder, q.getType());
    }
    @Test
    public void testCreateChannelQuery(){
        String query = "create channel channel1 from hans to peter";
        Query q = null;
        try{
            q = new Query(query);
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
        assertNotNull(q);
        assertEquals(Action.create, q.getAction());
        assertEquals("channel1", q.getName());
        assertEquals("hans", q.getParticipant01());
        assertEquals("peter", q.getParticipant02());
    }
    @Test
    public void testShowChannelQuery(){
        String query = "show channel";
        Query q = null;
        try{
            q = new Query(query);
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
        assertNotNull(q);
        assertEquals(Action.show, q.getAction());
    }
    @Test
    public void testDropChannelQuery(){
        String query = "drop channel channel1";
        Query q = null;
        try{
            q = new Query(query);
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
        assertNotNull(q);
        assertEquals(Action.drop, q.getAction());
        assertEquals("channel1", q.getName());
    }
    @Test
    public void testIntrudeChannelQuery(){
        String query = "intrude channel channel1 by hans";
        Query q = null;
        try{
            q = new Query(query);
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
        assertNotNull(q);
        assertEquals(Action.intrude, q.getAction());
        assertEquals("channel1", q.getName());
        assertEquals("hans", q.getParticipant01());
    }
    @Test
    public void testSendMessageUsingShiftQuery(){
        String query = "send message \"HalloWelt\" from hans to peter using shift and key 3";
        Query q = null;
        try{
            q = new Query(query);
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
        assertNotNull(q);
        assertEquals(Action.send, q.getAction());
        assertEquals("HalloWelt", q.getMessage());
        assertEquals("hans", q.getParticipant01());
        assertEquals("peter", q.getParticipant02());
        assertEquals(Algorithm.Shift, q.getAlgorithm());
        assertEquals(3, q.getKey());
    }
    @Test
    public void testSendMessageUsingRSAQuery(){
        String query = "send message \"HalloWelt\" from hans to peter using rsa and keyfile keyfile.txt";
        Query q = null;
        try{
            q = new Query(query);
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
        assertNotNull(q);
        assertEquals(Action.send, q.getAction());
        assertEquals("HalloWelt", q.getMessage());
        assertEquals("hans", q.getParticipant01());
        assertEquals("peter", q.getParticipant02());
        assertEquals(Algorithm.RSA, q.getAlgorithm());
        assertEquals("keyfile.txt", q.getKeyFile());
    }

}
