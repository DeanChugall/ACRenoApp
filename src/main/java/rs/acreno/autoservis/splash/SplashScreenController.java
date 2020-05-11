package rs.acreno.autoservis.splash;

import javafx.application.Platform;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

public class SplashScreenController implements Initializable {


    @Override public void initialize(URL url, ResourceBundle resourceBundle) {
        Platform.runLater(() -> {
            System.out.println("I AM HEREE !!!!");

        });

    }


}

