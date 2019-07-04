
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

public class kettlelog extends Application {
    public static void main(String[] args) {
        launch(args);
    }
    
    @Override
    //SETUP
    public void start(Stage setup) {
        setup.setTitle("KettleLog");
        //WELCOME BUTTON 
        Button btn = new Button();
        btn.setText("ADD");
        btn.setOnAction(new EventHandler<ActionEvent>() {
 
            @Override
            public void handle(ActionEvent event) {
                System.out.println("KettleLog!");
            }
        });
        
        //WELCOME BUTTON COORDINATES
        btn.setLayoutX(800);
        btn.setLayoutY(100);

        //CREATING THE MENU BAR
        MenuBar kettlemenu = new MenuBar();
        //CREATING MENU TABS
        Menu file = new Menu("File");
        Menu edit = new Menu("Edit");
        Menu view = new Menu("View");
        Menu help = new Menu("Help");
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
        //HELP SUBMENU
        MenuItem about = new Menu("About");
        MenuItem credits = new Menu("Credits");

        //ADDING MENUITEMS TO THEIR RESPECTIVE MENUS
        file.getItems().addAll(newtable, opentable, printtable, exit);
        edit.getItems().addAll(add, remove);
        view.getItems().addAll(fs, mi);
        help.getItems().addAll(about, credits);

        kettlemenu.getMenus().addAll(file, edit, view, help);


        TableView table = new TableView();

        TableColumn<String, Person> column1 = new TableColumn<>("First Name");
        column1.setCellValueFactory(new PropertyValueFactory<>("firstName"));


        TableColumn<String, Person> column2 = new TableColumn<>("Last Name");
        column2.setCellValueFactory(new PropertyValueFactory<>("lastName"));


        table.getColumns().add(column1);
        table.getColumns().add(column2);

        table.getItems().add(new Person("John", "Doe"));
        table.getItems().add(new Person("Jane", "Deer"));

        VBox vbox = new VBox(table);

        //ANCHORPANE 
        AnchorPane center = new AnchorPane();
        center.setStyle("-fx-background-color: green;");
        center.getChildren().add(btn);
        center.getChildren().add(vbox);

        //CREATE AND ADD ITEMS TO BASE
        BorderPane base = new BorderPane();
        base.setTop(kettlemenu);
        base.setCenter(center);

        center.setBottomAnchor(vbox, 10.0);

        setup.setScene(new Scene(base, 1024, 768));
        setup.show();



    }

    public class Person {

        private String firstName = null;
        private String lastName = null;

        public Person() {
        }

        public Person(String firstName, String lastName) {
            this.firstName = firstName;
            this.lastName = lastName;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }
    }
}