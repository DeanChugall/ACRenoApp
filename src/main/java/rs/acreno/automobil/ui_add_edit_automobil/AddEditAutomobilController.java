package rs.acreno.automobil.ui_add_edit_automobil;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import rs.acreno.automobil.AutomobiliController;
import rs.acreno.autoservis.AutoServisController;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicReference;

public class AddEditAutomobilController implements Initializable {

    /**
     * Referenca ka {@link AutomobiliController}-u
     */
    private final AtomicReference<AutomobiliController> automobiliController = new AtomicReference<>();
    @FXML private Button btnZatvoriAddAutomobil;
    private Stage stageCreateNewAutomobil;

    @Override public void initialize(URL url, ResourceBundle resourceBundle) {
    }

    /**
     * Seter metoda koja se koristi u {@link AutomobiliController#setAutoServisController(AutoServisController, Stage)}-u
     * Takodje se prosledjuje i STAGE ako bude zatrebalo, a iz {@link AutomobiliController #btnOpenIzmeniAutomobilUi()}-a
     * <p>
     * {@link AutoServisController #btnOpenIzmeniAutomobilUi(ActionEvent)}
     *
     * @param autmobilController    referenca ka automobil kontroloru
     * @param stageCreateNewAutomobil refereca ka automobil Stage-u
     * @see AutomobiliController
     */
    public void setAutmobilController(AutomobiliController autmobilController, Stage stageCreateNewAutomobil) {
        this.automobiliController.set(autmobilController);
        this.stageCreateNewAutomobil = stageCreateNewAutomobil;
    }

    public void btnZatvoriCreateAutomobilUi(ActionEvent actionEvent) {
        btnZatvoriAddAutomobil.fireEvent(new WindowEvent(stageCreateNewAutomobil, WindowEvent.WINDOW_CLOSE_REQUEST));
    }
}
