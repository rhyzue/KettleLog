import Columns.*;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import javafx.scene.paint.Color;
import javafx.geometry.Insets;
import javafx.event.ActionEvent;
import javafx.stage.Modality;
import javafx.stage.StageStyle;
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

        double spacefromtable = 5.0;
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
        TableColumn<Columns, String> iname = new TableColumn<>("Name");
            iname.setCellValueFactory(new PropertyValueFactory<>("name"));
            iname.prefWidthProperty().bind(table.widthProperty().multiply(0.20));
            iname.setResizable(false);

        TableColumn<Columns, String> istatus = new TableColumn<>("Status");
            istatus.setCellValueFactory(new PropertyValueFactory<>("status"));
            istatus.prefWidthProperty().bind(table.widthProperty().multiply(0.20));
            istatus.setResizable(false);

        TableColumn<Columns, String> iquantity = new TableColumn<>("Quantity");
            iquantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
            iquantity.prefWidthProperty().bind(table.widthProperty().multiply(0.20));
            iquantity.setResizable(false);

        TableColumn<Columns, String> imin = new TableColumn<>("Minimum");
            imin.setCellValueFactory(new PropertyValueFactory<>("minimum"));
            imin.prefWidthProperty().bind(table.widthProperty().multiply(0.20));
            imin.setResizable(false);

        TableColumn<Columns, String> options = new TableColumn<>("");
            options.setCellValueFactory(new PropertyValueFactory<>("icons"));
            options.prefWidthProperty().bind(table.widthProperty().multiply(0.1977));
            options.setResizable(false);

        //Does not allow the columns of the table to be swapped around.
        final TableColumn[] columns = {iname, istatus, iquantity, imin, options};

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

        //ADD COLUMNS TO TABLE
        table.getColumns().add(options);
        table.getColumns().add(iname);
        table.getColumns().add(istatus);
        table.getColumns().add(iquantity);
        table.getColumns().add(imin);
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

        EventHandler<ActionEvent> eventHandler = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {

                String itemClicked = ((Control)e.getSource()).getId();
            
                if(itemClicked=="addBtn"){
                    
                    //========================================================================
                    // NEW POPUP WINDOW FOR ADDING ITEM
                    //========================================================================

                    //Top part of the window which includes a title and a logo.
                    AnchorPane addtop = new AnchorPane();
                    addtop.setStyle("-fx-background-color: #1f66e5;");
                    addtop.setPrefSize(60, 80);     

                        //ADD NEW ITEM LABEL
                        Text addtext = new Text();
                        addtext.setText("frank");
                        addtext.setFont(new Font(18));
                        addtext.setFill(Color.WHITE);
                        AnchorPane.setLeftAnchor(addtext, 42.0);
                        AnchorPane.setBottomAnchor(addtext, 10.0);

                    addtop.getChildren().addAll(addtext);

                    Stage addwindow = new Stage();
                    addwindow.setResizable(false);
                    addwindow.setTitle("Add a New Item to Your Table Frank");

                    BorderPane abase = new BorderPane();
                    abase.setTop(addtop);
                    addwindow.setScene(new Scene(abase, 800, 400));

                    addwindow.initOwner(setup);
                    addwindow.initModality(Modality.WINDOW_MODAL);

                    addwindow.show();
                }      
            }
        };

        addBtn.setOnAction(eventHandler);
        
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
        Image kettleimage = new Image("./Misc/Logo.png");
        ImageView logo = new ImageView();
        logo.setFitHeight(100);
        logo.setFitWidth(200);
        logo.setImage(kettleimage);

        AnchorPane.setLeftAnchor(logo, 50.0);
        AnchorPane.setTopAnchor(logo, 10.0);

        //TOPBAR GENERAL INFORMATION 
        topBar.setStyle("-fx-background-color: #6495ed;");
        topBar.setPrefSize(100, 150);
        topBar.getChildren().addAll(logo, addBtn, removeBtn, searchbar, filter);
        

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
        //13 inch laptops are 1280 by 800. 
        setup.setScene(new Scene(base, 1024, 745));
        setup.show();

    }
}


