import Log.*;
import Item.*;
import java.util.*;
import java.io.*;
import java.sql.*;
import javafx.stage.*;
import javafx.scene.Scene;
import java.nio.file.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import javafx.collections.*;
import javafx.application.Application;
import java.text.*;
import java.time.temporal.ChronoUnit;

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
    private static Item empty = new Item("emptyID", "", "", "", "", "", "", false, false, "", "", emptylist);

    public static PrimaryStage primaryStage = new PrimaryStage();
    private static InfoStage infoStage = new InfoStage();
    private static AddStage addStage = new AddStage();
    private static AlertStage alertStage = new AlertStage();
    private static InsertData app = new InsertData();
    private static ObservableList<Item> placeholder = FXCollections.observableArrayList();
    private static NotifStage notifStage = new NotifStage();

    //================================================================================
    // METHODS
    //================================================================================
    public static void main(String[] args) {
        launch(args);
    }
 
    @Override
    public void start(Stage setup) {
        loadData();
        app.addNotification("Welcome to Kettlelog!", "None", 0, 0);
        itemsToDelete = FXCollections.observableArrayList(empty);
        primaryStage.show();
        primaryStage.updatePrimaryStage(data);
    
    }

    public PrimaryStage getPrimaryStage(){
        return primaryStage;
    }

    public AddStage getAddStage(){
        return addStage;
    }

    public InfoStage getInfoStage(){
        return infoStage;
    }

    public void showAddStage(int popuptype, String[] textarray, Item rowinfo){

        System.out.println("about to show add stage...");

        primaryStage.showOpaqueLayer();
        addStage.updateAddStage(popuptype, textarray, rowinfo);

        System.out.println("if this line shows, updateaddstage worked.");

        addStage.setX(primaryStage.getX() + primaryStage.getWidth() / 2 - 300);
        addStage.setY((primaryStage.getY() + primaryStage.getHeight() / 2 - 352.94) + extraheight);

        addStage.show();
    }


    public void hideAddStage(){
        primaryStage.hideOpaqueLayer();
        addStage.hideOpaqueLayer();
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

    public void showAlertStage(int popuptype, ObservableList<Item> itemsToDelete, Item rowinfo) {

        primaryStage.showOpaqueLayer();
        alertStage.updateAlertStage(popuptype, itemsToDelete, rowinfo);

        alertStage.setX(primaryStage.getX() + primaryStage.getWidth() / 2 - 250); //250 is width divided by 2
        alertStage.setY((primaryStage.getY() + primaryStage.getHeight() / 2 - 175) + extraheight); 

        alertStage.show();
    }

    public void hideAlertStage(int popuptype){
    	if (popuptype == 1) {
        	primaryStage.hideOpaqueLayer();
    	}
        alertStage.hide();
        itemsToDelete.clear();
    } 

    public void showNotifStage() {

        primaryStage.showOpaqueLayer();
        //notifStage.updateNotifStage();

        notifStage.setX(primaryStage.getX() + primaryStage.getWidth() / 2 - 250); //250 is width divided by 2
        notifStage.setY((primaryStage.getY() + primaryStage.getHeight() / 2 - 250) + extraheight); 

        notifStage.show();
    }

    public void hideNotifStage() {

        primaryStage.hideOpaqueLayer();

        notifStage.hide();
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

    //getting an item just based off of its id.
    public Item getItem(String id){
        for (int i = 0; i < data.size(); i++){
            //when we eventually find a match, return that item
            if(data.get(i).getID().equals(id)){
                return data.get(i);
            }
        }
        return null;
    }

    public void clearSearchBar(){
        primaryStage.clearSearchBar();
    }

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
                app.insertinfo(added.getID(), added.getName(), added.getStatus(), added.getQuantity(), added.getMinimum(), added.getDelivery(), added.getDesc(), 0, added.getDateAdded());
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
    // DATABASE[SQL]
    //================================================================================

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
            System.out.println(dbName+": Connection to SQLite has been established.");
            
        } catch (SQLException e) {
            System.out.println("getDataBase: "+e.getMessage());
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
            + "dateadded text not null"
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
        	+ "notifId int primary key"
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
    public static void editInfoTable(String id, String name, String status, String quantity, String minimum, String delivery, String desc, int starbool, String dateadded) {
        app.editinfo(id, name, status, quantity, minimum, delivery, desc, starbool, dateadded);
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

    //================================================================================
    // CALCULATIONS & ALGORITHM
    //================================================================================
    //This method determines which quantity should be set for the item.
    public String getNewQuan(String id, String mostrecentdate){
        int added = 0;
        Item rowinfo = getItem(id);
        Log mostrecentconsumption = new Log("", "", "", "");
        ObservableList<Log> loglist = rowinfo.getLogData();
        ObservableList<Log> dateloglist = FXCollections.observableArrayList();
        //If the date has any consumption-type log, that's the new quantity for sure.
        int length = loglist.size();
        //First, let's get a list of all the logs from that specific date.
        for (int i = 0; i < length; i++) {
            Log current = loglist.get(i);
            if ((current.getDateLogged()).equals(mostrecentdate)) {
                dateloglist.add(current);
            }
        }
        //Now, iterate through this dateloglist and see if there are any consumption-type logs.
        for (int j = 0; j < dateloglist.size(); j++) {
            Log current2 = dateloglist.get(j);
            if ((current2.getLogType()).equals("CONSUMPTION")) {
                return current2.getQuanLogged();
            }
        }
        //If we've reached this point, this most recent date only has REORDER-type logs.
        //We need to sort the loglist, putting the oldest logs at the front and newest at the front. 
        loglist = sortLogsByDate(loglist);

        //Iterate backwords until we find our most recent consumption-type log (which is guaranteed to exist.)
        int last = length - 1;
        for (int x = last; x >= 0; x--) {
            Log current3 = loglist.get(x);
            if ((current3.getLogType()).equals("CONSUMPTION")) {
                mostrecentconsumption = current3; 
                break;
            }
        }

        int initialquan = Integer.parseInt(mostrecentconsumption.getQuanLogged());
        String datetostop = mostrecentconsumption.getDateLogged();

        //Now we need to find all reorders AFTER THIS DATE and add it to initialquan.
        for (int y = last; y >= 0; y--) {
            Log reorderlog = loglist.get(y);
            if ((reorderlog.getDateLogged()).equals(datetostop)) {
                break;
            } else {
                //this must be a reorder log, so we need to get the quantity of it.
                String reorderquan = reorderlog.getQuanLogged();
                String stringquantity = reorderquan.substring(reorderquan.lastIndexOf('+') + 1); //everything after the plus sign, so just the number
                int intquantity = Integer.parseInt(stringquantity);
                System.out.println(intquantity);
                added = added + intquantity;
                //added now has a value of all the reorders, which is what we need to add to the LAST CONSUMPTION-TYPE LOG.
            }
        }

        //need to add initialquan to our added
        int finalquanint = initialquan + added;
        String finalquan = String.valueOf(finalquanint);

        return finalquan;
    }

    //This method takes in a list of logs and sorts it so that the most recent ones are at the end of the list.
    public static ObservableList<Log> sortLogsByDate(ObservableList<Log> loglist) {

        Collections.sort(loglist, new Comparator<Log>() {

            public int compare(Log log1, Log log2) {

                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                java.util.Date date1 = null;
                java.util.Date date2 = null;

                try {
                    date1=format.parse(log1.getDateLogged());
                    date2=format.parse(log2.getDateLogged());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                int firstcomp = date1.compareTo(date2);
                if (firstcomp != 0) {
                   return firstcomp;
                } 
                //If multiple logs share the same date, we need to then put the reorders first so that when we reverse, consumption is first.
                String type1 = log1.getLogType();
                String type2 = log2.getLogType();
                return -type1.compareTo(type2);
        }});

        return loglist;
    }

    //This method sets an item's quantity to be its most updated version.
    public void setUpdatedQuan(String id){

        ObservableList<Log>logData = getLogs(id);
        ArrayList<Integer> datelist = new ArrayList<>();

        //1. if the most recent log is a consumption-type, the item's quantity should be this one no matter what.
        //First, let's find the MOST RECENT date in this log list. 
        for (int i = 0; i < logData.size(); i++){
            String logdate = (logData.get(i)).getDateLogged(); //EX: 2019-02-27
            //Turn this date into an integer by removing the dashes. 
            logdate = logdate.replace("-", "");
            System.out.println(logdate);
            int dateint = Integer.parseInt(logdate);
            datelist.add(dateint);
        }
        //Finding the most recent is equivalent to finding the largest one of these numbers.
        int mostrecentint = getMax(datelist);
        String mostrecentstring = String.valueOf(mostrecentint); //EX: 20190227
        StringBuilder sb = new StringBuilder(mostrecentstring);
        sb.insert(4, "-"); //2019-0227
        sb.insert(7, "-"); //2019-02-27
        String finaldate = sb.toString();
        System.out.println(finaldate);

        Item item = getItem(id);

        System.out.println("Your id is " + id);
        System.out.println("Your most recent date is " + finaldate);

        //Here's a way where we can verify a log list is what we expect it to be.
        /*for (int i = 0; i < logData.size(); i++) {
            System.out.println((logData.get(i)).getDateLogged());
            System.out.println((logData.get(i)).getQuanLogged());
        }*/

        String newQuan = getNewQuan(id, finaldate);
        item.setQuantity(newQuan);

        //Item's Quantity is now changed. But we need to make the same change in the SQL database.
        editInfoTable(item.getID(), item.getName(), item.getStatus(), newQuan, item.getMinimum(), item.getDelivery(), item.getDesc(), 0, item.getDateAdded()); 

        //We also need to reset the table's data.
        setData(placeholder, 1);

    }

    //This method takes in a list of numbers (lon) and returns the max.
    public int getMax(ArrayList<Integer> lon){
        int max = Integer.MIN_VALUE;
        for(int i = 0; i < lon.size(); i++){
            if(lon.get(i) > max){
                max = lon.get(i);
            }
        }
        return max;
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

    //========================================================================================
    // ADC FORMULA: (Starting Inventory + Reordered Amount - Final Inventory) / Days Elapsed
    //========================================================================================
    public void getADC(String id) {
        //First, let's get our loglist, sort it, and remove the useless reorders.
        ObservableList<Log> loglist = getLogs(id);
        //If there's only one consumption-type log in our loglist, return 0 always.
        if (onlyOneConsumption(loglist)) {
            System.out.println("THERE IS ONLY ONE CONSUMPTION LOG, ADC IS 0.0.");
        }
        loglist = sortLogsByDate(loglist); //logs are now sorted from oldest to newest
        loglist = trimLogList(loglist); //all useless reorders removed.
        int length = loglist.size();
        Item currentitem = getItem(id);

        int startinv = getStartInv(loglist);
        System.out.println("Your starting inventory is " + startinv);

        int reorderamount = getReorderedAmount(loglist);
        System.out.println("Your total reordered amount is " + reorderamount);

        int finalinv = Integer.parseInt(currentitem.getQuantity());
        System.out.println("Your final inventory is " + finalinv);

        long daysbetween = getElapsedDays(loglist);
        System.out.println("The number of days elapsed is " + daysbetween);

        //Now that we have the four numbers we need, let's get our ADC.
        int numint = startinv + reorderamount - finalinv;
        double numerator = (double) numint; //casting to double.
        double adc = numerator / daysbetween;

        DecimalFormat newFormat = new DecimalFormat("#.###");
        adc =  Double.valueOf(newFormat.format(adc));

        System.out.println("Your ADC is " + adc);

    }

    //this method takes in an observablelist and trims it so that all useless reorder logs are removed. 
    //Reorder logs are considered useless if they are before the first consumption-type log.
    public ObservableList<Log> trimLogList(ObservableList<Log> loglist){
        int length = loglist.size();
        int index = 0;
        //Search through our loglist and find the first consumption log. 
        for (int i = 0; i < length; i++) {
            Log current = loglist.get(i);
            if ((current.getLogType()).equals("CONSUMPTION")) {
                index = i;
                break;
            }
        }

        ArrayList<Log> logAL = new ArrayList<Log>(loglist.subList(index, length));
        loglist = FXCollections.observableArrayList(logAL);
        //the length of the loglist has been changed so we need to factor that in.
        int length2 = loglist.size();

        System.out.println("ABOUT TO PRINT OUT THE TRIMMED LIST.");
        for (int x = 0; x < length2; x++) {
            System.out.println((loglist.get(x)).getDateLogged());
            System.out.println((loglist.get(x)).getQuanLogged());
        }

        return loglist;
    }

    //helper method that will get our starting inventory given a SORTED list of logs (oldest -> newest)
    public int getStartInv(ObservableList<Log> loglist) {
        //we just need to return oldest's quantity, which will be the first of our trimmed list.
        Log oldestconsumption = loglist.get(0);
        String startinvstr = oldestconsumption.getQuanLogged();
        int startinv = Integer.parseInt(startinvstr);
        return startinv;
    }

    //this method will take in a sorted loglist and calculate the total reordered amount for the item.
    public int getReorderedAmount(ObservableList<Log> loglist) {
        //Let's first extract all the reorder-type logs from our loglist and append them to a new one. 
        int reordertotal = 0;
        int length = loglist.size();
        ObservableList<Log> onlyreorders = FXCollections.observableArrayList();
        for (int i = 0; i < length; i++) {
            Log current = loglist.get(i);
            if ((current.getLogType()).equals("REORDER")) {
                onlyreorders.add(current);
            }
        }
        //Once extracted, we need to add up their number parts together.
        int reorderlength = onlyreorders.size();
        for (int j = 0; j < reorderlength; j++) {
            Log currentreorder = onlyreorders.get(j);
            String reorderquan = currentreorder.getQuanLogged();
            String stringquantity = reorderquan.substring(reorderquan.lastIndexOf('+') + 1); //everything after the plus sign, so just the number
            int intquantity = Integer.parseInt(stringquantity);
            reordertotal = reordertotal + intquantity;
        }

        return reordertotal;
    }

    //this method takes in a trimmed, sorted loglist and finds the number of days elapsed.
    public long getElapsedDays(ObservableList<Log> loglist) {
        int last = loglist.size() - 1;
        //We need to find our oldest and newest dates first.
        Log firstlog = loglist.get(0);
        Log lastlog = loglist.get(last);

        String firststring = firstlog.getDateLogged();
        String laststring = lastlog.getDateLogged();

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        java.util.Date firstdate = null;
        java.util.Date lastdate = null;

        try {
            firstdate=format.parse(firststring);
            lastdate=format.parse(laststring);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        long daysbetween = ChronoUnit.DAYS.between(firstdate.toInstant(), lastdate.toInstant());
        return daysbetween;
    }

    //this method determines if there is only one consumption-type log in a loglist.
    public boolean onlyOneConsumption(ObservableList<Log> loglist) {
        loglist = getConsumption(loglist);
        return (loglist.size()==1);
    }

    //method that updates final quantity, ADC, ROP, ROD all at once.
    public void updateEverything(String id){
        setUpdatedQuan(id);
        getADC(id);

    }

    //the purpose of this method is to take data from a .db database and load it into our code. 
    //two observablelists need to be created from the two tables in the database (ObservableList<Item>, ObservableList<Log>)

    public void loadData(){//ObservableList<Item> loadData(){
        //if .DS_Store exists, we're going to remove it completely. 
        File store = new File("./db/kettledb/.DS_Store");
        if (store.delete()) {
            System.out.println(".DS_Store has been deleted.");
        }

        File dir = new File("./db/kettledb");
        File[] dblist = dir.listFiles(); //store all databases in an array

        //if no files, exit function
        if (dblist.length==0){
            System.out.println("no files!");
            createNotifDB();
            data.add(empty); //if there's no files, add empty
            return;
        }

        boolean hasNotifDB = false;
        for (int i = 0; i < dblist.length; i++) { //loop through all dbs
            String dbName = dblist[i].getName();
            System.out.println("Loading data");
            System.out.println(dbName);

            if(dbName.equals(".gitignore")|| dbName.equals("notifications.db")){
                if(dbName.equals("notifications.db")){
                	hasNotifDB = true;
                }
                i++;
                if(i==dblist.length && data.size()==0){
                    System.out.println("no files!");
                    data.add(empty);
                    return;
                }
                if(i<dblist.length){
                    dbName = dblist[i].getName();
                    System.out.println(dbName);
                }
                else{
                    return;
                }
            }

            try{
                //connect to the db
                Connection conn = getDataBase(dbName);
                Statement stmt = conn.createStatement();

                String getMainData = "SELECT * FROM info"; //Call one table "Data", 2nd table "Log"
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
                /*String lastQuanLogged = logInfo.get(logInfo.size()-1).getQuanLogged();
                it.setQuantity(lastQuanLogged);*/

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
                System.out.println("loadData: "+e.getMessage());
            }
        }

        if(hasNotifDB==false){ //make notification db
        	createNotifDB();
        }
    }


}