package rs.acreno.artikli;

import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.converter.NumberStringConverter;
import org.apache.log4j.Logger;
import rs.acreno.autoservis.AutoServisController;
import rs.acreno.system.constants.Constants;
import rs.acreno.system.exeption.AcrenoException;
import rs.acreno.system.util.ActionButtonTableCell;
import rs.acreno.system.util.GeneralUiUtility;

import java.net.URL;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Klasa koja je za EDITOVANJE / UBACIVANJE {@link Artikl} Objekata
 * <p>
 * Trenutno se ucitavaju SVI ARTIKLI(probano sa 1K i radi ok), u buducnosti ->"Pagnation or Lazy Load"
 *
 * @author Dejan Cugalj
 * @version Alpha 1.1
 * @since Alpha 1.1
 */
public class ArtkikliController implements Initializable {

    private static final Logger logger = Logger.getLogger(ArtkikliController.class);

    // 1.0 *************** FXMLs **************************************

    // 1.1 *************** FXMLs GUI
    @FXML private BorderPane artikliUiBorderPane;
    @FXML private Pane paneUnosArtikli;

    // 1.2 *************** FXMLs TABELA ARTIKLI
    @FXML private TableView<Artikl> tblArtikli;
    @FXML private TableColumn<Artikl, Number> tblColIDartikla;
    @FXML private TableColumn<Artikl, String> tblColKataloskiBrojArtikla;
    @FXML private TableColumn<Artikl, String> tblColNazivArtikla;
    @FXML private TableColumn<Artikl, String> tblColOpisArtikla;
    @FXML private TableColumn<Artikl, String> tblColJedinicaMereArtikla;
    @FXML private TableColumn<Artikl, Number> tblColKolicinaArtikla;
    @FXML private TableColumn<Artikl, Number> tblColNabavnaCenaArtikla;
    @FXML private TableColumn<Artikl, Number> tblColCenaArtikla;
    @FXML private TableColumn<Artikl, Button> tblColButtonUredi;
    @FXML private TableColumn<Artikl, Button> tblButtonObrisi;

    //1.2 ****************FXMLs TXTFs
    @FXML private TextField txtfIdArtikla;
    @FXML private TextField txtfKataloskiBroj;
    @FXML private TextField txtfNazivArtikla;
    @FXML private TextField txtfOpisArtikla;
    @FXML private TextField txtfCenaArtikla;
    @FXML private TextField txtfNabavnaCenaArtikla;
    @FXML private TextField txtfKolicinaArtikla;
    @FXML private ComboBox<String> cmbJedinicaMere;

    // 1.3 ***************  BUTTONs STAFF
    @FXML private Button btnCloseArtikliKarticu;
    @FXML private Button btnDodajArtikl;

    // 1.4 ***************  TXT FIELDS STAFF
    @FXML private TextField txtfFindArtikl;

    /**
     * ListView koja cuva Listu {@link Artikl}-a u TxtF-u prilikom filtera u kucanju.
     */
    @FXML private ListView<Artikl> lvArtiklSearch;

    // 2.0 *************** REFERENCE KA AutoServisController-u **********

    /**
     * Referenca ka {@link AutoServisController}-u
     */
    private final AtomicReference<AutoServisController> autoServisController = new AtomicReference<>();

    /**
     * Psorledjivcanje {@link Stage} objekta u {@link AutoServisController #btnOtvoriArtiklKarticuAct()}
     *
     * @see #setAutmobilController(AutoServisController, Stage)
     */
    private Stage stageCreateNewArtikl;

    /**
     * Seter metoda koja se koristi u {@link AutoServisController #openAddEditklijent()}-u
     * {@code isWeAreInEditMode = true} nas obavestava da smo u EDIT MODU iz
     * {@link AutoServisController #btnOpenNoviKlijentGui(ActionEvent)}
     *
     * @param autoServisController referenca ka {@link AutoServisController} kontroloru
     * @see AutoServisController
     */
    public void setAutmobilController(AutoServisController autoServisController, Stage stageCreateNewArtikl) {
        this.autoServisController.set(autoServisController);
        this.stageCreateNewArtikl = stageCreateNewArtikl;
    }

