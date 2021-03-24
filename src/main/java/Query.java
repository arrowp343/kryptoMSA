import enums.Action;
import enums.Algorithm;
import enums.Type;
import hsqldb.HSQLDB;

import java.util.List;

public class Query {
    Action action;
    String message;
    Algorithm algorithm;
    int key;
    String keyFile;
    String name;
    Type type;
    String participant01, participant02;
    String output;

    public Query(String query) throws Exception{

        String[] command = query.split("\"");
        String[] commandPart;
        if (command.length == 1) {       // keine anführungszeichen
            commandPart = command[0].split(" ");
        } else if (command.length == 3) {   // eine message (zwei anführungszeichen)
            message = command[1];
            commandPart = (command[0] + " " + command[2]).split(" ");
        } else {
            throw new Exception("Anführungszeichen-Fehler");
        }
        switch (commandPart[0]) {
            case "encrypt", "decrypt" -> {
                if (commandPart[0].equals("encrypt")) action = Action.encrypt;
                else action = Action.decrypt;
                expect(commandPart[1], "message");
                expect(commandPart[2], "");
                expect(commandPart[3], "");
                expect(commandPart[4], "using");
                switch (commandPart[5]) {
                    case "shift" -> {
                        algorithm = Algorithm.Shift;
                        expect(commandPart[6], "and");
                        expect(commandPart[7], "key");
                        key = Integer.parseInt(commandPart[8]);
                    }
                    case "rsa" -> {
                        algorithm = Algorithm.RSA;
                        expect(commandPart[6], "and");
                        expect(commandPart[7], "keyfile");
                        keyFile = commandPart[8];
                    }
                    default -> throw new Exception("Unknown Algorithm '" + commandPart[5] + "' - choose between classic or rsa");
                }
            }
            case "crack" -> {
                action = Action.crack;
                expect(commandPart[1], "encrypted");
                expect(commandPart[2], "message");
                expect(commandPart[3], "");
                expect(commandPart[4], "");
                expect(commandPart[5], "using");
                switch (commandPart[6]) {
                    case "shift" -> algorithm = Algorithm.Shift;
                    case "rsa" -> {
                        algorithm = Algorithm.RSA;
                        expect(commandPart[7], "and");
                        expect(commandPart[8], "keyfile");
                        keyFile = commandPart[9];
                    }
                    default -> throw new Exception("Unknown Algorithm '" + commandPart[6] + "' - choose between classic or rsa");
                }
            }
            case "register" -> {
                action = Action.register;
                expect(commandPart[1], "participant");
                name = commandPart[2];
                expect(commandPart[3], "with");
                expect(commandPart[4], "type");
                switch (commandPart[5]) {
                    case "normal" -> type = Type.normal;
                    case "intruder" -> type = Type.intruder;
                    default -> throw new Exception("unknown Type '" + commandPart[5] + "' - choose between normal and intruder");
                }
                output = "participant " + name + "with type " + commandPart[5] + " registered and postbox_" + name + " created";
            }
            case "create" -> {
                action = Action.create;
                expect(commandPart[1], "channel");
                name = commandPart[2];
                expect(commandPart[3], "from");
                participant01 = commandPart[4];
                expect(commandPart[5], "to");
                participant02 = commandPart[6];
                output = "channel " + name + " from " + participant01 + " to " + participant02 + " successfully created";
            }
            case "show" -> {
                action = Action.show;
                List<String> result;
                switch (commandPart[1]){
                    case "participant":
                        result = HSQLDB.instance.showParticipants();
                        break;
                    case "channel":
                        result = HSQLDB.instance.showChannel();
                        break;
                    default:
                        throw new Exception("Syntax-Error at '" + commandPart[1] + "' - expected participant or channel");
                }
                StringBuilder outputParticipants = new StringBuilder();
                result.forEach(r -> outputParticipants.append(r).append(System.getProperty("line.separator")));
                outputParticipants.deleteCharAt(outputParticipants.length() - 1);
                output = outputParticipants.toString();
            }
            case "drop" -> {
                action = Action.drop;
                expect(commandPart[1], "channel");
                name = commandPart[2];
                output = "channel " + name + " deleted";
            }
            case "intrude" -> {
                action = Action.intrude;
                expect(commandPart[1], "channel");
                name = commandPart[2];
                expect("by", commandPart[3]);
                participant01 = commandPart[4];
            }
            case "send" -> {
                action = Action.send;
                expect("message", commandPart[1]);
                expect("", commandPart[2]);
                expect("", commandPart[3]);
                expect("from", commandPart[4]);
                participant01 = commandPart[5];
                expect("to", commandPart[6]);
                participant02 = commandPart[7];
                expect("using", commandPart[8]);
                switch (commandPart[9]) {
                    case "shift" -> {
                        algorithm = Algorithm.Shift;
                        expect("and", commandPart[10]);
                        expect("key", commandPart[11]);
                        key = Integer.parseInt(commandPart[12]);
                    }
                    case "rsa" -> {
                        algorithm = Algorithm.RSA;
                        expect("and", commandPart[10]);
                        expect("keyfile", commandPart[11]);
                        keyFile = commandPart[12];
                    }
                    default -> throw new Exception("Unknown Algorithm '" + commandPart[6] + "' - choose between classic or rsa");
                }
            }
        }
    }
    private void expect(String expect, String commandPart) throws Exception{
        if(!commandPart.equals(expect))
            throw new Exception("Syntax-Error at '" + commandPart + "' - expected " + expect);
    }

    public Action getAction(){
        return action;
    }
    public String getMessage() { return message; }

    public Algorithm getAlgorithm(){
        return algorithm;
    }
    public int getKey() { return key; }
    public String getKeyFile() { return keyFile; }
    public String getName() { return name; }
    public Type getType() { return type; }
    public String getParticipant01() { return participant01; }
    public String getParticipant02() { return participant02; }
    public String getOutput() { return output; }
}
