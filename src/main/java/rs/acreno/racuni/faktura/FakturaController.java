package rs.acreno.racuni.faktura;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleDoubleProperty;
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
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import javafx.util.converter.NumberStringConverter;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import rs.acreno.artikli.Artikl;
import rs.acreno.artikli.ArtikliDAO;
import rs.acreno.artikli.SQLArtikliDAO;
import rs.acreno.artikli.posao_artikli_dao.PosaoArtikli;
import rs.acreno.artikli.posao_artikli_dao.PosaoArtikliDAO;
import rs.acreno.artikli.posao_artikli_dao.PosaoArtikliDaoSearchType;
import rs.acreno.artikli.posao_artikli_dao.SQLPosaoArtikliDAO;
import rs.acreno.automobil.Automobil;
import rs.acreno.automobil.AutomobiliController;
import rs.acreno.autoservis.AutoServisController;
import rs.acreno.klijent.Klijent;
import rs.acreno.racuni.Racun;
import rs.acreno.racuni.RacuniDAO;
import rs.acreno.racuni.SQLRacuniDAO;
import rs.acreno.racuni.print_racun.PrintRacuniControler;
import rs.acreno.system.constants.Constants;
import rs.acreno.system.exeption.AcrenoException;
import rs.acreno.system.util.ActionButtonTableCell;
import rs.acreno.system.util.GeneralUiUtility;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Optional;
import java.util.ResourceBundle;


public class FakturaController implements Initializable {

    private static final Logger logger = Logger.getLogger(FakturaController.class);

    @FXML private Pane panePretragaArtikli;
    @FXML private Pane paneArtikli;
    @FXML private Button btnSacuvajRacun;
    @FXML private Button btnCloseFakture;
    @FXML private Button btnOdustaniObrisiRacun;
    @FXML private TextField txtFidRacuna;
    @FXML private TextField txtFklijentImePrezime;
    @FXML private TextField txtFregTablica;
    @FXML private TextField txtfKilometraza;
    @FXML private TextField txtFieldPretragaArtikla;
    @FXML private DatePicker datePickerDatumRacuna;
    @FXML private DatePicker datePickerDatumPrometa;
    @FXML private DatePicker datePickerDatumValute;
    @FXML private TextArea txtAreaNapomenaRacuna;

    //FXMLs KALKULACIJE TFa
    @FXML private TextField txtFpopustRacuna;
    @FXML private TextField txtfTotalPoCenama;
    @FXML private TextField txtfTotalPoNabavnimCenama;
    @FXML private TextField txtfTotalSaPopustomNaDelove;
    @FXML private TextField txtfGrandTotal;

    // FXMLs ARTICLES FIELDS in Faktura
    @FXML private ListView<Artikl> listViewPretragaArtikli;
    @FXML private ListView<Artikl> listViewPretragaKatalskiBrojArtikli;
    @FXML private TextField txtFidArtikla;
    @FXML private TextField txtfKataloskiBrojArtikla;
    @FXML private TextField txtFcenaArtikla;
    @FXML private TextField txtFnabavnaCenaArtikla;
    @FXML private TextField txtFKolicinaArtikla;
    @FXML private TextField txtFjedinicaMereArtikla;
    @FXML private TextField txtFpopustArtikla;
    @FXML private Button btnDodajArtiklRacun;
    @FXML private TextField txtfNazivArtikla;
    @FXML private TextField txtFopisArtikla;
    @FXML private TextArea txtAreaDetaljiOpisArtikla;

    //FXMLs Pretraga Artikala Tabela
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
    @FXML private TableColumn<PosaoArtikli, String> tblRowDetaljiPosaoArtikl;
    @FXML private TableColumn<PosaoArtikli, Number> tblRowTotal;
    @FXML private TableColumn<PosaoArtikli, Number> tblRowTotalCene;
    @FXML private TableColumn<PosaoArtikli, Number> tblRowTotalNabavneCene;
    @FXML private TableColumn<PosaoArtikli, Button> tblRowButton;

    /**
     * Bitna promenjiva jer se sve bazira na Broju fakture ili ti ID RACUNU
     */
    private int brojFakture;

    //INIT GUI FIELDS
    private int idAutomobila;

    //INIT ObservableList-s
    private ObservableList<Automobil> automobili;
    private ObservableList<Klijent> klijenti;

    //RACUN STAFF OBJECT
    private Racun noviRacun;

    /**
     * Promenjiva koja odredjuje da li ce se pojaviti dijalog o uspesnom cuvanju {@link Racun} jer ga cuvamo i kada se
     * direktno klikne na {@link #btnPrintAction()}. Za Detaljno objasnjenje {@link #btnSacuvajRacunAction()}
     *
     * @see #btnSacuvajRacunAction()
     * @see #btnPrintAction()
     */
    private boolean ifWeAreFromBtnSacuvajRacun = true;

    private ObservableList<Racun> racuni;
    private ObservableList<Artikl> artikli;
    private ObservableList<PosaoArtikli> posaoArtikli;

    //Inicijalizacija Racuni Objekta
    private final RacuniDAO racuniDAO = new SQLRacuniDAO();
    private final PosaoArtikliDAO posaoArtikliDAO = new SQLPosaoArtikliDAO();
    private final ArtikliDAO artikliDAO = new SQLArtikliDAO();

    private AutomobiliController automobiliController;
    private Stage automobilStage;

    public Automobil getAutomobil() {
        return automobili.get(0);
    }

    public Klijent getKlijent() {
        return klijenti.get(0);
    }

    /**
     * Prosledjuje vrednost {@link #txtFidRacuna} koji se koristi u {@link PrintRacuniControler #popuniTabeluPosaoArtikli()}
     * U toj metodi se inicijalizuje FILTRIRAN {@link PosaoArtikli} objekat po ID Racunu,
     * a u metodi {@link PrintRacuniControler #initPosaoArtikliDbTable(int)}.
     * <p>
     * Takodje se koristi i jos u {@link PrintRacuniControler #initRacunFields()}
     *
     * @return String za TF polje u Print Racuna Controloru
     * @see PrintRacuniControler
     * @see PrintRacuniControler #initPosaoArtikliDbTable(int)
     * @see PrintRacuniControler #popuniTabeluPosaoArtikli()
     * @see PosaoArtikli
     */
    public String getIdRacuna() {
        return txtFidRacuna.getText();
    }

    /**
     * Geter za {@link #txtFpopustRacuna} {@link PrintRacuniControler#initialize} polje koje popunjava popust na racunu.
     *
     * @return popust na celom racunu
     * @see #izracunajGrandTotalSaPopustomNaCeoRacun()
     * @see PrintRacuniControler#initialize
     */
    public String getPopustRacuna() {
        return txtFpopustRacuna.getText();
    }

    /**
     * Geter za {@link #txtfTotalSaPopustomNaDelove} total sumu sa popustom na delove u {@link PrintRacuniControler#initialize}.
     * Implementira se u {@link #izracunajTotalSumaSaPopustomNaDelove()} ()}
     *
     * @return suma sa popustom na delove
     * @see #izracunajTotalSumaSaPopustomNaDelove()
     * @see PrintRacuniControler#initialize
     */
    public String getTotalSumaSaPopustomNaDelove() {
        return txtfTotalSaPopustomNaDelove.getText();
    }

    /**
     * Geter za {@link #txtfTotalPoCenama} koji se koristu u  {@link PrintRacuniControler#initialize}
     * Popunjavanje polja za total sa popustom na delove.
     * Implementira se u {@link #izracunajTotalSumaSaPopustomNaDelove()} ()} ()}
     *
     * @return popust na delovima
     * @see #izracunajTotalSumaSaPopustomNaDelove()
     * @see PrintRacuniControler#initialize
     */
    public String getTotalBezPopustaSuma() {
        return txtfTotalPoCenama.getText();
    }

    /**
     * Geter za {@link #txtfGrandTotal} koji se koristu u  {@link PrintRacuniControler#initialize}
     * Popunjavanje polja za GRAND TOTAL u Print racunu, on racuna sve i uzima u obzir popust na ceo racun.
     * Implementira se u {@link #izracunajGrandTotalSaPopustomNaCeoRacun()} ()}
     *
     * @return popust na celom racunu
     * @see #izracunajGrandTotalSaPopustomNaCeoRacun()
     * @see PrintRacuniControler#initialize
     */
    public String getGrandTotalSumaSuma() {
        return txtfGrandTotal.getText();
    }

    /**
     * Posto smo u EDIT modu ne treba da se pravi novi racun nego se RACUN objekat prosledjuje
     * iz #{@link AutomobiliController#btnOpenFakturaUi()}
     *
     * @param noviRacun racun koji se EDITUJE
     * @see AutomobiliController#btnOpenFakturaUi()
     */
    public void setEditRacun(Racun noviRacun) {
        this.noviRacun = noviRacun;
    }

    public Racun getRacun() {
        return noviRacun;
    }

    /**
     * Seter za {@link #brojFakture} koji se inicijalizuje {@link AutomobiliController#btnOpenFakturaUi()}
     *
     * @param brojFakture ID RACUNA
     */
    public void setBrojFakture(int brojFakture) {
        this.brojFakture = brojFakture;
    }

    /**
     * Seter metoda koja se koristi u {@link AutomobiliController#setAutoServisController(AutoServisController, Stage)}-u
     * Takodje se prosledjuje i STAGE ako bude zatrebalo, a iz {@link AutomobiliController #btnOpenFakturaUi()}-a
     * Prosledjeni Automobil i Klijent objekti su iz {@link AutomobiliController}, a impl u {@link #initGUI()}
     *
     * @param autmobilController referenca ka automobil kontroloru
     * @param automobilStage     refereca ka automobil Stage-u
     * @see AutomobiliController
     * @see #automobiliController
     * @see #automobilStage
     */
    public void setAutmobilController(AutomobiliController autmobilController, Stage automobilStage) {
        this.automobiliController = autmobilController;
        this.automobilStage = automobilStage;
    }

