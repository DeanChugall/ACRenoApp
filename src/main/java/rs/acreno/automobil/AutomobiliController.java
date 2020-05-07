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
import rs.acreno.racuni.print_racun.UiPrintRacuniControler;
import rs.acreno.system.constants.Constants;
import rs.acreno.system.exeption.AcrenoException;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicReference;

public class AutomobiliController implements Initializable {

    //TOP MENU
    @FXML private Button btnNoviRacun;

    //TABELA FAKTURE
    @FXML private TableView<Racun> tblFakture;
    @FXML private TableColumn<Racun, Integer> tblRowIdRacuna;
    @FXML private TableColumn<Racun, Integer> tblRowIdAutomobila;
    @FXML private TableColumn<Racun, String> tblRowDatumRacuna;
    @FXML private TableColumn<Racun, Integer> tblRowPopustRacuna;
    @FXML private TableColumn<Racun, String> tblRowNapomeneRacuna;

    @FXML private Button btnClosePopup;
    @FXML private TextField txtFieldRegOznaka;
    @FXML private TextField txtFieldImeKlijenta;

    /**
     * Setovanje {@link Automobil} objekta preko seter metode, a u {@link AutoServisController}-u
     * Omoguceno preko {@link #setAutoServisController(AutoServisController, Stage)}
     *
     * @param automobil prosldjivanje Autmobil Objekta {@link AutoServisController} preko setovanog kontrolora.
     * @see Automobil
     * @see AutoServisController
     */
    private ObservableList<Automobil> automobil;
    public void setAutomobil(ObservableList<Automobil> automobil) {
        this.automobil = automobil;
    }

    /**
     * Geter za prosledjen Automobil objekat iz {@link AutoServisController}-a. Dobijen iz {@link #setAutomobil}
     * Prosledjuje se naknadno u {@link FakturaController #initGUI()}
     *
     * @return ObservableList<Automobil>
     * @see FakturaController
     * @see AutoServisController
     */
    public ObservableList<Automobil> getAutomobil() {
        return automobil;
    }

    /**
     * Setovanje {@link Klijent} objekta preko seter metode, a u {@link AutoServisController}-u
     * Omoguceno preko {@link #setAutoServisController(AutoServisController, Stage)}
     *
     * @param klijenti prosldjivanje Klijent Objekta {@link AutoServisController} preko setovanog kontrolora.
     * @see Klijent
     * @see AutoServisController
     */
    private ObservableList<Klijent> klijenti;
    public void setKlijenti(ObservableList<Klijent> klijenti) {
        this.klijenti = klijenti;
    }

    /**
     * Geter za prosledjen Klijent objekat iz {@link AutoServisController}-a. Dobijen iz {@link #setKlijenti}
     * Prosledjuje se naknadno u {@link FakturaController #initGUI()}
     *
     * @return ObservableList<Klijent>
     * @see FakturaController
     * @see AutoServisController
     */
    public ObservableList<Klijent> getKlijenti() {
        return klijenti;
    }



    /**
     * stageAutoSerivs referenca ako bude zatrebalo
     */
    private final AtomicReference<Stage> stageAutoSerivs = new AtomicReference<>();

    /**
     * autoServisController referenca ako bude zatrebalo
     */
    private final AtomicReference<AutoServisController> autoServisController = new AtomicReference<>();


    /**
     * Seter metoda koja se koristi u {@link AutoServisController #showAutomobiliUi()}-u
     * Preko nje mozemo da prosledimo Klijent i Automobil Objekat ovde,
     * a iz {@link AutoServisController #showAutomobiliUi()}-a
     *
     * @param autoServisController referenca ka auto servis kontroloru
     * @param stageAutoServis      refereca ka Stage-u auto servisu
     * @see AutoServisController
     * @see #setKlijenti(ObservableList)
     * @see #setAutomobil(ObservableList)
     */
    public void setAutoServisController(AutoServisController autoServisController, Stage stageAutoServis) {
        this.autoServisController.set(autoServisController);
        this.stageAutoSerivs.set(stageAutoServis);
    }

    /**
     * Empty AutomobilController Constructor
     */
    public AutomobiliController() {
    }


    private Stage stageFaktura;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Platform.runLater(() -> {
            btnClosePopup.setOnAction(e -> ((Stage) (((Button) e.getSource()).getScene().getWindow())).close());

            btnNoviRacun.setOnMouseClicked(e -> {
                try {

                    FXMLLoader loader = new FXMLLoader(getClass().getResource(Constants.FAKTURA_UI_VIEW_URI));
                    stageFaktura = new Stage();
                    stageFaktura.initModality(Modality.APPLICATION_MODAL);
                    stageFaktura.setScene(new Scene(loader.load()));
                    initUiFakturaControler(loader);
                    stageFaktura.showAndWait();

                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });
            txtFieldRegOznaka.setText(getAutomobil().get(0).getRegOznaka());
            txtFieldImeKlijenta.setText(klijenti.get(0).getImePrezime());
            popuniTabeluRacuni();
        });
    }



    /**
     * TODO: Napisati Java DOC
     */
    //Inicijalizacija Racuni Objekta
    private final RacuniDAO racuniDAO = new SQLRacuniDAO();
    private ObservableList<Racun> racuni;
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

    /**
     * Inicijalizacija {@link UiPrintRacuniControler}, a implementira se {@link #initialize}
     *
     * @param fxmlLoader prosledjivanje FXMLoadera {@link UiPrintRacuniControler} - u
     * @see UiPrintRacuniControler
     */
    private void initUiFakturaControler(@NotNull FXMLLoader fxmlLoader) {
        FakturaController fakturaController = fxmlLoader.getController();
        fakturaController.setAutmobilController(this, stageFaktura);
        stageFaktura.setTitle("Registarska Oznaka: " + txtFieldRegOznaka.getText()
                + " || Klijent: " + txtFieldImeKlijenta.getText());
    }
}

