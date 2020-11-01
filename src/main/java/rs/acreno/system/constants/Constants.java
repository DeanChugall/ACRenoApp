package rs.acreno.system.constants;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import org.apache.commons.lang3.SerializationUtils;
import rs.acreno.system.config.ConfigAcreno;
import rs.acreno.system.config.ConfigApp;
import rs.acreno.system.util.GeneralUiUtility;

import java.util.Arrays;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public class Constants {

    //Config Staf
    private ConfigAcreno configAcreno;
    private static ConfigApp configApp;

    public Constants() throws BackingStoreException {

        //Uzmi sve kljuceve iz prefa Noda
        Preferences prefs = Preferences.userNodeForPackage(Constants.class);
        String[] sviKljuceviPreferenceNode = prefs.keys();
        boolean configAcrenoTrue = Arrays.asList(sviKljuceviPreferenceNode).contains(Constants.ACRENO_CONFIG_NODE_KEY);
        boolean configAPPTrue = Arrays.asList(sviKljuceviPreferenceNode).contains(Constants.APP_CONFIG_NODE_KEY);

        //Ako Nodovi postoje radi DESERIJALIZACIJU objekta
        if (configAcrenoTrue) { // Objekat "ConfigAcreno"
            configAcreno = SerializationUtils.deserialize(prefs.getByteArray(Constants.ACRENO_CONFIG_NODE_KEY, null));
        } else {
            byte[] data = SerializationUtils.serialize(configAcreno);
            prefs.putByteArray(Constants.ACRENO_CONFIG_NODE_KEY, data);
        }
        if (configAPPTrue) { // Objekat "ConfigApp"
            configApp = SerializationUtils.deserialize(prefs.getByteArray(Constants.APP_CONFIG_NODE_KEY, null));
        } else {
            //Serijalizacija Objekta
            byte[] data = SerializationUtils.serialize(configApp);
            prefs.putByteArray(Constants.APP_CONFIG_NODE_KEY, data);
        }

        System.out.println("PUTANJAAAAAAAAAAAAAA: >>>>   " + defAPPconf().getPutanjaDoBazePodataka());

    }

    // *******************  DB Staf SQLite ***************************
    public String MSACCESS_STRING_URL = "jdbc:ucanaccess://" + defAPPconf().getPutanjaDoBazePodataka();

    // *******************  FX UIs Path ***************************
    //AUTO SERVIS HOME
    public static String SPLASH_SCREEN_URI = "/splash_screen.fxml";
    public static String HOME_UI_VIEW_URI = "/auto_servis_ui.fxml";
    public String AUTO_SERVIS_LICENCA_U_PODNOZIJU = defAPPconf().getLicencaPodnozijaAplikacije();

    //AUTOMOBIL
    public static final String AUTOMOBILI_UI_VIEW_URI = "/automobili.fxml";
    //RACUNI FAKTURE
    public static final String FAKTURA_UI_VIEW_URI = "/faktura_ui.fxml";
    public static final String PRINT_FAKTURA_UI_VIEW_URI = "/ui_print_racun.fxml";
    public String PRINT_FAKTURA_ADRESA_FIRME = defConfACReno().getAdresaFirme();
    public String PRINT_FAKTURA_GRAD_FIRME = defConfACReno().getGradFirme();
    public String PRINT_FAKTURA_TELEFON_FIRME = defConfACReno().getTelefonFirme();
    public String PRINT_FAKTURA_SAJT_FIRME = defConfACReno().getSajtFirme();
    public String PRINT_FAKTURA_EMAIL_FIRME = defConfACReno().getEmailFirme();
    public String PRINT_FAKTURA_ZIRORACUN_FIRME = defConfACReno().getZiroRacunFirme();

    //RADNI NALOZI
    public static final String RADNI_NALOZI_UI_VIEW_URI = "/radni_nalozi_ui.fxml";
    public static final String PRINT_RADNI_NALOG_UI_VIEW_URI = "/ui_print_nalozi.fxml";
    public static final String PRINT_BLANKO_RADNI_NALOG_UI_VIEW_URI = "/ui_print_blanko_nalozi.fxml";
    //DEFEKTAZA
    public static final String DEFEKTAZA_UI_VIEW_URI = "/defektaza_ui.fxml";
    public static final String PRINT_DEFEKTAZA_UI_VIEW_URI = "/ui_print_defektaza.fxml";
    public static final String PRINT_BLANKO_DEFEKTAZA_UI_VIEW_URI = "/ui_print_blanko_defektaza.fxml";
    //KLIJENTI
    public static final String CREATE_KLIJENT_UI_VIEW_URI = "/create_klijent_ui.fxml";
    public static final String CREATE_EDIT_AUTOMOBIL_UI_VIEW_URI = "/add_edit_automobil.fxml";
    //ARTIKLI / DELOVI
    public static final String ARTIKLI_UI_VIEW_URI = "/artikli_ui.fxml";
    //CONFIG GUI
    public static final String CONFIG_UI_VIEW_URI = "/config_ui.fxml";
    //PRETRAGA GUI
    public static final String PRETRAGA_UI_VIEW_URI = "/pretraga_ui.fxml";


    // *******************  KOMBINACIJE NA TASTERIMA ZA KEYBOARD SHORTCUT ***************************

    //AUTOMOBIL U AUTO SERVIS UIa
    public static final KeyCode OTVORI_AUTOMOBIL_KARTICU_KEYCODE = KeyCode.C;
    public static final KeyCombination.Modifier OTVORI_AUTOMOBIL_KARTICU_KEYCOMBINATION = KeyCombination.CONTROL_ANY;

    //KLIJENT U AUTO SERVIS UIa
    public static final KeyCode OTVORI_KLIJENT_KARTICU_KEYCODE = KeyCode.F;
    //public static final KeyCombination.Modifier OTVORI_KLIJENT_KARTICU_KEYCOMBINATION = KeyCombination.CONTROL_ANY;
    //Shor Cut Otvori ARTIKLE
    //public static final KeyCode OTVORI_ARTIKL_KARTICU_KEYCODE = KeyCode.A;
    //public static final KeyCombination.Modifier OTVORI_ARTIKL_KARTICU_KEYCOMBINATION = KeyCombination.CONTROL_ANY;

    // ******************* SYSTEM ***************************
    public static final String APP_ICON = "/faktura/logo-acreno.jpg";
    public static final String APP_CLIENTS_ICON = "/auto_servis/clients_home.png";
    public static final String APP_AUTOMOBIL_ICON = "/auto_servis/car_home.png";
    public static final String APP_ARTIKLI_ICON = "/auto_servis/bar_code_home.png";
    //public static final String APP_MAIN_PNG_LOGO = "/acr_logo.png";
    public int APP_UCESTALOST_PROVERE_INTERNETA = Integer.parseInt(defAPPconf().getIntervalProvereInternetaAplikacije());


    // ******************* SPLASH SCREEN ***************************
    public int SPLASH_SCREEN_DELAY = Integer.parseInt(defAPPconf().getSpashScreenDelayAplikacije());
    public int SPLASH_SCREEN_DELAY_ABOUT_WINDOW = Integer.parseInt(defAPPconf().getSpashScreenAboutDelayAplikacije());
    public String SPLASH_SCREEN_APP_VERION = defAPPconf().getVerzijaAplikacije();
    public String SPLASH_SCREEN_APP_DATE = defAPPconf().getDatumObjaveAplikacije();
    public String SPLASH_SCREEN_APP_LICENCE = defAPPconf().getLicencaAplikacije();
    public String SPLASH_SCREEN_IME_FIRME = defAPPconf().getImeFirme();

    public String PUTANJA_KALENDAR_ZAKAZIVANJE = defAPPconf().getPutanjaDoGKalendara();

    // ******************* CONFIG PREFs KEYs***************************
    public static final String ACRENO_CONFIG_NODE_KEY = "configAcreno";
    public static final String APP_CONFIG_NODE_KEY = "configAPP";

    //TODO: Napisati JAVA DOC
    private ConfigAcreno defConfACReno() {
        Preferences prefs = Preferences.userNodeForPackage(Constants.class);
        configAcreno = SerializationUtils.deserialize(prefs.getByteArray(ACRENO_CONFIG_NODE_KEY, null));

        return configAcreno;
    }

    //TODO: Napisati JAVA DOC
    private ConfigApp defAPPconf() {
        Preferences prefs = Preferences.userNodeForPackage(Constants.class);
        configApp = SerializationUtils.deserialize(prefs.getByteArray(APP_CONFIG_NODE_KEY, null));

        return configApp;
    }


}
