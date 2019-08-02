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

    private ObservableList<Item> data; 
    private ObservableList<Item> itemsToDelete;
    private TableView<Item> table;

    private String[] emptyinfo = {"", "", "", "", "", ""};
    private Item empty = new Item( "", "", "", "", "", "", "empty column", false, false, "", "");

    public static boolean starred = false;
    public static int expanded = 0;

    public static int presscount = 0; 
    public static boolean duplicatefound = false;

    public static int filterSel = 0; //1=starred,2=checked, 3=mostrecent, 4=none

    double xBounds = 0.0;
    double yBounds = 0.0;
    double screenX = 0.0;
    double screenY = 0.0;

    double w_to_h = 1.4;
    double w = 1024;
    double h = w / w_to_h;
    double spacefromtable = 7.5;

    public static Region opaqueLayer = new Region();
    //public InfoStage infoStage = new InfoStage();
    //public AddStage addStage = new AddStage();
    //public PrimaryStage primaryStage;

    public BorderPane base = new BorderPane();

    //FilteredList<Item> filteredData = new FilteredList<>(data, p -> true);

    public static void main(String[] args) {
        launch(args);
    }
 
    @Override
    public void start(Stage setup) {
    	setup = new PrimaryStage(base, opaqueLayer);

    	//make setup the primaryStage
    	//setup = primaryStage;        
        data = FXCollections.observableArrayList(empty);
        itemsToDelete = FXCollections.observableArrayList(empty);

        setup.show();
        

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
    }

    public void showAddStage(int popuptype, String[]textarray, Item rowinfo){
        opaqueLayer.setVisible(true); 
        Bounds sb = base.localToScreen(base.getBoundsInLocal());
        xBounds = sb.getMinX();
        yBounds = sb.getMinY();
        addStage.updateInfoStage(xBounds, yBounds, w, h, popuptype, textarray, table, data, rowInfo);
        addStage.initOwner(Kettlelog.primary);
        addStage.show();
    }

    public void hideAddStage(){
        opaqueLayer.setVisible(false);
        addStage.hide();
    } */
/*
    public class ColumnHandler implements ListChangeListener<TableColumn>{
        public boolean suspended;

        @Override
        public void onChanged(Change change) {
            change.next();
            if (change.wasReplaced() && !suspended) {
                this.suspended = true;
                table.getColumns().setAll(columns);
                this.suspended = false;
            }
        }
    }*/

}