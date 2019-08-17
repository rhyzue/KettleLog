import java.util.*;
import javafx.util.*;
import javafx.stage.*;
import javafx.event.*;
import javafx.scene.Scene;
import javafx.scene.text.*;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.scene.paint.Color;

import javafx.collections.transformation.*;



public class NotifStage extends Stage{

    private static Button clearBtn = new Button("Clear");
    private static Button closeBtn = new Button("Close");
    private static Label notifLabel = new Label("Notifications");

    private static AnchorPane topAnchor = new AnchorPane();
    private static AnchorPane bottomAnchor = new AnchorPane();
    private static BorderPane notifBP = new BorderPane();

    private static Kettlelog kettle = new Kettlelog();

    NotifStage(){
        NotifHandler notifHandler = new NotifHandler();

        //TOP (LABEL, CLEAR BTN)
        clearBtn.setId("clearBtn");
        clearBtn.setOnAction(notifHandler);
        AnchorPane.setRightAnchor(clearBtn, 10.0);
        AnchorPane.setTopAnchor(clearBtn, 20.0);

        AnchorPane.setLeftAnchor(notifLabel, 10.0);
        AnchorPane.setTopAnchor(notifLabel, 20.0);
        topAnchor.getChildren().addAll(notifLabel, clearBtn);


        //BOTTOM (CLOSE BTN)
        closeBtn.setId("closeBtn");
        closeBtn.setOnAction(notifHandler);

        AnchorPane.setRightAnchor(closeBtn, 10.0);
        AnchorPane.setBottomAnchor(closeBtn, 10.0);
        bottomAnchor.getChildren().addAll(closeBtn);



        notifBP.setTop(topAnchor);
        notifBP.setBottom(bottomAnchor);

        this.setResizable(false);
        this.setScene(new Scene(notifBP, 500, 500));
        this.initOwner(kettle.getPrimaryStage());
        this.initStyle(StageStyle.UNDECORATED);
        this.initModality(Modality.WINDOW_MODAL);

    }

    public void updateNotifStage(){
        

    }

    public class NotifHandler implements EventHandler<ActionEvent>{
        @Override
        public void handle(ActionEvent e) {

            String itemClicked = ((Control)e.getSource()).getId();

            switch(itemClicked){
                case "closeBtn":
                    kettle.hideNotifStage();
                    break;
                case "clearBtn":
                    break;
                default:
                    System.out.println("Default.");
            }
        }
    }

}