package application;

import Item.*;
import java.time.*; 
import java.util.*;
import javafx.util.*;
import javafx.stage.*;
import javafx.event.*;
import javafx.geometry.*;
import java.time.chrono.*; 
import javafx.scene.Scene;
import javafx.scene.text.*;
import java.time.LocalDate;
import javafx.collections.*;
import javafx.scene.image.*;
import javafx.beans.value.*;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.scene.chart.XYChart;
import javafx.geometry.Rectangle2D;
import javafx.scene.chart.LineChart;
import javafx.scene.image.ImageView;
import javafx.scene.control.Alert.*;
import javafx.scene.chart.NumberAxis;
import javafx.scene.input.MouseEvent;
import javafx.application.Application;
import java.time.format.DateTimeFormatter;
import javafx.collections.transformation.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.TableColumn.CellEditEvent;


public class InfoStage extends Stage{

    //Private Variables
    private static double infowidth = 500;
    private static double infoheight = 700;
    private static String infostriphex = "#004545;";
    private static String infomidhex = "#b8d6d6;";
    private static double distancedown = 40.0;

    //Objects
    private static Text infotitle = new Text();
    private static AnchorPane infotstrip = new AnchorPane();
    private static Label infolabel = new Label();
    private static Text dateadded = new Text();
    private static Label datelabel = new Label();
    private static Separator line1 = new Separator();
    private static Text adc = new Text();
    private static Label adclabel = new Label();
    private static Separator line2 = new Separator();
    private static TextArea infodesc = new TextArea();
    private static AnchorPane infocenter = new AnchorPane();
    private static Button infocancel = new Button();
    private static AnchorPane infobstrip = new AnchorPane();
    private static BorderPane infoborderpane = new BorderPane();

    //Variables whos values may change
    private String infostripcolour;
    private String infomidcolour;

    private static Kettlelog kettle = new Kettlelog();
    
