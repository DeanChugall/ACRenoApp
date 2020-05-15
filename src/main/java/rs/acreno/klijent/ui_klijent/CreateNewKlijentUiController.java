package rs.acreno.klijent.ui_klijent;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import rs.acreno.automobil.AutomobiliController;
import rs.acreno.autoservis.AutoServisController;
import rs.acreno.klijent.Klijent;
import rs.acreno.klijent.KlijentDAO;
import rs.acreno.klijent.SQLKlijnetDAO;
import rs.acreno.system.constants.Constants;
import rs.acreno.system.exeption.AcrenoException;
import rs.acreno.system.util.GeneralUiUtility;

import java.net.URL;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicReference;

public class CreateNewKlijentUiController implements Initializable {

    //FXMLs
    @FXML private Label lblKreateOrEditKlijenta;
    @FXML private TextField txtfIdKlijenta;
    @FXML private TextField txtfImePrezimeKlijenta;
    @FXML private TextField txtfMobilniTelefonKlijenta;
    @FXML private TextField txtfFiksniTelefonKlijenta;
    @FXML private TextField txrfEmailKlijenta;
    @FXML private TextField txtfUlicaBrojKlijenta;
    @FXML private TextField txtfGradKlijenta;
    @FXML private TextField txtfPosatanskiBrojKlijenta;
    @FXML private TextArea txtaOstaliDetaljiKlijenta;
    @FXML private TextField txtfMaticniBrojKlijenta;
    @FXML private TextField txtfBrojLicneKarteKlijenta;
    @FXML private TextField txtfBrojZiroRacunaKlijenta;
    @FXML private TextField txtfBankaKlijenta;
    @FXML private TextField txtfWebSajtKlijenta;
    @FXML private Button btnCloseCreateEditKlijent;

    /**
     * Referenca ka {@link AutomobiliController}-u
     */
    private final AtomicReference<AutomobiliController> automobiliController = new AtomicReference<>();
    private Stage stageCreateNewKlijent;

    /**
     * Objasnjeno u {@link #initialize(URL, ResourceBundle)}
     */
    private boolean isWeAreInEditMode = false;

    /**
     * Objasnjenjo u {@link #btnZatvoriCreateKlijentUi()}
     */
    private boolean isCloseButtonPresed = false;

    private Klijent klijent;

    private final KlijentDAO klijentDAO = new SQLKlijnetDAO();

    /**
     * Seter koji se koristi u {@link AutomobiliController #btnOpenIzmeniKlijentaUi(ActionEvent)}
     * i prosledjuje {@link Klijent} Objekat koji je za editovanje, a vazno za {@link KlijentDAO#updateKlijent(Klijent)}
     *
     * @param klijent Klijent objekat za EDIT MODE
     * @see Klijent
     * @see KlijentDAO#updateKlijent(Klijent)
     * @see AutomobiliController #btnOpenIzmeniKlijentaUi(ActionEvent)
     */
    public void setKlijent(Klijent klijent) {
        this.klijent = klijent;
    }

