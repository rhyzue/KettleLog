import Columns.*;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Insets;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.application.Application;
import javafx.scene.control.cell.PropertyValueFactory;


public class kettlelog extends Application {
    public static void main(String[] args) {
        launch(args);
    }
    
    @Override
    //SETUP
    public void start(Stage setup) {
        //================================================================================
        // INITIALIZATION
        //================================================================================

        double spacefromtable = 15.0;
        setup.setResizable(false);
        setup.setTitle("KettleLog");
        
        //================================================================================
        // MENU BAR
        //================================================================================

        MenuBar kettlemenu = new MenuBar();
        //CREATING MENU TABS
        Menu file = new Menu("File");
        Menu edit = new Menu("Edit");
        Menu view = new Menu("View");
        Menu info = new Menu("Info");
        //FILE SUBMENU
        MenuItem newtable = new Menu("New");
        MenuItem opentable = new Menu("Open");
        MenuItem printtable = new Menu("Print");
        MenuItem exit = new Menu("Exit");
        //EDIT SUBMENU
        MenuItem add = new Menu("Add Item");
        MenuItem remove = new Menu("Remove Item");
        //VIEW SUBMENU
        MenuItem fs = new Menu("Fullscreen");
        MenuItem mi = new Menu("Minimal Interface");
        //INFO SUBMENU
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

        TableView<Columns> table = new TableView<Columns>();

        //ADD ITEMS TO TABLE
        TableColumn<Columns, String> column1 = new TableColumn<>("Name");
        column1.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Columns, String> column2 = new TableColumn<>("Status");
        column2.setCellValueFactory(new PropertyValueFactory<>("status"));

        TableColumn<Columns, String> column3 = new TableColumn<>("Quantity");
        column3.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        TableColumn<Columns, String> column4 = new TableColumn<>("Minimum");
        column4.setCellValueFactory(new PropertyValueFactory<>("minimum"));


        table.getColumns().add(column1);
        table.getColumns().add(column2);
        table.getColumns().add(column3);
        table.getColumns().add(column4);

        table.getItems().add(new Columns("Gloves", "Good", "17", "5"));
        table.getItems().add(new Columns("Drills", "Low", "2", "10"));

        VBox tableBox = new VBox(table);

        //MAIN PANEL
        //CENTER (TABLE)
        BorderPane main = new BorderPane();
        main.setStyle("-fx-background-color: #6495ed;");
        tableBox.setPadding(new Insets(0, 50, 25, 50));
        main.setCenter(tableBox);

        //================================================================================
        // TOP BAR (includes add/remove, filter, search)
        //================================================================================

        AnchorPane topBar = new AnchorPane();

        //ADD BUTTON
        Button addBtn = new Button();
        addBtn.setText("ADD");
        addBtn.setOnAction(new EventHandler<ActionEvent>() {
 
            @Override
            public void handle(ActionEvent event) {
                System.out.println("KettleLog!");
            }
        });

        //REMOVE BUTTON
        Button removeBtn = new Button();
        removeBtn.setText("REMOVE");

        //POSITIONS OF ADD AND REMOVE
        AnchorPane.setRightAnchor(addBtn, 135.0);
        AnchorPane.setBottomAnchor(addBtn, spacefromtable);
        AnchorPane.setRightAnchor(removeBtn, 55.0);
        AnchorPane.setBottomAnchor(removeBtn, spacefromtable);
       
        //SEARCH BAR
        TextField searchbar = new TextField();
        searchbar.setPrefWidth(400.0);
        AnchorPane.setLeftAnchor(searchbar, 305.0);
        AnchorPane.setBottomAnchor(searchbar, spacefromtable);
        searchbar.setPromptText("Search");

        //FILTER COMBOBOX
        ComboBox<String> filter= new ComboBox<String>();
        filter.setPromptText("Filter By");
        filter.setPrefWidth(150.0);
        filter.getItems().add("Starred");
        filter.getItems().add("Checkboxed");
        filter.getItems().add("Most Recent");
        AnchorPane.setLeftAnchor(filter, 52.0);
        AnchorPane.setBottomAnchor(filter, spacefromtable);

        //TOPBAR GENERAL INFORMATION 
        topBar.setStyle("-fx-background-color: #6495ed;");
        topBar.setPrefSize(100, 150);
        topBar.getChildren().addAll(addBtn, removeBtn, searchbar, filter);
        main.setTop(topBar);

        //================================================================================
        // FINALIZATION
        //================================================================================

        //CREATE AND ADD ITEMS TO BASE
        BorderPane base = new BorderPane();
        base.setTop(kettlemenu);
        base.setCenter(main);

        //SHOW SCENE
        setup.setScene(new Scene(base, 1024, 768));
        setup.show();

    }

}