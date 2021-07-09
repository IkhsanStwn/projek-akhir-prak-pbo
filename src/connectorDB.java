import javax.swing.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
//menghubungkan dengan database

public class connectorDB {
    String DBurl = "jdbc:mysql://localhost/tokobuku";
    String DBusername = "root";
    String DBpassword = "";
    Connection koneksi;
    Statement statement;

    public connectorDB(){
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            koneksi = DriverManager.getConnection(DBurl,DBusername,DBpassword);
            System.out.println("berhasil koneksi database!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Koneksi Database Gagal!", "Peringatan", JOptionPane.ERROR_MESSAGE);
        }
    }
}
