package rs.acreno.racuni.print_racun;

import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;
import rs.acreno.artikli.posao_artikli_dao.PosaoArtikli;
import rs.acreno.artikli.posao_artikli_dao.PosaoArtikliDAO;
import rs.acreno.artikli.posao_artikli_dao.PosaoArtikliDaoSearchType;
import rs.acreno.artikli.posao_artikli_dao.SQLPosaoArtikliDAO;
import rs.acreno.automobil.Automobil;
import rs.acreno.klijent.Klijent;
import rs.acreno.racuni.Racun;
import rs.acreno.racuni.faktura.FakturaController;
import rs.acreno.system.exeption.AcrenoException;
import rs.acreno.system.util.DragAndDropTable;
import rs.acreno.system.util.GeneralUiUtility;
import rs.acreno.system.util.properties.AcrenoProperties;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicReference;

public class PrintRacuniControler implements Initializable {

    //HEADER
    @FXML private AnchorPane ancorPanePrint;
    @FXML private Button btnPrint;
    @FXML private Button btnPrintClose;

    //RACUN FIELDS IN PRINT
    private Racun racun;
    @FXML private TextField txtFidRacuna;
    @FXML private Label lblDatumRacuna;
    @FXML private Label lblDatumPrometa;
    @FXML private Label lblDatumValute;
    @FXML private TextField txtFpopustRacuna;
    @FXML private TextField txtfGrandTotaluRacunu;
    @FXML private TextField txtfImeFirmeNaRaacunu;
    @FXML private TextField txtfAdresaFirmeNaRacunu;
    @FXML private TextField txtfGradFirmeNaRacunu;
    @FXML private TextField txtfZiroRacunFirmeNaRacunu;
    @FXML private TextArea txtfNapomeneServiseraNaRacunu;

    //KLIJENT FIELDS IN PRINT
    private Klijent klijent;
    @FXML private TextField txtFidKlijenta;
    @FXML private TextField txtfImePrezimeKllijenta;
    @FXML private TextField txtfTelefonKlijenta;
    @FXML private TextField txtfEmailKlijenta;
    @FXML private TextField txtfAdresaKlijenta;
    @FXML private TextField txtfGradKlijenta;

    //AUTOMOBIL FIELDS IN PRINT
    private Automobil automobil;
    @FXML private TextField txtfIdAutomobila;
    @FXML private TextField txtfRegTablicaAutomobila;
    @FXML private TextField txtfKilometraza;
    @FXML private TextField txtfVinAutomobila;
    @FXML private TextField txtfModelAutomobila;
    @FXML private TextField txtfMarkaAutomobila;
    @FXML private TextField txtfGodisteAutomobila;
    @FXML private TextField txtfVrstaGorivaAutomobila;
    @FXML private TextField txtfSnagaAutomobila;

    //KALKULACIJE
    @FXML private TextField txtfUkupnoSadPopustomNaDelove;
    @FXML private TextField txtfTotalBezPopusta;
    @FXML private TextField txtfPopustDoleNaRacunu;
    @FXML private TextField txtfGrandTotal;

    //Pretraga Artikala Tabela
    @FXML private TableView<PosaoArtikli> tblPosaoArtikli;
    @FXML private TableColumn<PosaoArtikli, Number> tblRowidPosaoArtikli;
    @FXML private TableColumn<PosaoArtikli, Number> tblRowidRacuna;
    @FXML private TableColumn<PosaoArtikli, Number> tblRowidArtikla;
    @FXML private TableColumn<PosaoArtikli, String> tblRowNazivArtikla;
    @FXML private TableColumn<PosaoArtikli, String> tblRowOpisArtikla;
    @FXML private TableColumn<PosaoArtikli, Number> tblRowCena;
    @FXML private TableColumn<PosaoArtikli, Number> tblRowNabavnaCena;
    @FXML private TableColumn<PosaoArtikli, Number> tblRowKolicina;
    @FXML private TableColumn<PosaoArtikli, String> tblRowJedinicaMere;
    @FXML private TableColumn<PosaoArtikli, Number> tblRowPopust;
    @FXML private TableColumn<PosaoArtikli, Number> tblRowTotal;

    public PrintRacuniControler() {
        Platform.runLater(() -> {
            this.racun = fakturaController.getRacun(); // Inicijaizacija RACUN objekta
            this.klijent = fakturaController.getKlijent();
            this.automobil = fakturaController.getAutomobil();
        });
    }

    /**
     * Referenca ka {@link FakturaController}-u, ako slucajno zatreba nesto iz tog kontrolora
     */
    private final AtomicReference<Stage> fakturaStage = new AtomicReference<>();
    private FakturaController fakturaController;

