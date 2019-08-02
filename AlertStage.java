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

    private static double screenX = 0.0;
    private static double screenY = 0.0;

    private double xBounds = 0;
    private double yBounds = 0;
    private double w = 0;
    private double h = 0;

    private static Kettlelog kettle = new Kettlelog();

    //set opaquelayer to visible, alert.showandwait() in kettlelog.java
    AlertStage(){

        stripcolour = String.format("-fx-background-color: %s", striphex);
        alertmidcolour = String.format("-fx-background-color: %s", alertmidhex);

        //Ensures that alert popup is centered relatively to its parent stage (setup).
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

        //Top part of the pane which says "Confirm Deletion.""
        Text deltext = new Text();
            deltext.setText("Confirm Deletion");
            deltext.setFont(new Font(18));
            deltext.setFill(Color.WHITE);

        AnchorPane alerttstrip = new AnchorPane();
            AnchorPane.setLeftAnchor(deltext, 30.0);
            AnchorPane.setBottomAnchor(deltext, 10.0);
            alerttstrip.setStyle(stripcolour);
            alerttstrip.setPrefSize(alertwidth, 75); //(width, height)
            alerttstrip.getChildren().addAll(deltext);

        //Center part of the pane which contains the Kettlelog logo and some text labels.

        Text delconfirm = new Text();
            delconfirm.setText("Are you sure you want to delete");
            delconfirm.setFont(new Font(16));

        Image kettleonlyimage = new Image("./Misc/kettle.png");
        ImageView kettle = new ImageView();
            kettle.setFitHeight(150);
            kettle.setFitWidth(150);
            kettle.setImage(kettleonlyimage);

        Label itemlabel = new Label();
            itemlabel.setFont(new Font(16));
            itemlabel.setPrefHeight(50.0);
            itemlabel.setPrefWidth(280.0);
            itemlabel.setAlignment(Pos.CENTER);
            itemlabel.setTextOverrun(OverrunStyle.ELLIPSIS);
            itemlabel.setStyle("-fx-background-color: #cbadc3;");

        Text delperm = new Text();
            delperm.setFont(new Font(14));

        Text delundo = new Text();
            delundo.setText("You can't undo this action.");
            delundo.setFont(new Font(14));

        VBox alertcentervbox = new VBox(10);
            alertcentervbox.setPrefSize(220.0, 230.0);
            alertcentervbox.getChildren().addAll(delconfirm, itemlabel, delperm, delundo);
            alertcentervbox.setPadding(new Insets(50.0, 0.0, 0.0, 0.0));
            //alertcentervbox.setStyle("-fx-background-color: #cf1020;");
 
        AnchorPane alertcenter = new AnchorPane();
            AnchorPane.setTopAnchor(alertcentervbox, 0.0);
            AnchorPane.setRightAnchor(alertcentervbox, 30.0);
            AnchorPane.setLeftAnchor(kettle, 10.0);
            AnchorPane.setTopAnchor(kettle, 40.0);

            alertcenter.setStyle(alertmidcolour);
            alertcenter.getChildren().addAll(kettle, alertcentervbox);

        //Bottom part of the pane which has the two buttons "Cancel" and "Delete".  
        Button alertcancel = new Button();
            alertcancel.setText("Cancel");
            alertcancel.setPrefHeight(30);
            alertcancel.setId("alertcancel");

            AlertHandler alertHandler = new AlertHandler(); //declare object for handler class
            alertcancel.setOnAction(alertHandler); 

        Button alertdelete = new Button();
            alertdelete.setText("Delete Item");
            alertdelete.setPrefHeight(30);
            alertdelete.setId("alertdelete");
            alertdelete.setOnAction(alertHandler); 

        HBox alertbbx = new HBox(15);
            alertbbx.getChildren().addAll(alertcancel, alertdelete);

        AnchorPane alertbstrip = new AnchorPane();
            AnchorPane.setRightAnchor(alertbbx, 7.5);
            AnchorPane.setTopAnchor(alertbbx, 7.5);
            alertbstrip.setStyle(stripcolour);
            alertbstrip.setPrefSize(alertwidth, 50); //(width, height)
            alertbstrip.getChildren().addAll(alertbbx);

        BorderPane alertpane = new BorderPane();
            alertpane.setTop(alerttstrip);
            alertpane.setCenter(alertcenter);
            alertpane.setBottom(alertbstrip);

        this.setResizable(false);
        this.setScene(new Scene(alertpane, alertwidth, alertheight));

        this.initStyle(StageStyle.UNDECORATED);
        this.initModality(Modality.WINDOW_MODAL);

    }

    public void updateAlertStage(double xB, double yB, double wi, double hi, ObservableList<Columns> itemsToDelete){
        xBounds = xB;
        yBounds = yB;
        w = wi;
        h = hi;

        screenX = (xBounds + w / 2 - alertwidth / 2); 
        screenY = (yBounds + h / 2 - alertheight / 2);

        if(itemsToDelete.size()==1){
            itemlabel.setText(itemsToDelete.get(0).getName() + "?");
            delperm.setText("This item will be deleted permanently.");
        }
        else{
            itemlabel.setText("the selected items?");
            delperm.setText("The items will be deleted permanently.");
        }

    }

    public class AlertHandler implements EventHandler<ActionEvent>{
        @Override
        public void handle(ActionEvent e) {

            String itemClicked = ((Control)e.getSource()).getId();

            switch(itemClicked){
                case "alertcancel":
                    kettle.closeAlert();
                    /*
                    searchbar.clear();
                    alert.hide();
                    opaqueLayer.setVisible(false);
                    itemsToDelete.clear();
                    */
                    break;  

                case "alertdelete":
                    kettle.deleteItem();
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
