import Item.*;
import javafx.stage.*;
import javafx.event.*;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.text.*;
import javafx.scene.chart.*; 
import javafx.beans.value.*;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.scene.control.ScrollPane.*;

public class InfoStage extends Stage{

    //================================================================================
    // INITIALIZATION
    //================================================================================
    private String infostripcolour;
    private String infomidcolour;
    private static double infowidth = 500;
    private static double infoheight = 700;
    private static double scrollheight = 1000;
    private static double distancedown = 40.0;
    private static String infostriphex = "#004545;";
    private static String infomidhex = "#b8d6d6;";
    private static Separator line1 = new Separator();
    private static Separator line2 = new Separator();
    private static Separator line3 = new Separator();
    private static Separator line4 = new Separator();
    private static Text infotitle = new Text();
    private static Text dateadded = new Text();
    private static Text adc = new Text();
    private static Text erp = new Text();
    private static Text erd = new Text();
    private static Text graphtext = new Text(); //consumption graph
    private static Label infolabel = new Label(); //item name 
    private static Label datelabel = new Label(); //date added
    private static Label adclabel = new Label(); //average daily consumption 
    private static Label erplabel = new Label(); //estimated reorder point
    private static Label erdlabel = new Label(); //estimated reorder date
    private static TextArea infodesc = new TextArea();
    private static Button infocancel = new Button();
    private static AnchorPane infotstrip = new AnchorPane();
    private static AnchorPane infocenter = new AnchorPane();
    private static AnchorPane infobstrip = new AnchorPane();
    private static BorderPane infoborderpane = new BorderPane();
    private static VBox infobox = new VBox();
    private static Kettlelog kettle = new Kettlelog();
    