    /**
     * Empty Constructor if we need in some case
     */
    public FakturaController() {
    }


    // *************** INICIJALIZACIJA ***************************

    /**
     * Inicijalizacija {@link FakturaController}-a sa potrebni podacima
     * <p>
     * Posatvljanje "delete" dugmica u {@link #tblPosaoArtikli}, za brisanje artikla u racunu i obavestenje po brisanju
     * Brisemo poreko #{@link PosaoArtikliDAO#deletePosaoArtikliDao(PosaoArtikli)}.
     * {@code tblPosaoArtikli.getItems().remove(p)} Brise Artikl iz Table {@link #tblPosaoArtikli}.
     * <p>
     * Posale izracunavamo TOTAL SUMU u koloni {@link #tblRowTotal} zajedno sa popustom i
     * ubacujemo u TF ili LABEL zbog GRAND TOTALA.
     * <p>
     * {@code automobiliController.isRacunInEditMode()} Provera da li smo u EDIT MODU.
     * Ako je EDIT MODE onda prosledjujemo TRUE {@link #newOrEditRacun(boolean)}. Tu nam je objekat RACUN
     * prosledjen iz {@link AutomobiliController#btnOpenFakturaUi()}, pa nakon toga da bi popunili
     * tabelu {@link #tblPosaoArtikli} nalazimo sve objekte {@link PosaoArtikli}-e filtrirane po IDu
     * preko {@link PosaoArtikliDaoSearchType#ID_RACUNA_POSAO_ARTIKLI_DAO}.
     * <p>
     * Popunjavamo {@link #tblPosaoArtikli} taelu sa {@link PosaoArtikli} objektima.
     * <p>
     * Ako nismo u edit modu prosledjujemo FALSE u {@link #newOrEditRacun(boolean)} i pravimo novi {@link Racun} obj.
     *
     * @param url            loaction if we need in some case
     * @param resourceBundle resource if we need in some case
     * @see PosaoArtikli
     * @see PosaoArtikliDAO
     * @see AutomobiliController#btnOpenFakturaUi()
     * @see #newOrEditRacun(boolean)
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        Platform.runLater(() -> {
            try {
                artikli = FXCollections.observableArrayList(artikliDAO.findAllArtikle());
            } catch (AcrenoException | SQLException e) {
                e.printStackTrace();
            }
            //txtFieldPretragaArtikla.setOnKeyReleased(this::txtFieldPretragaArtiklaKeyListener);
            btnDodajArtiklRacun.setOnMouseClicked(this::btnDodajArtiklRacunMouseClick);

            //Postavljenje dugmica DELETE u Tabeli POSAO ARTIKLI
            tblRowButton.setCellFactory(ActionButtonTableCell.forTableColumn("Brisanje", p -> {
                try {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("BRISANJE ARTIKLA");
                    alert.setHeaderText("ARTIKL: " + p.getNazivArtikla());
                    alert.setContentText("Da li želite da obrišete stavku sa računa?");
                    ((Stage) alert.getDialogPane().getScene().getWindow()).getIcons().add(new Image(Constants.APP_ICON));

                    Optional<ButtonType> result = alert.showAndWait();
                    if (result.isPresent()) {
                        if (result.get() == ButtonType.OK) {
                            posaoArtikliDAO.deletePosaoArtikliDao(p);
                            tblPosaoArtikli.getItems().remove(p); //Brisi Artikl i tabele "tblPosaoArtikli"
                        }
                    }
                } catch (AcrenoException | SQLException e) {
                    e.printStackTrace();
                }
                // Izracunavanje TOTAL SUME Sa Popustom na delove i srednjivanje decimala
                double totalSumSaPopustomNaDelove = izracunajTotalSumaSaPopustomNaDelove();
                txtfTotalSaPopustomNaDelove.setText(GeneralUiUtility.formatDecimalPlaces(totalSumSaPopustomNaDelove));
                // Izracunavanje TOTAL SUME CENE i sredjivanje decimala
                double totalSumCene = izracunajTotalRegularneCene();
                txtfTotalPoCenama.setText(GeneralUiUtility.formatDecimalPlaces(totalSumCene));
                // Izracunavanje TOTAL SUME NABAVNE CENE i sredjivanje decimala
                double totalSumPoNabavnimCenama = izracunajTotalNabavneCene();
                txtfTotalPoNabavnimCenama.setText(GeneralUiUtility.formatDecimalPlaces(totalSumPoNabavnimCenama));
                // Izracunavanje GRAND TOTAL SUME Sa popustom na ceo racun i formatiramo decimale
                double totoalSumGrand = izracunajGrandTotalSaPopustomNaCeoRacun();
                txtfGrandTotal.setText(GeneralUiUtility.formatDecimalPlaces(totoalSumGrand));

                return p;
            }));

            //Izracunavanje GRAND TOTAL sume u tabeli Posao Artikli
            setGrandTotalSuma(tblRowTotal);
            //Izracunavanje CENE TOTAL sume u tabeli Posao Artikli
            setTotalSumaCene(tblRowTotalCene);
            //Izracunavanje NABAVNE CENE TOTAL sume u tabeli Posao Artikli
            setTotalSumaNabavneCene(tblRowTotalNabavneCene);
            // Ako je racun u edit modu nemoj praviti novi racun nego prosledi RACUN koji je za izmenu
            if (automobiliController.isRacunInEditMode()) { //TRUE
                newOrEditRacun(true);
                try { // Nadji sve PosaoArtikle po Broju Fakture i popuni tabelu jer smo u EDIT MODU
                    posaoArtikli = FXCollections.observableArrayList(
                            posaoArtikliDAO.findPosaoArtikliByPropertyDao(
                                    PosaoArtikliDaoSearchType.ID_RACUNA_POSAO_ARTIKLI_DAO, brojFakture)
                    );

                    popuniTabeluRacuni(); //popuni tabelu PosaoArtikli za Editovanje jer smo u EDIT MODU

                } catch (AcrenoException | SQLException e) {
                    e.printStackTrace();
                }
            } else { //Nismo u Edit Modu (FALSE)
                //Datum
                LocalDate now = LocalDate.now();
                datePickerDatumRacuna.setValue(now); //Postavi danasnji datum Racuna u datePiceru
                datePickerDatumPrometa.setValue(now); //Postavi danasnji datum Prometa u datePiceru
                datePickerDatumValute.setValue(now); //Postavi danasnji datum Prometa u datePiceru
                newOrEditRacun(false); // Nismo u edit modu pa napravi novi racun
            }
        });
    }

    /**
     * Inicijalizacija podataka {@link Automobil}, {@link Klijent} koji su dobijeni iz {@link AutomobiliController}
     * <p>
     * {@code .get(0)} Moze jer je samo jedan objkat Klijent ili Automobil prisutan u datom trenutku !
     *
     * @see AutomobiliController
     */
    private void initGUI() {
        //Inicijalizacija podataka
        automobili = automobiliController.getAutomobil(); //Get AUTOMOBIL from automobiliController #Filtered
        klijenti = automobiliController.getKlijenti(); //Get KLIJENTA from automobiliController #Filtered
        idAutomobila = automobili.get(0).getIdAuta(); //Moze jer je samo jedan Automobil
        //Popunjavanje GUIa
        txtFklijentImePrezime.setText(klijenti.get(0).getImePrezime()); //Moze jer je samo jedan Klijent
        txtFregTablica.setText(automobili.get(0).getRegOznaka()); //Moze jer je samo jedan Automobil
    }

