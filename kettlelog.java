import Log.*;
import Item.*;
import java.io.*;
import java.sql.*;
import javafx.stage.*;
import javafx.scene.Scene;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javafx.collections.*;
import javafx.application.Application;

public class Kettlelog extends Application {
    @SuppressWarnings("unchecked")

    //================================================================================
    // INITIALIZATION
    //================================================================================
    public static int expanded = 0;
    public static int presscount = 0; 
    public static int filterSel = 0; //1:starred, 2:checked, 3:mostrecent, 4:none
    private static double w = 1024;
    private static double w_to_h = 1.4;
    private static double h = w / w_to_h;
    private static double spacefromtable = 7.5;
    private static double xBounds = 0.0;
    private static double yBounds = 0.0;
    private static double extraheight = 5.0;
    public static boolean starred = false;
    public static boolean duplicatefound = false;

    private static ObservableList<Item> data = FXCollections.observableArrayList();
    private static ObservableList<Log> emptylist = FXCollections.observableArrayList();
    private static ObservableList<Item> itemsToDelete;
    private static Item empty = new Item("", "", "", "", "", "", false, false, "", "", emptylist);

    private static PrimaryStage primaryStage = new PrimaryStage();
    private static InfoStage infoStage = new InfoStage();
    private static AddStage addStage = new AddStage();
    private static AlertStage alertStage = new AlertStage();

    //================================================================================
    // METHODS
    //================================================================================
    public static void main(String[] args) {
        launch(args);
    }
 
    @Override
    public void start(Stage setup) {
        createDataBase();
        data.add(empty);
        itemsToDelete = FXCollections.observableArrayList(empty);
        primaryStage.show();
        primaryStage.updatePrimaryStage(data);
    
    }

    public PrimaryStage getPrimaryStage(){
        return primaryStage;
    }

    public void showAddStage(int popuptype, String[] textarray, Item rowinfo){

        primaryStage.showOpaqueLayer();
        addStage.updateAddStage(popuptype, textarray, rowinfo);

        addStage.setX(primaryStage.getX() + primaryStage.getWidth() / 2 - 300);
        addStage.setY((primaryStage.getY() + primaryStage.getHeight() / 2 - 352.94) + extraheight);

        addStage.show();
    }


    public void hideAddStage(){
        primaryStage.hideOpaqueLayer();
        addStage.hide();
    }

    public void showInfoStage(Item rowinfo) {
        primaryStage.showOpaqueLayer();
        infoStage.updateInfoStage(rowinfo);

        infoStage.setX(primaryStage.getX() + primaryStage.getWidth() / 2 - 250);
        infoStage.setY((primaryStage.getY() + primaryStage.getHeight() / 2 - 350) + extraheight);

        infoStage.show();
    }

    public void hideInfoStage(){
        primaryStage.hideOpaqueLayer();
        infoStage.hide();
    } 

    public void showAlertStage(ObservableList<Item> itemsToDelete) {

        primaryStage.showOpaqueLayer();
        alertStage.updateAlertStage(itemsToDelete);

        alertStage.setX(primaryStage.getX() + primaryStage.getWidth() / 2 - 250);
        alertStage.setY((primaryStage.getY() + primaryStage.getHeight() / 2 - 175) + extraheight);

        alertStage.show();
    }

    public void hideAlertStage(){
        primaryStage.hideOpaqueLayer();
        alertStage.hide();
        itemsToDelete.clear();
    } 

    public boolean isItemChecked(){
        for(int i = 0; i<data.size(); i++){
            if(data.get(i).getChecked()==true){
                return true;
            }
        }
        return false;
    }

    public ObservableList<Item> getCheckedItems(){
        for(int i=0; i<data.size(); i++){
            Item curItem = data.get(i);
            if(curItem.getChecked()==true){
                itemsToDelete.add(curItem);
            }
        }
        return itemsToDelete;
    }

    public void enableRemoveBtn(){
        primaryStage.enableRemoveBtn();
    }

    public void disableRemoveBtn(){
        primaryStage.disableRemoveBtn();

    }

