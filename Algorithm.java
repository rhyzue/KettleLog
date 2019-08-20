import Log.*;
import Item.*;
import java.io.*;
import java.sql.*;
import java.text.*;
import java.util.*;
import javafx.stage.*;
import java.nio.file.*;
import java.sql.Statement;
import javafx.scene.Scene;
import java.sql.Connection;
import javafx.collections.*;
import java.sql.SQLException;
import java.sql.DriverManager;
import java.time.temporal.ChronoUnit;
import javafx.application.Application;

public class Algorithm {
 
    private static Kettlelog kettle = new Kettlelog();
 	private static ObservableList<Item> data = kettle.getData();
	private static ObservableList<Item> placeholder = FXCollections.observableArrayList();

    Algorithm(){
    }

    //================================================================================
    // CALCULATIONS & ALGORITHM
    //================================================================================

    //this method gets the item of a corresponding ID
    public Item getItem(String id){
        for (int i = 0; i < data.size(); i++){
            //when we eventually find a match, return that item
            if(data.get(i).getID().equals(id)){
                return data.get(i);
            }
        }
        return null;
    }

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

    //This method sets an item's quantity to be its most updated version.
    public void setUpdatedQuan(String id){

        ObservableList<Log>logData = kettle.getLogs(id);
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
        kettle.editInfoTable(item.getID(), item.getName(), item.getStatus(), newQuan, item.getMinimum(), item.getDelivery(), item.getDesc(), 0, item.getDateAdded()); 

        //We also need to reset the table's data.
        kettle.setData(placeholder, 1);

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

    //========================================================================================
    // ADC FORMULA: (Starting Inventory + Reordered Amount - Final Inventory) / Days Elapsed
    //========================================================================================

    public void getADC(String id) {
        //First, let's get our loglist, sort it, and remove the useless reorders.
        ObservableList<Log> loglist = kettle.getLogs(id);
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
        loglist = kettle.getConsumption(loglist);
        return (loglist.size()==1);
    }
}

