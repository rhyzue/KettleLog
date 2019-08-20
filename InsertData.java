import Log.*;
import Item.*; 
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.SQLException;
 
public class InsertData {
 
    private static Kettlelog kettle = new Kettlelog();

    InsertData(){
    }

    public void insertinfo(String id, String name, String status, String quantity, String minimum, String delivery, String desc, int starbool, String dateadded, String adc) {

        String command = "INSERT INTO info(id, name, status, quantity, minimumstock, deliverytime, description, starred, dateadded, adc) VALUES(?,?,?,?,?,?,?,?,?,?)";


        try {
            //Connect to our database, so then we can access the tables in there directly
            String filename = id + ".db";
            Connection conn = kettle.getDataBase(filename);
            PreparedStatement v = conn.prepareStatement(command);

            //Setting our insertion values here.
                v.setString(1, id);
                v.setString(2, name);
                v.setString(3, status);
                v.setString(4, quantity);
                v.setString(5, minimum);
                v.setString(6, delivery);
                v.setString(7, desc);
                v.setInt(8, starbool);
                v.setString(9, dateadded);
                v.setString(10, adc);

            v.executeUpdate();

            v.close();
            conn.close();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    //We need the name parameter here to specify which database to connect to.
    public void insertlogs(String itemid, String logid, String logtype, String logdate, String logquan) {

        String command = "INSERT INTO log(logid, logtype, logdate, logquan) VALUES(?,?,?,?)";

        try {
            String filename = itemid + ".db";
            Connection conn = kettle.getDataBase(filename);
            PreparedStatement v = conn.prepareStatement(command);

            //Setting our insertion values here.
                v.setString(1, logid);
                v.setString(2, logtype);
                v.setString(3, logdate);
                v.setString(4, logquan);

            v.executeUpdate();

            v.close();
            conn.close();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }

    //This is a method that edits the infotable for a database. 
    public void editinfo(String id, String name, String status, String quantity, String minimum, String delivery, String desc, int starbool, String dateadded, String adc) {

        String command = "UPDATE info SET name = ?, "
                + "status = ?, "
                + "quantity = ?, "
                + "minimumstock = ?, "
                + "deliverytime = ?, "
                + "description = ?, "
                + "starred = ?, "
                + "dateadded = ?, "
                + "adc = ? "
                + "WHERE id = " + id;

        try {
            String filename = id + ".db";
            Connection conn = kettle.getDataBase(filename);
            PreparedStatement v = conn.prepareStatement(command);

           //Editing the values of our row.
                v.setString(1, name);
                v.setString(2, status);
                v.setString(3, quantity);
                v.setString(4, minimum);
                v.setString(5, delivery);
                v.setString(6, desc);
                v.setInt(7, starbool);
                v.setString(8, dateadded);
                v.setString(9, adc);

            v.executeUpdate();

            v.close();
            conn.close();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }         
    }

    //This is a method that deletes a log from logtable for a database. 
    public void removeLog(String id, String logid) {

        String command = "DELETE FROM log WHERE logid = ?";

        try {
            String filename = id + ".db";
            Connection conn = kettle.getDataBase(filename);
            PreparedStatement v = conn.prepareStatement(command);

            v.setString(1, logid);
            v.executeUpdate();

            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }         
    }

    public void addNotification(String message, String itemId, int readStatus, String notifId){

        String command = "INSERT INTO notifData(message, itemId, readStatus, notifId) VALUES(?,?,?,?)";

        try {
            String filename = "notifications.db";
            Connection conn = kettle.getDataBase(filename);
            PreparedStatement v = conn.prepareStatement(command);

            //Setting our insertion values here.
                v.setString(1, message);
                v.setString(2, itemId);
                v.setInt(3, readStatus);
                v.setString(4, notifId);

            v.executeUpdate();

            v.close();
            conn.close();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }
}