    public ObservableList<Item> getData(){
        return data;
    }

    public void clearSearchBar(){
        primaryStage.clearSearchBar();
    }

    public void setData(ObservableList<Item> items, int changetype){
        //0 refers to ADDING data from addwindow
        //1 refers to UPDATING data editwindow
        //2 refers to DELETING data from alertstage
        //3 is smth for filtering 

         switch(changetype){
            case 0:
                data.remove(empty);
                data.add(items.get(0)); //ITEMS WILL ONLY HAVE 1 ITEM, THE ONE THAT WE ARE ADDING.
                
                break;
            case 2:
                for(int i = 0; i<items.size(); i++){ //cycle thru itemsToDelete list and remove from data 
                    data.remove(items.get(i));
                }

                disableRemoveBtn();
                for(int j = 0; j<data.size(); j++){//search items for checked property
                    if((data.get(j)).getChecked()==true){
                        enableRemoveBtn();
                    }
                }
                //searchbar.clear();
                if(data.size()==0){
                    data.add(empty);
                }
                hideAlertStage();
                break;
            case 3:
                data = items;
                break;
            default:
                System.out.println("other option");
            }
            primaryStage.updatePrimaryStage(data);

    }

    public void uncheckAllItems(){
        for(int i = 0; i<data.size(); i++){
            Item curItem = data.get(i);
            curItem.setChecked(false);
        } 
    }

    //This is a method that takes in an item name and checks if there is a duplicate in the existing data.
    public boolean duplicatefound(String itemname) {
        String lowername = itemname.toLowerCase();
        int length = data.size();
        for (int i = 0; i < length; i++) {
            String lowercasedata = (data.get(i)).getName().toLowerCase();
            if (lowercasedata.equals(lowername)) {
                return true; 
            } 
        }
        return false;
    }

    //================================================================================
    // DATABASE
    //================================================================================

    //The purpose of this method will be to take an ObservableList<Item> and convert each Item into its own .db file. 
    public static void createDataBase(ObservableList<Item> datalist){
        int length = datalist.size();
        for (int i = 0; i < length; i++) {
            Item currentitem = datalist.get(i);
            //getName should eventually be changed to getID, which is a unique timestamp. 
            String filename = currentitem.getName().toLowerCase() + ".db";
            createDataBaseHelper(filename);
    	}
	}


    //takes in a filename and creates a new database with that filename in the db folder of Kettlelog.
    public static void createDataBaseHelper(String filename){
        String url = "jdbc:sqlite:./db/kettledb/" + filename;
 
        try (Connection conn = DriverManager.getConnection(url)) {
            //Class.forName("com.mysql.jdbc.Driver");
            if (conn != null) {
                System.out.println("A new database has been created.");
            }
 
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static Connection getDataBase(String dbName){
    	//initialize connection
    	Connection conn = null;
        try {
            String url = "jdbc:sqlite:./db/kettledb/" + dbName;
            conn = DriverManager.getConnection(url);
            System.out.println("Connection to SQLite has been established.");
            
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return conn;
    }

    //method to load in all items and logs from database
    public static void loadData(){//ObservableList<Item> loadData(){
    	//one database for each item
    	File dir = new File("./db/kettledb");
		File[] dblist = dir.listFiles(); //store all databases in an array

		for (int i = 0; i < dblist.length; i++) { //loop through all dbs
			//String dbName = dblist[i].getName();
			String dbName = "renTest.db";

			try{
				//connect to the db
				Connection conn = getDataBase(dbName);
				Statement stmt = conn.createStatement();

				String getMainData = "SELECT * FROM data"; //Call one table "Data", 2nd table "Log"
	            ResultSet mainData = stmt.executeQuery(getMainData);

	            while (mainData.next()) {
	                System.out.println(mainData.getString("name"));
	            }

	            String getLogData = "SELECT * FROM log";
	            ResultSet logData    = stmt.executeQuery(getLogData);
	            while (logData.next()) {
	                System.out.println(mainData.getString("quantity"));
	            }
        	}
        	catch (SQLException e) {
            	System.out.println(e.getMessage());
        	}


		}

    }


}