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


public class AlertStage extends Stage{

    private final String striphex = "#610031;";
    private final String alertmidhex = "#dfccd5;";
    private final double alertwidth = 500.0;
    private final double alertw_to_h = 1.42857;
    private final double alertheight = alertwidth / alertw_to_h;
    private static String stripcolour; 
    private static String alertmidcolour; 

    private static Text deltext = new Text();
    private static Image kettleonlyimage = new Image("./Misc/kettle.png");
    private static ImageView kettle = new ImageView();
    private static Text delconfirm = new Text();
    private static AnchorPane alerttstrip = new AnchorPane();
    private static VBox alertcentervbox = new VBox(10);
    private static Text delundo = new Text();
    private static Text delperm = new Text();
    private static Label itemlabel = new Label();
    private static AnchorPane alertcenter = new AnchorPane();
    private static Button alertcancel = new Button();
    private static Button alertdelete = new Button();
    private static HBox alertbbx = new HBox(15);
    private static AnchorPane alertbstrip = new AnchorPane();
    private static BorderPane alertpane = new BorderPane();

    private static Kettlelog kettleclass = new Kettlelog();

    AlertStage(){

        stripcolour = String.format("-fx-background-color: %s", striphex);
        alertmidcolour = String.format("-fx-background-color: %s", alertmidhex);

        //Top part of the pane which says "Confirm Deletion.""
        
        deltext.setText("Confirm Deletion");
        deltext.setFont(new Font(18));
        deltext.setFill(Color.WHITE);

        AnchorPane.setLeftAnchor(deltext, 30.0);
        AnchorPane.setBottomAnchor(deltext, 10.0);
        alerttstrip.setStyle(stripcolour);
        alerttstrip.setPrefSize(alertwidth, 75); //(width, height)
        alerttstrip.getChildren().addAll(deltext);

        //Center part of the pane which contains the Kettlelog logo and some text labels.

        delconfirm.setText("Are you sure you want to delete");
        delconfirm.setFont(new Font(16));

        kettle.setFitHeight(150);
        kettle.setFitWidth(150);
        kettle.setImage(kettleonlyimage);

        itemlabel.setFont(new Font(16));
        itemlabel.setPrefHeight(50.0);
        itemlabel.setPrefWidth(280.0);
        itemlabel.setAlignment(Pos.CENTER);
        itemlabel.setTextOverrun(OverrunStyle.ELLIPSIS);
        itemlabel.setStyle("-fx-background-color: #cbadc3;");

        delperm.setFont(new Font(14));

        delundo.setText("You can't undo this action.");
        delundo.setFont(new Font(14));

        alertcentervbox.setPrefSize(220.0, 230.0);
        alertcentervbox.getChildren().addAll(delconfirm, itemlabel, delperm, delundo);
        alertcentervbox.setPadding(new Insets(50.0, 0.0, 0.0, 0.0));
        //alertcentervbox.setStyle("-fx-background-color: #cf1020;");

        AnchorPane.setTopAnchor(alertcentervbox, 0.0);
        AnchorPane.setRightAnchor(alertcentervbox, 30.0);
        AnchorPane.setLeftAnchor(kettle, 10.0);
        AnchorPane.setTopAnchor(kettle, 40.0);

        alertcenter.setStyle(alertmidcolour);
        alertcenter.getChildren().addAll(kettle, alertcentervbox);

        //Bottom part of the pane which has the two buttons "Cancel" and "Delete". 

        alertcancel.setText("Cancel");
        alertcancel.setPrefHeight(30);
        alertcancel.setId("alertcancel");

        AlertHandler alertHandler = new AlertHandler(); //declare object for handler class
        alertcancel.setOnAction(alertHandler); 

        alertdelete.setText("Delete Item");
        alertdelete.setPrefHeight(30);
        alertdelete.setId("alertdelete");
        alertdelete.setOnAction(alertHandler); 
        alertbbx.getChildren().addAll(alertcancel, alertdelete);

        AnchorPane.setRightAnchor(alertbbx, 7.5);
        AnchorPane.setTopAnchor(alertbbx, 7.5);
        alertbstrip.setStyle(stripcolour);
        alertbstrip.setPrefSize(alertwidth, 50); //(width, height)
        alertbstrip.getChildren().addAll(alertbbx);

        alertpane.setTop(alerttstrip);
        alertpane.setCenter(alertcenter);
        alertpane.setBottom(alertbstrip);

        this.setResizable(false);
        this.setScene(new Scene(alertpane, alertwidth, alertheight));
        this.initOwner(kettleclass.getPrimaryStage());
        this.initStyle(StageStyle.UNDECORATED);
        this.initModality(Modality.WINDOW_MODAL);

    }

    public void updateAlertStage(Item rowinfo){//itemstodelete should be a parameter too potentially

        itemlabel.setText(rowinfo.getName() + "?");
        delperm.setText("This item will be deleted permanently.");

        /*
        if(itemsToDelete.size()==1){
            itemlabel.setText(itemsToDelete.get(0).getName() + "?");
            delperm.setText("This item will be deleted permanently.");
        }
        else{
            itemlabel.setText("the selected items?");
            delperm.setText("The items will be deleted permanently.");
        }*/

    }

    public class AlertHandler implements EventHandler<ActionEvent>{
        @Override
        public void handle(ActionEvent e) {

            String itemClicked = ((Control)e.getSource()).getId();

            switch(itemClicked){
                case "alertcancel":
                    kettleclass.hideAlertStage();
                    /*
                    searchbar.clear();
                    alert.hide();
                    opaqueLayer.setVisible(false);
                    itemsToDelete.clear();
                    */
                    break;  

                case "alertdelete":
                    //kettle.deleteItem();
                    /*
                    for(int i = 0; i<itemsToDelete.size(); i++){ //cycle thru itemsToDelete list and remove from data 
                        data.remove(itemsToDelete.get(i));
                        //System.out.println("Deleted: " +itemsToDelete.get(i).getName());
                    }
                    itemsToDelete.clear(); //clear itemsToDelete list
                    removeBtn.setDisable(true);

                    //System.out.println("New size: "+ data.size());
                    for(int j = 0; j<data.size(); j++){//search items for checked property
                        if((data.get(j)).getChecked()==true){
                            removeBtn.setDisable(false);
                        }
                    }
                    searchbar.clear();
                    if(data.size()==0){
                        data.add(empty);
                    }
                    CellGenerator cellFactory = new CellGenerator();    
                    columns[0].setCellFactory(cellFactory);
                    table.setItems(data);

                    alert.hide();
                    opaqueLayer.setVisible(false); */
                    break;  
                default:
                    System.out.println("Otherstuff");
            }
        }
    }

}