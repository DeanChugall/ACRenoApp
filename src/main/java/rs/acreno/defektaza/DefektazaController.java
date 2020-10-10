package rs.acreno.defektaza;

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
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import org.jetbrains.annotations.NotNull;
import rs.acreno.automobil.Automobil;
import rs.acreno.automobil.AutomobiliController;
import rs.acreno.autoservis.AutoServisController;
import rs.acreno.defektaza.print_defektaza.PrintDefektazaController;
import rs.acreno.klijent.Klijent;
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

public class DefektazaController implements Initializable {

    @FXML private Button btnObrisiDefektazubtnObrisiDefektazu;
    @FXML private Button btnCloseDefektaza;
    @FXML private TextField txtfRegOznaka;
    @FXML private TextField txtfKlijent;
    @FXML private Button btnObrisiDefektazu;
    @FXML private TextField txtfIdDefektaze;
    @FXML private TextField txtfKilometraza;
    @FXML private TextField txtfVreme;
    @FXML private TextArea txtAreaOpisDefektaze;
    @FXML private TextArea txtAreOstaliDetaljiDefektaze;

    @FXML private BorderPane defektazaUiBorderPane;
    @FXML private DatePicker datePickerDatum;

    private AutomobiliController automobiliController;
    private Stage stageDefektaza;

    //Inicijalizacija Radnog Naloga Objekta
    private final DefektazaDAO defektazaDAO = new SQLDefektazaDAO();

    /**
     * Bitna promenjiva jer se sve bazira na Broju Defektaze ili ti ID Defektaze
     */
    private int brojDefektaze;

    //INIT GUI FIELDS
    private int idAutomobila;


    //RADNI NALOG STAFF OBJECT
    private Defektaza novaDefektaza;

    private boolean ifWeAreFromBtnCloseDefektazaAction = true;


    public TextField getTxtfKlijent() {
        return txtfKlijent;
    }

    public TextField getTxtfRegOznaka() {
        return txtfRegOznaka;
    }

    public DatePicker getDatePickerDatum() {
        return datePickerDatum;
    }

    public TextField getTxtfIdDefektaze() {
        return txtfIdDefektaze;
    }

    public TextField getTxtfKilometraza() {
        return txtfKilometraza;
    }

    public TextField getTxtfVreme() {
        return txtfVreme;
    }

    public TextArea getTxtAreaOpisDefektaze() {
        return txtAreaOpisDefektaze;
    }

    public TextArea getTxtAreOstaliDetaljiDefektaze() {
        return txtAreOstaliDetaljiDefektaze;
    }


    /**
     * Seter metoda koja se koristi u {@link AutomobiliController#setAutoServisController(AutoServisController, Stage)}-u
     * Takodje se prosledjuje i STAGE ako bude zatrebalo, a iz {@link AutomobiliController #btnOpenFakturaUi()}-a
     * Prosledjeni Automobil i Klijent objekti su iz {@link AutomobiliController}, a impl u {@link #initGUI()}
     *
     * @param autmobilController referenca ka automobil kontroloru
     * @param stageDefektaza     refereca ka automobil Stage-u
     * @see AutomobiliController
     */
    public void setAutmobilController(AutomobiliController autmobilController, Stage stageDefektaza) {
        this.automobiliController = autmobilController;
        this.stageDefektaza = stageDefektaza;
    }

    public void setBrojDefektaze(int brojDefektaze) {
        this.brojDefektaze = brojDefektaze;
    }

    public void setEditDefektaza(Defektaza defektaza) {
        this.novaDefektaza = defektaza;
    }

