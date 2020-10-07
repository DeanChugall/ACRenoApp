package rs.acreno.autoservis;

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
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import rs.acreno.artikli.ArtkikliController;
import rs.acreno.automobil.*;
import rs.acreno.automobil.saobracajna.Saobracajna;
import rs.acreno.automobil.ui_add_edit_automobil.AddEditAutomobilController;
import rs.acreno.klijent.Klijent;
import rs.acreno.klijent.KlijentDAO;
import rs.acreno.klijent.KlijentSearchType;
import rs.acreno.klijent.SQLKlijnetDAO;
import rs.acreno.klijent.licna_karta.LicnaKarta;
import rs.acreno.klijent.ui_klijent.CreateNewKlijentUiController;
import rs.acreno.system.constants.Constants;
import rs.acreno.system.exeption.AcrenoException;
import rs.acreno.system.util.ActionButtonTableCell;
import rs.acreno.system.util.GeneralUiUtility;
import rs.acreno.system.util.properties.ApplicationProperties;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class AutoServisController implements Initializable {

    private static final Logger logger = Logger.getLogger(AutoServisController.class);


    @FXML private Line lineInternetIndicator; // Internet indikator linija

    // 1.0 *************** FXMLs **************************************
    @FXML private Label lblDate;
    @FXML private Label lblTime;
    @FXML private Label lblVerzijaAplikacije;
    @FXML private Label lblReleaseDate;
    @FXML private Button btnOtvoriArtiklKarticu;

    // 1.1 ************* FXMLs Automobil Kartica
    /**
     * txtF za Reg. Tablicu u {@link #txtFieldRegTablicaSaerchKeyListener), pa posle bitno za
     * prosledjivanje tablice {@link AutomobiliController}-u, a koristi se {@link #openAutomobiliUi }
     *
     * @see {@link #txtFieldRegTablicaSaerchKeyListener()}
     * @see {@link #openAutomobiliUi }
     * @see {@link AutomobiliController}
     */
    @FXML private TextField txtFieldRegOznaka;
    @FXML private TextField txtfVinAutomobila;
    @FXML private TextField txtfModelAutomobila;
    @FXML private TextField txtfMarkaAutomobila;
    @FXML private TextField txtfGoriivoAutomobila;
    @FXML private TextField txtfGodisteAutomobila;
    @FXML private Button btnNoviAutomobil;
    @FXML private Button btnUrediAutomobil;
    @FXML private TextField txtFidAutomobila;

    // 1.2 ************* FXMLs Klijent Kartica
    @FXML private TextField txtFieldPretragaKlijenta;
    @FXML private Button btnOtvoriAutomobilKarticu;
    @FXML private Button btnOtvoriCitacLicneKarte;
    @FXML private Button btnUcitajPodatkeSaLicneKarte;
    /* @FXML private Button btnNoviAutomobilInKlijentArea;
     @FXML private Button btnUrediAutomobilFromKlijent;*/
    @FXML private Button btnOtvoriKlijentEditMode;
    @FXML private TextField txtFiDKlijenta;
    @FXML private TextField txtFbrojTelefona;
    @FXML private TextField txtFadresaKlijenta;
    @FXML private TextField txtFmestoStanovanjaKlijenta;
    @FXML private TextField txtFeMailKlijenta;
    @FXML private TextArea txtAreaOstaliDetaljiKlijenta;

    //TABELA
    @FXML private TableView<Automobil> tblAutomobiliInKlijent;
    @FXML private TableColumn<Automobil, Number> tblColIDAutomobila;
    @FXML private TableColumn<Automobil, Number> tblColIDklijenta;
    @FXML private TableColumn<Automobil, String> tblColRegOznaka;
    @FXML private TableColumn<Automobil, String> tblColMarka;
    @FXML private TableColumn<Automobil, String> tblColModel;
    @FXML private TableColumn<Automobil, Number> tblColGodiste;
    @FXML private TableColumn<Automobil, String> tblColVrstaGoriva;
    @FXML private TableColumn<Automobil, String> tblColVibAutomobila;
    @FXML private TableColumn<Automobil, Button> tblRowButton;


    /**
     * ListView koja cuva Listu {@link Klijent} u TxtF-u prilikom filtera u kucanju.
     *
     * @see #txtfKlijentSaerchKeyListener
     * @see #zatvoriLvKlijent(MouseEvent)
     */
    @FXML private ListView<Klijent> listViewKlijentiSearch;

    /**
     * ListView koja cuva Listu {@link Automobil} u TxtF-u prilikom filtera u kucanju
     *
     * @see #txtFieldRegTablicaSaerchKeyListener
     * @see #zatvoriListViewSearchAutomobil
     */
    @FXML private ListView<Automobil> listViewAutmobiliSearch;


    /**
     * Zatvori Aplikaciju
     *
     * @see #btnCloseApplication()
     */
    @FXML public Button btnCloseApplication;


    // 2.0 *************** INICIJALIZACIJA **************************************
    /**
     * Inicijalizacija AUTOMOBIL Objekta
     * Da bi u samom startu imali sve objekte {@link Automobil}. Koristi se {@link AutomobilDAO} interfejs,
     * koji je implemetiran u {@link SQLAutomobilDAO}, a trenutno se koristi MSACCESS implementacija.
     * Sve se ubacuje u {@code ObservableList<Automobil>} zbog FX frameworks-a.
     *
     * @see Automobil
     * @see SQLAutomobilDAO#findAllAutomobil()
     */
    private final AutomobilDAO automobilDAO = new SQLAutomobilDAO();
    private final AtomicReference<ObservableList<Automobil>> automobili = new AtomicReference<>(
            FXCollections.observableArrayList(automobilDAO.findAllAutomobil()));

    /**
     * Inicijalizacija KLIJENT Objekta
     * Da bi u samom startu imali sve objekte {@link Klijent}. Koristi se {@link KlijentDAO} interfejs,
     * koji je implemetiran u {@link SQLKlijnetDAO}, a trenutno se koristi MSACCESS implementacija.
     * Sve se ubacuje u {@code ObservableList<Klijent>} zbog FX frameworks-a.
     *
     * @see Klijent
     * @see SQLKlijnetDAO#findAllKlijents()
     */
    private final KlijentDAO klijentDAO = new SQLKlijnetDAO();
    private final AtomicReference<ObservableList<Klijent>> klijenti =
            new AtomicReference<>(FXCollections.observableArrayList(klijentDAO.findAllKlijents()));
    private Klijent klijent = new Klijent();

    public void setKlijent(Klijent klijent) {
        this.klijent = klijent;
    }

    /**
     * Empty Constructor if we need this for later use
     *
     * @throws AcrenoException Zbog Exeption-sa u inicijalizaciji Objekata KLIJENT i AUTOMOBIL
     * @throws SQLException    Zbog Exeption-sa u inicijalizaciji Objekata KLIJENT i AUTOMOBIL
     */
    public AutoServisController() throws AcrenoException, SQLException {
    }


    private boolean weAreFromTable = false;
    private boolean ifWeAreFromUcitajSaobracajnu = false;


    /**
     * Empty inicijalizaciona metoda ovog {@link AutoServisController}-a, if we need this for later use
     *
     * @param url            FX staff
     * @param resourceBundle FX staff
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Platform.runLater(() -> {
            GeneralUiUtility.initSat(lblTime, DateTimeFormatter.ofPattern("HH:mm:ss"));
            GeneralUiUtility.initSat(lblDate, DateTimeFormatter.ofPattern("dd.MM.yyyy."));
            lblVerzijaAplikacije.setText(ApplicationProperties.getInstance().getProperty("app.version"));
            lblReleaseDate.setText(ApplicationProperties.getInstance().getProperty("app.date"));

            //PROVERA INTERNETA
            final ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor();
            ses.scheduleWithFixedDelay(() -> {
                if (GeneralUiUtility.netIsAvailable()) { //Imamo internet (TRUE)
                    lineInternetIndicator.setStroke(Color.rgb(36, 164, 11));//promeni boju indikatora
                } else { // Nmea Interneta (FALSE)
                    lineInternetIndicator.setStroke(Color.rgb(198, 13, 13));
                }
                System.out.println(new Date() + " = ping IMA INTERNETA !");
            }, 2, Constants.APP_UCESTALOST_PROVERE_INTERNETA, TimeUnit.SECONDS);
        });

        //Postavljenje dugmica DELETE u Tabeli POSAO ARTIKLI
        tblRowButton.setCellFactory(ActionButtonTableCell.forTableColumn("Učitaj", (Automobil p) -> {
            txtFieldRegOznaka.setText(p.getRegOznaka());
            btnOtvoriAutomobilKarticu.setDisable(false);
            btnUrediAutomobil.setDisable(false);
            popuniAutomobilTxtfOve(p); // Popuni Automobil TxtFove
            weAreFromTable = true;
            automobilForEdit = p;
            return p;
        }));
    }


    // 3.0 ***************  AUTOMOBILI STAFF ***************************

    private Automobil automobil;

    public void ucitajSaobracajnu(ActionEvent actionEvent) throws AcrenoException, SQLException {
        logger.info("Kliknuto ucitaj sobracajnu !!!");
        ifWeAreFromUcitajSaobracajnu = true; // Ako jesmo nemopj ucitavati sa Liste
        String regOznaka = Saobracajna.automobil().getRegOznaka();
        String test = regOznaka.substring(0, 2) + "-" + regOznaka.substring(2); // Sredi REG Tablicu jer nam treba XX-XXX-XX
        txtFieldRegOznaka.setText(test.toLowerCase()); // da bi radilo trebaju nam mala slova
        automobil = automobilDAO.findAutomobilByProperty(AutoSearchType.BR_TABLICE, test.toLowerCase()).get(0);
        automobilForEdit = automobil; // Da bi moglo da se uredi automobil klikom na dugme
        popuniAutomobilTxtfOve(automobil);

        // list view staff
        listViewAutmobiliSearch.setVisible(false); // Zatvori listu
        btnOtvoriAutomobilKarticu.setDisable(false); // Omoguci dugme za otvaranje Automobil kartice
        btnUrediAutomobil.setDisable(false); // Omoguci dugme za Editovanje Automobila


        //NADJI KLIJENTA i POSTAVI U txtf  txtFieldPretragaKlijenta
        klijent = klijentDAO.findKlijentByProperty(KlijentSearchType.ID_KLIJENTA,
                automobil.getIdKlijenta()).get(0);
        txtFieldPretragaKlijenta.setText(klijent.getImePrezime());
        txtFiDKlijenta.setText(String.valueOf(klijent.getIdKlijenta())); // u "txtFiDKlijenta" postavi ID klijenta
        txtFbrojTelefona.setText(klijent.getTelefonMobilni());
        txtFadresaKlijenta.setText(klijent.getUlicaBroj());
        txtFmestoStanovanjaKlijenta.setText(klijent.getMesto());
        txtFeMailKlijenta.setText(klijent.getEmail());
        txtAreaOstaliDetaljiKlijenta.setText(klijent.getOstaliDetalji());
        btnNoviAutomobil.setDisable(false);
        btnOtvoriKlijentEditMode.setDisable(false);

        //Popuni Tabelu sa automobilima u KLLIJENT KARTICI
        popuniTabeluAutomobiliklijenta(klijent);
    }

    /**
     * Pretraga i filtriranje Autmobila po REG. TABLICI u KeyListeneru TxtF-a
     * <p>
     * Prilikom kucanja u txtF pokazuju se filtrirani Automobili u ListView koji je inicijalno sakriven
     * Ukoliko ima podataka ListView se prikazuje i duplim klikom se selektuje Automobil objekat i
     * otvara se Automobil UI {@link AutomobiliController}. Ova implementacija otvaranja Automobil UIa
     * je definisana {@link #zatvoriListViewSearchAutomobil}
     * <p>
     * Deklarise se kretanje kroz {@link #listViewAutmobiliSearch} sa strelicama i ENTER za odabir Automobila.
     * Kada se odabere {@link Automobil} sa ENTEROM se posatavlja tablica u {@link #txtFieldRegOznaka}, pa
     * je implementiran i shortcut za otvaranje {@link AutomobiliController}-a KARTICE. Taj shortcut
     * je definisan u {@link Constants#OTVORI_AUTOMOBIL_KARTICU_KEYCODE} ako budemo menjali da je na jednom mestu.
     *
     * @author Dejan Cugalj
     * @see #zatvoriListViewSearchAutomobil(MouseEvent)
     * @see AutomobiliController
     */
    @FXML private void txtFieldRegTablicaSaerchKeyListener(KeyEvent keyEvent) {
        /*txtFieldRegOznaka.textProperty().addListener(observable -> {
            if (txtFieldRegOznaka.getText().isEmpty()) {
                listViewAutmobiliSearch.setItems(automobili);
                return;
            }
        });*/

        ObservableList<Automobil> auto = FXCollections.observableArrayList();
        ObservableList<Automobil> tempAutomobil = FXCollections.observableArrayList(); //Lista u koju dodajemo nadjene Auto objekte
        try {
            auto = FXCollections.observableArrayList(automobilDAO.findAllAutomobil()); //Svi Automobili

        } catch (AcrenoException | SQLException e) {
            e.printStackTrace();
        }
        for (Automobil automobil : auto) {
            String RegTablica = automobil.getRegOznaka().toLowerCase();//Trenutna tablica auta
            if (RegTablica.contains(txtFieldRegOznaka.textProperty().get())) {
                tempAutomobil.add(automobil); // Dodaje nadjeni auto u temp listu
                listViewAutmobiliSearch.setItems(tempAutomobil); // Dodaje u FXlistView
                listViewAutmobiliSearch.setCellFactory(param -> new ListCell<>() {
                    @Override
                    protected void updateItem(Automobil item, boolean empty) {
                        super.updateItem(item, empty);
                        listViewAutmobiliSearch.setVisible(true); //Prikazuje listu vidljivom
                        if (empty || item == null || item.getRegOznaka() == null) {
                            setText(null);
                            btnOtvoriAutomobilKarticu.setDisable(true);
                            btnNoviAutomobil.setDisable(true);
                            btnUrediAutomobil.setDisable(true);
                        } else {
                            setText(item.getRegOznaka());
                        }
                    }
                });
                //break;
            }
        }
        //VREDNOSTI ZA SHOUCUT SU U CONSTANTAMA (ako budemo menjali nesto da je na jednom mestu)
        KeyCombination keyCombinationCtrltC = new KeyCodeCombination(
                Constants.OTVORI_AUTOMOBIL_KARTICU_KEYCODE,
                Constants.OTVORI_AUTOMOBIL_KARTICU_KEYCOMBINATION);

        //Ako su pritisnuti Enter ili strelica dole uzmi foruks na ListView
        if (keyEvent.getCode() == KeyCode.ENTER) {
            listViewAutmobiliSearch.requestFocus(); // Uzmi foruks na ListView

        } else if (keyEvent.getCode() == KeyCode.DOWN) {
            listViewAutmobiliSearch.requestFocus(); // Uzmi foruks na ListView

        } else if (keyCombinationCtrltC.match(keyEvent)) {// Kombinacija CTRL + C -- Otvara Automobil Karticu
            if (!txtFieldRegOznaka.getText().equals("")) {  //Proveri da li je prazan TF zbog NULLEXEPTIONsa
                btnOtvoriAutomobilKarticu.fire();
            }
        }
    }

    /**
     * Zatvara popUp ListView pretrage i setuje selektovanu vrednost u TF sa DUPLIM KLIKOM
     * Nakon toga se zatvara ovj UIa i sve se inicijalizuje u {@link #openAutomobiliUi()}
     *
     * @param mouseEvent da se otvori Automobil controller i zatvori AutoServis Controler
     * @throws AcrenoException Malo bolje objasnjenje SQL Except
     * @throws SQLException    zbog {@code showAutomobiliUi()} {@link #openAutomobiliUi()}
     * @author Dejan Cugalj
     * @see #openAutomobiliUi()
     */
    @FXML private void zatvoriListViewSearchAutomobil(@NotNull MouseEvent mouseEvent) throws AcrenoException, SQLException {
        //Na dupli click vraca Radni Nalog Objekat i otvara Radni nalog Dashboard
        if (mouseEvent.getButton().equals(MouseButton.PRIMARY) && mouseEvent.getClickCount() == 2) {
            //Moze .getSelectedItems().get(0) jer ima samo jedan Automobil
            if (listViewAutmobiliSearch.getSelectionModel().getSelectedItems().size() > 0) {
                automobil = listViewAutmobiliSearch.getSelectionModel().getSelectedItems().get(0);
                popuniAutomobilTxtfOve(automobil); //POPUNI TXTFove SA PODACIUMA AUTOMOBILA

                // list view staff
                listViewAutmobiliSearch.setVisible(false); // Zatvori listu
                btnOtvoriAutomobilKarticu.setDisable(false); // Omoguci dugme za otvaranje Automobil kartice
                btnUrediAutomobil.setDisable(false); // Omoguci dugme za Editovanje Automobila

                //NADJI KLIJENTA i POSTAVI U txtf  txtFieldPretragaKlijenta
                klijent = klijentDAO.findKlijentByProperty(KlijentSearchType.ID_KLIJENTA,
                        listViewAutmobiliSearch.getSelectionModel().getSelectedItem().getIdKlijenta()).get(0);

                //Popuni TXTf sa podacima klijenta
                txtFieldPretragaKlijenta.setText(klijent.getImePrezime());
                txtFiDKlijenta.setText(String.valueOf(klijent.getIdKlijenta())); // u "txtFiDKlijenta" postavi ID klijenta
                txtFbrojTelefona.setText(klijent.getTelefonMobilni());
                txtFadresaKlijenta.setText(klijent.getUlicaBroj());
                txtFmestoStanovanjaKlijenta.setText(klijent.getMesto());
                txtFeMailKlijenta.setText(klijent.getEmail());
                txtAreaOstaliDetaljiKlijenta.setText(klijent.getOstaliDetalji());
                btnNoviAutomobil.setDisable(false);

                //Popuni Tabelu sa automobilima u KLLIJENT KARTICI
                popuniTabeluAutomobiliklijenta(klijent);

                //((Node) mouseEvent.getSource()).getScene().getWindow().hide();
                //openAutomobiliUi();
                //((Stage) ((Node) mouseEvent.getSource()).getScene().getWindow()).show();
            }
        }
    }

    /**
     * Kada se klikne na ENTER da se zatvori {@link #listViewAutmobiliSearch} lista i
     * da se postavi txt u {@link #txtFieldRegOznaka}
     *
     * @param keyEvent for get Down Up  or Enter listener
     */
    @FXML private void listViewAutmobiliSearchKeyReleased(@NotNull KeyEvent keyEvent) throws AcrenoException, SQLException {
        switch (keyEvent.getCode()) {
            case UP:
                //System.out.println("UP");
                break;
            case DOWN:
                //System.out.println("DOWN FRON LIST");
                break;
            case ENTER:
                Automobil automobil = listViewAutmobiliSearch.getSelectionModel().getSelectedItems().get(0);
                popuniAutomobilTxtfOve(automobil); // Popuni Automobil TxtFove
                txtFieldRegOznaka.requestFocus();
                btnOtvoriAutomobilKarticu.setDisable(false);
                btnUrediAutomobil.setDisable(false); // Omoguci dugme za Editovanje Automobila
                listViewAutmobiliSearch.setVisible(false);
                //NADJI KLIJENTA i POSTAVI U txtf  txtFieldPretragaKlijenta
                klijent = klijentDAO.findKlijentByProperty(KlijentSearchType.ID_KLIJENTA,
                        listViewAutmobiliSearch.getSelectionModel().getSelectedItem().getIdKlijenta()).get(0);
                txtFieldPretragaKlijenta.setText(klijent.getImePrezime());
                btnNoviAutomobil.setDisable(false);

                //Popuni Tabelu sa automobilima u KLLIJENT KARTICI
                popuniTabeluAutomobiliklijenta(klijent);
                break;
            default:
                break;
        }
    }

    /**
     * Metoda koja otvara {@link AutomobiliController} UI i prosledjuje CONTROLLER i STAGE u {@link AutomobiliController}.
     * <p>
     * Prosledjuje {@link Automobil} objekat u {@link AutomobiliController} filtriran po REG. TABLICI.
     * Automobil se vraca {@code ObservableList<Automobil> automobili} i sada je samo jedan Automobil u listi.
     * <p>
     * Takodje, posto imamo samo jednog {@link Klijent}-a vezanog za Automobil, prosledjujemo isto i ID Klijenta
     * u  {@link AutomobiliController}.  Cinjenica da imamo samo jednog Klijenta
     * moze {@code automobili.get(0).getIdKlijenta()}. Filtriran Klijent Objekat
     * se vraca u {@code AtomicReference<ObservableList<Klijent>> klijenti}.
     *
     * @throws AcrenoException Malo bolji pregled greske
     * @throws SQLException    SQL MSACCESS greska prilikom dobijanja podataka iz MS baze
     * @author Dejan Cugalj
     * @see #txtFieldRegTablicaSaerchKeyListener
     * @see #zatvoriListViewSearchAutomobil
     * @see AutomobiliController
     */
    private void openAutomobiliUi() throws AcrenoException, SQLException {
        try {
            // Standart FX load UI
            FXMLLoader fxmlLoaderAutomobil = new FXMLLoader(getClass().getResource(Constants.AUTOMOBILI_UI_VIEW_URI));
            Stage stageAutomobil = new Stage();
            stageAutomobil.getIcons().add(new Image(AutoServisController.class.getResourceAsStream(Constants.APP_AUTOMOBIL_ICON)));
            stageAutomobil.initModality(Modality.APPLICATION_MODAL);

            stageAutomobil.setResizable(false);
            Scene scene = new Scene(fxmlLoaderAutomobil.load());
            stageAutomobil.setScene(scene);
            stageAutomobil.setTitle("Automobil: " + txtFieldRegOznaka.getText());

            stageAutomobil.setOnCloseRequest(windowEvent -> {
                // txtFieldImeKlijenta.setText(klijenti.get(0).getImePrezime());// Moze jer je samo jedan Klijent
                System.out.println("FORM BUUTTON openAutomobiliUi --- AUTOMOBILI_UI_VIEW_URI");
            });

            //Set AutoServisController u AutomobiliController UI
            AutomobiliController automobiliController = fxmlLoaderAutomobil.getController();
            automobiliController.setAutoServisController(this, stageAutomobil);

            //Prosledjivanje filtriranog AUTOMOBILA po REG tablici u ObservableList "automobil"
            String regOznaka = txtFieldRegOznaka.getText(); //Uzmi reg tablicu kao filter parametar za auto
            automobili.set(FXCollections.observableArrayList(
                    automobilDAO.findAutomobilByProperty(AutoSearchType.BR_TABLICE, regOznaka)));
            automobiliController.setAutomobil(automobili.get()); // Prosledjivanje "Automobil" Obj u Automobil Kontroler

            //Prosledjivanje filtriranog KLIJENTA po ID AUTOMOBILA u ObservableList "klijenti"
            int idKlijenta = automobili.get().get(0).getIdKlijenta();//Moze automobili.get(0), jer imamo samo jednog Klijenta.
            klijenti.set(FXCollections.observableArrayList(
                    klijentDAO.findKlijentByProperty(KlijentSearchType.ID_KLIJENTA, idKlijenta)
            ));
            automobiliController.setKlijenti(klijenti.get()); // Prosledjivanje "Klijent" Obj u Automobil Kontroler

            stageAutomobil.showAndWait(); // Prikazi i cekaj Automobil UI

        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    /**
     * Otvaranje {@link AddEditAutomobilController} UI-a i zatvaranje {@link AutomobiliController} UI-a
     * Ceo proces inicijalizacije je u {@link #openAutomobiliUi()}
     *
     * @param actionEvent if we need in some case
     * @throws AcrenoException malo bolje objasnjenje exception-sa
     * @throws SQLException    SQL ex, nesto nije uredu sa bazom
     * @author Dejan Cugalj
     * @see #openAutomobiliUi()
     * @see AddEditAutomobilController
     * @see AutomobiliController
     */
    @FXML private void btnOtvoriAutomobilKarticu(@NotNull ActionEvent actionEvent) throws AcrenoException, SQLException {
        // ((Node) actionEvent.getSource()).getScene().getWindow().hide();
        openAutomobiliUi();
        // ((Stage) ((Node) actionEvent.getSource()).getScene().getWindow()).show();
    }

    /**
     * Otvaranje {@link AddEditAutomobilController}-a UI-a za INSERT OR EDIT {@link Automobil}-a
     * <p>
     * Posale FXMLs staffa, inicijalizujemo {@link AddEditAutomobilController} i
     * prosledjujemo {@link AddEditAutomobilController#setWeAreInEditMode(boolean)}da smo u INSERT MODU
     * jer je direktno kliknuto na btnNoviAutomobil.
     *
     * @throws IOException not found {@link Constants#CREATE_KLIJENT_UI_VIEW_URI}
     * @autor Dejan Cugalj
     * @see AddEditAutomobilController
     * @see AddEditAutomobilController#setWeAreInEditMode(boolean)
     * @see Automobil auto
     * @see Constants#CREATE_EDIT_AUTOMOBIL_UI_VIEW_URI
     */
    @FXML private void btnOpenNoviAutomobilGui() throws IOException {
        // Standart FX load UI

        FXMLLoader fxmlLoaderNewAutomobil = new FXMLLoader(getClass().getResource(Constants.CREATE_EDIT_AUTOMOBIL_UI_VIEW_URI));
        Stage stageNewAutomobil = new Stage();
        stageNewAutomobil.getIcons().add(new Image(AutoServisController.class.getResourceAsStream(Constants.APP_AUTOMOBIL_ICON)));
        stageNewAutomobil.initModality(Modality.APPLICATION_MODAL);
        stageNewAutomobil.initStyle(StageStyle.UNDECORATED);
        Scene scene = new Scene(fxmlLoaderNewAutomobil.load());
        stageNewAutomobil.setScene(scene);
        stageNewAutomobil.setResizable(false);
        stageNewAutomobil.setTitle("Kreiraj Novi Autmobil");

        //Set AutoServisController u AutomobiliController UI
        AddEditAutomobilController addEditAutomobilController = fxmlLoaderNewAutomobil.getController();
        addEditAutomobilController.setAutoServisController(this, stageNewAutomobil);
        addEditAutomobilController.setWeAreInEditMode(false); // NISMO U EDITu kliknuto diretno na dugme Novi Automobil
        addEditAutomobilController.setKlijent(klijent); //Prosledi Klijent Obj


        //Kada se zatvori "CREATE_EDIT_AUTOMOBIL_UI_VIEW_URI" da uradimo neke stvari ovde
        stageNewAutomobil.setOnCloseRequest(windowEvent -> {
            logger.debug("stageNewAutomobil --> setOnCloseRequest");
            popuniTabeluAutomobiliklijenta(klijent);
            Automobil tempAuto = addEditAutomobilController.getAutomobil();
            popuniAutomobilTxtfOve(tempAuto);
            btnOtvoriAutomobilKarticu.setDisable(false);
            btnUrediAutomobil.setDisable(false);
            //Ako se brise auto da se isprazne polja u automobil sekciji...Dobijamo podatak iz AddEdit kontrolora da
            //je stisnuto dume "Brisi Automobil"
            if (addEditAutomobilController.isDeleteButtonPressed()) {
                System.out.println(addEditAutomobilController.isDeleteButtonPressed() + " ...YES");
                txtFidAutomobila.setText("");
                txtFieldRegOznaka.setText("");
                txtfVinAutomobila.setText("");
                txtfGodisteAutomobila.setText("");
                txtfMarkaAutomobila.setText("");
                txtfModelAutomobila.setText("");
                txtfGoriivoAutomobila.setText("");
                btnOtvoriAutomobilKarticu.setDisable(true);
                btnUrediAutomobil.setDisable(true);
            }
        });
        stageNewAutomobil.showAndWait();
    }


    /**
     * Popunjavanje {@link Automobil} TXTfova, izdvojena metoda jer nam nam treba na -
     * vise mesta: {@link #listViewAutmobiliSearchKeyReleased(KeyEvent)}, i u {@link #zatvoriListViewSearchAutomobil(MouseEvent)}
     *
     * @param automobil {@link Automobil}
     */
    private void popuniAutomobilTxtfOve(@NotNull Automobil automobil) {

        txtFidAutomobila.setText(String.valueOf(automobil.getIdAuta()));
        txtFieldRegOznaka.setText(automobil.getRegOznaka()); // Postavi REG. OZNAKU u TF
        txtfVinAutomobila.setText(automobil.getVinVozila());
        txtfModelAutomobila.setText(automobil.getModelVozila());
        txtfMarkaAutomobila.setText(automobil.getMarkaVozila());
        txtfGoriivoAutomobila.setText(automobil.getVrstaGorivaVozila());
        txtfGodisteAutomobila.setText(String.valueOf(automobil.getGodisteVozila()));
    }


    // 3.1 *************** EDIT AUTOMOBILI STAFF

    /**
     * Kada se klikne na {@link #txtFieldRegOznaka} da se zatvori {@link #listViewKlijentiSearch} jer ostaje otvoren.
     * i cisti text iz {@link #txtFieldRegOznaka}
     */
    public void txtFieldRegTablicaSaerchhOnMouseClick() {
        listViewKlijentiSearch.setVisible(false);
    }

    private Automobil automobilForEdit;

    /**
     * Metoda koja se koristi za editovanje {@link Automobil} objekta, a dugme {@link #btnUrediAutomobil)}.
     * <p>
     * {@code listViewAutmobiliSearch.getSelectionModel().getSelectedItem()} se korristi za inicijalizaciju
     * {@link Automobil} objekta koji se dobija selekcijom iz {@link #listViewAutmobiliSearch}.
     * <p>
     * Pristup {@link AddEditAutomobilController}-u i prosledjivanje {@link Automobil} dobijenog objekta.
     *
     * @throws IOException ULI Locatio is not fonu in {@link Constants#CREATE_EDIT_AUTOMOBIL_UI_VIEW_URI}
     * @bitno {@code listViewAutmobiliSearch.getSelectionModel().getSelectedItem()}
     * @see #btnUrediAutomobil
     * @see Automobil
     * @see AddEditAutomobilController
     */
    @FXML private void btnUrediAutomobilAct() throws IOException {
        // Moze jer je samo jed izabran u "listViewAutmobiliSearch"
        //  Ako je kliknuto iz tabele auto, nemoj selektovati iz ListViewa i ako je ucitana saobracajna nemoj ovo.
        if (!weAreFromTable && !ifWeAreFromUcitajSaobracajnu) {
            automobilForEdit = listViewAutmobiliSearch.getSelectionModel().getSelectedItem();
        }

        // Standart FX load UI
        FXMLLoader fxmlLoaderNewAutomobil = new FXMLLoader(getClass().getResource(Constants.CREATE_EDIT_AUTOMOBIL_UI_VIEW_URI));
        Stage stageNewAutomobil = new Stage();
        stageNewAutomobil.getIcons().add(new Image(AutoServisController.class.getResourceAsStream(Constants.APP_AUTOMOBIL_ICON)));
        stageNewAutomobil.initModality(Modality.APPLICATION_MODAL);
        if (automobilForEdit.getRegOznaka().isEmpty()) {
            automobilForEdit.setRegOznaka("");
        } else {
            stageNewAutomobil.setTitle("Izmena Autmobila: " + automobilForEdit.getRegOznaka());
        }
        //Inicijalizacija CREATE NEW KLIJENT Controllora-a
        Scene scene = new Scene(fxmlLoaderNewAutomobil.load());
        stageNewAutomobil.setScene(scene);
        stageNewAutomobil.setResizable(false);
        //Set AutoServisController u AutomobiliController UI
        AddEditAutomobilController addEditAutomobilController = fxmlLoaderNewAutomobil.getController();
        addEditAutomobilController.setAutoServisController(this, stageNewAutomobil);
        addEditAutomobilController.setWeAreInEditMode(true); // NISMO U EDITu kliknuto diretno na dugme Novi Automobil
        addEditAutomobilController.setAutomobil(automobilForEdit); //Prosledi Automobil Obj
        addEditAutomobilController.setKlijent(klijent); //Prosledi Klijent Obj
        addEditAutomobilController.setLblHeaderTitle("IZMENA AUTOMOBILA:");


        stageNewAutomobil.setOnCloseRequest(windowEvent -> {
            // txtFieldImeKlijenta.setText(klijenti.get(0).getImePrezime());// Moze jer je samo jedan Klijent
            System.out.println("FORM BUUTTON btnUrediAutomobilAct --- CREATE_EDIT_AUTOMOBIL_UI_VIEW_URI");
            popuniAutomobilTxtfOve(automobilForEdit);
            popuniTabeluAutomobiliklijenta(klijent);
            btnOtvoriAutomobilKarticu.setDisable(false);
            btnUrediAutomobil.setDisable(false);
            //Ako se brise auto da se isprazne polja u automobil sekciji...Dobijamo podatak iz AddEdit kontrolora da
            //je stisnuto dume "Brisi Automobil"
            if (addEditAutomobilController.isDeleteButtonPressed()) {
                System.out.println(addEditAutomobilController.isDeleteButtonPressed() + " ...YES");
                txtFidAutomobila.setText("");
                txtFieldRegOznaka.setText("");
                txtfVinAutomobila.setText("");
                txtfGodisteAutomobila.setText("");
                txtfMarkaAutomobila.setText("");
                txtfModelAutomobila.setText("");
                txtfGoriivoAutomobila.setText("");
                btnOtvoriAutomobilKarticu.setDisable(true);
                btnUrediAutomobil.setDisable(true);
            }
        });
        stageNewAutomobil.showAndWait();
    }


    // 4.0 ***************  KLIJENTI STAFF ***************************

    //Otvori GUI za citanje Licne Karte
    @FXML public void ucitajLicnuKartu(ActionEvent actionEvent) {
        LicnaKarta.main(null); //Otvori GUI za citanje Licne Karte
        //btnOtvoriCitacLicneKarte.setDisable(true);
        btnUcitajPodatkeSaLicneKarte.setDisable(false);
    }

    public static Klijent klijentStatic; // Potrebno jer prosledjujemo Klijenta iz Klase SAOBRACAJNA

    //TODO moze ovo bolje
    public static void ucitajLicnuKartu(@NotNull Klijent klijent) {
        logger.info("CITANJE LICNE KARTE");
        klijentStatic = klijent; //Postavljanje staticne metode...za sada tako
    }

    @FXML private void popunjavanjePoljaSaLicneKarte() throws AcrenoException, SQLException {
        //txtFiDKlijenta.setText(String.valueOf(klijentStatic.getIdKlijenta()));
        // list view staff
        listViewAutmobiliSearch.setVisible(false); // Zatvori listu
        btnOtvoriAutomobilKarticu.setDisable(true); // Omoguci dugme za otvaranje Automobil kartice
        btnUrediAutomobil.setDisable(true); // Omoguci dugme za Editovanje Automobila

        txtFidAutomobila.setText("");
        txtFieldRegOznaka.setText("");
        txtfVinAutomobila.setText("");
        txtfGodisteAutomobila.setText("");
        txtfMarkaAutomobila.setText("");
        txtfModelAutomobila.setText("");
        txtfGoriivoAutomobila.setText("");


        //NADJI KLIJENTA PO JMBG i POSTAVI U txtf  txtFieldPretragaKlijenta
        String maticniBroj = klijentStatic.getMaticniBroj();
        //NADJI KLIJENTA i POSTAVI U txtf  txtFieldPretragaKlijenta
        klijent = klijentDAO.findKlijentByProperty(KlijentSearchType.MATICNI_BROJ, maticniBroj).get(0);
        System.out.println("DATUM PRVE ACR REGISTRACIJE: " + klijent.getDatumAcrRegistracijeKliljenta());
        txtFiDKlijenta.setText(String.valueOf(klijent.getIdKlijenta())); // u "txtFiDKlijenta" postavi ID klijenta
        txtFieldPretragaKlijenta.setText(klijent.getImePrezime());
        txtFbrojTelefona.setText(klijent.getTelefonMobilni());
        txtFadresaKlijenta.setText(klijent.getUlicaBroj());
        txtFmestoStanovanjaKlijenta.setText(klijent.getMesto());
        txtFeMailKlijenta.setText(klijent.getEmail());
        txtAreaOstaliDetaljiKlijenta.setText(klijent.getOstaliDetalji());
        btnNoviAutomobil.setDisable(false);
        btnOtvoriKlijentEditMode.setDisable(false);
        btnOtvoriCitacLicneKarte.setDisable(false);
        btnUcitajPodatkeSaLicneKarte.setDisable(true);
        popuniTabeluAutomobiliklijenta(klijent);
    }

    /**
     * Pretraga i filtriranje Klijenta po IMENU I PREZIMENU u KeyListeneru TxtF-a
     * <p>
     * Prilikom kucanja u txtF pokazuju se filtrirani Klienti u ListView koji je inicijalno sakriven
     * Ukoliko ima podataka ListView se prikazuje i duplim klikom se selektuje Klijent objekat i
     * otvara se {@link CreateNewKlijentUiController} Klijent UI. Ova implementacija otvaranja klijent UIa
     * je definisana {@link #zatvoriLvKlijent(MouseEvent)}
     * <p>
     * Deklarise se kretanje kroz {@link #listViewKlijentiSearch} sa strelicama i ENTER za odabir Klijenta.
     * Kada se odabere {@link Klijent} sa ENTEROM se posatavlja tablica u {@link #txtFieldPretragaKlijenta}, pa
     * je implementiran i shortcut za otvaranje {@link CreateNewKlijentUiController}-a KARTICE. Taj shortcut
     * je definisan u {@link Constants#OTVORI_KLIJENT_KARTICU_KEYCODE} ako budemo menjali da je na jednom mestu.
     *
     * @author Dejan Cugalj
     * @see #zatvoriLvKlijent(MouseEvent)
     * @see CreateNewKlijentUiController controlle sad
     */
    @FXML private void txtfKlijentSaerchKeyListener(KeyEvent keyEvent) {
       /* txtFieldPretragaKlijenta.textProperty().addListener(observable -> {
            if (txtFieldPretragaKlijenta.textProperty().get().isEmpty()) {
                //listViewKlijentiSearch.setItems(klijenti.get());
                listViewKlijentiSearch.setVisible(false);
            }
        });*/

        ObservableList<Klijent> klijenti = FXCollections.observableArrayList();
        ObservableList<Klijent> tempKlijent = FXCollections.observableArrayList();
        try {
            klijenti = FXCollections.observableArrayList(klijentDAO.findAllKlijents()); //Svi Klijenti

        } catch (AcrenoException | SQLException e) {
            e.printStackTrace();
        }
        for (Klijent klijent : klijenti) {
            String ImePrezime = klijent.getImePrezime().toLowerCase();//Trenutno IME PREZIME KLIJENTAs
            if (ImePrezime.contains(txtFieldPretragaKlijenta.textProperty().get())) {
                tempKlijent.add(klijent); // Dodaje nadjenog klijenta u temp listu
                listViewKlijentiSearch.setItems(tempKlijent); // Dodaje u FXlistView
                listViewKlijentiSearch.setCellFactory(param -> new ListCell<>() {
                    @Override
                    protected void updateItem(Klijent item, boolean empty) {
                        super.updateItem(item, empty);
                        listViewKlijentiSearch.setVisible(true); //Prikazuje listu vidljivom
                        if (empty || item == null || item.getImePrezime() == null) {
                            setText(null);
                            btnOtvoriAutomobilKarticu.setDisable(true);
                            btnNoviAutomobil.setDisable(true);
                            btnUrediAutomobil.setDisable(true);
                        } else {
                            setText(item.getImePrezime());

                        }
                    }
                });
                //break;
            }
        }

        //Ako su pritisnuti Enter ili strelica dole uzmi foruks na ListView
        if (keyEvent.getCode() == KeyCode.ENTER) {
            listViewKlijentiSearch.requestFocus(); // Uzmi foruks na ListView

        } else if (keyEvent.getCode() == KeyCode.DOWN) {
            listViewKlijentiSearch.requestFocus(); // Uzmi foruks na ListView

            //VREDNOSTI ZA SHOUCUT SU U CONSTANTAMA (ako budemo menjali nesto da je na jednom mestu)
         /*   KeyCombination keyCombinationCtrltF = new KeyCodeCombination(
                    Constants.OTVORI_KLIJENT_KARTICU_KEYCODE,
                    Constants.OTVORI_KLIJENTL_KARTICU_KEYCOMBINATION);*/

        }/* else if (keyCombinationCtrltF.match(keyEvent)) {// Kombinacija CTRL + F -- Otvara KLIJENTA EDIT UIa
            if (!txtFieldPretragaKlijenta.getText().equals("")) {  //Proveri da li je prazan TF zbog NULLEXEPTIONsa
                btnOtvoriKlijentEditMode.fire();

            }
        }*/
    }

    /**
     * Zatvara popUp ListView pretrage i setuje selektovanu vrednost u TF sa DUPLIM KLIKOM
     * Nakon toga se zatvara ovj UIa i sve se inicijalizuje u {@link #openAddEditklijent()}
     * <p>
     * Inicijalizuje se {@link Klijent} objekat sa odabranom vrednoscu iz {@link #listViewKlijentiSearch}
     *
     * @param mouseEvent da se otvori {@link CreateNewKlijentUiController}
     * @author Dejan Cugalj
     * @see #openAutomobiliUi()
     */
    @FXML private void zatvoriLvKlijent(@NotNull MouseEvent mouseEvent) {
        //Na dupli click vraca Radni Nalog Objekat i otvara Radni nalog Dashboard
        if (mouseEvent.getButton().equals(MouseButton.PRIMARY) && mouseEvent.getClickCount() == 2) {
            if (listViewKlijentiSearch.getSelectionModel().getSelectedItems().size() > 0) {
                //Moze .getSelectedItems().get(0) jer ima samo jedan Automobil
                String imePrezimeKlijenta = listViewKlijentiSearch.getSelectionModel().getSelectedItems().get(0).getImePrezime();
                String brojTelefonaKlijenta = listViewKlijentiSearch.getSelectionModel().getSelectedItems().get(0).getTelefonMobilni();
                int idKlijenta = listViewKlijentiSearch.getSelectionModel().getSelectedItems().get(0).getIdKlijenta();


                //Napravi Klijent Objekat iz odabrane LISTVIEW stavke
                klijent = listViewKlijentiSearch.getSelectionModel().getSelectedItems().get(0);

                txtFieldPretragaKlijenta.setText(imePrezimeKlijenta); // Postavi IME I PREZIME u TF
                txtFiDKlijenta.setText(String.valueOf(idKlijenta)); // u "txtFiDKlijenta" postavi ID klijenta
                txtFbrojTelefona.setText(brojTelefonaKlijenta);
                txtFadresaKlijenta.setText(klijent.getUlicaBroj());
                txtFmestoStanovanjaKlijenta.setText(klijent.getMesto());
                txtFeMailKlijenta.setText(klijent.getEmail());
                txtAreaOstaliDetaljiKlijenta.setText(klijent.getOstaliDetalji());
                btnNoviAutomobil.setDisable(false);
                listViewKlijentiSearch.setVisible(false); // Zatvori listu
                popuniTabeluAutomobiliklijenta(klijent);
                btnOtvoriKlijentEditMode.setDisable(false);
                //((Node) mouseEvent.getSource()).getScene().getWindow().hide();
                //openAddEditklijent();
                // ((Stage) ((Node) mouseEvent.getSource()).getScene().getWindow()).show();
            }
        }
    }

    /**
     * Kada se klikne na ENTER da se zatvori {@link #listViewKlijentiSearch} lista i
     * da se postavi txt u {@link #txtFieldPretragaKlijenta}
     * Takodje se inicijalizuje {@link Klijent} objekat sa odabranom vrednoscu iz {@link #listViewKlijentiSearch}
     *
     * @param keyEvent for get Down Up  or Enter listener
     * @autor Dejan Cugalj
     * @see #listViewKlijentiSearch
     * @see Klijent
     */
    @FXML private void listViewKlijentiSearchKeyReleased(@NotNull KeyEvent keyEvent) {
        switch (keyEvent.getCode()) {
            case UP:
                //System.out.println("UP");
                break;
            case DOWN:
                //System.out.println("DOWN FRON LIST");
                break;
            case ENTER:
                String imePrezimeklijenta = listViewKlijentiSearch.getSelectionModel().getSelectedItem().getImePrezime();
                txtFieldPretragaKlijenta.setText(imePrezimeklijenta);
                txtFieldPretragaKlijenta.requestFocus();

                //Napravi Klijent Objekat iz odabrane LISTVIEW stavke
                klijent = listViewKlijentiSearch.getSelectionModel().getSelectedItems().get(0);
                btnNoviAutomobil.setDisable(false);
                listViewKlijentiSearch.setVisible(false);
                popuniTabeluAutomobiliklijenta(klijent);
                btnOtvoriKlijentEditMode.setDisable(false);
                break;
            default:
                break;
        }
    }

    /**
     * Otvaranje {@link AddEditAutomobilController} UIa i uzimanje instance {@link CreateNewKlijentUiController}.
     * {@link AddEditAutomobilController}u se prosledjuje {@link #klijent} Objekat koji je inicijalizovan
     * u {@link #zatvoriLvKlijent(MouseEvent)}
     *
     * @throws IOException not foud in #{@link Constants#CREATE_KLIJENT_UI_VIEW_URI}
     * @autor Dejan Cugalj
     * @see #zatvoriLvKlijent(MouseEvent)
     * @see #klijent
     * @see CreateNewKlijentUiController
     */
    @FXML private void openAddEditklijent() throws IOException {

       /* if (ifWeAreFromCitacLicneKarte == true) {
            //createNewKlijentUiController.setKlijent(klijentStatic); // prosledi Klijenta u EDIT KLIIJENT CONTROLLER
           // klijent = klijentStatic;

        }
        if (ifWeAreFromCitacLicneKarte == false) {
            //createNewKlijentUiController.setKlijent(klijent); // prosledi Klijenta u EDIT KLIIJENT CONTROLLER
            System.out.println("nooooo" + ifWeAreFromCitacLicneKarte);
        }*/
        // Standart FX load UI
        if (klijent.getImePrezime() != null) {
            FXMLLoader fxmlLoaderKlijent = new FXMLLoader(getClass().getResource(Constants.CREATE_KLIJENT_UI_VIEW_URI));
            Stage stageKljent = new Stage();
            stageKljent.getIcons().add(new Image(AutoServisController.class.getResourceAsStream(Constants.APP_CLIENTS_ICON)));
            stageKljent.initModality(Modality.APPLICATION_MODAL);
            Scene scene = new Scene(fxmlLoaderKlijent.load());
            stageKljent.setScene(scene);
            stageKljent.setResizable(false);
            stageKljent.setTitle("Klijent: " + txtFieldPretragaKlijenta.getText());

            //Set AutoServisController u CREATE NEW KLIJENT CONTROLORU  UI
            CreateNewKlijentUiController createNewKlijentUiController = fxmlLoaderKlijent.getController();
            createNewKlijentUiController.setAutoServisController(this, stageKljent);
            createNewKlijentUiController.setWeAreInEditMode(true);

            createNewKlijentUiController.setKlijent(klijent); // prosledi Klijenta u EDIT KLIIJENT CONTROLLER

            stageKljent.setOnCloseRequest(windowEvent -> { // Kada se yatvori EDIT KLIIJENT CONTROLLER, ovo se desava
                logger.debug("stageKljent --> setOnCloseRequest: " + createNewKlijentUiController.isDeleteButtonPressed());
                //Ako je Klijent ID =0 to znaci da nismo sacuvali ili uneli Klijenta pa dugme novi automobil mora biti DISABLE
                if (klijent.getIdKlijenta() == 0) {
                    btnNoviAutomobil.setDisable(true);
                    btnOtvoriKlijentEditMode.setDisable(true);

                }
                if (klijent.getIdKlijenta() != 0) {
                    txtFiDKlijenta.setText(String.valueOf(klijent.getIdKlijenta())); // u "txtFiDKlijenta" postavi ID klijenta
                    txtFieldPretragaKlijenta.setText(klijent.getImePrezime());
                    txtFbrojTelefona.setText(klijent.getTelefonMobilni());
                    txtFadresaKlijenta.setText(klijent.getUlicaBroj());
                    txtFmestoStanovanjaKlijenta.setText(klijent.getMesto());
                    txtFeMailKlijenta.setText(klijent.getEmail());
                    txtAreaOstaliDetaljiKlijenta.setText(klijent.getOstaliDetalji());
                    //txtFieldRegOznaka.setText("");
                    txtFieldRegOznaka.requestFocus();
                    btnNoviAutomobil.setDisable(false);
                    btnOtvoriKlijentEditMode.setDisable(false);
                }
                if (createNewKlijentUiController.isDeleteButtonPressed()) {
                    btnNoviAutomobil.setDisable(true);
                    btnOtvoriAutomobilKarticu.setDisable(true);
                    btnUrediAutomobil.setDisable(true);
                    btnOtvoriKlijentEditMode.setDisable(true);
                    txtFieldRegOznaka.setText("");
                    txtFieldPretragaKlijenta.setText("");
                    //Popunjavanje Klijent polja posle odabira sa praznim poljima jer je obrisan
                    txtFiDKlijenta.setText(""); // u "txtFiDKlijenta" postavi ID klijenta
                    txtFbrojTelefona.setText("");
                    txtFadresaKlijenta.setText("");
                    txtFmestoStanovanjaKlijenta.setText("");
                    txtFeMailKlijenta.setText("");
                    txtAreaOstaliDetaljiKlijenta.setText("");
                    tblAutomobiliInKlijent.getItems().clear();
                    tblAutomobiliInKlijent.refresh();
                }
            });
            stageKljent.showAndWait();

        } else {
            GeneralUiUtility.alertDialogBox(Alert.AlertType.ERROR, "Nije izabran nijedan Klijent !"
                    , "Greška !"
                    , "Da bi ste ušli u Edit Mod Klijenta, morate izabrati nekog !");
        }
    }

    /**
     * Otvaranje {@link CreateNewKlijentUiController}-a UI-a za INSERT {@link Klijent}-a
     *
     * @throws IOException not found {@link Constants#CREATE_KLIJENT_UI_VIEW_URI}
     * @autor Dejan Cugalj
     * @see CreateNewKlijentUiController
     * @see Klijent
     * @see Constants#CREATE_KLIJENT_UI_VIEW_URI
     */
    @FXML private void btnOpenNoviKlijentGui() throws IOException {
        // Standart FX load UI
        FXMLLoader fxmlLoaderNewKlijent = new FXMLLoader(getClass().getResource(Constants.CREATE_KLIJENT_UI_VIEW_URI));
        Stage stageNewKlijent = new Stage();
        stageNewKlijent.getIcons().add(new Image(AutoServisController.class.getResourceAsStream(Constants.APP_CLIENTS_ICON)));
        stageNewKlijent.initModality(Modality.APPLICATION_MODAL);
        stageNewKlijent.initStyle(StageStyle.UNDECORATED);
        stageNewKlijent.setResizable(false);
        Scene scene = new Scene(fxmlLoaderNewKlijent.load());
        stageNewKlijent.setScene(scene);
        stageNewKlijent.setResizable(false);
        stageNewKlijent.setTitle("Kreiranje Novog Klijenta");

        //Set AutoServisController u "ARTIKLI_UI_VIEW_URI"  UI
        CreateNewKlijentUiController createNewKlijentUiController = fxmlLoaderNewKlijent.getController();
        createNewKlijentUiController.setAutoServisController(this, stageNewKlijent);


        //Kada se zatvori "CREATE_EDIT_KLIJENT" da uradimo neke stvari ovde
        stageNewKlijent.setOnCloseRequest(windowEvent -> {
            logger.debug("stageNewKlijent --> setOnCloseRequest: " + createNewKlijentUiController.isDeleteButtonPressed());

            //Ako je Klijent ID =0 to znaci da nismo sacuvali ili uneli Klijenta pa dugme novi automobil mora biti DISABLE
            if (klijent.getIdKlijenta() == 0) {
                btnNoviAutomobil.setDisable(true);
                btnOtvoriKlijentEditMode.setDisable(true);

            }
            if (klijent.getIdKlijenta() != 0) {
                txtFieldPretragaKlijenta.setText(klijent.getImePrezime());
                txtFiDKlijenta.setText(String.valueOf(klijent.getIdKlijenta())); // u "txtFiDKlijenta" postavi ID klijenta
                txtFbrojTelefona.setText(klijent.getTelefonMobilni());
                txtFadresaKlijenta.setText(klijent.getUlicaBroj());
                txtFmestoStanovanjaKlijenta.setText(klijent.getMesto());
                txtFeMailKlijenta.setText(klijent.getEmail());
                txtAreaOstaliDetaljiKlijenta.setText(klijent.getOstaliDetalji());
                txtFieldRegOznaka.setText("");
                txtFieldRegOznaka.requestFocus();
                btnNoviAutomobil.setDisable(false);
                btnOtvoriKlijentEditMode.setDisable(false);
            }
            if (createNewKlijentUiController.isDeleteButtonPressed()) {
                btnNoviAutomobil.setDisable(true);
                btnOtvoriAutomobilKarticu.setDisable(true);
                btnUrediAutomobil.setDisable(true);
                btnOtvoriKlijentEditMode.setDisable(true);
                txtFieldRegOznaka.setText("");
                txtFieldPretragaKlijenta.setText("");
                //Popunjavanje Klijent polja posle odabira sa praznim poljima jer je obrisan
                txtFiDKlijenta.setText(""); // u "txtFiDKlijenta" postavi ID klijenta
                txtFbrojTelefona.setText("");
                txtFadresaKlijenta.setText("");
                txtFmestoStanovanjaKlijenta.setText("");
                txtFeMailKlijenta.setText("");
                txtAreaOstaliDetaljiKlijenta.setText("");
                tblAutomobiliInKlijent.getItems().clear();
                tblAutomobiliInKlijent.refresh();
            }


            //btnOtvoriKlijentEditMode.setDisable(true);
            listViewKlijentiSearch.setVisible(false);

        });
        stageNewKlijent.showAndWait();
    }

    /**
     * Kada se klikne na {@link #txtFieldPretragaKlijenta} da se zatvori {@link #listViewAutmobiliSearch} jer ostaje otvoren.
     * i cisti text iz {@link #txtFieldRegOznaka}(ne sada)
     */
    @FXML private void txtfKlijentSearchOnMouseClick() {
        //txtFieldRegOznaka.setText("");
        listViewAutmobiliSearch.setVisible(false);
    }

    // 4.1 **************** TABELA SA AUTOMOBILIMA IZABRANOG KLIJENTA (AKO NAM ZATREBA)
    private void popuniTabeluAutomobiliklijenta(@NotNull Klijent klijent) {
        logger.debug("ID KLIJENTA: " + klijent.getIdKlijenta()
                + " || Ime i Prezime Klijenta: " + klijent.getImePrezime());

        try {
            ObservableList<Automobil> automobili = FXCollections.observableArrayList(
                    automobilDAO.findAutomobilByProperty(AutoSearchType.KLIJNET_ID, klijent.getIdKlijenta()));

            //ID AUTA
            tblColIDAutomobila.setCellValueFactory(cellData ->
                    new SimpleIntegerProperty(cellData.getValue().getIdAuta()));
            tblColIDAutomobila.setStyle("-fx-alignment: CENTER;");
            //ID KLIJENTA
            tblColIDklijenta.setCellValueFactory(cellData ->
                    new SimpleIntegerProperty(cellData.getValue().getIdKlijenta()));
            tblColIDklijenta.setStyle("-fx-alignment: CENTER;");
            //REG OZNAKA
            tblColRegOznaka.setCellValueFactory(cellData ->
                    new SimpleStringProperty(cellData.getValue().getRegOznaka()));
            tblColRegOznaka.setStyle("-fx-alignment: CENTER;");
            //MARKA VOZILA
            tblColMarka.setCellValueFactory(cellData ->
                    new SimpleStringProperty(cellData.getValue().getMarkaVozila()));
            tblColMarka.setStyle("-fx-alignment: CENTER;");
            //MODEL VOZILA
            tblColModel.setCellValueFactory(cellData ->
                    new SimpleStringProperty(cellData.getValue().getModelVozila()));
            tblColModel.setStyle("-fx-alignment: CENTER;");
            //GODISTE VOZILA
            tblColGodiste.setCellValueFactory(cellData ->
                    new SimpleIntegerProperty(cellData.getValue().getGodisteVozila()));
            tblColGodiste.setStyle("-fx-alignment: CENTER;");
            //VRSTA GORIVA VOZILA
            tblColVrstaGoriva.setCellValueFactory(cellData ->
                    new SimpleStringProperty(cellData.getValue().getVrstaGorivaVozila()));
            tblColVrstaGoriva.setStyle("-fx-alignment: CENTER;");
            //VIN VOZILA
            tblColVibAutomobila.setCellValueFactory(cellData ->
                    new SimpleStringProperty(cellData.getValue().getVinVozila()));
            tblColVibAutomobila.setStyle("-fx-alignment: CENTER;");

            tblAutomobiliInKlijent.setItems(automobili);
        } catch (AcrenoException | SQLException e) {
            e.printStackTrace();
        }
    }


    // 5.0 *************** ARTIKLI STAFF ***************************
    @FXML private void btnOtvoriArtiklKarticuAct() throws IOException {
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

        //Set AutoServisController u "ARTIKLI_UI_VIEW_URI"  UI
        ArtkikliController createNewArtiklUiController = fxmlLoaderArtikli.getController();
        createNewArtiklUiController.setAutmobilController(this, stageArtikli);
        //createNewArtiklUiController.setWeAreInEditMode(true);

        stageArtikli.showAndWait();
    }

    // 6.0 *************** BUTTONs STAFF ***************************

    /**
     * Print Blanko radnih Naloga
     * Samo otvara {@link rs.acreno.nalozi.RadniNalogController} i implementira mogucnost stampanja
     * blanko {@link rs.acreno.nalozi.RadniNalog}-a.
     *
     * @param actionEvent if we need in some case
     * @throws IOException not found {@link Constants#RADNI_NALOZI_UI_VIEW_URI}
     */
    @FXML private void btnPrintBlankoRadniNalogAct(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoaderRadniNalog = new FXMLLoader(getClass().getResource(Constants.PRINT_BLANKO_RADNI_NALOG_UI_VIEW_URI));
        Stage stageRadniNalog = new Stage();
        stageRadniNalog.initModality(Modality.APPLICATION_MODAL);
        stageRadniNalog.setResizable(false);
        stageRadniNalog.getIcons().add(new Image(AutoServisController.class.getResourceAsStream(Constants.APP_ICON)));
        stageRadniNalog.setScene(new Scene(fxmlLoaderRadniNalog.load()));
        stageRadniNalog.showAndWait();
    }

    /**
     * Print Blanko Defektaze
     * Samo otvara {@link rs.acreno.defektaza.DefektazaController} i implementira mogucnost stampanja
     * blanko {@link rs.acreno.defektaza.Defektaza}-a.
     *
     * @param actionEvent if we need in some case
     * @throws IOException not found {@link Constants#RADNI_NALOZI_UI_VIEW_URI}
     */
    @FXML private void btnPrintBlankoDefektazaAct(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoaderDefektaza = new FXMLLoader(getClass().getResource(Constants.PRINT_BLANKO_DEFEKTAZA_UI_VIEW_URI));
        Stage stageRadniNalog = new Stage();
        stageRadniNalog.initModality(Modality.APPLICATION_MODAL);
        stageRadniNalog.setResizable(false);
        stageRadniNalog.getIcons().add(new Image(AutoServisController.class.getResourceAsStream(Constants.APP_ICON)));
        stageRadniNalog.setScene(new Scene(fxmlLoaderDefektaza.load()));
        stageRadniNalog.showAndWait();
    }

    /**
     * Kada se klikne na BORDER PANE da se zatvori {@link #listViewAutmobiliSearch}, {@link #listViewKlijentiSearch}
     * jer ostaje otvorena i cisti text iz {@link #txtFieldRegOznaka}(ne sada)
     */
    @FXML private void bPaneOnMouseClick() {
        // txtFieldRegOznaka.setText("");
        listViewAutmobiliSearch.setVisible(false);
        listViewKlijentiSearch.setVisible(false);
    }

    /**
     * Zatvori ACReno Aplikaciju
     */
    @FXML private void btnCloseApplication() {
        logger.info("******* CLOSE APPLICATION *******");
        Stage stage = (Stage) btnCloseApplication.getScene().getWindow();
        stage.close();
        System.exit(0);
    }


}

