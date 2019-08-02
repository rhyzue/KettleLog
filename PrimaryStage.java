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

public class PrimaryStage extends Stage{

	//VARIABLES AND NODES -----------temp? Does this belong to main or here//if here, need get and possibly set methods
    double w_to_h = 1.4;
    double w = 1024;
    double h = w / w_to_h;
    double spacefromtable = 7.5;

    double screenX = 0.0;
    double screenY = 0.0; //----------uptohere

    ObservableList<String> filterOptions = FXCollections.observableArrayList("Starred", "Most Recent", "Oldest Added", "None");


	//Region opaqueLayer = new Region();
	//BorderPane base = new BorderPane();
	TextField searchbar = new TextField();
	MenuBar kettlemenu = new MenuBar();
    Kettlelog kettle = new Kettlelog();
    //Handler eventHandler = new Handler();

    public TableView<Item> table = new TableView<Item>();
    String[] titles = {"", "Name","Status","Quantity","Minimum"};
    TableColumn<Item, String>[] itemArray = (TableColumn<Item, String>[]) new TableColumn[titles.length];


	PrimaryStage(BorderPane base, Region opaqueLayer){ //TEMP: opaque should belong to PS. make set method to access from main

	 	this.setResizable(false);
        this.setTitle("KettleLog");
        /*
        opaqueLayer.setStyle("-fx-background-color: #001a34;");
        opaqueLayer.setOpacity(0.7);
        opaqueLayer.setVisible(false); */

        ////////////////////////////////////////////////////////////////Menu Bar
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
        // TABLE
        //================================================================================

        table.setFixedCellSize(40.0);
        table.setPrefSize(300, 508.0);
        table.setPlaceholder(new Label("Sorry, your search did not match any item names."));

        //COLUMN TITLES       
        for(int i=0; i<titles.length; i++)
        {
            TableColumn<Item, String> item = new TableColumn<Item, String>(titles[i]);
            item.setCellValueFactory(new PropertyValueFactory<>(titles[i].toLowerCase()));
            item.prefWidthProperty().bind(table.widthProperty().multiply(0.1977));
            item.setStyle( "-fx-alignment: CENTER-LEFT;");
            item.setResizable(false);
            itemArray[i] = item;
        }

        table.getColumns().<Item, String>addAll(itemArray);
/*
        ColumnHandler columnChange = new ColumnHandler(); //---------------maybe move later
        table.getColumns().addListener(columnChange);  */

        //table.setItems(data);  ------------------------------setTable method later

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

        AnchorPane topBar = new AnchorPane();

        //ADD BUTTON
        Button addBtn = new Button();
        addBtn.setText("ADD");
        addBtn.setId("addBtn");

        //REMOVE BUTTON
        Button removeBtn = new Button();
        removeBtn = new Button();
        removeBtn.setText("REMOVE");
        removeBtn.setId("removeBtn"); 
        removeBtn.setDisable(true);

        //================================================================================
        // ADD AND REMOVE BUTTON FUNCTIONALITY
        //================================================================================
        

        /*Handler eventHandler = new Handler();  ----------------------add event handling later
        addBtn.setOnAction(eventHandler);
        removeBtn.setOnAction(eventHandler);*/
        
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

        /*
        FilteredList<Columns> filteredData = new FilteredList<>(data, p -> true);  //---------------later

        searchbar.textProperty().addListener((observable, oldValue, newValue) -> { //----------------move into class
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
            CellGenerator cellFactory = new CellGenerator();    
            columns[0].setCellFactory(cellFactory);     
            table.setItems(sortedData);
        }); */
        
        //FILTER COMBOBOX
        ObservableList<String> filterOptions = FXCollections.observableArrayList("Starred", "Most Recent", "Oldest Added", "None");
        ComboBox<String> filter= new ComboBox<String>(filterOptions);
            filter.setPromptText("Filter By");
            filter.setPrefWidth(150.0);
/*
            FilterHandler filterListener = new FilterHandler();
            filter.valueProperty().addListener(filterListener);*/

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

 /*   public class Handler implements EventHandler<ActionEvent>{
        @Override
        public void handle(ActionEvent e) {

            String itemClicked = ((Control)e.getSource()).getId();

            switch(itemClicked){
                case "addBtn":
                    presscount = 1;
                    addItemPopup(0, emptyinfo, empty); 
                    break;
                case "removeBtn":
                    for(int i=0; i<data.size(); i++){
                        Columns curItem = data.get(i);
                        if(curItem.getChecked()==true){
                            System.out.println("Delete: "+ curItem.getName());
                            itemsToDelete.add(curItem);
                        }
                    }
                    displayAlert(itemsToDelete);
                    break;    
                default:
                    System.out.println("Otherstuff");
            }
        }
    }*/


}