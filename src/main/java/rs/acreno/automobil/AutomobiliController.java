package rs.acreno.automobil;

import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
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
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import rs.acreno.automobil.ui_add_edit_automobil.AddEditAutomobilController;
import rs.acreno.autoservis.AutoServisController;
import rs.acreno.defektaza.*;
import rs.acreno.klijent.Klijent;
import rs.acreno.klijent.ui_klijent.CreateNewKlijentUiController;
import rs.acreno.nalozi.*;
import rs.acreno.racuni.Racun;
import rs.acreno.racuni.RacuniDAO;
import rs.acreno.racuni.RacuniSearchType;
import rs.acreno.racuni.SQLRacuniDAO;
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

    private static final Logger logger = Logger.getLogger(AutomobiliController.class);
    @FXML private Button btnZatvoriProzorAutomobili;


    //MENU
    @FXML private TextField txtFieldRegOznaka;
    @FXML private TextField txtFieldImeKlijenta;

    //FXMLs TABELA FAKTURE
    @FXML private TableView<Racun> tblFakture;
    @FXML private TableColumn<Racun, Number> tblRowIdRacuna;
    @FXML private TableColumn<Racun, Number> tblRowIdAutomobila;
    @FXML private TableColumn<Racun, String> tblRowIdKilometraza;
    @FXML private TableColumn<Racun, String> tblRowDatumRacuna;
    @FXML private TableColumn<Racun, Number> tblRowPopustRacuna;
    @FXML private TableColumn<Racun, String> tblRowNapomeneRacuna;
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

    /**
     * Posto koristimo isti UI za EDIT I NEW {@link Racun}, potrebno je da pratimo da li smo
     * u EDIT modu ili u NEW modu.
     * Edit mode dobijamo kada se klikne na dugmic u tabeli {@link #popuniTabeluRacuni()}, a koji je inicijalizovan
     * u {@link #initialize(URL, ResourceBundle)}.
     *
     * @see #initialize(URL, ResourceBundle)
     * @see #btnOpenFakturaUi()
     */
    private boolean isRacunInEditMode; // Da li je recun u Edit Modu

    private boolean isDefektazaInEditMode; // Da li je Defektaza u Edit Modu

    private ObservableList<Automobil> automobil;
    private ObservableList<Klijent> klijenti;

    /**
     * Geter za {@link #isRacunInEditMode} koji se koristi u {@link FakturaController#initialize(URL, ResourceBundle)}
     * U inicijalnoj metodi {@link FakturaController} proveravamo da li smo u EDIT MODU ili u NEW MODU.
     * Ako smo u EDITu onda se ne pravi novi Objekat {@link Racun}, a ako smo u NEW pravimo novi objekat {@link Racun}.
     *
     * @see FakturaController #newOrEditRacun(boolean)
     * @see FakturaController#initialize(URL, ResourceBundle)
     * @see Racun
     */
    public boolean isRacunInEditMode() {
        return isRacunInEditMode;
    }

    /**
     * Posto koristimo isti UI za EDIT I NEW {@link RadniNalog}, potrebno je da pratimo da li smo
     * u EDIT modu ili u NEW modu.
     * Edit mode dobijamo kada se klikne na dugmic u tabeli {@link #popuniTabeluRacuni()}, a koji je inicijalizovan
     * u {@link #initialize(URL, ResourceBundle)}.
     *
     * @see #initialize(URL, ResourceBundle)
     * @see #btnOpenNoviRadniNalog()
     */
    private boolean isRadniNalogInEditMode; // Da li je Radni Nalog u Edit Modu

    /**
     * Geter za {@link #isRacunInEditMode} koji se koristi u {@link RadniNalogController#initialize(URL, ResourceBundle)}
     * U inicijalnoj metodi {@link RadniNalogController} proveravamo da li smo u EDIT MODU ili u NEW MODU.
     * Ako smo u EDITu onda se ne pravi novi Objekat {@link RadniNalog},
     * a ako smo u NEW pravimo novi objekat {@link RadniNalog}.
     *
     * @see RadniNalogController #newOrEditRAdniNalog(boolean)
     * @see RadniNalogController#initialize(URL, ResourceBundle)
     * @see RadniNalog
     */
    public boolean isRadniNalogInEditMode() {
        return isRadniNalogInEditMode;
    }


    /**
     * Geter za {@link #isDefektazaInEditMode} koji se koristi u
     * {@link DefektazaController#initialize(URL, ResourceBundle)}
     * U inicijalnoj metodi {@link DefektazaController} proveravamo da li smo u EDIT MODU ili u NEW MODU.
     * Ako smo u EDITu onda se ne pravi novi Objekat {@link Defektaza},
     * a ako smo u NEW pravimo novi objekat {@link Defektaza}.
     *
     * @see DefektazaController #newOrEditRAdniNalog(boolean)
     * @see DefektazaController#initialize(URL, ResourceBundle)
     * @see Defektaza
     */
    public boolean isDefektazaInEditMode() {
        return isDefektazaInEditMode;
    }

    /**
     * Empty AutomobilController Constructor
     */
    public AutomobiliController() {
    }

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

    // 1.0 ******************* KOMUNIKACIJA SA AUTOSERVIS CONTROLLOROM ******************

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

    // 2.0 ************* INICIJALIZACIJA *********************************

    /**
     * Inicijalizacija {@link AutomobiliController}-a
     * {@code tblRowBtnIzmeniRacun.setCellFactory} postavlje dugmice u {@link #tblFakture} tabelu Racuna.
     * Dugmici pokrecu {@link #btnOpenFakturaUi()} btn koji vraca vrednost {@link #isRacunInEditMode}. Ako je
     * EDIT mode prosledjujemo {@code isRacunInEditMode = true;} i brojFakture u {@link FakturaController}.
     * Prilikom klika na Edit dugme u tabeli setujemo {@code racun = rac;} koji se dalje prosledjuje u
     * {@link #btnOpenFakturaUi()}
     * <p>
     * Setuje se REG. TABLICA{@code txtFieldRegOznaka} i IME KLIJENTA{@code txtFieldImeKlijenta}
     * {@code  txtFieldRegOznaka.setText(getAutomobil().get(0).getRegOznaka())} Moze jer je samo jedan Automobil
     * {@code  txtFieldImeKlijenta.setText(klijenti.get(0).getImePrezime()} Moze jer je samo jedan Klijent
     * <p>
     * Pa nakon toga se popunjava tabela sa racunima u {@link #popuniTabeluRacuni()}
     *
     * @param location  gde da ucitamo
     * @param resources da li ima nesto u resource
     * @see #popuniTabeluRacuni()
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Platform.runLater(() -> {
            //Inicijalizacija i Postavljenje dugmeta "IZMENI" u tabeli RACUNI
            tblRowBtnIzmeniRacun.setCellFactory(ActionButtonTableCell.forTableColumn("Izmeni", (Racun p) -> {
                // btnOpenEditRacunUiAction(p);
                try {
                    isRacunInEditMode = true; // U edit modu RACUNA smo
                    brojFakture = p.getIdRacuna(); // Setuj broj fakture jer je EDIT MODE
                    racun = p;
                    btnOpenFakturaUi(); //Otvori Fakturu UI u EDIT MODU...Provera je u FAKTURA CONTROLORU

                } catch (IOException e) {
                    e.printStackTrace();
                }
                //tblPosaoArtikli.getItems().remove(p);
                return p;
            }));

            //Inicijalizacija i Postavljenje dugmeta "IZMENI" u tabeli RADNI NALOZI
            tblColRadniNalogBtnIzmeni.setCellFactory(ActionButtonTableCell.forTableColumn("Izmeni", (RadniNalog p) -> {
                // btnOpenEditRacunUiAction(p);
                try {
                    isRadniNalogInEditMode = true; // U edit modu RADNOG NALOGA smo
                    brojRadnogNaloga = p.getIdRadnogNaloga(); // Setuj broj Radnog Naloga jer je EDIT MODE
                    radniNalog = p;
                    btnOpenNoviRadniNalog(); //Otvori RADNO NALOG UI u EDIT MODU...Provera je u RADNI NALOG CONTROLORU

                } catch (IOException e) {
                    e.printStackTrace();
                }
                //tblRadniNalozi.getItems().remove(p);
                return p;
            }));

            //Inicijalizacija i Postavljenje dugmeta "IZMENI" u tabeli DEFEKTAZA
            tblColBtnIzmeniDefektazu.setCellFactory(ActionButtonTableCell.forTableColumn("Izmeni", (Defektaza defektaza) -> {
                // btnOpenEditRacunUiAction(p);
                try {
                    isDefektazaInEditMode = true; // U edit modu DEFEKTAZE smo
                    brojDefektaze = defektaza.getIdDefektaze(); // Setuj broj DEFEKTAZE jer je EDIT MODE
                    this.defektaza = defektaza;
                    btnOpenDefektaza(); //Otvori DEFEKTAZU UI u EDIT MODU...Provera je u DEFEKTAZA CONTROLORU

                } catch (IOException e) {
                    e.printStackTrace();
                }
                //tblDefektaza.getItems().remove(p);
                return defektaza;
            }));

            txtFieldRegOznaka.setText(getAutomobil().get(0).getRegOznaka());// Moze jer je samo jedan Automobil
            txtFieldImeKlijenta.setText(klijenti.get(0).getImePrezime());// Moze jer je samo jedan Klijent

            popuniTabeluRacuni(); // Popuni tabelu RACUNI sa podacima
            popuniTabeluRadniNalozi(); // Popuni tabelu RADNI NALOZI sa podacima
            popuniTabeluDefektaza(); // Popuni tabelu DEFEKTAZE sa podacima
        });
    }


    // 3.0 *************** FAKTURE / RACUNI ***************************

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

        tblFakture.setItems(racuni);
    }

    /**
     * Otvaranje {@link FakturaController} UI-a i komunikacija izmedju ova dva kontrolora. {@code onMouseClick}
     * <p>
     * Komunikacija se implemetira {@link FakturaController#setAutmobilController(AutomobiliController, Stage)}
     * <p>
     * Takodje se prosledjuje i STAGE u slucaju da zatreba.
     * <p>
     * Nakon toga se incijalizuje Title preko {@code stageFaktura.setTitle }
     * <p>
     * Vraca vrednost da li je u edit modu ili ne... Ovde nije, jer je kliknuto direktno dugme "NAPRAVI NOVI RACUN"
     * U edit modu se postavlja na true {@link #initialize(URL, ResourceBundle)}
     * <p>
     * {@code windowEvent -> {tblFakture.refresh()} Refresuje tabelu u povratku u Automobil UI da se vide izmene
     * koji se takodje poziva iz {@link FakturaController #btnCloseFaktureAction(ActionEvent)}, a sve zbog
     * refresha i popunjavanja "tblFakture" po zavrsetku EDITa.
     *
     * @return boolean isRacunInEditMode //
     * @throws IOException ako nije nadjen .fxml {@link Constants#FAKTURA_UI_VIEW_URI}
     * @see {@link FakturaController#btnCloseFaktureAction(ActionEvent)}
     * @see #initialize(URL, ResourceBundle)
     */
    @FXML public boolean btnOpenFakturaUi() throws IOException {
        FXMLLoader fxmlLoaderFaktura = new FXMLLoader(getClass().getResource(Constants.FAKTURA_UI_VIEW_URI));
        Stage stageFaktura = new Stage();
        stageFaktura.initModality(Modality.APPLICATION_MODAL);
        stageFaktura.getIcons().add(new Image(AutoServisController.class.getResourceAsStream(Constants.APP_ICON)));
        stageFaktura.setScene(new Scene(fxmlLoaderFaktura.load()));

        //Refresuje tabelu u povratku u Automobil UI
        stageFaktura.setOnCloseRequest(windowEvent -> {
            popuniTabeluRacuni(); //Popuni tabelu jer kada se pravi novi racun nece da se refresuje
            tblFakture.refresh(); //Uradi refresh tabele da se vide izmene
        });

        //Inicijalizacija FakturaController-a i setovanje naslova
        FakturaController fakturaController = fxmlLoaderFaktura.getController();
        fakturaController.setAutmobilController(this, stageFaktura);

        //Postavi Title u stageu FakturaController
        stageFaktura.setTitle("Registarska Oznaka: " + txtFieldRegOznaka.getText()
                + " || Klijent: " + txtFieldImeKlijenta.getText());

        fakturaController.setBrojFakture(brojFakture);//Prosledi u FakturaView broj fakture (EDIT MODE)
        fakturaController.setEditRacun(racun); //Prosledi u RACUN Objekat broj fakture (EDIT MODE)
        stageFaktura.showAndWait();

        return isRacunInEditMode = false; //Nije u Edit Modu jer je kliknuto direktno dugme NOVI RACUN
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
            radniNalozi = FXCollections.observableArrayList(
                    radniNalogDAO.findRadniNalogByProperty(RadniNalogSearchType.ID_AUTOMOBILA, getAutomobil().get(0).getIdAuta()));
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

    /**
     * Otavranje {@link RadniNalogController}-a UI-a, setovanje {@code OnCloseRequest(windowEvent} koji
     * se koristi za update tabele u {@link #popuniTabeluRadniNalozi()} u {@link AutomobiliController}-u.
     * <p>
     * {@code tblRadniNalozi.refresh()} mora da se refresuje tabela {@link #tblRadniNalozi} kada se naprave izmene
     * u {@link RadniNalogController} UI-u. Drugim recima, kada se unesu neke vrednosti defektaze i sacuvaju
     * pa se zatvori RAdni Nalog UIa, mora da se popuni i u {@link AutomobiliController} tabele radni nalog.
     * <p>
     * Inicijalizacija {@link RadniNalogController}-a da bi smo mogli da pristupimo poljima, ali najbitnije
     * da prosledimo broj radnog naloga, jer odavde mozemo da idemo i u EDIT MODE. EDIT MODE se proverava
     *
     * @throws IOException not found {@link Constants#RADNI_NALOZI_UI_VIEW_URI}
     * @see RadniNalogController#setEditRadniNalog(RadniNalog)
     * @see RadniNalogController #newOrEditRadniNalog (boolean)
     * @see #popuniTabeluRadniNalozi()
     */
    @FXML public boolean btnOpenNoviRadniNalog() throws IOException {
        FXMLLoader fxmlLoaderRadniNalog = new FXMLLoader(getClass().getResource(Constants.RADNI_NALOZI_UI_VIEW_URI));
        Stage stageRadniNalog = new Stage();
        stageRadniNalog.initModality(Modality.APPLICATION_MODAL);
        stageRadniNalog.getIcons().add(new Image(AutoServisController.class.getResourceAsStream(Constants.APP_ICON)));
        stageRadniNalog.setScene(new Scene(fxmlLoaderRadniNalog.load()));

        stageRadniNalog.setOnCloseRequest(windowEvent -> {
            popuniTabeluRadniNalozi(); //Popuni tabelu jer kada se pravi novi Radni nalog nece da se refresuje
            tblRadniNalozi.refresh(); //Uradi refresh tabele da se vide izmene
        });

        //Inicijalizacija FakturaController-a i setovanje naslova
        RadniNalogController radniNalogController = fxmlLoaderRadniNalog.getController();
        radniNalogController.setAutmobilController(this, stageRadniNalog);

        //Postavi Title u stageu FakturaController
        stageRadniNalog.setTitle("Registarska Oznaka: " + txtFieldRegOznaka.getText()
                + " || Klijent: " + txtFieldImeKlijenta.getText());

        radniNalogController.setBrojRadnogNaloga(brojRadnogNaloga);//Prosledi u RadniNalogView broj RN (EDIT MODE)
        radniNalogController.setEditRadniNalog(radniNalog); //Prosledi u R.Nalog Objekat broj Radnog Naloga (EDIT MODE)
        stageRadniNalog.showAndWait();
        return isRadniNalogInEditMode = false;
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
                    defektazaDAO.findDefektazaByProperty(DefektazaSearchType.ID_AUTA, getAutomobil().get(0).getIdAuta()));
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
    @FXML public boolean btnOpenDefektaza() throws IOException {
        FXMLLoader fxmlLoaderDefektaza = new FXMLLoader(getClass().getResource(Constants.DEFEKTAZA_UI_VIEW_URI));
        Stage stageDefektaza = new Stage();
        stageDefektaza.initModality(Modality.APPLICATION_MODAL);
        stageDefektaza.getIcons().add(new Image(AutoServisController.class.getResourceAsStream(Constants.APP_ICON)));
        stageDefektaza.setScene(new Scene(fxmlLoaderDefektaza.load()));

        stageDefektaza.setOnCloseRequest(windowEvent -> {
            popuniTabeluDefektaza(); //Popuni tabelu jer kada se pravi nova Defektaza nece da se refresuje.
            tblDefektaza.refresh(); //Uradi refresh tabele Defektaza da se vide izmene.
        });

        //Inicijalizacija Defektaza Controllora-a i setovanje naslova
        DefektazaController defektazaController = fxmlLoaderDefektaza.getController();
        defektazaController.setAutmobilController(this, stageDefektaza);
        defektazaController.setBrojDefektaze(brojDefektaze);//Prosledi u DefektazaView broj DF (EDIT MODE)
        defektazaController.setEditDefektaza(defektaza); //Prosledi u Defektaza Objekat broj Defektaze (EDIT MODE)

        //Postavi Title u stageu FakturaController
        stageDefektaza.setTitle("Registarska Oznaka: " + txtFieldRegOznaka.getText()
                + " || Klijent: " + txtFieldImeKlijenta.getText());

        stageDefektaza.showAndWait();
        return isDefektazaInEditMode = false;
    }


    // 6.0 *************** KLIJENT ***************************

    /**
     * Otavranje {@link CreateNewKlijentUiController}-a UI-a, setovanje {@code OnCloseRequest(windowEvent} koji
     * se koristi za update polja Ime i prezime klijenta u {@link AutomobiliController}-u
     * <p>
     * Inicijalizacija {@link CreateNewKlijentUiController} da bi smo mogli da pristupimo poljima, ali najbitnije
     * da prosledimo {@link Klijent} Objekat posto odavde idemo u EDIT MODE.
     *
     * @throws IOException Location exception
     * @see CreateNewKlijentUiController#setKlijent(Klijent)
     * @see Klijent
     */
    @FXML private void btnOpenIzmeniKlijentaUi() throws IOException {

        FXMLLoader fxmlLoaderKlijent = new FXMLLoader(getClass().getResource(Constants.CREATE_KLIJENT_UI_VIEW_URI));
        Stage stageKlijent = new Stage();
        stageKlijent.initModality(Modality.APPLICATION_MODAL);
        stageKlijent.getIcons().add(new Image(AutoServisController.class.getResourceAsStream(Constants.APP_ICON)));
        stageKlijent.setScene(new Scene(fxmlLoaderKlijent.load()));

        stageKlijent.setOnCloseRequest(windowEvent -> {
            txtFieldImeKlijenta.setText(klijenti.get(0).getImePrezime());// Moze jer je samo jedan Klijent
        });

        //Inicijalizacija CREATE NEW KLIJENT Controllora-a
        CreateNewKlijentUiController createNewKlijentUiController = fxmlLoaderKlijent.getController();
        createNewKlijentUiController.setAutmobilController(this, stageKlijent);
        createNewKlijentUiController.setKlijent(klijenti.get(0));//Prosledi u  KLIJENT OBJEKAT (EDIT MODE)
        createNewKlijentUiController.setWeAreInEditMode(true);

        //Postavi Title u stageu Klijent Controlloru
        stageKlijent.setTitle("Uređivanje Klijenta: " + txtFieldImeKlijenta.getText());

        stageKlijent.showAndWait();
    }


    // 7.0 *************** EDIT AUTOMOBILI ***************************
    Stage stageNewAutomobil;
    @FXML private void btnOpenIzmeniAutomobilUi(ActionEvent actionEvent) throws IOException {
        // Standart FX load UI
        FXMLLoader fxmlLoaderNewAutomobil = new FXMLLoader(getClass().getResource(Constants.CREATE_EDIT_AUTOMOBIL_UI_VIEW_URI));
        stageNewAutomobil = new Stage();
        stageNewAutomobil.getIcons().add(new Image(AutoServisController.class.getResourceAsStream(Constants.APP_ICON)));
        stageNewAutomobil.initModality(Modality.APPLICATION_MODAL);
        Scene scene = new Scene(fxmlLoaderNewAutomobil.load());
        stageNewAutomobil.setScene(scene);
        stageNewAutomobil.setResizable(false);
        stageNewAutomobil.setTitle("Kreiraj Novoi Autmobil");


        stageNewAutomobil.setOnCloseRequest(windowEvent -> {
            // txtFieldImeKlijenta.setText(klijenti.get(0).getImePrezime());// Moze jer je samo jedan Klijent
            System.out.println("FORM BUUTTON btnOpenIzmeniAutomobilUi --- CREATE_EDIT_AUTOMOBIL_UI_VIEW_URI");
        });

        //Inicijalizacija CREATE NEW KLIJENT Controllora-a
        AddEditAutomobilController addEditAutomobilController = fxmlLoaderNewAutomobil.getController();
        addEditAutomobilController.setAutmobilController(this, stageNewAutomobil);

        //Prosledi u  AUTOMOBIL OBJEKAT (EDIT MODE)...Moze automobil.get(0) jer je samo jedan Automobil Obj
        addEditAutomobilController.setWeAreInEditMode(true);
        addEditAutomobilController.setAutomobil(automobil.get(0));
        addEditAutomobilController.setKlijent(klijenti.get(0));
        //Postavi Title u stageu Klijent Controlloru
        stageNewAutomobil.setTitle("Registarska Oznaka: " + txtFieldRegOznaka.getText()
                + " || Klijent: " + txtFieldImeKlijenta.getText());

        stageNewAutomobil.showAndWait();

    }


    // 8.0 *************** BUTTON STAFF ***************************

    /**
     * Zatvori prozor Automobili
     *
     * @param actionEvent posto koristimo sakrivanje prozara
     */
    @FXML private void btnZatvoriProzorAutomobiliAction(@NotNull ActionEvent actionEvent) {
        btnZatvoriProzorAutomobili.fireEvent(new WindowEvent(stageNewAutomobil, WindowEvent.WINDOW_CLOSE_REQUEST));
        //((Stage) (((Button) actionEvent.getSource()).getScene().getWindow())).close();
    }
}