    /**
     * Pravljenje ili Editovanje {@link Racun} i DB FK {@link PosaoArtikli} objekata
     * <p>
     * Prvo inicijalizujemo GUI {@link #initGUI()} bez obzira da li je EDIT mode ili ne.
     * <p>
     * EDIT MODE STATUS DOBIJAMO IZ {@link AutomobiliController#btnOpenFakturaUi()}
     * <p>
     * Ako smo u EDIT modu(TRUE) {@code if (isInEditMode)} ne treba da pravimo {@link Racun} objekat
     * nego smo ga prosledili iz {@link AutomobiliController#btnOpenFakturaUi()}
     * u kodu {@code fakturaController.setEditRadniNalog(racun)}, a u seteru {@link #setEditRacun(Racun)}
     * <p>
     * Ako nismo u EDIT MODU(FALSE), pravimo novi objekat {@link Racun} i bitno da se odredi koji
     * je sledeci {@link #brojFakture}. Ovde je bio problem jer kada se obrise Racun ID se pomera za jedan
     * iako je obrisan.
     * <p>
     * {@code GeneralUiUtility.fromStringDate} formatiramo datum za Serbiu, a u {@link GeneralUiUtility#fromStringDate}
     *
     * @param isInEditMode da li smo u Edit Modu
     * @author Dejan Cugalj
     * @see AutomobiliController#btnOpenFakturaUi()
     * @see GeneralUiUtility#fromStringDate(String)
     */
    private void newOrEditRacun(boolean isInEditMode) {
        initGUI(); //Inicijalizacija podataka za novi racun bez obzira na edit mode
        if (isInEditMode) {
            txtFidRacuna.setText(String.valueOf(noviRacun.getIdRacuna()));
            datePickerDatumRacuna.setValue(GeneralUiUtility.fromStringDate(noviRacun.getDatum()));
            datePickerDatumPrometa.setValue(GeneralUiUtility.fromStringDate(noviRacun.getDatumPrometa()));
            datePickerDatumValute.setValue(GeneralUiUtility.fromStringDate(noviRacun.getDatumValute()));
            txtAreaNapomenaRacuna.setText(noviRacun.getNapomeneRacuna());
            txtFpopustRacuna.setText(String.valueOf(noviRacun.getPopust()));
            txtfKilometraza.setText(noviRacun.getKilometraza());
        } else {
            noviRacun = new Racun();
            noviRacun.setIdRacuna(brojFakture);
            noviRacun.setIdAutomobila(idAutomobila);
            noviRacun.setKilometraza(txtfKilometraza.getText());
            noviRacun.setDatum(GeneralUiUtility.formatDateForUs(datePickerDatumRacuna.getValue()));
            noviRacun.setDatumPrometa(GeneralUiUtility.formatDateForUs(datePickerDatumPrometa.getValue()));
            noviRacun.setDatumValute(GeneralUiUtility.formatDateForUs(datePickerDatumValute.getValue()));
            noviRacun.setNapomeneRacuna(txtAreaNapomenaRacuna.getText());
            if (!txtFpopustRacuna.getText().isEmpty())
                noviRacun.setPopust(Integer.parseInt(txtFpopustRacuna.getText()));
            try {
                racuniDAO.insertRacun(noviRacun);
                //Inicijalizacija broja fakture MORA DA IDE OVDE
                racuni = FXCollections.observableArrayList(racuniDAO.findAllRacune());
                brojFakture = racuniDAO.findAllRacune().get(racuni.size() - 1).getIdRacuna();
                txtFidRacuna.setText(String.valueOf(brojFakture));

            } catch (AcrenoException | SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Promenjiva koja nam je potrebna za update PosaoArtikl podataka u celijama tablele {@link #tblPosaoArtikli}
     * <p>
     * Kada ubacimo Artikl u Tabelu {@link #tblPosaoArtikli} i hocemo da uradimo update nekih podataka(cene, kolicine)
     * moramo iammo temo promenjuvu "posaoArtikliTemp" jer pratimo koje vrednosti ubacujemo, a to sve zbog
     * {@link PosaoArtikliDAO#updatePosaoArtikliDao(PosaoArtikli)} jer se ovde trazi ID Posao Artikla i koju
     * vrednsot menjamo.
     *
     * @author Dejan Cugalj
     * @see #setTableData(ObservableList PosaoArtikli)  OBAVEZNO !!!
     * @see PosaoArtikliDAO#updatePosaoArtikliDao(PosaoArtikli)
     */
    private PosaoArtikli posaoArtikliTemp;

    /**
     * EDIT MODE (TRUE) pa je potrebno popuniti tabelu sa {@link PosaoArtikli}-ma koji su
     * filtrirani po IDu preko {@link PosaoArtikliDaoSearchType#ID_RACUNA_POSAO_ARTIKLI_DAO}
     * u {@link #initialize} metodi {@link FakturaController}-a.
     *
     * @see PosaoArtikli
     * @see PosaoArtikliDaoSearchType#ID_RACUNA_POSAO_ARTIKLI_DAO
     */
    private void popuniTabeluRacuni() {
        setTableData(posaoArtikli);
    }

    /**
     * Pomocna metoda za omogucavanje EDIT ćelija u {@link #tblPosaoArtikli}
     * <p>
     * Posto nam je kod za omogucavanje editovanja celija u tabeli {@link #tblPosaoArtikli} potreban na dva mesta
     * a to su: {@link #popuniTabeluRacuni()} i {@link #btnDodajArtiklRacunMouseClick(MouseEvent)}, da se ne bi
     * ponavljali koristimo ovu pomocnu metodu.
     * <p>
     * Parametar metode {@code ObservableList<PosaoArtikli> posaoArtikli} je potreban
     * jer u {@link #btnDodajArtiklRacunMouseClick(MouseEvent)} koristimo jos jednu filter observable listu
     * pa je u tom delu i prosledjujemo.
     * <p>
     * {@code tblPosaoArtikli.refresh()} Nam je potreban zbo izracunavanja TOTAL SUME u tabeli, a
     * implementira se i {@link #initialize(URL, ResourceBundle)}
     * <p>
     * OVA METODA OMOGUCAVA ISTO TAKO I UPDATE U DB CIM SE KLIKNE ENTER SA IZMENJENOM VREDNOSCU.
     * <p>
     * Na kraju se izracunava GRAND TOTAL SUMA u {@link #izracunajTotalSumaSaPopustomNaDelove()} i posatvlja u TF {@link #txtfTotalSaPopustomNaDelove}
     *
     * @param posaoArtikli ObservableList {@link PosaoArtikli}
     * @author Dejan Cugalj
     * @see #popuniTabeluRacuni()
     * @see #btnDodajArtiklRacunMouseClick(MouseEvent)
     * @see PosaoArtikliDAO#updatePosaoArtikliDao(PosaoArtikli)
     * @see #initialize
     */
    private void setTableData(ObservableList<PosaoArtikli> posaoArtikli) {
        tblPosaoArtikli.getSelectionModel().setCellSelectionEnabled(true);
        tblPosaoArtikli.setEditable(true);
        //ID POSAO ARTIKLA
        tblRowidPosaoArtikli.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getIdPosaoArtikli()));

        //ID RACUNA
        tblRowidRacuna.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getIdRacuna()));

        //ID ARTIKLA
        tblRowidArtikla.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getIdArtikla()));


        //WRAP TEXT IN COLUMN NAZIV ARTKLA
        tblRowNazivArtikla.setCellFactory(tc -> {
            TableCell<PosaoArtikli, String> cell = new TableCell<>();
            Text text = new Text();
            cell.setGraphic(text);
            cell.setPrefHeight(Control.USE_COMPUTED_SIZE);
            text.wrappingWidthProperty().bind(tblRowNazivArtikla.widthProperty());
            text.textProperty().bind(cell.itemProperty());

            // NAZIV ARTIKLA -- MORA OVDE DA BI SE EDITOVALO U SAMOJ TABELI
            tblRowNazivArtikla.setCellValueFactory(cellData ->
                    new SimpleStringProperty(cellData.getValue().getNazivArtikla()));
            tblRowNazivArtikla.setStyle("-fx-alignment: CENTER;");
            tblRowNazivArtikla.setCellFactory(TextFieldTableCell.forTableColumn());
            tblRowNazivArtikla.setOnEditCommit(t -> {
                if (t.getNewValue().equals("")) {
                    GeneralUiUtility.alertDialogBox(Alert.AlertType.ERROR,
                            "GREŠKA", "PRAZNO POLJE", "Polje mora imati vrednost!");
                } else {
                    t.getRowValue().setNazivArtikla(t.getNewValue());
                    try {
                        posaoArtikliTemp = t.getRowValue();
                        posaoArtikliTemp.setIdPosaoArtikli(t.getRowValue().getIdPosaoArtikli()); // Obavezno ID zbog update-a
                        posaoArtikliTemp.setNazivArtikla(t.getRowValue().getNazivArtikla());

                        posaoArtikliDAO.updatePosaoArtikliDao(posaoArtikliTemp); // update u DB

                    } catch (SQLException | AcrenoException throwables) {
                        throwables.printStackTrace();
                    }
                }
            });

            return cell;
        });

        //OPIS ARTIKLA
        tblRowOpisArtikla.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getOpisPosaoArtiklli()));
        tblRowNazivArtikla.setStyle("-fx-alignment: CENTER-RIGHT;");
        tblRowOpisArtikla.setCellFactory(TextFieldTableCell.forTableColumn());
        tblRowOpisArtikla.setOnEditCommit(t -> {
            if (t.getNewValue().equals("")) {
                GeneralUiUtility.alertDialogBox(Alert.AlertType.ERROR,
                        "GREŠKA", "PRAZNO POLJE", "Polje mora imati vrednost!");
            } else {
                t.getRowValue().setOpisPosaoArtiklli(t.getNewValue());
                try {
                    posaoArtikliTemp = t.getRowValue();
                    posaoArtikliTemp.setIdPosaoArtikli(t.getRowValue().getIdPosaoArtikli()); // Obavezno ID zbog update-a
                    posaoArtikliTemp.setOpisPosaoArtiklli(t.getRowValue().getOpisPosaoArtiklli());

                    posaoArtikliDAO.updatePosaoArtikliDao(posaoArtikliTemp); // update u DB

                } catch (SQLException | AcrenoException throwables) {
                    throwables.printStackTrace();
                }
            }
        });
        //WRAP TEXT IN COLUMN
      /*  tblRowOpisArtikla.setCellFactory(tc -> {
            tblRowOpisArtikla.setEditable(true);
            TableCell<PosaoArtikli, String> cell = new TableCell<>();
            Text text = new Text();
            cell.setGraphic(text);
            cell.setPrefHeight(Control.USE_COMPUTED_SIZE);
            text.wrappingWidthProperty().bind(tblRowOpisArtikla.widthProperty());
            text.textProperty().bind(cell.itemProperty());
            return cell ;
        });*/

        // CENA
        tblRowCena.setCellValueFactory(cellData ->
                new SimpleDoubleProperty(cellData.getValue().getCena()));
        tblRowCena.setCellFactory(TextFieldTableCell.forTableColumn(new NumberStringConverter()));
        tblRowCena.setOnEditCommit(t -> {
            if (t.getNewValue() == null) {
                GeneralUiUtility.alertDialogBox(Alert.AlertType.ERROR,
                        "GRESKA", "PRAZNO POLJE", "Polje mora imati vrednost!");
            } else {
                t.getRowValue().setCena(t.getNewValue().doubleValue());
                try {
                    posaoArtikliTemp = t.getRowValue();
                    posaoArtikliTemp.setIdPosaoArtikli(t.getRowValue().getIdPosaoArtikli()); // Obavezno ID zbog update-a
                    posaoArtikliTemp.setCena(t.getRowValue().getCena());
                    posaoArtikliDAO.updatePosaoArtikliDao(posaoArtikliTemp); // update u DB
                } catch (SQLException | AcrenoException throwables) {
                    throwables.printStackTrace();
                }
            }
            tblPosaoArtikli.refresh();// Potrebno zbog izracunavanja TOTAL sume u tabeli

            double totalSumSaPopustomNaDelove = izracunajTotalSumaSaPopustomNaDelove();
            txtfTotalSaPopustomNaDelove.setText(GeneralUiUtility.formatDecimalPlaces(totalSumSaPopustomNaDelove));
            // Izracunavanje TOTAL SUME CENE i sredjivanje decimala
            double totalSumCene = izracunajTotalRegularneCene();
            txtfTotalPoCenama.setText(GeneralUiUtility.formatDecimalPlaces(totalSumCene));
            // Izracunavanje TOTAL SUME NABAVNE CENE i sredjivanje decimala
            double totalSumPoNabavnimCenama = izracunajTotalNabavneCene();
            txtfTotalPoNabavnimCenama.setText(GeneralUiUtility.formatDecimalPlaces(totalSumPoNabavnimCenama));
            // Izracunavanje GRAND TOTAL SUME Sa popustom na ceo racun i formatiramo decimale
            double totoalSumGrand = izracunajGrandTotalSaPopustomNaCeoRacun();
            txtfGrandTotal.setText(GeneralUiUtility.formatDecimalPlaces(totoalSumGrand));
        });

        //NABAVNA CENA
        tblRowNabavnaCena.setCellValueFactory(cellData ->
                new SimpleDoubleProperty(cellData.getValue().getNabavnaCena()));
        tblRowNabavnaCena.setCellFactory(TextFieldTableCell.forTableColumn(new NumberStringConverter()));
        tblRowNabavnaCena.setOnEditCommit((TableColumn.CellEditEvent<PosaoArtikli, Number> t) -> {
            if (t.getNewValue() == null) {
                GeneralUiUtility.alertDialogBox(Alert.AlertType.ERROR,
                        "GRESKA", "PRAZNO POLJE", "Polje mora imati vrednost!");
            } else {
                try {
                    t.getRowValue().setNabavnaCena(t.getNewValue().doubleValue());
                    posaoArtikliTemp = t.getRowValue();
                    posaoArtikliTemp.setIdPosaoArtikli(t.getRowValue().getIdPosaoArtikli()); // Obavezno ID zbog update-a
                    posaoArtikliTemp.setNabavnaCena(t.getRowValue().getNabavnaCena());
                    posaoArtikliDAO.updatePosaoArtikliDao(posaoArtikliTemp); // update u DB
                } catch (SQLException | AcrenoException throwables) {
                    throwables.printStackTrace();
                }
            }
            tblPosaoArtikli.refresh();// Potrebno zbog izracunavanja TOTAL sume u tabeli

            double totalSumSaPopustomNaDelove = izracunajTotalSumaSaPopustomNaDelove();
            txtfTotalSaPopustomNaDelove.setText(GeneralUiUtility.formatDecimalPlaces(totalSumSaPopustomNaDelove));
            // Izracunavanje TOTAL SUME CENE i sredjivanje decimala
            double totalSumCene = izracunajTotalRegularneCene();
            txtfTotalPoCenama.setText(GeneralUiUtility.formatDecimalPlaces(totalSumCene));
            // Izracunavanje TOTAL SUME NABAVNE CENE i sredjivanje decimala
            double totalSumPoNabavnimCenama = izracunajTotalNabavneCene();
            txtfTotalPoNabavnimCenama.setText(GeneralUiUtility.formatDecimalPlaces(totalSumPoNabavnimCenama));
            // Izracunavanje GRAND TOTAL SUME Sa popustom na ceo racun i formatiramo decimale
            double totoalSumGrand = izracunajGrandTotalSaPopustomNaCeoRacun();
            txtfGrandTotal.setText(GeneralUiUtility.formatDecimalPlaces(totoalSumGrand));
        });

        //KOLICINA
        tblRowKolicina.setCellValueFactory(cellData ->
                new SimpleDoubleProperty(cellData.getValue().getKolicina()));
        tblRowKolicina.setCellFactory(TextFieldTableCell.forTableColumn(new NumberStringConverter()));
        tblRowKolicina.setOnEditCommit((TableColumn.CellEditEvent<PosaoArtikli, Number> t) -> {
            if (t.getNewValue() == null) {
                GeneralUiUtility.alertDialogBox(Alert.AlertType.ERROR,
                        "GRESKA", "PRAZNO POLJE", "Polje mora imati vrednost!");
            } else {
                try {
                    t.getRowValue().setKolicina(t.getNewValue().intValue());
                    posaoArtikliTemp = t.getRowValue();
                    posaoArtikliTemp.setIdPosaoArtikli(t.getRowValue().getIdPosaoArtikli()); // Obavezno ID zbog update-a
                    posaoArtikliTemp.setKolicina(t.getRowValue().getKolicina());
                    posaoArtikliDAO.updatePosaoArtikliDao(posaoArtikliTemp); // update u DB
                } catch (SQLException | AcrenoException throwables) {
                    throwables.printStackTrace();
                }
            }
            tblPosaoArtikli.refresh();// Potrebno zbog izracunavanja TOTAL sume u tabeli

            // Izracunavanje TOTAL SUME SA POPUSTOM NA DELOVE i sredjivanje decimala
            double totalSumSaPopustomNaDelove = izracunajTotalSumaSaPopustomNaDelove();
            txtfTotalSaPopustomNaDelove.setText(GeneralUiUtility.formatDecimalPlaces(totalSumSaPopustomNaDelove));

            // Izracunavanje TOTAL SUME CENE i sredjivanje decimala
            double totalSumCene = izracunajTotalRegularneCene();
            txtfTotalPoCenama.setText(GeneralUiUtility.formatDecimalPlaces(totalSumCene));

            // Izracunavanje TOTAL SUME NABAVNE CENE i sredjivanje decimala
            double totalSumPoNabavnimCenama = izracunajTotalNabavneCene();
            txtfTotalPoNabavnimCenama.setText(GeneralUiUtility.formatDecimalPlaces(totalSumPoNabavnimCenama));

            // Izracunavanje GRAND TOTAL SUME Sa popustom na ceo racun i formatiramo decimale
            double totoalSumGrand = izracunajGrandTotalSaPopustomNaCeoRacun();
            txtfGrandTotal.setText(GeneralUiUtility.formatDecimalPlaces(totoalSumGrand));
        });

        //JEIDNICA MERE
        tblRowJedinicaMere.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getJedinicaMere()));
        tblRowJedinicaMere.setCellFactory(TextFieldTableCell.forTableColumn());
        tblRowJedinicaMere.setOnEditCommit(t -> {
            if (t.getNewValue().equals("")) {
                GeneralUiUtility.alertDialogBox(Alert.AlertType.ERROR,
                        "GRESKA", "PRAZNO POLJE", "Polje mora imati vrednost!");
            } else {
                t.getRowValue().setJedinicaMere(t.getNewValue());
                try {
                    posaoArtikliTemp = t.getRowValue();
                    posaoArtikliTemp.setIdPosaoArtikli(t.getRowValue().getIdPosaoArtikli()); // Obavezno ID zbog update-a
                    posaoArtikliTemp.setJedinicaMere(t.getRowValue().getJedinicaMere());

                    posaoArtikliDAO.updatePosaoArtikliDao(posaoArtikliTemp); // update u DB

                } catch (SQLException | AcrenoException throwables) {
                    throwables.printStackTrace();
                }
            }
        });

        //POPUST
        tblRowPopust.setCellValueFactory(cellData ->
                new SimpleDoubleProperty(cellData.getValue().getPopust()));
        tblRowPopust.setCellFactory(TextFieldTableCell.forTableColumn(new NumberStringConverter()));
        tblRowPopust.setOnEditCommit((TableColumn.CellEditEvent<PosaoArtikli, Number> t) -> {
            if (t.getNewValue() == null) {
                GeneralUiUtility.alertDialogBox(Alert.AlertType.ERROR,
                        "GRESKA", "PRAZNO POLJE", "Polje mora imati vrednost!");
            } else {
                try {
                    t.getRowValue().setPopust(t.getNewValue().intValue());
                    posaoArtikliTemp = t.getRowValue();
                    posaoArtikliTemp.setIdPosaoArtikli(t.getRowValue().getIdPosaoArtikli()); // Obavezno ID zbog update-a
                    posaoArtikliTemp.setPopust(t.getRowValue().getPopust());
                    posaoArtikliDAO.updatePosaoArtikliDao(posaoArtikliTemp); // update u DB
                } catch (SQLException | AcrenoException throwables) {
                    throwables.printStackTrace();
                }
            }
            tblPosaoArtikli.refresh();// Potrebno zbog izracunavanja TOTAL sume u tabeli

            double totalSumSaPopustomNaDelove = izracunajTotalSumaSaPopustomNaDelove();
            txtfTotalSaPopustomNaDelove.setText(GeneralUiUtility.formatDecimalPlaces(totalSumSaPopustomNaDelove));
            // Izracunavanje TOTAL SUME CENE i sredjivanje decimala
            double totalSumCene = izracunajTotalRegularneCene();
            txtfTotalPoCenama.setText(GeneralUiUtility.formatDecimalPlaces(totalSumCene));
            // Izracunavanje TOTAL SUME NABAVNE CENE i sredjivanje decimala
            double totalSumPoNabavnimCenama = izracunajTotalNabavneCene();
            txtfTotalPoNabavnimCenama.setText(GeneralUiUtility.formatDecimalPlaces(totalSumPoNabavnimCenama));
            // Izracunavanje GRAND TOTAL SUME Sa popustom na ceo racun i formatiramo decimale
            double totoalSumGrand = izracunajGrandTotalSaPopustomNaCeoRacun();
            txtfGrandTotal.setText(GeneralUiUtility.formatDecimalPlaces(totoalSumGrand));
        });

        //DETALJI POSAO ARTIKL
        tblRowDetaljiPosaoArtikl.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getDetaljiPosaoArtikli()));
        tblRowDetaljiPosaoArtikl.setCellFactory(TextFieldTableCell.forTableColumn());
        tblRowDetaljiPosaoArtikl.setOnEditCommit(t -> {
            if (t.getNewValue().equals("")) {
                GeneralUiUtility.alertDialogBox(Alert.AlertType.ERROR,
                        "GRESKA", "PRAZNO POLJE", "Polje mora imati vrednost!");
            } else {
                t.getRowValue().setDetaljiPosaoArtikli(t.getNewValue());
                try {
                    posaoArtikliTemp = t.getRowValue();
                    posaoArtikliTemp.setIdPosaoArtikli(t.getRowValue().getIdPosaoArtikli()); // Obavezno ID zbog update-a
                    posaoArtikliTemp.setDetaljiPosaoArtikli(t.getRowValue().getDetaljiPosaoArtikli());
                    posaoArtikliDAO.updatePosaoArtikliDao(posaoArtikliTemp); // update u DB

                } catch (SQLException | AcrenoException throwables) {
                    throwables.printStackTrace();
                }
            }
        });

        tblPosaoArtikli.setItems(posaoArtikli);

        double totalSumSaPopustomNaDelove = izracunajTotalSumaSaPopustomNaDelove();
        txtfTotalSaPopustomNaDelove.setText(GeneralUiUtility.formatDecimalPlaces(totalSumSaPopustomNaDelove));
        // Izracunavanje TOTAL SUME CENE i sredjivanje decimala
        double totalSumCene = izracunajTotalRegularneCene();
        txtfTotalPoCenama.setText(GeneralUiUtility.formatDecimalPlaces(totalSumCene));
        // Izracunavanje TOTAL SUME NABAVNE CENE i sredjivanje decimala
        double totalSumPoNabavnimCenama = izracunajTotalNabavneCene();
        txtfTotalPoNabavnimCenama.setText(GeneralUiUtility.formatDecimalPlaces(totalSumPoNabavnimCenama));
        // Izracunavanje GRAND TOTAL SUME Sa popustom na ceo racun i formatiramo decimale
        double totoalSumGrand = izracunajGrandTotalSaPopustomNaCeoRacun();
        txtfGrandTotal.setText(GeneralUiUtility.formatDecimalPlaces(totoalSumGrand));
    }


    // ******************** KALKULACIJA ***************************

    /**
     * Izracunavanje  SUME SA POPUSTOM NA DELOVE racuna sa popustom i to po regularnim cenama.
     * Koristi se u {@link #initialize}, i u svakom polju tabele u {@link #setTableData(ObservableList)}
     * koji se menja da bi se uradio update GRAND TOTAL sume.
     *
     * @return (double) GRAND TOTAL RACUNA
     * @author Dejan Cugalj
     * @see #setTableData
     * @see #initialize
     */
    private double izracunajTotalSumaSaPopustomNaDelove() {
        return tblPosaoArtikli.getItems().stream().mapToDouble(o ->
                tblRowTotal.getCellData(o).doubleValue()).sum();
    }

    /**
     * Izracunavanje  SUME PO NABAVNIM CENAMA DELOVA sa popustom i to po regularnim cenama.
     * Koristi se u {@link #initialize}, i u svakom polju tabele u {@link #setTableData(ObservableList)}
     * koji se menja da bi se uradio update GRAND TOTAL sume.
     *
     * @return (double) GRAND TOTAL RACUNA
     * @author Dejan Cugalj
     * @see #setTableData
     * @see #initialize
     */
    private double izracunajTotalNabavneCene() {
        return tblPosaoArtikli.getItems().stream().mapToDouble(o ->
                tblRowTotalNabavneCene.getCellData(o).doubleValue()).sum();
    }

    /**
     * Izracunavanje  SUME PO REGULARNIM CENAMA DELOVA bez popustom.
     * Koristi se u {@link #initialize}, i u svakom polju tabele u {@link #setTableData(ObservableList)}
     * koji se menja da bi se uradio update GRAND TOTAL sume.
     *
     * @return (double) GRAND TOTAL RACUNA
     * @author Dejan Cugalj
     * @see #setTableData
     * @see #initialize
     */
    private double izracunajTotalRegularneCene() {
        return tblPosaoArtikli.getItems().stream().mapToDouble(o ->
                tblRowTotalCene.getCellData(o).doubleValue()).sum();
    }

    /**
     * Izracunavanje  GRAND TOTAL SUME SA POPUSTOM CELOG RACUNA.
     * Posto imamo opciju da pored popusta na delove stavimo i popust na CEO RACUN, ovde radimo kalkulaciju.
     * Koristi se u {@link #initialize}, i u svakom polju tabele u {@link #setTableData(ObservableList)}
     * koji se menja da bi se uradio update GRAND TOTAL sume.
     *
     * @return (double) GRAND TOTAL RACUNA
     * @author Dejan Cugalj
     * @see #setTableData
     * @see #initialize
     */
    private double izracunajGrandTotalSaPopustomNaCeoRacun() {
        double totalsaPopustomNaDelove = tblPosaoArtikli.getItems().stream().mapToDouble(o ->
                tblRowTotal.getCellData(o).doubleValue()).sum();
        double popustNaCelomRacunu = Double.parseDouble(txtFpopustRacuna.getText());
        return totalsaPopustomNaDelove - ((totalsaPopustomNaDelove * popustNaCelomRacunu) / 100);
    }

    /**
     * Izracunavanje GRAND TOTAL SUME sa svim popustima i na Artikle(Delove) i na popust ceo racun
     * Koristimo je i u {@link PrintRacuniControler#initialize(URL, ResourceBundle)} preko setovanog
     * kontrolora u {@link #initUiPrintControler(FXMLLoader)}
     *
     * @param tblRowTotal ciljna kolona u tabeli
     * @author Dejan Cugalj
     * @see PrintRacuniControler
     * @see #initUiPrintControler(FXMLLoader)
     */
    public static void setGrandTotalSuma(@NotNull TableColumn<PosaoArtikli, Number> tblRowTotal) {
        tblRowTotal.setCellValueFactory(cellData -> {
            PosaoArtikli posaoArtikli = cellData.getValue();
            return Bindings.createDoubleBinding(
                    () -> {
                        try {
                            double price = Double.parseDouble(String.valueOf(posaoArtikli.getCena()));
                            double quantity = Integer.parseInt(String.valueOf(posaoArtikli.getKolicina()));
                            double popust = Integer.parseInt(String.valueOf(posaoArtikli.getPopust()));
                            double total = price * quantity;
                            return total - ((total * popust) / 100);

                        } catch (NumberFormatException nfe) {
                            return (double) 0;
                        }
                    }
            );
        });
    }

    /**
     * Izracunavanje TOTAL SUME CENA bez popusta na racuni ili na artiklima
     * Koristimo je i u {@link PrintRacuniControler#initialize(URL, ResourceBundle)} preko setovanog
     * kontrolora u {@link #initUiPrintControler(FXMLLoader)}
     *
     * @param tblRowTotal ciljna kolona u tabeli
     * @author Dejan Cugalj
     * @see PrintRacuniControler
     * @see #initUiPrintControler(FXMLLoader)
     */
    private void setTotalSumaCene(@NotNull TableColumn<PosaoArtikli, Number> tblRowTotal) {
        tblRowTotal.setCellValueFactory(cellData -> {
            PosaoArtikli posaoArtikli = cellData.getValue();
            return Bindings.createDoubleBinding(
                    () -> {
                        try {
                            double price = Double.parseDouble(String.valueOf(posaoArtikli.getCena()));
                            double quantity = Integer.parseInt(String.valueOf(posaoArtikli.getKolicina()));
                            return price * quantity;

                        } catch (NumberFormatException nfe) {
                            return (double) 0;
                        }
                    }
            );
        });
    }

    /**
     * Izracunavanje TOTAL SUME NABAVNA CENA bez popusta na racuni ili na artiklima
     * Koristimo je i u {@link PrintRacuniControler#initialize(URL, ResourceBundle)} preko setovanog
     * kontrolora u {@link #initUiPrintControler(FXMLLoader)}
     *
     * @param tblRowTotal ciljna kolona u tabeli
     * @author Dejan Cugalj
     * @see PrintRacuniControler
     * @see #initUiPrintControler(FXMLLoader)
     */
    private void setTotalSumaNabavneCene(@NotNull TableColumn<PosaoArtikli, Number> tblRowTotal) {
        tblRowTotal.setCellValueFactory(cellData -> {
            PosaoArtikli posaoArtikli = cellData.getValue();
            return Bindings.createDoubleBinding(
                    () -> {
                        try {
                            double price = Double.parseDouble(String.valueOf(posaoArtikli.getNabavnaCena()));
                            double quantity = Integer.parseInt(String.valueOf(posaoArtikli.getKolicina()));
                            return price * quantity;

                        } catch (NumberFormatException nfe) {
                            return (double) 0;
                        }
                    }
            );
        });
    }


    //TODO: PRETRAGA PO KATALOSKOM BROJU U RACUNU(za BAR CODE skener...)
    @FXML private void txtfPretragaPoKataloskomBroju(KeyEvent keyEvent) {
        txtFieldPretragaArtikla.textProperty().addListener(observable -> {
            if (txtFieldPretragaArtikla.textProperty().get().isEmpty()) {
                listViewPretragaKatalskiBrojArtikli.setVisible(false);
            }
        });
        listViewPretragaArtikli.setVisible(false);
        ObservableList<Artikl> artikl = FXCollections.observableArrayList();
        ObservableList<Artikl> tempArtikl = FXCollections.observableArrayList();
        try {
            // ArtikliDAO artikliDAO = new SQLArtikliDAO();// inicijalizacija podataka iz BAZE
            artikl = FXCollections.observableArrayList(artikliDAO.findAllArtikle()); //Svi Automobili
            // tempArtikl = FXCollections.observableArrayList(); //Lista u koju dodajemo nadjene Auto objekte
        } catch (AcrenoException | SQLException e) {
            e.printStackTrace();
        }

        for (Artikl value : artikl) {

            String katBrojArtikla = value.getKataloskiBrArtikla().toLowerCase();//Trenutna tablica auta

            if (katBrojArtikla.contains(txtfKataloskiBrojArtikla.textProperty().get())) {
                tempArtikl.add(value); // Dodaje nadjeni auto u temp listu
                listViewPretragaKatalskiBrojArtikli.setItems(tempArtikl); // Dodaje u FXlistView
                listViewPretragaKatalskiBrojArtikli.setCellFactory(param -> new ListCell<>() {
                    @Override
                    protected void updateItem(Artikl item, boolean empty) {
                        super.updateItem(item, empty);
                        listViewPretragaKatalskiBrojArtikli.setVisible(true); //Prikazuje listu vidljivom
                        if (empty || item == null || item.getKataloskiBrArtikla() == null) {
                            setText(null);
                        } else {
                            setText(item.getKataloskiBrArtikla());
                        }
                    }
                });
            }
        }
        switch (keyEvent.getCode()) {
            case ESCAPE:
                System.out.println("ESCAPE");
                listViewPretragaKatalskiBrojArtikli.setVisible(false);
                txtfKataloskiBrojArtikla.requestFocus();
                break;
            case DOWN:
                System.out.println("DOWN FRON LIST");
                listViewPretragaKatalskiBrojArtikli.requestFocus();
                break;
            case ENTER:
                System.out.println("ENTER FRON LIST");
                break;
            default:
                break;
        }
    }

    @FXML private void zatvoriListViewPretragaPoKatBrojuArtikla(@NotNull MouseEvent mouseEvent) {
        //Na dupli click vraca Radni Nalog Objekat i otvara Radni nalog Dashboard
        if (mouseEvent.getButton().equals(MouseButton.PRIMARY) && mouseEvent.getClickCount() == 2) {
            // Popunjavanje TF polja sa podacima Artikla
            String nazivArtikla =
                    listViewPretragaKatalskiBrojArtikli.getSelectionModel().getSelectedItem().getNazivArtikla();
            String kataloskiBrojArtikla =
                    listViewPretragaKatalskiBrojArtikli.getSelectionModel().getSelectedItem().getKataloskiBrArtikla();
            int idArtikla =
                    listViewPretragaKatalskiBrojArtikli.getSelectionModel().getSelectedItem().getIdArtikla();
            double cenaArtikla =
                    listViewPretragaKatalskiBrojArtikli.getSelectionModel().getSelectedItem().getCenaArtikla();
            double nabavnaCenaArtikla =
                    listViewPretragaKatalskiBrojArtikli.getSelectionModel().getSelectedItem().getNabavnaCenaArtikla();
            String jedinicaMereArtikla =
                    listViewPretragaKatalskiBrojArtikli.getSelectionModel().getSelectedItem().getJedinicaMere();
            String opisArtikla =
                    listViewPretragaKatalskiBrojArtikli.getSelectionModel().getSelectedItem().getOpisArtikla();

            //Popunjavanje GUI polja
            txtFidArtikla.setText(String.valueOf(idArtikla));
            txtfKataloskiBrojArtikla.setText(kataloskiBrojArtikla);
            txtFcenaArtikla.setText(String.valueOf(cenaArtikla));
            txtFnabavnaCenaArtikla.setText(String.valueOf(nabavnaCenaArtikla));
            txtFKolicinaArtikla.setText(String.valueOf(1));
            txtFjedinicaMereArtikla.setText(jedinicaMereArtikla);
            txtFpopustArtikla.setText(String.valueOf(0));
            txtFieldPretragaArtikla.setText(nazivArtikla);
            txtfNazivArtikla.setText(nazivArtikla);
            txtFopisArtikla.setText(opisArtikla);

            listViewPretragaKatalskiBrojArtikli.setVisible(false); //Sakrij ListView
        }
    }


    //****************** LIST VIEW STAFF PO ID ARTIKLU  ************************

    /**
     * Pretraga i filtriranje Artikala po NAZIVU ARTIKLA u KeyListeneru TxtF-a
     * <p>
     * Prilikom kucanja u txtF pokazuju se filtrirani Artikl u ListView koji je inicijalno sakriven
     * Ukoliko ima podataka ListView se prikazuje, i na kraju se dupli klik resava u {@link #zatvoriListViewSearchArtikli}
     *
     * @author Dejan Cugalj
     * @see ArtikliDAO
     * @see SQLArtikliDAO
     * @see #zatvoriListViewSearchArtikli(MouseEvent)
     * @see #listViewPretragaArtikli
     */
    @FXML private void txtFieldPretragaArtiklaKeyListener(KeyEvent keyEvent) {
        txtFieldPretragaArtikla.textProperty().addListener(observable -> {
            if (txtFieldPretragaArtikla.textProperty().get().isEmpty()) {
                //listViewPretragaArtikli.setItems(artikli);
                listViewPretragaArtikli.setVisible(false);
            }
        });
        listViewPretragaKatalskiBrojArtikli.setVisible(false);
        ObservableList<Artikl> artikl = FXCollections.observableArrayList();
        ObservableList<Artikl> tempArtikl = FXCollections.observableArrayList();
        try {
            artikl = FXCollections.observableArrayList(artikliDAO.findAllArtikle()); //Svi Automobili
        } catch (AcrenoException | SQLException e) {
            e.printStackTrace();
        }
        for (Artikl value : artikl) {

            String nazivArtikla = value.getNazivArtikla().toLowerCase();//Trenutna tablica auta

            if (nazivArtikla.contains(txtFieldPretragaArtikla.textProperty().get())) {
                tempArtikl.add(value); // Dodaje nadjeni auto u temp listu
                listViewPretragaArtikli.setItems(tempArtikl); // Dodaje u FXlistView
                listViewPretragaArtikli.setCellFactory(param -> new ListCell<>() {
                    @Override
                    protected void updateItem(Artikl item, boolean empty) {
                        super.updateItem(item, empty);
                        listViewPretragaArtikli.setVisible(true); //Prikazuje listu vidljivom
                        if (empty || item == null || item.getNazivArtikla() == null) {
                            setText(null);
                        } else {
                            setText(item.getNazivArtikla());
                        }
                    }
                });
                //break;
            }
        }
    }

    /**
     * Duplim klikom se selektuje Artikl objekat i popunjavaju se TF polja podacima tog Artikla
     * Nakon toga se {@link #btnDodajArtiklRacunMouseClick(MouseEvent)} dodaje u tabelu {@link #tblPosaoArtikli}
     * {@code .getSelectedItems().get(0)} Moze jer je samo jedan odabran Artikl u jednom momentu
     *
     * @param mouseEvent na dupli click selektuje Artikl objekat
     * @author Dejan Cugalj
     * @see #btnDodajArtiklRacunMouseClick(MouseEvent)
     * @see #tblPosaoArtikli
     * @see Artikl
     * @see PosaoArtikli
     * @see PosaoArtikliDAO
     */
    @FXML private void zatvoriListViewSearchArtikli(@NotNull MouseEvent mouseEvent) {
        //Na dupli click vraca Radni Nalog Objekat i otvara Radni nalog Dashboard
        if (mouseEvent.getButton().equals(MouseButton.PRIMARY) && mouseEvent.getClickCount() == 2) {
            // Popunjavanje TF polja sa podacima Artikla
            String nazivArtikla =
                    listViewPretragaArtikli.getSelectionModel().getSelectedItem().getNazivArtikla();
            String kataloskiBrojArtikla =
                    listViewPretragaArtikli.getSelectionModel().getSelectedItem().getKataloskiBrArtikla();
            int idArtikla =
                    listViewPretragaArtikli.getSelectionModel().getSelectedItem().getIdArtikla();
            double cenaArtikla =
                    listViewPretragaArtikli.getSelectionModel().getSelectedItem().getCenaArtikla();
            double nabavnaCenaArtikla =
                    listViewPretragaArtikli.getSelectionModel().getSelectedItem().getNabavnaCenaArtikla();
            String jedinicaMereArtikla =
                    listViewPretragaArtikli.getSelectionModel().getSelectedItem().getJedinicaMere();
            String opisArtikla =
                    listViewPretragaArtikli.getSelectionModel().getSelectedItem().getOpisArtikla();

            // Create Posao Artikli Object and populate with data
            PosaoArtikli posaoArtikliObject = new PosaoArtikli();
            //  posaoArtikliObject.setIdPosaoArtikli(0);
            posaoArtikliObject.setIdRacuna(brojFakture);
            posaoArtikliObject.setIdArtikla(Integer.parseInt(String.valueOf(idArtikla)));
            posaoArtikliObject.setNazivArtikla(nazivArtikla);
            posaoArtikliObject.setOpisPosaoArtiklli(opisArtikla);
            posaoArtikliObject.setCena(Double.parseDouble(String.valueOf(cenaArtikla)));
            posaoArtikliObject.setNabavnaCena(Double.parseDouble(String.valueOf(nabavnaCenaArtikla)));
            posaoArtikliObject.setKolicina(Integer.parseInt("1"));
            posaoArtikliObject.setJedinicaMere(jedinicaMereArtikla);
            posaoArtikliObject.setDetaljiPosaoArtikli(txtAreaDetaljiOpisArtikla.getText());
            posaoArtikliObject.setPopust(Integer.parseInt("0"));
            try {
                posaoArtikliDAO.insertPosaoArtikliDao(posaoArtikliObject); // SQL Staff
                //Create ObservableList<PosaoArtikli> and insert in table for print and preview
                ObservableList<PosaoArtikli> posaoArtiklovi = FXCollections.observableArrayList(
                        posaoArtikliDAO.findPosaoArtikliByPropertyDao(
                                PosaoArtikliDaoSearchType.ID_RACUNA_POSAO_ARTIKLI_DAO, brojFakture));

                setTableData(posaoArtiklovi); // Popunjavanje i omogucavanje EDITA u Tabeli

            } catch (AcrenoException | SQLException e) {
                e.printStackTrace();
            }

            listViewPretragaArtikli.setVisible(false); //Sakrij ListView
        }
    }

    /**
     * <p> Pravljenje "POSAO ARTIKLI" objekta za FK tabelu "PosaoArtikli" (Many to Many) </p>
     * <p> Kreiranje objekta {@link PosaoArtikli} i popunjavanje sa podacima </p>
     * <p>
     * {@code  posaoArtikliDAO.insertPosaoArtikliDao(posaoArtikliObject);} // SQL Staff {@link PosaoArtikliDAO}
     * <p>
     * {@code setTableData(posaoArtiklovi)} Omogucava editovanje ćelija u tabeli pomocu pomocne metode
     * deklarisane u {@link #setTableData(ObservableList posaoArtikli)}
     * <p>
     * Popunjavanje tabele {@link #tblPosaoArtikli}
     * </p>
     *
     * @param mouseEvent in case if we need
     * @see PosaoArtikli
     * @see PosaoArtikliDAO#insertPosaoArtikliDao(PosaoArtikli)
     * @see #setTableData(ObservableList posaoArtikli)
     */
    private void btnDodajArtiklRacunMouseClick(MouseEvent mouseEvent) {

        // Create Posao Artikli Object and populate with data
        PosaoArtikli posaoArtikliObject = new PosaoArtikli();
        //  posaoArtikliObject.setIdPosaoArtikli(0);
        posaoArtikliObject.setIdRacuna(brojFakture);

        posaoArtikliObject.setIdArtikla(Integer.parseInt("40"));

        posaoArtikliObject.setNazivArtikla(txtfNazivArtikla.getText());
        posaoArtikliObject.setOpisPosaoArtiklli(txtFopisArtikla.getText());
        posaoArtikliObject.setCena(Double.parseDouble(txtFcenaArtikla.getText()));
        posaoArtikliObject.setNabavnaCena(Double.parseDouble(txtFnabavnaCenaArtikla.getText()));
        posaoArtikliObject.setKolicina(Integer.parseInt(txtFKolicinaArtikla.getText()));
        posaoArtikliObject.setJedinicaMere(txtFjedinicaMereArtikla.getText());
        posaoArtikliObject.setDetaljiPosaoArtikli(txtAreaDetaljiOpisArtikla.getText());
        posaoArtikliObject.setPopust(Integer.parseInt(txtFpopustArtikla.getText()));
        try {
            posaoArtikliDAO.insertPosaoArtikliDao(posaoArtikliObject); // SQL Staff

            //Create ObservableList<PosaoArtikli> and insert in table for print and preview
            ObservableList<PosaoArtikli> posaoArtiklovi = FXCollections.observableArrayList(
                    posaoArtikliDAO.findPosaoArtikliByPropertyDao(
                            PosaoArtikliDaoSearchType.ID_RACUNA_POSAO_ARTIKLI_DAO, brojFakture));

            setTableData(posaoArtiklovi); // Popunjavanje i omogucavanje EDITA u Tabeli

        } catch (AcrenoException | SQLException e) {
            e.printStackTrace();
        }
        //GeneralUiUtility.clearTextFieldsInPane(paneArtikli);
        txtfNazivArtikla.setText("");
        txtFopisArtikla.setText("");
        txtFjedinicaMereArtikla.setText("kom");
        txtFKolicinaArtikla.setText("1");
        txtFcenaArtikla.setText("0");
        txtFnabavnaCenaArtikla.setText("0");
        txtFpopustArtikla.setText("0");
        txtAreaDetaljiOpisArtikla.setText("");

        GeneralUiUtility.clearTextFieldsInPane(panePretragaArtikli);
        // txtFieldPretragaArtikla.requestFocus();
    }


    //******************* PRINT INICIJALIZACIJA*******************

    /**
     * Promenjiva kojom se pristupaju promenjive iz ovog kontrolora, a u {@link PrintRacuniControler}
     */
    private Stage stagePrint;
    PrintRacuniControler printRacuniControler;
    /**
     * Inicijalizacija {@link PrintRacuniControler}, a implementira se {@link #initialize}
     *
     * @param fxmlLoader prosledjivanje FXMLoadera {@link PrintRacuniControler} - u
     * @see PrintRacuniControler
     */
    private void initUiPrintControler(@NotNull FXMLLoader fxmlLoader) {
        printRacuniControler = fxmlLoader.getController();
        printRacuniControler.setFakturaController(this, stagePrint);
    }

    /**
     * Otvaranje Print Fakture {@link PrintRacuniControler}
     * <p>
     * Posto je moguce da se doda napomena racuna u {@link #txtAreaNapomenaRacuna} i da se klikne odmah na PRINT
     * moramo da sacuvamo izmene u racunu a u {@link #btnSacuvajRacunAction()}. U ovoj metodi imamo obavestenje da je
     * Racun uspesno sacuvan, pa kada se klikne na Print dugme i otvori se GUI za print iza ostane Dialog.
     * Promenjivom {@link #ifWeAreFromBtnSacuvajRacun} regulisemo da se ono ne pojavljujekadase klikne na print.
     * <p>
     * Inicijalizacija Print Controlora i prosledjivanje id Racuna {@link #initUiPrintControler}
     * Na ovom mestu je zato sto je ovo poslednja pozicija koja se radi pre otvaranja Print Cotrolora
     *
     * @see #initUiPrintControler(FXMLLoader)
     * @see PrintRacuniControler
     * @see #btnSacuvajRacunAction()
     */
    @FXML
    private void btnPrintAction() {
        try {
            ifWeAreFromBtnSacuvajRacun = false; //Obavesti da ne cuvamo racun iz dugmeta btnSacuvajRacun
            btnSacuvajRacun.fire(); // Fire dugme za cuvanje bez dijaloga

            FXMLLoader loader = new FXMLLoader(getClass().getResource(Constants.PRINT_FAKTURA_UI_VIEW_URI));
            stagePrint = new Stage();
            stagePrint.initModality(Modality.APPLICATION_MODAL);
            stagePrint.initStyle(StageStyle.UNDECORATED);
            stagePrint.setScene(new Scene(loader.load()));

            initUiPrintControler(loader); //Inicijalizacija Print Controlora i prosledjivanje id Racuna

            stagePrint.showAndWait();//Open Stage and wait

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Otvaranje Print Fakture {@link PrintRacuniControler} i štampanje PONUDE
     * <p>
     * Posto je moguce da se doda napomena racuna u {@link #txtAreaNapomenaRacuna} i da se klikne odmah na PRINT
     * moramo da sacuvamo izmene u racunu a u {@link #btnSacuvajRacunAction()}. U ovoj metodi imamo obavestenje da je
     * Racun uspesno sacuvan, pa kada se klikne na Print dugme i otvori se GUI za print iza ostane Dialog.
     * Promenjivom {@link #ifWeAreFromBtnSacuvajRacun} regulisemo da se ono ne pojavljujekadase klikne na print.
     * <p>
     * Inicijalizacija Print Controlora i prosledjivanje id Racuna {@link #initUiPrintControler}
     * Na ovom mestu je zato sto je ovo poslednja pozicija koja se radi pre otvaranja Print Cotrolora
     *
     * @see #initUiPrintControler(FXMLLoader)
     * @see PrintRacuniControler
     * @see #btnSacuvajRacunAction()
     */
    public void btnPrintPonudaAction(ActionEvent actionEvent) {
        try {
            ifWeAreFromBtnSacuvajRacun = false; //Obavesti da ne cuvamo racun iz dugmeta btnSacuvajRacun
            btnSacuvajRacun.fire(); // Fire dugme za cuvanje bez dijaloga

            FXMLLoader loader = new FXMLLoader(getClass().getResource(Constants.PRINT_FAKTURA_UI_VIEW_URI));
            stagePrint = new Stage();
            stagePrint.initModality(Modality.APPLICATION_MODAL);
            stagePrint.initStyle(StageStyle.UNDECORATED);
            stagePrint.setScene(new Scene(loader.load()));
            initUiPrintControler(loader); //Inicijalizacija Print Controlora i prosledjivanje id Racuna
            printRacuniControler.lblRacunIliPonuda.setText("Ponuda br:");
            printRacuniControler.lblDatumRacunaPonuda.setText("Datum Ponude:");
            printRacuniControler.lblDatumPrometaPonuda.setText("Datum Prometa:");
            printRacuniControler.lblPonudaVaziDo.setText("Važi do:");
            printRacuniControler.lblPodnozije.setText("Ova ponuda je validan bez pečata i potpisa " +
                                                            "po identifikacionoj oznaci u zaglavlju.");
            stagePrint.showAndWait();//Open Stage and wait

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    //******************* BUTTON ACTION STAFF*********************

    /**
     * UPDATE Racuna kada se nesto promeni u njemu...(Datum...Napomena...Popust)
     * <p>
     * Setuju se svi podaci za izmenjen racun pokupljeni iz TF-ova
     * Zatim se radi update sa {@link RacuniDAO#updateRacun(Racun)}
     * Nakon toga se radi update GRAND TOTAL SUME SA POPUSTOM NA CEO RACUN {@link #izracunajGrandTotalSaPopustomNaCeoRacun()}
     * <p>
     * {@code GeneralUiUtility.formatDateForUs} Sredjuje Datum za Serbiu, a u {@link GeneralUiUtility#formatDateForUs}
     * <p>
     * {@code  ifWeAreFromBtnSacuvajRacun = true} regulise da se ne pojavljuje Dijalog kada se klikne odmah na print
     * dugme {@see btnPrintAction} objasnjenje za detalje.
     *
     * @see RacuniDAO#updateRacun(Racun)
     * @see GeneralUiUtility#alertDialogBox(Alert.AlertType, String, String, String)
     * @see GeneralUiUtility#formatDateForUs(LocalDate)
     */
    @FXML
    private void btnSacuvajRacunAction() {
        try {
            //UPDATE NOVO RACUNA SA NOVIM VREDNOSTIMA ZATO OVDE REDEFINISEMO NOVI RACUN
            noviRacun.setIdRacuna(brojFakture);
            noviRacun.setIdAutomobila(idAutomobila);
            noviRacun.setKilometraza(txtfKilometraza.getText());
            noviRacun.setDatum(GeneralUiUtility.formatDateForUs(datePickerDatumRacuna.getValue()));
            noviRacun.setDatumPrometa(GeneralUiUtility.formatDateForUs(datePickerDatumPrometa.getValue()));
            noviRacun.setDatumValute(GeneralUiUtility.formatDateForUs(datePickerDatumValute.getValue()));
            noviRacun.setNapomeneRacuna(txtAreaNapomenaRacuna.getText());
            noviRacun.setPopust(Integer.parseInt(txtFpopustRacuna.getText()));
            racuniDAO.updateRacun(noviRacun);
            if (ifWeAreFromBtnSacuvajRacun) {
                GeneralUiUtility.alertDialogBox(
                        Alert.AlertType.INFORMATION,
                        "USPESNO SACUVAN RACUN",
                        "Uspešno ste sačuvali Račun Br: " + brojFakture,
                        "Račun sa brojem: " + brojFakture + " je uspešno sačuvan!"
                );
            }

            // Izracunavanje GRAND TOTAL SUME Sa popustom na ceo racun i formatiramo decimale
            double totoalSumGrand = izracunajGrandTotalSaPopustomNaCeoRacun();
            txtfGrandTotal.setText(GeneralUiUtility.formatDecimalPlaces(totoalSumGrand));

            ifWeAreFromBtnSacuvajRacun = true; //Obavesti da je racun sacuvan

        } catch (SQLException | AcrenoException throwables) {
            throwables.printStackTrace();
            GeneralUiUtility.alertDialogBox(
                    Alert.AlertType.CONFIRMATION,
                    "GRESKA U CUVANJU RACUNA",
                    "EDITOVANJE RACUNA",
                    "Niste sacuvali  racun br." + brojFakture + ", Kontatiraj Administratora sa porukom: \n"
                            + throwables.getMessage()
            );
        }
    }

    /**
     * Brisanje {@link Racun} Objekta jer smo odustali i li vise necemo ovaj Racun.
     * <p>
     * Brise se preko {@link RacuniDAO#deleteRacun(int)} ID racuna koji se dobija iz {@link #brojFakture}.
     * <p>
     * {@link FakturaController} UIa i fire WINDOW_CLOSE_REQUEST on {@link AutomobiliController}
     * zbog refresha tabele Racuni u {@link AutomobiliController}-u, a implemtira se u
     * {@link AutomobiliController#btnOpenFakturaUi()}
     *
     * @param actionEvent zatvaranje UIa
     * @see AutomobiliController#btnOpenFakturaUi()
     * @see RacuniDAO#deleteRacun(int)
     */
    @FXML
    private void btnOdustaniObrisiRacunAction(@NotNull ActionEvent actionEvent) {
        try {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Potvrda brisanja Računa: " + brojFakture);
            alert.setHeaderText("Brisanje Računa sa Brojem: " + brojFakture);
            alert.setContentText("Da li ste sigurni da želite da obrišete račun br: " + brojFakture);
            ((Stage) alert.getDialogPane().getScene().getWindow()).getIcons().add(new Image(Constants.APP_ICON));
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent()) {
                if (result.get() == ButtonType.OK) {
                    racuniDAO.deleteRacun(brojFakture);
                    btnOdustaniObrisiRacun.fireEvent(new WindowEvent(automobilStage, WindowEvent.WINDOW_CLOSE_REQUEST));
                    ((Stage) (((Button) actionEvent.getSource()).getScene().getWindow())).close();
                }
            }
        } catch (AcrenoException | SQLException acrenoException) {
            acrenoException.printStackTrace();
        }
    }

    /**
     * Zatvaranje FAKTURA UIa i refresh tabele Racuni u {@link AutomobiliController}-u
     * <p>
     * {@code ifWeAreFromBtnSacuvajRacun = false } je za onemogucavanje dijaloga o uspesnom cuvanju Racuna.
     * Tj. kada se klikne na dugme {@link #btnCloseFaktureAction} prosledjujemo FALSE u
     * {@link #btnSacuvajRacunAction()} koja onda proverava da li treba prikazivatio Dijalog.
     * <p>
     * Da bi se refresovala tabela Racuni u {@link AutomobiliController}-u potrebno je pozvati
     * {@code WindowEvent.WINDOW_CLOSE_REQUEST} koji je implementiran u {@link AutomobiliController#btnOpenFakturaUi}
     * <p>
     * Ovde je implementiran i "SAMART CLOSE" jer ako se otvori nova Faktura ona je odmah ubacena u DB,
     * po ako se ne unese ni jedan {@link Artikl} onda predpostavljamo da smo odustali i samim tim brisemo iz DBa!
     *
     * @param actionEvent event for hide scene {@link FakturaController}
     * @throws AcrenoException malo bolje objasnjenje
     * @throws SQLException    problem u DBu
     * @author Dejan Cugalj
     * @see AutomobiliController#btnOpenFakturaUi
     */
    @FXML
    private void btnCloseFaktureAction(@NotNull ActionEvent actionEvent) throws AcrenoException, SQLException {
        ifWeAreFromBtnSacuvajRacun = false; //Obavesti da ne cuvamo racun iz dugmeta btnCloseFaktureAction
        // Pametno bisanje racuna ako je samo otvoren novi i nema artikala
        /*if (tblPosaoArtikli.getItems().size() == 0) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("SMART brisanja Računa");
            alert.setHeaderText("Niste uneli nijedan Artikal, da li možemo Račun da obrišemo?");
            alert.setContentText("Račun je već napravljen u bazi, ali niste uneli Artikal pa predpostavljamo da " +
                    "možemo da obrišemo ovaj Račun?");
            ((Stage) alert.getDialogPane().getScene().getWindow()).getIcons().add(new Image(Constants.APP_ICON));
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent()) {
                if (result.get() == ButtonType.OK) {
                    racuniDAO.deleteRacun(brojFakture);
                    btnCloseFakture.fireEvent(new WindowEvent(stagePrint, WindowEvent.WINDOW_CLOSE_REQUEST));
                    ((Stage) (((Button) actionEvent.getSource()).getScene().getWindow())).close();
                }
            }
        } else { }*/
            btnSacuvajRacunAction(); //Cuvamo kljijenta ako ima nesto u TXTFu Ime Prezime
            btnCloseFakture.fireEvent(new WindowEvent(stagePrint, WindowEvent.WINDOW_CLOSE_REQUEST));

    }

    //ARTIKLI STAFF

    public void btnDodajArtikl(ActionEvent actionEvent) throws IOException {
        // Standart FX load UI
        FXMLLoader fxmlLoaderArtikli = new FXMLLoader(getClass().getResource(Constants.ARTIKLI_UI_VIEW_URI));
        Stage stageArtikli = new Stage();
        stageArtikli.getIcons().add(new Image(AutoServisController.class.getResourceAsStream(Constants.APP_ARTIKLI_ICON)));
        stageArtikli.initModality(Modality.APPLICATION_MODAL);
        stageArtikli.setResizable(false);
        Scene scene = new Scene(fxmlLoaderArtikli.load());
        stageArtikli.setScene(scene);
        stageArtikli.setResizable(false);
        stageArtikli.setTitle("Artikli Kartica");

        stageArtikli.setOnCloseRequest(windowEvent -> logger.debug("stageArtikli --> setOnCloseRequest"));

       /* //Set AutoServisController u "ARTIKLI_UI_VIEW_URI"  UI
        ArtkikliController createNewArtiklUiController = fxmlLoaderArtikli.getController();
        createNewArtiklUiController.setAutmobilController(this, stageArtikli);
        //createNewArtiklUiController.setWeAreInEditMode(true);*/
        stageArtikli.showAndWait();
    }

    //***************** UTILLITY ********************

    /**
     * Setovan Listener za {@link #txtFieldPretragaArtikla} da kada izgubi focu zatvori {@link #listViewPretragaArtikli}
     * Impementirano u {@link #initialize(URL, ResourceBundle)}
     *
     * @see #initialize(URL, ResourceBundle)
     */
    private final ChangeListener<Boolean> focusListenerCloseImeArtiklaListView = new ChangeListener<Boolean>() {
        @Override
        public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
            if (!newValue) {
                listViewPretragaArtikli.setVisible(false);
            }
        }
    };

    /**
     * Setovan Listener za {@link #txtfKataloskiBrojArtikla} da kada izgubi focu zatvori {@link #listViewPretragaKatalskiBrojArtikli}
     * Impementirano u {@link #initialize(URL, ResourceBundle)}
     *
     * @see #initialize(URL, ResourceBundle)
     */
    private final ChangeListener<Boolean> focusListenerCloseImeKatBrojListView = new ChangeListener<Boolean>() {
        @Override
        public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
            if (!newValue) {
                listViewPretragaKatalskiBrojArtikli.setVisible(false);
            }
        }
    };

    public void txtFopisArtiklaMc(MouseEvent mouseEvent) {
        txtFopisArtikla.setText("");
    }


}



