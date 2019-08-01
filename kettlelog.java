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
    int filterSel = 0; //1=starred,2=checked, 3=mostrecent, 4=none
 
    @Override
    public void start(Stage primary) {

        data = FXCollections.observableArrayList(empty);
        itemsToDelete = FXCollections.observableArrayList(empty);
        table = new TableView<Item>();

        //Here is our leftmost column, the one with the anchorpane full of buttons.
        TableColumn<Item, Boolean> buttoncolumn = new TableColumn<>("");

        //ensuring that our cellfactory is only added for non-empty rows.
        buttoncolumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Item, Boolean>, ObservableValue<Boolean>>() {
              @Override public ObservableValue<Boolean> call(TableColumn.CellDataFeatures<Item, Boolean> features) {
                return new SimpleBooleanProperty(features.getValue() != null);
              }
          });

        buttoncolumn.setCellFactory(new Callback<TableColumn<Item, Boolean>, TableCell<Item, Boolean>>() {
              @Override public TableCell<Item, Boolean> call(TableColumn<Item, Boolean> itemBooleanTableColumn) {
                new AddButtonCell(table, data);
              }
          });
                
        //COLUMN TITLES
        String[] titles = {"Name","Status","Quantity","Minimum"};

        //array of tablecolumns
        TableColumn<Item, String>[] colarray = (TableColumn<Item, String>[])new TableColumn[titles.length];

        for(int i=0; i<titles.length; i++)
        {
            TableColumn<Item, String> column = new TableColumn<Columns, String>(titles[i]);
            column.setCellValueFactory(new PropertyValueFactory<>(titles[i].toLowerCase()));
            column.prefWidthProperty().bind(table.widthProperty().multiply(0.1977));
            column.setStyle( "-fx-alignment: CENTER-LEFT;");
            column.setResizable(false);
            colarray[i] = column;
        }

        table.getColumns().<Item, String>addAll(buttoncolumn, colarray);
        table.setFixedCellSize(40.0);
        table.setPrefSize(300, 508.0);
        table.setPlaceholder(new Label("Sorry, your search did not match any item names."));

        ColumnHandler columnChange = new ColumnHandler();
        table.getColumns().addListener(columnChange); 
        table.setItems(data);

        new PrimaryStage();
    }

    public void hideInfoStage(InfoStage infoStage){
        opaqueLayer.setVisible(false);
        infoStage.hide();
        infoStage = null;
    }

    public void showInfoStage(InfoStage infoStage){
        opaqueLayer.setVisible(true);       
        infoStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

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
    }

}