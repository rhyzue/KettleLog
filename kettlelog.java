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
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.TableColumn.CellEditEvent;

public class Kettlelog extends Application {
    //================================================================================
    // GLOBAL VARIABLES
    //================================================================================
    @SuppressWarnings("unchecked")

    private static ObservableList<Item> data = FXCollections.observableArrayList();
    private ObservableList<Item> itemsToDelete;
    private static Item empty = new Item("", "", "", "", "", "", false, false, "", "");

    public static boolean starred = false;
    public static int expanded = 0;

    public static int presscount = 0; 
    public static boolean duplicatefound = false;

    public static int filterSel = 0; //1=starred,2=checked, 3=mostrecent, 4=none

    private static double xBounds = 0.0;
    private static double yBounds = 0.0;
    double screenX = 0.0;
    double screenY = 0.0;

    double w_to_h = 1.4;
    double w = 1024;
    double h = w / w_to_h;
    double spacefromtable = 7.5;

    double extraheight = 5.0;

    private static PrimaryStage primaryStage = new PrimaryStage();

    //public InfoStage infoStage = new InfoStage();
    private static AddStage addStage = new AddStage();

    //FilteredList<Item> filteredData = new FilteredList<>(data, p -> true);

    public static void main(String[] args) {
        launch(args);
    }
 
    @Override
    public void start(Stage setup) {
        
        data.add(empty);
        itemsToDelete = FXCollections.observableArrayList(empty);
        primaryStage.show();
        primaryStage.updatePrimaryStage(data);
    
    }

    public void showAddStage(int popuptype, String[] textarray){//int popuptype, String[]textarray, Item rowinfo){

        primaryStage.showOpaqueLayer();
        addStage.updateAddStage(popuptype, textarray);

        addStage.setX(primaryStage.getX() + primaryStage.getWidth() / 2 - 300);
        addStage.setY((primaryStage.getY() + primaryStage.getHeight() / 2 - 352.94) + extraheight);

        addStage.show();
    }

    public PrimaryStage getPrimaryStage(){
        return primaryStage;
    }


    //takes in one item from AddStage and adds it to our data.
    public void setData(ObservableList<Item> items, int changetype){
        //0 refers to adding data
        //1 refers to DELETING data

        if (changetype == 0){
            data.remove(empty);
            data.add(items.get(0)); //ITEMS WILL ONLY HAVE 1 ITEM, THE ONE THAT WE ARE ADDING.
        }

        //System.out.println(data.size());
        primaryStage.updatePrimaryStage(data);

    }
   
/*
    public void showInfoStage(Item rowInfo){
        opaqueLayer.setVisible(true); 
        Bounds sb = base.localToScreen(base.getBoundsInLocal());
        xBounds = sb.getMinX();
        yBounds = sb.getMinY();

        //Here we pass in the row's information to updateinfostage.
        infoStage.initOwner(primaryStage);
        infoStage.updateInfoStage(xBounds, yBounds, w, h, rowInfo);
        infoStage.show();
    }

    public void hideInfoStage(){
        opaqueLayer.setVisible(false);
        infoStage.hide();
    }*/


    public void hideAddStage(){
        primaryStage.hideOpaqueLayer();
        addStage.hide();
    } 

}