package rs.acreno.autoservis;

import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import org.jetbrains.annotations.NotNull;
import rs.acreno.system.constants.Constants;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class AutoServisApp extends Application {

    private final Timer t = new Timer();
    private TimerTask tt;
    private boolean isJustOpenApp = true;


    @Override
    public void start(@NotNull Stage stage) throws Exception {

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(AutoServisController.class.getResource(Constants.SPLASH_SCREEN_URI));
        stage.initStyle(StageStyle.UNDECORATED);
        stage.initStyle(StageStyle.TRANSPARENT);

        Scene scene = new Scene(loader.load());
        scene.setFill(Color.TRANSPARENT);

        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
        //Load splash screen with fade in effect
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(1), stage.getScene().getRoot());
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.setCycleCount(1);

        fadeIn.setAutoReverse(true);

        //Finish splash with fade out effect
        FadeTransition fadeOut = new FadeTransition(Duration.seconds(1), stage.getScene().getRoot());
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.setCycleCount(1);
        fadeIn.play();

        //After fade in, start fade out
        fadeIn.setOnFinished((e) -> {
            tt = new TimerTask() {
                @Override
                public void run() {

                    if (isJustOpenApp) {
                        fadeOut.play();
                        isJustOpenApp = false;
                    }
                }
            };
            t.schedule(tt, 500, 5000);
        });

        fadeOut.setOnFinished((e) -> Platform.runLater(new Runnable() {
            @Override public void run() {
                try {
                    Parent root = FXMLLoader.load(getClass().getResource(Constants.HOME_UI_VIEW_URI));
                    Scene scene = new Scene(root);
                    Stage stage = new Stage();
                    stage.setScene(scene);
                    stage.setTitle("ACR Informacioni Sistem || ACReno auto Servis");
                    stage.show();
                    tt.cancel();
                    t.purge();

                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        }));
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
