package rs.acreno.system.constants;

import rs.acreno.autoservis.splash.SplashScreenController;
import rs.acreno.system.util.properties.ApplicationProperties;

import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Constants {

    //DB Staf SQLite
    public static final String IME_BAZE = ApplicationProperties.getInstance().getProperty("database.name");
    private static final String RESOURCE_DIRECTORY = ApplicationProperties.getInstance().getProperty("database.dir");
    private static final String RESOURCE_DIRECTORY_DEVELOP = ApplicationProperties.getInstance().getProperty("database.dir.develop");

    public static final String MSACCESS_STRING_URL = "jdbc:ucanaccess://" + RESOURCE_DIRECTORY_DEVELOP + IME_BAZE;

    //FX UIs Path
    public static final String SPLASH_SCREEN_URI = "/splash_screen.fxml";
    public static final String HOME_UI_VIEW_URI = "/auto_servis_ui.fxml";
    public static final String AUTOMOBILI_UI_VIEW_URI = "/automobili.fxml";
    public static final String FAKTURA_UI_VIEW_URI = "/faktura_ui.fxml";
    public static final String PRINT_FAKTURA_UI_VIEW_URI = "/ui_print_racun.fxml";
    public static final String RADNI_NALOZI_UI_VIEW_URI = "/radni_nalozi_ui.fxml";
    public static final String PRINT_RADNI_NALOG_UI_VIEW_URI = "/ui_print_nalozi.fxml";
    public static final String DEFEKTAZA_UI_VIEW_URI = "/defektaza_ui.fxml";
    public static final String PRINT_DEFEKTAZA_UI_VIEW_URI = "/ui_print_defektaza.fxml";

//public static final String APP_ICON = ApplicationProperties.getInstance().getProperty("app.icon");
public static final String APP_ICON = "/faktura/logo-acreno.jpg";

    public static final String KLIJENT_UI_VIEW_URI = "/klijent_ui.fxml";
    public static final String RADNI_NALOZI_EDIT_URI = "/radni_nalozi_edit_ui.fxml";
    public static final String USLUGE_UI_VIEW_URI = "/usluge_ui.fxml";
    public static final String ARTIKLI_UI_VIEW_URI = "/artikli_ui.fxml";
}
