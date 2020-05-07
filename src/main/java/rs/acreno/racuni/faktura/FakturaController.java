package rs.acreno.racuni.faktura;

import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
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

    @FXML private TextField txtFidRacuna;
    @FXML private TextField txtFklijentImePrezime;
    @FXML private TextField txtFregTablica;
    @FXML private TextField txtFieldPretragaArtikla;
    @FXML private DatePicker datePickerDatumRacuna;

    //ARTICLES FIELDS in Faktura
    @FXML private ListView<Artikl> listViewPretragaArtikli;
    @FXML private TextField txtFidArtikla;
    @FXML private TextField txtFcenaArtikla;
    @FXML private TextField txtFnabavnaCenaArtikla;
    @FXML private TextField txtFKolicinaArtikla;
    @FXML private TextField txtFjedinicaMereArtikla;
    @FXML private TextField txtFpopustArtikla;
    @FXML private Button btnDodajArtiklRacun;
    @FXML private TextArea txtAreaNapomenaRacuna;
    @FXML private TextField txtFpopustRacuna;
    @FXML private Button btnOdustaniObrisiRacun;
    @FXML private Button btnSacuvajRacun;
    @FXML private TextField txtFopisArtikla;
    @FXML private TextArea txtAreaDetaljiOpisArtikla;

    @FXML private Button btnCloseFakture;

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
    private int brojFakture;
    private Racun noviRacun;

    //Inicijalizacija Racuni Objekta
    private final RacuniDAO racuniDAO = new SQLRacuniDAO();
    private final ArtikliDAO artikliDAO = new SQLArtikliDAO();
    private final PosaoArtikliDAO posaoArtikliDAO = new SQLPosaoArtikliDAO();

    /**
     * Empty Constructor
     */
    public FakturaController() {
    }

    private Stage automobilStage;
    private AutomobiliController automobiliController;

    public void setAutmobilController(AutomobiliController autmobilController, Stage automobilStage) {
        this.automobiliController = autmobilController;
        this.automobilStage = automobilStage;
    }

    private void initGUI() {
        //Inicijalizacija podataka
        automobili = automobiliController.getAutomobil(); //Get AUTOMOBIL from automobiliController #Filtered
        klijenti = automobiliController.getKlijenti(); //Get KLIJENTA from automobiliController #Filtered
        idAutomobila = automobili.get(0).getIdAuta();
        regOznakaAutomobila = automobili.get(0).getRegOznaka();
        idKlijenta = klijenti.get(0).getIdKlijenta();
        imePrezimeKlijenta = klijenti.get(0).getImePrezime();
        //Popunjavanje GUIa
        txtFklijentImePrezime.setText(imePrezimeKlijenta);
        txtFregTablica.setText(regOznakaAutomobila);
        txtFpopustRacuna.setText(String.valueOf(0));
    }

    public Button btnPrint;


    Stage stagePrint;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Platform.runLater(() -> {

            btnPrint.setOnAction(e -> {
                System.out.println("OTVORI PRINT FXML **UiProntControler !!!!");
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource(Constants.PRINT_FAKTURA_UI_VIEW_URI));
                    stagePrint = new Stage();
                    stagePrint.initModality(Modality.APPLICATION_MODAL);
                    stagePrint.setScene(new Scene(loader.load()));
                    /**
                     *  Inicijalizacija Porint Controlora i prosledjivanje id Racuna {@link #initUiPrintControler}
                     *  Na ovom mestu je zato sto je ovo poslednja pozicija koja se radi pre otvaranja Print Cotrolora
                     *  TODO: posataviti seter u ovom controleru kao i u {@link UiPrintRacuniControler}
                     */
                    initUiPrintControler(loader);

                    stagePrint.showAndWait();//Open Stage and wait

                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });

            btnCloseFakture.setOnAction(e -> ((Stage) (((Button) e.getSource()).getScene().getWindow())).close());

            //BTN SACUVAJ UPDATE RACUNA
            btnSacuvajRacun.setOnAction(e -> {
                try {
                    //UPSATE NOVO RACUNA SA NOVIM VREDNOSTIMA ZATO OVDE REDEFINISEMO NOVI RACUN
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
                }
            });

            //BTN OBRISI ODUSTANI RACUN
            btnOdustaniObrisiRacun.setOnAction(e -> {
                try {
                    racuniDAO.deleteRacun(brojFakture);
                    ((Stage) (((Button) e.getSource()).getScene().getWindow())).close();
                } catch (AcrenoException | SQLException acrenoException) {
                    acrenoException.printStackTrace();
                }
            });

            txtFieldPretragaArtikla.setOnKeyReleased(this::txtFieldPretragaArtiklaKeyListener);
            listViewPretragaArtikli.setOnMouseClicked(this::zatvoriListViewSearchAutomobil);
            btnDodajArtiklRacun.setOnMouseClicked(this::btnDodajArtiklRacunMouseClick);

            //Postavljenje dugmica ADD u Tabeli ARTIKLI
            tblRowButton.setCellFactory(ActionButtonTableCell.forTableColumn("x", (PosaoArtikli p) -> {
                System.out.println("KLICK FROM BUTTON IN TABLE ARTIKLI");
                System.out.println("ID RACINA: " + p.getIdRacuna());
                System.out.println("ID ARTIKLA: " + p.getIdArtikla());
                System.out.println("ID ARTIKLA: " + p.getIdPosaoArtikli());
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
                tblPosaoArtikli.getItems().remove(p);
                return p;
            }));

            //Datum
            LocalDate now = LocalDate.now();
            datePickerDatumRacuna.setValue(now);
            napraviNoviRacun();
        });
    }


    public void txtFieldPretragaArtiklaKeyListener(KeyEvent keyEvent) {
        txtFieldPretragaArtikla.textProperty().addListener(observable -> {
            if (txtFieldPretragaArtikla.textProperty().get().isEmpty()) {
                listViewPretragaArtikli.setItems(artikli);
                return;
            }
            ObservableList<Artikl> artikl = null;
            ObservableList<Artikl> tempArtikl = null;
            try {
                artikl = FXCollections.observableArrayList(artikliDAO.findAllArtikle()); //Svi Automobili
                tempArtikl = FXCollections.observableArrayList(); //Lista u koju dodajemo nadjene Auto objekte
            } catch (AcrenoException | SQLException e) {
                e.printStackTrace();
            }
            for (int i = 0; i < (artikl != null ? artikl.size() : 0); i++) {

                String RegTablica = artikl.get(i).getNazivArtikla().toLowerCase();//Trenutna tablica auta

                if (RegTablica.contains(txtFieldPretragaArtikla.textProperty().get())) {
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

    // Zatvara popUp ListView pretrage i setuje selektovanu vrednost u TF sa double click
    public void zatvoriListViewSearchAutomobil(@NotNull MouseEvent mouseEvent) {
        //Na dupli click vraca Radni Nalog Objekat i otvara Radni nalog Dashboard
        if (mouseEvent.getButton().equals(MouseButton.PRIMARY) && mouseEvent.getClickCount() == 2) {
            String nazivArtikla = listViewPretragaArtikli.getSelectionModel().getSelectedItems().get(0).getNazivArtikla();
            int idArtikla = listViewPretragaArtikli.getSelectionModel().getSelectedItems().get(0).getIdArtikla();
            double cenaArtikla = listViewPretragaArtikli.getSelectionModel().getSelectedItems().get(0).getCenaArtikla();
            double nabavnaCenaArtikla = listViewPretragaArtikli.getSelectionModel().getSelectedItems().get(0).getNabavnaCenaArtikla();
            String jedinicaMereArtikla = listViewPretragaArtikli.getSelectionModel().getSelectedItems().get(0).getJedinicaMere();
            String opisArtikla = listViewPretragaArtikli.getSelectionModel().getSelectedItems().get(0).getOpisArtikla();
            txtFidArtikla.setText(String.valueOf(idArtikla));
            txtFcenaArtikla.setText(String.valueOf(cenaArtikla));
            txtFnabavnaCenaArtikla.setText(String.valueOf(nabavnaCenaArtikla));
            txtFKolicinaArtikla.setText(String.valueOf(1));
            txtFjedinicaMereArtikla.setText(jedinicaMereArtikla);
            txtFpopustArtikla.setText(String.valueOf(0));
            txtFieldPretragaArtikla.setText(nazivArtikla);
            txtFopisArtikla.setText(opisArtikla);
            btnDodajArtiklRacun.setDisable(false); // omoguci dugme dodaj u listu
            listViewPretragaArtikli.setVisible(false);
        }
    }

    public void napraviNoviRacun() {
        initGUI();

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

    public void btnDodajArtiklRacunMouseClick(MouseEvent mouseEvent) {

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

            posaoArtikliDAO.insertPosaoArtikliDao(posaoArtikliObject);

            ObservableList<PosaoArtikli> test = FXCollections.observableArrayList(
                    posaoArtikliDAO.findPosaoArtikliByPropertyDao(
                            PosaoArtikliDaoSearchType.ID_RACUNA_POSAO_ARTIKLI_DAO, brojFakture));

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
}

