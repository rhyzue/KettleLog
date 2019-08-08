import Log.*;
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

public class PrimaryStage extends Stage{

    private static double screenX = 0.0;
    private static double screenY = 0.0;

    private static double w_to_h = 1.4;
    private static double w = 1024;
    private static double h = w / w_to_h;
    private static double spacefromtable = 7.5;

    //UI Items
	private static TextField searchbar = new TextField();
	private static MenuBar kettlemenu = new MenuBar();
    private static Region opaqueLayer = new Region();
    private static Button removeBtn = new Button();
    private static AnchorPane topBar = new AnchorPane();
    private ComboBox<String> filter;

    private static TableView<Item> table = new TableView<Item>();
    private static final String[] titles = {"Name","Status","Quantity","Minimum"};
    private static TableColumn<Item, String> buttoncolumn = new TableColumn<>("");
    private static ObservableList<Log> emptylist = FXCollections.observableArrayList();
    private static Item empty = new Item("emptyid", "", "", "", "", "", "", false, false, "", "", emptylist);
    private static ObservableList<String> filterOptions = FXCollections.observableArrayList("Starred", "Most Recent", "Oldest Added", "None");
    private static String[] emptyinfo = {"", "", "", "", "", ""};
    private static ObservableList<Item> itemsToDelete;

    private static TableColumn<Item, String>[] itemArray = (TableColumn<Item, String>[]) new TableColumn[titles.length];
    private static BorderPane base = new BorderPane();

    //Handler eventHandler = new Handler();
    Kettlelog kettle = new Kettlelog();
    private static FilterHandler filterListener = new FilterHandler();


	PrimaryStage(){ //TEMP: opaque should belong to PS. make set method to access from main
        //System.out.println("===============herePri");

	 	this.setResizable(false);
        this.setTitle("KettleLog");
        
        opaqueLayer.setStyle("-fx-background-color: #001a34;");
        opaqueLayer.setOpacity(0.7);
        opaqueLayer.setVisible(false); 

        ////////////////////////////////////////////////////////////////Menu Bar

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
        // TABLE
        //================================================================================

        table.setFixedCellSize(40.0);
        table.setPrefSize(300, 508.0);
        table.setPlaceholder(new Label("Sorry, your search did not match any item names."));

        buttoncolumn.prefWidthProperty().bind(table.widthProperty().multiply(0.1980));

        //COLUMN TITLES       
        for(int i=0; i<titles.length; i++)
        {
            TableColumn<Item, String> item = new TableColumn<Item, String>(titles[i]);
            item.setCellValueFactory(new PropertyValueFactory<>(titles[i].toLowerCase()));
            item.prefWidthProperty().bind(table.widthProperty().multiply(0.1980));
            item.setStyle( "-fx-alignment: CENTER-LEFT;");
            item.setResizable(false);
            itemArray[i] = item;
        }

        table.getColumns().<Item, String>addAll(buttoncolumn, itemArray[0], itemArray[1], itemArray[2], itemArray[3]);

        ColumnHandler columnChange = new ColumnHandler();
        table.getColumns().addListener(columnChange);  

        table.setOnMouseClicked((MouseEvent event) -> {
            if (event.getClickCount() >= 1) {
                System.out.println("PRINTING ROW INFORMATION BELOW.");
                //onSelection();
            }
        });

        VBox tableBox = new VBox(table);

        //================================================================================
        // TOP BAR (includes add/remove, filter, search)
        //================================================================================

        //ADD BUTTON
        Button addBtn = new Button();
        addBtn.setText("ADD");
        addBtn.setId("addBtn");

        //REMOVE BUTTON
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

        
        FilteredList<Item> filteredData = new FilteredList<>(kettle.getData(), p -> true);  

        //SEARCHBAR FUNCTIONALITY
        searchbar.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(Item -> {

                // If there is nothing in the search bar, we want to display all the items. 
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                // lowercasesearch is referring to what the user is searching in the search bar
                String lowercasesearch = newValue.toLowerCase();
                if (String.valueOf(Item.getName()).toLowerCase().contains(lowercasesearch)) {
                    return true;
                } else {
                    return false;
                }
            });

            SortedList<Item> sortedData = new SortedList<>(filteredData);
            sortedData.comparatorProperty().bind(table.comparatorProperty());   
            table.setItems(sortedData);

        }); 

  
        //FILTER COMBOBOX
        //ObservableList<String> filterOptions = FXCollections.observableArrayList("Starred", "Most Recent", "Oldest Added", "None");
        filter = new ComboBox<String>(filterOptions);
            filter.setPromptText("Filter By");
            filter.setPrefWidth(150.0);
    
        //FilterHandler filterListener = new FilterHandler();
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

        this.setScene(new Scene(root, w, h));

        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        screenX = (screenBounds.getWidth() - w) / 2;
        screenY = (screenBounds.getHeight() - h) / 2;

        this.setX(screenX); 
        this.setY(screenY);
    }

    public void showOpaqueLayer(){
        opaqueLayer.setVisible(true); 
    }

    public void hideOpaqueLayer(){
        opaqueLayer.setVisible(false);
    }

    public void updatePrimaryStage(ObservableList<Item> data){

        if(!data.get(0).getName().equals("")){ //if empty
            AddButtonCell cell = new AddButtonCell(); 
            buttoncolumn.setCellFactory(cell);
        }
        else{
            BlankCell blankCell = new BlankCell();
            buttoncolumn.setCellFactory(blankCell);
        }
        table.setItems(data);
    }

    public void enableRemoveBtn(){
        removeBtn.setDisable(false);
    }

    public void disableRemoveBtn(){
        removeBtn.setDisable(true);

    }

    public void clearSearchBar(){
        searchbar.clear();
    }

    public void resetComboBox(){
        //remove the combobox
        topBar.getChildren().remove(filter);

        //create a new one
        filter = new ComboBox<String>(filterOptions);
        filter.setPromptText("Filter By");
        filter.setPrefWidth(150.0);

        AnchorPane.setLeftAnchor(filter, 52.0);
        AnchorPane.setBottomAnchor(filter, spacefromtable);

        filter.valueProperty().addListener(filterListener);
        topBar.getChildren().add(filter);
    }

    public class ColumnHandler implements ListChangeListener<TableColumn>{
        public boolean suspended;

            @Override
            public void onChanged(Change change) {
                change.next();
                if (change.wasReplaced() && !suspended) {
                    this.suspended = true;
                    table.getColumns().setAll(buttoncolumn, itemArray[0], itemArray[1], itemArray[2], itemArray[3]);
                    this.suspended = false;
                }
            }
    }

    public class Handler implements EventHandler<ActionEvent>{
        @Override
        public void handle(ActionEvent e) {

            String itemClicked = ((Control)e.getSource()).getId();

            switch(itemClicked){
                case "addBtn":
                    //presscount = 1;
                    kettle.showAddStage(0, emptyinfo, empty);
                    break;
                case "removeBtn":
                    //pass to kettlelog to see which items are checked
                    //pass into alert stage for confirmation
                    //if yes, alert stage passes back into kettlelog to set data
                    itemsToDelete=kettle.getCheckedItems();
                    kettle.showAlertStage(itemsToDelete);
                    itemsToDelete.clear();
                    break;    
                default:
                    System.out.println("Default.");
            }
        }
    }


}