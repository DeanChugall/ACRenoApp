package rs.acreno.nalozi.print_blanko_nalozi;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;
import rs.acreno.system.util.GeneralUiUtility;

import java.net.URL;
import java.util.ResourceBundle;

public class PrintBlankoNaloziController implements Initializable {

    //FXMLs  STAFF
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


    @Override public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    @FXML private void btnPrintAct(ActionEvent actionEvent) {
        ancorPanePrint.requestFocus(); // remove focus from table for print

        GeneralUiUtility.printStaff(ancorPanePrint, btnPrint, btnPrintZatvori);

    }

    @FXML private void btnZatvoriPrintAct(@NotNull ActionEvent actionEvent) {
        ((Stage) (((Button) actionEvent.getSource()).getScene().getWindow())).close();
    }
}
