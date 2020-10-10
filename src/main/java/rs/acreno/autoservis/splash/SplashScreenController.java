package rs.acreno.autoservis.splash;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.stage.Stage;
import org.apache.log4j.Logger;
import rs.acreno.autoservis.AutoServisApp;
import rs.acreno.system.constants.Constants;
import rs.acreno.system.util.properties.ApplicationProperties;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicReference;

public class SplashScreenController implements Initializable {

    private static final Logger logger = Logger.getLogger(SplashScreenController.class);

    @FXML private ProgressBar prgBarLoadStaff;
    @FXML private Label lblReleaseDate;
    @FXML private Label lblAppVersion;
    @FXML private Label lblCopyRight;
    @FXML private Label lblImeFirme;
    @FXML private AnchorPane aPaneSplashScreen;

    public ProgressBar getPrgBarLoadStaff() {
        return prgBarLoadStaff;
    }

    public void setPrgBarLoadStaff(ProgressBar prgBarLoadStaff) {
        this.prgBarLoadStaff = prgBarLoadStaff;
    }

   private Constants constants = new Constants();

    private final AtomicReference<Stage> stageSpashScreen = new AtomicReference<>();
    private final AtomicReference<AutoServisApp> autoServisAppController = new AtomicReference<>();

    public void setAutoServisAppController(AutoServisApp autoServisAppController, Stage stageSpashScreen) {
        this.autoServisAppController.set(autoServisAppController);
        this.stageSpashScreen.set(stageSpashScreen);
    }

    @Override public void initialize(URL url, ResourceBundle resourceBundle) {
        Platform.runLater(() -> {
            aPaneSplashScreen.setBackground(Background.EMPTY);
            //Get individual properties
            lblAppVersion.setText(constants.SPLASH_SCREEN_APP_VERION);
            lblReleaseDate.setText(constants.SPLASH_SCREEN_APP_DATE);
            lblCopyRight.setText(constants.SPLASH_SCREEN_APP_LICENCE);
            lblImeFirme.setText(constants.SPLASH_SCREEN_IME_FIRME);

            //All property names
            logger.info("********** PRISTUP APLIKACIJI **********");
        });
    }
}

