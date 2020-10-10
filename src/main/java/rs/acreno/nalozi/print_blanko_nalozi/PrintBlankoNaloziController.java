package rs.acreno.nalozi.print_blanko_nalozi;

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
import rs.acreno.system.util.GeneralUiUtility;

import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
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
        Platform.runLater(() -> {
            //Formatiranje Vremena
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("kk:mm:ss");
            LocalDateTime now = LocalDateTime.now();
            txtfVreme.setText(dtf.format(now));
            //******************************
            DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy.");
            Date date = new Date();
            txtfDatum.setText(dateFormat.format(date));
        });

    }

    @FXML private void btnPrintAct(ActionEvent actionEvent) {
        ancorPanePrint.requestFocus(); // remove focus from table for print

        GeneralUiUtility.printStaff(ancorPanePrint, btnPrint, btnPrintZatvori);

    }

    @FXML private void btnZatvoriPrintAct(@NotNull ActionEvent actionEvent) {
        ((Stage) (((Button) actionEvent.getSource()).getScene().getWindow())).close();
    }
}