    public void setFakturaController(FakturaController fakturaController, Stage fakturaStage) {
        this.fakturaController = fakturaController;
        this.fakturaStage.set(fakturaStage);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Platform.runLater(() -> {

            initRacunFields(); //INIT RACUN FIELDS
            initKlijentsFields();//INIT KLIJENT FIELDS
            initAutomobil();//INIT AUTOMOBIL FIELDS

            //INIT KALKULACIJE
            txtfUkupnoSadPopustomNaDelove.setText(fakturaController.getTotalSumaSaPopustomNaDelove()); // Set total sa popustom na delove form Faktura Controller
            txtfTotalBezPopusta.setText(fakturaController.getTotalBezPopustaSuma()); // Set Total bez popusta form Faktura Controller
            txtFpopustRacuna.setText(fakturaController.getPopustRacuna()); // Set Popust RACUNA TF
            txtfPopustDoleNaRacunu.setText(txtFpopustRacuna.getText());
            txtfGrandTotal.setText(fakturaController.getGrandTotalSumaSuma()); // Grand Total suma sa popustom

            //INIT TABLE STAFF
            popuniTabeluPosaoArtikli(); // Popuni tabelu Posao Artikli
            DragAndDropTable.dragAndDropTbl(tblPosaoArtikli); //Rearrange table rows with mouse if need in print Racun/Faktura
            FakturaController.setGrandTotalSuma(tblRowTotal); //Izracunavanje TOTAL sume u tabeli

        });
    }

    public void btnPrintAct(ActionEvent actionEvent) {
        ancorPanePrint.requestFocus(); // remove focus from table for print
        tblPosaoArtikli.getSelectionModel().clearSelection(); // clear selection from table for print
        GeneralUiUtility.printStaff(ancorPanePrint, btnPrint, btnPrintClose);
    }

