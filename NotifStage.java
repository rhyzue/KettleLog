import Notif.*;
import java.util.*;
import javafx.util.*;
import javafx.stage.*;
import javafx.event.*;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.text.*;
import javafx.scene.image.*;
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
    private static Button refreshBtn = new Button();
    private static Label notifLabel = new Label("Notifications");
    private static Font notifFont = new Font(20);

    //panes
    private static AnchorPane topAnchor = new AnchorPane();
    private static AnchorPane bottomAnchor = new AnchorPane();
    private static ScrollPane sp = new ScrollPane();
    private static VBox notifVB = new VBox(5);
    private static BorderPane notifBP = new BorderPane();
    
    //controls
    private static List<Notif> notifList = new ArrayList<Notif>();
    private static Scene notifScene;
    private static Kettlelog kettle = new Kettlelog();

    NotifStage(){
        NotifStageHandler notifStageHandler = new NotifStageHandler();

        //TOP (LABEL, CLEAR BTN)
        clearBtn.setId("clearBtn");
        clearBtn.setOnAction(notifStageHandler);
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
        closeBtn.setOnAction(notifStageHandler);

        Image refreshImg = new Image("./Misc/refreshBlack.png");
        ImageView refreshImgView = new ImageView();
            refreshBtn.setStyle("-fx-background-color: transparent;");             
            refreshImgView.setImage(refreshImg);
            refreshImgView.setFitWidth(25);
            refreshImgView.setPreserveRatio(true);
            refreshImgView.setSmooth(true);
            refreshImgView.setCache(true); 
            refreshBtn.setGraphic(refreshImgView);

        Tooltip refreshTP = new Tooltip("Refresh");
        refreshTP.setShowDelay(new javafx.util.Duration(100.0));
        refreshBtn.setTooltip(refreshTP); 

        refreshBtn.setId("refreshBtn");
        refreshBtn.setOnAction(notifStageHandler);

        AnchorPane.setRightAnchor(closeBtn, 10.0);
        AnchorPane.setBottomAnchor(closeBtn, 10.0);
        AnchorPane.setLeftAnchor(refreshBtn, 10.0);
        AnchorPane.setBottomAnchor(refreshBtn, 7.0);
        bottomAnchor.setStyle("-fx-background-color: #ff940c;");
        bottomAnchor.setPrefHeight(50);
        bottomAnchor.getChildren().addAll(refreshBtn, closeBtn);


        //VBOX CONTENTS (NOTIFS)
        notifVB.setPadding(new Insets(10.0, 20.0, 10.0, 20.0));
        notifVB.setStyle("-fx-background-color: #ffe1bb;");

        for(int i = 0; i<20; i++){
            String notifID = "NOTIF"+String.valueOf(i);
            Notif notifBox = new Notif();
            notifList.add(notifBox);
            notifVB.getChildren().add(notifList.get(i).getNotifBox());
        }

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

    public void updateNotifStage(List<Notif> kettleNotifs){
        
        for(int i = 0; i< kettleNotifs.size(); i++){
            //SET ALL NOTIF DATA TO KETTLE NOTIF DATA

            notifList.get(i).setMessage(kettleNotifs.get(i).getMessage());
        }

    }

    public class NotifStageHandler implements EventHandler<ActionEvent>{
        @Override
        public void handle(ActionEvent e) {

            String itemClicked = ((Control)e.getSource()).getId();

            if(itemClicked.equals("closeBtn")){
                kettle.hideNotifStage();
            }
            else if(itemClicked.equals("clearBtn")){
                for(int i = 0; i<notifList.size(); i++){
                    notifList.get(i).setNotifVisible(false);
                    //
                }
                //also clear the db
            }
            else if(itemClicked.equals("refreshBtn")){
                //reload db info
            }

        }
    }

}