    /**
     * Inicijalizacija {@link DefektazaController}-a i provera da li smo u EDIT MODU ili ne
     * Postavljanje danasnjeg datuma u datePickeru
     *
     * @param location  Location if we nee in some case
     * @param resources resource if we nee in some case
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Platform.runLater(() -> {
            // Ako je DEFEKTAZA u edit modu nemoj praviti novu DEFEKTAZU nego prosledi DF koji je za izmenu
            if (automobiliController.isDefektazaInEditMode()) { //TRUE
                newOrEditDefektaza(true);
            } else { //Nismo u Edit Modu (FALSE)
                //Datum
                LocalDate now = LocalDate.now();
                datePickerDatum.setValue(now); //Postavi danasnji datum Defektaze u datePiceru
                newOrEditDefektaza(false); // Nismo u edit modu pa napravi novi racun
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
     * Pravljenje ili Editovanje {@link Defektaza}-a sa FKom {@link Automobil} objekata
     * <p>
     * Prvo inicijalizujemo GUI {@link #initGUI()} bez obzira da li je EDIT mode ili ne.
     * <p>
     * EDIT MODE STATUS DOBIJAMO IZ {@link AutomobiliController#btnOpenNoviRadniNalog()} u {@code isDefektazaInEditMode}
     * <p>
     * Ako smo u EDIT modu(TRUE) {@code if (isInEditMode)} ne treba da pravimo {@link Defektaza} objekat
     * nego smo ga prosledili iz {@link AutomobiliController#btnOpenDefektaza()}
     * <p>
     * Ako nismo u EDIT MODU(FALSE), pravimo novi objekat {@link Defektaza} i bitno da se odredi koji
     * je sledeci {@link #brojDefektaze}. Ovde je bio problem jer kada se obrise DEFEKTAZA ID se pomera za jedan
     * iako je obrisan u bazi.
     * <p>
     * {@code GeneralUiUtility.fromStringDate} formatiramo datum za Serbiu, a u {@link GeneralUiUtility#fromStringDate}
     *
     * @param isInEditMode da li smo u Edit Modu
     * @see AutomobiliController#btnOpenNoviRadniNalog()
     * @see GeneralUiUtility#fromStringDate(String)
     */
    private void newOrEditDefektaza(boolean isInEditMode) {
        initGUI(); //Inicijalizacija podataka za novu DEFEKTAZU bez obzira na edit mode

        if (isInEditMode) {
            txtfIdDefektaze.setText(String.valueOf(novaDefektaza.getIdDefektaze()));
            txtfKilometraza.setText(novaDefektaza.getKilometraza());
            datePickerDatum.setValue(GeneralUiUtility.fromStringDate(novaDefektaza.getDatumDefektaze()));
            txtfVreme.setText(novaDefektaza.getVreme());
            txtAreaOpisDefektaze.setText(novaDefektaza.getOpisDefektaze());
            txtAreOstaliDetaljiDefektaze.setText(novaDefektaza.getOstaliDetaljiDefektaze());
        } else {
            novaDefektaza = new Defektaza();
            novaDefektaza.setIdDefektaze(brojDefektaze);
            novaDefektaza.setIdAuta(idAutomobila);
            novaDefektaza.setKilometraza(txtfKilometraza.getText());
            novaDefektaza.setDatumDefektaze(datePickerDatum.getValue().toString());
            try {
                defektazaDAO.insertDefektaza(novaDefektaza);
                //Inicijalizacija broja fakture MORA DA IDE OVDE
                ObservableList<Defektaza> defektaze = FXCollections.observableArrayList(defektazaDAO.findAllDefektaza());
                brojDefektaze = defektazaDAO.findAllDefektaza().get(defektaze.size() - 1).getIdDefektaze();
                txtfIdDefektaze.setText(String.valueOf(brojDefektaze));

            } catch (AcrenoException | SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // ******************* BUTTON ACTION STAFF*********************

    /**
     * UPDATE Defektaze kada se nesto promeni u njemu...(Datum...Vreme...Detalji...)
     * <p>
     * Setuju se svi podaci za izmenjena Defektaza pokupljeni iz TF-ova u pobjekat {@link #novaDefektaza}
     * Zatim se radi update sa {@link DefektazaDAO#updateDefektaza(Defektaza)}
     * {@code GeneralUiUtility.formatDateForUs} formatiramo datum za Serbiu, a u {@link GeneralUiUtility#formatDateForUs}
     *
     * @see DefektazaDAO#updateDefektaza(Defektaza)
     * @see GeneralUiUtility#alertDialogBox(Alert.AlertType, String, String, String)
     * @see GeneralUiUtility#formatDateForUs
     */
    @FXML
    private void btnSacuvajDefektazuAction() {
        try {
            //UPDATE NOVE DEFEKTAZE SA NOVIM VREDNOSTIMA ZATO OVDE REDEFINISEMO NOVU DEFEKTAZU
            novaDefektaza.setIdDefektaze(brojDefektaze);
            novaDefektaza.setIdAuta(idAutomobila);
            novaDefektaza.setKilometraza(txtfKilometraza.getText());
            novaDefektaza.setDatumDefektaze(GeneralUiUtility.formatDateForUs(datePickerDatum.getValue()));
            novaDefektaza.setVreme(txtfVreme.getText());
            novaDefektaza.setOpisDefektaze(txtAreaOpisDefektaze.getText());
            novaDefektaza.setOstaliDetaljiDefektaze(txtAreOstaliDetaljiDefektaze.getText());
            defektazaDAO.updateDefektaza(novaDefektaza);
            if (ifWeAreFromBtnCloseDefektazaAction) {
                GeneralUiUtility.alertDialogBox(
                        Alert.AlertType.CONFIRMATION,
                        "USPESNO SACUVANA DEFEKTAZA",
                        "EDITOVANJE DEFEKTAZE",
                        "Uspesno ste sacuvali DEF pod brojem: " + brojDefektaze
                );
            }

        } catch (SQLException | AcrenoException throwables) {
            throwables.printStackTrace();
            GeneralUiUtility.alertDialogBox(
                    Alert.AlertType.CONFIRMATION,
                    "GRESKA U CUVANJU DEFEKTAZE",
                    "EDITOVANJE DEFEKTAZE",
                    "Niste sacuvali  DEFEKTAZU br. " + brojDefektaze + ", Kontatiraj Administratora sa porukom: \n"
                            + throwables.getMessage()
            );
        }

    }

    /**
     * Brisanje {@link Defektaza} Objekta jer smo odustali i li vise necemo ovau Defektazu
     * <p>
     * Brise se preko {@link DefektazaDAO#deleteDefektaza(int)} ID racuna koji se dobija iz {@link #brojDefektaze}.
     * <p>
     * {@link DefektazaController} UIa i fire WINDOW_CLOSE_REQUEST on {@link AutomobiliController}
     * zbog refresha tabele Defektaze u {@link AutomobiliController}-u, a implemtira se u
     * {@link AutomobiliController#btnOpenDefektaza()}
     *
     * @param actionEvent zatvaranje UIa
     * @see AutomobiliController#btnOpenDefektaza() ()
     * @see DefektazaDAO#deleteDefektaza(int)
     */
    @FXML
    private void btnObrisiDefektazuAction(@NotNull ActionEvent actionEvent) {
        try {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Potvrda brisanja Defektaže: " + brojDefektaze);
            alert.setHeaderText("Brisanje Defektaže: " + brojDefektaze);
            alert.setContentText("Da li ste sigurni da želite da obrišete Defektažu br: " + brojDefektaze);
            ((Stage) alert.getDialogPane().getScene().getWindow()).getIcons().add(new Image(Constants.APP_ICON));
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent()) {
                if (result.get() == ButtonType.OK) {
                    defektazaDAO.deleteDefektaza(brojDefektaze);
                    btnObrisiDefektazu.fireEvent(new WindowEvent(stageDefektaza, WindowEvent.WINDOW_CLOSE_REQUEST));
                    ((Stage) (((Button) actionEvent.getSource()).getScene().getWindow())).close();
                }
            }
        } catch (AcrenoException | SQLException acrenoException) {
            acrenoException.printStackTrace();
        }
    }

    /**
     * Inicijalizacija {@link PrintDefektazaController}, a implementira se {@link #initialize}
     *
     * @param fxmlLoader prosledjivanje FXMLoadera {@link PrintDefektazaController} - u
     * @see PrintDefektazaController
     */
    private void initUiPrintControler(@NotNull FXMLLoader fxmlLoader) {
        PrintDefektazaController printDefektazaController = fxmlLoader.getController();
        printDefektazaController.setDefektazaController(this, stageDefektaza);
        //uiPrintRacuniControler.setIdRacuna(Integer.parseInt(txtFidRacuna.getText()));
    }

    /**
     * Otvaranje Print Fakture {@link PrintDefektazaController}
     * Inicijalizacija Print Defektaza Controlora i prosledjivanje id Racuna {@link #initUiPrintControler}
     * Na ovom mestu je zato sto je ovo poslednja pozicija koja se radi pre otvaranja Print Defektaza Cotrolora
     *
     * @param actionEvent if we need in some case
     * @see #initUiPrintControler(FXMLLoader)
     * @see PrintDefektazaController
     */
    public void btnPrintPregledDefektaza(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(Constants.PRINT_DEFEKTAZA_UI_VIEW_URI));
            stageDefektaza = new Stage();
            stageDefektaza.initModality(Modality.APPLICATION_MODAL);
            stageDefektaza.initStyle(StageStyle.UNDECORATED);
            stageDefektaza.setScene(new Scene(loader.load()));

            initUiPrintControler(loader); //Inicijalizacija Porint Controlora i prosledjivanje id Racuna

            stageDefektaza.showAndWait();//Open Stage and wait

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Zatvaranje {@link DefektazaController} UIa i cuvanje {@link Defektaza} objekta.
     * <p>
     * Ovde je implementiran i "SAMART CLOSE" jer ako se otvori nova Defektaza ona je odmah ubacena u DB,
     * po ako se ne unese ni {@link #txtAreaOpisDefektaze} ili {@link #txtAreOstaliDetaljiDefektaze} txt,
     * onda predpostavljamo da smo odustali i samim tim brisemo iz DBa!
     * <p>
     * Da bi se refresovala tabela Racuni u {@link AutomobiliController}-u potrebno je pozvati
     * {@code WindowEvent.WINDOW_CLOSE_REQUEST} koji je implementiran u {@link AutomobiliController#btnOpenDefektaza()} ()}
     *
     * @param actionEvent event for hide scene {@link DefektazaController}
     * @throws AcrenoException malo bolje objasnjenje
     * @throws SQLException    problem u DBu
     * @author Dejan Cugalj
     * @see AutomobiliController#btnOpenDefektaza()
     * @see DefektazaController
     * @see Defektaza
     */
    @FXML
    private void btnCloseDefektazaAction(@NotNull ActionEvent actionEvent) throws AcrenoException, SQLException {
        ifWeAreFromBtnCloseDefektazaAction = false;
        if (txtAreOstaliDetaljiDefektaze.getText().equals("")) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("SMART brisanja Defektaže");
            alert.setHeaderText("Niste uneli ''OPIS DEFEKTAZE'' ni ''OSTALI DETALJI DEFELTAZE''," +
                    " da li možemo da obrišemo ovu Defektažu?");
            alert.setContentText("Defektaža je već napravljen u bazi, ali niste uneli ''Detalji Stranke''" +
                    " niti ''Detalje Servisera'' pa predpostavljamo da " +
                    "možemo da obrišemo ovau Defektažu?");
            ((Stage) alert.getDialogPane().getScene().getWindow()).getIcons().add(new Image(Constants.APP_ICON));
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent()) {
                if (result.get() == ButtonType.OK) {
                    defektazaDAO.deleteDefektaza(brojDefektaze);
                    btnCloseDefektaza.fireEvent(new WindowEvent(stageDefektaza, WindowEvent.WINDOW_CLOSE_REQUEST));
                    ((Stage) (((Button) actionEvent.getSource()).getScene().getWindow())).close();
                }
            }
        } else {
            btnSacuvajDefektazuAction(); //Cuvamo Defektayu ako ima nesto u TXTFu Opis Defektaye Servisera
            btnCloseDefektaza.fireEvent(new WindowEvent(stageDefektaza, WindowEvent.WINDOW_CLOSE_REQUEST));
        }
    }
}
