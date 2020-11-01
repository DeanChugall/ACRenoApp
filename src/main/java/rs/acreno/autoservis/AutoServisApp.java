package rs.acreno.autoservis;

import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import org.apache.commons.lang3.JavaVersion;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.SystemUtils;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import rs.acreno.autoservis.splash.SplashScreenController;
import rs.acreno.system.config.ConfigAcreno;
import rs.acreno.system.config.ConfigApp;
import rs.acreno.system.constants.Constants;
import rs.acreno.system.util.GeneralUiUtility;

import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.*;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public class AutoServisApp extends Application implements Initializable, Serializable {

    private static final long serialVersionUID = 7755070825881292183L;

    private static final Logger logger = Logger.getLogger(AutoServisApp.class);

    private final Timer t = new Timer();
    private TimerTask tt;
    private boolean isJustOpenApp = true;

    private final Preferences prefs = Preferences.userNodeForPackage(Constants.class);
    private final String[] sviKljuceviPreferenceNode = prefs.keys(); //Uzmi sve kljuceve iz prefa Noda

    private ConfigApp configApp;
    private ConfigAcreno configAcreno;

    public AutoServisApp() throws BackingStoreException {
    }

    @Override public void initialize(URL url, ResourceBundle resourceBundle) {

        boolean configAPPTrue = Arrays.asList(sviKljuceviPreferenceNode).contains(Constants.APP_CONFIG_NODE_KEY);

        if (configAPPTrue) { // Objekat "ConfigApp"
            configApp = SerializationUtils.deserialize(prefs.getByteArray(Constants.APP_CONFIG_NODE_KEY, null));
        }

        Platform.runLater(() -> {
        });
    }

    @Override
    public void start(@NotNull Stage stage) throws Exception {
        //prefs.removeNode();
        //System.exit(-1);
        if (SystemUtils.isJavaVersionAtLeast(JavaVersion.JAVA_11)) {
            if (sviKljuceviPreferenceNode.length == 0) {
                configApp = new ConfigApp();
                configApp.setSpashScreenAboutDelayAplikacije("4000");
                configApp.setDatumObjaveAplikacije("11.10.2020");
                //configApp.setImeBazePodatakaAplikacije("Database-ACReo-APP.accdb");
                //configApp.setPutanjaDoBazePodataka(configApp.getPutanjaDoBazePodataka());
                configApp.setImeFirme("");
                configApp.setIntervalProvereInternetaAplikacije("30");
                configApp.setLicencaAplikacije("Copyright @ 2020 AC Reno Inc. All rights reserved");
                configApp.setLicencaPodnozijaAplikacije("Copyright @ 2020 \"AC Reno\" Inc. All rights reserved under GNU GENERAL PUBLIC LICENSE  Version 3");
                configApp.setPutanjaDoGKalendara("http://calendar.google.com");
                configApp.setSpashScreenDelayAplikacije("400");
                configApp.setVerzijaAplikacije("Beta 1.4");
                byte[] data = SerializationUtils.serialize(configApp);
                prefs.putByteArray(Constants.APP_CONFIG_NODE_KEY, data);
                configAcreno = new ConfigAcreno();
                byte[] dataConfigAcreno = SerializationUtils.serialize(configAcreno);
                prefs.putByteArray(Constants.ACRENO_CONFIG_NODE_KEY, dataConfigAcreno);

                ButtonType OK = new ButtonType("Da", ButtonBar.ButtonData.OK_DONE);
                ButtonType CANCEL = new ButtonType("Odustani", ButtonBar.ButtonData.CANCEL_CLOSE);

                String text =
                        "Aplikacija je prvi put pokrenuta." + "\n" +
                                "Molimo Vas da popunite polja u konfiguracionom prozoru." + "\n" +
                                "Takođe isto tako možete da ih posle izmenite!" + "\n" +
                                "Putanja: Glavni Meni || APP Info || Konfiguracija";

                Alert alert = new Alert(Alert.AlertType.INFORMATION, text, OK, CANCEL);
                alert.setTitle("Obaveštenje!");
                alert.initStyle(StageStyle.UTILITY);
                alert.setHeaderText("*** Prvi Put Pokrenuta Aplikacija ***");
                Optional<ButtonType> result = alert.showAndWait();

                if (result.orElse(CANCEL) == OK) {
                    FXMLLoader fxmlLoaderConfig = new FXMLLoader(getClass().getResource(Constants.CONFIG_UI_VIEW_URI));
                    Stage stageConfig = new Stage();
                    stageConfig.initModality(Modality.APPLICATION_MODAL);
                    stageConfig.setResizable(false);
                    stageConfig.getIcons().add(new Image(AutoServisController.class.getResourceAsStream(Constants.APP_ICON)));
                    stageConfig.setTitle("Konfiguracija Aplikacije");
                    stageConfig.setScene(new Scene(fxmlLoaderConfig.load()));
                    stageConfig.showAndWait();

                    pokreniApp();
                    logger.info("Prvo Pokretanje APlikacije!");

                }
            } else {
                pokreniApp();
            }
        } else {
            System.out.println("Java version was 8 or greater!");
            String java_version = System.getProperty("java.version");
            String java_runtime_version = System.getProperty("java.runtime.version");
            String java_home = System.getProperty("java.home");
            String java_vendor = System.getProperty("java.vendor");
            String java_vendor_uri = System.getProperty("java.vendor.url");
            //String clasPath = ("CLAS PATH: " + System.getProperty("java.class.path"));
            GeneralUiUtility.alertDialogBox(Alert.AlertType.ERROR,
                    "Greška...",
                    "Greška JAVA !",
                    "Nemate noviju JAVU instaliranu na računaru.\n" +
                            java_version + "\n" +
                            java_runtime_version + "\n" +
                            java_home + "\n" +
                            java_vendor + "\n" +
                            java_vendor_uri);

            logger.error(" >>>>   GRESKA U start() NIJE DOBRA ! " + System.class.getName());
            System.exit(-1);
        }
    }


    private void pokreniApp() throws BackingStoreException, IOException {
        Constants constants = new Constants();
        Stage stageSpashScreen = new Stage();
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(AutoServisController.class.

                getResource(Constants.SPLASH_SCREEN_URI));
        stageSpashScreen.initStyle(StageStyle.UNDECORATED);
        stageSpashScreen.initStyle(StageStyle.TRANSPARENT);
        stageSpashScreen.setResizable(false);
        stageSpashScreen.getIcons().

                add(new Image(AutoServisController.class.getResourceAsStream(Constants.APP_ICON)));
        Scene scene = new Scene(loader.load());
        scene.setFill(Color.TRANSPARENT);
        stageSpashScreen.setScene(scene);
        stageSpashScreen.setResizable(true);

        // SplashScreenController INSTANCA
        SplashScreenController splashScreenController = loader.getController();
        splashScreenController.setAutoServisAppController(this, stageSpashScreen);

        final Task<Void> task = new Task<>() {
            final int N_ITERATIONS = 50;

            @Override
            protected @Nullable Void call() throws Exception {
                for (int i = 0; i < N_ITERATIONS; i++) {
                    updateProgress(i + 1, N_ITERATIONS);
                    // sleep is used to simulate doing some work which takes some time....
                    Thread.sleep(40);
                }

                return null;
            }
        };
        splashScreenController.getPrgBarLoadStaff().

                progressProperty().

                bind(task.progressProperty());
        // color the bar green when the work is complete.
        /*splashScreenController.getPrgBarLoadStaff().progressProperty().addListener(observable -> {
            if (splashScreenController.getPrgBarLoadStaff().getProgress() >= 1 - EPSILON) {
                splashScreenController.getPrgBarLoadStaff().setStyle("-fx-accent: forestgreen;");
            }
        });*/

        stageSpashScreen.show();

        //Start Thread
        final Thread thread = new Thread(task, "task-thread");
        thread.setDaemon(true);
        thread.start();

        //Load splash screen with fade in effect
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(1), stageSpashScreen.getScene().getRoot());
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.setCycleCount(1);
        fadeIn.setAutoReverse(true);

        //Finish splash with fade out effect
        FadeTransition fadeOut = new FadeTransition(Duration.seconds(1), stageSpashScreen.getScene().getRoot());
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.setCycleCount(1);
        fadeIn.play();

        //After fade in, start fade out
        fadeIn.setOnFinished((e) ->

        {
            tt = new TimerTask() {
                @Override
                public void run() {

                    if (isJustOpenApp) {
                        fadeOut.play();
                        isJustOpenApp = false;
                    }
                }
            };

            t.schedule(tt, constants.SPLASH_SCREEN_DELAY, 5000);
        });

        fadeOut.setOnFinished((e) ->
                Platform.runLater(new Runnable() {
                    @Override public void run() {
                        try {
                            FXMLLoader fxmlLoaderAutoServiApp =
                                    new FXMLLoader(getClass().getResource(Constants.HOME_UI_VIEW_URI));
                            Scene sceneAutoServisApp = new Scene(fxmlLoaderAutoServiApp.load());
                            Stage stageAutoServisApp = new Stage();
                            stageAutoServisApp.getIcons()
                                    .add(new Image(AutoServisController.class.
                                            getResourceAsStream(Constants.APP_ICON)));
                            stageAutoServisApp.setScene(sceneAutoServisApp);
                            stageAutoServisApp.setResizable(false);
                            stageAutoServisApp.setTitle("ACR Informacioni Sistem || \"AC Reno\" Auto Servis");
                            stageAutoServisApp.show();
                            tt.cancel();
                            t.purge();
                            stageSpashScreen.close();
                        } catch (IOException ioException) {
                            ioException.printStackTrace();
                        }
                    }
                }));
    }

    @Override
    public void stop() {
        System.exit(0);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
