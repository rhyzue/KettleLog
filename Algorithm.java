import Log.*;
import Item.*;
import java.io.*;
import java.sql.*;
import java.text.*;
import java.util.*;
import javafx.stage.*;
import java.lang.Math;
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
    // HELPERS
    //================================================================================
    public Item getItem(String id){
        for (int i = 0; i < data.size(); i++){
            //when we eventually find a match, return that item
            if(data.get(i).getID().equals(id)){
                return data.get(i);
            }
        }
        return null;
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

    //================================================================================
    // UPDATERS
    //================================================================================
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
        kettle.editInfoTable(item.getID(), item.getName(), item.getStatus(), newQuan, 
            item.getMinimum(), item.getDelivery(), item.getDesc(), 0, item.getDateAdded(), 
            item.getADC(), item.getROP(), item.getROD()); 

        //We also need to reset the table's data.
        kettle.setData(placeholder, 1);

    }

    //This method will set the status of an item to be its most recent version.
    public void setUpdatedStatus(String id) {
        Item item = getItem(id);
        String status = getUpdatedStatus(id);
        System.out.println("your new status is " + status);

        //Changing the item's status using the setter.
        item.setStatus(status);

        //Making the same change in the SQL Database.
        kettle.editInfoTable(item.getID(), item.getName(), status, item.getQuantity(), 
            item.getMinimum(), item.getDelivery(), item.getDesc(), 0, 
            item.getDateAdded(), item.getADC(), item.getROP(), item.getROD()); 

    }

    //method that will get the most updated status according to an algorithm
    public String getUpdatedStatus(String id) {

        String status = "";
        Item item = getItem(id);
        String reorderpointstr = item.getROP();
        String safetystr = item.getMinimum();
        String currentquanstr = item.getQuantity();
        String adcstr = item.getADC();

        //check if we even know the reorder point first.
        if (reorderpointstr.equals("N/A")) {
            status = "More Info Needed";
            return status;
        }

        int reorder = Integer.parseInt(reorderpointstr); 
        int safety = Integer.parseInt(safetystr);
        int current = Integer.parseInt(currentquanstr);
        double adc = Double.parseDouble(adcstr);

        System.out.println("You currently have " + current);
        System.out.println("Your minimum is " + safety);
        System.out.println("Your reorder point is " + reorder);

        //1. Two weeks time before reorder point is hit = VERY GOOD
        //2. One weeks time before reorder point is hit = GOOD
        //3. 3-6 days before reorder point is hit = MODERATE.
        //4. 1-2 days before reorder point is hit = REORDER SOON
        if (current > reorder) {
            int i = current - reorder;
            System.out.println("Number between current and reorder is + " + i);
            double daysbetween = (double) i;
            double buffer = daysbetween / adc;
            //int days = (int) Math.round(between);
            System.out.println("Number of days before reorderpoint is hit is + " + buffer);

            if (buffer >= 14.0) {
                status = "Very Good";
                return status;
            } else if (buffer >= 7.0) {
                status = "Good";
                return status;
            } else if (buffer > 2.0) {
                status = "Moderate";
                return status;
            } else {
                status = "Reorder Soon";
                return status;
            }
        }

        else {
            status = "REORDER NEEDED";
            return status;
        }
    }

    //====================================================================================
    //CALCULATIONS: ADC, ROP, ROD
    //====================================================================================
    public void setCalculations(String id) {
        Item item = getItem(id);

        String[] calcinfo = getCalculations(id);
        String adc = calcinfo[0];
        String rop = calcinfo[1];
        String rod = calcinfo[2];

        System.out.println("Your ADC is " + adc);
        System.out.println("Your reorder point is " + rop);
        System.out.println("Your reorder date is " + rod);

        //changing the ADC of our item.
        item.setADC(adc);
        item.setROP(rop);
        item.setROD(rod);

        //Item's ADC, ROP, and ROD is now changed. But we need to make the same change in the SQL database.
        kettle.editInfoTable(item.getID(), item.getName(), item.getStatus(), 
            item.getQuantity(), item.getMinimum(), item.getDelivery(), 
            item.getDesc(), 0, item.getDateAdded(), adc, rop, rod); 
    }

    //This method will return an array of 3 strings. 
    //[ADC, REORDER POINT, REORDER DATE] will the order of the indices of this array.
    public String[] getCalculations(String id) {

        //========================================================================================
        // INITIALIZATION
        //========================================================================================
        String[] calcs = new String[3];
        //First, let's get our loglist, sort it, and remove the useless reorders.
        ObservableList<Log> loglist = kettle.getLogs(id);
        ObservableList<Log> onlyconsumption = kettle.getConsumption(loglist);
        int numOfCons = onlyconsumption.size();

        loglist = sortLogsByDate(loglist); //logs are now sorted from oldest to newest
        loglist = trimLogList(loglist); //all useless reorders removed.
        int length = loglist.size();
        Item currentitem = getItem(id);
        double adc = 0.0;

        int shipping = Integer.parseInt(currentitem.getDelivery());
            System.out.println("Your shipping time is " + shipping);

        int minimum = Integer.parseInt(currentitem.getMinimum());
            System.out.println("Your safety stock is " + minimum);

        int startinv = getStartInv(loglist);
            System.out.println("Your starting inventory is " + startinv);

        int reorderamount = getReorderedAmount(loglist);
            System.out.println("Your total logged reordered amount is " + reorderamount);

        int finalinv = Integer.parseInt(currentitem.getQuantity());
            System.out.println("Your final inventory is " + finalinv);

        long daysbetween = getElapsedDays(loglist);
            System.out.println("The number of days elapsed is " + daysbetween);

        int unloggedincrease = getUnloggedReorderAmount(loglist);
            System.out.println("Your unlogged increase amount is " + unloggedincrease);

        //========================================================================================
        // ADC FORMULA: (Starting Inventory + Reordered Amount - Final Inventory) / Days Elapsed
        //========================================================================================
        if (onlyOneConsumption(loglist)) {
            //If there's only one consumption-type log in our loglist, return 0 always.
            calcs[0] = "0.0";

        } else {

            int numint = startinv + (reorderamount + unloggedincrease) - finalinv;
            double numerator = (double) numint; //casting to double.
            adc = numerator / daysbetween;

            DecimalFormat newFormat = new DecimalFormat("#.###");
            adc =  Double.valueOf(newFormat.format(adc));
            String adcstring = String.valueOf(adc);

            calcs[0] = adcstring;
        }
           
        //========================================================================================
        // REORDER POINT: (ADC X SHIPPING TIME) + MINIMUM
        //========================================================================================
        //If there are less than 3 logs, we don't have enough data for a reorder point.
        if (numOfCons < 3) {
            System.out.println("You have less than 3 non-reorder type logs.");
            String moreinfo = "N/A";
            calcs[1] = moreinfo;

        } else {
            double adcShipProduct = adc * shipping;
            int adcShipInt = (int) Math.round(adcShipProduct);
            int rop = adcShipInt + minimum;
            String ropstring = Integer.toString(rop);

            calcs[1] = ropstring;
        }

        //========================================================================================
        // REORDER DATE = TODAY'S DATE + [(QUANTITY - MINIMUM) / ADC] - SHIPPING TIME
        //========================================================================================
            int quanMinDiff = finalinv - minimum;
                System.out.println("Quantity - Minimum = " + quanMinDiff);

            double quotient = quanMinDiff / adc; 
                System.out.println("Quotient Double = " + quotient);

            int quotientint = (int) Math.ceil(quotient);
                System.out.println("Quotient Rounded = " + quotientint);

            int addedDays = quotientint - shipping;
                System.out.println("Added Days = " + addedDays);

        if (addedDays < 0) {
            //if added days is less than 0, the user has already missed their reorder date.
            String rodstring = "OVERDUE";
            calcs[2] = rodstring;
            System.out.println("OVERDUE DATE!");
        } 

        //Cannot divide by 0, and non-reorder type logs should be 3 or greater.
        else if ((adc == 0) || (numOfCons < 3)) {
            String notapplicable = "N/A";
            calcs[2] = notapplicable;
        }

        else {

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            java.util.Date today = new java.util.Date();
            String todayString = dateFormat.format(today);
            System.out.println("Today's date is " + todayString);

            Calendar cal = Calendar.getInstance();

            try {
                java.util.Date futuredate = dateFormat.parse(todayString);
                cal.setTime(futuredate);
            } catch (ParseException e) {
                System.out.println("Something went wrong when trying to set calendar.");
                e.printStackTrace();
            }

            // Here is where we can add the number of days found earlier.
            cal.add(Calendar.DATE, addedDays);  
            String rodstring = dateFormat.format(cal.getTime());  

            calcs[2] = rodstring;

        }

      return calcs;
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

    //We need to figure out a way to deal with un-logged reorders that result in a random increase in quantity.  
    public int getUnloggedReorderAmount(ObservableList<Log> loglist){
    	int total = 0;
    	int unloggedamount = 0;
    	int sumofreorders = 0;
    	//the first thing we need to do is locate the indices of the consumption logs in our list. 
    	ArrayList<Integer> indiceslist = new ArrayList<>();
    	int length = loglist.size();
    	for (int i = 0; i < length; i++) {
    		Log current = loglist.get(i);
    		if (current.getLogType().equals("CONSUMPTION")) {
    			indiceslist.add(i);
    		}
    	}
    	System.out.println("Indices of Consumption Logs: " + indiceslist);
    	int numofcalc = indiceslist.size() - 1; //how many times we need to perform our mini-algorithm

    	for (int x = 0; x < numofcalc; x++) {

    		int firstindex = indiceslist.get(x);
    		//System.out.println("first index is " + firstindex);
    		int nextindex = indiceslist.get(x + 1);
    		//System.out.println("next index is " + nextindex);

    		Log firstlog = loglist.get(firstindex);
    		Log nextlog = loglist.get(nextindex);

    		int firstquan = Integer.parseInt(firstlog.getQuanLogged());
    		//System.out.println(firstquan);
    		int nextquan = Integer.parseInt(nextlog.getQuanLogged());
    		//System.out.println(nextquan);

    		//the next quantity is lower, so this is not a problem. 
    		if (firstquan >= nextquan) {
    			continue;
    		}
    		//need to consider the reorders in between these two.
    		for (int j = firstindex + 1; j < nextindex; j++) {
    			Log currentreorder = loglist.get(j);
    			String reorderquan = currentreorder.getQuanLogged();
	            String stringquantity = reorderquan.substring(reorderquan.lastIndexOf('+') + 1); //everything after the plus sign, so just the number
	            int intquantity = Integer.parseInt(stringquantity);
	            sumofreorders = sumofreorders + intquantity;
    		}
    		//if the first quantity and the sum of its quantity and all the reorders summed up between the first and next log is greater, we are still good
    		if ((firstquan + sumofreorders) >= nextquan) {
    			sumofreorders = 0;
    			continue;
    		}
    		//if we've reached this point there is an unlogged increase, and we must calculate it.
    		else {
    			unloggedamount = nextquan - (firstquan + sumofreorders);
    			total = total + unloggedamount;
    			sumofreorders = 0;
    			unloggedamount = 0;
    		}
    	}

    	return total;
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

