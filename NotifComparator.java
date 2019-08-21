import Notif.*;
import java.util.*;

public class NotifComparator implements Comparator<Notif>{
    @Override
    public int compare(Notif c1, Notif c2){

        String c1Date = c1.getDateGenerated();
        String c2Date = c2.getDateGenerated();

        //Split date string into an array: year, month, day
        String[] c1Split = c1Date.split("-");
        String[] c2Split = c2Date.split("-");

        int[] c1Dates = new int[3];
        int[] c2Dates = new int[3];

        //Convert String dates to integer
        for(int i = 0; i<3; i++){
            try {
               c1Dates[i] = Integer.parseInt(c1Split[i]);
               c2Dates[i] = Integer.parseInt(c2Split[i]);
            }
            catch (NumberFormatException e){
               c1Dates[i] = 0;
               c2Dates[i] = 0;
               System.out.println("ERROR");
            }

            //Compare each element in the array only if they are different
            //ie. if yr1==yr2, then don't return and instead compare the next element
            if (c1Dates[i] != c2Dates[i]){
                return Integer.valueOf(c1Dates[i]).compareTo(Integer.valueOf(c2Dates[i]));
            }

        }
        //if the for loop has not returned anything, 2 dates are the same. return any random comparison
        return Integer.valueOf(c1Dates[0]).compareTo(Integer.valueOf(c2Dates[0]));       
    }
}