import Columns.*;
import javafx.util.*;
import javafx.scene.text.*;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Screen;
import javafx.scene.layout.*;
import javafx.geometry.Bounds;
import javafx.scene.control.*;
import javafx.geometry.Insets;
import javafx.stage.Modality;
import javafx.stage.StageStyle;
import javafx.event.ActionEvent;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.ImageView;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.collections.ListChangeListener;
import javafx.scene.control.cell.PropertyValueFactory;

public class Kettlelog extends Application {
    //================================================================================
    // GLOBAL VARIABLES
    //================================================================================
    Stage setup = new Stage();
    BorderPane base = new BorderPane();

    double w_to_h = 1.4;
    double w = 1024;
    double h = 1024 / w_to_h;
    double spacefromtable = 7.5;

    double screenX = 0.0;
    double screenY = 0.0;

    @SuppressWarnings("unchecked")
    Region opaqueLayer = new Region();
    TableView<Columns> table = new TableView<Columns>();
    String[] titles = {"", "Name","Status","Quantity","Minimum"};
    TableColumn<Columns, String>[] columns = (TableColumn<Columns, String>[])new TableColumn[titles.length];

    public static void main(String[] args) {
        launch(args);
    }
    
    @Override
    //SETUP
    public void start(Stage setup) {
        //================================================================================
        // INITIALIZATION
        //================================================================================
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

        CellGenerator cellFactory = new CellGenerator();

        //COLUMN TITLES
        
        for(int i=0; i<titles.length; i++)
        {
            TableColumn<Columns, String> column = new TableColumn<Columns, String>(titles[i]);
            column.setCellValueFactory(new PropertyValueFactory<>(titles[i].toLowerCase()));
            column.prefWidthProperty().bind(table.widthProperty().multiply(0.1977));
            column.setResizable(false);
            columns[i] = column;
            columns[0].setCellFactory(cellFactory);
        }
        table.getColumns().<Columns, String>addAll(columns);

        ColumnHandler columnChange = new ColumnHandler();
        table.getColumns().addListener(columnChange);
            
        table.getItems().add(new Columns("Gloves", "Good", "17", "5"));
        table.getItems().add(new Columns("Drills", "Low", "2", "10"));

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
        removeBtn.setText("REMOVE");
        removeBtn.setId("removeBtn"); 

        //================================================================================
        // ADD AND REMOVE BUTTON FUNCTIONALITY
        //================================================================================
        Handler eventHandler = new Handler();
        addBtn.setOnAction(eventHandler);
        
        //POSITIONS OF ADD AND REMOVE
        AnchorPane.setRightAnchor(addBtn, 135.0);
        AnchorPane.setBottomAnchor(addBtn, spacefromtable);
        AnchorPane.setRightAnchor(removeBtn, 55.0);
        AnchorPane.setBottomAnchor(removeBtn, spacefromtable);
       
        //SEARCH BAR
        TextField searchbar = new TextField();
            searchbar.setPrefWidth(w / 2.56);
            searchbar.setPromptText("Search");
        AnchorPane.setLeftAnchor(searchbar, 305.0); 
        AnchorPane.setBottomAnchor(searchbar, spacefromtable);
        
        //FILTER COMBOBOX
        ComboBox<String> filter= new ComboBox<String>();
            filter.setPromptText("Filter By");
            filter.setPrefWidth(150.0);
            filter.getItems().add("Starred");
            filter.getItems().add("Checked");
            filter.getItems().add("Most Recent");
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

        //SHOW SCENE
        //13 inch laptops are 1280 by 800. 

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

    //NEW WINDOW FOR ADD ITEMS
    public void addItemPopup(){

        Bounds sb = base.localToScreen(base.getBoundsInLocal());

        //Variable initialization
        Stage addwindow = new Stage();
        opaqueLayer.setVisible(true);
        double addwidth = 600;
        double addw_to_h = 0.85;
        double addheight = addwidth / addw_to_h;
        screenX = (sb.getMinX() + w / 2 - addwidth / 2); 
        screenY = (sb.getMinY() + h / 2 - addheight / 2);

        //TOP PART of the window which includes a title and a logo.
        AnchorPane addtop = new AnchorPane();
        addtop.setStyle("-fx-background-color: #800040;");
        addtop.setPrefSize(60, 80);     

        //ADD NEW ITEM LABEL
        Text addtext = new Text();
        addtext.setText("Add New Item");
        addtext.setFont(new Font(18));
        addtext.setFill(Color.WHITE);
        AnchorPane.setLeftAnchor(addtext, 42.0);
        AnchorPane.setBottomAnchor(addtext, 10.0);

        //BOTTOM PART of the window which includes an "Add" and "Cancel" button.
        AnchorPane addbottom = new AnchorPane();
        addbottom.setStyle("-fx-background-color: #800040;");
        addbottom.setPrefSize(30, 40);   
        Button cancelbtn = new Button();
        cancelbtn.setText("Cancel");
        cancelbtn.setId("cancelBtn");
        AnchorPane.setRightAnchor(cancelbtn, 7.0);
        AnchorPane.setBottomAnchor(cancelbtn, 7.0);

        //Attach components to their respective panes.
        addtop.getChildren().addAll(addtext);
        addbottom.getChildren().addAll(cancelbtn);

        //CANCEL BUTTON FUNCTIONALITY
        cancelbtn.setOnAction(new EventHandler<ActionEvent>() {
        @Override
            public void handle(ActionEvent event) {
                addwindow.hide();
                opaqueLayer.setVisible(false);
            }
        }); 

        //BASE BORDER (CENTER WILL BE THE TEXT FIELDS)
        BorderPane abase = new BorderPane();
        abase.setTop(addtop);
        abase.setBottom(addbottom);
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

        //STAGE INFORMATION 
        addwindow.initStyle(StageStyle.UNDECORATED);
        addwindow.initOwner(setup);
        addwindow.initModality(Modality.WINDOW_MODAL);
        addwindow.setResizable(false);
        addwindow.setTitle("Add New Item");
        addwindow.show();

    }

    //================================================================================
    // CLASSES
    //================================================================================

    public class CellGenerator implements Callback<TableColumn<Columns, String>, TableCell<Columns, String>>{
        @Override
        public TableCell call(final TableColumn<Columns, String> param) {
                final TableCell<Columns, String> cell = new TableCell<Columns, String>() {
                    int b = 0;
                    Button starBtn = new Button("s");
                    Button checkBtn = new Button("c");
                    Button triangleBtn = new Button("t");
                    Button penBtn = new Button("p"); 
                    Button delBtn = new Button("d");
                    Handler eventHandler = new Handler();

                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);

                        starBtn.setId("starBtn");
                        starBtn.setOnAction(eventHandler);

                        checkBtn.setId("checkBtn");
                        checkBtn.setOnAction(eventHandler);

                        triangleBtn.setId("triangleBtn");
                        triangleBtn.setOnAction(eventHandler);

                        penBtn.setId("penBtn");
                        penBtn.setOnAction(eventHandler);

                        delBtn.setId("delBtn");
                        delBtn.setOnAction(eventHandler);

                        HBox iconBox = new HBox(10);
                        iconBox.getChildren().addAll(starBtn, checkBtn, triangleBtn, penBtn, delBtn);
                        setGraphic(iconBox);
                        setText(null);
                        
                    }
                };
            return cell;
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

    public class Handler implements EventHandler<ActionEvent>{
        @Override
        public void handle(ActionEvent e) {

            String itemClicked = ((Control)e.getSource()).getId();

            switch(itemClicked){
                case "addBtn":
                    addItemPopup(); 
                    break;
                case "starBtn":
                    System.out.println("Star");
                    break;
                case "checkBtn":
                    System.out.println("Check");
                    break;
                case "triangleBtn":
                    System.out.println("Triangle");
                    break;
                case "penBtn":
                    System.out.println("Pen");
                    break;
                case "delBtn":
                    System.out.println("Delete");
                    break;
                default:
                    System.out.println("Otherstuff");
            }
        }
    }


}
