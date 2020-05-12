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
import rs.acreno.defektaza.*;
import rs.acreno.klijent.Klijent;
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


    //MENU
    @FXML private Button btnNoviRadniNalog;

    @FXML private TextField txtFieldRegOznaka;
    @FXML private TextField txtFieldImeKlijenta;

    private int brojFakture;
    private Racun racun;
    private int brojRadnogNaloga;
    private RadniNalog radniNalog;
    private int brojDefektaze;
    private Defektaza defektaza;

    //TABELA FAKTURE
    @FXML private TableView<Racun> tblFakture;
    @FXML private TableColumn<Racun, Integer> tblRowIdRacuna;
    @FXML private TableColumn<Racun, Integer> tblRowIdAutomobila;
    @FXML private TableColumn<Racun, String> tblRowIdKilometraza;
    @FXML private TableColumn<Racun, String> tblRowDatumRacuna;
    @FXML private TableColumn<Racun, Integer> tblRowPopustRacuna;
    @FXML private TableColumn<Racun, String> tblRowNapomeneRacuna;
    @FXML private TableColumn<Racun, Button> tblRowBtnIzmeniRacun;

    //TABELA RADNI NALOZI
    @FXML private TableView<RadniNalog> tblRadniNalozi;
    @FXML private TableColumn<RadniNalog, Integer> tblColIdRadniNaloga;
    @FXML private TableColumn<RadniNalog, Integer> tblColRadniNalogIdAutomobila;
    @FXML private TableColumn<RadniNalog, String> tblColRadniNalogDatum;
    @FXML private TableColumn<RadniNalog, String> tblColRadniNalogVreme;
    @FXML private TableColumn<RadniNalog, String> tblColRadniNalogKilometraza;
    @FXML private TableColumn<RadniNalog, String> tblColRadniNalogDetaljiStranke;
    @FXML private TableColumn<RadniNalog, String> tblColRadniNalogDetaljiServisera;
    @FXML private TableColumn<RadniNalog, Button> tblColRadniNalogBtnIzmeni;

    //TABELA DEFEKTAZE
    @FXML private TableView<Defektaza> tblDefektaza;
    @FXML private TableColumn<Defektaza, Integer> tblColIdDefektaze;
    @FXML private TableColumn<Defektaza, Integer> tblColIdAutaDefektaze;
    @FXML private TableColumn<Defektaza, String> tblColKilometraza;
    @FXML private TableColumn<Defektaza, String> tblColDatumDefektaze;
    @FXML private TableColumn<Defektaza, String> tblColVreme;
    @FXML private TableColumn<Defektaza, String> tblColOpisDefektaze;
    @FXML private TableColumn<Defektaza, String> tblColOstaliDetaljiDefektaze;
    @FXML private TableColumn<Defektaza, Button> tblColBtnIzmeniDefektazu;

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

    private boolean isDefektazaInEditMode; // Da li je Defektaza u Edit Modu

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


    /*
     ************************************************************
     *************** FAKTURE / RACUNI ***************************
     ************************************************************
     */
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
        tblRowIdKilometraza.setCellValueFactory(new PropertyValueFactory<>("kilometraza"));
        tblRowIdKilometraza.setStyle("-fx-alignment: CENTER;");
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
    @FXML
    public boolean btnOpenFakturaUi() throws IOException {
        FXMLLoader fxmlLoaderFaktura = new FXMLLoader(getClass().getResource(Constants.FAKTURA_UI_VIEW_URI));
        Stage stageFaktura = new Stage();
        stageFaktura.initModality(Modality.APPLICATION_MODAL);
        stageFaktura.setScene(new Scene(fxmlLoaderFaktura.load()));
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


    /*
     ********************************************************
     *************** RADNI NALOZI ***************************
     ********************************************************
     */
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
        tblColIdRadniNaloga.setCellValueFactory(new PropertyValueFactory<>("IdRadnogNaloga"));
        tblColIdRadniNaloga.setStyle("-fx-alignment: CENTER;");
        tblColRadniNalogIdAutomobila.setCellValueFactory(new PropertyValueFactory<>("IdAutomobila"));
        tblColRadniNalogIdAutomobila.setStyle("-fx-alignment: CENTER;");
        tblColRadniNalogDatum.setCellValueFactory(new PropertyValueFactory<>("Datum"));
        tblColRadniNalogDatum.setStyle("-fx-alignment: CENTER;");
        tblColRadniNalogVreme.setCellValueFactory(new PropertyValueFactory<>("Vreme"));
        tblColRadniNalogVreme.setStyle("-fx-alignment: CENTER;");
        tblColRadniNalogKilometraza.setCellValueFactory(new PropertyValueFactory<>("Kilometraza"));
        tblColRadniNalogKilometraza.setStyle("-fx-alignment: CENTER;");
        tblColRadniNalogDetaljiStranke.setCellValueFactory(new PropertyValueFactory<>("DetaljiStranke"));
        tblColRadniNalogDetaljiStranke.setStyle("-fx-alignment: CENTER;");
        tblColRadniNalogDetaljiServisera.setCellValueFactory(new PropertyValueFactory<>("DetaljiServisera"));
        tblColRadniNalogDetaljiServisera.setStyle("-fx-alignment: CENTER;");

        tblRadniNalozi.setItems(radniNalozi);
    }


    /**
     * Otvaranje Prozora {@link RadniNalogController}
     *
     * @throws IOException not found {@link Constants#RADNI_NALOZI_UI_VIEW_URI}
     */
    @FXML
    public boolean btnOpenNoviRadniNalog() throws IOException {
        FXMLLoader fxmlLoaderRadniNalog = new FXMLLoader(getClass().getResource(Constants.RADNI_NALOZI_UI_VIEW_URI));
        Stage stageRadniNalog = new Stage();
        stageRadniNalog.initModality(Modality.APPLICATION_MODAL);
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


    /*
     ********************************************************
     *************** DEFEKTAZA ***************************
     ********************************************************
     */
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

        tblColIdDefektaze.setCellValueFactory(new PropertyValueFactory<>("idDefektaze"));
        tblColIdDefektaze.setStyle("-fx-alignment: CENTER;");
        tblColIdAutaDefektaze.setCellValueFactory(new PropertyValueFactory<>("idAuta"));
        tblColIdAutaDefektaze.setStyle("-fx-alignment: CENTER;");
        tblColKilometraza.setCellValueFactory(new PropertyValueFactory<>("kilometraza"));
        tblColKilometraza.setStyle("-fx-alignment: CENTER;");
        tblColDatumDefektaze.setCellValueFactory(new PropertyValueFactory<>("datumDefektaze"));
        tblColDatumDefektaze.setStyle("-fx-alignment: CENTER;");
        tblColVreme.setCellValueFactory(new PropertyValueFactory<>("vreme"));
        tblColVreme.setStyle("-fx-alignment: CENTER;");
        tblColOpisDefektaze.setCellValueFactory(new PropertyValueFactory<>("opisDefektaze"));
        tblColOpisDefektaze.setStyle("-fx-alignment: CENTER;");
        tblColOstaliDetaljiDefektaze.setCellValueFactory(new PropertyValueFactory<>("ostaliDetaljiDefektaze"));
        tblColOstaliDetaljiDefektaze.setStyle("-fx-alignment: CENTER;");

        tblDefektaza.setItems(defektaze);
    }

    @FXML
    public boolean btnOpenDefektaza() throws IOException {

        FXMLLoader fxmlLoaderDefektaza = new FXMLLoader(getClass().getResource(Constants.DEFEKTAZA_UI_VIEW_URI));
        Stage stageDefektaza = new Stage();
        stageDefektaza.initModality(Modality.APPLICATION_MODAL);
        stageDefektaza.setScene(new Scene(fxmlLoaderDefektaza.load()));

        stageDefektaza.setOnCloseRequest(windowEvent -> {
            popuniTabeluDefektaza(); //Popuni tabelu jer kada se pravi nova Defektaza nece da se refresuje.
            tblDefektaza.refresh(); //Uradi refresh tabele Defektaza da se vide izmene.
        });

        //Inicijalizacija Defektaza Controllora-a i setovanje naslova
        DefektazaController defektazaController = fxmlLoaderDefektaza.getController();
        defektazaController.setAutmobilController(this, stageDefektaza);

        //Postavi Title u stageu FakturaController
        stageDefektaza.setTitle("Registarska Oznaka: " + txtFieldRegOznaka.getText()
                + " || Klijent: " + txtFieldImeKlijenta.getText());

        defektazaController.setBrojDefektaze(brojDefektaze);//Prosledi u DefektazaView broj DF (EDIT MODE)
        defektazaController.setEditDefektaza(defektaza); //Prosledi u Defektaza Objekat broj Defektaze (EDIT MODE)

        stageDefektaza.showAndWait();
        return isDefektazaInEditMode = false;
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

