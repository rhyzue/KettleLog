package Notif;

import javafx.event.*;
import javafx.geometry.*;
import javafx.scene.text.*;
import javafx.scene.image.*;
import javafx.scene.paint.*;
import javafx.scene.control.*;

public class Notif {

	public HBox Notif(){ //should take in string id: id = empty+num on initial generation, will be replaced later on
        NotifHandler notifHandler = new NotifHandler();

        HBox hb = new HBox(10);
        Button readBtn = new Button("Mark read");
        readBtn.setPrefWidth(100);
        readBtn.setId("READ-"+id); //later in switch/case, if item contains readbtn, split and update status
        readBtn.setOnAction(notifHandler);

        Label message = new Label(id);
        message.setPrefWidth(250.0);
        message.setPrefHeight(30.0);
        message.setId("MSG-"+id); //use scene.lookup to set message in update notifstage

        Button linkBtn = new Button();

        Image linkBtnImg = new Image("./Misc/link.png");
        ImageView linkImg = new ImageView();          
        linkBtn.setStyle("-fx-background-color: transparent;");             
            linkImg.setImage(linkBtnImg);
            linkImg.setFitWidth(20);
            linkImg.setPreserveRatio(true);
            linkImg.setSmooth(true);
            linkImg.setCache(true); 
        linkBtn.setGraphic(linkImg);

        Tooltip linkTP = new Tooltip("View");
        linkTP.setShowDelay(new javafx.util.Duration(100.0));
        linkBtn.setTooltip(linkTP);   
        linkBtn.setId("LINK-"+id);
        linkBtn.setOnAction(notifHandler);

        Button delBtn = new Button();

        Image delBtnImg = new Image("./Misc/delete2.png");
        ImageView delImg = new ImageView();          
        delBtn.setStyle("-fx-background-color: transparent;");             
            delImg.setImage(delBtnImg);
            delImg.setFitWidth(20);
            delImg.setPreserveRatio(true);
            delImg.setSmooth(true);
            delImg.setCache(true); 
        delBtn.setGraphic(delImg); 

        Tooltip delTP = new Tooltip("Delete");
        delTP.setShowDelay(new javafx.util.Duration(100.0));
        delBtn.setTooltip(delTP);  
        delBtn.setOnAction(notifHandler);
        delBtn.setId("DEL-"+id);

        hb.setStyle("-fx-background-color: #ffbe5c;");
        hb.setPadding(new Insets(10, 10, 10, 10));
        hb.getChildren().addAll(readBtn, message, linkBtn, delBtn);
        hb.setId("HB-"+id);
        return hb;
    }

    public class NotifHandler implements EventHandler<ActionEvent>{


}