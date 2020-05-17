package rs.acreno.automobil.ui_add_edit_automobil;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import rs.acreno.automobil.Automobil;
import rs.acreno.automobil.AutomobilDAO;
import rs.acreno.automobil.AutomobiliController;
import rs.acreno.automobil.SQLAutomobilDAO;
import rs.acreno.autoservis.AutoServisController;
import rs.acreno.klijent.Klijent;
import rs.acreno.system.constants.Constants;
import rs.acreno.system.exeption.AcrenoException;
import rs.acreno.system.util.GeneralUiUtility;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicReference;

public class AddEditAutomobilController implements Initializable {

    private static final Logger logger = Logger.getLogger(AutoServisController.class);


    // ****************** FXMLs STAFF *************************
    @FXML private Label lblHeaderTitle;
    @FXML private TextField txtfIdKAutomobila;
    @FXML private TextField txtFieldRegOznaka;
    @FXML private Label lblImeKlijenta;
    @FXML private TextField txtfIdKlijenta;
    @FXML private TextField txtfVinVozila;
    @FXML private DatePicker datePickerDatumAcrRegistracije;
    @FXML private TextField txtfModelAutomobila;
    @FXML private TextField txtfMarkaAutomobila;
    @FXML private TextField txtfVrstaAutomobila;
    @FXML private TextField txtfGodisteAutomobila;
    @FXML private TextField txtfZapreminaAutomobila;
    @FXML private TextField txtfSnagaAutomobila;
    @FXML private ComboBox<String> cmbVrstaGorivaAutomobila;
    @FXML private TextField txtfKilometrazaAutomobila;
    @FXML private TextField txtfBrojMotoraAutomobila;
    @FXML private TextField txtfBojaAutomobila;
    @FXML private TextField txtfMasaAutomobila;
    @FXML private TextField txtfBrojVrataAutomobila;
    @FXML private TextField txtfDozvoljenaMasaAutomobila;
    @FXML private TextField txtfDatumPrveRegistracijeAutomobila;
    @FXML private TextField txtfBrojMestaZaSedenje;
    @FXML private TextArea txtaNapomeneAutomobila;


    @FXML private Button btnClose;

    private boolean isCloseButtonPresed = false;
    private Stage stageCreateNewAutomobil;
    private boolean isWeAreInEditModeAutomobil;
    private Automobil automobil;
    private Klijent klijent;

    private final AutomobilDAO automobilDAO = new SQLAutomobilDAO();

    /**
     * Referenca ka {@link AutomobiliController}-u
     */
    private final AtomicReference<AutomobiliController> automobiliController = new AtomicReference<>();

    /**
     * Referenca ka {@link AutoServisController}-u
     */
    private final AtomicReference<AutoServisController> autoServisController = new AtomicReference<>();

    /**
     * Seter metoda koja se koristi u {@link AutomobiliController#setAutoServisController(AutoServisController, Stage)}-u
     * Takodje se prosledjuje i STAGE ako bude zatrebalo, a iz {@link AutomobiliController #btnOpenIzmeniAutomobilUi()}-a
     * <p>
     * {@link AutoServisController #btnOpenIzmeniAutomobilUi(ActionEvent)}
     *
     * @param autmobilController      referenca ka automobil kontroloru
     * @param stageCreateNewAutomobil refereca ka automobil Stage-u
     * @see AutomobiliController
     */
    public void setAutmobilController(AutomobiliController autmobilController, Stage stageCreateNewAutomobil) {
        this.automobiliController.set(autmobilController);
        this.stageCreateNewAutomobil = stageCreateNewAutomobil;
    }

    public void setAutoServisController(AutoServisController autoServisController, Stage stageCreateNewAutomobil) {
        this.autoServisController.set(autoServisController);
        this.stageCreateNewAutomobil = stageCreateNewAutomobil;
    }

    public Automobil getAutomobil() {
        return automobil;
    }

    public void setAutomobil(Automobil automobil) {
        this.automobil = automobil;
    }

    public Klijent getKlijent() {
        return klijent;
    }

    public void setKlijent(Klijent klijent) {
        this.klijent = klijent;
    }

    public boolean isWeAreInEditModeAutomobil() {
        return isWeAreInEditModeAutomobil;
    }

    public void setWeAreInEditMode(boolean isWeAreInEditModeAutomobil) {
        this.isWeAreInEditModeAutomobil = isWeAreInEditModeAutomobil;
    }

    public void setLblHeaderTitle(String lblHeaderTitle) {
        this.lblHeaderTitle.setText(lblHeaderTitle);
    }

