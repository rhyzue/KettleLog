
import java.text.*;
import java.util.*;
import javafx.util.*;
import javafx.stage.*;
import javafx.event.*;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.text.*;
import javafx.scene.image.*;
import javafx.scene.paint.*;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.collections.transformation.*;


public class TutorialStage extends Stage{

    //components
    private static Button closeBtn = new Button("Close");
    private static Button nextBtn = new Button("Prev");
    private static Button prevBtn = new Button("Next");

    //panes
    private static HBox hcontainer= new HBox();
    private static AnchorPane leftAnchor = new AnchorPane();
    private static AnchorPane rightAnchor = new AnchorPane();

    private static Kettlelog kettle = new Kettlelog();
    
    //controls
    private static Scene scene;

    TutorialStage(){
        TutorialStageHandler handler = new TutorialStageHandler();


        //ADDING NODES TO MAIN BORDER PANE
        hcontainer.setStyle("-fx-background-color: #ffe1bb;");

        scene = new Scene(hcontainer, 800, 500);

        this.setResizable(false);
        this.setScene(scene);
        this.initOwner(kettle.getPrimaryStage());
        this.initStyle(StageStyle.UNDECORATED);
        this.initModality(Modality.WINDOW_MODAL);

    }


    public class TutorialStageHandler implements EventHandler<ActionEvent>{
        @Override
        public void handle(ActionEvent e) {

            String itemClicked = ((Control)e.getSource()).getId();

            switch(itemClicked){
                case "nextBtn":
                break;
                case "prevBtn":
                break;
                case "closeBtn":
                break;

            }
        }
    }

}