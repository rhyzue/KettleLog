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
    ObservableList<Item> itemsToDelete;

    String striphex = "#610031;";
    String alertmidhex = "#dfccd5;";
    String stripcolour = String.format("-fx-background-color: %s", striphex);
    String alertmidcolour = String.format("-fx-background-color: %s", alertmidhex);

    double alertwidth = 500.0;
    double alertw_to_h = 1.42857;
    double alertheight = alertwidth / alertw_to_h;

    Bounds sb = base.localToScreen(base.getBoundsInLocal());

    Text deltext = new Text();

    AnchorPane alerttstrip = new AnchorPane();
    Text delconfirm = new Text();
    Image kettleonlyimage = new Image("./Misc/kettle.png");
    ImageView kettle = new ImageView();
    Label itemlabel = new Label();

    Text delundo = new Text();
    Text delperm = new Text();
    VBox alertcentervbox = new VBox(10);
    AnchorPane alertcenter = new AnchorPane();
    Button alertcancel = new Button();

    Button alertdelete = new Button();

    AnchorPane alertbstrip = new AnchorPane();
    Box alertbbx = new HBox(15);
    BorderPane alertpane = new BorderPane();



    AlertStage(ObservableList itemsToDelete){
        this.itemsToDelete = itemsToDelete;
        opaqueLayer.setVisible(true);

        screenX = (sb.getMinX() + w / 2 - alertwidth / 2); 
        screenY = (sb.getMinY() + h / 2 - alertheight / 2);


        //Ensures that alert popup is centered relatively to its parent stage (setup).
        ChangeListener<Number> widthListener = (observable, oldValue, newValue) -> {
            alert.setX(screenX);
        };
        ChangeListener<Number> heightListener = (observable, oldValue, newValue) -> {
            alert.setY(screenY);
        };



        this.widthProperty().addListener(widthListener);
        this.heightProperty().addListener(heightListener);

        this.setOnShown(shown -> {
            this.widthProperty().removeListener(widthListener);
            this.heightProperty().removeListener(heightListener);
        });



        this.initStyle(StageStyle.UNDECORATED);
        this.initModality(Modality.WINDOW_MODAL);
        this.initOwner(Kettlelog.setup);

        //Top part of the pane which says "Confirm Deletion.""

        deltext.setText("Confirm Deletion");
        deltext.setFont(new Font(18));
        deltext.setFill(Color.WHITE);

        AnchorPane.setLeftAnchor(deltext, 30.0);
        AnchorPane.setBottomAnchor(deltext, 10.0);
        alerttstrip.setStyle(stripcolour);
        alerttstrip.setPrefSize(alertwidth, 75); //(width, height)
        alerttstrip.getChildren().addAll(deltext);


        delconfirm.setText("Are you sure you want to delete");
        delconfirm.setFont(new Font(16));

    
        kettle.setFitHeight(150);
        kettle.setFitWidth(150);
        kettle.setImage(kettleonlyimage);


        if(itemsToDelete.size()==1){
            itemlabel.setText(itemsToDelete.get(0).getName() + "?");
        }
        else{
            itemlabel.setText("the selected items?");
        }
        itemlabel.setFont(new Font(16));
        itemlabel.setPrefHeight(50.0);
        itemlabel.setPrefWidth(280.0);
        itemlabel.setAlignment(Pos.CENTER);
        itemlabel.setTextOverrun(OverrunStyle.ELLIPSIS);
        itemlabel.setStyle("-fx-background-color: #cbadc3;");

        if(itemsToDelete.size()==1){
            delperm.setText("This item will be deleted permanently.");
        }
        else{
            delperm.setText("The items will be deleted permanently.");
        }
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

        alertcancel.setText("Cancel");
            alertcancel.setPrefHeight(30);

            alertcancel.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                    public void handle(ActionEvent event) {
                        searchbar.clear();
                        alert.hide();
                        opaqueLayer.setVisible(false);
                        itemsToDelete.clear();
                    }
                }); 

        alertdelete.setText("Delete Item");
        alertdelete.setPrefHeight(30);

        alertdelete.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                    public void handle(ActionEvent event) {
                        //System.out.println("Old size: "+ data.size());
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
                        opaqueLayer.setVisible(false);
                    }
                }); 


        alertbbx.getChildren().addAll(alertcancel, alertdelete);


        AnchorPane.setRightAnchor(alertbbx, 7.5);
        AnchorPane.setTopAnchor(alertbbx, 7.5);
        alertbstrip.setStyle(stripcolour);
        alertbstrip.setPrefSize(alertwidth, 50); //(width, height)
        alertbstrip.getChildren().addAll(alertbbx);

    
        alertpane.setTop(alerttstrip);
        alertpane.setCenter(alertcenter);
        alertpane.setBottom(alertbstrip);

        alert.setResizable(false);
        alert.setScene(new Scene(alertpane, alertwidth, alertheight));
        alert.showAndWait();
    }
}

