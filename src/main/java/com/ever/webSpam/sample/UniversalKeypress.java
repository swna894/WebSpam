package com.ever.webSpam.sample;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
 
public class UniversalKeypress extends Application {
 
    public static void main(String[] args) {
        launch(args);
    }
 
    @Override
    public void start(Stage stage) throws Exception {
        TextField textfield1 = new TextField();
        TextField textfield2 = new TextField();
        VBox root = new VBox(textfield1, textfield2);
        Scene scene = new Scene(root);
         
        scene.addEventFilter(KeyEvent.KEY_PRESSED, (KeyEvent event) -> {
            System.out.println("Key pressed");
            if (event.getCode() == KeyCode.F1) {
                System.out.println("F1 pressed");
            }
            event.consume();
        });
         
        stage.setScene(scene);
        stage.show();
    }
 
}
