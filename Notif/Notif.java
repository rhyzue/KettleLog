package Notif;

import javafx.event.*;
import javafx.geometry.*;
import javafx.scene.text.*;
import javafx.scene.image.*;
import javafx.scene.paint.*;
import javafx.scene.layout.*;
import javafx.scene.control.*;

public class Notif {

	private HBox hb;
    private Button readBtn = new Button("Mark read");
    private Label message = new Label("Message");
    private Button linkBtn = new Button();
    private Image linkBtnImg = new Image("./Misc/link.png");
    private ImageView linkImg = new ImageView();  
    private Tooltip linkTP = new Tooltip("View");
    private Button delBtn = new Button();
    private Image delBtnImg = new Image("./Misc/delete2.png");
    private ImageView delImg = new ImageView();    
    private Tooltip delTP = new Tooltip("Delete");

    private String linkId;
    private String messageStr;
    private int readStatus;
    private String notifId;

	public Notif(){
        this.linkId = "No ID given";
        this.hb = makeNotifBox();
    }

    public Notif(String messageStr, String linkId, int readStatus, String notifId){
        this.messageStr = messageStr;
        this.linkId = linkId;
        this.readStatus = readStatus;
        this.notifId = notifId;
    	this.hb= makeNotifBox();
    }

    public HBox makeNotifBox(){
    	hb = new HBox(10);
    	NotifHandler notifHandler = new NotifHandler();

        readBtn.setPrefWidth(100);
        readBtn.setId("readBtn"); 
        readBtn.setOnAction(notifHandler);

        message.setPrefWidth(250.0);
        message.setPrefHeight(30.0);

        linkBtn.setStyle("-fx-background-color: transparent;");             
            linkImg.setImage(linkBtnImg);
            linkImg.setFitWidth(20);
            linkImg.setPreserveRatio(true);
            linkImg.setSmooth(true);
            linkImg.setCache(true); 
        linkBtn.setGraphic(linkImg);

        linkTP.setShowDelay(new javafx.util.Duration(100.0));
        linkBtn.setTooltip(linkTP);   
        linkBtn.setId("linkBtn");
        linkBtn.setOnAction(notifHandler);

        delBtn.setStyle("-fx-background-color: transparent;");             
            delImg.setImage(delBtnImg);
            delImg.setFitWidth(20);
            delImg.setPreserveRatio(true);
            delImg.setSmooth(true);
            delImg.setCache(true); 
        delBtn.setGraphic(delImg); 

        delTP.setShowDelay(new javafx.util.Duration(100.0));
        delBtn.setTooltip(delTP);  
        delBtn.setOnAction(notifHandler);
        delBtn.setId("delBtn");

        hb.setStyle("-fx-background-color: #ffbe5c;");
        hb.setPadding(new Insets(10, 10, 10, 10));
        hb.getChildren().addAll(readBtn, message, linkBtn, delBtn);
    	return hb;
    }

    public HBox getNotifBox(){
    	return hb;
    }

    public void setNotifVisible(boolean value){
    	hb.setVisible(value);
    }

    public String getMessage(){
        return messageStr;
    }

    public void setMessage(String text){
        this.messageStr = text;
    	message.setText(text);
    }

    public String getItemId(){
        return linkId;
    }

    public void setItemId(String linkId){
        this.linkId = linkId;
    }

    public int getReadStatus(){
        return readStatus;
    }

    public void setReadStatus(int readStatus){
        this.readStatus = readStatus;
        hb.setVisible(readStatus==1);
    }


    public String getNotifId(){
        return notifId;
    }

    public void setNotifId(String notifId){
        this.notifId = notifId;
    }


    public class NotifHandler implements EventHandler<ActionEvent>{
    	@Override
        public void handle(ActionEvent e) {

            String itemClicked = ((Control)e.getSource()).getId();

            switch(itemClicked){
            	case "readBtn":
	            	if(hb.getOpacity()==0.5){
	                    hb.setOpacity(1);
	                    readBtn.setText("Mark Read");
	                }
	                else{
	                    hb.setOpacity(0.5);
	                    readBtn.setText("Mark Unread");
	                }
            		break;
            	case "delBtn":
            		hb.setVisible(false);
            		break;
	            default:
	            	System.out.println("other");

            }
        }
    }
}