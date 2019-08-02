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


public class FilterComparators extends Kettlelog{ 

    public ObservableList<Columns> data;
    public TableView<Columns> table;

    public FilterComparators(ObservableList<Columns> data, TableView<Columns> table){
        this.data = data;
        this.table = table;
    }

    //Methods
    public void sortByMostRecent(int order){
        DateComparator comp = new DateComparator();
        if(order==2){
            data.sort(comp.reversed());
        }
        else if (order==3){
            data.sort(comp);
        }
        table.setItems(data);
    }

    public void sortByStarred(){
        StarComparator comp = new StarComparator();
        data.sort(comp.reversed());
        table.setItems(data);
    }


    //Classes
    public class DateComparator implements Comparator<Columns>{
        @Override
        public int compare(Columns c1, Columns c2){

            String c1Date = c1.getDateAdded();
            String c2Date = c2.getDateAdded();

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

    public class StarComparator implements Comparator<Columns> {
        @Override
        public int compare(Columns c1, Columns c2) {
            return Boolean.valueOf(c1.getStarred()).compareTo(Boolean.valueOf(c2.getStarred()));
        }
    }
}

