package rs.acreno.autoservis.splash;

import javafx.application.Platform;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;

import java.net.URL;
import java.util.ResourceBundle;

public class SplashScreenController implements Initializable {


    public AnchorPane aPaneSplashScreen;

    @Override public void initialize(URL url, ResourceBundle resourceBundle) {
        Platform.runLater(() -> {
            System.out.println("I AM HEREE !!!!");
            aPaneSplashScreen.setBackground(Background.EMPTY);

        });

    }


}

