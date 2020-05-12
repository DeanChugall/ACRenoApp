package rs.acreno.nalozi;

import javafx.application.Platform;
import javafx.fxml.Initializable;
import javafx.stage.Stage;
import rs.acreno.automobil.AutomobiliController;
import rs.acreno.autoservis.AutoServisController;

import java.net.URL;
import java.util.ResourceBundle;

public class RadniNalogController implements Initializable {

    private AutomobiliController automobiliController;
    private Stage automobilStage;

    /**
     * Seter metoda koja se koristi u {@link AutomobiliController#setAutoServisController(AutoServisController, Stage)}-u
     * Takodje se prosledjuje i STAGE ako bude zatrebalo, a iz {@link AutomobiliController #btnOpenFakturaUi()}-a
     * Prosledjeni Automobil i Klijent objekti su iz {@link AutomobiliController}, a impl u {@link #initGUI()}
     *
     * @param autmobilController referenca ka automobil kontroloru
     * @param automobilStage     refereca ka automobil Stage-u
     * @see AutomobiliController
     */
    public void setAutmobilController(AutomobiliController autmobilController, Stage automobilStage) {
        this.automobiliController = autmobilController;
        this.automobilStage = automobilStage;
    }

    @Override public void initialize(URL url, ResourceBundle resourceBundle) {
        Platform.runLater(() -> {
            System.out.println(automobiliController.getAutomobil().get(0).getRegOznaka());
            System.out.println(automobiliController.getKlijenti().get(0).getImePrezime());
        });
    }

    private void initGUI() {
        //Inicijalizacija podataka

    }
}
