package application;
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

    @Override
    public void changed(ObservableValue ov, String oldValue, String newValue){
        FilterComparators filterObject = new FilterComparators(data, table);

        switch(newValue){
            case "Starred":
                filterSel = 1;
                filterObject.sortByStarred();
                searchbar.clear();
                break;
            case "Most Recent":
                filterSel = 2;
                filterObject.sortByMostRecent(2);
                searchbar.clear();
                break;
            case "Oldest Added":
                filterSel = 3;
                filterObject.sortByMostRecent(3);
                searchbar.clear();
                break;
            case "None":
                searchbar.clear();
                filterSel = 0;
            default:
                filterSel = 0;
        }

    }
}