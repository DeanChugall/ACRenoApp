package rs.acreno.automobil;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;
import rs.acreno.autoservis.AutoServisController;
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

    //TOP MENU
    public Button btnNoviRacun;

    //TABELA FAKTURA
    public TableView<Racun> tblFakture;
    public TableColumn<Racun, Integer> tblRowIdRacuna;
    public TableColumn<Racun, Integer> tblRowIdAutomobila;
    public TableColumn<Racun, String> tblRowDatumRacuna;
    public TableColumn<Racun, Integer> tblRowPopustRacuna;
    public TableColumn<Racun, String> tblRowNapomeneRacuna;

    @FXML
    public Button btnClosePopup;
    public TextField txtFieldRegOznaka;
    public TextField txtFieldImeKlijenta;

    private ObservableList<Automobil> automobil;
    private ObservableList<Klijent> klijenti;
    private ObservableList<Racun> racuni;

    public void setAutomobil(ObservableList<Automobil> automobil) {
        this.automobil = automobil;
    }

    public ObservableList<Automobil> getAutomobil() {
        return automobil;
    }

    public void setKlijenti(ObservableList<Klijent> klijenti) {
        this.klijenti = klijenti;
    }

    public ObservableList<Klijent> getKlijenti() {
        return klijenti;
    }


    //Inicijalizacija Racuni Objekta
    private final RacuniDAO racuniDAO = new SQLRacuniDAO();

    private Stage stage;
    private final AutoServisController autoServisController;

    public AutomobiliController(@NotNull AutoServisController autoServisController) {
        this.autoServisController = autoServisController;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(Constants.AUTOMOBILI_UI_VIEW_URI));
            // Set this class as the controller
            loader.setController(this);
            // Load the scene
            stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(loader.load()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showAutmobilStage() {
        stage.setTitle(getAutomobil().get(0).getRegOznaka() + " || " + getKlijenti().get(0).getImePrezime());
        stage.showAndWait();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        btnClosePopup.setOnAction(e -> ((Stage) (((Button) e.getSource()).getScene().getWindow())).close());

        btnNoviRacun.setOnMouseClicked(e -> {
            FakturaController fakturaController = new FakturaController(this);
            fakturaController.showFakturaStage();
        });

        Platform.runLater(() -> {
            txtFieldRegOznaka.setText(getAutomobil().get(0).getRegOznaka());
            txtFieldImeKlijenta.setText(klijenti.get(0).getImePrezime());
            popuniTabeluRacuni();
        });
    }

    private void popuniTabeluRacuni() {
        try {
            racuni = FXCollections.observableArrayList(
                    racuniDAO.findRacunByProperty(RacuniSearchType.ID_AUTOMOBILA, getAutomobil().get(0).getIdAuta()));
        } catch (AcrenoException | SQLException e) {
            e.printStackTrace();
        }
        tblRowIdRacuna.setCellValueFactory(new PropertyValueFactory<>("idRacuna"));
        tblRowIdRacuna.setStyle("-fx-alignment: CENTER;");
        tblRowIdAutomobila.setCellValueFactory(new PropertyValueFactory<>("IdAutomobila"));
        tblRowIdAutomobila.setStyle("-fx-alignment: CENTER;");
        tblRowDatumRacuna.setCellValueFactory(new PropertyValueFactory<>("datum"));
        tblRowDatumRacuna.setStyle("-fx-alignment: CENTER;");
        tblRowPopustRacuna.setCellValueFactory(new PropertyValueFactory<>("popust"));
        tblRowPopustRacuna.setStyle("-fx-alignment: CENTER;");
        tblRowNapomeneRacuna.setCellValueFactory(new PropertyValueFactory<>("napomeneRacuna"));
        tblRowNapomeneRacuna.setStyle("-fx-alignment: CENTER;");
        tblFakture.setItems(racuni);
    }

}

