
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
    private static Label prevLabel = new Label("Previous");
    private static Label nextLabel = new Label("Next");
    private static Label titleLabel = new Label("Welcome to Kettlelog!");
    private static Font font = new Font(25);
    private static Font titleFont = new Font(35);
    //panes
    private static AnchorPane topAnchor= new HBox(20);
    private static BorderPane mainBP = new BorderPane();
    private static AnchorPane leftAnchor = new AnchorPane();
    private static AnchorPane rightAnchor = new AnchorPane();

    private static Kettlelog kettle = new Kettlelog();
    
    //controls
    private static Scene scene;

    TutorialStage(){
        TutorialStageHandler handler = new TutorialStageHandler();

        prevLabel.setFont(font);
        nextLabel.setFont(font);
        titleLabel.setFont(titleFont);

        AnchorPane.setLeftAnchor(prevLabel, 10);
        AnchorPane.setBottomAnchor(prevLabel, 10);

        AnchorPane.setRightAnchor(nextLabel, 10);
        AnchorPane.setBottomAnchor(nextLabel, 10);


        nextBtn.setId("nextBtn");
        nextBtn.setStyle("-fx-background-color: #ffe1bb;");
        nextBtn.setSkin(new FadeButtonSkin(nextBtn));
        nextBtn.setOnAction(handler);
        AnchorPane.setRightAnchor(nextBtn, 10);
        AnchorPane.setBottomAnchor(nextBtn, 10);

        prevBtn.setId("prevBtn");
        prevBtn.setStyle("-fx-background-color: #ffe1bb;");
        prevBtn.setSkin(new FadeButtonSkin(prevBtn));
        prevBtn.setOnAction(handler);
        AnchorPane.setLeftAnchor(prevBtn, 10);
        AnchorPane.setBottomAnchor(prevBtn, 10);

        topAnchor.getChildren().addAll(prevBtn, prevLabel, titleLabel, nextLabel, nextBtn);

        topAnchor.setStyle("-fx-background-color: #3246a8;");
        topAnchor.setPrefHeight(75);

        //ADDING NODES TO MAIN BORDER PANE
        mainBP.setStyle("-fx-background-color: #d8ecf0;");
        mainBP.setTop(topAnchor);

        scene = new Scene(mainBP, 800, 500);

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