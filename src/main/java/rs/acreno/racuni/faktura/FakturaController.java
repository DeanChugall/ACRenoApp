package rs.acreno.racuni.faktura;

import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
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
import rs.acreno.racuni.print_racun.UiPrintRacuniControler;
import rs.acreno.system.constants.Constants;
import rs.acreno.system.exeption.AcrenoException;
import rs.acreno.system.util.ActionButtonTableCell;
import rs.acreno.system.util.GeneralUiUtility;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class FakturaController implements Initializable {

    public Button btnCloseFakture;
    public Button btnOdustaniObrisiRacun;
    @FXML private TextField txtFidRacuna;
    @FXML private TextField txtFklijentImePrezime;
    @FXML private TextField txtFregTablica;
    @FXML private TextField txtFieldPretragaArtikla;
    @FXML private DatePicker datePickerDatumRacuna;
    @FXML private TextField txtFpopustRacuna;
    @FXML private TextArea txtAreaNapomenaRacuna;


    //ARTICLES FIELDS in Faktura
    @FXML private ListView<Artikl> listViewPretragaArtikli;
    @FXML private TextField txtFidArtikla;
    @FXML private TextField txtFcenaArtikla;
    @FXML private TextField txtFnabavnaCenaArtikla;
    @FXML private TextField txtFKolicinaArtikla;
    @FXML private TextField txtFjedinicaMereArtikla;
    @FXML private TextField txtFpopustArtikla;
    @FXML private Button btnDodajArtiklRacun;
    @FXML private TextField txtFopisArtikla;
    @FXML private TextArea txtAreaDetaljiOpisArtikla;

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
    @FXML private TableColumn<PosaoArtikli, Button> tblRowButton;

    //INIT GUI FIELDS
    private int idAutomobila;
    private String regOznakaAutomobila;
    private int idKlijenta;
    private String imePrezimeKlijenta;

    //INIT ObservableList-s
    private ObservableList<Automobil> automobili;
    private ObservableList<Klijent> klijenti;
    private ObservableList<Racun> racuni;
    private ObservableList<Artikl> artikli;
    private ObservableList<PosaoArtikli> posaoArtikli;

    //RACUN STAFF OBJECT
    private Racun noviRacun;

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

    private int brojFakture;

    public void setBrojFakture(int brojFakture) {
        this.brojFakture = brojFakture;
    }

    //Inicijalizacija Racuni Objekta
    private final RacuniDAO racuniDAO = new SQLRacuniDAO();
    private final PosaoArtikliDAO posaoArtikliDAO = new SQLPosaoArtikliDAO();

    private Stage stagePrint;

    /**
     * Empty Constructor if we need in some case
     */
    public FakturaController() {
    }

    private AutomobiliController automobiliController;
    private Stage automobilStage;

    /**
     * Seter metoda koja se koristi u {@link AutomobiliController#setAutoServisController(AutoServisController, Stage)}-u
     * Takodje se prosledjuje i STAGE ako bude zatrebalo, a iz {@link AutomobiliController #btnOpenFakturaUi()}-a
     * Prosledjeni Automobil i Klijent objekti su iz {@link AutomobiliController}, a impl u {@link #initGUI()}
     *
     * @param autmobilController referenca ka automobil kontroloru
     * @param automobilStage     refereca ka automobil Stage-u
     * @see AutomobiliController
     */
    public void setAutmobilController(AutomobiliController autmobilController, Stage automobilStage) {
        this.automobiliController = autmobilController;
        this.automobilStage = automobilStage;
    }

    /**
     * Inicijalizacija {@link FakturaController}-a sa potrebni podacima
     * <p>
     * Posatvljanje "delete" dugmica u {@link #tblPosaoArtikli}, za brisanje artikla u racunu i obavestenje po brisanju
     * Brisemo poreko #{@link PosaoArtikliDAO#deletePosaoArtikliDao(PosaoArtikli)}.
     * {@code tblPosaoArtikli.getItems().remove(p)} Brise Artikl iz Table {@link #tblPosaoArtikli}
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
            txtFieldPretragaArtikla.setOnKeyReleased(this::txtFieldPretragaArtiklaKeyListener);
            listViewPretragaArtikli.setOnMouseClicked(this::zatvoriListViewSearchArtikli);
            btnDodajArtiklRacun.setOnMouseClicked(this::btnDodajArtiklRacunMouseClick);

            //Postavljenje dugmica ADD u Tabeli ARTIKLI
            tblRowButton.setCellFactory(ActionButtonTableCell.forTableColumn("x", (PosaoArtikli p) -> {
                try {
                    posaoArtikliDAO.deletePosaoArtikliDao(p);
                    GeneralUiUtility.alertDialogBox(
                            Alert.AlertType.CONFIRMATION,
                            "BRISANJE ARTIKLA",
                            "ARTIKL: " + p.getIdArtikla(),
                            "Uspesno obrisan artikl.");
                } catch (AcrenoException | SQLException e) {
                    e.printStackTrace();
                }
                tblPosaoArtikli.getItems().remove(p); //Brisi Artikl i tabele "tblPosaoArtikli"
                return p;
            }));

            // Ako je racun u edit modu nemoj praviti novi racun nego prosledi RACUN koji je za izmenu
            if (automobiliController.isRacunInEditMode()) { //TRUE
                System.out.println("DA LI JE RACUN U EDIT MODU: " + automobiliController.isRacunInEditMode());
                System.out.println("ID RACUNA U EDIT MODU JE: " + brojFakture);
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
        regOznakaAutomobila = automobili.get(0).getRegOznaka();//Moze jer je samo jedan Automobil
        idKlijenta = klijenti.get(0).getIdKlijenta();//Moze jer je samo jedan Klijent
        imePrezimeKlijenta = klijenti.get(0).getImePrezime();//Moze jer je samo jedan Klijent
        //Popunjavanje GUIa
        txtFklijentImePrezime.setText(imePrezimeKlijenta);
        txtFregTablica.setText(regOznakaAutomobila);
        txtFpopustRacuna.setText(String.valueOf(0));
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
     * u kodu {@code fakturaController.setEditRacun(racun)}, a u seteru {@link #setEditRacun(Racun)}
     * <p>
     * Ako nismo u EDIT MODU(FALSE), pravimo novi objekat {@link Racun} i bitno da se odredi koji
     * je sledeci {@link #brojFakture}. Ovde je bio problem jer kada se obrise Racun ID se pomera za jedan
     * iako je obrisan.
     *
     * @param isInEditMode da li smo u Edit Modu
     * @see AutomobiliController#btnOpenFakturaUi()
     */
    private void newOrEditRacun(boolean isInEditMode) {
        initGUI(); //Inicijalizacija podataka za novi racun bez obzira na edit mode
        if (isInEditMode) {
            System.out.println("TRUE WE ARE IN EDIT MODE");
            txtFidRacuna.setText(String.valueOf(noviRacun.getIdRacuna()));
            datePickerDatumRacuna.setValue(LocalDate.parse(noviRacun.getDatum()));
            txtAreaNapomenaRacuna.setText(noviRacun.getNapomeneRacuna());
            txtFpopustRacuna.setText(String.valueOf(noviRacun.getPopust()));

        } else {
            noviRacun = new Racun();
            noviRacun.setIdRacuna(brojFakture);
            noviRacun.setIdAutomobila(idAutomobila);
            noviRacun.setDatum(datePickerDatumRacuna.getValue().toString());
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
     * EDIT MODE (TRUE) pa je potrebno popuniti tabelu sa {@link PosaoArtikli}-ma koji su
     * filtrirani po IDu preko {@link PosaoArtikliDaoSearchType#ID_RACUNA_POSAO_ARTIKLI_DAO}
     * u {@link #initialize} metodi {@link FakturaController}-a.
     *
     * @see PosaoArtikli
     * @see PosaoArtikliDaoSearchType#ID_RACUNA_POSAO_ARTIKLI_DAO
     */
    private void popuniTabeluRacuni() {
        tblRowidPosaoArtikli.setCellValueFactory(new PropertyValueFactory<>("idPosaoArtikli"));
        tblRowidPosaoArtikli.setStyle("-fx-alignment: CENTER;");
        tblRowidRacuna.setCellValueFactory(new PropertyValueFactory<>("idRacuna"));
        tblRowidRacuna.setStyle("-fx-alignment: CENTER;");
        tblRowidArtikla.setCellValueFactory(new PropertyValueFactory<>("idArtikla"));
        tblRowidArtikla.setStyle("-fx-alignment: CENTER;");
        tblRowNazivArtikla.setCellValueFactory(new PropertyValueFactory<>("nazivArtikla"));
        tblRowNazivArtikla.setStyle("-fx-alignment: CENTER;");
        tblRowOpisArtikla.setCellValueFactory(new PropertyValueFactory<>("opisPosaoArtiklli"));
        tblRowOpisArtikla.setStyle("-fx-alignment: CENTER;");
        tblRowCena.setCellValueFactory(new PropertyValueFactory<>("cena"));
        tblRowCena.setStyle("-fx-alignment: CENTER;");
        tblRowNabavnaCena.setCellValueFactory(new PropertyValueFactory<>("nabavnaCena"));
        tblRowNabavnaCena.setStyle("-fx-alignment: CENTER;");
        tblRowKolicina.setCellValueFactory(new PropertyValueFactory<>("kolicina"));
        tblRowKolicina.setStyle("-fx-alignment: CENTER;");
        tblRowJedinicaMere.setCellValueFactory(new PropertyValueFactory<>("jedinicaMere"));
        tblRowJedinicaMere.setStyle("-fx-alignment: CENTER;");
        tblRowPopust.setCellValueFactory(new PropertyValueFactory<>("popust"));
        tblRowPopust.setStyle("-fx-alignment: CENTER;");
        tblPosaoArtikli.setItems(posaoArtikli);
    }

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
    private void txtFieldPretragaArtiklaKeyListener(KeyEvent keyEvent) {
        txtFieldPretragaArtikla.textProperty().addListener(observable -> {
            if (txtFieldPretragaArtikla.textProperty().get().isEmpty()) {
                listViewPretragaArtikli.setItems(artikli);
                return;
            }
            ObservableList<Artikl> artikl = null;
            ObservableList<Artikl> tempArtikl = null;
            try {
                ArtikliDAO artikliDAO = new SQLArtikliDAO();// inicijalizacija podataka iz BAZE
                artikl = FXCollections.observableArrayList(artikliDAO.findAllArtikle()); //Svi Automobili
                tempArtikl = FXCollections.observableArrayList(); //Lista u koju dodajemo nadjene Auto objekte
            } catch (AcrenoException | SQLException e) {
                e.printStackTrace();
            }
            for (int i = 0; i < (artikl != null ? artikl.size() : 0); i++) {

                String nazivArtikla = artikl.get(i).getNazivArtikla().toLowerCase();//Trenutna tablica auta

                if (nazivArtikla.contains(txtFieldPretragaArtikla.textProperty().get())) {
                    tempArtikl.add(artikl.get(i)); // Dodaje nadjeni auto u temp listu
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
        });
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
    private void zatvoriListViewSearchArtikli(@NotNull MouseEvent mouseEvent) {
        //Na dupli click vraca Radni Nalog Objekat i otvara Radni nalog Dashboard
        if (mouseEvent.getButton().equals(MouseButton.PRIMARY) && mouseEvent.getClickCount() == 2) {
            // Popunjavanje TF polja sa podacima Artikla
            String nazivArtikla =
                    listViewPretragaArtikli.getSelectionModel().getSelectedItems().get(0).getNazivArtikla();
            int idArtikla =
                    listViewPretragaArtikli.getSelectionModel().getSelectedItems().get(0).getIdArtikla();
            double cenaArtikla =
                    listViewPretragaArtikli.getSelectionModel().getSelectedItems().get(0).getCenaArtikla();
            double nabavnaCenaArtikla =
                    listViewPretragaArtikli.getSelectionModel().getSelectedItems().get(0).getNabavnaCenaArtikla();
            String jedinicaMereArtikla =
                    listViewPretragaArtikli.getSelectionModel().getSelectedItems().get(0).getJedinicaMere();
            String opisArtikla =
                    listViewPretragaArtikli.getSelectionModel().getSelectedItems().get(0).getOpisArtikla();
            //Popunjavanje GUI polja
            txtFidArtikla.setText(String.valueOf(idArtikla));
            txtFcenaArtikla.setText(String.valueOf(cenaArtikla));
            txtFnabavnaCenaArtikla.setText(String.valueOf(nabavnaCenaArtikla));
            txtFKolicinaArtikla.setText(String.valueOf(1));
            txtFjedinicaMereArtikla.setText(jedinicaMereArtikla);
            txtFpopustArtikla.setText(String.valueOf(0));
            txtFieldPretragaArtikla.setText(nazivArtikla);
            txtFopisArtikla.setText(opisArtikla);

            btnDodajArtiklRacun.setDisable(false); // omoguci dugme dodaj u listu
            listViewPretragaArtikli.setVisible(false); //Sakrij ListView
        }
    }

    /**
     * <p> Pravljenje "POSAO ARTIKLI" objekta za FK tabelu "PosaoArtikli" (Many to Many) </p>
     * <p> Kreiranje objekta {@link PosaoArtikli} i popunjavanje sa podacima </p>
     * <p>
     * {@code  posaoArtikliDAO.insertPosaoArtikliDao(posaoArtikliObject);} // SQL Staff {@link PosaoArtikliDAO}
     * </p>
     * <p>
     * Popunjavanje tabele {@link #tblPosaoArtikli}
     * </p>
     *
     * @param mouseEvent in case if we need
     * @see PosaoArtikli
     * @see PosaoArtikliDAO#insertPosaoArtikliDao(PosaoArtikli)
     */
    private void btnDodajArtiklRacunMouseClick(MouseEvent mouseEvent) {

        // Create Posao Artikli Object and populate with data
        PosaoArtikli posaoArtikliObject = new PosaoArtikli();
        //  posaoArtikliObject.setIdPosaoArtikli(0);
        posaoArtikliObject.setIdRacuna(brojFakture);
        posaoArtikliObject.setIdArtikla(Integer.parseInt(txtFidArtikla.getText()));
        posaoArtikliObject.setNazivArtikla(txtFieldPretragaArtikla.getText());
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
            ObservableList<PosaoArtikli> test = FXCollections.observableArrayList(
                    posaoArtikliDAO.findPosaoArtikliByPropertyDao(
                            PosaoArtikliDaoSearchType.ID_RACUNA_POSAO_ARTIKLI_DAO, brojFakture));
            // Insert in table "tblPosaoArtikli"
            tblRowidPosaoArtikli.setCellValueFactory(cellData ->
                    new SimpleIntegerProperty(cellData.getValue().getIdPosaoArtikli()));
            tblRowidRacuna.setCellValueFactory(cellData ->
                    new SimpleIntegerProperty(cellData.getValue().getIdRacuna()));
            tblRowidArtikla.setCellValueFactory(cellData ->
                    new SimpleIntegerProperty(cellData.getValue().getIdArtikla()));
            tblRowNazivArtikla.setCellValueFactory(cellData ->
                    new SimpleStringProperty(cellData.getValue().getNazivArtikla()));
            tblRowOpisArtikla.setCellValueFactory(cellData ->
                    new SimpleStringProperty(cellData.getValue().getOpisPosaoArtiklli()));
            tblRowCena.setCellValueFactory(cellData ->
                    new SimpleDoubleProperty(cellData.getValue().getCena()));
            tblRowNabavnaCena.setCellValueFactory(cellData ->
                    new SimpleDoubleProperty(cellData.getValue().getNabavnaCena()));
            tblRowKolicina.setCellValueFactory(cellData ->
                    new SimpleDoubleProperty(cellData.getValue().getKolicina()));
            tblRowJedinicaMere.setCellValueFactory(cellData ->
                    new SimpleStringProperty(cellData.getValue().getJedinicaMere()));
            tblRowPopust.setCellValueFactory(cellData ->
                    new SimpleDoubleProperty(cellData.getValue().getPopust()));
            tblPosaoArtikli.setItems(test);

        } catch (AcrenoException | SQLException e) {
            e.printStackTrace();
        }
        btnDodajArtiklRacun.setDisable(true); // onemoguci dugme dodaj u listu
    }

    /**
     * Inicijalizacija {@link UiPrintRacuniControler}, a implementira se {@link #initialize}
     *
     * @param fxmlLoader prosledjivanje FXMLoadera {@link UiPrintRacuniControler} - u
     * @see UiPrintRacuniControler
     */
    private void initUiPrintControler(@NotNull FXMLLoader fxmlLoader) {
        UiPrintRacuniControler uiPrintRacuniControler = fxmlLoader.getController();
        uiPrintRacuniControler.setFakturaController(this, stagePrint);
        uiPrintRacuniControler.setIdRacuna(Integer.parseInt(txtFidRacuna.getText()));
    }

    /**
     * Otvaranje Print Fakture {@link UiPrintRacuniControler}
     * Inicijalizacija Print Controlora i prosledjivanje id Racuna {@link #initUiPrintControler}
     * Na ovom mestu je zato sto je ovo poslednja pozicija koja se radi pre otvaranja Print Cotrolora
     *
     * @see #initUiPrintControler(FXMLLoader)
     * @see UiPrintRacuniControler
     */
    @FXML
    private void btnPrintAction() {
        System.out.println("OTVORI PRINT FXML **UiProntControler !!!!");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(Constants.PRINT_FAKTURA_UI_VIEW_URI));
            stagePrint = new Stage();
            stagePrint.initModality(Modality.APPLICATION_MODAL);
            stagePrint.setScene(new Scene(loader.load()));

            initUiPrintControler(loader); //Inicijalizacija Porint Controlora i prosledjivanje id Racuna

            stagePrint.showAndWait();//Open Stage and wait

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * UPDATE Racuna kada se nesto promeni u njemu...(Datum...Napomena...Popust)
     * <p>
     * Setuju se svi podaci za izmenjen racun pokupljeni iz TF-ova
     * Zatim se radi update sa {@link RacuniDAO#updateRacun(Racun)}
     * @see RacuniDAO#updateRacun(Racun)
     * @see GeneralUiUtility#alertDialogBox(Alert.AlertType, String, String, String)
     */
    @FXML
    private void btnSacuvajRacunAction() {
        try {
            //UPDATE NOVO RACUNA SA NOVIM VREDNOSTIMA ZATO OVDE REDEFINISEMO NOVI RACUN
            noviRacun.setIdRacuna(brojFakture);
            noviRacun.setIdAutomobila(idAutomobila);
            noviRacun.setDatum(datePickerDatumRacuna.getValue().toString());
            noviRacun.setNapomeneRacuna(txtAreaNapomenaRacuna.getText());
            noviRacun.setPopust(Integer.parseInt(txtFpopustRacuna.getText()));
            racuniDAO.updateRacun(noviRacun);
            GeneralUiUtility.alertDialogBox(
                    Alert.AlertType.CONFIRMATION,
                    "USPESNO SACUVAN RACUN",
                    "EDITOVANJE RACUNA",
                    "Uspesno ste sacuvali racun br." + brojFakture
            );
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
            racuniDAO.deleteRacun(brojFakture);
            btnOdustaniObrisiRacun.fireEvent(new WindowEvent(automobilStage, WindowEvent.WINDOW_CLOSE_REQUEST));
            ((Stage) (((Button) actionEvent.getSource()).getScene().getWindow())).close();
        } catch (AcrenoException | SQLException acrenoException) {
            acrenoException.printStackTrace();
        }
    }

    /**
     * Zatvaranje FAKTURA UIa i refresh tabele RAcuni u {@link AutomobiliController}-u
     * <p>
     * Da bi se refresovala tabela Racuni u {@link AutomobiliController}-u potrebno je pozvati
     * {@code WindowEvent.WINDOW_CLOSE_REQUEST} koji je implementiran u {@link AutomobiliController#btnOpenFakturaUi}
     *
     * @param actionEvent event for hide scene {@link FakturaController}
     * @see AutomobiliController#btnOpenFakturaUi
     */
    @FXML
    private void btnCloseFaktureAction(@NotNull ActionEvent actionEvent) {
        //TODO: pitati na zatvaranju da li hocemo da se sacuva RACUN ili da obrise
        btnCloseFakture.fireEvent(new WindowEvent(automobilStage, WindowEvent.WINDOW_CLOSE_REQUEST));
        ((Stage) (((Button) actionEvent.getSource()).getScene().getWindow())).close();
    }

}

