import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

public class GUI extends Application {

    private boolean isDebugMode = false;

    public void start(Stage primaryStage) {
        primaryStage.setTitle("MSA | Mergentheim/Mosbach Security Agency");

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

        executeButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                System.out.println("[execute] pressed");
                try{
                    Query query = new Query(commandLineArea.getText());
                    switch (query.getAction()){
                        case encrypt:
                        case decrypt:
                            String result;
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
                            //TODO  register normal/intruder
                            break;
                        case create:
                            //TODO  create channel
                            break;
                        case show:
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
            }
        });

        closeButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent actionEvent) {
                System.out.println("[close] pressed");
                System.exit(0);
            }
        });

        hBox.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
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
                else if(event.getCode() == KeyCode.F8){     //Print last Logfile in Output

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
        if (! directory.exists())
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
            System.exit(-1);
        }
    }
}