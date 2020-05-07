package rs.acreno.automobil;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
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
import rs.acreno.racuni.faktura.EditRacunController;
import rs.acreno.racuni.faktura.FakturaController;
import rs.acreno.system.constants.Constants;
import rs.acreno.system.exeption.AcrenoException;
import rs.acreno.system.util.ActionButtonTableCell;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicReference;

public class AutomobiliController implements Initializable {

    @FXML private TextField txtFieldRegOznaka;
    @FXML private TextField txtFieldImeKlijenta;

    //TABELA FAKTURE
    @FXML private TableView<Racun> tblFakture;
    @FXML private TableColumn<Racun, Integer> tblRowIdRacuna;
    @FXML private TableColumn<Racun, Integer> tblRowIdAutomobila;
    @FXML private TableColumn<Racun, String> tblRowDatumRacuna;
    @FXML private TableColumn<Racun, Integer> tblRowPopustRacuna;
    @FXML private TableColumn<Racun, String> tblRowNapomeneRacuna;
    @FXML private TableColumn<Racun, Button> tblRowBtnIzmeniRacun;

    /**
     * Empty AutomobilController Constructor
     */
    public AutomobiliController() {
    }

    private ObservableList<Automobil> automobil;

    /**
     * Setovanje {@link Automobil} objekta preko seter metode, a u {@link AutoServisController}-u
     * Omoguceno preko {@link #setAutoServisController(AutoServisController, Stage)}
     *
     * @see Automobil
     * @see AutoServisController
     */
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

    private ObservableList<Klijent> klijenti;

    /**
     * Setovanje {@link Klijent} objekta preko seter metode, a u {@link AutoServisController}-u
     * Omoguceno preko {@link #setAutoServisController(AutoServisController, Stage)}
     *
     * @see Klijent
     * @see AutoServisController
     */
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
     * Inicijalizacija {@link AutomobiliController}-a
     * {@code tblRowBtnIzmeniRacun.setCellFactory} postavlje dugmice u {@link #tblFakture} tabelu Racuna.
     * Dugmici otvaraju {@link #btnOpenEditRacunUiAction(Racun)}
     * <p>
     * Setuje se REG. TABLICA{@code txtFieldRegOznaka} i IME KLIJENTA{@code txtFieldImeKlijenta}
     * {@code  txtFieldRegOznaka.setText(getAutomobil().get(0).getRegOznaka())} Moze jer je samo jedan Automobil
     * {@code  txtFieldImeKlijenta.setText(klijenti.get(0).getImePrezime()} Moze jer je samo jedan Klijent
     * <p>
     * Pa nakon toga se popunjava tabela sa racunima u {@link #popuniTabeluRacuni()}
     *
     * @param location  gde da ucitamo
     * @param resources da li ima nesto u resource
     * @see #btnOpenEditRacunUiAction(Racun)
     * @see #popuniTabeluRacuni()
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Platform.runLater(() -> {

            //Inicijalizacija i Postavljenje dugmeta u tabeli racuni
            tblRowBtnIzmeniRacun.setCellFactory(ActionButtonTableCell.forTableColumn("Izmeni", (Racun p) -> {
                btnOpenEditRacunUiAction(p);
                //tblPosaoArtikli.getItems().remove(p);
                return p;
            }));

            txtFieldRegOznaka.setText(getAutomobil().get(0).getRegOznaka());// Moze jer je samo jedan Automobil
            txtFieldImeKlijenta.setText(klijenti.get(0).getImePrezime());// Moze jer je samo jedan Klijent

            popuniTabeluRacuni(); // Popuni tabelu Racuni sa podacima

        });
    }

    /**
     * Inicijalizacija Racuni Objekta iz DBa {@link SQLRacuniDAO}
     */
    private final RacuniDAO racuniDAO = new SQLRacuniDAO();

    /**
     * ObservableList racuni koja cuva sve filtrirane objemte po ID AUTOMOBILA {@link RacuniSearchType#ID_AUTOMOBILA}
     */
    private ObservableList<Racun> racuni;

    /**
     * Popunjavanje tabele "tblFakture" sa Fakturama filtriranim po ID AUTOMOBILU.
     * {@code getAutomobil().get(0).getIdAuta()} moze jer ima samo jedan auto sa tom REG. TABLICOM
     */
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
     * Otvaranje {@link FakturaController} UI-a i komunikacija izmedju ova dva kontrolora. {@code onMouseClick}
     * Komunikacija se implemetira {@link FakturaController#setAutmobilController(AutomobiliController, Stage)}
     * Takodje se prosledjuje i STAGE u slucaju da zatreba.
     * Nakon toga se incijalizuje Title preko {@code stageFaktura.setTitle }
     *
     * @throws IOException ako nije nadjen .fxml {@link Constants#FAKTURA_UI_VIEW_URI}
     * @see FakturaController#setAutmobilController(AutomobiliController, Stage)
     */
    @FXML
    public void btnOpenFakturaUi() throws IOException {
        FXMLLoader fxmlLoaderFaktura = new FXMLLoader(getClass().getResource(Constants.FAKTURA_UI_VIEW_URI));
        Stage stageFaktura = new Stage();
        stageFaktura.initModality(Modality.APPLICATION_MODAL);
        stageFaktura.setScene(new Scene(fxmlLoaderFaktura.load()));

        //Inicijalizacija FakturaController-a i setovanje naslova
        FakturaController fakturaController = fxmlLoaderFaktura.getController();
        fakturaController.setAutmobilController(this, stageFaktura);
        //Postavi Title u stageu FakturaController
        stageFaktura.setTitle("Registarska Oznaka: " + txtFieldRegOznaka.getText()
                + " || Klijent: " + txtFieldImeKlijenta.getText());

        stageFaktura.showAndWait();
    }

    /**
     * Metoda koja otvara Editovanje racuna {@link EditRacunController} UI
     * i prosledjuje CONTROLLER i STAGE u {@link AutomobiliController}.
     * Implementirana je u {@link #initialize(URL, ResourceBundle)} {@code tblRowBtnIzmeniRacun.setCellFactory} delu.
     *
     * @author Dejan Cugalj
     * @see EditRacunController
     * @see AutomobiliController
     */
    public void btnOpenEditRacunUiAction(@NotNull Racun racun) {
        FXMLLoader fxmlLoaderEditRacun = new FXMLLoader(getClass().getResource(Constants.EDIT_RACUN_UI_VIEW));
        Stage stageEditRacun = new Stage();
        stageEditRacun.initModality(Modality.APPLICATION_MODAL);
        try {
            stageEditRacun.setScene(new Scene(fxmlLoaderEditRacun.load()));

            //Inicijalizacija EDIT RACUN Kontrolora
            EditRacunController editRacunController = fxmlLoaderEditRacun.getController();
            editRacunController.setAutomobiliController(this, stageEditRacun);
            stageEditRacun.setTitle("TEST ID RACUNA: " + racun.getIdRacuna());
            editRacunController.setIdRacuna(racun.getIdRacuna());
            stageEditRacun.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Zatvori prozor Automobili
     *
     * @param actionEvent posto koristimo sakrivanje prozara
     */
    @FXML
    public void btnZatvoriProzorAutomobiliAction(@NotNull ActionEvent actionEvent) {
        ((Stage) (((Button) actionEvent.getSource()).getScene().getWindow())).close();
    }

}

