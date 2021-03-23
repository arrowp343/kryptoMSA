import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.*;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;

import hsqldb.HSQLDB;

public class GUI extends Application {

    private boolean isDebugMode = false;

    public void start(Stage primaryStage) {
        primaryStage.setTitle("MSA | Mergentheim/Mosbach Security Agency");

        HSQLDB.instance.setupDatabase();

        HBox hBox = new HBox();
        hBox.setPadding(new Insets(15, 12, 15, 12));
        hBox.setSpacing(10);
        hBox.setStyle("-fx-background-color: #336699;");

        Button executeButton = new Button("Execute");
        executeButton.setPrefSize(100, 20);

        Button closeButton = new Button("Close");
        closeButton.setPrefSize(100, 20);

        TextArea commandLineArea = new TextArea();
        commandLineArea.setWrapText(true);

        TextArea outputArea = new TextArea();
        outputArea.setWrapText(true);
        outputArea.setEditable(false);

        executeButton.setOnAction(event -> {
            System.out.println("[execute] pressed");
            try{
                Query query = new Query(commandLineArea.getText());
                switch (query.getAction()){
                    case encrypt:
                    case decrypt:
                        switch (query.getAlgorithm()){
                            case Shift:
                                //TODO shift encrypt/decrypt
                            case RSA:
                                //TODO rsa encrypt/decrypt
                                break;
                        }
                        break;
                    case crack:
                        //TODO  crack shift/rsa
                        break;
                    case register:
                        HSQLDB.instance.registerParticipant(query.getName(), query.getType());
                        outputArea.setText(query.getOutput());
                        break;
                    case create:
                        //TODO  create channel
                        break;
                    case show:
                        outputArea.setText(query.getOutput());
                        //TODO  show channel
                        break;
                    case drop:
                        //TODO  drop channel
                        break;
                    case intrude:
                        //TODO  intrude channel
                        break;
                    case send:
                        //TODO  send message
                        break;
                }
            } catch (Exception e){
                outputArea.setText(e.getMessage());
            }
        });

        closeButton.setOnAction(actionEvent -> {
            System.out.println("[close] pressed");
            HSQLDB.instance.shutdown();
            System.exit(0);
        });

        hBox.setOnKeyPressed(event -> {
            if(event.getCode() == KeyCode.F3){          //Debug-Mode
                String message;
                if(isDebugMode){
                    isDebugMode = false;
                    message = "Debug-Mode was disabled";
                } else {
                    isDebugMode = true;
                    message = "Debug-Mode was enabled";
                }
                outputArea.setText(message);
            }
            else if(event.getCode() == KeyCode.F8) {     //Print latest Logfile in Output
                try {
                    File dir = new File("log");
                    File[] files  = dir.listFiles();
                    if(files != null) {
                        Arrays.sort(files, new Comparator() {
                            public int compare(Object o1, Object o2) {
                                return compare((File) o1, (File) o2);
                            }
                            private int compare(File f1, File f2) {
                                long result = f2.lastModified() - f1.lastModified();
                                if (result > 0) {
                                    return 1;
                                } else if (result < 0) {
                                    return -1;
                                } else {
                                    return 0;
                                }
                            }
                        });
                        File latestFile = files[0];
                        BufferedReader reader = new BufferedReader(new FileReader(latestFile));
                        StringBuilder stringBuilder = new StringBuilder();
                        String line;
                        String ls = System.getProperty("line.separator");
                        stringBuilder.append("Latest Logfile: ").append(latestFile).append(ls).append("-------------------------------------").append(ls);
                        while ((line = reader.readLine()) != null) {
                            stringBuilder.append(line);
                            stringBuilder.append(ls);
                        }
                        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
                        reader.close();

                        outputArea.setText(stringBuilder.toString());
                    } else {
                        outputArea.setText("No Logfile found!");
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        hBox.getChildren().addAll(executeButton, closeButton);

        VBox vbox = new VBox(20);
        vbox.setPadding(new Insets(25, 25, 25, 25));
        vbox.getChildren().addAll(hBox, commandLineArea, outputArea);

        Scene scene = new Scene(vbox, 950, 500);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void createLogFile(String value){
        String directoryName = "log";
        Date timeStamp = new Date();
        String fileName = "kryptoLogFile" + timeStamp.toInstant() + ".txt";

        File directory = new File(directoryName);
        if (!directory.exists())
            directory.mkdir();

        File file = new File(directoryName + "/" + fileName);
        try{
            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(value);
            bw.close();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }
}