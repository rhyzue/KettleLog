import Log.*;
import Item.*; 
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
 
public class InsertData {
 
    private static Kettlelog kettle = new Kettlelog();

    InsertData(){
    }

    public void insertinfo(String name, String status, String minimum, String delivery, String desc, int starbool, String dateadded) {

        String command = "INSERT INTO info(name, status, minimumstock, deliverytime, description, starred, dateadded) VALUES(?,?,?,?,?,?,?)";


        try {
            //Connect to our database, so then we can access the tables in there directly
            String filename = name + ".db";
            Connection conn = kettle.getDataBase(filename);
            PreparedStatement v = conn.prepareStatement(command);

            //Setting our insertion values here.
                v.setString(1, name);
                v.setString(2, status);
                v.setString(3, minimum);
                v.setString(4, delivery);
                v.setString(5, desc);
                v.setInt(6, starbool);
                v.setString(7, dateadded);

            v.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void insertlogs(String name, String logdate, String logquan) {

        String command = "INSERT INTO log(logdate, logquan) VALUES(?,?)";

        try {

            String filename = name + ".db";
            Connection conn = kettle.getDataBase(filename);
            PreparedStatement v = conn.prepareStatement(command);

            //Setting our insertion values here.
                v.setString(1, logdate);
                v.setString(2, logquan);

            v.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }
}