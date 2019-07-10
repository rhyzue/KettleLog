package KettleEventHandler;
import javafx.scene.Scene;
import javafx.event.ActionEvent;
import javafx.event.EventHandler; 
import javafx.scene.Node;
import javafx.stage.Stage;
import javafx.scene.control.*;
import javafx.scene.layout.*;



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

        //creating new popup 
        Stage addw = new Stage();
        addw.setResizable(false);
        addw.setTitle("Add a New Issue");
        BorderPane abase = new BorderPane();
        addw.setScene(new Scene(abase, 800, 560));
        addw.show();

    }

    //================================================================================
    // REMOVE BUTTON 
    //================================================================================
    public void removeBtnAction(){
    	System.out.println("Item Removed");

    }
        
}

