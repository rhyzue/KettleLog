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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.TableColumn.CellEditEvent;

public class AddButtonCell extends TableCell<Item, String> implements Callback<TableColumn<Item, String>, TableCell<Item, String>> {

        Kettlelog kettle = new Kettlelog();

        @Override
        public TableCell call(final TableColumn<Item, String> param) {

            return new TableCell<Item, String>() {

                @Override
                public void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);

                    Button starBtn = new Button();
                    Image starImgClr = new Image("./Misc/starBtnClr.png");   
                    Image starImgSel = new Image("./Misc/starBtnSel.png");
                    ImageView starImg = new ImageView(); 
                    CheckBox checkBtn = new CheckBox();
                    AnchorPane buttonanchor = new AnchorPane();

                    buttonanchor = createButtonAnchorPane(this, starBtn, starImgClr, starImgSel, starImg, checkBtn);

                    //int sz = data.size();
                    //String desc = "";
                    //if(sz == 1){
                    //    desc = (data.get(0)).getID();
                    //}

                    /*if(!desc.equals("empty column")){
                        buttonanchor = createButtonAnchorPane(this, starBtn, starImgClr, starImgSel, starImg, checkBtn);
                    }*/

                    if (empty) {
                        setGraphic(null);
                        setText(null);
                    }
                    else{
                        setGraphic(buttonanchor);
                        setText(null);

                        Item curItem = (Item) this.getTableRow().getItem();
                        /*starred = curItem.getStarred();  
                        if(starred==true){   
                            starImg.setImage(starImgSel);    
                        }       
                        else{ 
                            starImg.setImage(starImgClr);           
                        }
                        starBtn.setGraphic(starImg);

                        if(curItem.getChecked()==true){
                            checkBtn.setSelected(true);
                        }
                        else{
                            checkBtn.setSelected(false);
                        }*/ 
                    }
                }
                
        };

    }

    //Here is a method that creates a new TableCell with buttons attached to it. 
    //The functions of the buttons are also defined here too.
    public AnchorPane createButtonAnchorPane(final TableCell cell, final Button starBtn, final Image starImgClr, final Image starImgSel, final ImageView starImg, final CheckBox checkBtn) {

            double dfromtop = 1.0;
            //CheckBox checkBtn = new CheckBox();
            Button triangleBtn = new Button();
            Button penBtn = new Button(); 
            Button delBtn = new Button();  

            starBtn.setTooltip(new Tooltip("Star"));                                      
            starBtn.setStyle("-fx-background-color: transparent;");             
            starImg.setFitWidth(20);
            starImg.setPreserveRatio(true);
            starImg.setSmooth(true);
            starImg.setCache(true); 

            starImg.setImage(starImgClr);
            starBtn.setGraphic(starImg);  
                
            /*starBtn.setOnAction(new EventHandler<ActionEvent>() {       
                @Override       
                public void handle(ActionEvent event) { 
                    //deselect all checkboxes

                    Columns item = (Columns) cell.getTableRow().getItem();
                    starred = item.getStarred();
                    if(starred==true){  
                        starImg.setImage(starImgClr);      
                        starBtn.setGraphic(starImg);      
                        item.setStarred(false);
                    }       
                    else{ 
                        starImg.setImage(starImgSel);         
                        starBtn.setGraphic(starImg);    
                        item.setStarred(true);  
                    } 
                    if(filterSel==1){
                        for(int i = 0; i<data.size(); i++){
                        Columns curItem = data.get(i);
                        curItem.setChecked(false);
                        CellGenerator cellFactory = new CellGenerator();    
                        columns[0].setCellFactory(cellFactory);
                    } 
                    removeBtn.setDisable(true);
                        sortByStarred();
                    }     
                }       
            });         

            checkBtn.setSelected(false);
            checkBtn.setTooltip(new Tooltip("Select"));

            checkBtn.selectedProperty().addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                    checkBtn.setSelected(newValue);
                    Columns item = (Columns) cell.getTableRow().getItem();
                    item.setChecked(newValue);

                    removeBtn.setDisable(true);
                    for(int x=0; x<data.size(); x++){ //if at least one item is checked, removeBtn should be enabled
                        Columns curItem = data.get(x);
                        if(curItem.getChecked()==true){
                            removeBtn.setDisable(false);
                        }
                    }
                }
            });   */    

            triangleBtn.setId("triangleBtn");
            triangleBtn.setStyle("-fx-background-color: transparent;");                  
            Image triangleBtnImg = new Image("./Misc/info.png");

            ImageView triangleImg = new ImageView();          
            triangleImg.setStyle("-fx-background-color: transparent;");             
            triangleImg.setImage(triangleBtnImg);     
            triangleImg.setFitWidth(20);
            triangleImg.setPreserveRatio(true);
            triangleImg.setSmooth(true);
            triangleImg.setCache(true); 
            triangleBtn.setGraphic(triangleImg);    

            triangleBtn.setOnAction(new EventHandler<ActionEvent>() {       
                @Override       
                public void handle(ActionEvent event) {   
                    Item test = (Item) cell.getTableRow().getItem();
                    //displayInfo(test);  
                }       
            });

            penBtn.setId("penBtn");
            penBtn.setTooltip(new Tooltip("Edit"));

            //Icon taken from flaticon.com
            Image penBtnImg = new Image("./Misc/pencil2.png");

            ImageView penImg = new ImageView();          
            penBtn.setStyle("-fx-background-color: transparent;");             
            penImg.setImage(penBtnImg);
            penImg.setFitWidth(20);
            penImg.setPreserveRatio(true);
            penImg.setSmooth(true);
            penImg.setCache(true); 
            penBtn.setGraphic(penImg);

            penBtn.setOnAction(new EventHandler<ActionEvent>() {            
                @Override               
                public void handle(ActionEvent event) {         
                    Item test = (Item) cell.getTableRow().getItem();      
                    System.out.println(test.getName());
                    String[] editinfo = {test.getName(), test.getQuantity(), test.getMinimum(), test.getDelivery(), test.getDesc(), test.getDate()};        
                    kettle.showAddStage(1, editinfo);     
                }       
            }); 

            delBtn.setId("delBtn");
            delBtn.setTooltip(new Tooltip("Delete"));

            //Icon taken from flaticon.com
            Image delBtnImg = new Image("./Misc/delete2.png");

            ImageView delImg = new ImageView();          
            delBtn.setStyle("-fx-background-color: transparent;");             
            delImg.setImage(delBtnImg);
            delImg.setFitWidth(20);
            delImg.setPreserveRatio(true);
            delImg.setSmooth(true);
            delImg.setCache(true); 
            delBtn.setGraphic(delImg); 

            /*delBtn.setOnAction(new EventHandler<ActionEvent>() {       
                @Override       
                public void handle(ActionEvent event) { 
                    Columns test = (Columns) cell.getTableRow().getItem();
                    itemsToDelete.add(test);
                    displayAlert(itemsToDelete);
                }
            });*/   

            AnchorPane iconPane = new AnchorPane();
            iconPane.setPrefSize(200, 30);
            iconPane.setLeftAnchor(checkBtn, 10.0);
            iconPane.setTopAnchor(checkBtn, 8.0);
            iconPane.setLeftAnchor(starBtn, 33.0);
            iconPane.setTopAnchor(starBtn, dfromtop);
            iconPane.setLeftAnchor(triangleBtn, 68.0);
            iconPane.setTopAnchor(triangleBtn, dfromtop);
            iconPane.setLeftAnchor(penBtn, 103.0);
            iconPane.setTopAnchor(penBtn, dfromtop);
            iconPane.setLeftAnchor(delBtn, 138.0);
            iconPane.setTopAnchor(delBtn, dfromtop);

            iconPane.getChildren().addAll(checkBtn, starBtn, triangleBtn, penBtn, delBtn);

            return iconPane;

    }   

}