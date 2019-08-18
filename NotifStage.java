import java.util.*;
import javafx.util.*;
import javafx.stage.*;
import javafx.event.*;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.text.*;
import javafx.scene.paint.*;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.scene.control.ScrollPane.*;



import javafx.collections.transformation.*;

/*
    Notif stage contains rows of hboxes, each with:
    1. Button: mark read/unread, will toggle
    2. Label: Message
    3. Button: Visit item (pulls out info stage)

    Need db to save notifs: Contains message, item id, read status

*/

public class NotifStage extends Stage{

    //components
    private static Button clearBtn = new Button("Clear");
    private static Button closeBtn = new Button("Close");
    private static Label notifLabel = new Label("Notifications");
    private static Font notifFont = new Font(20);

    //panes
    private static AnchorPane topAnchor = new AnchorPane();
    private static AnchorPane bottomAnchor = new AnchorPane();
    private static ScrollPane sp = new ScrollPane();
    private static VBox notifVB = new VBox(5);
    private static BorderPane notifBP = new BorderPane();
    
    //controls
    private static List<HBox> notifList = new ArrayList<>();

    private static Scene notifScene;
    private static Kettlelog kettle = new Kettlelog();

    NotifStage(){
        NotifHandler notifHandler = new NotifHandler();

        //TOP (LABEL, CLEAR BTN)
        clearBtn.setId("clearBtn");
        clearBtn.setOnAction(notifHandler);
        AnchorPane.setRightAnchor(clearBtn, 10.0);
        AnchorPane.setTopAnchor(clearBtn, 20.0);

        notifLabel.setFont(notifFont);
        notifLabel.setStyle("-fx-text-fill: white");
        AnchorPane.setLeftAnchor(notifLabel, 20.0);
        AnchorPane.setTopAnchor(notifLabel, 20.0);
        topAnchor.setStyle("-fx-background-color: #ff940c;");
        topAnchor.setPrefHeight(60);
        topAnchor.getChildren().addAll(notifLabel, clearBtn);


        //BOTTOM (CLOSE BTN)
        closeBtn.setId("closeBtn");
        closeBtn.setOnAction(notifHandler);

        AnchorPane.setRightAnchor(closeBtn, 10.0);
        AnchorPane.setBottomAnchor(closeBtn, 10.0);
        bottomAnchor.setStyle("-fx-background-color: #ff940c;");
        bottomAnchor.setPrefHeight(50);
        bottomAnchor.getChildren().addAll(closeBtn);


        //VBOX CONTENTS (NOTIFS)
        notifVB.setPadding(new Insets(10.0, 25.0, 10.0, 25.0));
        notifVB.setStyle("-fx-background-color: #ffe1bb;");

        for(int i = 0; i<20; i++){
            String noID = "Empty"+String.valueOf(i);
            notifList.add(generateNotifBox(noID));
        }
        notifVB.getChildren().addAll(notifList);

        //SETTING CONTENT OF SCROLLPANE
        sp.setContent(notifVB);
        sp.setFitToWidth(true);
        sp.setFitToHeight(true);

        //ADDING NODES TO MAIN BORDER PANE
        notifBP.setTop(topAnchor);
        notifBP.setBottom(bottomAnchor);
        notifBP.setCenter(sp);
        notifBP.setStyle("-fx-background-color: #ffe1bb;");

        notifScene = new Scene(notifBP, 500, 500);

        this.setResizable(false);
        this.setScene(notifScene);
        this.initOwner(kettle.getPrimaryStage());
        this.initStyle(StageStyle.UNDECORATED);
        this.initModality(Modality.WINDOW_MODAL);

    }

    public void updateNotifStage(){
        

    }

    public HBox generateNotifBox(String id){ //should take in string id: id = empty+num on initial generation, will be replaced later on
        NotifHandler notifHandler = new NotifHandler();

        HBox hb = new HBox(10);
        Button readBtn = new Button("Mark read");
        readBtn.setPrefWidth(100);
        readBtn.setId("READ-"+id); //later in switch/case, if item contains readbtn, split and update status
        readBtn.setOnAction(notifHandler);

        Label message = new Label("Reorder needed for item: Gloves");
        message.setPrefWidth(240.0);
        message.setPrefHeight(30.0);
        message.setId("MSG-"+id); //use scene.lookup to set message in update notifstage

        Button linkBtn = new Button("View");
        linkBtn.setId("LINK-"+id);
        linkBtn.setOnAction(notifHandler);

        hb.setStyle("-fx-background-color: #ffbe5c;");
        hb.setPadding(new Insets(10, 10, 10, 10));
        hb.getChildren().addAll(readBtn, message, linkBtn);
        hb.setId("HB-"+id);
        return hb;
    }

    public class NotifHandler implements EventHandler<ActionEvent>{
        @Override
        public void handle(ActionEvent e) {

            String itemClicked = ((Control)e.getSource()).getId();

            if(itemClicked.equals("closeBtn")){
                kettle.hideNotifStage();
            }
            else if(itemClicked.equals("clearBtn")){

            }
            else if(itemClicked.contains("READ")){
                //get the id of the hbox, and lower its opacity
                String lookupID = itemClicked.split("-")[1];
                HBox hb = (HBox)notifScene.lookup("#HB-"+lookupID);
                Button rd = (Button) notifScene.lookup("#READ-"+lookupID);

                //if the notif is already read, mark it unread
                if(hb.getOpacity()==0.5){
                    hb.setOpacity(1);
                    rd.setText("Mark Read");
                }
                else{
                    hb.setOpacity(0.5);
                    rd.setText("Mark Unread");
                }

            }


        }
    }

}