import Item.*;
import javafx.util.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;


public class BlankCell extends TableCell<Item, String> implements Callback<TableColumn<Item, String>, TableCell<Item, String>> {

    @Override
    public TableCell call(final TableColumn<Item, String> param) {
        //System.out.println("===============hereCell");

        return new TableCell<Item, String>() {

            @Override
            public void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(null);
                setText(null);
                
            }
            
        };
    }
}