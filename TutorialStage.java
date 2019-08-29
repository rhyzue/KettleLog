
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
    private static Button nextBtn = new Button();
    private static Button prevBtn = new Button();
    private static Label titleLabel = new Label("Welcome to Kettlelog!");
    private static Font font = new Font(15);
    private static Font titleFont = new Font(25);
    private static Tooltip nextTP = new Tooltip("Next");
    private static Tooltip prevTP = new Tooltip("Previous");
    private static Label tutLabel = new Label();

    private Image nextImg = new Image("./Misc/right.png");
    private ImageView nextImgView = new ImageView();
    private Image prevImg = new Image("./Misc/left.png");
    private ImageView prevImgView = new ImageView();
    private Image pg1Img = new Image("./Misc/tutp1.png");
    private ImageView pg1ImgView = new ImageView();
    private Image pg2Img = new Image("./Misc/tutp2.png");
    private ImageView pg2ImgView = new ImageView();

    //panes
    private static AnchorPane topAnchor= new AnchorPane();
    private static AnchorPane bottomAnchor = new AnchorPane();
    private static BorderPane mainBP = new BorderPane();
    private static AnchorPane leftAnchor = new AnchorPane();
    private static AnchorPane rightAnchor = new AnchorPane();

    private static Kettlelog kettle = new Kettlelog();
    
    //controls
    private static Scene scene;

    TutorialStage(){
        TutorialStageHandler handler = new TutorialStageHandler();

        titleLabel.setFont(titleFont);
        titleLabel.setTextFill(Color.WHITE);

        AnchorPane.setTopAnchor(titleLabel, 10.0);
        AnchorPane.setLeftAnchor(titleLabel, 125.0);

        nextBtn.setId("nextBtn");
        nextBtn.setOnAction(handler);
        nextTP.setShowDelay(new javafx.util.Duration(100.0));
        nextBtn.setTooltip(nextTP); 

            nextBtn.setStyle("-fx-background-color: transparent;");             
            nextImgView.setImage(nextImg);
            nextImgView.setFitWidth(40);
            nextImgView.setPreserveRatio(true);
            nextImgView.setSmooth(true);
            nextImgView.setCache(true); 
            nextBtn.setGraphic(nextImgView);

        AnchorPane.setRightAnchor(nextBtn, 10.0);
        AnchorPane.setBottomAnchor(nextBtn, 0.0);

        prevBtn.setId("prevBtn");
        prevBtn.setOnAction(handler);
        prevTP.setShowDelay(new javafx.util.Duration(100.0));
        prevBtn.setTooltip(prevTP); 
        prevBtn.setDisable(true);

            prevBtn.setStyle("-fx-background-color: transparent;");             
            prevImgView.setImage(prevImg);
            prevImgView.setFitWidth(40);
            prevImgView.setPreserveRatio(true);
            prevImgView.setSmooth(true);
            prevImgView.setCache(true); 
            prevBtn.setGraphic(prevImgView);

        AnchorPane.setLeftAnchor(prevBtn, 10.0);
        AnchorPane.setBottomAnchor(prevBtn, 0.0);

        closeBtn.setId("closeBtn");
        closeBtn.setStyle("-fx-background-color: #d8ecf0;");
        closeBtn.setSkin(new FadeButtonSkin(closeBtn));
        closeBtn.setOnAction(handler);

        AnchorPane.setRightAnchor(closeBtn, 10.0);
        AnchorPane.setBottomAnchor(closeBtn, 10.0);

        bottomAnchor.setStyle("-fx-background-color: #3246a8;");
        bottomAnchor.setPrefHeight(45);
        bottomAnchor.getChildren().addAll(closeBtn);

        topAnchor.getChildren().addAll(prevBtn, titleLabel, nextBtn);
        topAnchor.setStyle("-fx-background-color: #3246a8;");
        topAnchor.setPrefHeight(60);

        pg1ImgView.setImage(pg1Img);
            pg1ImgView.setFitWidth(400);
            pg1ImgView.setPreserveRatio(true);
            pg1ImgView.setSmooth(true);
            pg1ImgView.setCache(true);

        pg2ImgView.setImage(pg2Img);
            pg2ImgView.setFitWidth(400);
            pg2ImgView.setPreserveRatio(true);
            pg2ImgView.setSmooth(true);
            pg2ImgView.setCache(true);

        tutLabel.setGraphic(pg1ImgView);

        //ADDING NODES TO MAIN BORDER PANE
        mainBP.setStyle("-fx-background-color: #d4e1f5;");
        mainBP.setTop(topAnchor);
        mainBP.setCenter(tutLabel);
        mainBP.setBottom(bottomAnchor);

        scene = new Scene(mainBP, 500, kettle.getScreenHeight());

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
                    tutLabel.setGraphic(pg2ImgView);
                    nextBtn.setDisable(true);
                    prevBtn.setDisable(false);
                    break;
                case "prevBtn":
                    tutLabel.setGraphic(pg1ImgView);
                    nextBtn.setDisable(false);
                    prevBtn.setDisable(true);
                    break;
                case "closeBtn":
                    kettle.hideTutorialStage();
                break;

            }
        }
    }

}