package dao;

import org.junit.Test;
import rs.acreno.system.constants.Constants;

import java.sql.Connection;
import java.sql.DriverManager;

import static org.junit.Assert.assertEquals;

public class DatabaseTest {
    Connection conn = null;

    @Test
    public void setup() {

        String url = Constants.MSACCESS_STRING_URL;
        String expextedUrl = "jdbc:sqlite:/home/datatab/Desktop/PROGRAMIRANJE/PROJEKTI/ACReno/src/main/resources/db/acreno.db";
        try {
            Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
            conn = DriverManager.getConnection(url);
           // assertEquals(url, expextedUrl);
            conn.close();

        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        if (conn == null) {
            System.out.println("Connection is Closed !");
        } else {
            System.out.println("Connection is OPEN !");
        }
        System.out.println("Opened database successfully");
        if (conn == null) {
            System.out.println("Connection is Closed !");
        } else {
            System.out.println("Connection is OPEN !");
        }
    }

    @Test
    public void connect() {
        if (conn == null) {
            System.out.println("Connection is Closed !");
        } else {
            System.out.println("Connection is OPEN !");
        }
    }
}



