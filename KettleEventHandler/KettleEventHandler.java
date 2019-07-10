package KettleEventHandler;

import Popups.*;
import javafx.event.ActionEvent;
import javafx.event.ActionEvent; 
import javafx.event.EventHandler; 
import javafx.scene.Node;
import javafx.stage.Stage;
import javafx.scene.control.*;


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
        //Popups addwindow = new Popups();
        //addwindow.main(null);
    }

    //================================================================================
    // REMOVE BUTTON 
    //================================================================================
    public void removeBtnAction(){
    	System.out.println("Item Removed");

    }
        
        
        

}

