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

public class FilterHandler implements ChangeListener<String>{

    private static Kettlelog kettle = new Kettlelog();
    private static FilterComparators filterObject = new FilterComparators(); //class will get data and table itself
    private static int filterSel = 0;

    @Override
    public void changed(ObservableValue ov, String oldValue, String newValue){

        switch(newValue){
            case "Starred":
                filterSel = 1;
                filterObject.sortByStarred();
                kettle.clearSearchBar();
                break;
            case "Most Recent":
                filterSel = 2;
                filterObject.sortByMostRecent(2);
                kettle.clearSearchBar();
                break;
            case "Oldest Added":
                filterSel = 3;
                filterObject.sortByMostRecent(3);
                kettle.clearSearchBar();
                break;
            case "None":
                kettle.clearSearchBar();
                filterSel = 0;
            default:
                filterSel = 0;
        }

    }
    
    public int getFilterSel(){
        //System.out.println("FilterSel is: "+filterSel);
        return filterSel;
    }

}