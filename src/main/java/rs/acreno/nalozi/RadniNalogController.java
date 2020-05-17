package rs.acreno.nalozi;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import rs.acreno.automobil.Automobil;
import rs.acreno.automobil.AutomobiliController;
import rs.acreno.autoservis.AutoServisController;
import rs.acreno.klijent.Klijent;
import rs.acreno.nalozi.print_nalozi.PrintNaloziController;
import rs.acreno.system.constants.Constants;
import rs.acreno.system.exeption.AcrenoException;
import rs.acreno.system.util.GeneralUiUtility;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.ResourceBundle;

public class RadniNalogController implements Initializable {

    private static final Logger logger = Logger.getLogger(RadniNalogController.class);


    @FXML private Stage automobilStage;

    @FXML private Button btnObrisiRadniNalogAction;
    @FXML private Button btnCloseRadniNalog;

    @FXML private TextField txtfIdRadnogNaloga;
    @FXML private TextField txtfRegOznaka;
    @FXML private TextField txtfKlijent;
    @FXML private DatePicker datePickerDatum;
    @FXML private TextField txtfVreme;
    @FXML private TextField txtfKilometraza;
    @FXML private TextArea txtAreaDetaljiStranke;
    @FXML private TextArea txtAreDetaljiServisera;

    //Inicijalizacija Radnog Naloga Objekta
    private final RadniNalogDAO radniNalogDAO = new SQLRadniNalogDAO();

    //INIT GUI FIELDS
    private int idAutomobila;

    //RADNI NALOG STAFF OBJECT
    private RadniNalog noviRadniNalog;

    /**
     * Bitna promenjiva jer se sve bazira na Broju Radnog Naloga ili ti ID Radnog Naloga
     */
    private int brojRadnogNaloga;

    private AutomobiliController automobiliController;

    /**
     * Promenjiva kojom se pristupaju promenjive iz ovog kontrolora, a u {@link PrintNaloziController}
     */
    private Stage stagePrintRadniNalog;

    private boolean ifWeAreFromBtnCloseFaktureAction = true;

    public int getBrojRadnogNaloga() {
        return brojRadnogNaloga;
    }

    public String getKlijent() {
        return txtfKlijent.getText();
    }

    public String getTxtfRegOznaka() {
        return txtfRegOznaka.getText();
    }

    public String getDatum() {
        return GeneralUiUtility.formatDateForUs(datePickerDatum.getValue());
    }

    public String getVreme() {
        return txtfVreme.getText();
    }

    public String getKilometraza() {
        return txtfKilometraza.getText();
    }

    public String getDetaljiStranke() {
        return txtAreaDetaljiStranke.getText();
    }

    public String getDetaljiServisera() {
        return txtAreDetaljiServisera.getText();
    }

    public void setEditRadniNalog(RadniNalog noviRadniNalog) {
        this.noviRadniNalog = noviRadniNalog;
    }

