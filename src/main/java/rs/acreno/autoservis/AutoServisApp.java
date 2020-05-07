package rs.acreno.autoservis;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;
import rs.acreno.system.constants.Constants;

import java.net.URL;
import java.util.ResourceBundle;

public class AutoServisApp extends Application implements Initializable {


    @Override
    public void start(@NotNull Stage stage) throws Exception {

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(AutoServisController.class.getResource(Constants.HOME_UI_VIEW_URI));
        // loader.setControllerFactory(t -> buildAppControler());

        stage.setTitle("ACReno Auto Servis");
        stage.setScene(new Scene(loader.load()));
        stage.setResizable(false);
        stage.show();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        System.out.println("Entering stop method in AutoServisApp");
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("initialize in AutoServisApp");
    }
}
