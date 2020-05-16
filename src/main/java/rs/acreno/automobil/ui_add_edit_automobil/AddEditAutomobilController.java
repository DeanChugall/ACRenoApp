package rs.acreno.automobil.ui_add_edit_automobil;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.apache.log4j.Logger;
import rs.acreno.automobil.Automobil;
import rs.acreno.automobil.AutomobilDAO;
import rs.acreno.automobil.AutomobiliController;
import rs.acreno.automobil.SQLAutomobilDAO;
import rs.acreno.autoservis.AutoServisController;
import rs.acreno.klijent.Klijent;
import rs.acreno.system.exeption.AcrenoException;
import rs.acreno.system.util.GeneralUiUtility;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicReference;

public class AddEditAutomobilController implements Initializable {

    private static final Logger logger = Logger.getLogger(AutoServisController.class);

    @FXML private Button btnClose;
    @FXML private TextField txtfIdKAutomobila;
    @FXML private TextField txtFieldRegOznaka;
    @FXML private Label lblImeKlijenta;


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


    @Override public void initialize(URL url, ResourceBundle resourceBundle) {
        Platform.runLater(() -> {
            if (isWeAreInEditModeAutomobil()) {
                logger.info("WE ARE ****IN**** EDIT MODE: " + getAutomobil().getRegOznaka());
                lblImeKlijenta.setText(getKlijent().getImePrezime());
                txtFieldRegOznaka.setText(getAutomobil().getRegOznaka());
                // Ako je AUTOMOBIL u edit modu nemoj praviti nivi Obj Automobila nego prosledi AUTOMOBIL obj koji je za izmenu
                newOrEditAutomobil(true);
            } else {
                logger.info("WE ARE ****NOT**** IN EDIT MODE: ");

                lblImeKlijenta.setText(getKlijent().getImePrezime());
                newOrEditAutomobil(false); // Nismo u edit modu pa napravi novi Automobil obj

            }
        });
    }

    private void newOrEditAutomobil(boolean isEditMode) {
        if (isEditMode) { //NOT EDIT MODE (TRUE)
            txtfIdKAutomobila.setDisable(false);
            txtfIdKAutomobila.setText(String.valueOf(automobil.getIdAuta()));

        } else { //NOT EDIT MODE (FALSE)
            /*try {
                automobilDAO.insertAutomobil(automobil);
                ObservableList<Automobil> automobili = FXCollections.observableArrayList(automobilDAO.findAllAutomobil());
                int tempIDautomobila = automobilDAO.findAllAutomobil().get(automobili.size() - 1).getIdAuta();
                automobil.setIdAuta(tempIDautomobila); // ako se predomislimo i hocemo da obrisemo ovde postavljamo ID
                txtfIdKAutomobila.setText(String.valueOf(tempIDautomobila)); // Ubaci ID kojenta u TF "txtfIdKlijenta"
            } catch (AcrenoException | SQLException e) {
                e.printStackTrace();
            }*/

        }
    }

    @FXML private void saveAutomobil() {
        if (txtFieldRegOznaka.getText().equals("")) {
            GeneralUiUtility.alertDialogBox(
                    Alert.AlertType.ERROR,
                    "Molimo Vas da unesete Registarsku Oznaku !",
                    "GREŠKA U ČUVANJU AUTOMOBILA...",
                    "Niste u mogućnosti da sačuvate ovaj Automobil jer nije unešena Registarska oznaka!"
            );
        } else {
            automobil = new Automobil();
            automobil.setIdAuta(Integer.parseInt(txtfIdKAutomobila.getText()));
            automobil.setRegOznaka(txtFieldRegOznaka.getText());
            automobil.setIdKlijenta(klijent.getIdKlijenta());
            try {
                automobilDAO.updateAutomobil(automobil);
                GeneralUiUtility.alertDialogBox(
                        Alert.AlertType.CONFIRMATION,
                        "USPESNO SAČUVAN AUTOMOBIL",
                        "ČUVANJE AUTOMOBILA",
                        "Uspesno ste sacuvali Automobil registarske oznake: " + automobil.getRegOznaka()
                );
            } catch (AcrenoException | SQLException e) {
                e.printStackTrace();
            }
        }

    }


    @FXML private void btnZatvoriCreateAutomobilUi(ActionEvent actionEvent) {
        btnClose.fireEvent(new WindowEvent(stageCreateNewAutomobil, WindowEvent.WINDOW_CLOSE_REQUEST));
    }
}