    @Override public void initialize(URL url, ResourceBundle resourceBundle) {
        Platform.runLater(() -> {
            //Bez Obzira na EDIT MODE postavi podatke o Klijentu
            lblImeKlijenta.setText(klijent.getImePrezime());
            txtfIdKlijenta.setText(String.valueOf(klijent.getIdKlijenta()));
            LocalDate now = LocalDate.now();
            datePickerDatumAcrRegistracije.setValue(now); //Postavi danasnji datum Racuna u datePiceru
          /*  datePickerDatumAcrRegistracije.setValue(
                    GeneralUiUtility.formatDateForUs(new Date(LocalDate.now()))
            );*/
            if (isWeAreInEditModeAutomobil()) {
                logger.error("WE ARE ****IN**** EDIT MODE: " + getAutomobil().getRegOznaka());
                // Ako je AUTOMOBIL u edit modu nemoj praviti nivi Obj Automobila nego prosledi AUTOMOBIL obj koji je za izmenu
                newOrEditAutomobil(true);
            } else {
                logger.info("WE ARE ****NOT**** IN EDIT MODE: ");
                newOrEditAutomobil(false); // Nismo u edit modu pa napravi novi Automobil obj
            }
        });
    }

    private void newOrEditAutomobil(boolean isEditMode) {
        if (isEditMode) { //NOT EDIT MODE (TRUE)
            txtfIdKAutomobila.setDisable(false);
            txtfIdKAutomobila.setText(String.valueOf(getAutomobil().getIdAuta()));
            txtFieldRegOznaka.setText(getAutomobil().getRegOznaka());
            txtfVinVozila.setText(getAutomobil().getVinVozila());
            datePickerDatumAcrRegistracije.setValue(
                    GeneralUiUtility.fromStringDate(getAutomobil().getDatumAcrRegistracijeAuta()));
            txtfModelAutomobila.setText(getAutomobil().getModelVozila());
            txtfMarkaAutomobila.setText(getAutomobil().getMarkaVozila());
            txtfVrstaAutomobila.setText(getAutomobil().getVrstaVozila());
            txtfGodisteAutomobila.setText(String.valueOf(getAutomobil().getGodisteVozila()));
            txtfZapreminaAutomobila.setText(String.valueOf(getAutomobil().getZapreminaVozila()));
            txtfSnagaAutomobila.setText(String.valueOf(getAutomobil().getSnagaVozila()));
            cmbVrstaGorivaAutomobila.setValue(getAutomobil().getVrstaGorivaVozila());
            txtfKilometrazaAutomobila.setText(getAutomobil().getKilomteraza());
            txtfBrojMotoraAutomobila.setText(getAutomobil().getBrojMotoraVozila());
            txtfBojaAutomobila.setText(getAutomobil().getBojaVozila());
            txtfMasaAutomobila.setText(String.valueOf(getAutomobil().getMasaVozila()));
            txtfBrojVrataAutomobila.setText(String.valueOf(getAutomobil().getBrojVrataVozila()));
            txtfDozvoljenaMasaAutomobila.setText(String.valueOf(getAutomobil().getNajvecaDozvoljenaMasaVozila()));
            txtfDatumPrveRegistracijeAutomobila.setText(getAutomobil().getDatumPrveRegistracijeVozila());
            txtfBrojMestaZaSedenje.setText(String.valueOf(getAutomobil().getBrojMestaZaSedenje()));
            txtaNapomeneAutomobila.setText(getAutomobil().getNapomeneAutomobila());

        } else { //NOT EDIT MODE (FALSE)
            try {
                automobil = new Automobil();
                automobil.setIdKlijenta(Integer.parseInt(txtfIdKlijenta.getText()));
                automobilDAO.insertAutomobil(automobil);
                ObservableList<Automobil> automobili = FXCollections.observableArrayList(automobilDAO.findAllAutomobil());
                int tempIDautomobila = automobilDAO.findAllAutomobil().get(automobili.size() - 1).getIdAuta();
                automobil.setIdAuta(tempIDautomobila); // ako se predomislimo i hocemo da obrisemo ovde postavljamo ID
                txtfIdKAutomobila.setText(String.valueOf(tempIDautomobila)); // Ubaci ID auta u TF "txtfIdKlijenta"
            } catch (AcrenoException | SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML private void saveAutomobil() {
        if (txtFieldRegOznaka.getText().equals("")) {
            GeneralUiUtility.alertDialogBox(
                    Alert.AlertType.ERROR,
                    "Molimo Vas da unesete Registarsku Oznaku !",
                    "GREŠKA U ČUVANJU AUTOMOBILA...",
                    "Niste u mogućnosti da sačuvate ovaj Automobil jer nije unešena Registarska oznaka!");
        } else {
            // automobil = new Automobil();
            automobil.setIdAuta(Integer.parseInt(txtfIdKAutomobila.getText()));
            automobil.setIdKlijenta(klijent.getIdKlijenta());
            automobil.setRegOznaka(txtFieldRegOznaka.getText());
            automobil.setVinVozila(txtfVinVozila.getText());
            automobil.setDatumAcrRegistracijeAuta(
                    GeneralUiUtility.formatDateForUs(datePickerDatumAcrRegistracije.getValue()));
            automobil.setModelVozila(txtfModelAutomobila.getText());
            automobil.setMarkaVozila(txtfMarkaAutomobila.getText());
            automobil.setVrstaVozila(txtfVrstaAutomobila.getText());
            automobil.setGodisteVozila(Integer.parseInt(txtfGodisteAutomobila.getText()));
            automobil.setZapreminaVozila(Integer.parseInt(txtfZapreminaAutomobila.getText()));
            automobil.setSnagaVozila(Integer.parseInt(txtfSnagaAutomobila.getText()));
            automobil.setVrstaGorivaVozila(String.valueOf(cmbVrstaGorivaAutomobila.getValue()));
            automobil.setKilomteraza(txtfKilometrazaAutomobila.getText());
            automobil.setBrojMotoraVozila(txtfBrojMotoraAutomobila.getText());
            automobil.setBojaVozila(txtfBojaAutomobila.getText());
            automobil.setMasaVozila(Integer.parseInt(txtfMasaAutomobila.getText()));
            automobil.setBrojVrataVozila(Integer.parseInt(txtfBrojVrataAutomobila.getText()));
            automobil.setNajvecaDozvoljenaMasaVozila(Integer.parseInt(txtfDozvoljenaMasaAutomobila.getText()));
            automobil.setDatumPrveRegistracijeVozila(txtfDatumPrveRegistracijeAutomobila.getText());
            automobil.setBrojMestaZaSedenje(Integer.parseInt(txtfBrojMestaZaSedenje.getText()));
            automobil.setNapomeneAutomobila(txtaNapomeneAutomobila.getText());

            try {
                automobilDAO.updateAutomobil(automobil);
            } catch (AcrenoException | SQLException e) {
                e.printStackTrace();
            }
            if (!isCloseButtonPresed) {
                GeneralUiUtility.alertDialogBox(
                        Alert.AlertType.CONFIRMATION,
                        "USPESNO SAČUVAN AUTOMOBIL",
                        "ČUVANJE AUTOMOBILA",
                        "Uspesno ste sacuvali Automobil registarske oznake: " + automobil.getRegOznaka());
            }
        }
    }

    @FXML private void btnZatvoriCreateAutomobilUi(ActionEvent actionEvent) throws AcrenoException, SQLException {

        isCloseButtonPresed = true;

        if (txtFieldRegOznaka.getText().isEmpty()) { // Cuvaj Klijenta ako makar ima nesto u TFu ime i prezime
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Potvrda brisanja Automobila");
            alert.setHeaderText("Niste uneli Registarsku Tablicu, da li možemo da ga obrišemo?");
            alert.setContentText("Automobil je već napravljen u bazi, ali niste uneli Registarsku Tablicu pa predpostavljamo da " +
                    "možemo da obrišemo ovog Automobil.");
            ((Stage) alert.getDialogPane().getScene().getWindow()).getIcons().add(new Image(Constants.APP_ICON));
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent()) {
                if (result.get() == ButtonType.OK) {
                    automobilDAO.deleteAutomobil(automobil);
                    btnClose.fireEvent(new WindowEvent(stageCreateNewAutomobil, WindowEvent.WINDOW_CLOSE_REQUEST));
                }
            }
        } else {
            saveAutomobil(); //Cuvamo kljijenta ako ima nesto u TXTFu Ime Prezime
            btnClose.fireEvent(new WindowEvent(stageCreateNewAutomobil, WindowEvent.WINDOW_CLOSE_REQUEST));
        }
    }

    public void btnObrisiAutomobilAct(ActionEvent actionEvent) throws AcrenoException, SQLException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Potvrda Brisanja Automobila: " + getAutomobil().getRegOznaka());
        alert.setHeaderText("Brisanja Automobila: " + getAutomobil().getRegOznaka());
        alert.setContentText("Da li ste sigurni da želite da obrišete " + getAutomobil().getRegOznaka() + " iz baze podataka ?");
        ((Stage) alert.getDialogPane().getScene().getWindow()).getIcons().add(new Image(Constants.APP_ICON));
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent()) {
            if (result.get() == ButtonType.OK) {
                automobilDAO.deleteAutomobil(automobil);
                ((Stage) (((Button) actionEvent.getSource()).getScene().getWindow())).close();
            }
        }
    }
}
