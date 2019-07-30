import Columns.*;
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

    private static Stage setup;
    BorderPane base = new BorderPane();
    Button removeBtn;

    double w_to_h = 1.4;
    double w = 1024;
    double h = w / w_to_h;
    double spacefromtable = 7.5;

    public static int numRows = 0;
    public static int numRowsAdded = 0;
    public static boolean starred = false;
    public static int expanded = 0;
    public static int presscount = 0; 
    public static boolean duplicatefound = false;

    double screenX = 0.0;
    double screenY = 0.0;

    int filterSel = 0; //1=starred,2=checked, 3=mostrecent, 4=none

    @SuppressWarnings("unchecked")
    Region opaqueLayer = new Region();

    //table with 5 columns
    public static TableView<Columns> table = new TableView<Columns>();
    String[] titles = {"", "Name","Status","Quantity","Minimum"};
    TableColumn<Columns, String>[] columns = (TableColumn<Columns, String>[])new TableColumn[titles.length];

    public static TextField searchbar = new TextField();

    Columns empty = new Columns( "", "", "", "", "", "", "empty column", false, false, "", "");
    public static String[] emptyinfo = {"", "", "", "", "", ""};

    public static ArrayList<String> btnids = new ArrayList<String>();

    private final ObservableList<Columns> data = FXCollections.observableArrayList(empty);
    private final ObservableList<Columns> itemsToDelete = FXCollections.observableArrayList(empty);

    public static void main(String[] args) {
        launch(args);
    }
    
    @Override
    //SETUP
    public void start(Stage setup) {
        //================================================================================
        // INITIALIZATION
        //================================================================================
        this.setup = setup;
        opaqueLayer.setStyle("-fx-background-color: #001a34;");
        opaqueLayer.setOpacity(0.7);
        opaqueLayer.setVisible(false);

        setup.setResizable(false);
        setup.setTitle("KettleLog");
        
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
        // TABLE
        //================================================================================

        table.setFixedCellSize(40.0);
        table.setPrefSize(300, 508.0);
        table.setPlaceholder(new Label("Sorry, your search did not match any item names."));

        //COLUMN TITLES
        for(int i=0; i<titles.length; i++)
        {
            TableColumn<Columns, String> column = new TableColumn<Columns, String>(titles[i]);
            column.setCellValueFactory(new PropertyValueFactory<>(titles[i].toLowerCase()));
            column.prefWidthProperty().bind(table.widthProperty().multiply(0.1977));
            column.setStyle( "-fx-alignment: CENTER-LEFT;");
            column.setResizable(false);
            columns[i] = column;
        }

        table.getColumns().<Columns, String>addAll(columns);

        ColumnHandler columnChange = new ColumnHandler();
        table.getColumns().addListener(columnChange); 

        table.setItems(data);

        table.setOnMouseClicked((MouseEvent event) -> {
            if (event.getClickCount() >= 1) {
                System.out.println("PRINTING ROW INFORMATION BELOW.");
                onSelection();
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
            CellGenerator cellFactory = new CellGenerator();    
            columns[0].setCellFactory(cellFactory);     
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

        setup.setX(screenX); 
        setup.setY(screenY);
        setup.show();

    }

    //================================================================================
    // METHODS
    //================================================================================

    //Here we are ensuring that when the user clicks on an empty row, an error doesn't occur.
    public void onSelection() {
        Columns selectedItem = table.getSelectionModel().getSelectedItem();
        if(table.getSelectionModel().getSelectedItem()==null){
            System.out.println("No item here");
        }
        else{
            System.out.println(selectedItem.getDesc());
        }
    }

    //Here is a method that displays the information of a certain item when it is pressed.
    public void displayInfo(Columns rowinfo) {

        Stage infostage = new Stage();
        double infowidth = 500;
        double infoheight = 700;
        opaqueLayer.setVisible(true);

        //Hex codes for colours used on this stage
        String infostriphex = "#004545;";
        String infomidhex = "#b8d6d6;";
        String infostripcolour = String.format("-fx-background-color: %s", infostriphex);
        String infomidcolour = String.format("-fx-background-color: %s", infomidhex);

        //Ensures that alert popup is centered relatively to its parent stage (setup).
        Bounds sb = base.localToScreen(base.getBoundsInLocal());

        screenX = (sb.getMinX() + w / 2 - infowidth / 2); 
        screenY = (sb.getMinY() + h / 2 - infoheight / 2);

        ChangeListener<Number> widthListener = (observable, oldValue, newValue) -> {
            infostage.setX(screenX);
        };
        ChangeListener<Number> heightListener = (observable, oldValue, newValue) -> {
            infostage.setY(screenY);
        };

        infostage.widthProperty().addListener(widthListener);
        infostage.heightProperty().addListener(heightListener);

        infostage.setOnShown(shown -> {
            infostage.widthProperty().removeListener(widthListener);
            infostage.heightProperty().removeListener(heightListener);
        });

        //Top strip of info panel which has text saying "Item Information"
        Text infotitle = new Text();
            infotitle.setText("Item Information");
            infotitle.setFont(new Font(18));
            infotitle.setFill(Color.WHITE);

        AnchorPane infotstrip = new AnchorPane();
            AnchorPane.setLeftAnchor(infotitle, 50.0);
            AnchorPane.setBottomAnchor(infotitle, 5.0);
            infotstrip.setStyle(infostripcolour);
            infotstrip.setPrefSize(infowidth, 50); //(width, height)
            infotstrip.getChildren().addAll(infotitle);

        //Center portion of the info panel
        Label infolabel = new Label();
            AnchorPane.setTopAnchor(infolabel, 25.0);
            AnchorPane.setLeftAnchor(infolabel, 100.0);
            infolabel.setText(rowinfo.getName());
            infolabel.setFont(new Font(16));
            infolabel.setPrefHeight(50.0);
            infolabel.setPrefWidth(300.0);
            infolabel.setFont(new Font(16));
            infolabel.setAlignment(Pos.CENTER);
            infolabel.setTextOverrun(OverrunStyle.ELLIPSIS);
            infolabel.setStyle("-fx-background-color: #709c9c");

        Text dateadded = new Text();
            AnchorPane.setLeftAnchor(dateadded, 25.0);
            AnchorPane.setTopAnchor(dateadded, 102.0);
            dateadded.setText("Date Added");
            dateadded.setFont(new Font(16));

        Label datelabel = new Label();
            AnchorPane.setRightAnchor(datelabel, 25.0);
            AnchorPane.setTopAnchor(datelabel, 100.0);
            datelabel.setText(rowinfo.getDateAdded());
            datelabel.setPrefHeight(25.0);
            datelabel.setPrefWidth(125.0);
            datelabel.setFont(new Font(16));
            datelabel.setAlignment(Pos.CENTER);
            datelabel.setStyle("-fx-background-color: #95bfbf");

        Separator line1 = new Separator();
            AnchorPane.setTopAnchor(line1, 110.0);
            AnchorPane.setRightAnchor(line1, 160.0);
            line1.setPrefWidth(220.0);

        double distancedown = 40.0;

        Text adc = new Text();
            AnchorPane.setLeftAnchor(adc, 25.0);
            AnchorPane.setTopAnchor(adc, 102.0 + distancedown); // we're moving the other parts down
            adc.setText("Average Daily Consumption");
            adc.setFont(new Font(16));

        Label adclabel = new Label();
            AnchorPane.setRightAnchor(adclabel, 25.0);
            AnchorPane.setTopAnchor(adclabel, 100.0 + distancedown);
            adclabel.setFont(new Font(14));
            adclabel.setPrefHeight(25.0);
            adclabel.setPrefWidth(125.0);
            adclabel.setAlignment(Pos.CENTER);
            adclabel.setStyle("-fx-background-color: #95bfbf");

        Separator line2 = new Separator();
            AnchorPane.setTopAnchor(line2, 110.0 + distancedown);
            AnchorPane.setRightAnchor(line2, 160.0);
            line2.setPrefWidth(100.0);

        TextArea infodesc = new TextArea();
        	AnchorPane.setLeftAnchor(infodesc, 25.0);
        	AnchorPane.setTopAnchor(infodesc, 100.0 + (distancedown * 2) + 10.0);
        	infodesc.setPrefWidth(450.0);
        	infodesc.setPrefHeight(125.0);
        	infodesc.setEditable(false);
        	infodesc.setStyle("-fx-opacity: 1;");
        	infodesc.setWrapText(true);
        	if (rowinfo.getDesc().trim().length() <= 0) {
        		infodesc.setText("There is no description for this item.");
        	} else {
        		infodesc.setText("Item Description: " + rowinfo.getDesc());
        	}

    	//CONSUMPTION GRAPH THAT WILL SHOW THE USER'S USAGE
        AnchorPane infocenter = new AnchorPane();

        infocenter.setStyle(infomidcolour);
        infocenter.getChildren().addAll(infolabel, dateadded, datelabel, line1, adc, adclabel, line2, infodesc);

        //Bottom part of the strip that has a cancel button
        Button infocancel = new Button();
            infocancel.setText("Close");
            infocancel.setPrefHeight(27.5);

            infocancel.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                    public void handle(ActionEvent event) {
                        infostage.hide();
                        opaqueLayer.setVisible(false);
                    }
                }); 

        AnchorPane infobstrip = new AnchorPane();
            AnchorPane.setRightAnchor(infocancel, 5.0);
            AnchorPane.setTopAnchor(infocancel, 5.0);
            infobstrip.setStyle(infostripcolour);
            infobstrip.setPrefSize(infowidth, 37.5);
            infobstrip.getChildren().addAll(infocancel);

        BorderPane infoborderpane = new BorderPane();
            infoborderpane.setTop(infotstrip);
            infoborderpane.setCenter(infocenter);
            infoborderpane.setBottom(infobstrip);

        infostage.setResizable(false);
        infostage.initStyle(StageStyle.UNDECORATED);
        infostage.initModality(Modality.WINDOW_MODAL);
        infostage.initOwner(Kettlelog.setup);
        infostage.setScene(new Scene(infoborderpane, infowidth, infoheight));
        infostage.show();
    }


    //Here is a method that will display the confirmation alert (stage) when a user wishes to delete an item.
    public void displayAlert(ObservableList<Columns> itemsToDelete){//Columns rowinfo) {

        Stage alert = new Stage();
        opaqueLayer.setVisible(true);

        String striphex = "#610031;";
        String alertmidhex = "#dfccd5;";
        String stripcolour = String.format("-fx-background-color: %s", striphex);
        String alertmidcolour = String.format("-fx-background-color: %s", alertmidhex);

        double alertwidth = 500.0;
        double alertw_to_h = 1.42857;
        double alertheight = alertwidth / alertw_to_h;

        Bounds sb = base.localToScreen(base.getBoundsInLocal());

        screenX = (sb.getMinX() + w / 2 - alertwidth / 2); 
        screenY = (sb.getMinY() + h / 2 - alertheight / 2);

        alert.initStyle(StageStyle.UNDECORATED);
        alert.initModality(Modality.WINDOW_MODAL);
        alert.initOwner(Kettlelog.setup);

        //Ensures that alert popup is centered relatively to its parent stage (setup).
        ChangeListener<Number> widthListener = (observable, oldValue, newValue) -> {
            alert.setX(screenX);
        };
        ChangeListener<Number> heightListener = (observable, oldValue, newValue) -> {
            alert.setY(screenY);
        };

        alert.widthProperty().addListener(widthListener);
        alert.heightProperty().addListener(heightListener);

        alert.setOnShown(shown -> {
            alert.widthProperty().removeListener(widthListener);
            alert.heightProperty().removeListener(heightListener);
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

        Text delperm = new Text();
            delperm.setText("This item will be deleted permanently.");
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

            alertcancel.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                    public void handle(ActionEvent event) {
                        searchbar.clear();
                        alert.hide();
                        opaqueLayer.setVisible(false);
                        itemsToDelete.clear();
                    }
                }); 

        Button alertdelete = new Button();
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

        alert.setResizable(false);
        alert.setScene(new Scene(alertpane, alertwidth, alertheight));
        alert.showAndWait();
    }

    //Here is a singular method that allows a user to either Edit or Add an item depending on the parameters specified.
    public void addItemPopup(int popuptype, String[]textarray, Columns rowinfo){
        //0 --> ADD WINDOW
        //1 --> EDIT WINDOW

        //PRE-SET TEXT FIELDS TAKEN FROM 5-ELEMENT ARRAY
        //This array takes the name, quantity, minimum, shipping time, description and date of the column.
        //The purpose of this is to save the information so that it can be displayed when the edit button is clicked.
        String prename = textarray[0];
        String prequan = textarray[1];
        String premin = textarray[2];
        String predel = textarray[3];
        String predesc = textarray[4];
        String predate = textarray[5];
        
        //COLOURS
        Stage addwindow = new Stage();
        String tbcolour = "#006733;";
        String midcolour = "#d5f0e2;";
        String topbottom = String.format("-fx-background-color: %s", tbcolour);
        String middle = String.format("-fx-background-color: %s", midcolour);

        Bounds sb = base.localToScreen(base.getBoundsInLocal());

        //Variable initialization
        AnchorPane addbottom = new AnchorPane();
        HBox bottomBox = new HBox(10);
        Button cancelbtn = new Button();
        Button addbtn = new Button();
        bottomBox.getChildren().addAll(cancelbtn, addbtn);

        opaqueLayer.setVisible(true);
        double addwidth = 600;
        double addw_to_h = 0.85;
        double addheight = addwidth / addw_to_h;
        screenX = (sb.getMinX() + w / 2 - addwidth / 2); 
        screenY = (sb.getMinY() + h / 2 - addheight / 2);

        //TOP PART of the window which includes a title and a logo.
        AnchorPane addtop = new AnchorPane();
        addtop.setStyle(topbottom);
        addtop.setPrefSize(60, 80); 

        //ADD TITLE
        Text addtext = new Text();
        if (popuptype == 0) {
            addtext.setText("Add New Item");
        } else {
            addtext.setText("Edit Item");
        }

        addtext.setFont(new Font(18));
        addtext.setFill(Color.WHITE);
        AnchorPane.setLeftAnchor(addtext, 42.0);
        AnchorPane.setBottomAnchor(addtext, 10.0);
        addtop.getChildren().addAll(addtext);

        //BOTTOM PART of the window which includes an "Add" and "Cancel" button.
        addbottom.setStyle(topbottom);
        addbottom.setPrefSize(30, 40); 

        if (popuptype == 0) {
            addbtn.setText("Create");
        } else {
            addbtn.setText("Edit");
        }
        
        addbtn.setId("createditem");  
        addbtn.setStyle("-fx-background-color: #093d23;");
        addbtn.setTextFill(Color.WHITE);
        cancelbtn.setText("Cancel");
        cancelbtn.setId("cancelBtn");
        cancelbtn.setStyle("-fx-background-color: #d5f0e2;");
        AnchorPane.setRightAnchor(bottomBox, 6.25);
        AnchorPane.setTopAnchor(bottomBox, 6.25);
        addbottom.getChildren().addAll(bottomBox);

        //BASE BORDER (CENTER WILL BE THE TEXT FIELDS)
        BorderPane abase = new BorderPane();
        VBox wcenter = new VBox();
        abase.setTop(addtop);
        abase.setBottom(addbottom);


        //================================================================================
        // WINDOW CONTENTS (LABELS & TEXT BOXES)
        //================================================================================

        //VARIABLE INTIALIZATION
        double numbertextwidth = 100.0;

        //ITEM NAME ~ REQUIRED FIELD
        AnchorPane ianchor = new AnchorPane();
        ianchor.setPrefSize(addwidth, 80);

        Text itemname = new Text("Item Name:");
        Font f = new Font(15);
            itemname.setFont(f);
            itemname.setFill(Color.BLACK);
        AnchorPane.setRightAnchor(itemname, 460.0);
        AnchorPane.setBottomAnchor(itemname, 15.0);

        Text a = new Text("*");
            a.setFont(new Font (15));
            a.setFill(Color.RED);
        AnchorPane.setRightAnchor(a, 545.0);
        AnchorPane.setBottomAnchor(a, 16.0);

        TextField itemtext = new TextField();
            itemtext.setPrefWidth(150);
            itemtext.setText(prename);
        AnchorPane.setLeftAnchor(itemtext, 150.0); 
        AnchorPane.setBottomAnchor(itemtext, 10.0);

        //Icon taken from flaticon.com
        Image helpBtnImg = new Image("./Misc/help.png");
        ImageView helpImg = new ImageView(); 
            helpImg.setImage(helpBtnImg);
            helpImg.setFitWidth(20);
            helpImg.setPreserveRatio(true);
            helpImg.setSmooth(true);
            helpImg.setCache(true);  

        //instruction tooltip for help button
        String addtip = "add";
        String edittip = "edit";
        Tooltip helptip = new Tooltip();
        //helptip.setShowDuration(javafx.util.Duration.seconds(60));
        if (popuptype == 0) {
            helptip.setText(addtip);
        } else {
            helptip.setText(edittip);
        }

        Button helpBtn = new Button();
            helpBtn.setGraphic(helpImg);    
            helpBtn.setStyle("-fx-background-color: transparent;");    
            helpBtn.setTooltip(helptip);     
            AnchorPane.setRightAnchor(helpBtn, 175.0);
            AnchorPane.setBottomAnchor(helpBtn, 8.0);
            
        //datepicker where user can select when the action occurred
        DatePicker datepicker = new DatePicker();
            datepicker.setPrefWidth(125);
            datepicker.setEditable(false);
            if (popuptype == 0) {
                datepicker.setValue(LocalDate.now());
            } else {
	            LocalDate originaldate = LocalDate.parse(predate);
	            datepicker.setValue(originaldate);
            }
            
        AnchorPane.setRightAnchor(datepicker, 50.0);
        AnchorPane.setBottomAnchor(datepicker, 10.0);

        ianchor.setStyle(middle);
        ianchor.getChildren().addAll(itemname, itemtext, a, datepicker, helpBtn);
        HBox iBox = new HBox(ianchor);

        //QUANTITY ~ REQUIRED FIELD
        AnchorPane qanchor = new AnchorPane();
        qanchor.setPrefSize(addwidth, 90);

        Text quantity = new Text("Quantity:");
            quantity.setFont(f);
            quantity.setFill(Color.BLACK);
        AnchorPane.setRightAnchor(quantity, 460.0);
        AnchorPane.setBottomAnchor(quantity, 45.0);

        Text a2 = new Text("*");
            a2.setFont(new Font (15));
            a2.setFill(Color.RED);
        AnchorPane.setRightAnchor(a2, 527.8);
        AnchorPane.setBottomAnchor(a2, 46.0);

        TextField qtext = new TextField();
            qtext.setPrefWidth(numbertextwidth);
            qtext.setText(prequan);
        AnchorPane.setLeftAnchor(qtext, 150.0); 
        AnchorPane.setBottomAnchor(qtext, 40.0);

        // QUANTITY TEXT FIELD MUST BE NUMERIC
        qtext.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(javafx.beans.value.ObservableValue<? extends String> observable, String oldValue, 
                String newValue) {
                if (!newValue.matches("\\d*")) {
                    qtext.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });

        Text qdesc = new Text("How much of this item do you currently have on hand?");
            qdesc.setFont(new Font(12));
            qdesc.setFill(Color.GREY);
        AnchorPane.setLeftAnchor(qdesc, 150.0);
        AnchorPane.setBottomAnchor(qdesc, 20.0);

        Text qdesc2 = new Text("Entry should be in singular units (eg. 3 boxes of 50 gloves = 150).");
            qdesc2.setFont(new Font(12));
            qdesc2.setFill(Color.GREY);
        AnchorPane.setLeftAnchor(qdesc2, 150.0);
        AnchorPane.setBottomAnchor(qdesc2, 5.0);
 
        qanchor.setStyle(middle);
        qanchor.getChildren().addAll(quantity, qtext, qdesc, qdesc2, a2);
        HBox qBox = new HBox(qanchor);

        //MINIMUM ~ REQUIRED FIELD
        AnchorPane manchor = new AnchorPane();
        manchor.setPrefSize(addwidth, 90);

        Text minimum = new Text("Minimum:");
            minimum.setFont(f);
            minimum.setFill(Color.BLACK);
        AnchorPane.setRightAnchor(minimum, 460.0);
        AnchorPane.setBottomAnchor(minimum, 45.0);

        Text a3 = new Text("*");
            a3.setFont(new Font (15));
            a3.setFill(Color.RED);
        AnchorPane.setRightAnchor(a3, 532.0);
        AnchorPane.setBottomAnchor(a3, 46.0);

        TextField mtext = new TextField();
            mtext.setPrefWidth(numbertextwidth);
            mtext.setText(premin);
        AnchorPane.setLeftAnchor(mtext, 150.0); 
        AnchorPane.setBottomAnchor(mtext, 40.0);

        // MINIMUM TEXT FIELD MUST BE NUMERIC
        mtext.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(javafx.beans.value.ObservableValue<? extends String> observable, String oldValue, 
                String newValue) {
                if (!newValue.matches("\\d*")) {
                    mtext.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });

        Text mdesc = new Text("What is the minimum number of this item you want in your office?");
            mdesc.setFont(new Font(12));
            mdesc.setFill(Color.GREY);
        AnchorPane.setLeftAnchor(mdesc, 150.0);
        AnchorPane.setBottomAnchor(mdesc, 20.0);

        Text mdesc2 = new Text("Entry should be in singular units (eg. 3 boxes of 50 gloves = 150).");
            mdesc2.setFont(new Font(12));
            mdesc2.setFill(Color.GREY);
        AnchorPane.setLeftAnchor(mdesc2, 150.0);
        AnchorPane.setBottomAnchor(mdesc2, 5.0);
 
        manchor.setStyle(middle);
        manchor.getChildren().addAll(minimum, mtext, mdesc, mdesc2, a3);
        HBox mBox = new HBox(manchor);

        //SHIPPING ~ REQUIRED FIELD
        AnchorPane sanchor = new AnchorPane();
        sanchor.setPrefSize(addwidth, 90);

        Text shipping = new Text("Delivery Time:");
            shipping.setFont(f);
            shipping.setFill(Color.BLACK);
        AnchorPane.setRightAnchor(shipping, 460.0);
        AnchorPane.setBottomAnchor(shipping, 45.0);

        Text a4 = new Text("*");
            a4.setFont(new Font (15));
            a4.setFill(Color.RED);
        AnchorPane.setRightAnchor(a4, 565.0);
        AnchorPane.setBottomAnchor(a4, 46.0);

        TextField stext = new TextField();
            stext.setText(predel);
            stext.setPrefWidth(numbertextwidth);
        AnchorPane.setLeftAnchor(stext, 150.0); 
        AnchorPane.setBottomAnchor(stext, 40.0);

        // DELIVERY TEXT FIELD MUST BE NUMERIC
        stext.textProperty().addListener(new ChangeListener<String>() {
        @Override
            public void changed(javafx.beans.value.ObservableValue<? extends String> observable, String oldValue, 
                String newValue) {
                if (!newValue.matches("\\d*")) {
                    stext.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });

        Text sdesc = new Text("An estimate of how long the item would take to deliver to your location.");
            sdesc.setFont(new Font(12));
            sdesc.setFill(Color.GREY);
        AnchorPane.setLeftAnchor(sdesc, 150.0);
        AnchorPane.setBottomAnchor(sdesc, 20.0);

        Text sdesc2 = new Text("Entry should be in the number of days (if bought in person, enter 0).");
            sdesc2.setFont(new Font(12));
            sdesc2.setFill(Color.GREY);
        AnchorPane.setLeftAnchor(sdesc2, 150.0);
        AnchorPane.setBottomAnchor(sdesc2, 5.0);
 
        sanchor.setStyle(middle);
        sanchor.getChildren().addAll(shipping, stext, sdesc, sdesc2, a4);
        HBox sBox = new HBox(sanchor);

        //DESCRIPTION BIG H-BOX
        AnchorPane danchor = new AnchorPane();
        danchor.setPrefSize(addwidth, 240);

        Text describe = new Text("Description:");
            describe.setFont(f);
            describe.setFill(Color.BLACK);
        AnchorPane.setRightAnchor(describe, 460.0);
        AnchorPane.setTopAnchor(describe, 20.0);

        TextArea dtext = new TextArea();
            dtext.positionCaret(4);
            dtext.setPrefWidth(400);
            dtext.setPrefHeight(175);
            dtext.setWrapText(true);
            dtext.setText(predesc);

        AnchorPane.setLeftAnchor(dtext, 150.0); 
        AnchorPane.setTopAnchor(dtext, 20.0);

        Text missing = new Text();
            missing.setFont(new Font(12));
            missing.setFill(Color.FIREBRICK);
        AnchorPane.setRightAnchor(missing, 50.0);
        AnchorPane.setBottomAnchor(missing, 15.0);
        missing.setVisible(false);

        danchor.setStyle(middle);
        danchor.getChildren().addAll(describe, dtext, missing);
        HBox dBox = new HBox(danchor);

        //ADDING THE HBOXES TO A VBOX
        wcenter.getChildren().addAll(iBox, qBox, mBox, sBox, dBox);
        abase.setCenter(wcenter);
        addwindow.setScene(new Scene(abase, addwidth, addheight));

        //Ensures that addwindow is centered relatively to its parent stage (setup).
        ChangeListener<Number> widthListener = (observable, oldValue, newValue) -> {
            addwindow.setX(screenX);
        };
        ChangeListener<Number> heightListener = (observable, oldValue, newValue) -> {
            addwindow.setY(screenY);
        };

        addwindow.widthProperty().addListener(widthListener);
        addwindow.heightProperty().addListener(heightListener);

        addwindow.setOnShown(shown -> {
            addwindow.widthProperty().removeListener(widthListener);
            addwindow.heightProperty().removeListener(heightListener);
        });

        //BOTTOM BUTTONS FUNCTIONALITY
        cancelbtn.setOnAction(new EventHandler<ActionEvent>() {
        @Override
            public void handle(ActionEvent event) {
                addwindow.hide();
                opaqueLayer.setVisible(false);
                duplicatefound = false;
                presscount = 0;
            }
        }); 

        addbtn.setOnAction(new EventHandler<ActionEvent>() {
        @Override
            public void handle(ActionEvent event) {

            	if(duplicatefound){
            		presscount++;
            	}

                boolean incomplete = false;
                String itemStatus = "";
                String iName = itemtext.getText();
                String curQuan = qtext.getText();
                String minQuan = mtext.getText();
                String delTime = stext.getText();
                String itemDesc = dtext.getText();

                //we need to get the value that the user sets as the date and convert it to a string
                String newdate = datepicker.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

                //CHECKS IF THERE ARE ANY REQUIRED FIELDS THAT ARE LEFT EMPTY
                if ((iName.trim().length() <= 0) || curQuan.isEmpty() || minQuan.isEmpty() || delTime.isEmpty()) {
                    incomplete = true;
                }

                if (incomplete) {
                    missing.setText("* One or more required fields have not been filled out.");
                    missing.setVisible(true);
                }
                else {
                	duplicatefound = false;
                	for (int i = 0; i < data.size(); i++) {
                		if ((data.get(i)).getName().equals(iName)) {
                			duplicatefound = true;	
                		} 
                	}

                	if (!duplicatefound) {
                		presscount = 2;
                	}

                    //EVERY ITEM GETS ASSIGNED A UNIQUE ID WHICH IS THE TIMESTAMP AT WHICH IT WAS CREATED
                    String id = new java.text.SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new java.util.Date());
                    btnids.add(id);
                    numRows++;      

                    CellGenerator cellFactory = new CellGenerator();    
                    columns[0].setCellFactory(cellFactory);     
                    numRowsAdded=0;

                    missing.setVisible(false);
                    int intQuan = Integer.parseInt(curQuan);
                    int intMin = Integer.parseInt(minQuan);
                    int total = intQuan + intMin;
                    double health = (((double) intQuan / total)) * 100;

                    //DETERMINING THE STATUS OF THE ITEM 
                    if (intQuan == 0) {
                        itemStatus = "Empty";
                    } else if (health < 25) {
                        itemStatus = "Very Poor";
                    } else if (health < 40) {
                        itemStatus = "Poor";
                    } else if (health < 50) {
                        itemStatus = "Moderate";
                    } else if (health < 75) {
                        itemStatus = "Good";
                    } else {
                        itemStatus = "Very Good";
                    }

                    //user tries to add a duplicate for the first time, so we should display the message.
                    if (popuptype == 0 && presscount == 1) {
                    	missing.setText("* Possible duplicate found. Are you sure you want to add this item?");
                		missing.setVisible(true);
                    } 

                    //if the type is to add, then add the row.
                    else if (popuptype == 0 && presscount == 2) {
                    	String olddate = newdate;
                        data.add(new Columns(iName, itemStatus, curQuan, minQuan, delTime, itemDesc, id, false, false, newdate, olddate));
                        presscount = 0;
                        duplicatefound = false;
                        data.remove(empty);
                        itemsToDelete.remove(empty);

                        searchbar.clear();
                        opaqueLayer.setVisible(false);
	                    addwindow.hide();
	                    table.setItems(data);
                    }

                    //if the type is to edit, update the information at every field.
                    else {
                        rowinfo.setName(iName);
                        rowinfo.setQuantity(curQuan);
                        rowinfo.setMinimum(minQuan);
                        rowinfo.setDelivery(delTime);
                        rowinfo.setDesc(itemDesc);
                        rowinfo.setDate(newdate);

                        searchbar.clear();
                        opaqueLayer.setVisible(false);
	                    addwindow.hide();
	                    table.setItems(data);
                    }
                }
                if(filterSel==1){
                    sortByStarred();
                }
                else if(filterSel==2 || filterSel==3){
                    sortByMostRecent(filterSel);
                }
            }
        }); 

        //STAGE INFORMATION 
        addwindow.initStyle(StageStyle.UNDECORATED);
        addwindow.initModality(Modality.WINDOW_MODAL);
        addwindow.initOwner(Kettlelog.setup);
        addwindow.setResizable(false);
        addwindow.setTitle("Add New Item");
        addwindow.show();

    }

    //Here is a method that creates a new TableCell with buttons attached to it. 
    //The functions of the buttons are also defined here too.
    public AnchorPane createButtonAnchorPane(final TableCell cell, final Button starBtn, final Image starImgClr, final Image starImgSel, final ImageView starImg, final CheckBox checkBtn) {

            double dfromtop = 1.0;
            //CheckBox checkBtn = new CheckBox();
            Button triangleBtn = new Button();
            Button penBtn = new Button(); 
            Button delBtn = new Button();  

            starBtn.setTooltip(new Tooltip("Star"));                                      
            starBtn.setStyle("-fx-background-color: transparent;");             
            starImg.setFitWidth(20);
            starImg.setPreserveRatio(true);
            starImg.setSmooth(true);
            starImg.setCache(true); 

            starImg.setImage(starImgClr);
            starBtn.setGraphic(starImg);  
                
            starBtn.setOnAction(new EventHandler<ActionEvent>() {       
                @Override       
                public void handle(ActionEvent event) { 
                    //deselect all checkboxes

                    Columns item = (Columns) cell.getTableRow().getItem();
                    starred = item.getStarred();
                    if(starred==true){  
                        starImg.setImage(starImgClr);      
                        starBtn.setGraphic(starImg);      
                        item.setStarred(false);
                    }       
                    else{ 
                        starImg.setImage(starImgSel);         
                        starBtn.setGraphic(starImg);    
                        item.setStarred(true);  
                    } 
                    if(filterSel==1){
                        for(int i = 0; i<data.size(); i++){
                        Columns curItem = data.get(i);
                        curItem.setChecked(false);
                        CellGenerator cellFactory = new CellGenerator();    
                        columns[0].setCellFactory(cellFactory);
                    } 
                    removeBtn.setDisable(true);
                        sortByStarred();
                    }     
                }       
            });         

            checkBtn.setSelected(false);
            checkBtn.setTooltip(new Tooltip("Select"));

            checkBtn.selectedProperty().addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                    checkBtn.setSelected(newValue);
                    Columns item = (Columns) cell.getTableRow().getItem();
                    item.setChecked(newValue);

                    removeBtn.setDisable(true);
                    for(int x=0; x<data.size(); x++){ //if at least one item is checked, removeBtn should be enabled
                        Columns curItem = data.get(x);
                        if(curItem.getChecked()==true){
                            removeBtn.setDisable(false);
                        }
                    }
                }
            });       

            triangleBtn.setId("triangleBtn");
            triangleBtn.setStyle("-fx-background-color: transparent;");                  
            Image triangleBtnImg = new Image("./Misc/info.png");

            ImageView triangleImg = new ImageView();          
            triangleImg.setStyle("-fx-background-color: transparent;");             
            triangleImg.setImage(triangleBtnImg);     
            triangleImg.setFitWidth(20);
            triangleImg.setPreserveRatio(true);
            triangleImg.setSmooth(true);
            triangleImg.setCache(true); 
            triangleBtn.setGraphic(triangleImg);    

            triangleBtn.setOnAction(new EventHandler<ActionEvent>() {       
                @Override       
                public void handle(ActionEvent event) {   
                    Columns test = (Columns) cell.getTableRow().getItem();
                    displayInfo(test);  
                }       
            });

            penBtn.setId("penBtn");
            penBtn.setTooltip(new Tooltip("Edit"));

            //Icon taken from flaticon.com
            Image penBtnImg = new Image("./Misc/pencil2.png");

            ImageView penImg = new ImageView();          
            penBtn.setStyle("-fx-background-color: transparent;");             
            penImg.setImage(penBtnImg);
            penImg.setFitWidth(20);
            penImg.setPreserveRatio(true);
            penImg.setSmooth(true);
            penImg.setCache(true); 
            penBtn.setGraphic(penImg);

            penBtn.setOnAction(new EventHandler<ActionEvent>() {            
                @Override               
                public void handle(ActionEvent event) {         
                    Columns test = (Columns) cell.getTableRow().getItem();      
                    String[] editinfo = {test.getName(), test.getQuantity(), test.getMinimum(), test.getDelivery(), test.getDesc(), test.getDate()};        
                    addItemPopup(1, editinfo, test);        
                }       
            });  

            delBtn.setId("delBtn");
            delBtn.setTooltip(new Tooltip("Delete"));

            //Icon taken from flaticon.com
            Image delBtnImg = new Image("./Misc/delete2.png");

            ImageView delImg = new ImageView();          
            delBtn.setStyle("-fx-background-color: transparent;");             
            delImg.setImage(delBtnImg);
            delImg.setFitWidth(20);
            delImg.setPreserveRatio(true);
            delImg.setSmooth(true);
            delImg.setCache(true); 
            delBtn.setGraphic(delImg); 

            delBtn.setOnAction(new EventHandler<ActionEvent>() {       
                @Override       
                public void handle(ActionEvent event) { 
                    Columns test = (Columns) cell.getTableRow().getItem();
                    itemsToDelete.add(test);
                    displayAlert(itemsToDelete);
                }
            });    

            AnchorPane iconPane = new AnchorPane();
            iconPane.setPrefSize(200, 30);
            iconPane.setLeftAnchor(checkBtn, 10.0);
            iconPane.setTopAnchor(checkBtn, 8.0);
            iconPane.setLeftAnchor(starBtn, 33.0);
            iconPane.setTopAnchor(starBtn, dfromtop);
            iconPane.setLeftAnchor(triangleBtn, 68.0);
            iconPane.setTopAnchor(triangleBtn, dfromtop);
            iconPane.setLeftAnchor(penBtn, 103.0);
            iconPane.setTopAnchor(penBtn, dfromtop);
            iconPane.setLeftAnchor(delBtn, 138.0);
            iconPane.setTopAnchor(delBtn, dfromtop);

            iconPane.getChildren().addAll(checkBtn, starBtn, triangleBtn, penBtn, delBtn);

            return iconPane;

    }   

    public void sortByStarred(){
        StarComparator comp = new StarComparator();
        data.sort(comp.reversed());
        table.setItems(data);

    }

    public void sortByMostRecent(int order){
        DateComparator comp = new DateComparator();
        if(order==2){
            data.sort(comp.reversed());
        }
        else if (order==3){
            data.sort(comp);
        }
        table.setItems(data);
    }

    //================================================================================
    // CLASSES
    //================================================================================

    public class CellGenerator extends TableCell<Columns, String> implements Callback<TableColumn<Columns, String>, TableCell<Columns, String>> {

        @Override
        public TableCell call(final TableColumn<Columns, String> param) {

            return new TableCell<Columns, String>() {

                @Override
                public void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);

                    Button starBtn = new Button();
                    Image starImgClr = new Image("./Misc/starBtnClr.png");   
                    Image starImgSel = new Image("./Misc/starBtnSel.png");
                    ImageView starImg = new ImageView(); 
                    CheckBox checkBtn = new CheckBox();
                    AnchorPane buttonanchor = new AnchorPane();

                    int sz = data.size();
                    String desc = "";
                    if(sz == 1){
                        desc = (data.get(0)).getID();
                    }

                    if(!desc.equals("empty column")){
                        buttonanchor = createButtonAnchorPane(this, starBtn, starImgClr, starImgSel, starImg, checkBtn);
                    }

                    if (empty) {
                        setGraphic(null);
                        setText(null);
                    }
                    else{
                        setGraphic(buttonanchor);
                        setText(null);

                        Columns curItem = (Columns) this.getTableRow().getItem();
                        starred = curItem.getStarred();  
                        if(starred==true){   
                            starImg.setImage(starImgSel);    
                        }       
                        else{ 
                            starImg.setImage(starImgClr);           
                        }
                        starBtn.setGraphic(starImg);

                        if(curItem.getChecked()==true){
                            checkBtn.setSelected(true);
                        }
                        else{
                            checkBtn.setSelected(false);
                        }   
                    }
                }
                
        };

    }
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

    public class FilterHandler implements ChangeListener<String>{

        @Override
        public void changed(ObservableValue ov, String oldValue, String newValue){

            switch(newValue){
                case "Starred":
                    filterSel = 1;
                    sortByStarred();
                    break;
                case "Most Recent":
                    filterSel = 2;
                    sortByMostRecent(2);
                    break;
                case "Oldest Added":
                    filterSel = 3;
                    sortByMostRecent(3);
                    break;
                case "None":
                    filterSel = 0;
                default:
                    filterSel = 0;
            }

        }
    }

    public class StarComparator implements Comparator<Columns> {
        @Override
        public int compare(Columns c1, Columns c2) {
            return Boolean.valueOf(c1.getStarred()).compareTo(Boolean.valueOf(c2.getStarred()));
        }
    }

    public class DateComparator implements Comparator<Columns>{
        @Override
        public int compare(Columns c1, Columns c2){

            //Split date string into an array: year, month, day
            String[] c1Split = (c1.getDateAdded()).split("-");
            String[] c2Split = (c2.getDateAdded()).split("-");

            int[] c1Dates = new int[3];
            int[] c2Dates = new int[3];

            //Convert String dates to integer
            for(int i = 0; i<3; i++){
                try {
                   c1Dates[i] = Integer.parseInt(c1Split[i]);
                   c2Dates[i] = Integer.parseInt(c2Split[i]);
                }
                catch (NumberFormatException e){
                   c1Dates[i] = 0;
                   c2Dates[i] = 0;
                   System.out.println("ERROR");
                }

                //Compare each element in the array only if they are different
                //ie. if yr1==yr2, then don't return and instead compare the next element
                if (c1Dates[i] != c2Dates[i]){
                    return Integer.valueOf(c1Dates[i]).compareTo(Integer.valueOf(c2Dates[i]));
                }

            }

            //if the for loop has not returned anything, 2 dates are the same. return any random comparison
            return Integer.valueOf(c1Dates[0]).compareTo(Integer.valueOf(c2Dates[0]));
            
        }
    }

    public class Handler implements EventHandler<ActionEvent>{
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
    }


}