    //Constructor
    InfoStage(){

        //Strip colours
        infostripcolour = String.format("-fx-background-color: %s", infostriphex);
        infomidcolour = String.format("-fx-background-color: %s", infomidhex);

        //Top strip of info panel which has text saying "Item Information"  
        AnchorPane.setLeftAnchor(infotitle, 50.0);
        AnchorPane.setBottomAnchor(infotitle, 5.0);    
        infotitle.setText("Item Information");
        infotitle.setFont(new Font(18));
        infotitle.setFill(Color.WHITE);
        infotstrip.setStyle(infostripcolour);
        infotstrip.setPrefSize(infowidth, 50); //(width, height)       
        infotstrip.getChildren().addAll(infotitle);

        //Item Name Label
        AnchorPane.setTopAnchor(infolabel, 25.0);
        AnchorPane.setLeftAnchor(infolabel, 100.0);
        infolabel.setFont(new Font(16));
        infolabel.setPrefHeight(50.0);
        infolabel.setPrefWidth(300.0);
        infolabel.setFont(new Font(16));
        infolabel.setAlignment(Pos.CENTER);
        infolabel.setTextOverrun(OverrunStyle.ELLIPSIS);
        infolabel.setStyle("-fx-background-color: #709c9c");

        //Date Added Text 
        AnchorPane.setLeftAnchor(dateadded, 25.0);
        AnchorPane.setTopAnchor(dateadded, 102.0);
        dateadded.setText("Date Added");
        dateadded.setFont(new Font(16));

        //Date Added Label
        AnchorPane.setRightAnchor(datelabel, 25.0);
        AnchorPane.setTopAnchor(datelabel, 100.0);
        datelabel.setPrefHeight(25.0);
        datelabel.setPrefWidth(125.0);
        datelabel.setFont(new Font(16));
        datelabel.setAlignment(Pos.CENTER);
        datelabel.setStyle("-fx-background-color: #95bfbf");

        //First Separator
        AnchorPane.setTopAnchor(line1, 110.0);
        AnchorPane.setRightAnchor(line1, 160.0);
        line1.setPrefWidth(220.0);

        //Average Daily Consumption Text
        AnchorPane.setLeftAnchor(adc, 25.0);
        AnchorPane.setTopAnchor(adc, 102.0 + distancedown); 
        adc.setText("Average Daily Consumption");
        adc.setFont(new Font(16));

        //Average Daily Consumption Label
        AnchorPane.setRightAnchor(adclabel, 25.0);
        AnchorPane.setTopAnchor(adclabel, 100.0 + distancedown);
        adclabel.setFont(new Font(14));
        adclabel.setPrefHeight(25.0);
        adclabel.setPrefWidth(125.0);
        adclabel.setAlignment(Pos.CENTER);
        adclabel.setStyle("-fx-background-color: #95bfbf");

        //Second Separator
        AnchorPane.setTopAnchor(line2, 110.0 + distancedown);
        AnchorPane.setRightAnchor(line2, 160.0);
        line2.setPrefWidth(100.0);

        //Estimated Reorder Point Text
        AnchorPane.setLeftAnchor(erp, 25.0);
        AnchorPane.setTopAnchor(erp, 102.0 + (distancedown * 2)); 
        erp.setText("Estimated Reorder Point");
        erp.setFont(new Font(16));

        //Estimated Reorder Point Label
        AnchorPane.setRightAnchor(erplabel, 25.0);
        AnchorPane.setTopAnchor(erplabel, 100.0 + (distancedown * 2));
        erplabel.setFont(new Font(14));
        erplabel.setPrefHeight(25.0);
        erplabel.setPrefWidth(125.0);
        erplabel.setAlignment(Pos.CENTER);
        erplabel.setStyle("-fx-background-color: #95bfbf");

        //Third Separator
        AnchorPane.setTopAnchor(line3, 110.0 + (distancedown * 2));
        AnchorPane.setRightAnchor(line3, 160.0);
        line3.setPrefWidth(125.0);

        //Estimated Reorder Date Text
        AnchorPane.setLeftAnchor(erd, 25.0);
        AnchorPane.setTopAnchor(erd, 102.0 + (distancedown * 3)); 
        erd.setText("Estimated Reorder Date");
        erd.setFont(new Font(16));

        //Estimated Reorder Date Label
        AnchorPane.setRightAnchor(erdlabel, 25.0);
        AnchorPane.setTopAnchor(erdlabel, 100.0 + (distancedown * 3));
        erdlabel.setFont(new Font(14));
        erdlabel.setPrefHeight(25.0);
        erdlabel.setPrefWidth(125.0);
        erdlabel.setAlignment(Pos.CENTER);
        erdlabel.setStyle("-fx-background-color: #95bfbf");

        //Third Separator
        AnchorPane.setTopAnchor(line4, 110.0 + (distancedown * 3));
        AnchorPane.setRightAnchor(line4, 160.0);
        line4.setPrefWidth(130.0);

        //Uneditable Description Box
        AnchorPane.setLeftAnchor(infodesc, 25.0);
        AnchorPane.setTopAnchor(infodesc, 100.0 + (distancedown * 4) + 10.0);
        infodesc.setPrefWidth(450.0);
        infodesc.setPrefHeight(125.0);
        infodesc.setEditable(false);
        infodesc.setStyle("-fx-opacity: 1;");
        infodesc.setWrapText(true);

        //Consumption Graph Text
        AnchorPane.setLeftAnchor(graphtext, 25.0);
        AnchorPane.setTopAnchor(graphtext, 95.0 + (distancedown * 8)); 
        graphtext.setText("Consumption Graph");
        graphtext.setFont(new Font(16));

        //================================================================================
        // CONSUMPTION GRAPH
        //================================================================================

        NumberAxis xAxis = new NumberAxis();
        xAxis.setLabel("Date");

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Quantity");

        LineChart linechart = new LineChart(xAxis, yAxis);
        XYChart.Series dataSeries1 = new XYChart.Series();

        dataSeries1.getData().add(new XYChart.Data( 1, 567));
        dataSeries1.getData().add(new XYChart.Data( 5, 612));
        dataSeries1.getData().add(new XYChart.Data(10, 800));
        dataSeries1.getData().add(new XYChart.Data(20, 780));
        dataSeries1.getData().add(new XYChart.Data(40, 810));
        dataSeries1.getData().add(new XYChart.Data(80, 850));

        linechart.getData().add(dataSeries1);
        linechart.setLegendVisible(false);

        //Adding the graph to the anchorpane.
        AnchorPane.setLeftAnchor(linechart, 20.0);
        AnchorPane.setTopAnchor(linechart, 80.0 + (distancedown * 9));
        linechart.setPrefWidth(450.0);
        linechart.setPrefHeight(350.0);

        //SETTING THE ANCHORPANE
        infocenter.setStyle(infomidcolour);     
        infocenter.getChildren().addAll(infolabel, dateadded, datelabel, line1, adc, adclabel);
        infocenter.getChildren().addAll(line2, erp, erplabel, line3, erd, erdlabel, line4, infodesc);
        infocenter.getChildren().addAll(graphtext, linechart);

        //SCROLLPANE CONFIGURATION
        infobox.getChildren().add(infocenter);
        ScrollPane scrollpane = new ScrollPane(infobox);
        scrollpane.setContent(infobox);
        scrollpane.setPrefHeight(scrollheight);
        scrollpane.setFitToWidth(true);
        scrollpane.setVbarPolicy(ScrollBarPolicy.NEVER);

        //CANCEL BUTTON 
        infocancel.setText("Close");
        infocancel.setPrefHeight(27.5);
        infocancel.setId("infocancel");
        InfoHandler infoHandler = new InfoHandler();
        infocancel.setOnAction(infoHandler); 
        
        AnchorPane.setRightAnchor(infocancel, 5.0);
        AnchorPane.setTopAnchor(infocancel, 5.0);
        infobstrip.setStyle(infostripcolour);
        infobstrip.setPrefSize(infowidth, 37.5);
        infobstrip.getChildren().addAll(infocancel);  
    
        //Setting the elements of the borderpane.
        infoborderpane.setTop(infotstrip);
        infoborderpane.setCenter(scrollpane);
        infoborderpane.setBottom(infobstrip);

        this.setResizable(false);
        this.initOwner(kettle.getPrimaryStage());
        this.initStyle(StageStyle.UNDECORATED);
        this.initModality(Modality.WINDOW_MODAL);
        this.setScene(new Scene(infoborderpane, infowidth, infoheight));
    }

    public void updateInfoStage(Item rowinfo){

        infolabel.setText(rowinfo.getName());
        datelabel.setText(rowinfo.getDateAdded());

        if (rowinfo.getDesc().trim().length() <= 0) {
            infodesc.setText("There is no description for this item.");
        } else {
            infodesc.setText("Item Description: " + rowinfo.getDesc());
        }

    }

    public class InfoHandler implements EventHandler<ActionEvent>{
        @Override
        public void handle(ActionEvent e) {

            String itemClicked = ((Control)e.getSource()).getId();

            switch(itemClicked){
                case "infocancel":
                    kettle.hideInfoStage();
                    break;    
                default:
                    System.out.println("Otherstuff");
            }
        }
    }

}