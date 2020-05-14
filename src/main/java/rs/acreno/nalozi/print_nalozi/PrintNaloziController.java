package rs.acreno.nalozi.print_nalozi;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;
import rs.acreno.nalozi.RadniNalogController;
import rs.acreno.system.util.GeneralUiUtility;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicReference;

public class PrintNaloziController implements Initializable {

    @FXML private Button btnPrintZatvori;
    @FXML private TextField txtfKlijent;
    @FXML private TextArea txtAreaDetaljiKlijenta;
    @FXML private TextArea txtAreaDetaljiServisera;
    @FXML private TextField txtfDatum;
    @FXML private TextField txtfVreme;
    @FXML private TextField txtfKilometraza;
    @FXML private TextField txtRegTablica;
    @FXML private Button btnPrint;
    @FXML private AnchorPane ancorPanePrint;
    @FXML private TextField txtFidRadnogNaloga;

    /**
     * Referenca ka {@link RadniNalogController}-u, ako slucajno zatreba nesto iz tog kontrolora
     */
    private final AtomicReference<Stage> radniNalogStage = new AtomicReference<>();
    private RadniNalogController radniNalogController;

    public void setRadniNalogController(RadniNalogController radniNalogController, Stage radniNalogStage) {
        this.radniNalogController = radniNalogController;
        this.radniNalogStage.set(radniNalogStage);
    }

    @Override public void initialize(URL url, ResourceBundle resourceBundle) {
        Platform.runLater(() -> {
            txtFidRadnogNaloga.setText(String.valueOf(radniNalogController.getBrojRadnogNaloga()));
            txtRegTablica.setText(radniNalogController.getTxtfRegOznaka());
            txtfKlijent.setText(radniNalogController.getKlijent());
            txtfDatum.setText(radniNalogController.getDatum());
            txtfVreme.setText(radniNalogController.getVreme());
            txtfKilometraza.setText(radniNalogController.getKilometraza());
            txtAreaDetaljiKlijenta.setText(radniNalogController.getDetaljiStranke());
            txtAreaDetaljiServisera.setText(radniNalogController.getDetaljiServisera());
        });
    }

    public void btnPrintAct(ActionEvent actionEvent) {
        ancorPanePrint.requestFocus(); // remove focus from table for print

        GeneralUiUtility.printStaff(ancorPanePrint, btnPrint, btnPrintZatvori);

    }

    @FXML private void btnZatvoriPrintAct(@NotNull ActionEvent actionEvent) {
        ((Stage) (((Button) actionEvent.getSource()).getScene().getWindow())).close();
    }
}
