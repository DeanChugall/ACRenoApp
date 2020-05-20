package rs.acreno.system.util;

import org.apache.log4j.Logger;
import rs.acreno.autoservis.AutoServisApp;
import rs.acreno.system.constants.Constants;
import rs.acreno.system.exeption.AcrenoException;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class AcrSqlSetUp {

    private static final Logger logger = Logger.getLogger(AcrSqlSetUp.class);

    static Connection conn = null;

    public static Connection connect() {
        try {
            String url = Constants.MSACCESS_STRING_URL;
            logger.debug("URL DATABASE: " + url);
            // db parameters
            Class.forName("net.ucanaccess.jdbc.UcanaccessDriver"); //BITNOOOO JER NECE DA RADI U JARu ako nije tu
            // create a connection to the database
            conn = DriverManager.getConnection(url);
            logger.debug("Connection to SQLite has been established.");

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
}
