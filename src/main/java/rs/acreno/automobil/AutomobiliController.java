package rs.acreno.automobil;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import rs.acreno.klijent.Klijent;
import rs.acreno.racuni.Racun;
import rs.acreno.racuni.RacuniDAO;
import rs.acreno.racuni.RacuniSearchType;
import rs.acreno.racuni.SQLRacuniDAO;
import rs.acreno.racuni.faktura.FakturaController;
import rs.acreno.system.constants.Constants;
import rs.acreno.system.exeption.AcrenoException;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class AutomobiliController implements Initializable {

    @FXML
    public BorderPane automobiliUiBorderPane;
    public Button btnClosePopup;
    public TextField txtFieldRegOznaka;
    public TextField txtFieldImeKlijenta;

    private ObservableList<Automobil> automobil;
    private ObservableList<Klijent> klijenti;
    private ObservableList<Racun> racuni;
    //TOP MENU
    public Button btnNoviRacun;

    //TABELA FAKTURA
    public TableView<Racun> tblFakture;
    public TableColumn tblRowIdRacuna;
    public TableColumn tblRowIdAutomobila;
    public TableColumn tblRowDatumRacuna;
    public TableColumn tblRowPopustRacuna;
    public TableColumn tblRowNapomeneRacuna;

    //Inicijalizacija Racuni Objekta
    private final RacuniDAO racuniDAO = new SQLRacuniDAO();

    public AutomobiliController() {
    }

    public void setAutomobil(ObservableList<Automobil> automobil) {
        this.automobil = automobil;
    }

    public void setKlijenti(ObservableList<Klijent> klijenti) {
        this.klijenti = klijenti;
    }

    public void setRacuni(ObservableList<Racun> racuni) {
        this.racuni = racuni;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // TODO
        btnClosePopup.setOnAction(e -> {
            ((Stage) (((Button) e.getSource()).getScene().getWindow())).close();
        });
        Platform.runLater(() -> {
            txtFieldRegOznaka.setText(automobil.get(0).getRegOznaka());
            txtFieldImeKlijenta.setText(klijenti.get(0).getImePrezime());
            try {
                popuniTabeluRacuni();
            } catch (AcrenoException | SQLException e) {
                e.printStackTrace();
            }
        });
    }


    public void btnNoviRacunMouseEventNovaFaktura(MouseEvent mouseEvent) {
        showFakturaUi();
    }

    private void showFakturaUi() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(FakturaController.class.getResource(Constants.FAKTURA_UI_VIEW_URI));
            Parent root = fxmlLoader.load();

            FakturaController fakturaController = fxmlLoader.getController();
            fakturaController.setAutomobili(automobil);
            fakturaController.setKlijenti(klijenti);
            fakturaController.setRacuni(racuni);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("test");
            stage.setScene(new Scene(root));
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void popuniTabeluRacuni() throws AcrenoException, SQLException {
        tblRowIdRacuna.setCellValueFactory(new PropertyValueFactory<>("idRacuna"));
        tblRowIdRacuna.setStyle("-fx-alignment: CENTER;");
        int IdAutomobila = automobil.get(0).getIdAuta();
        ObservableList<Racun> filteredRacuni = FXCollections.observableArrayList(
                racuniDAO.findRacunByProperty(RacuniSearchType.ID_AUTOMOBILA, IdAutomobila));
        tblFakture.setItems(filteredRacuni);
    }
}

