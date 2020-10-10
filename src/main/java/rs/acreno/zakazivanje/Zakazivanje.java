package rs.acreno.zakazivanje;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import rs.acreno.system.constants.Constants;

public class Zakazivanje extends Application {
    @Override
    public void start(Stage primaryStage) {

        Constants constants = new Constants();
        WebView webView = new WebView();
        webView.getEngine().load(constants.PUTANJA_KALENDAR_ZAKAZIVANJE);
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
