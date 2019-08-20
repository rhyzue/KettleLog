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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.TableColumn.CellEditEvent;


public class AddButtonCell extends TableCell<Item, String> implements Callback<TableColumn<Item, String>, TableCell<Item, String>> {

        private final Image starImgClr = new Image("./Misc/starBtnClr.png");   
        private final Image starImgSel = new Image("./Misc/starBtnSel.png");
        private static boolean starred = false;
        private static int optionSel = 0;
        private static ObservableList<Item> rowinfo = FXCollections.observableArrayList();
        private static ObservableList<Log> emptylist = FXCollections.observableArrayList();
        private static Item empty = new Item("emptyid", "", "", "", "", "", "", false, false, "", "", emptylist, "0.0");

        private static Kettlelog kettle = new Kettlelog();
        private static OptionHandler optionHandler = new OptionHandler();
        private static OptionComparators optionObject = new OptionComparators();

        @Override
        public TableCell call(final TableColumn<Item, String> param) {
            //System.out.println("===============hereCell");

            return new TableCell<Item, String>() {

                @Override
                public void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);

                    Button starBtn = new Button();
                    //Image starImgClr = new Image("./Misc/starBtnClr.png");   
                    //Image starImgSel = new Image("./Misc/starBtnSel.png");
                    ImageView starImg = new ImageView(); 
                    CheckBox checkBtn = new CheckBox();
                    AnchorPane buttonanchor = new AnchorPane();

                    buttonanchor = createButtonAnchorPane(this, starBtn, starImgClr, starImgSel, starImg, checkBtn);

                    if (empty) {
                        setGraphic(null);
                        setText(null);
                    }
                    else{
                        setGraphic(buttonanchor);
                        setText(null);

                        //Check if the current item is starred; set appropriate image
                        Item curItem = (Item) this.getTableRow().getItem();

                        if(curItem!=null){
                            starred = curItem.getStarred();  
                            if(starred==true){   
                                starImg.setImage(starImgSel);    
                            }       
                            else{ 
                                starImg.setImage(starImgClr);           
                            }
                            starBtn.setGraphic(starImg);

                            //If current item is checked, display the checkbox as so
                            if(curItem.getChecked()==true){
                                checkBtn.setSelected(true);
                            }
                            else{
                                checkBtn.setSelected(false);
                            }
                        }
                    }
                }
                
            };

    }

    //Here is a method that creates a new TableCell with buttons attached to it. 
    //The functions of the buttons are also defined here too.
    public AnchorPane createButtonAnchorPane(final TableCell cell, final Button starBtn, final Image starImgClr, final Image starImgSel, final ImageView starImg, final CheckBox checkBtn) {

            double dfromtop = 1.0;
            //CheckBox checkBtn = new CheckBox();
            Button infoBtn = new Button();
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
            starBtn.setId("starBtn");
                
            starBtn.setOnAction(new EventHandler<ActionEvent>() {    ////////////////////////////////////    
                @Override       
                public void handle(ActionEvent event) { 
                    //deselect all checkboxes
                    Item item = (Item) cell.getTableRow().getItem();
                    if(item.getStarred()==true){  //item is already starred - unstar
                        starImg.setImage(starImgClr);      
                        starBtn.setGraphic(starImg);      
                        item.setStarred(false);
                        kettle.updateStarredDB(item.getID(), false);
                    }       
                    else{ //item is not currently starred - set star to true
                        starImg.setImage(starImgSel);         
                        starBtn.setGraphic(starImg);    
                        item.setStarred(true);  
                        kettle.updateStarredDB(item.getID(), true);
                    }

                    optionSel = optionHandler.getOptionSel();
                    if(optionSel==1){
                        kettle.setAllChecked(false);
                        kettle.disableRemoveBtn();
                        optionObject.sortByStarred();
                    }     
                }       
            });         

            checkBtn.setSelected(false);
            checkBtn.setTooltip(new Tooltip("Select"));

            checkBtn.selectedProperty().addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                    checkBtn.setSelected(newValue);
                    Item item = (Item) cell.getTableRow().getItem();
                    item.setChecked(newValue);

                    kettle.disableRemoveBtn();

                    //if at least one item is checked, enable removeBtn
                    if(kettle.isItemChecked()==true){
                        kettle.enableRemoveBtn();
                    }
                }
            });      

            infoBtn.setId("infoBtn");
            infoBtn.setStyle("-fx-background-color: transparent;");                  
            Image infoBtnImg = new Image("./Misc/info.png");

            ImageView infoImg = new ImageView();          
            infoImg.setStyle("-fx-background-color: transparent;");             
            infoImg.setImage(infoBtnImg);     
            infoImg.setFitWidth(20);
            infoImg.setPreserveRatio(true);
            infoImg.setSmooth(true);
            infoImg.setCache(true); 
            infoBtn.setGraphic(infoImg);   

            infoBtn.setOnAction(new EventHandler<ActionEvent>() {       
                @Override       
                public void handle(ActionEvent event) {   
                    Item rowinfo = (Item) cell.getTableRow().getItem();
                    kettle.showInfoStage(rowinfo);
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
                    System.out.println("getting name: "+test.getName());
                    String[] editinfo = {test.getName(), test.getQuantity(), test.getMinimum(), test.getDelivery(), test.getDesc(), test.getDate()};        
                    kettle.showAddStage(1, editinfo, test);     
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

            delBtn.setOnAction(new EventHandler<ActionEvent>() {       
                @Override       
                public void handle(ActionEvent event) { 
                    Item curItem = (Item) cell.getTableRow().getItem();
                    rowinfo.add(curItem);
                    kettle.showAlertStage(1, rowinfo, empty);
                    rowinfo.remove(curItem);
                }
            }); 

            AnchorPane iconPane = new AnchorPane();
            iconPane.setPrefSize(200, 30);
            iconPane.setLeftAnchor(checkBtn, 10.0);
            iconPane.setTopAnchor(checkBtn, 8.0);
            iconPane.setLeftAnchor(starBtn, 33.0);
            iconPane.setTopAnchor(starBtn, dfromtop);
            iconPane.setLeftAnchor(infoBtn, 68.0);
            iconPane.setTopAnchor(infoBtn, dfromtop);
            iconPane.setLeftAnchor(penBtn, 103.0);
            iconPane.setTopAnchor(penBtn, dfromtop);
            iconPane.setLeftAnchor(delBtn, 138.0);
            iconPane.setTopAnchor(delBtn, dfromtop);

            iconPane.getChildren().addAll(checkBtn, starBtn, infoBtn, penBtn, delBtn);

            return iconPane;
    }
}   