    /**
     * Inicijalizacija {@link CreateNewKlijentUiController}-a i to... {@code if (!isWeAreInEditMode)}
     * da li smo u EDIT MODU u koji dolazimo samo iz {@link AutomobiliController}-a jer tamo mozemo da EDITUJEMO
     * {@link Klijent} objekat. Ako smo u EDIT MODU nema potrebe praviti novi {@link Klijent} objekat nego
     * samo radimo {@link #initGUI()}
     *
     * @param location  url Location
     * @param resources res
     * @see CreateNewKlijentUiController
     * @see AutomobiliController
     * @see #initGUI()
     * @see Klijent
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Platform.runLater(() -> {
            try {
                if (!isWeAreInEditMode) {
                    klijent = new Klijent();
                    klijentDAO.insertKlijnet(klijent);
                    System.out.println("NOOO WE ARE NOT IN EDIT MODE");
                }
            } catch (AcrenoException | SQLException e) {
                e.printStackTrace();
            }
            initGUI();
        });
    }

    /**
     * Inicijalizacija GUI KLIJENTA sa proverao da li smo u EDIT MODU ILI NE
     * <p>
     * Ako smo u EDIT MODU moramo da popunimo TF polja sa prosledjenim {@link Klijent} Objektom koji
     * smo dobili iz {@link AutomobiliController #btnOpenIzmeniKlijentaUi(ActionEvent)}
     * <p>
     * Ako nismo u EDIT MODU, posatvljamo ID KLIJENTA na sledici index dobijen iz baze. Ovo moze da
     * koristi ako zelimo odavde da stampamo mozda BAR Code ili tako nesto...
     * <p>
     * {@code klijent.setIdKlijenta(tempIDKlijenta)} -- Kada napravimo novi Klijent obj, pa se slucajno predomislimo
     * ovde postavljamo ID KLIJENTA koji koristimo u {@link #btnObrisiKlijenta()}, koji se koristi u
     * {@link KlijentDAO#deleteKlijent(Klijent)}
     *
     * @see AutomobiliController #btnOpenIzmeniKlijentaUi(ActionEvent)
     * @see Klijent
     */
    private void initGUI() {

        if (isWeAreInEditMode) { //U EDIT MODU SMO
            Platform.runLater(() -> {
                lblKreateOrEditKlijenta.setText("UREĐIVANJE KLIJENTA:");
                txtfIdKlijenta.setText(String.valueOf(klijent.getIdKlijenta()));
                txtfImePrezimeKlijenta.setText(klijent.getImePrezime());
                txtfMobilniTelefonKlijenta.setText(klijent.getTelefonMobilni());
                txtfFiksniTelefonKlijenta.setText(klijent.getTelefonFiksni());
                txrfEmailKlijenta.setText(klijent.getEmail());
                txtfUlicaBrojKlijenta.setText(klijent.getUlicaBroj());
                txtfGradKlijenta.setText(klijent.getMesto());
                txtfPosatanskiBrojKlijenta.setText(klijent.getPostanskiBroj());
                txtaOstaliDetaljiKlijenta.setText(klijent.getOstaliDetalji());
                txtfMaticniBrojKlijenta.setText(klijent.getMaticniBroj());
                txtfBrojLicneKarteKlijenta.setText(klijent.getBrLicneKarte());
                txtfBrojZiroRacunaKlijenta.setText(klijent.getBrojRacuna());
                txtfBankaKlijenta.setText(klijent.getBanka());
                txtfWebSajtKlijenta.setText(klijent.getWeb());
            });

        } else { // NISMO U EDIT MODU
            try {
                ObservableList<Klijent> klijenti = FXCollections.observableArrayList(klijentDAO.findAllKlijents());
                int tempIDKlijenta = klijentDAO.findAllKlijents().get(klijenti.size() - 1).getIdKlijenta();
                klijent.setIdKlijenta(tempIDKlijenta); // ako se predomislimo i hocemo da obrisemo ovde postavljamo ID
                txtfIdKlijenta.setText(String.valueOf(tempIDKlijenta)); // Ubaci ID kojenta u TF "txtfIdKlijenta"
            } catch (AcrenoException | SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Inicijalizacija {@link Klijent} objekta i UPDATE {@link KlijentDAO#updateKlijent(Klijent)}.
     * <p>
     * {@code if (!isCloseButtonPresed)} je tu da bi se proverilo da li je
     * kliknuto dugme EXIT {@link #btnCloseCreateEditKlijent}, a nismo sacuvali objekat {@link Klijent}
     * pa ga pametno cuvamo u {@link #btnZatvoriCreateKlijentUi()} i to bi trebalo da ide bez
     * Dijaloga, jer se odmah i zatvara UI. Pa ako je to TRUE, onda nemoj koristiti Dijalog.
     *
     * @throws AcrenoException malo bolje ojasnjenje greske
     * @see KlijentDAO#updateKlijent(Klijent)
     * @see #btnCloseCreateEditKlijent
     * @see #btnZatvoriCreateKlijentUi()
     * @see Klijent
     */
    @FXML private void saveKlijent() throws AcrenoException {
        try {
            //klijent = new Klijent();
            klijent.setIdKlijenta(Integer.parseInt(txtfIdKlijenta.getText()));
            klijent.setImePrezime(txtfImePrezimeKlijenta.getText());
            klijent.setMesto(txtfGradKlijenta.getText());
            klijent.setPostanskiBroj(txtfPosatanskiBrojKlijenta.getText());
            klijent.setTelefonMobilni(txtfMobilniTelefonKlijenta.getText());
            klijent.setTelefonFiksni(txtfFiksniTelefonKlijenta.getText());
            klijent.setEmail(txrfEmailKlijenta.getText());
            klijent.setUlicaBroj(txtfUlicaBrojKlijenta.getText());
            klijent.setOstaliDetalji(txtaOstaliDetaljiKlijenta.getText());
            klijent.setMaticniBroj(txtfMaticniBrojKlijenta.getText());
            klijent.setBrLicneKarte(txtfBrojLicneKarteKlijenta.getText());
            klijent.setBrojRacuna(txtfBrojZiroRacunaKlijenta.getText());
            klijent.setBanka(txtfBankaKlijenta.getText());
            klijent.setWeb(txtfWebSajtKlijenta.getText());

            klijentDAO.updateKlijent(klijent);

            if (!isCloseButtonPresed) {
                GeneralUiUtility.alertDialogBox(
                        Alert.AlertType.INFORMATION,
                        "USPESNO SACUVAN KLIJENT: " + klijent.getIdKlijenta(),
                        "EDITOVANJE KLIJENTA: " + klijent.getImePrezime(),
                        "Uspesno sačuvane izmene Klijenta: " + klijent.getImePrezime() + " !"
                );
            }
        } catch (AcrenoException | SQLException e) {

            GeneralUiUtility.alertDialogBox(
                    Alert.AlertType.INFORMATION,
                    "GRESKA U CUVANJU KLIJENTA",
                    "EDITOVANJE KLIJENTA",
                    "Niste sacuvali  KLIJENTA br." + klijent.getImePrezime()
                            + ", Kontatiraj Administratora sa porukom: \n"
                            + e.getMessage()
            );
            throw new AcrenoException("Greska iz CREATE NEW CLINET CONTROLORA\n" + e.getMessage());
        }

    }

    /**
     * Seter metoda koja se koristi u {@link AutomobiliController#setAutoServisController(AutoServisController, Stage)}-u
     * Takodje se prosledjuje i STAGE ako bude zatrebalo, a iz {@link AutomobiliController #btnOpenIzmeniKlijentaUi()}-a
     * <p>
     * {@code isWeAreInEditMode = true} nas obavestava da smo u EDIT MODU iz
     * {@link AutoServisController#btnOpenNoviKlijentGui(ActionEvent)}
     *
     * @param autmobilController    referenca ka automobil kontroloru
     * @param stageCreateNewKlijent refereca ka automobil Stage-u
     * @see AutomobiliController
     */
    public void setAutmobilController(AutomobiliController autmobilController, Stage stageCreateNewKlijent) {
        this.automobiliController.set(autmobilController);
        this.stageCreateNewKlijent = stageCreateNewKlijent;
        isWeAreInEditMode = true;
    }

    /**
     * Brisanje Klijnet Objekta
     * ID KLIJENTA dobijamo iz {@link #initGUI()} metode u kodu: {@code klijent.setIdKlijenta(tempIDKlijenta)}
     *
     * @throws AcrenoException malo bolje objasnjenje greske
     * @throws SQLException    SQL Exception
     * @see KlijentDAO#deleteKlijent(Klijent)
     * @see #initGUI()
     */
    @FXML private void btnObrisiKlijenta() throws AcrenoException, SQLException {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Potvrda brisanja Klijenta: " + klijent.getIdKlijenta());
        alert.setHeaderText("Brisanje Klijenta: " + klijent.getImePrezime());
        alert.setContentText("Da li ste sigurni da želite da obrišete " + klijent.getImePrezime() + " iz baze podataka ?");
        ((Stage) alert.getDialogPane().getScene().getWindow()).getIcons().add(new Image(Constants.APP_ICON));
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent()) {
            if (result.get() == ButtonType.OK) {
                klijentDAO.deleteKlijent(klijent);
                btnCloseCreateEditKlijent.fire();
            }
        }
    }

    /**
     * Zatvori prozor ADD OR EDIT KLIJENT i pametno proveri da li smo odustali ili slucano stisnuli EXIT pre cuvanja.
     * ako udjemo u ovj GUI i ne unesemo Ime i Prezime onda se predpostavlja da smo odustali od unosa Klijenta.
     * Pa shodno tome proveravamo da li ima nesto u TFu Ime prezime ako nema onda obrisi Ojekat {@link Klijent} koji
     * je ranije napravljen u {@link #initialize(URL, ResourceBundle)}.
     * <p>
     * {@code isCloseButtonPresed = true} boolean promenjiva je tu da se ne bi pri izlasku pojavio Dialog koji je
     * u {@link #saveKlijent()} je pametno izlazimo i cuvamo Klijent objekat u bazu.
     *
     * @see #initialize(URL, ResourceBundle)
     * @see Klijent
     */
    @FXML private void btnZatvoriCreateKlijentUi() throws AcrenoException, SQLException {
        isCloseButtonPresed = true;
        if (txtfImePrezimeKlijenta.getText().isEmpty()) { // Cuvaj Klijenta ako makar ima nesto u TFu ime i prezime
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Potvrda brisanja Klijenta");
            alert.setHeaderText("Niste uneli Ime i Prezime Klijenta, da li možemo da ga obrišemo?");
            alert.setContentText("Klijent je već napravljen u bazi, ali niste uneli ime i prezime pa predpostavljamo da " +
                    "možemo da obrišemo ovog klijenta.");
            ((Stage) alert.getDialogPane().getScene().getWindow()).getIcons().add(new Image(Constants.APP_ICON));
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent()) {
                if (result.get() == ButtonType.OK) {
                    klijentDAO.deleteKlijent(klijent);
                    btnCloseCreateEditKlijent.fireEvent(new WindowEvent(stageCreateNewKlijent, WindowEvent.WINDOW_CLOSE_REQUEST));
                }
            }
        } else {
            saveKlijent(); //Cuvamo kljijenta ako ima nesto u TXTFu Ime Prezime
            btnCloseCreateEditKlijent.fireEvent(new WindowEvent(stageCreateNewKlijent, WindowEvent.WINDOW_CLOSE_REQUEST));
        }

    }
}

