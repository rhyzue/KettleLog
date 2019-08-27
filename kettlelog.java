import Log.*;
import Item.*;
import Notif.*;
import java.io.*;
import java.sql.*;
import java.text.*;
import java.util.*;
import java.util.concurrent.*;
import javafx.stage.*;
import java.nio.file.*;
import java.sql.Statement;
import java.time.LocalDate;
import javafx.scene.Scene;
import java.sql.Connection;
import javafx.collections.*;
import java.sql.SQLException;
import java.sql.DriverManager;
import java.time.temporal.ChronoUnit;
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
    private static List<Notif> notifList = new ArrayList<Notif>();
    private static List<Notif> invalidNotifList = new ArrayList<Notif>();
    private static ObservableList<Item> itemsToDelete;
    private static Item empty = new Item("emptyID", "", "", "", "", "", "", false, false, "", "", emptylist, "0.0", "N/A", "N/A");

    public static PrimaryStage primaryStage = new PrimaryStage();
    private static InfoStage infoStage = new InfoStage();
    private static AddStage addStage = new AddStage();
    private static AlertStage alertStage = new AlertStage();
    private static CreditStage creditStage = new CreditStage();
    private static InsertData app = new InsertData();
    private static Algorithm alg = new Algorithm();
    private static ObservableList<Item> placeholder = FXCollections.observableArrayList();
    private static NotifStage notifStage = new NotifStage();
    private static NotifComparator notifComparator = new NotifComparator();

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    //================================================================================
    // METHODS
    //================================================================================
    public static void main(String[] args) {
        launch(args);
    }
 
    @Override
    public void start(Stage setup) {

        scheduler.scheduleAtFixedRate(new Runnable() {
            public void run() {
                deleteInvalidNotifs();
                generateNotifsIfNeeded();
                System.out.println("scheduled task has executed");
            }
        }, 1, 1, TimeUnit.HOURS);

        loadData(); //will load data and notif data if it exists
        deleteInvalidNotifs();
        generateNotifsIfNeeded();

        if(notifList.size()>1){
            //sorting by most recent
            notifList.sort(notifComparator.reversed());
        }
        primaryStage.updateNotifIcon();
        itemsToDelete = FXCollections.observableArrayList(empty);
        primaryStage.updatePrimaryStage(data);
        primaryStage.show();
      }

    //================================================================================
    // STAGE GETTERS
    //================================================================================
    public PrimaryStage getPrimaryStage(){
        return primaryStage;
    }

    public AddStage getAddStage(){
        return addStage;
    }

    public InfoStage getInfoStage(){
        return infoStage;
    }

    public Algorithm getAlgorithm(){
        return alg;
    }

    //================================================================================
    // STAGE SHOWERS
    //================================================================================
    public void showAddStage(int popuptype, String[] textarray, Item rowinfo){
        primaryStage.showOpaqueLayer();
        addStage.updateAddStage(popuptype, textarray, rowinfo);

        addStage.setX(primaryStage.getX() + primaryStage.getWidth() / 2 - 300);
        addStage.setY((primaryStage.getY() + primaryStage.getHeight() / 2 - 352.94) + extraheight);

        addStage.show();
    }

    public void showInfoStage(Item rowinfo) {
        primaryStage.showOpaqueLayer();
        infoStage.updateInfoStage(rowinfo);

        infoStage.setX(primaryStage.getX() + primaryStage.getWidth() / 2 - 250);
        infoStage.setY((primaryStage.getY() + primaryStage.getHeight() / 2 - 350) + extraheight);

        infoStage.show();
    }

    public void showAlertStage(int popuptype, ObservableList<Item> itemsToDelete, Item rowinfo) {
        primaryStage.showOpaqueLayer();
        alertStage.updateAlertStage(popuptype, itemsToDelete, rowinfo);

        alertStage.setX(primaryStage.getX() + primaryStage.getWidth() / 2 - 250); //250 is width divided by 2
        alertStage.setY((primaryStage.getY() + primaryStage.getHeight() / 2 - 175) + extraheight); 

        alertStage.show();
    }

    public void showNotifStage() {
        primaryStage.showOpaqueLayer();
        //loadNotifData();
        notifStage.updateNotifStage(notifList);

        notifStage.setX(primaryStage.getX() + primaryStage.getWidth() / 2 - 250); //250 is width divided by 2
        notifStage.setY((primaryStage.getY() + primaryStage.getHeight() / 2 - 250) + extraheight); 

        notifStage.show();
    }

    public void showCreditStage() {
        primaryStage.showOpaqueLayer();
        creditStage.showCreditStage();

        creditStage.setX(primaryStage.getX() + primaryStage.getWidth() / 2 - 310); 
        creditStage.setY((primaryStage.getY() + primaryStage.getHeight() / 2 - 220) + extraheight); 

        creditStage.show();

    }

    //================================================================================
    // STAGE HIDERS
    //================================================================================
    public void hideAddStage(){
        primaryStage.hideOpaqueLayer();
        addStage.hideOpaqueLayer();
        addStage.hide();
    }

    public void hideInfoStage(){
        primaryStage.hideOpaqueLayer();
        infoStage.hide();
    } 

    public void hideAlertStage(int popuptype){
    	if (popuptype == 1) {
        	primaryStage.hideOpaqueLayer();
    	}
        alertStage.hide();
        itemsToDelete.clear();
    } 

    public void hideNotifStage() {
        primaryStage.hideOpaqueLayer();
        notifStage.hide();
    }

    public void hideCreditStage() {
        primaryStage.hideOpaqueLayer();
        creditStage.hide();
    }

    //================================================================================
    // CHECKBOX HELPERS
    //================================================================================
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

    //This method will the checked items in the table without the empty row.
    public ObservableList<Item> getCheckedNoEmpty() {
        ObservableList<Item> checkedlist = FXCollections.observableArrayList();
        ObservableList<Item> data = getData();
        int length = data.size();
        for (int i = 0; i < length; i++) {
            Item curItem = data.get(i);
            if(curItem.getChecked()==true){
                System.out.println("This item is checked.");
                checkedlist.add(curItem);
            }
        }
        return checkedlist;
    }
    //================================================================================
    // ALGORITHM HELPERS
    //================================================================================
    public static ObservableList<Log> sortLogsByDate(ObservableList<Log> loglist) {
        return alg.sortLogsByDate(loglist);
    }

    //this method gets the Logs of an item with a certain ID
    public ObservableList<Log> getLogs(String id){
        //loop through data
        for(int i = 0; i < data.size(); i++){
            //find which item has correct id, return that list of logs
            if(data.get(i).getID().equals(id)){
                System.out.println("Got item: "+ data.get(i).getName());
                return data.get(i).getLogData();
            }
        }
        return null;
    }

    //This method takes in a loglist and returns the same list, but with all the reorder-type logs REMOVED.
    public ObservableList<Log> getConsumption(ObservableList<Log> loglist) {
        ObservableList<Log> consumptiononly = FXCollections.observableArrayList();
        int length = loglist.size();
        for (int i = 0; i < loglist.size(); i++) {
            Log current = loglist.get(i);
            if (current.getLogType().equals("CONSUMPTION")) {
                consumptiononly.add(current);
            } 
        }

        return consumptiononly;
    }

    //method that updates final quantity, ADC, ROP, ROD all at once.
    public void updateEverything(String id){
        alg.setUpdatedQuan(id);
        alg.setCalculations(id);
        alg.setUpdatedStatus(id);

    }

    //================================================================================
    // NOTIFICATION MANAGEMENT
    //================================================================================

    public void generateNotifsIfNeeded(){

        //if there are no items, there should be no notifications generated
    	if(data.size()==0 || data.get(0).getID().equals("emptyID")){
    		return;
    	}
        
    	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		java.util.Date today = new java.util.Date();
		String todayString = dateFormat.format(today);
        List<Notif> notifsToAdd = new ArrayList<Notif>();

		//loop through all items and check the reorder date
		for(int i = 0; i<data.size(); i++){
			Item curItem = data.get(i);

            java.util.Date rod = new java.util.Date();
            String rodString = curItem.getROD();

            //there is no reorder date given
            if(rodString.equals("N/A")){
                continue; //skip to next iteration
            }

            //get the ROD as a date object
            try{
                rod = dateFormat.parse(rodString);
            }
            catch(ParseException ex){
                ex.printStackTrace();
            }

            //if the date today is equal to or greater than the reorder date
			if(today.after(rod) || todayString.equals(rodString)){
				String message = "Reorder needed for: "+curItem.getName();
                String randomId = java.util.UUID.randomUUID().toString();

                //create a notif and check for a duplicate
                Notif nf = new Notif(message, curItem.getID(), 0, randomId, todayString);
				boolean duplicateNotifFound = duplicateExists(nf);
                //there is no duplicate
				if(!duplicateNotifFound){
                    //add it to a list to generate later
					notifsToAdd.add(nf);
				}
			}
		}

        //If >20 notifs need to be generated, only generate 1 notif with the below message
        if(notifsToAdd.size()>20){
            String nfId = java.util.UUID.randomUUID().toString();
            Notif n = new Notif("Reorder needed for 20+ items", "N/A", 0, nfId, todayString);
            if(!duplicateExists(n)){
                app.addNotification(n);
                notifList.add(n);
            }
        }
        //else just add it normally
        else{
            for(int k = 0; k<notifsToAdd.size(); k++){
                Notif curNf = notifsToAdd.get(k);
                app.addNotification(curNf);
                notifList.add(curNf);
            }
        }
        notifsToAdd.clear();
        //Account for already existing notifs
        checkNotifOverflow();
		notifStage.updateNotifStage(notifList);
    }

    public void checkNotifOverflow(){
        List<Notif> notifsToDelete = new ArrayList<Notif>();

        //if there are more than 20 notifs
        if(notifList.size()>20){
            int sz = notifList.size();
            int numToDelete = 20-sz;

            //delete the last few notifs
            for(int i = 0; i<numToDelete; i++){

                Notif nf = notifList.get(sz-1-i);
                notifsToDelete.add(nf);

            }
            deleteNotifs(notifsToDelete);
        }
    }

    public boolean duplicateExists(Notif pdNf){//potenial duplicate notification

        String pdMsg = pdNf.getMessage();
        String pdItemId = pdNf.getItemId();

        //search the valid notif list
        for(int j = 0; j<notifList.size(); j++){
            Notif existNf = notifList.get(j);
            if(existNf.getMessage().equals(pdMsg) && (existNf.getItemId()).equals(pdItemId)){
                return true;
            }
        }

        //search the invalid notifs
        for(int k = 0; k<invalidNotifList.size(); k++){
            Notif existNf = invalidNotifList.get(k);
            if(existNf.getMessage().equals(pdMsg) && (existNf.getItemId()).equals(pdItemId)){
                return true;
            }
        }

        return false;
    }

    //removes the given notifs from notifList
    public void deleteNotifs(List<Notif> notifsToDelete){

        //find the given notif in notifList and remove it
    	for(int i = 0; i<notifsToDelete.size(); i++){
    		String notifId = notifsToDelete.get(i).getNotifId();
            int readStatus = notifsToDelete.get(i).getReadStatus();
         
            //get the notif with the correct ID and remove it
	    	for(int j = 0; j<notifList.size(); j++){
                Notif del = notifList.get(j);
	    		if((del.getNotifId()).equals(notifId)){
	    			notifList.remove(j);
	    			break;
	    		}
	    	}
        }
    }

    public void deleteInvalidNotifs(){
        if(invalidNotifList.size()==0){
            return;
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        java.util.Date today = new java.util.Date();
        String todayString = dateFormat.format(today);

        for(int i = 0; i <invalidNotifList.size(); i++){
            Notif curNf = invalidNotifList.get(i);
            //if date generated != today, we can delete it from both the notif list and the db
            if(!(curNf.getDateGenerated()).equals(todayString)){
                invalidNotifList.remove(i);
                app.deleteNotif(curNf.getNotifId());
                i--;
            }
        }
    }

    public void updateNotifs(List<Notif> nfList){

        //for each notif to update
    	for(int x = 0; x<nfList.size(); x++){
    		Notif nf = nfList.get(x);
            int readStatus = nf.getReadStatus();
            String notifId = nf.getNotifId();

            //also update kettle's list
            //if read status is -3, remove it from notifList and add it to invalidNotifList
            if(readStatus==-3){
                app.updateNotifReadStatus(readStatus, notifId);
                invalidNotifList.add(nf);
                for(int i = 0; i<notifList.size(); i++){
                    if((notifList.get(i).getNotifId()).equals(notifId)){
                        notifList.remove(i);
                        break;
                    }
                }
            }
            //-1 = just delete
            else if(readStatus==-1){
                for(int i = 0; i<notifList.size(); i++){
                    if((notifList.get(i).getNotifId()).equals(notifId)){
                        notifList.remove(i);
                        break;
                    }
                }
                app.deleteNotif(notifId);
            }
            else if (readStatus==0 || readStatus == 1){
                app.updateNotifReadStatus(readStatus, notifId);
    	    	for(int i = 0; i<notifList.size(); i++){
    	    		Notif curNf = notifList.get(i);
    	    		if((curNf.getNotifId()).equals(notifId)){
    	    			curNf.setReadStatus(nf.getReadStatus());
    	    		}
    	    	}
            }
	    }
    }


    public boolean hasUnreadNotif(){
        //loop through notif list and check if there is an unread notif
        for(int i = 0; i<notifList.size(); i++){
            if(notifList.get(i).getReadStatus()==0){
                return true;
            }
        }
        return false;
    }

    public List<Notif> getNotifList(){
        return notifList;
    }

    //================================================================================
    // MISCELLANEOUS
    //================================================================================
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

    public Item getItemById(String id){
        if(data.size()==0){
            return null;
        }

        for(int i = 0; i<data.size(); i++){
            Item curItem = data.get(i);
            if(curItem.getID().equals(id)){
                return curItem;
            }
        }
        return null;
    }

    //================================================================================
    // DATABASE[SQL]
    //================================================================================
    public void setData(ObservableList<Item> items, int changetype){
        //===CHANGETYPE PARAMETER===//
        //0 refers to ADDING data from addwindow
        //1 refers to UPDATING data editwindow
        //2 refers to DELETING data from alertstage
        //3 refers to FILTERING data 

         switch(changetype){
            case 0:
                data.remove(empty);
                //Our observablelist from the parameter only has one item in this case, so let's take it out. 
                Item added = items.get(0);

                //We then need to do FOUR things with this item. 

                //1. Create a database(.db) file for it. 
                String dbname = added.getID() + ".db";
                createDataBase(dbname);

                //2. Create two tables for this item's database: one for info, one for log. 
                createTables(dbname);

                //3. Then, we need to populate the database tables with the information. 
                // The zero is referring to the starred. When a brand new item is added, the item is obviously not starred so it will be 0. 
                // 0 -> Not Starred, 1 -> Starred
                app.insertinfo(added.getID(), added.getName(), added.getStatus(), added.getQuantity(), 
                    added.getMinimum(), added.getDelivery(), added.getDesc(), 0, 
                    added.getDateAdded(), added.getADC(), added.getROP(), added.getROD());

                app.insertlogs(added.getID(), added.getID(), "CONSUMPTION", added.getDate(), added.getQuantity());

                //4.Add it our data's observablelist so that we can display it in the table. 
                data.add(added); 
                
                break;
            case 2:
                for(int i = 0; i<items.size(); i++){ //cycle thru itemsToDelete list and remove from data 
                    //remove database
                    if(!items.get(i).getID().equals("emptyID")){
                        deleteDB(items.get(i).getID());
                    }
                    data.remove(items.get(i));
                }

                disableRemoveBtn();
                for(int j = 0; j<data.size(); j++){ //search items for checked property
                    if((data.get(j)).getChecked()==true){
                        enableRemoveBtn();
                    }
                }
                //searchbar.clear();
                if(data.size()==0){
                    data.add(empty);
                }
                hideAlertStage(1);
                break;
            case 3:
                data = items;
                break;
            default:
                System.out.println("other option");
            }
            primaryStage.updatePrimaryStage(data);

    }

    public void setAllChecked(boolean value){
        for(int i = 0; i<data.size(); i++){
            Item curItem = data.get(i);
            curItem.setChecked(value);
        } 
    }

    //takes in a filename and creates a new database with that filename in the db folder of Kettlelog.
    //filename needs to be UNIQUE in order to create the database. otherwise, we're just connecting to an existing database. 
    public static void createDataBase(String filename){

        String url = "jdbc:sqlite:./db/kettledb/" + filename;
        Connection conn = null;
 
        try {
            conn = DriverManager.getConnection(url);
            if (conn != null) {
                System.out.println("A new database has been created.");
            }
            conn.close();
 
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
            
        } catch (SQLException e) {
            System.out.println("getDataBase: "+ e.getMessage());
        }

        return conn;
    }

    //whenever an item is created, we need to create two tables in its database. 
    //the parameter will specify which database we attach the tables to. 
    public static void createTables(String filename) {

        // SQL statement that creates the table representing the info of the item (name, shipping time, minimum, etc.)
        // Notes: Checked is left out because if the user closes the program with something checked we don't need to save that check.
        //        Starred is represented as an integer, although it is a boolean. 
        String infotableSQL = "CREATE TABLE IF NOT EXISTS info ("
            + "id text primary key,"
            + "name text not null,"
            + "status text not null,"
            + "quantity text not null,"
            + "minimumstock text not null,"
            + "deliverytime text not null,"
            + "description text not null,"
            + "starred int not null,"
            + "dateadded text not null,"
            + "adc text not null,"
            + "rop text not null,"
            + "rod text not null"
            + ");";

        //Creating the log table in the same database. 
        String logtableSQL = "CREATE TABLE IF NOT EXISTS log ("
            + "logid integer primary key,"
            + "logtype text not null,"
            + "logdate text not null,"
            + "logquan text not null"
            + ");";

        try{
            Connection conn = getDataBase(filename);
            Statement stmt = conn.createStatement();
            //creating our two tables in the database
            stmt.execute(infotableSQL);
            stmt.execute(logtableSQL);
            System.out.println("Tables have been created successfully.");
            stmt.close();
            conn.close();
        } 
        catch (SQLException e) {
            System.out.println("tablecatch");
            System.out.println(e.getMessage());
        }

    }

    public void createNotifDB(){
    	System.out.println("trying to create notif.db");
    	String url = "jdbc:sqlite:./db/kettledb/notifications.db";
        Connection conn = null;

        String notifTableSQL = "CREATE TABLE IF NOT EXISTS notifData ("
        	+ "message text not null," 
        	+ "itemId text not null,"
        	+ "readStatus int not null,"
        	+ "notifId String not null,"
            + "dateGenerated String not null"
        	+ ");";

        try {
            conn = DriverManager.getConnection(url);
            Statement stmt = conn.createStatement();
            //creating our two tables in the database
            stmt.execute(notifTableSQL);
            System.out.println("Notification table has been created");
            stmt.close();
            conn.close();
 
        } catch (SQLException e) {
            System.out.println("createNotifDB: "+ e.getMessage());
        }
    }

    //This method will add a log to certain database's Log Table, specified by the ID parameter. 
    public static void addLog(String itemid, String logid, String type, String date, String quantity) {
        app.insertlogs(itemid, logid, type, date, quantity); 
    }

    //Method that edits the information table for a specific database. 
    public static void editInfoTable(String id, String name, String status, String quantity, 
        String minimum, String delivery, String desc, int starbool, String dateadded, 
        String adc, String rop, String rod) {

        app.editinfo(id, name, status, quantity, minimum, delivery, desc, starbool, dateadded, adc, rop, rod);
    }

    //This method will delete a single log from the SQL Database for an item.
    public static void deleteLog(String itemid, String logid) {
        System.out.println("helloworlddelete");
        app.removeLog(itemid, logid);
    }

    public void updateStarredDB(String id, boolean value){

        //find file with that id
        Connection conn = getDataBase(id+".db");

        try{
            String cmd = "UPDATE info SET starred = " + value + " WHERE id = " + id +";";
            Statement stmt = conn.createStatement();
            stmt.execute(cmd);
            stmt.close();
            conn.close();
        }
        catch(SQLException e){
            System.out.println(e.getMessage());
        }
    }

    //id to find db, info to update to, type of info (logdate, logquan)
    public void updateLog(String id, String type, String info, String logid){
        //get id from item
        //find file with that id
        Connection conn = getDataBase(id+".db");

        try{
            String cmd = "UPDATE log SET " + type + " = '" + info + "' WHERE logid = '" + logid +"';";
            System.out.println(cmd);
            Statement stmt = conn.createStatement();
            stmt.execute(cmd);
            stmt.close();
            conn.close();
        }
        catch(SQLException e){
            System.out.println(e.getMessage());
        }

    }

    //Use this method to delete a database file
    public void deleteDB(String id){
        try {
            Files.delete(Paths.get("./db/kettledb/"+id+".db"));
        } catch (NoSuchFileException x) {
            System.err.format("%s: no such" + " file or directory%n", id+".db");
        } catch (DirectoryNotEmptyException x) {
            System.err.format("%s not empty%n", "kettledb");
        } catch (IOException x) {
            // File permission problems are caught here.
            System.err.println(x);
        }
    }

    /* This method will create Notification objects, set their values, and add it to the list */
    public void loadNotifData(){
    	System.out.println("Loading notif data...");
    	try{
            //connect to the db
            Connection conn = getDataBase("notifications.db");
            Statement stmt = conn.createStatement();

            String command = "SELECT * FROM notifData"; 
            ResultSet notifRS = stmt.executeQuery(command);

            int index = 0;

            while (notifRS.next()) { 
            	Notif nt = new Notif();
                nt.setMessage(notifRS.getString("message"));
                nt.setItemId(notifRS.getString("itemId"));
                nt.setReadStatus(notifRS.getInt("readStatus"));
                nt.setNotifId(notifRS.getString("notifId"));
                nt.setDateGenerated(notifRS.getString("dateGenerated"));

                if(notifRS.getInt("readStatus")==-3){
                    System.out.println("Added "+ notifRS.getString("notifId")+ " to invalid list");
                    invalidNotifList.add(nt);
                }
                else{
                    notifList.add(nt);
                }
            }

            stmt.close();
            conn.close();
            System.out.println("notif data loaded successfully");
        }
        catch (SQLException e) {
            System.out.println("loadNotifData: "+e.getMessage());
        }
    }

    /*This method will load in all item data and call the method createNotifDB() to create a 
    database file for notifications if it doesn't exist already. */
    public void loadData(){

        //if .DS_Store exists, we're going to remove it completely. 
        File store = new File("./db/kettledb/.DS_Store");
        if (store.delete()) {
            System.out.println(".DS_Store has been deleted.");
        }

        //store all databases in an array
        File dir = new File("./db/kettledb");
        File[] dblist = dir.listFiles(); 

        //if no files, exit function and add an empty data object.
        if (dblist.length==0){
            System.out.println("no files!");
            createNotifDB();
            data.add(empty); 
            return;
        }

        boolean hasNotifDB = false;
        //loop through the list of files and retrieve data
        for (int i = 0; i < dblist.length; i++) {
            String dbName = dblist[i].getName();
            
            //skip gitignore
            if(dbName.equals(".gitignore")){
            	System.out.println("Skipping .gitignore");
                if(i==dblist.length-2 && data.size()==0){
                    data.add(empty);
                }
            	continue;
            }
            //if notifications exists, load notification data
            if(dbName.equals("notifications.db")){
            	hasNotifDB = true;
            	loadNotifData();
                if(i==dblist.length-2 && data.size()==0){
                    data.add(empty);
                }
            	continue;
            }

            //if we have reached the end of our files and there is no data
            if(i>=dblist.length && data.size()==0){ 
                data.add(empty);
                //if there is no notification database, create it
                if(!hasNotifDB){
                	createNotifDB();
                	hasNotifDB = true;
                }
                return;
            }

            try{
                //connect to the db
                Connection conn = getDataBase(dbName);
                Statement stmt = conn.createStatement();

                String getMainData = "SELECT * FROM info"; 
                ResultSet mainData = stmt.executeQuery(getMainData);

                Item it = new Item();

	            while (mainData.next()) { //only one row in mainData
                    it.setID(mainData.getString("id"));
	                it.setName(mainData.getString("name"));
                    it.setStatus(mainData.getString("status"));
                    it.setQuantity(mainData.getString("quantity"));
                    it.setMinimum(mainData.getString("minimumstock"));
                    it.setDelivery(mainData.getString("deliverytime"));
                    it.setDesc(mainData.getString("description"));
                    it.setStarred(mainData.getInt("starred")==1); //1=1, true, 0=1, false
                    it.setDateAdded(mainData.getString("dateadded"));
                    it.setADC(mainData.getString("adc"));
                    it.setROP(mainData.getString("rop"));
                    it.setROD(mainData.getString("rod"));
                    it.setChecked(false);
                }

                String getLogData = "SELECT * FROM log";
                ResultSet logData = stmt.executeQuery(getLogData);

                ObservableList<Log> logInfo = FXCollections.observableArrayList();

                //get all info from log table, stick it in new Item
                while (logData.next()) {
                    Log itLog = new Log();
                    itLog.setID(logData.getString("logid"));
                    itLog.setLogType(logData.getString("logtype"));
                    itLog.setDateLogged(logData.getString("logdate"));
                    itLog.setQuanLogged(logData.getString("logquan"));
                    logInfo.add(0, itLog);
                }

                //give main item a quantity from the last log
                String lastDateLogged = logInfo.get(logInfo.size()-1).getDateLogged();
                it.setDate(lastDateLogged);

                //add observable list to item
                it.setLogData(logInfo);

                //add item to data
                data.add(it);

                stmt.close();
                conn.close();
            }
            catch (SQLException e) {
                System.out.println("loadData: "+ e.getMessage());
            }
        }

        //make notification db if we have reached the end and it doesn't exist
        if(hasNotifDB==false){ 
        	createNotifDB();
        }

        if(data.size()==0){
            data.add(empty);
        }
    }

}