package application;
import Columns.*; 
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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.TableColumn.CellEditEvent;

public class Handler implements EventHandler<ActionEvent>{
    @Override
    public void handle(ActionEvent e) {

        String itemClicked = ((Control)e.getSource()).getId();

        switch(itemClicked){
            case "addBtn":
                presscount = 1;
                addItemPopup(0, emptyinfo, empty); 
                break;
            case "removeBtn":
                for(int i=0; i<data.size(); i++){
                    Columns curItem = data.get(i);
                    if(curItem.getChecked()==true){
                        System.out.println("Delete: "+ curItem.getName());
                        itemsToDelete.add(curItem);
                    }
                }
                displayAlert(itemsToDelete);
                break;    
            default:
                System.out.println("Otherstuff");
        }
    }
}