package KettleEventHandler;

import javafx.event.ActionEvent;
import javafx.event.ActionEvent; 
import javafx.event.EventHandler; 

public class KettleEventHandler implements EventHandler<ActionEvent>{

    public void handle(ActionEvent e) {
        System.out.println("Click");
    }

}