package KettleEventHandler;

import javafx.event.ActionEvent;
import javafx.event.ActionEvent; 
import javafx.event.EventHandler; 
import javafx.scene.Node;
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

    public void addBtnAction(){
    	System.out.println("Item Added");

    }

    public void removeBtnAction(){
    	System.out.println("Item Removed");

    }
        
        
        

}