    //Constructor
    InfoStage(){

        //Assign values to variables
        infostripcolour = String.format("-fx-background-color: %s", infostriphex);
        infomidcolour = String.format("-fx-background-color: %s", infomidhex);

        screenX = (xBounds + w / 2 - infowidth / 2); 
        screenY = (yBounds + h / 2 - infoheight / 2);

        //Don't allow stage to be moved
        ChangeListener<Number> widthListener = (observable, oldValue, newValue) -> {
            this.setX(screenX);
        };
        ChangeListener<Number> heightListener = (observable, oldValue, newValue) -> {
            this.setY(screenY);
        };

        this.widthProperty().addListener(widthListener);
        this.heightProperty().addListener(heightListener);

        this.setOnShown(shown -> {
            this.widthProperty().removeListener(widthListener);
            this.heightProperty().removeListener(heightListener);
        });


        //Top strip of info panel which has text saying "Item Information"      
        infotitle.setText("Item Information");
        infotitle.setFont(new Font(18));
        infotitle.setFill(Color.WHITE);

        
        AnchorPane.setLeftAnchor(infotitle, 50.0);
        AnchorPane.setBottomAnchor(infotitle, 5.0);
        infotstrip.setStyle(infostripcolour);
        infotstrip.setPrefSize(infowidth, 50); //(width, height)
        

        try {
            //  Block of code to try
            infotstrip.getChildren().addAll(infotitle);
        }
        catch(Exception e) {
        //  Block of code to handle errors
            System.out.println("Already added");
        }



        //Center portion of the info panel
        AnchorPane.setTopAnchor(infolabel, 25.0);
        AnchorPane.setLeftAnchor(infolabel, 100.0);
        infolabel.setText(rowinfo.getName());
        infolabel.setFont(new Font(16));
        infolabel.setPrefHeight(50.0);
        infolabel.setPrefWidth(300.0);
        infolabel.setFont(new Font(16));
        infolabel.setAlignment(Pos.CENTER);
        infolabel.setTextOverrun(OverrunStyle.ELLIPSIS);
        infolabel.setStyle("-fx-background-color: #709c9c");

        //Set date info text to anchor pane
        AnchorPane.setLeftAnchor(dateadded, 25.0);
        AnchorPane.setTopAnchor(dateadded, 102.0);
        dateadded.setText("Date Added");
        dateadded.setFont(new Font(16));

        AnchorPane.setRightAnchor(datelabel, 25.0);
        AnchorPane.setTopAnchor(datelabel, 100.0);
        datelabel.setText(rowinfo.getDateAdded());
        datelabel.setPrefHeight(25.0);
        datelabel.setPrefWidth(125.0);
        datelabel.setFont(new Font(16));
        datelabel.setAlignment(Pos.CENTER);
        datelabel.setStyle("-fx-background-color: #95bfbf");


        //Seperator
        AnchorPane.setTopAnchor(line1, 110.0);
        AnchorPane.setRightAnchor(line1, 160.0);
        line1.setPrefWidth(220.0);

        //average daily consumption text
        AnchorPane.setLeftAnchor(adc, 25.0);
        AnchorPane.setTopAnchor(adc, 102.0 + distancedown); // we're moving the other parts down
        adc.setText("Average Daily Consumption");
        adc.setFont(new Font(16));

        //add adc to anchorpane
        AnchorPane.setRightAnchor(adclabel, 25.0);
        AnchorPane.setTopAnchor(adclabel, 100.0 + distancedown);
        adclabel.setFont(new Font(14));
        adclabel.setPrefHeight(25.0);
        adclabel.setPrefWidth(125.0);
        adclabel.setAlignment(Pos.CENTER);
        adclabel.setStyle("-fx-background-color: #95bfbf");

        //seperator
        AnchorPane.setTopAnchor(line2, 110.0 + distancedown);
        AnchorPane.setRightAnchor(line2, 160.0);
        line2.setPrefWidth(100.0);

        //infodesc text area
        AnchorPane.setLeftAnchor(infodesc, 25.0);
        AnchorPane.setTopAnchor(infodesc, 100.0 + (distancedown * 2) + 10.0);
        infodesc.setPrefWidth(450.0);
        infodesc.setPrefHeight(125.0);
        infodesc.setEditable(false);
        infodesc.setStyle("-fx-opacity: 1;");
        infodesc.setWrapText(true);
        if (rowinfo.getDesc().trim().length() <= 0) {
            infodesc.setText("There is no description for this item.");
        } else {
            infodesc.setText("Item Description: " + rowinfo.getDesc());
        }

        //CONSUMPTION GRAPH THAT WILL SHOW THE USER'S USAGE
        infocenter.setStyle(infomidcolour);
        
        try {
            //  Block of code to try
            infocenter.getChildren().addAll(infolabel, dateadded, datelabel, line1, adc, adclabel, line2, infodesc);
        }
        catch(Exception e) {
        //  Block of code to handle errors
            System.out.println("Already added");
        }

        //Bottom part of the strip that has a cancel button
        infocancel.setText("Close");
        infocancel.setPrefHeight(27.5);
        infocancel.setId("infocancel");
        InfoHandler infoHandler = new InfoHandler();
        infocancel.setOnAction(infoHandler); 
        
        AnchorPane.setRightAnchor(infocancel, 5.0);
        AnchorPane.setTopAnchor(infocancel, 5.0);
        infobstrip.setStyle(infostripcolour);
        infobstrip.setPrefSize(infowidth, 37.5);

        try {
            //  Block of code to try
            infobstrip.getChildren().addAll(infocancel);
        }
        catch(Exception e) {
        //  Block of code to handle errors
            System.out.println("Already added");
        }
        
        
        infoborderpane.setTop(infotstrip);
        infoborderpane.setCenter(infocenter);
        infoborderpane.setBottom(infobstrip);

        this.setResizable(false);
        this.initStyle(StageStyle.UNDECORATED);
        this.initModality(Modality.WINDOW_MODAL);
        //this.initOwner(Kettlelog.setup);
        this.setScene(new Scene(infoborderpane, infowidth, infoheight));
        this.show();
    }

    public class InfoHandler implements EventHandler<ActionEvent>{
        @Override
        public void handle(ActionEvent e) {

            String itemClicked = ((Control)e.getSource()).getId();

            switch(itemClicked){
                case "infocancel":
                    InfoStage.this.hide();
                    break;    
                default:
                    System.out.println("Otherstuff");
            }
        }
    }

}
