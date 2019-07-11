package KettleEventHandler;
import javafx.scene.Scene;
import javafx.event.ActionEvent;
import javafx.event.EventHandler; 
import javafx.scene.Node;
import javafx.stage.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage.*;


public class KettleEventHandler implements EventHandler<ActionEvent>{
	//Add item to the table
    public void handle(ActionEvent e) {
    	String itemClicked = ((Control)e.getSource()).getId();
    	
        if(itemClicked=="addBtn"){
        	addBtnAction();
        }
        else if(itemClicked=="removeBtn"){
        	removeBtnAction();
        }
            
    }

    //================================================================================
    // ADD BUTTON 
    //================================================================================
    public void addBtnAction(){
    	System.out.println("Item Added");

            //========================================================================
            // NEW POPUP WINDOW FOR ADDING ITEM
            //========================================================================

            //Top part of the window which includes a title and a logo.
            AnchorPane addtop = new AnchorPane();
            addtop.setStyle("-fx-background-color: #1f66e5;");
            addtop.setPrefSize(60, 80);     

                //ADD NEW ITEM LABEL
                Text addtext = new Text();
                addtext.setText("Add New Item");
                addtext.setFont(new Font(18));
                addtext.setFill(Color.WHITE);
                AnchorPane.setLeftAnchor(addtext, 42.0);
                AnchorPane.setBottomAnchor(addtext, 10.0);

            addtop.getChildren().addAll(addtext);

            Stage addwindow = new Stage();
            addwindow.setResizable(false);
            addwindow.setTitle("Add a New Item to Your Table");
            

            BorderPane abase = new BorderPane();
            abase.setTop(addtop);
            addwindow.requestFocus();
            addwindow.setScene(new Scene(abase, 800, 560));

            addwindow.initOwner(setup);  
            addwindow.initModality(Modality.WINDOW_MODAL);

            addwindow.show();

    }

    //================================================================================
    // REMOVE BUTTON 
    //================================================================================
    public void removeBtnAction(){
    	System.out.println("Item Removed");

    }
        
}