    // 3.0 *************** INICIJALIZACIJA *******************************

    /**
     * Empty Constructor if we need this for later use
     *
     * @throws AcrenoException Zbog Exeption-sa u inicijalizaciji Objekata ARTIKL
     * @throws SQLException    Zbog Exeption-sa u inicijalizaciji Objekata ARTIKL
     */
    public ArtkikliController() throws AcrenoException, SQLException {
    }

    /**
     * Inicijalizacija {@link Artikl} Objekta
     * Da bi u samom startu imali sve objekte {@link Artikl}. Koristi se {@link ArtikliDAO#findAllArtikle()} interfejs,
     * koji je implemetiran u {@link SQLArtikliDAO}, a trenutno se koristi MSACCESS implementacija.
     * Sve se ubacuje u {@code ObservableList<Artikl> artikli} zbog FX frameworks-a.
     *
     * @see Artikl
     * @see SQLArtikliDAO#findAllArtikle()
     * @since Aplha 1.1
     */
    private final ArtikliDAO artikliDAO = new SQLArtikliDAO();
    private ObservableList<Artikl> artikli = FXCollections.observableArrayList(artikliDAO.findAllArtikle());
    private boolean ifWeAreInEditMode = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Platform.runLater(() -> {
            popuniTabeluArtikli();
            //Inicijalizacija i Postavljenje dugmeta "IZMENI" u tabeli ARTIKLI
            tblColButtonUredi.setCellFactory(ActionButtonTableCell.forTableColumn("Izmeni", (Artikl artikl) -> {
                ifWeAreInEditMode = true;
                logger.debug("DA LI SMO U EDIT MODU: " + ifWeAreInEditMode);
                logger.debug("ARTIKL: " + artikl.getKataloskiBrArtikla() + " || " + artikl.getNazivArtikla());
                txtfIdArtikla.setText(String.valueOf(artikl.getIdArtikla()));
                txtfKataloskiBroj.setText(artikl.getKataloskiBrArtikla());
                txtfNazivArtikla.setText(artikl.getNazivArtikla());
                txtfOpisArtikla.setText(artikl.getOpisArtikla());
                txtfCenaArtikla.setText(String.valueOf(artikl.getCenaArtikla()));
                txtfNabavnaCenaArtikla.setText(String.valueOf(artikl.getNabavnaCenaArtikla()));
                txtfKolicinaArtikla.setText(String.valueOf(artikl.getKolicina()));
                cmbJedinicaMere.setValue(artikl.getJedinicaMere());
                btnDodajArtikl.setDisable(false);

                btnDodajArtikl.setText("UREĐIVANJE ARTIKLA: " + artikl.getIdArtikla());
                artikl = new Artikl();
                return artikl;
            }));

            tblButtonObrisi.setCellFactory(ActionButtonTableCell.forTableColumn("Obriši", (Artikl artikl) -> {
                try {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Potvrda brisanja Artikla: " + artikl.getIdArtikla());
                    alert.setHeaderText("Brisanje Artikla: " + artikl.getNazivArtikla());
                    alert.setContentText("Da li ste sigurni da želite da obrišete " + artikl.getNazivArtikla() + " iz baze podataka ?");
                    ((Stage) alert.getDialogPane().getScene().getWindow()).getIcons().add(new Image(Constants.APP_ICON));
                    Optional<ButtonType> result = alert.showAndWait();
                    if (result.isPresent()) {
                        if (result.get() == ButtonType.OK) {
                            artikliDAO.deleteArtikli(artikl);
                            tblArtikli.getItems().remove(artikl);
                            tblArtikli.refresh();
                        }
                    }

                } catch (AcrenoException | SQLException e) {
                    e.printStackTrace();
                }
                return artikl;
            }));
        });
    }


    // 4.0 **************** ARTIKLI STAFF ***************************************
    /**
     * Promenjiva koja nam je potrebna za update PosaoArtikl podataka u celijama tablele {@link #tblArtikli}
     * <p>
     * Kada ubacimo Artikl u Tabelu {@link #tblArtikli} i hocemo da uradimo update nekih podataka(cene, kolicine)
     * moramo iammo temo promenjuvu "posaoArtikliTemp" jer pratimo koje vrednosti ubacujemo, a to sve zbog
     * {@link ArtikliDAO#updateArtikli(Artikl)} jer se ovde trazi ID Posao Artikla i koju
     * vrednsot menjamo.
     *
     * @see #popuniTabeluArtikli
     * @see ArtikliDAO#updateArtikli(Artikl)
     */
    private Artikl artikliTemp;


    // 4.1 **************** TABELA SA AUTOMOBILIMA IZABRANOG KLIJENTA (AKO NAM ZATREBA)
    private void popuniTabeluArtikli() {
        tblArtikli.getSelectionModel().setCellSelectionEnabled(true);
        tblArtikli.setEditable(true);
        // logger.debug("ID KLIJENTA: " + artikli.get(0).getNazivArtikla());
        //tblColl ID ARTIKLA
        tblColIDartikla.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getIdArtikla()));
        tblColIDartikla.setStyle("-fx-alignment: CENTER;");
        //tblColl KATALOSKI BROJ
        tblColKataloskiBrojArtikla.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getKataloskiBrArtikla()));
        tblColKataloskiBrojArtikla.setStyle("-fx-alignment: CENTER;");
        tblColKataloskiBrojArtikla.setCellFactory(TextFieldTableCell.forTableColumn());
        tblColKataloskiBrojArtikla.setOnEditCommit(t -> {
            if (t.getNewValue().equals("")) {
                GeneralUiUtility.alertDialogBox(Alert.AlertType.ERROR,
                        "GREŠKA", "PRAZNO POLJE", "Polje mora imati vrednost!");
            } else {
                t.getRowValue().setKataloskiBrArtikla(t.getNewValue());
                try {
                    artikliTemp = t.getRowValue();
                    artikliTemp.setIdArtikla(t.getRowValue().getIdArtikla()); // Obavezno ID zbog update-a
                    artikliTemp.setKataloskiBrArtikla(t.getRowValue().getKataloskiBrArtikla());

                    artikliDAO.updateArtikli(artikliTemp); // update u DB

                } catch (SQLException | AcrenoException throwables) {
                    throwables.printStackTrace();
                }
            }
        });
        //tblColl NAZIV ARTIKLA
        tblColNazivArtikla.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getNazivArtikla()));
        tblColNazivArtikla.setStyle("-fx-alignment: CENTER;");
        tblColNazivArtikla.setCellFactory(TextFieldTableCell.forTableColumn());
        tblColNazivArtikla.setOnEditCommit(t -> {
            if (t.getNewValue().equals("")) {
                GeneralUiUtility.alertDialogBox(Alert.AlertType.ERROR,
                        "GREŠKA", "PRAZNO POLJE", "Polje mora imati vrednost!");
            } else {
                t.getRowValue().setNazivArtikla(t.getNewValue());
                try {
                    artikliTemp = t.getRowValue();
                    artikliTemp.setIdArtikla(t.getRowValue().getIdArtikla()); // Obavezno ID zbog update-a
                    artikliTemp.setNazivArtikla(t.getRowValue().getNazivArtikla());

                    artikliDAO.updateArtikli(artikliTemp); // update u DB

                } catch (SQLException | AcrenoException throwables) {
                    throwables.printStackTrace();
                }
            }
        });
        //tblColl OPIS ARTIKLA
        tblColOpisArtikla.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getOpisArtikla()));
        tblColOpisArtikla.setStyle("-fx-alignment: CENTER;");
        tblColOpisArtikla.setCellFactory(TextFieldTableCell.forTableColumn());
        tblColOpisArtikla.setOnEditCommit(t -> {
            if (t.getNewValue().equals("")) {
                GeneralUiUtility.alertDialogBox(Alert.AlertType.ERROR,
                        "GREŠKA", "PRAZNO POLJE", "Polje mora imati vrednost!");
            } else {
                t.getRowValue().setOpisArtikla(t.getNewValue());
                try {
                    artikliTemp = t.getRowValue();
                    artikliTemp.setIdArtikla(t.getRowValue().getIdArtikla()); // Obavezno ID zbog update-a
                    artikliTemp.setOpisArtikla(t.getRowValue().getOpisArtikla());

                    artikliDAO.updateArtikli(artikliTemp); // update u DB

                } catch (SQLException | AcrenoException throwables) {
                    throwables.printStackTrace();
                }
            }
        });
        //tblColl JEDINICA MERE ARTIKLA
        tblColJedinicaMereArtikla.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getJedinicaMere()));
        tblColJedinicaMereArtikla.setStyle("-fx-alignment: CENTER;");
        tblColJedinicaMereArtikla.setCellFactory(TextFieldTableCell.forTableColumn());
        tblColJedinicaMereArtikla.setOnEditCommit(t -> {
            if (t.getNewValue().equals("")) {
                GeneralUiUtility.alertDialogBox(Alert.AlertType.ERROR,
                        "GREŠKA", "PRAZNO POLJE", "Polje mora imati vrednost!");
            } else {
                t.getRowValue().setJedinicaMere(t.getNewValue());
                try {
                    artikliTemp = t.getRowValue();
                    artikliTemp.setIdArtikla(t.getRowValue().getIdArtikla()); // Obavezno ID zbog update-a
                    artikliTemp.setJedinicaMere(t.getRowValue().getJedinicaMere());

                    artikliDAO.updateArtikli(artikliTemp); // update u DB

                } catch (SQLException | AcrenoException throwables) {
                    throwables.printStackTrace();
                }
            }
        });
        //tblColl KOLICINA ARTIKLA
        tblColKolicinaArtikla.setCellValueFactory(cellData ->
                new SimpleDoubleProperty(cellData.getValue().getKolicina()));
        tblColKolicinaArtikla.setStyle("-fx-alignment: CENTER;");
        tblColKolicinaArtikla.setCellFactory(TextFieldTableCell.forTableColumn(new NumberStringConverter()));
        tblColKolicinaArtikla.setOnEditCommit((TableColumn.CellEditEvent<Artikl, Number> t) -> {
            if (t.getNewValue() == null) {
                GeneralUiUtility.alertDialogBox(Alert.AlertType.ERROR,
                        "GRESKA", "PRAZNO POLJE", "Polje mora imati vrednost!");
            } else {
                try {
                    t.getRowValue().setKolicina(t.getNewValue().intValue());
                    artikliTemp = t.getRowValue();
                    artikliTemp.setIdArtikla(t.getRowValue().getIdArtikla()); // Obavezno ID zbog update-a
                    artikliTemp.setKolicina(t.getRowValue().getKolicina());
                    artikliDAO.updateArtikli(artikliTemp); // update u DB
                } catch (SQLException | AcrenoException throwables) {
                    throwables.printStackTrace();
                }
            }
        });

        //tblColl NABAVNA CENA ARTIKLA
        tblColNabavnaCenaArtikla.setCellValueFactory(cellData ->
                new SimpleDoubleProperty(cellData.getValue().getNabavnaCenaArtikla()));
        tblColNabavnaCenaArtikla.setStyle("-fx-alignment: CENTER;");
        tblColNabavnaCenaArtikla.setCellFactory(TextFieldTableCell.forTableColumn(new NumberStringConverter()));
        tblColNabavnaCenaArtikla.setOnEditCommit((TableColumn.CellEditEvent<Artikl, Number> t) -> {
            if (t.getNewValue() == null) {
                GeneralUiUtility.alertDialogBox(Alert.AlertType.ERROR,
                        "GRESKA", "PRAZNO POLJE", "Polje mora imati vrednost!");
            } else {
                try {
                    t.getRowValue().setNabavnaCenaArtikla(t.getNewValue().doubleValue());
                    artikliTemp = t.getRowValue();
                    artikliTemp.setIdArtikla(t.getRowValue().getIdArtikla()); // Obavezno ID zbog update-a
                    artikliTemp.setNabavnaCenaArtikla(t.getRowValue().getNabavnaCenaArtikla());
                    artikliDAO.updateArtikli(artikliTemp); // update u DB
                } catch (SQLException | AcrenoException throwables) {
                    throwables.printStackTrace();
                }
            }
        });

        //tblColl CENA ARTIKLA
        tblColCenaArtikla.setCellValueFactory(cellData ->
                new SimpleDoubleProperty(cellData.getValue().getCenaArtikla()));
        tblColCenaArtikla.setStyle("-fx-alignment: CENTER;");
        tblColCenaArtikla.setCellFactory(TextFieldTableCell.forTableColumn(new NumberStringConverter()));
        tblColCenaArtikla.setOnEditCommit(t -> {
            if (t.getNewValue() == null) {
                GeneralUiUtility.alertDialogBox(Alert.AlertType.ERROR,
                        "GRESKA", "PRAZNO POLJE", "Polje mora imati vrednost!");
            } else {
                t.getRowValue().setCenaArtikla(t.getNewValue().doubleValue());
                try {
                    artikliTemp = t.getRowValue();
                    artikliTemp.setIdArtikla(t.getRowValue().getIdArtikla()); // Obavezno ID zbog update-a
                    artikliTemp.setCenaArtikla(t.getRowValue().getCenaArtikla());
                    artikliDAO.updateArtikli(artikliTemp); // update u DB
                } catch (SQLException | AcrenoException throwables) {
                    throwables.printStackTrace();
                }
            }

        });

        tblArtikli.setItems(artikli);
    }

    /**
     * Filtrira tabelu "Klijenti" kada se kuca u txtField txtFieldFilterTable
     * onKeyReleased ubacen okidac.
     * Koristie se polja: "idKlijentaData", "imePrezimeData".
     */
    @FXML private void pretragaTabeleKlijenti() {

        txtfFindArtikl.textProperty().addListener(observable -> {
            if (txtfFindArtikl.textProperty().get().isEmpty()) {
                tblArtikli.setItems(artikli);
                return;
            }
            ObservableList<Artikl> tableItems = FXCollections.observableArrayList();
            for (Artikl artikl : artikli) {
                String kataloskiBroj = tblArtikli.getColumns().get(1)
                        .getCellObservableValue(artikl).getValue().toString().toLowerCase();
                String nazivArtikla = tblArtikli.getColumns()
                        .get(2).getCellObservableValue(artikl).getValue().toString().toLowerCase();
                if (kataloskiBroj.contains(txtfFindArtikl.textProperty().get())) {
                    tableItems.add(artikl);
                    break;
                } else if (nazivArtikla.contains(txtfFindArtikl.textProperty().get())) {
                    tableItems.add(artikl);
                }
            }
            tblArtikli.setItems(tableItems);
        });
    }

    /**
     * Zatvori Prozor i prosledjuje "Oclose EVET" u {@link AutoServisController #btnOtvoriArtiklKarticuAct()}
     *
     * @see AutoServisController #btnOtvoriArtiklKarticuAct()
     * @since Alpha 1.1
     */
    @FXML private void btnCloseArtikliKarticuAct() {
        btnCloseArtikliKarticu.fireEvent(new WindowEvent(stageCreateNewArtikl, WindowEvent.WINDOW_CLOSE_REQUEST));
    }

    @FXML private void btnDodajArtiklAct(MouseEvent mouseEvent) throws SQLException, AcrenoException {

        if (txtfKataloskiBroj.getText().isEmpty()
                || txtfNazivArtikla.getText().isEmpty()) {
            GeneralUiUtility.alertDialogBox(Alert.AlertType.ERROR, "Greška..."
                    , "Greška u unosu Artikla", "Morate da popunite polja Artikla da bi smo ga sačuvali!");
        } else {
            int idArtiklaTemp = 1;
            artikliTemp = new Artikl();
            //Inicijalizacija broja fakture MORA DA IDE OVDE
            try {
                ObservableList<Artikl> artiklovi = FXCollections.observableArrayList(artikliDAO.findAllArtikle());
                try {

                    if (artiklovi.size() < 1) {
                        artikliTemp.setIdArtikla(1);
                    } else {
                        idArtiklaTemp = artiklovi.get(artiklovi.size() - 1).getIdArtikla();
                    }
                        artikliTemp.setIdArtikla(idArtiklaTemp);
                        artikliTemp.setKataloskiBrArtikla(txtfKataloskiBroj.getText());
                        artikliTemp.setNazivArtikla(txtfNazivArtikla.getText());
                        artikliTemp.setOpisArtikla(txtfOpisArtikla.getText());
                        artikliTemp.setJedinicaMere(cmbJedinicaMere.getValue());
                        artikliTemp.setKolicina(Integer.parseInt(txtfKolicinaArtikla.getText()));
                        artikliTemp.setNabavnaCenaArtikla(Double.parseDouble(txtfNabavnaCenaArtikla.getText()));
                        artikliTemp.setCenaArtikla(Double.parseDouble(txtfCenaArtikla.getText()));

                } catch (IndexOutOfBoundsException exception) {
                    logger.error("IndexOutOfBoundsException, FORM #btnDodajArtiklAct sa porukom: " + exception);
                    GeneralUiUtility.alertDialogBox(Alert.AlertType.ERROR,
                            "GREŠKA, bilo bi dobro da kontaktirate Administratora!" ,"Greška"
                            , "IndexOutOfBoundsException, FORM #btnDodajArtiklAct sa porukom:: " + exception);
                }
            } catch (NumberFormatException ex) {
                logger.info("GRESKA U NUMBER FORMATU");

            }
            if (ifWeAreInEditMode) {
                artikliTemp.setIdArtikla(Integer.parseInt(txtfIdArtikla.getText()));
                artikliDAO.updateArtikli(artikliTemp);
            } else {
                if (txtfKataloskiBroj.getText().isEmpty())
                    txtfKataloskiBroj.setText("");
                if (txtfCenaArtikla.getText().isEmpty())
                    txtfCenaArtikla.setText("0");
                if (txtfNabavnaCenaArtikla.getText().isEmpty())
                    txtfNabavnaCenaArtikla.setText("0");
                if (txtfKolicinaArtikla.getText().isEmpty())
                    txtfKolicinaArtikla.setText("0");

                artikliDAO.insertArtikli(artikliTemp);
            }
            ObservableList<Artikl> artikloviUpdate = FXCollections.observableArrayList(artikliDAO.findAllArtikle());
            tblArtikli.getItems().clear();
            artikli.addAll(artikloviUpdate);
            //popuniTabeluArtikli();
            //tblArtikli.refresh();
            //Obrisi Polja
           // GeneralUiUtility.clearTextFieldsInPane(paneUnosArtikli);
            txtfKataloskiBroj.setText("");
            txtfNazivArtikla.setText("");
            txtfOpisArtikla.setText("");
            txtfCenaArtikla.setText("0");
            txtfNabavnaCenaArtikla.setText("0");
            txtfKolicinaArtikla.setText("0");
            ifWeAreInEditMode = false;
            btnDodajArtikl.setText("DODAJ ARTIKL U BAZU");
        }
    }
}
