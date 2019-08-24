package Notif;

import javafx.event.*;
import javafx.geometry.*;
import javafx.scene.text.*;
import javafx.scene.image.*;
import javafx.scene.paint.*;
import javafx.scene.layout.*;
import javafx.scene.control.*;

public class Notif {

    private String linkId;
    private String messageStr;
    private int readStatus;
    private String notifId;
    private String dateGenerated;

    /*
        readStatus:
        0=unread
        1=read
        -1=need to delete
        -2=empty
        -3= reorder date is today and notif has been deleted

    */

	public Notif(){
        this.linkId = "N/A";
        this.readStatus = -2;
    }

    public Notif(String messageStr, String linkId, int readStatus, String notifId, String dateGenerated){
        this.messageStr = messageStr;
        this.linkId = linkId;
        this.readStatus = readStatus;
        this.notifId = notifId;
        this.dateGenerated = dateGenerated;
    }

    public String getMessage(){
        return messageStr;
    }

    public void setMessage(String text){
        this.messageStr = text;
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
    }


    public String getNotifId(){
        return notifId;
    }

    public void setNotifId(String notifId){
        this.notifId = notifId;
    }

    public String getDateGenerated(){
        //return dateGenerated;
        return "2019-08-23";
    }

    public void setDateGenerated(String dateGenerated){
        this.dateGenerated = dateGenerated;
    }
}