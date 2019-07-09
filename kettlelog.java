import Columns.*;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Insets;
import javafx.event.ActionEvent;
import javafx.scene.image.Image;
import javafx.event.EventHandler;
import javafx.scene.image.ImageView;
import javafx.application.Application;
import javafx.collections.ListChangeListener;
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

        double spacefromtable = 12.0;
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

        TableView<Columns> table = new TableView<Columns>();
        table.setFixedCellSize(40.0);
        table.setPrefSize(300, 508.0);

        //ADD ITEMS TO TABLE
        TableColumn<Columns, String> column1 = new TableColumn<>("Name");
            column1.setCellValueFactory(new PropertyValueFactory<>("name"));
            column1.prefWidthProperty().bind(table.widthProperty().multiply(0.25));
            column1.setResizable(false);

        TableColumn<Columns, String> column2 = new TableColumn<>("Status");
            column2.setCellValueFactory(new PropertyValueFactory<>("status"));
            column2.prefWidthProperty().bind(table.widthProperty().multiply(0.25));
            column2.setResizable(false);

        TableColumn<Columns, String> column3 = new TableColumn<>("Quantity");
            column3.setCellValueFactory(new PropertyValueFactory<>("quantity"));
            column3.prefWidthProperty().bind(table.widthProperty().multiply(0.25));
            column3.setResizable(false);

        TableColumn<Columns, String> column4 = new TableColumn<>("Minimum");
            column4.setCellValueFactory(new PropertyValueFactory<>("minimum"));
            column4.prefWidthProperty().bind(table.widthProperty().multiply(0.2477));
            column4.setResizable(false);

        //Does not allow the columns of the table to be swapped around.
        final TableColumn[] columns = {column1, column2, column3, column4};

        table.getColumns().addListener(new ListChangeListener() {
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
        });

        //Adding the columns to the table.
        table.getColumns().add(column1);
        table.getColumns().add(column2);
        table.getColumns().add(column3);
        table.getColumns().add(column4);

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
        //Image kettleimage = new Image("kettle.png");
        //ImageView logo = new ImageView();
        //logo.setImage(kettleimage);

        //TOPBAR GENERAL INFORMATION 
        topBar.setStyle("-fx-background-color: #6495ed;");
        topBar.setPrefSize(100, 150);
        topBar.getChildren().addAll(addBtn, removeBtn, searchbar, filter);
        

        //================================================================================
        // FINALIZATION
        //================================================================================

        BorderPane main = new BorderPane();
        main.setStyle("-fx-background-color: #6495ed;");
        BorderPane.setMargin(tableBox, new Insets(10, 50, 200, 50));
        main.setBottom(tableBox);
        main.setTop(topBar);

        BorderPane base = new BorderPane();
        base.setTop(kettlemenu);
        base.setCenter(main);

        //SHOW SCENE
        setup.setScene(new Scene(base, 1024, 745));
        setup.show();

    }

}