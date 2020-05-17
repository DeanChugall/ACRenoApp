package rs.acreno.autoservis.splash;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import org.apache.log4j.Logger;
import rs.acreno.system.constants.Constants;
import rs.acreno.system.util.properties.AcrenoProperties;
import rs.acreno.system.util.properties.ApplicationProperties;

import java.net.URL;
import java.util.ResourceBundle;

public class SplashScreenController implements Initializable {

    private static final Logger logger = Logger.getLogger(SplashScreenController.class);


    @FXML private  Label lblReleaseDate;
    @FXML private  Label lblAppVersion;
    @FXML private AnchorPane aPaneSplashScreen;


    @Override public void initialize(URL url, ResourceBundle resourceBundle) {
        Platform.runLater(() -> {
            aPaneSplashScreen.setBackground(Background.EMPTY);
            //Get individual properties
            lblAppVersion.setText(ApplicationProperties.getInstance().getProperty("app.version"));
            lblReleaseDate.setText(ApplicationProperties.getInstance().getProperty("app.date"));
            System.out.println(ApplicationProperties.getInstance().getProperty("app.date"));
            System.out.println(ApplicationProperties.getInstance().getProperty("app.name"));

            //All property names
            System.out.println(ApplicationProperties.getInstance().getAllPropertyNames());
            System.out.println("**********************************");
            System.out.println(Constants.MSACCESS_STRING_URL);
            System.out.println("**********************************");
            logger.error("********** PRISTUP APLIKACIJI **********");

        });
    }
}

