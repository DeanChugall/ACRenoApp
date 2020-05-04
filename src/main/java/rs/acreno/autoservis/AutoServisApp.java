package rs.acreno.autoservis;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;
import rs.acreno.system.constants.Constants;

public class AutoServisApp extends Application {


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
}
