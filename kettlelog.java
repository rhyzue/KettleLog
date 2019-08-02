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

    private static Stage setup;

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

    public static Region opaqueLayer = new Region();
    public InfoStage infoStage = new InfoStage();
    public AddStage addStage = new AddStage();

    public static void main(String[] args) {
        launch(args);
    }
 
    @Override
    public void start(Stage setup) {

        this.setup = setup;
        opaqueLayer.setStyle("-fx-background-color: #001a34;");
        opaqueLayer.setOpacity(0.7);
        opaqueLayer.setVisible(false);
        
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

        //array of table
        TableColumn<Item, String>[] colarray = (TableColumn<Item, String>[])new TableColumn[titles.length];

        for(int i=0; i<titles.length; i++)
        {
            TableColumn<Item, String> column = new TableColumn<Item, String>(titles[i]);
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

        VBox tableBox = new VBox(table);

        //================================================================================
        // MENU BAR
        //================================================================================

        MenuBar kettlemenu = new MenuBar();
      
        //FILE SUBMENU
        Menu file = new Menu("File");
            MenuItem newtable = new Menu("New");
            MenuItem opentable = new Menu("Open");
            MenuItem printtable = new Menu("Print");
            MenuItem exit = new Menu("Exit");

        //EDIT SUBMENU
        Menu edit = new Menu("Edit");
            MenuItem add = new Menu("Add Item");
            MenuItem remove = new Menu("Remove Item");

        //VIEW SUBMENU
        Menu view = new Menu("View");
            MenuItem fs = new Menu("Fullscreen");
            MenuItem mi = new Menu("Minimal Interface");

        //INFO SUBMENU
        Menu info = new Menu("Info");
            MenuItem about = new Menu("About");
            MenuItem tutorial = new Menu("Tutorial");
            MenuItem credits = new Menu("Credits");
        
        //ADDING MENUITEMS TO THEIR RESPECTIVE MENUS
        file.getItems().addAll(newtable, opentable, printtable, exit);
        edit.getItems().addAll(add, remove);
        view.getItems().addAll(fs, mi);
        info.getItems().addAll(about, tutorial, credits);
        kettlemenu.getMenus().addAll(file, edit, view, info);
 

        //================================================================================
        // TOP BAR (includes add/remove, filter, search)
        //================================================================================

        AnchorPane topBar = new AnchorPane();

        //ADD BUTTON
        Button addBtn = new Button();
        addBtn.setText("ADD");
        addBtn.setId("addBtn");

        //REMOVE BUTTON
        //Button removeBtn = new Button();
        removeBtn = new Button();
        removeBtn.setText("REMOVE");
        removeBtn.setId("removeBtn"); 
        removeBtn.setDisable(true);

        //================================================================================
        // ADD AND REMOVE BUTTON FUNCTIONALITY
        //================================================================================
        Handler eventHandler = new Handler();
        addBtn.setOnAction(eventHandler);
        removeBtn.setOnAction(eventHandler);
        
        //POSITIONS OF ADD AND REMOVE
        AnchorPane.setRightAnchor(addBtn, 135.0);
        AnchorPane.setBottomAnchor(addBtn, spacefromtable);
        AnchorPane.setRightAnchor(removeBtn, 55.0);
        AnchorPane.setBottomAnchor(removeBtn, spacefromtable);
       
        //SEARCH BAR
        searchbar.setPrefWidth(w / 2.56);
        searchbar.setPromptText("Search");
        AnchorPane.setLeftAnchor(searchbar, 305.0); 
        AnchorPane.setBottomAnchor(searchbar, spacefromtable);

        FilteredList<Columns> filteredData = new FilteredList<>(data, p -> true);

        searchbar.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(Columns -> {
                // If there is nothing in the search bar, we want to display all the items.
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                // lowercasesearch is referring to what the user is searching in the search bar
                String lowercasesearch = newValue.toLowerCase();
                if (String.valueOf(Columns.getName()).toLowerCase().contains(lowercasesearch)) {
                    return true;
                } else {
                    return false;
                }
            });
            SortedList<Columns> sortedData = new SortedList<>(filteredData);
            sortedData.comparatorProperty().bind(table.comparatorProperty());
            /*CellGenerator cellFactory = new CellGenerator();    
            columns[0].setCellFactory(cellFactory);*/    
            table.setItems(sortedData);
        });
        
        //FILTER COMBOBOX
        ObservableList<String> filterOptions = FXCollections.observableArrayList("Starred", "Most Recent", "Oldest Added", "None");
        ComboBox<String> filter= new ComboBox<String>(filterOptions);
            filter.setPromptText("Filter By");
            filter.setPrefWidth(150.0);

            FilterHandler filterListener = new FilterHandler();
            filter.valueProperty().addListener(filterListener);

        AnchorPane.setLeftAnchor(filter, 52.0);
        AnchorPane.setBottomAnchor(filter, spacefromtable);

        //KETTLELOG LOGO
        Image kettleimage = new Image("./Misc/Logo.png");
        ImageView logo = new ImageView();
        logo.setFitHeight(100);
        logo.setFitWidth(200);
        logo.setImage(kettleimage);

        AnchorPane.setLeftAnchor(logo, 50.0);
        AnchorPane.setTopAnchor(logo, 10.0);

        //TOPBAR GENERAL INFORMATION 
        topBar.setStyle("-fx-background-color: #004080;");
        topBar.setPrefSize(100, 150);
        topBar.getChildren().addAll(logo, addBtn, removeBtn, searchbar, filter);
        
        //================================================================================
        // FINALIZATION
        //================================================================================

        BorderPane main = new BorderPane();
        main.setStyle("-fx-background-color: #004080;");
        BorderPane.setMargin(tableBox, new Insets(10, 50, 0, 50));
        main.setCenter(tableBox);
        main.setTop(topBar);

        base.setTop(kettlemenu);
        base.setCenter(main);

        StackPane root = new StackPane();
        root.getChildren().addAll(base, opaqueLayer);

        //SHOW SCENE (13 inch laptops are 1280 by 800)

        setup.setScene(new Scene(root, w, h));
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();

        screenX = (screenBounds.getWidth() - w) / 2;
        screenY = (screenBounds.getHeight() - h) / 2;

        setup.setResizable(false);
        setup.setTitle("KettleLog");
        setup.setX(screenX); 
        setup.setY(screenY);
        setup.show();


    }
   

    public void showInfoStage(Item rowInfo){
        opaqueLayer.setVisible(true); 
        Bounds sb = base.localToScreen(base.getBoundsInLocal());
        xBounds = sb.getMinX();
        yBounds = sb.getMinY();

        //Here we pass in the row's information to updateinfostage.
        infoStage.initOwner(Kettlelog.primary);
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