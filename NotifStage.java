import Item.*;
import Notif.*;
import java.text.*;
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

    private static List<NotifContainer> notifBoxList = new ArrayList<NotifContainer>();
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
            NotifContainer notifBox = new NotifContainer();
            notifBoxList.add(notifBox);
            notifVB.getChildren().add(notifBox.getNotifBox());
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

        System.out.println("kettle notif size: "+kettleNotifs.size());
        int sz = kettleNotifs.size();
        int numToGen = 20;
        
        for(int i = 0; i<numToGen; i++){
            if(i<sz){
                //SET ALL NOTIF DATA TO KETTLE NOTIF DATA
                //if the item doesn't have status deleted, add it to stage's notifs
                notifBoxList.get(i).updateNotifContainer(kettleNotifs.get(i));
                notifBoxList.get(i).setNotifVisible(true);
            }
            //if less than 20 notifs exist
            else{
                notifBoxList.get(i).setNotifVisible(false);
            }
        }
    }


    public void onClose(){
        List<Notif> notifsToUpdate = new ArrayList<Notif>();

        for(int i = 0; i<20; i++){
            Notif curNotif = notifBoxList.get(i).getNotif();

            if(curNotif.getReadStatus()==-1){
                //if this item has reorder date<=today && dategenerated==today, read status should be -3
                Item it = kettle.getItemById(curNotif.getItemId());

                //if the user already deleted the item, we do not need to worry about regenerating
                //so treat like regular delete
                if(it==null){
                    notifsToUpdate.add(curNotif);
                    continue;
                }

                //get value of ROD
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                java.util.Date today = new java.util.Date();
                String todayString = dateFormat.format(today);
                java.util.Date rod = new java.util.Date();
                String rodString = it.getROD();
                try{
                    rod = dateFormat.parse(rodString);
                }
                catch(ParseException ex){
                    ex.printStackTrace();
                }

                //set read status to -3 
                if(todayString.equals(curNotif.getDateGenerated()) && (today.after(rod) || todayString.equals(rodString))){
                    curNotif.setReadStatus(-3);
                } 
                notifsToUpdate.add(curNotif);
            }
            else if (curNotif.getReadStatus()==0 || curNotif.getReadStatus()==1){
                notifsToUpdate.add(curNotif);
            }
        } 
        kettle.updateNotifs(notifsToUpdate);
    }


    public class NotifStageHandler implements EventHandler<ActionEvent>{
        @Override
        public void handle(ActionEvent e) {

            String itemClicked = ((Control)e.getSource()).getId();

            if(itemClicked.equals("closeBtn")){
                kettle.hideNotifStage();
                onClose();
                kettle.primaryStage.updateNotifIcon();
            }
            else if(itemClicked.equals("clearBtn")){
                //if notifs are visible, set them to not visible
                //set read status to -1

                for(int i =0; i<20; i++){
                    if(notifBoxList.get(i).getNotifVisible()){
                        notifBoxList.get(i).setNotifReadStatus(-1);
                        notifBoxList.get(i).setNotifVisible(false);
                    }
                }
                
            }
            else if(itemClicked.equals("refreshBtn")){
                kettle.hideNotifStage();
                onClose();
                kettle.primaryStage.updateNotifIcon();
                kettle.showNotifStage();
            }

        }
    }

}