    /**
     * Popunjavanje tabele {@link #tblPosaoArtikli} sa inicijalizovanim filtriranim {@link PosaoArtikli} Objektom.
     * Inicijalizacija se radi u metodi {@link #initPosaoArtikliDbTable(int)}, tamo za vise detalja.
     * <p>
     * {@code fakturaController.getIdRacuna()} se dobija ID RACUNA iz {@link FakturaController}-a,
     * u metodu {@link FakturaController#getIdRacuna()}. Id Racuna nam sluzi za dobijanje svih {@link PosaoArtikli}
     * vredsnosti koji su vezani za taj ID RACUNA.
     *
     * @see #tblPosaoArtikli
     * @see #initPosaoArtikliDbTable(int)
     * @see FakturaController
     * @see FakturaController#getIdRacuna()
     * @see PosaoArtikli
     */
    private void popuniTabeluPosaoArtikli() {
        ObservableList<PosaoArtikli> posaoArtikli = initPosaoArtikliDbTable(Integer.parseInt(fakturaController.getIdRacuna()));

        //Tbl Coll ID POSAO ARTIKLA
        tblRowidPosaoArtikli.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getIdPosaoArtikli()));
        tblRowidPosaoArtikli.setStyle("-fx-alignment: CENTER;");

        //Tbl Coll ID RACUNA
        tblRowidRacuna.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getIdRacuna()));
        tblRowidRacuna.setStyle("-fx-alignment: CENTER;");

        //Tbl Coll ID ARTIKLA
        tblRowidArtikla.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getIdArtikla()));
        tblRowidArtikla.setStyle("-fx-alignment: CENTER;");

        //Tbl Coll NAZIV ARTKLA
        tblRowNazivArtikla.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getNazivArtikla()));
        tblRowNazivArtikla.setStyle("-fx-alignment: CENTER;");

        //Tbl Coll OPIS ARTIKLA
        tblRowOpisArtikla.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getNazivArtikla()));
        tblRowOpisArtikla.setStyle("-fx-alignment: CENTER;");

        //Tbl Coll KOLICINA
        tblRowKolicina.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getKolicina()));
        tblRowKolicina.setStyle("-fx-alignment: CENTER;");

        //Tbl Coll JEDINICA MERE
        tblRowJedinicaMere.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getJedinicaMere()));
        tblRowJedinicaMere.setStyle("-fx-alignment: CENTER;");

        //Tbl Coll CENA
        tblRowCena.setCellValueFactory(cellData ->
                new SimpleDoubleProperty(cellData.getValue().getCena()));
        tblRowCena.setStyle("-fx-alignment: CENTER;");

        //Tbl Coll POPUST
        tblRowPopust.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getPopust()));
        tblRowPopust.setStyle("-fx-alignment: CENTER;");

        tblPosaoArtikli.setItems(posaoArtikli);
    }

    /**
     * Prilikom ulaza u {@link PrintRacuniControler} potrebno je naci sve artikle iz vezne tabele koji su u korelaciji
     * sa ovim ID Racunom.
     * Filtrira se preko ID RACUNA, a u {@link PosaoArtikliDAO#findPosaoArtikliByPropertyDao(PosaoArtikliDaoSearchType, Object)}
     * <p>
     * Ova metoda je pomocna metoda za {@link #popuniTabeluPosaoArtikli()}.
     *
     * @param idRacuna id racuna kao parametar pretrage u bazi za Posao Artikle veznu Tabelu
     * @return ObservableList<PosaoArtikli>
     * @see PosaoArtikliDAO#findPosaoArtikliByPropertyDao
     * @see #popuniTabeluPosaoArtikli()
     */
    private ObservableList<PosaoArtikli> initPosaoArtikliDbTable(int idRacuna) {
        PosaoArtikliDAO posaoArtikliDAO = new SQLPosaoArtikliDAO();
        ObservableList<PosaoArtikli> posaoArtikli = null;
        try {
            posaoArtikli = FXCollections.observableArrayList(
                    posaoArtikliDAO.findPosaoArtikliByPropertyDao(
                            PosaoArtikliDaoSearchType.ID_RACUNA_POSAO_ARTIKLI_DAO, idRacuna));

        } catch (AcrenoException | SQLException e) {
            e.printStackTrace();
        }
        return posaoArtikli;
    }

    // ******************** INICIJALIZACIJA POLJA *****************

    /**
     * Init {@link Racun} Polja u racunu. Sekcija gde su polja vezana za racun se ovde popunjavaju, a
     * koristi se u {@link #initialize(URL, ResourceBundle)}
     * {@link Racun} objekat smo dobili iz {@link FakturaController}-a
     *
     * @see Racun
     * @see FakturaController
     */
    private void initRacunFields() {
        //INIT RACUN FIELDS
        txtFidRacuna.setText(fakturaController.getIdRacuna()); // SET ID RACUNA/FAKTURE form Faktura Controller
        lblDatumRacuna.setText(racun.getDatum());
        lblDatumPrometa.setText(racun.getDatumPrometa());
        lblDatumValute.setText(racun.getDatumValute());
        txtfGrandTotaluRacunu.setText(fakturaController.getGrandTotalSumaSuma());
        txtfImeFirmeNaRaacunu.setText(AcrenoProperties.getInstance().getProperty("ime.firme"));
        txtfAdresaFirmeNaRacunu.setText(AcrenoProperties.getInstance().getProperty("adresa.firme"));
        txtfGradFirmeNaRacunu.setText(AcrenoProperties.getInstance().getProperty("grad.firme"));
        txtfZiroRacunFirmeNaRacunu.setText(AcrenoProperties.getInstance().getProperty("ziro.racun"));
        txtfNapomeneServiseraNaRacunu.setText(racun.getNapomeneRacuna());
    }

    /**
     * Init {@link Klijent} Polja u racunu. Sekcija gde su polja vezana za Klijenta se ovde popunjavaju, a
     * koristi se u {@link #initialize(URL, ResourceBundle)}
     * {@link Klijent} objekat smo dobili iz {@link FakturaController}-a
     *
     * @see Klijent
     * @see FakturaController
     */
    private void initKlijentsFields() {
        txtFidKlijenta.setText(String.valueOf(klijent.getIdKlijenta()));
        txtfImePrezimeKllijenta.setText(klijent.getImePrezime());
        txtfTelefonKlijenta.setText(klijent.getTelefonMobilni());
        txtfEmailKlijenta.setText(klijent.getEmail());
        txtfAdresaKlijenta.setText(klijent.getUlicaBroj());
        txtfGradKlijenta.setText(klijent.getMesto());
    }

    /**
     * Init {@link Automobil} Polja u racunu. Sekcija gde su polja vezana za Klijenta se ovde popunjavaju, a
     * koristi se u {@link #initialize(URL, ResourceBundle)}
     * {@link Automobil} objekat smo dobili iz {@link FakturaController}-a
     *
     * @see Automobil
     * @see FakturaController
     */
    private void initAutomobil() {
        txtfIdAutomobila.setText(String.valueOf(automobil.getIdAuta()));
        txtfRegTablicaAutomobila.setText(automobil.getRegOznaka());
        txtfKilometraza.setText(automobil.getKilomteraza());
        txtfVinAutomobila.setText(automobil.getVinVozila());
        txtfModelAutomobila.setText(automobil.getModelVozila());
        txtfMarkaAutomobila.setText(automobil.getMarkaVozila());
        txtfGodisteAutomobila.setText(String.valueOf(automobil.getGodisteVozila()));
        txtfVrstaGorivaAutomobila.setText(automobil.getVrstaGorivaVozila());
        txtfSnagaAutomobila.setText(String.valueOf(automobil.getSnagaVozila()));
    }

    /**
     * TODO: Napisati JAVA DOC
     *
     * @param actionEvent
     */
    @FXML private void btnPrintActClose(@NotNull ActionEvent actionEvent) {
        ((Stage) (((Button) actionEvent.getSource()).getScene().getWindow())).close();
    }
}
