package rs.acreno.defektaza.print_defektaza;

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
import rs.acreno.defektaza.DefektazaController;
import rs.acreno.system.util.GeneralUiUtility;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicReference;

public class PrintDefektazaController implements Initializable {

    @FXML private Button btnZatvoriPrintDefektaza;
    @FXML private AnchorPane ancorPanePrint;
    @FXML private Button btnPrintDefektaza;

    @FXML private TextField txtFidDefektaze;
    @FXML private TextField txtRegTablica;
    @FXML private TextField txtfKilometraza;
    @FXML private TextField txtfDatumDefektaze;
    @FXML private TextField txtfVreme;
    @FXML private TextArea txtAreaopisDefektaze;
    @FXML private TextArea txtAreaDetaljiDefektaze;
    @FXML private TextField txtfKlijent;

    /**
     * Referenca ka {@link DefektazaController}-u, ako slucajno zatreba nesto iz tog kontrolora
     */
    private final AtomicReference<Stage> stageDefektaza = new AtomicReference<>();
    private DefektazaController defektazaController;

    public void setDefektazaController(DefektazaController defektazaController, Stage stageDefektaza) {
        this.defektazaController = defektazaController;
        this.stageDefektaza.set(stageDefektaza);
    }

    @Override public void initialize(URL url, ResourceBundle resourceBundle) {
        Platform.runLater(() -> {
            txtFidDefektaze.setText(defektazaController.getTxtfIdDefektaze().getText());
            txtfKlijent.setText(defektazaController.getTxtfKlijent().getText());
            txtRegTablica.setText(defektazaController.getTxtfRegOznaka().getText());
            txtfKilometraza.setText(defektazaController.getTxtfKilometraza().getText());
            txtfDatumDefektaze.setText(GeneralUiUtility.formatDateForUs(defektazaController.getDatePickerDatum().getValue()));
            txtfVreme.setText(defektazaController.getTxtfVreme().getText());
            txtAreaopisDefektaze.setText(defektazaController.getTxtAreaOpisDefektaze().getText());
            txtAreaDetaljiDefektaze.setText(defektazaController.getTxtAreOstaliDetaljiDefektaze().getText());
        });
    }

    /**
     * TODO: Napisati JAVA DOC
     *
     * @param actionEvent
     */
    public void btnPrintActDefektazaAct(ActionEvent actionEvent) {
        ancorPanePrint.requestFocus(); // remove focus from table for print
        GeneralUiUtility.printStaff(ancorPanePrint, btnPrintDefektaza, btnZatvoriPrintDefektaza);
    }

    /**
     * TODO: Napisati JAVA DOC
     *
     * @param actionEvent
     */
    @FXML private void btnZatvoriPrintDefektazaActtion(@NotNull ActionEvent actionEvent) {
        ((Stage) (((Button) actionEvent.getSource()).getScene().getWindow())).close();
    }
}
