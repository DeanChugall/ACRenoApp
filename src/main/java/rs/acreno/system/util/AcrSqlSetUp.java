package rs.acreno.system.util;

import rs.acreno.system.constants.Constants;
import rs.acreno.system.exeption.AcrenoException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class AcrSqlSetUp {

    static Connection conn = null;

    public static Connection connect()  {
        try {
            // db parameters
            Class.forName("net.ucanaccess.jdbc.UcanaccessDriver"); //BITNOOOO JER NECE DA RADI U JARu ako nije tu
            String url = Constants.MSACCESS_STRING_URL;

            // create a connection to the database
            conn = DriverManager.getConnection(url);

            System.out.println("Connection to SQLite has been established.");


    } catch (SQLException | ClassNotFoundException throwables) {
            throwables.printStackTrace();
        }
        return conn;
    }

    public static void close() throws SQLException {
        try {
            if (conn != null) {
                conn.close();
                System.out.println("ZATVORENA BAZA");
            }
        } catch (SQLException e) {
            conn.close();
        }
    }

    public static void main(String[] args) throws SQLException, AcrenoException {
        // createNewDatabase();
        //connect();
       // close();
        //creteTable(SqlQuerys.CREATE_TABLE_USLUGE);
    }


}
