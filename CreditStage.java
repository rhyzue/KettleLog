import Log.*;
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
import java.lang.NumberFormatException;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.TableColumn.CellEditEvent;


public class CreditStage extends Stage{

	private String stripcolour;
	private String midcolour;
	private final String striphex = "#610031;";
    private final String midhex = "#dfccd5;";
	private final double creditwidth = 620.0;
	private final double creditheight = 420.0;

	private static AnchorPane credtstrip = new AnchorPane();
	private static AnchorPane centerstrip = new AnchorPane();
	private static AnchorPane credbstrip = new AnchorPane();

	private static BorderPane creditpane = new BorderPane();
	private static Text heading = new Text();
	private static Button close = new Button();
	private static Kettlelog kettle = new Kettlelog();

	CreditStage(){

		stripcolour = String.format("-fx-background-color: %s", striphex);
		midcolour = String.format("-fx-background-color: %s", midhex);
	    //================================================================================
	    // TOP
	    //================================================================================
		heading.setFont(new Font(18));
	        AnchorPane.setLeftAnchor(heading, 20.0);
	        AnchorPane.setBottomAnchor(heading, 10.0);

       	credtstrip.setPrefSize(creditwidth, 60); //(width, height)
        credtstrip.getChildren().addAll(heading);
        credtstrip.setStyle(stripcolour);

	    //================================================================================
	    // CENTER 
	    //================================================================================




	    //================================================================================
	    // BOTTOM
	    //================================================================================
        close.setText("Close");
        close.setPrefHeight(30);
        	AnchorPane.setRightAnchor(close, 5.0);
        	AnchorPane.setBottomAnchor(close, 5.0);

        credbstrip.setPrefSize(creditwidth, 40);
        credbstrip.getChildren().addAll(close);
        credbstrip.setStyle(stripcolour);

	    //================================================================================
	    // FINALIZATION
	    //================================================================================
        creditpane.setTop(credtstrip);
        creditpane.setCenter(centerstrip);
        creditpane.setBottom(credbstrip);

		this.setResizable(false);
        this.setScene(new Scene(creditpane, creditwidth, creditheight));
        this.initOwner(kettle.getPrimaryStage());
        this.initStyle(StageStyle.UNDECORATED);
        this.initModality(Modality.WINDOW_MODAL);

	}

	public void showCreditStage(){
		close.setOnAction(new EventHandler<ActionEvent>() {
            @Override
                public void handle(ActionEvent event) {
                    kettle.hideCreditStage();
                }
            });

	}
}