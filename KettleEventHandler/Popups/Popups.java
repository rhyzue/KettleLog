package Popups;

import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.application.Application;

public class Popups extends Application {
    public static void main(String[] args) {
        launch(args);
    }
    
    @Override
    //SETUP
    public void start(Stage addw) {

        addw.setResizable(false);
        addw.setTitle("Add a New Issue");

        BorderPane abase = new BorderPane();

        addw.setScene(new Scene(abase, 1024, 745));
        addw.show();

    }

}