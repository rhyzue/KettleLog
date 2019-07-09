package KettleEventHandler;

import javafx.event.ActionEvent;
import java.beans.EventHandler;

public class KettleEventHandler implements EventHandler<ActionEvent> {

    @Override
    public void handle(ActionEvent event) {
        System.out.println("Click");
    }

}