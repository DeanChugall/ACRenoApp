package rs.acreno.system.constants;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Constants {

    //DB Staf SQLite
    public static final String IME_BAZE = "Database-ACReo-APP.accdb";
    //private static final Path RESOURCE_DIRECTORY = Paths.get("src", "main", "resources", "db");
    private static final String RESOURCE_DIRECTORY = "src\\main\\resources\\AcrDB\\";

    public static final String ABSOLUTE_PATH = RESOURCE_DIRECTORY;

    public static final String MSACCESS_STRING_URL = "jdbc:ucanaccess://" + ABSOLUTE_PATH + IME_BAZE;

    //FX UIs Path
    public static final String HOME_UI_VIEW_URI = "/auto_servis_ui.fxml";
    public static final String AUTOMOBILI_UI_VIEW_URI = "/automobili.fxml";
    public static final String FAKTURA_UI_VIEW_URI = "/faktura_ui.fxml";
    public static final String PRINT_FAKTURA_UI_VIEW_URI = "/ui_print_racun.fxml";


    public static final String RADNI_NALOZI_UI_VIEW_URI = "/radni_nalozi_ui.fxml";
    public static final String KLIJENT_UI_VIEW_URI = "/klijent_ui.fxml";
    public static final String RADNI_NALOZI_EDIT_URI = "/radni_nalozi_edit_ui.fxml";
    public static final String DEFEKTAZA_UI_VIEW_URI = "/defektaza_ui.fxml";
    public static final String USLUGE_UI_VIEW_URI = "/usluge_ui.fxml";
    public static final String ARTIKLI_UI_VIEW_URI = "/artikli_ui.fxml";
}
