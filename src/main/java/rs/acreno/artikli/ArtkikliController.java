package rs.acreno.artikli;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import rs.acreno.automobil.AutomobiliController;
import rs.acreno.autoservis.AutoServisController;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicReference;

public class ArtkikliController implements Initializable {

    /**
     * Referenca ka {@link AutoServisController}-u
     */
    private final AtomicReference<AutoServisController> autoServisController = new AtomicReference<>();
    public Button btnCloseArtikliKarticu;
    private Stage stageCreateNewArtikl;

    @FXML
    public BorderPane artikliUiBorderPane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    /**
     * Seter metoda koja se koristi u {@link AutoServisController #openAddEditklijent()}-u
     * {@code isWeAreInEditMode = true} nas obavestava da smo u EDIT MODU iz
     * {@link AutoServisController #btnOpenNoviKlijentGui(ActionEvent)}
     *
     * @param autoServisController referenca ka {@link AutoServisController} kontroloru
     * @see AutoServisController
     */
    public void setAutmobilController(AutoServisController autoServisController, Stage stageCreateNewArtikl) {
        this.autoServisController.set(autoServisController);
        this.stageCreateNewArtikl = stageCreateNewArtikl;
        //isWeAreInEditMode = true;
    }

    public void btnCloseArtikliKarticuAct(MouseEvent mouseEvent) {
        btnCloseArtikliKarticu.fireEvent(new WindowEvent(stageCreateNewArtikl, WindowEvent.WINDOW_CLOSE_REQUEST));
    }
}
