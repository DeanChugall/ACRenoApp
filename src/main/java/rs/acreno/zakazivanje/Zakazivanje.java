package rs.acreno.zakazivanje;

import com.sun.javafx.webkit.WebConsoleListener;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebEvent;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import rs.acreno.system.constants.Constants;

import java.util.prefs.BackingStoreException;

public class Zakazivanje extends Application {
    final
    @Override
    public void start(Stage primaryStage) throws BackingStoreException {
        System.setProperty("sun.net.http.allowRestrictedHeaders", "true");
        Constants constants = new Constants();
        WebView webView = new WebView();
        WebEngine webEngine = webView.getEngine();
        webView.getEngine().load(constants.PUTANJA_KALENDAR_ZAKAZIVANJE);
        webEngine.setUserAgent("AC Reno Web Browser 1.0 - AppleWebKit/555.99 JavaFX 8.0");
        webEngine.setOnAlert((WebEvent<String> wEvent) -> { // From https://stackoverflow.com/a/32682018
            System.out.println("JS alert() message: " + wEvent.getData() );
        });
        StackPane root = new StackPane();
        root.getChildren().add(webView);
        primaryStage.setScene(new Scene(root, 1200, 800));
        primaryStage.show();
    }

    public static void main(String[] args) {
        System.setProperty("sun.net.http.allowRestrictedHeaders", "true");
        launch(args);
    }
}