    public void setBrojRadnogNaloga(int brojRadnogNaloga) {
        this.brojRadnogNaloga = brojRadnogNaloga;
    }

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
     * Inicijalizacija {@link RadniNalogController}-a i provera da li smo u EDIT MODU ili ne
     * Postavljanje danasnjeg datuma u datePickeru
     *
     * @param location Location if we nee in some case
     * @param resource resource if we nee in some case
     */
    @Override public void initialize(URL location, ResourceBundle resource) {
        Platform.runLater(() -> {
            // Ako je Radni Nalog u edit modu nemoj praviti novi Radni Nalog nego prosledi RN koji je za izmenu
            if (automobiliController.isRadniNalogInEditMode()) { //TRUE
                newOrEditRadniNalog(true);

            } else { //Nismo u Edit Modu (FALSE)
                //Datum
                LocalDate now = LocalDate.now();
                datePickerDatum.setValue(now); //Postavi danasnji datum RNa u datePiceru
                newOrEditRadniNalog(false); // Nismo u edit modu pa napravi novi RN
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
        ObservableList<Automobil> automobili = automobiliController.getAutomobil(); //Get AUTOMOBIL from automobiliController #Filtered
        ObservableList<Klijent> klijenti = automobiliController.getKlijenti(); //Get KLIJENTA from automobiliController #Filtered
        idAutomobila = automobili.get(0).getIdAuta(); //Moze jer je samo jedan Automobil
        String regOznakaAutomobila = automobili.get(0).getRegOznaka();//Moze jer je samo jedan Automobil
        String imePrezimeKlijenta = klijenti.get(0).getImePrezime();//Moze jer je samo jedan Klijent
        //Popunjavanje GUIa
        txtfKlijent.setText(imePrezimeKlijenta);
        txtfRegOznaka.setText(regOznakaAutomobila);
        //Formatiranje Vremena
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        txtfVreme.setText(dtf.format(now));
    }

    /**
     * Pravljenje ili Editovanje {@link RadniNalog}-a sa FKom {@link Automobil} objekata
     * <p>
     * Prvo inicijalizujemo GUI {@link #initGUI()} bez obzira da li je EDIT mode ili ne.
     * <p>
     * EDIT MODE STATUS DOBIJAMO IZ {@link AutomobiliController#btnOpenNoviRadniNalog()} u {@code isRadniNalogInEditMode}
     * <p>
     * Ako smo u EDIT modu(TRUE) {@code if (isInEditMode)} ne treba da pravimo {@link RadniNalog} objekat
     * nego smo ga prosledili iz {@link AutomobiliController#btnOpenNoviRadniNalog()}
     * <p>
     * Ako nismo u EDIT MODU(FALSE), pravimo novi objekat {@link RadniNalog} i bitno da se odredi koji
     * je sledeci {@link #brojRadnogNaloga}. Ovde je bio problem jer kada se obrise RADNI NALOG ID se pomera za jedan
     * iako je obrisan u bazi.
     * <p>
     * {@code GeneralUiUtility.fromStringDate} formatiramo datum za Serbiu, a u {@link GeneralUiUtility#fromStringDate}
     *
     * @param isInEditMode da li smo u Edit Modu
     * @see AutomobiliController#btnOpenNoviRadniNalog()
     * @see GeneralUiUtility#fromStringDate(String)
     */
    private void newOrEditRadniNalog(boolean isInEditMode) {
        initGUI(); //Inicijalizacija podataka za novi radni nalog bez obzira na edit mode
        if (isInEditMode) {
            txtfIdRadnogNaloga.setText(String.valueOf(noviRadniNalog.getIdRadnogNaloga()));
            datePickerDatum.setValue(GeneralUiUtility.fromStringDate(noviRadniNalog.getDatum()));
            txtfVreme.setText(noviRadniNalog.getVreme());
            txtfKilometraza.setText(noviRadniNalog.getKilometraza());
            txtAreaDetaljiStranke.setText(noviRadniNalog.getDetaljiStranke());
            txtAreDetaljiServisera.setText(noviRadniNalog.getDetaljiServisera());
        } else {
            noviRadniNalog = new RadniNalog();
            noviRadniNalog.setIdRadnogNaloga(brojRadnogNaloga);
            noviRadniNalog.setIdAutomobila(idAutomobila);
            noviRadniNalog.setKilometraza(txtfKilometraza.getText());
            noviRadniNalog.setDatum(datePickerDatum.getValue().toString());
            try {
                radniNalogDAO.insertRadniNalog(noviRadniNalog);
                //Inicijalizacija broja fakture MORA DA IDE OVDE
                ObservableList<RadniNalog> radniNalozi = FXCollections.observableArrayList(radniNalogDAO.findAllRadniNalog());
                brojRadnogNaloga = radniNalogDAO.findAllRadniNalog().get(radniNalozi.size() - 1).getIdRadnogNaloga();
                txtfIdRadnogNaloga.setText(String.valueOf(brojRadnogNaloga));

            } catch (AcrenoException | SQLException e) {
                e.printStackTrace();
            }
        }
    }


    // ******************* BUTTON ACTION STAFF*********************

    /**
     * UPDATE Radnog Naloga kada se nesto promeni u njemu...(Datum...Vreme...Detalji...)
     * <p>
     * Setuju se svi podaci za izmenjen Radn Nalog pokupljeni iz TF-ova
     * Zatim se radi update sa {@link RadniNalogDAO#updateRadniNalog(RadniNalog)}
     * <p>
     * {@code GeneralUiUtility.formatDateForUs} formatiramo datum za Serbiu, a u {@link GeneralUiUtility#formatDateForUs}
     *
     * @see RadniNalogDAO#updateRadniNalog(RadniNalog)
     * @see GeneralUiUtility#alertDialogBox(Alert.AlertType, String, String, String)
     * @see GeneralUiUtility#formatDateForUs
     */
    @FXML
    private void btnSacuvajRadniNalogAction() {
        try {
            //UPDATE NOVOG RNa SA NOVIM VREDNOSTIMA ZATO OVDE REDEFINISEMO NOVI RADNI NALOG
            noviRadniNalog.setIdRadnogNaloga(brojRadnogNaloga);
            noviRadniNalog.setIdAutomobila(idAutomobila);
            noviRadniNalog.setKilometraza(txtfKilometraza.getText());
            noviRadniNalog.setDatum(GeneralUiUtility.formatDateForUs(datePickerDatum.getValue()));
            noviRadniNalog.setVreme(txtfVreme.getText());
            noviRadniNalog.setDetaljiStranke(txtAreaDetaljiStranke.getText());
            noviRadniNalog.setDetaljiServisera(txtAreDetaljiServisera.getText());
            radniNalogDAO.updateRadniNalog(noviRadniNalog);
            if (ifWeAreFromBtnCloseFaktureAction) {
                GeneralUiUtility.alertDialogBox(
                        Alert.AlertType.CONFIRMATION,
                        "USPESNO SACUVAN RADNI NALOG",
                        "EDITOVANJE RADNOG NALOGA",
                        "Uspesno ste sacuvali RN pod brojem: " + brojRadnogNaloga
                );
            }

        } catch (SQLException | AcrenoException throwables) {
            throwables.printStackTrace();
            GeneralUiUtility.alertDialogBox(
                    Alert.AlertType.CONFIRMATION,
                    "GRESKA U CUVANJU RADNOG NALOGA",
                    "EDITOVANJE RADNOG NALOGA",
                    "Niste sacuvali  Radni nalog br. " + brojRadnogNaloga + ", Kontatiraj Administratora sa porukom: \n"
                            + throwables.getMessage()
            );
        }
    }

    /**
     * Brisanje {@link RadniNalog} Objekta jer smo odustali i li vise necemo ovaj RN.
     * <p>
     * Brise se preko {@link RadniNalogDAO#deleteRadniNalog(int)} (int)} ID racuna koji se dobija iz {@link #brojRadnogNaloga}.
     * <p>
     * {@link RadniNalogController} UIa i fire WINDOW_CLOSE_REQUEST on {@link AutomobiliController}
     * zbog refresha tabele Radnog Naloga u {@link AutomobiliController}-u, a implemtira se u
     * {@link AutomobiliController#btnOpenNoviRadniNalog()}
     *
     * @param actionEvent zatvaranje UIa
     * @see AutomobiliController#btnOpenNoviRadniNalog()
     * @see RadniNalogDAO#deleteRadniNalog(int)
     */
    @FXML
    private void btnObrisiRadniNalogAction(@NotNull ActionEvent actionEvent) {
        try {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Potvrda brisanja Radnog Naloga: " + brojRadnogNaloga);
            alert.setHeaderText("Brisanje Radnog Naloga sa ID brojem: " + brojRadnogNaloga);
            alert.setContentText("Da li ste sigurni da želite da obrišete Radni Nalog br: " + brojRadnogNaloga);
            ((Stage) alert.getDialogPane().getScene().getWindow()).getIcons().add(new Image(Constants.APP_ICON));
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent()) {
                if (result.get() == ButtonType.OK) {
                    radniNalogDAO.deleteRadniNalog(brojRadnogNaloga);
                    btnObrisiRadniNalogAction.fireEvent(new WindowEvent(automobilStage, WindowEvent.WINDOW_CLOSE_REQUEST));
                    ((Stage) (((Button) actionEvent.getSource()).getScene().getWindow())).close();
                }
            }
        } catch (AcrenoException | SQLException acrenoException) {
            acrenoException.printStackTrace();
        }
    }

    /**
     * Inicijalizacija {@link PrintNaloziController}, a implementira se {@link #initialize}
     *
     * @param fxmlLoader prosledjivanje FXMLoadera {@link PrintNaloziController} - u
     * @see PrintNaloziController
     */
    private void initUiPrintControler(@NotNull FXMLLoader fxmlLoader) {
        PrintNaloziController printNaloziController = fxmlLoader.getController();
        printNaloziController.setRadniNalogController(this, stagePrintRadniNalog);
        //uiPrintRacuniControler.setIdRacuna(Integer.parseInt(txtFidRacuna.getText()));
    }

    /**
     * Otvaranje Print Fakture {@link PrintNaloziController}
     * Inicijalizacija Print Controlora i prosledjivanje id Racuna {@link #initUiPrintControler}
     * Na ovom mestu je zato sto je ovo poslednja pozicija koja se radi pre otvaranja Print Cotrolora
     *
     * @see #initUiPrintControler(FXMLLoader)
     * @see PrintNaloziController
     */
    @FXML
    private void btnPrintAction() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(Constants.PRINT_RADNI_NALOG_UI_VIEW_URI));
            stagePrintRadniNalog = new Stage();
            stagePrintRadniNalog.initModality(Modality.APPLICATION_MODAL);
            stagePrintRadniNalog.initStyle(StageStyle.UNDECORATED);
            stagePrintRadniNalog.setScene(new Scene(loader.load()));

            initUiPrintControler(loader); //Inicijalizacija Porint Controlora i prosledjivanje id Racuna

            stagePrintRadniNalog.showAndWait();//Open Stage and wait

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Zatvaranje {@link RadniNalogController} UIa i cuvanje {@link RadniNalog} objekta.
     * <p>
     * Ovde je implementiran i "SAMART CLOSE" jer ako se otvori novi Radni Nalog ona je odmah ubacena u DB,
     * po ako se ne unese ni {@link #txtAreaDetaljiStranke} ili {@link #txtAreDetaljiServisera} txt, onda predpostavljamo
     * da smo odustali i samim tim brisemo iz DBa!
     *
     * @param actionEvent event for hide scene {@link RadniNalogController}
     * @throws AcrenoException malo bolje objasnjenje
     * @throws SQLException    problem u DBu
     * @author Dejan Cugalj
     */
    @FXML
    private void btnCloseFaktureAction(@NotNull ActionEvent actionEvent) throws AcrenoException, SQLException {
        ifWeAreFromBtnCloseFaktureAction = false;
        // Pametno bisanje racuna ako je samo otvoren novi i nema Unosa u txtF
        if (txtAreaDetaljiStranke.getText().equals("") || txtAreDetaljiServisera.getText().equals("")) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("SMART brisanja Radnog Naloga");
            alert.setHeaderText("Niste uneli ''Detalje stranke'' ni ''Detalje Servisera'', da li možemo Radnog Naloga da obrišemo?");
            alert.setContentText("Radnog Naloga je već napravljen u bazi, ali niste uneli ''Detalji Stranke''" +
                    " niti ''Detalje Servisera'' pa predpostavljamo da " +
                    "možemo da obrišemo ovaj RadnoiNalog?");
            ((Stage) alert.getDialogPane().getScene().getWindow()).getIcons().add(new Image(Constants.APP_ICON));
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent()) {
                if (result.get() == ButtonType.OK) {
                    radniNalogDAO.deleteRadniNalog(brojRadnogNaloga);
                    btnCloseRadniNalog.fireEvent(new WindowEvent(stagePrintRadniNalog, WindowEvent.WINDOW_CLOSE_REQUEST));
                    ((Stage) (((Button) actionEvent.getSource()).getScene().getWindow())).close();
                }
            }
        } else {
            btnSacuvajRadniNalogAction(); //Cuvamo Radni Nalog ako ima nesto u TXTFu Detalji Servisera
            btnCloseRadniNalog.fireEvent(new WindowEvent(stagePrintRadniNalog, WindowEvent.WINDOW_CLOSE_REQUEST));
        }
    }
}
