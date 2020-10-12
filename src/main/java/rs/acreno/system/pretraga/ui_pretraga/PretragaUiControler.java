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
import rs.acreno.automobil.*;
import rs.acreno.autoservis.AutoServisController;
import rs.acreno.defektaza.*;
import rs.acreno.klijent.Klijent;
import rs.acreno.klijent.KlijentDAO;
import rs.acreno.klijent.KlijentSearchType;
import rs.acreno.klijent.SQLKlijnetDAO;
import rs.acreno.nalozi.RadniNalog;
import rs.acreno.nalozi.RadniNalogDAO;
import rs.acreno.nalozi.RadniNalogSearchType;
import rs.acreno.nalozi.SQLRadniNalogDAO;
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

    //FXMLs TABELA RADNI NALOZI
    @FXML private TableView<RadniNalog> tblRadniNalozi;
    @FXML private TableColumn<RadniNalog, Number> tblColIdRadniNaloga;
    @FXML private TableColumn<RadniNalog, Number> tblColRadniNalogIdAutomobila;
    @FXML private TableColumn<RadniNalog, String> tblColRadniNalogDatum;
    @FXML private TableColumn<RadniNalog, String> tblColRadniNalogVreme;
    @FXML private TableColumn<RadniNalog, String> tblColRadniNalogKilometraza;
    @FXML private TableColumn<RadniNalog, String> tblColRadniNalogDetaljiStranke;
    @FXML private TableColumn<RadniNalog, String> tblColRadniNalogDetaljiServisera;
    @FXML private TableColumn<RadniNalog, Button> tblColRadniNalogBtnIzmeni;

    //FXMLs TABELA DEFEKTAZE
    @FXML private TableView<Defektaza> tblDefektaza;
    @FXML private TableColumn<Defektaza, Number> tblColIdDefektaze;
    @FXML private TableColumn<Defektaza, Number> tblColIdAutaDefektaze;
    @FXML private TableColumn<Defektaza, String> tblColKilometraza;
    @FXML private TableColumn<Defektaza, String> tblColDatumDefektaze;
    @FXML private TableColumn<Defektaza, String> tblColVreme;
    @FXML private TableColumn<Defektaza, String> tblColOpisDefektaze;
    @FXML private TableColumn<Defektaza, String> tblColOstaliDetaljiDefektaze;
    @FXML private TableColumn<Defektaza, Button> tblColBtnIzmeniDefektazu;

    private int brojFakture;
    private int brojRadnogNaloga;
    private int brojDefektaze;
    private Racun racun;
    private RadniNalog radniNalog;
    private Defektaza defektaza;

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
                e.printStackTrace();
            }
            // Listen for text changes in the filter text field
            txtfPretragaRacuna.textProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observable,
                                    String oldValue, String newValue) {

                    popuniTabeluRacuni(newValue);

                }
            });


            // popuniTabeluRacuni(); // Popuni tabelu RACUNI sa podacima
            popuniTabeluRadniNalozi(); // Popuni tabelu RADNI NALOZI sa podacima
            popuniTabeluDefektaza(); // Popuni tabelu DEFEKTAZE sa podacima
            //Inicijalizacija i Postavljenje dugmeta "IZMENI" u tabeli RACUNI
            tblRowBtnIzmeniRacun.setCellFactory(ActionButtonTableCell.forTableColumn("Pogledaj", (Racun p) -> {
                // btnOpenEditRacunUiAction(p);
                try {
                    brojFakture = p.getIdRacuna(); // Setuj broj fakture jer je EDIT MODE
                    racun = p;
                    btnOpenFakturaUi(); //Otvori Fakturu UI u EDIT MODU...Provera je u FAKTURA CONTROLORU

                } catch (IOException e) {
                    e.printStackTrace();
                }
                //tblPosaoArtikli.getItems().remove(p);
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

            automobili = FXCollections.observableArrayList(
                    automobilDAO.findAutomobilByProperty(AutoSearchType.ID_AUTA, racuni.get(0).getIdAutomobila()));

            klijenti = FXCollections.observableArrayList(
                    klijentDAO.findKlijentByProperty(KlijentSearchType.ID_KLIJENTA, "14"));

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
        });
        //Inicijalizacija FakturaController-a i setovanje naslova
        FakturaController fakturaController = fxmlLoaderFaktura.getController();
        fakturaController.setPretragaUiControler(this, stageFaktura);
        fakturaController.setBrojFakture(brojFakture);//Prosledi u FakturaView broj fakture (EDIT MODE)
        fakturaController.setEditRacun(racun); //Prosledi u RACUN Objekat broj fakture (EDIT MODE)

        stageFaktura.showAndWait();

    }


    // 4.0 *************** RADNI NALOZI ***************************

    /**
     * Inicijalizacija Radni Nalog Objekta iz DBa {@link SQLRadniNalogDAO}
     */
    private final RadniNalogDAO radniNalogDAO = new SQLRadniNalogDAO();

    /**
     * ObservableList racuni koja cuva sve filtrirane objemte po ID AUTOMOBILA {@link RadniNalogSearchType#ID_AUTOMOBILA}
     */
    private ObservableList<RadniNalog> radniNalozi;

    /**
     * Popunjavanje tabele {@link #tblRadniNalozi} sa Radnim Nalozima filtriranim po ID AUTOMOBILU.
     * {@code getAutomobil().get(0).getIdAuta()} moze jer ima samo jedan auto sa tom REG. TABLICOM.
     */
    private void popuniTabeluRadniNalozi() {
        try {
            radniNalozi = FXCollections.observableArrayList(radniNalogDAO.findAllRadniNalog());
        } catch (AcrenoException | SQLException e) {
            e.printStackTrace();
        }
        //tblCol ID RADNOG NALOGA
        tblColIdRadniNaloga.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getIdRadnogNaloga()));
        tblColIdRadniNaloga.setStyle("-fx-alignment: CENTER;");

        //tblCol ID AUTOMOBILA
        tblColRadniNalogIdAutomobila.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getIdAutomobila()));
        tblColRadniNalogIdAutomobila.setStyle("-fx-alignment: CENTER;");

        //tblCol DATUM RADNOG NALOGA
        tblColRadniNalogDatum.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getDatum()));
        tblColRadniNalogDatum.setStyle("-fx-alignment: CENTER;");

        //tblCol VREME RADNOG NALOGA
        tblColRadniNalogVreme.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getVreme()));
        tblColRadniNalogVreme.setStyle("-fx-alignment: CENTER;");

        //tblCol KILOMETRAZA RADNOG NALOGA
        tblColRadniNalogKilometraza.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getKilometraza()));
        tblColRadniNalogKilometraza.setStyle("-fx-alignment: CENTER;");

        //tblCol DETALJI STRANKE RADNOG NALOGA
        tblColRadniNalogDetaljiStranke.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getDetaljiStranke()));
        tblColRadniNalogDetaljiStranke.setStyle("-fx-alignment: CENTER;");

        //tblCol DETALJI SERVISERA RADNOG NALOGA
        tblColRadniNalogDetaljiServisera.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getDetaljiServisera()));
        tblColRadniNalogDetaljiServisera.setStyle("-fx-alignment: CENTER;");

        //Set Table "Radni Nalozi" Data
        tblRadniNalozi.setItems(radniNalozi);
    }


    @FXML public void btnOpenNoviRadniNalog() throws IOException {
        FXMLLoader fxmlLoaderRadniNalog = new FXMLLoader(getClass().getResource(Constants.RADNI_NALOZI_UI_VIEW_URI));
        Stage stageRadniNalog = new Stage();
        stageRadniNalog.initModality(Modality.APPLICATION_MODAL);
        stageRadniNalog.setResizable(false);
        stageRadniNalog.getIcons().add(new Image(AutoServisController.class.getResourceAsStream(Constants.APP_ICON)));
        stageRadniNalog.setScene(new Scene(fxmlLoaderRadniNalog.load()));

        stageRadniNalog.setOnCloseRequest(windowEvent -> {
            popuniTabeluRadniNalozi(); //Popuni tabelu jer kada se pravi novi Radni nalog nece da se refresuje
            tblRadniNalozi.refresh(); //Uradi refresh tabele da se vide izmene
        });


        stageRadniNalog.showAndWait();
    }


    // 5.0 *************** DEFEKTAZA ***************************

    /**
     * Inicijalizacija Defektaza Objekta iz DBa {@link SQLDefektazaDAO}
     */
    private final DefektazaDAO defektazaDAO = new SQLDefektazaDAO();

    /**
     * ObservableList Defektaza koja cuva sve filtrirane objemte po ID AUTOMOBILA {@link DefektazaSearchType#ID_AUTA}
     */
    private ObservableList<Defektaza> defektaze;

    /**
     * Popunjavanje tabele {@link #tblDefektaza} sa Defektaza filtriranim po ID AUTOMOBILU.
     * {@code getAutomobil().get(0).getIdAuta()} moze jer ima samo jedan auto sa tom REG. TABLICOM.
     */
    private void popuniTabeluDefektaza() {
        try {
            defektaze = FXCollections.observableArrayList(
                    defektazaDAO.findAllDefektaza());
        } catch (AcrenoException | SQLException e) {
            e.printStackTrace();
        }

        //tblCol ID DEFEKTAZE
        tblColIdDefektaze.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getIdDefektaze()));
        tblColIdRadniNaloga.setStyle("-fx-alignment: CENTER;");

        //tblCol ID AUTOMOBILA
        tblColIdAutaDefektaze.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getIdAuta()));
        tblColIdAutaDefektaze.setStyle("-fx-alignment: CENTER;");

        //tblCol KILOMETRAZA DEFEKTAZE
        tblColKilometraza.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getKilometraza()));
        tblColKilometraza.setStyle("-fx-alignment: CENTER;");

        //tblCol Datum  DEFEKTAZE
        tblColDatumDefektaze.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getDatumDefektaze()));
        tblColDatumDefektaze.setStyle("-fx-alignment: CENTER;");

        //tblCol VEME  DEFEKTAZE
        tblColVreme.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getVreme()));
        tblColVreme.setStyle("-fx-alignment: CENTER;");

        //tblCol OPIS  DEFEKTAZE
        tblColOpisDefektaze.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getOpisDefektaze()));
        tblColOpisDefektaze.setStyle("-fx-alignment: CENTER;");

        //tblCol OSTALI DETALJI  DEFEKTAZE
        tblColOstaliDetaljiDefektaze.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getOstaliDetaljiDefektaze()));
        tblColOstaliDetaljiDefektaze.setStyle("-fx-alignment: CENTER;");

        //Set Table "Defektaza" Data
        tblDefektaza.setItems(defektaze);
    }

    /**
     * Otavranje {@link DefektazaController}-a UI-a, setovanje {@code OnCloseRequest(windowEvent} koji
     * se koristi za update tabele u {@link #popuniTabeluDefektaza()} u {@link AutomobiliController}-u.
     * <p>
     * {@code tblDefektaza.refresh()} mora da se refresuje tabela {@link #tblDefektaza} kada se naprave izmene
     * u {@link DefektazaController} UI-u. Drugim recima, kada se unesu neke vrednosti defektaze i sacuvaju
     * pa se zatvori Defektaza UIa, mora da se popuni i u {@link AutomobiliController} tabele defektaze.
     * <p>
     * Inicijalizacija {@link DefektazaController}-a da bi smo mogli da pristupimo poljima, ali najbitnije
     * da prosledimo broj defektaze jer odavde mozemo da idemo i u EDIT MODE. EDIT MODE se proverava
     * u {@link DefektazaController #newOrEditDefektaza(boolean)}
     *
     * @throws IOException Location exception
     * @see DefektazaController#setEditDefektaza(Defektaza)
     * @see DefektazaController #newOrEditDefektaza(boolean)
     * @see #popuniTabeluDefektaza()
     */
    @FXML public void btnOpenDefektaza() throws IOException {
        FXMLLoader fxmlLoaderDefektaza = new FXMLLoader(getClass().getResource(Constants.DEFEKTAZA_UI_VIEW_URI));
        Stage stageDefektaza = new Stage();
        stageDefektaza.initModality(Modality.APPLICATION_MODAL);
        stageDefektaza.setResizable(false);
        stageDefektaza.getIcons().add(new Image(AutoServisController.class.getResourceAsStream(Constants.APP_ICON)));
        stageDefektaza.setScene(new Scene(fxmlLoaderDefektaza.load()));

        stageDefektaza.setOnCloseRequest(windowEvent -> {
            popuniTabeluDefektaza(); //Popuni tabelu jer kada se pravi nova Defektaza nece da se refresuje.
            tblDefektaza.refresh(); //Uradi refresh tabele Defektaza da se vide izmene.
        });

        stageDefektaza.showAndWait();
    }

    public void btnActZatvoriPretragu(ActionEvent actionEvent) {
        ((Stage) (((Button) actionEvent.getSource()).getScene().getWindow())).close();
    }

    public void btnPonistiPretraguAct(ActionEvent actionEvent) {
        System.out.println("PONISTI PRETRAGU");
    }

    public ObservableList<Automobil> getAutomobil() {
        return automobili;
    }

    Klijent klijent = new Klijent();

    public ObservableList<Klijent> getKlijenti() {

        return klijenti;

    }
}
