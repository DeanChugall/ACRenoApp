package rs.acreno.autoservis.splash;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Background;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import rs.acreno.system.constants.Constants;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class SplashScreenController implements Initializable {

    private static final int shadowSize = 50;
    public StackPane stackPaneSplashScreen;

    @Override public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("I AM HEREE !!!!");
       /* Platform.runLater(() -> {
            stackPaneSplashScreen.setStyle("-fx-background-color: transparent;");

        });*/

    }



}

