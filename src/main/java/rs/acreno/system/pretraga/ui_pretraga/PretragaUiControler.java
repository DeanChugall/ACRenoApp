package rs.acreno.system.pretraga.ui_pretraga;

import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.log4j.Logger;
import rs.acreno.automobil.AutoSearchType;
import rs.acreno.automobil.Automobil;
import rs.acreno.automobil.AutomobilDAO;
import rs.acreno.automobil.SQLAutomobilDAO;
import rs.acreno.autoservis.AutoServisController;
import rs.acreno.klijent.Klijent;
import rs.acreno.klijent.KlijentDAO;
import rs.acreno.klijent.KlijentSearchType;
import rs.acreno.klijent.SQLKlijnetDAO;
import rs.acreno.racuni.Racun;
import rs.acreno.racuni.RacuniDAO;
import rs.acreno.racuni.RacuniSearchType;
import rs.acreno.racuni.SQLRacuniDAO;
import rs.acreno.racuni.faktura.FakturaController;
import rs.acreno.system.constants.Constants;
import rs.acreno.system.exeption.AcrenoException;
import rs.acreno.system.util.ActionButtonTableCell;

import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;


public class PretragaUiControler implements Initializable, Serializable {

    private static final long serialVersionUID = 7755070825881292183L;

    private static final Logger logger = Logger.getLogger(PretragaUiControler.class);

    @FXML TextField txtfPretragaRacuna;

    //FXMLs TABELA FAKTURE
    @FXML private TableView<Racun> tblFakture;
    @FXML private TableColumn<Racun, Number> tblRowIdRacuna;
    @FXML private TableColumn<Racun, Number> tblRowIdAutomobila;
    @FXML private TableColumn<Racun, String> tblRowIdKilometraza;
    @FXML private TableColumn<Racun, String> tblRowDatumRacuna;
    @FXML private TableColumn<Racun, Number> tblRowPopustRacuna;
    @FXML private TableColumn<Racun, String> tblRowNapomeneRacuna;
    @FXML private TableColumn<Racun, String> tblRowUradjeno;
    @FXML private TableColumn<Racun, Button> tblRowBtnIzmeniRacun;

    private int brojFakture;
    private Racun racun;

    public PretragaUiControler() {

    }

    private boolean isRacunInEditMode; // Da li je recun u Edit Modu

    public boolean isRacunInEditMode() {
        return isRacunInEditMode =true;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Platform.runLater(() -> {
            try {
                racuni = FXCollections.observableArrayList(racuniDAO.findAllRacune());

            } catch (AcrenoException | SQLException e) {
                logger.error(e.getMessage());
                e.printStackTrace();
            }
            // Listen for text changes in the filter text field
            txtfPretragaRacuna.textProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observable,
                                    String oldValue, String newValue) {

                    //setujRacunBrojFakture();
                    popuniTabeluRacuni(newValue);

                }
            });

