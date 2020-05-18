package rs.acreno.autoservis;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import rs.acreno.automobil.*;
import rs.acreno.automobil.ui_add_edit_automobil.AddEditAutomobilController;
import rs.acreno.klijent.Klijent;
import rs.acreno.klijent.KlijentDAO;
import rs.acreno.klijent.KlijentSearchType;
import rs.acreno.klijent.SQLKlijnetDAO;
import rs.acreno.klijent.ui_klijent.CreateNewKlijentUiController;
import rs.acreno.system.constants.Constants;
import rs.acreno.system.exeption.AcrenoException;
import rs.acreno.system.util.GeneralUiUtility;

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
    @FXML private Line lineInternetIndicator;
    @FXML private Button btnNoviAutomobil;
    @FXML private Button btnNoviKlijent;
    @FXML private Button btnUrediAutomobil;

    // 1.0 *************** FXMLs **************************************
    @FXML private Label lblDate;
    @FXML private Label lblTime;

    @FXML private TextField txtFieldPretragaKlijenta;
    @FXML private Button btnOtvoriAutomobilKarticu;
    @FXML private Button btnOtvoriKlijentEditMode;

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
     * txtF za Reg. Tablicu u {@link #txtFieldRegTablicaSaerchKeyListener), pa posle bitno za
     * prosledjivanje tablice {@link AutomobiliController}-u, a koristi se {@link #openAutomobiliUi }
     *
     * @see {@link #txtFieldRegTablicaSaerchKeyListener()}
     * @see {@link #openAutomobiliUi }
     * @see {@link AutomobiliController}
     */
    @FXML private TextField txtFieldRegOznaka;

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
    private ObservableList<Automobil> automobili = FXCollections.observableArrayList(automobilDAO.findAllAutomobil());

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

    /**
     * Empty Constructor if we need this for later use
     *
     * @throws AcrenoException Zbog Exeption-sa u inicijalizaciji Objekata KLIJENT i AUTOMOBIL
     * @throws SQLException    Zbog Exeption-sa u inicijalizaciji Objekata KLIJENT i AUTOMOBIL
     */
    public AutoServisController() throws AcrenoException, SQLException {
    }

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

            //PROVERA INTERNETA
            final ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor();
            ses.scheduleWithFixedDelay(new Runnable() {
                @Override
                public void run() {
                    if (GeneralUiUtility.netIsAvailable()) { //Imamo internet (TRUE)
                        lineInternetIndicator.setStroke(Color.rgb(36, 164, 11));//promeni boju indikatora
                    } else { // Nmea Interneta (FALSE)
                        lineInternetIndicator.setStroke(Color.rgb(198, 13, 13));
                    }
                    System.out.println(new Date() + " = ping IMA INTERNETA !");
                }
            }, 2, Constants.APP_UCESTALOST_PROVERE_INTERNETA, TimeUnit.SECONDS);
        });
    }


    // 3.0 ***************  AUTOMOBILI STAFF ***************************

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
        txtFieldRegOznaka.textProperty().addListener(observable -> {
            if (txtFieldRegOznaka.textProperty().get().isEmpty()) {
                listViewAutmobiliSearch.setItems(automobili);
            }
        });

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
        if (mouseEvent.getButton().equals(MouseButton.PRIMARY) && mouseEvent.getClickCount() == 1) {
            //Moze .getSelectedItems().get(0) jer ima samo jedan Automobil
            if (listViewAutmobiliSearch.getSelectionModel().getSelectedItems().size() > 0) {
                String regOznaka = listViewAutmobiliSearch.getSelectionModel().getSelectedItems().get(0).getRegOznaka();
                txtFieldRegOznaka.setText(regOznaka); // Postavi REG. OZNAKU u TF
                listViewAutmobiliSearch.setVisible(false); // Zatvori listu
                btnOtvoriAutomobilKarticu.setDisable(false); // Omoguci dugme za otvaranje Automobil kartice
                btnUrediAutomobil.setDisable(false); // Omoguci dugme za Editovanje Automobila

                //NADJI KLIJENTA i POSTAVI U txtf  txtFieldPretragaKlijenta
                klijent = klijentDAO.findKlijentByProperty(KlijentSearchType.ID_KLIJENTA,
                        listViewAutmobiliSearch.getSelectionModel().getSelectedItem().getIdKlijenta()).get(0);
                txtFieldPretragaKlijenta.setText(klijent.getImePrezime());
                btnNoviAutomobil.setDisable(false);

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
                String regOznaka = listViewAutmobiliSearch.getSelectionModel().getSelectedItem().getRegOznaka();
                txtFieldRegOznaka.setText(regOznaka);
                txtFieldRegOznaka.requestFocus();
                btnOtvoriAutomobilKarticu.setDisable(false);
                btnUrediAutomobil.setDisable(false); // Omoguci dugme za Editovanje Automobila
                listViewAutmobiliSearch.setVisible(false);
                //NADJI KLIJENTA i POSTAVI U txtf  txtFieldPretragaKlijenta
                klijent = klijentDAO.findKlijentByProperty(KlijentSearchType.ID_KLIJENTA,
                        listViewAutmobiliSearch.getSelectionModel().getSelectedItem().getIdKlijenta()).get(0);
                txtFieldPretragaKlijenta.setText(klijent.getImePrezime());
                btnNoviAutomobil.setDisable(false);
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
            automobili = FXCollections.observableArrayList(
                    automobilDAO.findAutomobilByProperty(AutoSearchType.BR_TABLICE, regOznaka));
            automobiliController.setAutomobil(automobili); // Prosledjivanje "Automobil" Obj u Automobil Kontroler

            //Prosledjivanje filtriranog KLIJENTA po ID AUTOMOBILA u ObservableList "klijenti"
            int idKlijenta = automobili.get(0).getIdKlijenta();//Moze automobili.get(0), jer imamo samo jednog Klijenta.
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
        ((Node) actionEvent.getSource()).getScene().getWindow().hide();
        openAutomobiliUi();
        ((Stage) ((Node) actionEvent.getSource()).getScene().getWindow()).show();
    }

    /**
     * Otvaranje {@link AddEditAutomobilController}-a UI-a za INSERT OR EDIT {@link Automobil}-a
     * <p>
     * Posale FXMLs staffa, inicijalizujemo {@link AddEditAutomobilController} i
     * prosledjujemo {@link AddEditAutomobilController#setWeAreInEditMode(boolean)}da smo u INSERT MODU
     * jer je direktno kliknuto na btnNoviAutomobil.
     *
     * @param actionEvent if we need in some case
     * @throws IOException not found {@link Constants#CREATE_KLIJENT_UI_VIEW_URI}
     * @autor Dejan Cugalj
     * @see AddEditAutomobilController
     * @see AddEditAutomobilController#setWeAreInEditMode(boolean)
     * @see Automobil
     * @see Constants#CREATE_EDIT_AUTOMOBIL_UI_VIEW_URI
     */
    @FXML private void btnOpenNoviAutomobilGui(ActionEvent actionEvent) throws IOException {
        // Standart FX load UI
        FXMLLoader fxmlLoaderNewAutomobil = new FXMLLoader(getClass().getResource(Constants.CREATE_EDIT_AUTOMOBIL_UI_VIEW_URI));
        Stage stageNewAutomobil = new Stage();
        stageNewAutomobil.getIcons().add(new Image(AutoServisController.class.getResourceAsStream(Constants.APP_AUTOMOBIL_ICON)));
        stageNewAutomobil.initModality(Modality.APPLICATION_MODAL);
        Scene scene = new Scene(fxmlLoaderNewAutomobil.load());
        stageNewAutomobil.setScene(scene);
        stageNewAutomobil.setResizable(false);
        stageNewAutomobil.setTitle("Kreiraj Novi Autmobil");

        stageNewAutomobil.setOnCloseRequest(windowEvent -> {
            // txtFieldImeKlijenta.setText(klijenti.get(0).getImePrezime());// Moze jer je samo jedan Klijent
            System.out.println("FORM BUUTTON btnOpenNoviAutomobilGui --- CREATE_EDIT_AUTOMOBIL_UI_VIEW_URI");
        });

        //Set AutoServisController u AutomobiliController UI
        AddEditAutomobilController addEditAutomobilController = fxmlLoaderNewAutomobil.getController();
        addEditAutomobilController.setAutoServisController(this, stageNewAutomobil);
        addEditAutomobilController.setWeAreInEditMode(false); // NISMO U EDITu kliknuto diretno na dugme Novi Automobil
        addEditAutomobilController.setKlijent(klijent); //Prosledi Klijent Obj

        stageNewAutomobil.showAndWait();
    }


    // 3.1 *************** EDIT AUTOMOBILI STAFF

    /**
     * Kada se klikne na {@link #txtFieldRegOznaka} da se zatvori {@link #listViewKlijentiSearch} jer ostaje otvoren.
     * i cisti text iz {@link #txtFieldRegOznaka}
     *
     * @param mouseEvent if we need in some case
     */
    public void txtFieldRegTablicaSaerchhOnMouseClick(MouseEvent mouseEvent) {
        listViewKlijentiSearch.setVisible(false);
        btnOtvoriAutomobilKarticu.setDisable(true);
        btnNoviAutomobil.setDisable(true);
        btnUrediAutomobil.setDisable(true);
    }

    /**
     * Metoda koja se koristi za editovanje {@link Automobil} objekta, a dugme {@link #btnUrediAutomobil)}.
     * <p>
     * {@code listViewAutmobiliSearch.getSelectionModel().getSelectedItem()} se korristi za inicijalizaciju
     * {@link Automobil} objekta koji se dobija selekcijom iz {@link #listViewAutmobiliSearch}.
     * <p>
     * Pristup {@link AddEditAutomobilController}-u i prosledjivanje {@link Automobil} dobijenog objekta.
     *
     * @param actionEvent not used here if we need in some case
     * @throws IOException ULI Locatio is not fonu in {@link Constants#CREATE_EDIT_AUTOMOBIL_UI_VIEW_URI}
     * @bitno {@code listViewAutmobiliSearch.getSelectionModel().getSelectedItem()}
     * @author Dejan Cugalj
     * @see #btnUrediAutomobil
     * @see Automobil
     * @see AddEditAutomobilController
     */
    @FXML private void btnUrediAutomobilAct(ActionEvent actionEvent) throws IOException {
        // Moze jer je samo jed izabran u "listViewAutmobiliSearch"
        Automobil automobil = listViewAutmobiliSearch.getSelectionModel().getSelectedItem();
        // Standart FX load UI
        FXMLLoader fxmlLoaderNewAutomobil = new FXMLLoader(getClass().getResource(Constants.CREATE_EDIT_AUTOMOBIL_UI_VIEW_URI));
        Stage stageNewAutomobil = new Stage();
        stageNewAutomobil.getIcons().add(new Image(AutoServisController.class.getResourceAsStream(Constants.APP_AUTOMOBIL_ICON)));
        stageNewAutomobil.initModality(Modality.APPLICATION_MODAL);
        stageNewAutomobil.setTitle("Izmena Autmobila: " + automobil.getRegOznaka());

        stageNewAutomobil.setOnCloseRequest(windowEvent -> {
            // txtFieldImeKlijenta.setText(klijenti.get(0).getImePrezime());// Moze jer je samo jedan Klijent
            System.out.println("FORM BUUTTON btnUrediAutomobilAct --- CREATE_EDIT_AUTOMOBIL_UI_VIEW_URI");
        });


        Scene scene = new Scene(fxmlLoaderNewAutomobil.load());
        stageNewAutomobil.setScene(scene);
        stageNewAutomobil.setResizable(false);

        //Set AutoServisController u AutomobiliController UI
        AddEditAutomobilController addEditAutomobilController = fxmlLoaderNewAutomobil.getController();
        addEditAutomobilController.setAutoServisController(this, stageNewAutomobil);
        addEditAutomobilController.setWeAreInEditMode(true); // NISMO U EDITu kliknuto diretno na dugme Novi Automobil
        addEditAutomobilController.setAutomobil(automobil); //Prosledi Automobil Obj
        addEditAutomobilController.setKlijent(klijent); //Prosledi Klijent Obj
        addEditAutomobilController.setLblHeaderTitle("IZMENA AUTOMOBILA:");

        stageNewAutomobil.showAndWait();

    }


    // 4.0 ***************  KLIJENTI STAFF ***************************

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
     * @see ##zatvoriLvKlijent(MouseEvent)
     * @see CreateNewKlijentUiController
     */
    @FXML private void txtfKlijentSaerchKeyListener(KeyEvent keyEvent) {
        txtFieldPretragaKlijenta.textProperty().addListener(observable -> {
            if (txtFieldPretragaKlijenta.textProperty().get().isEmpty()) {
                listViewKlijentiSearch.setItems(klijenti.get());
            }
        });

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
                        } else {
                            setText(item.getImePrezime());
                        }
                    }
                });
                //break;
            }
        }
        //VREDNOSTI ZA SHOUCUT SU U CONSTANTAMA (ako budemo menjali nesto da je na jednom mestu)
        KeyCombination keyCombinationCtrltC = new KeyCodeCombination(
                Constants.OTVORI_KLIJENT_KARTICU_KEYCODE,
                Constants.OTVORI_KLIJENTL_KARTICU_KEYCOMBINATION);

        //Ako su pritisnuti Enter ili strelica dole uzmi foruks na ListView
        if (keyEvent.getCode() == KeyCode.ENTER) {
            listViewKlijentiSearch.requestFocus(); // Uzmi foruks na ListView

        } else if (keyEvent.getCode() == KeyCode.DOWN) {
            listViewKlijentiSearch.requestFocus(); // Uzmi foruks na ListView

        } else if (keyCombinationCtrltC.match(keyEvent)) {// Kombinacija CTRL + C -- Otvara KLIJENTA EDIT UIa
            if (!txtFieldPretragaKlijenta.getText().equals("")) {  //Proveri da li je prazan TF zbog NULLEXEPTIONsa
                btnOtvoriKlijentEditMode.fire();

            }
        }
    }

    /**
     * Zatvara popUp ListView pretrage i setuje selektovanu vrednost u TF sa DUPLIM KLIKOM
     * Nakon toga se zatvara ovj UIa i sve se inicijalizuje u {@link #openAddEditklijent()}
     * <p>
     * Inicijalizuje se {@link Klijent} objekat sa odabranom vrednoscu iz {@link #listViewKlijentiSearch}
     *
     * @param mouseEvent da se otvori {@link CreateNewKlijentUiController}
     * @throws IOException not foun Location
     * @author Dejan Cugalj
     * @see #openAutomobiliUi()
     */
    @FXML private void zatvoriLvKlijent(@NotNull MouseEvent mouseEvent) throws IOException {
        //Na dupli click vraca Radni Nalog Objekat i otvara Radni nalog Dashboard
        if (mouseEvent.getButton().equals(MouseButton.PRIMARY) && mouseEvent.getClickCount() == 1) {
            if (listViewKlijentiSearch.getSelectionModel().getSelectedItems().size() > 0) {
                //Moze .getSelectedItems().get(0) jer ima samo jedan Automobil
                String imePrezimeKlijenta = listViewKlijentiSearch.getSelectionModel().getSelectedItems().get(0).getImePrezime();

                //Napravi Klijent Objekat iz odabrane LISTVIEW stavke
                klijent = listViewKlijentiSearch.getSelectionModel().getSelectedItems().get(0);

                txtFieldPretragaKlijenta.setText(imePrezimeKlijenta); // Postavi REG. OZNAKU u TF
                btnNoviAutomobil.setDisable(false);
                listViewKlijentiSearch.setVisible(false); // Zatvori listu

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
            createNewKlijentUiController.setAutoServisController(this);
            createNewKlijentUiController.setWeAreInEditMode(true);

            createNewKlijentUiController.setKlijent(klijent); // prosledi Klijenta u EDIT KLIIJENT CONTROLLER

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
     * @param actionEvent if we need in some case
     * @throws IOException not found {@link Constants#CREATE_KLIJENT_UI_VIEW_URI}
     * @autor Dejan Cugalj
     * @see CreateNewKlijentUiController
     * @see Klijent
     * @see Constants#CREATE_KLIJENT_UI_VIEW_URI
     */
    @FXML private void btnOpenNoviKlijentGui(ActionEvent actionEvent) throws IOException {
        // Standart FX load UI
        FXMLLoader fxmlLoaderNewKlijent = new FXMLLoader(getClass().getResource(Constants.CREATE_KLIJENT_UI_VIEW_URI));
        Stage stageNewKlijent = new Stage();
        stageNewKlijent.getIcons().add(new Image(AutoServisController.class.getResourceAsStream(Constants.APP_CLIENTS_ICON)));
        stageNewKlijent.initModality(Modality.APPLICATION_MODAL);
        stageNewKlijent.setResizable(false);
        Scene scene = new Scene(fxmlLoaderNewKlijent.load());
        stageNewKlijent.setScene(scene);
        stageNewKlijent.setResizable(false);
        stageNewKlijent.setTitle("Kreiranje Novog Klijenta");

        stageNewKlijent.showAndWait();
    }

    /**
     * Kada se klikne na {@link #txtFieldPretragaKlijenta} da se zatvori {@link #listViewAutmobiliSearch} jer ostaje otvoren.
     * i cisti text iz {@link #txtFieldRegOznaka}(ne sada)
     *
     * @param mouseEvent if we need in some case
     */
    @FXML private void txtfKlijentSearchOnMouseClick(MouseEvent mouseEvent) {
        //txtFieldRegOznaka.setText("");
        listViewAutmobiliSearch.setVisible(false);
    }

    // 4.1 **************** TABELA SA AUTOMOBILIMA IZABRANOG KLIJENTA (AKO NAM ZATREBA)
    //     ***************** In some case if we need
    /*private ObservableList<Automobil> popuniTabeluAutomobiliklijenta(@NotNull Klijent klijent) {
        logger.debug("ID KLIJENTA: " + klijent.getIdKlijenta()
        + " || Ime i Prezime Klijenta: " + klijent.getImePrezime());

        try {
            ObservableList<Automobil> automobili = FXCollections.observableArrayList(
                    automobilDAO.findAutomobilByProperty(AutoSearchType.KLIJNET_ID, klijent.getIdKlijenta()));
            for (Automobil automobil : automobili) {
                logger.debug(automobil.getRegOznaka());
            }
        } catch (AcrenoException | SQLException e) {
            e.printStackTrace();
        }
        return automobili;
    }*/


    // 5.0 *************** ARTIKLI STAFF ***************************
    public void btnOtvoriArtiklKarticuAct(ActionEvent actionEvent) throws IOException {
        // Standart FX load UI
        FXMLLoader fxmlLoaderArtikli = new FXMLLoader(getClass().getResource(Constants.ARTIKLI_UI_VIEW_URI));
        Stage stageArtikli = new Stage();
        stageArtikli.getIcons().add(new Image(AutoServisController.class.getResourceAsStream(Constants.APP_ICON)));
        stageArtikli.initModality(Modality.APPLICATION_MODAL);
        stageArtikli.setResizable(false);
        Scene scene = new Scene(fxmlLoaderArtikli.load());
        stageArtikli.setScene(scene);
        stageArtikli.setResizable(false);
        stageArtikli.setTitle("Artikli Kartica");

        stageArtikli.showAndWait();
    }

    // 6.0 *************** BUTTONs STAFF ***************************

    /**
     * Kada se klikne na BORDER PANE da se zatvori {@link #listViewAutmobiliSearch}, {@link #listViewKlijentiSearch}
     * jer ostaje otvorena i cisti text iz {@link #txtFieldRegOznaka}(ne sada)
     *
     * @param mouseEvent if we need in some case
     */
    @FXML private void bPaneOnMouseClick(MouseEvent mouseEvent) {
        // txtFieldRegOznaka.setText("");
        listViewAutmobiliSearch.setVisible(false);
        listViewKlijentiSearch.setVisible(false);
    }

    /**
     * Zatvori ACReno Aplikaciju
     */
    @FXML private void btnCloseApplication() {
        System.exit(0);
    }



}
