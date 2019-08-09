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

public class OptionHandler implements ChangeListener<String>{

    private static Kettlelog kettle = new Kettlelog();
    private static OptionComparators object = new OptionComparators(); //class will get data and table itself
    private static int optionSel = 0;

    @Override
    public void changed(ObservableValue ov, String oldValue, String newValue){

        switch(newValue){
            case "Sort by: Starred":
                optionSel = 1;
                object.sortByStarred();
                kettle.clearSearchBar();
                break;
            case "Sort by: Most Recent":
                optionSel = 2;
                object.sortByMostRecent(2);
                kettle.clearSearchBar();
                break;
            case "Sort by: Oldest Added":
                optionSel = 3;
                object.sortByMostRecent(3);
                kettle.clearSearchBar();
                break;
            case "Select All":
                kettle.setAllChecked(true);
                kettle.primaryStage.updatePrimaryStage(kettle.getData());
                kettle.primaryStage.resetComboBox();
                break;
            case "None":
                kettle.primaryStage.resetComboBox();
                kettle.clearSearchBar();
                optionSel = 0;
            default:
                optionSel = 0;
        }

    }
    
    public int getOptionSel(){
        return optionSel;
    }

}