            //Inicijalizacija i Postavljenje dugmeta "IZMENI" u tabeli RACUNI
            tblRowBtnIzmeniRacun.setCellFactory(ActionButtonTableCell.forTableColumn("Pogledaj", (Racun p) -> {
                // btnOpenEditRacunUiAction(p);
                try {
                    racun = p;
                    brojFakture = p.getIdRacuna(); // Setuj broj fakture jer je EDIT MODE

                    automobili = FXCollections.observableArrayList(
                            automobilDAO.findAutomobilByProperty(AutoSearchType.ID_AUTA, p.getIdAutomobila()));

                    klijenti = FXCollections.observableArrayList(
                            klijentDAO.findKlijentByProperty(KlijentSearchType.ID_KLIJENTA, automobili.get(0).getIdKlijenta()));

                    btnOpenFakturaUi(); //Otvori Fakturu UI u EDIT MODU...Provera je u FAKTURA CONTROLORU

                } catch (IOException | AcrenoException | SQLException e) {
                    e.printStackTrace();
                }
                return p;
            }));

        });
    }

    // 3.0 *************** FAKTURE / RACUNI ***************************

    /**
     * Inicijalizacija Racuni Objekta iz DBa {@link SQLRacuniDAO}
     */
    private final RacuniDAO racuniDAO = new SQLRacuniDAO();

    private final AutomobilDAO automobilDAO = new SQLAutomobilDAO();

    private final KlijentDAO klijentDAO = new SQLKlijnetDAO();

    /**
     * ObservableList racuni koja cuva sve filtrirane objemte po ID AUTOMOBILA {@link RacuniSearchType#ID_AUTOMOBILA}
     */
    private ObservableList<Racun> racuni;

    private ObservableList<Automobil> automobili;

    private ObservableList<Klijent> klijenti;

    /**
     * Popunjavanje tabele "tblFakture" sa Fakturama filtriranim po ID AUTOMOBILU.
     * {@code getAutomobil().get(0).getIdAuta()} moze jer ima samo jedan auto sa tom REG. TABLICOM
     */
    private void popuniTabeluRacuni(String pretraga) {
        try {

            racuni = FXCollections.observableArrayList(
                    racuniDAO.findRacunByProperty(RacuniSearchType.STA_JE_URADJENO, pretraga));


        } catch (AcrenoException | SQLException e) {
            e.printStackTrace();
        }
        //Tbl Coll ID RACUNA
        tblRowIdRacuna.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getIdRacuna()));
        tblRowIdRacuna.setStyle("-fx-alignment: CENTER;");

        //Tbl Coll ID AUTOMOBILA RACUNA
        tblRowIdAutomobila.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getIdAutomobila()));
        tblRowIdAutomobila.setStyle("-fx-alignment: CENTER;");

        //Tbl Coll KILOMETRAZA RACUNA
        tblRowIdKilometraza.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getKilometraza()));
        tblRowIdKilometraza.setStyle("-fx-alignment: CENTER;");

        //Tbl Coll DATUM RACUNA
        tblRowDatumRacuna.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getDatum()));
        tblRowDatumRacuna.setStyle("-fx-alignment: CENTER;");

        //Tbl Coll POPUST NA RACUNU
        tblRowPopustRacuna.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getPopust()));
        tblRowPopustRacuna.setStyle("-fx-alignment: CENTER;");

        //Tbl Coll NAPOMENA RACUNA
        tblRowNapomeneRacuna.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getNapomeneRacuna()));
        tblRowNapomeneRacuna.setStyle("-fx-alignment: CENTER;");

        //Tbl Coll URADJENO
        tblRowUradjeno.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getStaJeUradjeno()));
        tblRowUradjeno.setStyle("-fx-alignment: CENTER;");


        tblFakture.setItems(racuni);
    }

    @FXML public void btnOpenFakturaUi() throws IOException {
        FXMLLoader fxmlLoaderFaktura = new FXMLLoader(getClass().getResource(Constants.FAKTURA_UI_VIEW_URI));
        Stage stageFaktura = new Stage();
        stageFaktura.initModality(Modality.APPLICATION_MODAL);
        stageFaktura.setResizable(false);
        // stageFaktura.initStyle(StageStyle.UNDECORATED);
        stageFaktura.getIcons().add(new Image(AutoServisController.class.getResourceAsStream(Constants.APP_ICON)));
        stageFaktura.setScene(new Scene(fxmlLoaderFaktura.load()));

        //Refresuje tabelu u povratku u Automobil UI
        stageFaktura.setOnCloseRequest(windowEvent -> {
            // popuniTabeluRacuni(); //Popuni tabelu jer kada se pravi novi racun nece da se refresuje
            tblFakture.refresh(); //Uradi refresh tabele da se vide izmene
            brojFakture = 0;
            racun = null;

        });
        //Inicijalizacija FakturaController-a i setovanje naslova
        FakturaController fakturaController = fxmlLoaderFaktura.getController();
        fakturaController.setPretragaUiControler(this, stageFaktura);
        fakturaController.setBrojFakture(brojFakture);//Prosledi u FakturaView broj fakture (EDIT MODE)
        fakturaController.setEditRacun(racun); //Prosledi u RACUN Objekat broj fakture (EDIT MODE)

        stageFaktura.showAndWait();

    }

    public void btnActZatvoriPretragu(ActionEvent actionEvent) {
        ((Stage) (((Button) actionEvent.getSource()).getScene().getWindow())).close();
    }

    public void btnPonistiPretraguAct(ActionEvent actionEvent) {

    }

    public ObservableList<Automobil> getAutomobil() {
        System.out.println(automobili.get(0).getRegOznaka() +  " ObservableList<Automobil> getAutomobil(): ");
        return automobili;
    }

    Klijent klijent = new Klijent();

    public ObservableList<Klijent> getKlijenti() {

        return klijenti;

    }
}
