package hsqldb;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import enums.Type;

public enum HSQLDB {
    instance;

    private Connection connection;

    public void setupConnection() {
        System.out.println("--- setupConnection");

        try {
            Class.forName("org.hsqldb.jdbcDriver");
            String databaseURL = Configuration.instance.driverName + Configuration.instance.databaseFile;
            connection = DriverManager.getConnection(databaseURL, Configuration.instance.username, Configuration.instance.password);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private synchronized void update(String sqlStatement) {
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate(sqlStatement);
            statement.close();
        } catch (SQLException sqle) {
            System.out.println(sqle.getMessage());
        }
    }

    /*
       [algorithms]
       id TINYINT       NOT NULL PK
       name VARCHAR(10) NOT NULL unique
    */
    public void createTableAlgorithms() {
        System.out.println("--- createTableAlgorithms");

        StringBuilder sqlStringBuilder01 = new StringBuilder();
        sqlStringBuilder01.append("CREATE TABLE IF NOT EXISTS algorithms (");
        sqlStringBuilder01.append("id TINYINT NOT NULL").append(",");
        sqlStringBuilder01.append("name VARCHAR(10) NOT NULL").append(",");
        sqlStringBuilder01.append("PRIMARY KEY (id)");
        sqlStringBuilder01.append(")");
        System.out.println("sqlStringBuilder : " + sqlStringBuilder01.toString());
        update(sqlStringBuilder01.toString());

        StringBuilder sqlStringBuilder02 = new StringBuilder();
        sqlStringBuilder02.append("CREATE UNIQUE INDEX IF NOT EXISTS idx_algorithms ON algorithms (name)");
        System.out.println("sqlStringBuilder : " + sqlStringBuilder02.toString());
        update(sqlStringBuilder02.toString());

        String insertAlgorithm = "INSERT INTO algorithms (id, name) VALUES ('1', 'shift'), ('2', 'rsa');";
        System.out.println("insertAlgorithm : " + insertAlgorithm);
        update(insertAlgorithm);
    }

    /*
       [types]
       id TINYINT       NOT NULL PK
       name VARCHAR(10) NOT NULL unique
    */
    public void createTableTypes() {
        System.out.println("--- createTableTypes");

        StringBuilder sqlStringBuilder01 = new StringBuilder();
        sqlStringBuilder01.append("CREATE TABLE IF NOT EXISTS types (");
        sqlStringBuilder01.append("id TINYINT NOT NULL").append(",");
        sqlStringBuilder01.append("name VARCHAR(10) NOT NULL").append(",");
        sqlStringBuilder01.append("PRIMARY KEY (id)");
        sqlStringBuilder01.append(")");
        System.out.println("sqlStringBuilder : " + sqlStringBuilder01.toString());
        update(sqlStringBuilder01.toString());

        StringBuilder sqlStringBuilder02 = new StringBuilder();
        sqlStringBuilder02.append("CREATE UNIQUE INDEX IF NOT EXISTS idx_types ON types (name)");
        System.out.println("sqlStringBuilder : " + sqlStringBuilder02.toString());
        update(sqlStringBuilder02.toString());

        String insertType = "INSERT INTO types (id, name) VALUES ('1', 'normal'), ('2', 'intruder');";
        System.out.println("insertType : " + insertType);
        update(insertType);
    }

    /*
       [participants]
       id TINYINT       NOT NULL PK
       name VARCHAR(50) NOT NULL unique
       type_id TINYINT  NOT NULL FK
    */
    public void createTableParticipants() {
        System.out.println("--- createTableParticipants");

        StringBuilder sqlStringBuilder01 = new StringBuilder();
        sqlStringBuilder01.append("CREATE TABLE IF NOT EXISTS participants (");
        sqlStringBuilder01.append("id TINYINT NOT NULL").append(",");
        sqlStringBuilder01.append("name VARCHAR(50) NOT NULL").append(",");
        sqlStringBuilder01.append("type_id TINYINT NULL").append(",");
        sqlStringBuilder01.append("PRIMARY KEY (id)");
        sqlStringBuilder01.append(")");
        System.out.println("sqlStringBuilder : " + sqlStringBuilder01.toString());
        update(sqlStringBuilder01.toString());

        StringBuilder sqlStringBuilder02 = new StringBuilder();
        sqlStringBuilder02.append("CREATE UNIQUE INDEX IF NOT EXISTS idx_participants ON types (name)");
        System.out.println("sqlStringBuilder : " + sqlStringBuilder02.toString());
        update(sqlStringBuilder02.toString());

        StringBuilder sqlStringBuilder03 = new StringBuilder();
        sqlStringBuilder03.append("ALTER TABLE participants ADD CONSTRAINT IF NOT EXISTS fk_participants ");
        sqlStringBuilder03.append("FOREIGN KEY (type_id) ");
        sqlStringBuilder03.append("REFERENCES types (id) ");
        sqlStringBuilder03.append("ON DELETE CASCADE");
        System.out.println("sqlStringBuilder : " + sqlStringBuilder03.toString());

        update(sqlStringBuilder03.toString());

        try {
            registerParticipant("branch_hkg", Type.normal);
            registerParticipant("branch_cpt", Type.normal);
            registerParticipant("branch_sfo", Type.normal);
            registerParticipant("branch_syd", Type.normal);
            registerParticipant("branch_wuh", Type.normal);
            registerParticipant("msa", Type.intruder);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    public void registerParticipant(String name, Type type) throws Exception{
        try {
            Statement selectStatement = connection.createStatement();
            ResultSet resultSet = selectStatement.executeQuery("SELECT * FROM participants WHERE name = '" + name + "';");
            if(resultSet.next()) throw new Exception("participant " + name + " already exists, using existing postbox_" + name);
            else{
                Statement selectLatestId = connection.createStatement();
                ResultSet latestId = selectLatestId.executeQuery("SELECT id FROM participants ORDER BY id DESC;");
                int nextId = latestId.next() ? Integer.parseInt(latestId.getString("id")) : 1;
                nextId++;
                String insert = "INSERT INTO participants(id, name, type_id) VALUES (" + nextId + ", '" + name + "', '";
                System.out.println("Register new Participant: " + insert);
                if(type == Type.normal)
                    insert += "1";
                else if (type == Type.intruder)
                    insert += "2";
                insert += "')";
                update(insert);
                createTablePostbox(name);
            }
            selectStatement.close();
        } catch (SQLException sqle) {
            System.out.println(sqle.getMessage());
        }
    }
    public List<String> showParticipants(){
        List<String> result = new ArrayList<>();
        try {
            Statement selectStatement = connection.createStatement();
            ResultSet resultSet = selectStatement.executeQuery("SELECT p.name AS p_name, t.name AS p_type FROM participants p INNER JOIN types t ON p.type_id = t.id;");
            while (resultSet.next())
                result.add(resultSet.getString("p_name") + "\t\t" + resultSet.getString("p_type"));
        } catch (SQLException sqle){
            System.out.println(sqle.getMessage());
        }
        return result;
    }
    public Type getTypeOfParticipant(String name){
        try {
            Statement selectParticipantType = connection.createStatement();
            ResultSet participantType = selectParticipantType.executeQuery("SELECT type_id FROM participants WHERE name = '" + name + "';");
            if(participantType.next()) {
                int type = participantType.getInt("type_id");
                if(type == 1)
                    return Type.normal;
                else if(type == 2)
                    return Type.intruder;
            }
        } catch (Exception e){
            return null;
        }
        return null;
    }

    /*
       [channel]
       name           VARCHAR(25) NOT NULL PK
       participant_01 TINYINT NOT NULL FK
       participant_02 TINYINT NOT NULL FK
    */
    public void createTableChannel() {
        System.out.println("--- createTableChannel");

        StringBuilder sqlStringBuilder01 = new StringBuilder();
        sqlStringBuilder01.append("CREATE TABLE IF NOT EXISTS channel (");
        sqlStringBuilder01.append("name VARCHAR(25) NOT NULL").append(",");
        sqlStringBuilder01.append("participant_01 TINYINT NOT NULL").append(",");
        sqlStringBuilder01.append("participant_02 TINYINT NOT NULL").append(",");
        sqlStringBuilder01.append("PRIMARY KEY (name)");
        sqlStringBuilder01.append(" )");
        System.out.println("sqlStringBuilder : " + sqlStringBuilder01.toString());
        update(sqlStringBuilder01.toString());

        StringBuilder sqlStringBuilder02 = new StringBuilder();
        sqlStringBuilder02.append("ALTER TABLE channel ADD CONSTRAINT IF NOT EXISTS fk_channel_01 ");
        sqlStringBuilder02.append("FOREIGN KEY (participant_01) ");
        sqlStringBuilder02.append("REFERENCES participants (id) ");
        sqlStringBuilder02.append("ON DELETE CASCADE");
        System.out.println("sqlStringBuilder : " + sqlStringBuilder02.toString());
        update(sqlStringBuilder02.toString());

        StringBuilder sqlStringBuilder03 = new StringBuilder();
        sqlStringBuilder03.append("ALTER TABLE channel ADD CONSTRAINT IF NOT EXISTS fk_channel_02 ");
        sqlStringBuilder03.append("FOREIGN KEY (participant_02) ");
        sqlStringBuilder03.append("REFERENCES participants (id) ");
        sqlStringBuilder03.append("ON DELETE CASCADE");
        System.out.println("sqlStringBuilder : " + sqlStringBuilder03.toString());
        update(sqlStringBuilder03.toString());
        try {
            createChannel("hkg_wuh", "branch_hkg", "branch_wuh");
            createChannel("hkg_cpt", "branch_hkg", "branch_cpt");
            createChannel("cpt_syd", "branch_cpt", "branch_syd");
            createChannel("syd_sfo", "branch_syd", "branch_sfo");

        } catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
    public void createChannel(String name, String participant01, String participant02) throws Exception{
        if(participant01.equals(participant02)) throw new Exception(participant01 + " and " + participant02 + " are identical - cannot create channel on itself");

        Statement selectStatement = connection.createStatement();
        ResultSet resultSet = selectStatement.executeQuery("SELECT * FROM channel WHERE name = '" + name + "';");
        if(resultSet.next()) throw new Exception("channel " + name + " already exists");

        if(connectionAlreadyExists(participant01, participant02)) throw new Exception("communication channel between " + participant01 + " and " + participant02 + " already exists");

        if(getTypeOfParticipant(participant01) == null) throw new Exception("Participant " + participant01 + " does not exist");
        if(getTypeOfParticipant(participant02) == null) throw new Exception("Participant " + participant02 + " does not exist");

        if(getTypeOfParticipant(participant01) != Type.normal) throw new Exception("Participant " + participant01 + " must be from type normal");
        if(getTypeOfParticipant(participant02) != Type.normal) throw new Exception("Participant " + participant02 + " must be from type normal");

        int p1_id = 0, p2_id = 0;
        Statement getParticipantId = connection.createStatement();
        ResultSet p1 = getParticipantId.executeQuery("SELECT id FROM participants WHERE name = '" + participant01 + "';");
        if(p1.next()) p1_id = p1.getInt("id");
        else throw new Exception("Participant " + participant01 + " does not exist");
        ResultSet p2 = getParticipantId.executeQuery("SELECT id FROM participants WHERE name = '" + participant02 + "';");
        if(p2.next()) p2_id = p2.getInt("id");
        else throw new Exception("Participant " + participant02 + " does not exist");

        Statement insertConnection = connection.createStatement();
        insertConnection.execute("INSERT INTO channel (name, participant_01, participant_02) VALUES ('" + name + "', " + p1_id + ", " + p2_id + ")");
    }
    public List<String> showChannel(){
        List<String> result = new ArrayList<>();
        try {
            Statement selectStatement = connection.createStatement();
            StringBuilder sql = new StringBuilder();
            sql.append("SELECT c.name AS channelName, p1.name AS part01Name, p2.name AS part02Name ");
            sql.append("FROM (channel c INNER JOIN participants p1 ON c.participant_01 = p1.id) ");
            sql.append("INNER JOIN participants p2 ON c.participant_02 = p2.id;");
            ResultSet resultSet = selectStatement.executeQuery(sql.toString());
            while (resultSet.next()){
                result.add(resultSet.getString("channelName") + " | " + resultSet.getString("part01Name") + " and " + resultSet.getString("part02Name"));
            }

        } catch (SQLException sqle){
            System.out.println(sqle.getMessage());
        }
        return result;
    }
    public boolean connectionAlreadyExists(String participant01, String participant02) throws Exception{
        Statement selectStatement = connection.createStatement();
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT p1.name, p2.name ");
        sql.append("FROM (channel c INNER JOIN participants p1 ON c.participant_01 = p1.id) ");
        sql.append("INNER JOIN participants p2 ON c.participant_02 = p2.id ");
        sql.append("WHERE p1.name = '" + participant01 + "' AND p2.name = '" + participant02 + "' OR ");
        sql.append("p1.name = '" + participant02 + "' AND p2.name = '" + participant01 + "';");
        ResultSet combination = selectStatement.executeQuery(sql.toString());
        if(combination.next()) return true;
        return false;
    }

    /*
      [messages]
      id                  TINYINT NOT NULL
      participant_from_id TINYINT NOT NULL
      participant_to_id   TINYINT NOT NULL
      plain_message       VARCHAR(50) NOT NULL
      algorithm_id        TINYINT NOT NULL
      encrypted_message   VARCHAR(50) NOT NULL
      keyfile             VARCHAR(20) NOT NULL
      timestamp           INT
    */
    public void createTableMessages() {
        System.out.println("--- createTableMessages");

        StringBuilder sqlStringBuilder01 = new StringBuilder();
        sqlStringBuilder01.append("CREATE TABLE IF NOT EXISTS messages (");
        sqlStringBuilder01.append("id TINYINT NOT NULL").append(",");
        sqlStringBuilder01.append("participant_from_id TINYINT NOT NULL").append(",");
        sqlStringBuilder01.append("participant_to_id TINYINT NOT NULL").append(",");
        sqlStringBuilder01.append("plain_message VARCHAR(50) NOT NULL").append(",");
        sqlStringBuilder01.append("algorithm_id TINYINT NOT NULL").append(",");
        sqlStringBuilder01.append("encrypted_message VARCHAR(50) NOT NULL").append(",");
        sqlStringBuilder01.append("keyfile VARCHAR(20) NOT NULL").append(",");
        sqlStringBuilder01.append("timestamp INT NOT NULL").append(",");
        sqlStringBuilder01.append("PRIMARY KEY (id)");
        sqlStringBuilder01.append(" )");
        System.out.println("sqlStringBuilder : " + sqlStringBuilder01.toString());
        update(sqlStringBuilder01.toString());

        StringBuilder sqlStringBuilder02 = new StringBuilder();
        sqlStringBuilder02.append("ALTER TABLE messages ADD CONSTRAINT IF NOT EXISTS fk_messages_01 ");
        sqlStringBuilder02.append("FOREIGN KEY (participant_from_id) ");
        sqlStringBuilder02.append("REFERENCES participants (id) ");
        sqlStringBuilder02.append("ON DELETE CASCADE");
        System.out.println("sqlStringBuilder : " + sqlStringBuilder02.toString());
        update(sqlStringBuilder02.toString());

        StringBuilder sqlStringBuilder03 = new StringBuilder();
        sqlStringBuilder03.append("ALTER TABLE messages ADD CONSTRAINT IF NOT EXISTS fk_messages_02 ");
        sqlStringBuilder03.append("FOREIGN KEY (participant_to_id) ");
        sqlStringBuilder03.append("REFERENCES participants (id) ");
        sqlStringBuilder03.append("ON DELETE CASCADE");
        System.out.println("sqlStringBuilder : " + sqlStringBuilder03.toString());
        update(sqlStringBuilder03.toString());

        StringBuilder sqlStringBuilder04 = new StringBuilder();
        sqlStringBuilder04.append("ALTER TABLE messages ADD CONSTRAINT IF NOT EXISTS fk_messages_03 ");
        sqlStringBuilder04.append("FOREIGN KEY (algorithm_id) ");
        sqlStringBuilder04.append("REFERENCES algorithms (id) ");
        sqlStringBuilder04.append("ON DELETE CASCADE");
        System.out.println("sqlStringBuilder : " + sqlStringBuilder04.toString());
        update(sqlStringBuilder04.toString());
    }

    /*
       [postbox_[participant_name]]
       id                  TINYINT NOT NULL
       participant_from_id TINYINT NOT NULL
       message             VARCHAR(50) NOT NULL
       timestamp           INT
     */
    public void createTablePostbox(String participantName) {
        String table = "postbox_" + participantName;
        System.out.println("--- createTablePostbox_" + participantName);

        StringBuilder sqlStringBuilder01 = new StringBuilder();
        sqlStringBuilder01.append("CREATE TABLE IF NOT EXISTS ").append(table).append(" (");
        sqlStringBuilder01.append("id TINYINT NOT NULL").append(",");
        sqlStringBuilder01.append("participant_from_id TINYINT NOT NULL").append(",");
        sqlStringBuilder01.append("message VARCHAR(50) NOT NULL").append(",");
        sqlStringBuilder01.append("timestamp BIGINT NOT NULL").append(",");
        sqlStringBuilder01.append("PRIMARY KEY (id)");
        sqlStringBuilder01.append(")");
        System.out.println("sqlStringBuilder : " + sqlStringBuilder01.toString());
        update(sqlStringBuilder01.toString());

        StringBuilder sqlStringBuilder02 = new StringBuilder();
        sqlStringBuilder02.append("ALTER TABLE ").append(table).append(" ADD CONSTRAINT IF NOT EXISTS fk_postbox_").append(participantName);
        sqlStringBuilder02.append(" FOREIGN KEY (participant_from_id) ");
        sqlStringBuilder02.append("REFERENCES participants (id) ");
        sqlStringBuilder02.append("ON DELETE CASCADE");
        System.out.println("sqlStringBuilder : " + sqlStringBuilder02.toString());
        update(sqlStringBuilder02.toString());
    }



    public void shutdown() {
        System.out.println("--- shutdown");

        try {
            Statement statement = connection.createStatement();
            statement.execute("SHUTDOWN");
            connection.close();
        } catch (SQLException sqle) {
            System.out.println(sqle.getMessage());
        }
    }

    public void setupDatabase() {
        setupConnection();
        createTableAlgorithms();
        createTableTypes();
        createTableParticipants();
        createTableChannel();
        createTableMessages();
        createTablePostbox("msa");
    }
}