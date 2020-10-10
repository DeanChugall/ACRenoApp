package rs.acreno.system.config.ui_config;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.log4j.Logger;
import rs.acreno.system.config.ConfigAcreno;
import rs.acreno.system.config.ConfigApp;
import rs.acreno.system.constants.Constants;
import rs.acreno.system.util.GeneralUiUtility;
import rs.acreno.system.util.properties.AcrenoProperties;

import java.net.URL;
import java.util.Arrays;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public class ConfigUiControler implements Initializable {

    @FXML Pane paneConfig;
    @FXML Button btnCloseConfig;
    @FXML Button btnOtkljucajAdminPolja;
    @FXML PasswordField pswdTxtFLozinka;
    @FXML Button btnPonistiConfig;

    //***** FXMLs ACReno PROPERTIES ***********
    @FXML private TextField txtFadresa;
    @FXML private TextField txtFGrad;
    @FXML private TextField txtFziroRacun;
    @FXML private TextField txtFtelefon;
    @FXML private TextField txtFwebSajt;
    @FXML private TextField txtFeMail;

    //***** FXMLs APP PROPERTIES ***********
    @FXML private TextField txtFImeFirme;
    @FXML private TextField txtFVerzijaAplikacije;
    @FXML private TextField txtFLicenca;
    @FXML private TextField txtFLicencaPodnozijaAplikacije;
    @FXML private TextField txtFdatumObjave;
    @FXML private TextField txtFimeBazePodataka;
    @FXML private TextField txtFintervalProvereInterneta;
    @FXML private TextField txtFsplashScreenDelay;
    @FXML private TextField txtFsplashScreenAboutDelay;
    @FXML private TextField txtFputanjaDoGkalendara;
    @FXML private Label lblLozinka;

    private ConfigAcreno configAcreno;
    private ConfigApp configApp;

    private static final Logger logger = Logger.getLogger(AcrenoProperties.class);

    private final Preferences prefs = Preferences.userNodeForPackage(String.class);

    private final String[] sviKljuceviPreferenceNode = prefs.keys(); //Uzmi sve kljuceve iz prefa Noda

    public ConfigUiControler() throws BackingStoreException { //Empty Constructor because exception "BackingStoreException"
    }

    @Override public void initialize(URL url, ResourceBundle resourceBundle) {
        Platform.runLater(() -> {

            btnOtkljucajAdminPolja.setOnAction(e -> {
                if (!pswdTxtFLozinka.getText().equals("acrenoA!")) {
                    lblLozinka.setText("Lozinka nije tačna!");
                    lblLozinka.setTextFill(Color.rgb(173, 26, 17));
                    GeneralUiUtility.alertDialogBox(Alert.AlertType.ERROR,
                            "Greška...Lozinka nije tačna !",
                            "Greška...Lozinka nije tačna !",
                            "Greška...Lozinka nije tačna, pošaljite email ACR dev timu. (it@acreno.rs)");
                } else {
                    lblLozinka.setText("Potvrdjena lozinka!");
                    lblLozinka.setTextFill(Color.rgb(100, 203, 12));
                    txtFVerzijaAplikacije.setDisable(false);
                    txtFLicenca.setDisable(false);
                    txtFLicencaPodnozijaAplikacije.setDisable(false);
                    txtFdatumObjave.setDisable(false);
                    txtFimeBazePodataka.setDisable(false);
                    txtFintervalProvereInterneta.setDisable(false);
                    txtFsplashScreenDelay.setDisable(false);
                    txtFsplashScreenAboutDelay.setDisable(false);
                    btnPonistiConfig.setDisable(false);
                }
                pswdTxtFLozinka.clear();
            });

            //Provera da li ima nesto u Java Properties NODU, ako ima radi DEserijalizaciju, a ako nema SErjjalizaciju
            boolean configAcrenoTrue = Arrays.asList(sviKljuceviPreferenceNode).contains(Constants.ACRENO_CONFIG_NODE_KEY);
            boolean configAPPTrue = Arrays.asList(sviKljuceviPreferenceNode).contains(Constants.APP_CONFIG_NODE_KEY);

            //Ako Nodovi postoje radi DESERIJALIZACIJU objekta
            if (configAcrenoTrue) { // Objekat "ConfigAcreno"
                configAcreno = SerializationUtils.deserialize(prefs.getByteArray(Constants.ACRENO_CONFIG_NODE_KEY, null));
            }
            if (configAPPTrue) { // Objekat "ConfigApp"
                configApp = SerializationUtils.deserialize(prefs.getByteArray(Constants.APP_CONFIG_NODE_KEY, null));
            }

            // ConfigAcreno
            if (configAcreno != null) { //Ako je sve OK pravi novi Objekat i radi deserijalizacijju
                configAcreno = new ConfigAcreno();
                configAcreno = SerializationUtils.deserialize(prefs.getByteArray(Constants.ACRENO_CONFIG_NODE_KEY, null));
                //SET ACReno CONFIG TXTf
                txtFadresa.setText(configAcreno.getAdresaFirme());
                txtFGrad.setText(configAcreno.getGradFirme());
                txtFziroRacun.setText(configAcreno.getZiroRacunFirme());
                txtFtelefon.setText(configAcreno.getTelefonFirme());
                txtFwebSajt.setText(configAcreno.getSajtFirme());
                txtFeMail.setText(configAcreno.getEmailFirme());

            } else { //Ako je NODE ne postoji uradi SERIJALIZACIJU i napravi objekat
                configAcreno = new ConfigAcreno();
                configAcreno.setAdresaFirme(txtFadresa.getText());
                configAcreno.setGradFirme(txtFGrad.getText());
                configAcreno.setZiroRacunFirme(txtFziroRacun.getText());
                configAcreno.setTelefonFirme(txtFtelefon.getText());
                configAcreno.setSajtFirme(txtFwebSajt.getText());
                configAcreno.setEmailFirme(txtFeMail.getText());
                //Serijalizacija Objekta
                byte[] data = SerializationUtils.serialize(configAcreno);
                prefs.putByteArray(Constants.ACRENO_CONFIG_NODE_KEY, data);
            }

            // ConfigApp
            if (configApp != null) { //Ako je sve OK pravi novi Objekat i radi deserijalizacijju

                configApp = SerializationUtils.deserialize(prefs.getByteArray(Constants.APP_CONFIG_NODE_KEY, null));
                txtFImeFirme.setText(configApp.getImeFirme());
                txtFVerzijaAplikacije.setText(configApp.getVerzijaAplikacije());
                txtFLicenca.setText(configApp.getLicencaAplikacije());
                txtFLicencaPodnozijaAplikacije.setText(configApp.getLicencaPodnozijaAplikacije());
                txtFdatumObjave.setText(configApp.getDatumObjaveAplikacije());
                txtFimeBazePodataka.setText(configApp.getImeBazePodatakaAplikacije());
                txtFintervalProvereInterneta.setText(configApp.getIntervalProvereInternetaAplikacije());
                txtFsplashScreenDelay.setText(configApp.getSpashScreenDelayAplikacije());
                txtFsplashScreenAboutDelay.setText(configApp.getSpashScreenAboutDelayAplikacije());
                txtFputanjaDoGkalendara.setText(configApp.getPutanjaDoGKalendara());

            } else { //Ako je NODE ne postoji uradi SERIJALIZACIJU i napravi objekat
                configApp = new ConfigApp();
                configApp.setImeFirme(txtFImeFirme.getText());
                configApp.setVerzijaAplikacije(txtFVerzijaAplikacije.getText());
                configApp.setLicencaAplikacije(txtFLicenca.getText());
                configApp.setLicencaPodnozijaAplikacije(txtFLicencaPodnozijaAplikacije.getText());
                configApp.setDatumObjaveAplikacije(txtFdatumObjave.getText());
                configApp.setImeBazePodatakaAplikacije(txtFimeBazePodataka.getText());
                configApp.setIntervalProvereInternetaAplikacije(txtFimeBazePodataka.getText());
                configApp.setSpashScreenDelayAplikacije(txtFsplashScreenDelay.getText());
                configApp.setSpashScreenAboutDelayAplikacije(txtFsplashScreenAboutDelay.getText());
                configApp.setSpashScreenAboutDelayAplikacije(txtFintervalProvereInterneta.getText());
                configApp.setPutanjaDoGKalendara(txtFputanjaDoGkalendara.getText());
                //Serijalizacija Objekta
                byte[] data = SerializationUtils.serialize(configApp);
                prefs.putByteArray(Constants.APP_CONFIG_NODE_KEY, data);
            }
            //Setovanje Akcije za dugme
            btnCloseConfig.setOnAction(event -> ((Stage) (((Button) event.getSource()).getScene().getWindow())).close());
        });
    }

    //TODO: Napisi JAVA DOC
    @FXML public void sacuvajAct(ActionEvent actionEvent) {
        actionEvent.getEventType();
        //Provera da li ima slova tamo gde ne treba, u tri polja...
        if (!GeneralUiUtility.proveriStringDaLiImaSlova(txtFsplashScreenDelay.getText() +
                txtFsplashScreenAboutDelay.getText() + txtFintervalProvereInterneta.getText())) {

            //ACR CONFIG
            configAcreno.setAdresaFirme(txtFadresa.getText());
            configAcreno.setGradFirme(txtFGrad.getText());
            configAcreno.setZiroRacunFirme(txtFziroRacun.getText());
            configAcreno.setTelefonFirme(txtFtelefon.getText());
            configAcreno.setSajtFirme(txtFwebSajt.getText());
            configAcreno.setEmailFirme(txtFeMail.getText());

            //APP CONFIG
            configApp.setImeFirme(txtFImeFirme.getText());
            configApp.setVerzijaAplikacije(txtFVerzijaAplikacije.getText());
            configApp.setLicencaAplikacije(txtFLicenca.getText());
            configApp.setLicencaPodnozijaAplikacije(txtFLicencaPodnozijaAplikacije.getText());
            configApp.setDatumObjaveAplikacije(txtFdatumObjave.getText());
            configApp.setImeBazePodatakaAplikacije(txtFimeBazePodataka.getText());
            configApp.setIntervalProvereInternetaAplikacije(txtFintervalProvereInterneta.getText());
            configApp.setSpashScreenDelayAplikacije(txtFsplashScreenDelay.getText());
            configApp.setSpashScreenAboutDelayAplikacije(txtFsplashScreenAboutDelay.getText());
            configApp.setPutanjaDoGKalendara(txtFputanjaDoGkalendara.getText());

            byte[] dataACRconfig = SerializationUtils.serialize(configAcreno);
            prefs.putByteArray(Constants.ACRENO_CONFIG_NODE_KEY, dataACRconfig);

            byte[] dataAPPconfig = SerializationUtils.serialize(configApp);
            prefs.putByteArray(Constants.APP_CONFIG_NODE_KEY, dataAPPconfig);

            try {
                prefs.flush();
                GeneralUiUtility.alertDialogBox(
                        Alert.AlertType.INFORMATION,
                        "Čuvanje Konfiguracije...",
                        "Čuvanje Konfiguracije",
                        "Uspešno ste izmenuli konfiguracione podatke u aplikaciji.");

                logger.info("USPESNO SACUVANA KONFIGURACIJA !");

            } catch (BackingStoreException e) {
                e.printStackTrace();
                GeneralUiUtility.alertDialogBox(Alert.AlertType.ERROR,
                        "Greška...",
                        "Greška u čuvanju konfiguracije !",
                        "Konfiguracija nije uspešno sačuvana, pošaljite LOG ACR dev timu.");

                logger.error(e.getMessage() + " >>>>   GRESKA U CUVANJU KONFIGURACIJE !");
            }

        } else {
            GeneralUiUtility.alertDialogBox(Alert.AlertType.ERROR,
                    "Greška...Polja (sek) smeju da imaju samo brojne vrednosti !",
                    "Greška u čuvanju konfiguracije !",
                    "Konfiguracija nije uspešno sačuvana, pošaljite LOG ACR dev timu.");

        }
    }

    //TODO: Napisi JAVA DOC
    @FXML private void removeAllPref() {

        ButtonType OK = new ButtonType("Da", ButtonBar.ButtonData.OK_DONE);
        ButtonType CANCEL = new ButtonType("Odustani", ButtonBar.ButtonData.CANCEL_CLOSE);
        Alert alert = new Alert(Alert.AlertType.WARNING,
                "Ova akcija ce obrisati sve, Jeste li sugurni",
                OK,
                CANCEL);

        alert.setTitle("Brisanje Podesavanja");
        Optional<ButtonType> result = alert.showAndWait();

        if (result.orElse(CANCEL) == OK) {
            try {
                prefs.removeNode(); //Ukloni Sve Nodove iz REGISTIJA ili za Linux...
                btnCloseConfig.fire(); // Zatvori Prozor
                GeneralUiUtility.alertDialogBox(
                        Alert.AlertType.INFORMATION,
                        "Brisanje Konfiguracije...",
                        "Brisanje Konfiguracije",
                        "Uspešno ste obrisali konfiguracione podatke u aplikaciji.");

                logger.info("Brisanje KONFIGURACIJE !");

            } catch (BackingStoreException e) {
                e.printStackTrace();
                GeneralUiUtility.alertDialogBox(Alert.AlertType.ERROR,
                        "Greška...",
                        "Greška u brisanju konfiguracije !",
                        "Konfiguracija nije uspešno obrisana, pošaljite LOG ACR dev timu.");

                logger.error(e.getMessage() + " >>>>   GRESKA U brisanju KONFIGURACIJE !");
            }
        } else {
            GeneralUiUtility.alertDialogBox(Alert.AlertType.ERROR,
                    "Odustajanje...",
                    "Drago nam je što niste obrisali konfiguracije !",
                    "Odustanak od brisanja konfiguracija.");
        }
    }

    @FXML public void btnActZatvoriConfig(ActionEvent actionEvent) {
        ((Stage) (((Button) actionEvent.getSource()).getScene().getWindow())).close();
    }
}
