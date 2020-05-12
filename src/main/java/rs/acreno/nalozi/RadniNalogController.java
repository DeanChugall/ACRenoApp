package rs.acreno.nalozi;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.jetbrains.annotations.NotNull;
import rs.acreno.automobil.Automobil;
import rs.acreno.automobil.AutomobiliController;
import rs.acreno.autoservis.AutoServisController;
import rs.acreno.klijent.Klijent;
import rs.acreno.system.exeption.AcrenoException;
import rs.acreno.system.util.GeneralUiUtility;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class RadniNalogController implements Initializable {

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
    @FXML private Stage automobilStage;

    //INIT ObservableList-s
    private ObservableList<Automobil> automobili;
    private ObservableList<Klijent> klijenti;
    private ObservableList<RadniNalog> radniNalozi;

    private AutomobiliController automobiliController;

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

    //Inicijalizacija Radnog Naloga Objekta
    private final RadniNalogDAO radniNalogDAO = new SQLRadniNalogDAO();

    //INIT GUI FIELDS
    private int idAutomobila;
    private String regOznakaAutomobila;
    private int idKlijenta;
    private String imePrezimeKlijenta;

    @Override public void initialize(URL url, ResourceBundle resourceBundle) {
        Platform.runLater(() -> {
            System.out.println(automobiliController.getAutomobil().get(0).getRegOznaka());
            System.out.println(automobiliController.getKlijenti().get(0).getImePrezime());
            // Ako je racun u edit modu nemoj praviti novi racun nego prosledi RACUN koji je za izmenu
            if (automobiliController.isRadniNalogInEditMode()) { //TRUE
                newOrEditRadniNalog(true);

            } else { //Nismo u Edit Modu (FALSE)
                //Datum
                LocalDate now = LocalDate.now();
                datePickerDatum.setValue(now); //Postavi danasnji datum Racuna u datePiceru
                newOrEditRadniNalog(false); // Nismo u edit modu pa napravi novi racun
            }
        });
    }

    private void initGUI() {
        //Inicijalizacija podataka
        automobili = automobiliController.getAutomobil(); //Get AUTOMOBIL from automobiliController #Filtered
        klijenti = automobiliController.getKlijenti(); //Get KLIJENTA from automobiliController #Filtered
        idAutomobila = automobili.get(0).getIdAuta(); //Moze jer je samo jedan Automobil
        regOznakaAutomobila = automobili.get(0).getRegOznaka();//Moze jer je samo jedan Automobil
        idKlijenta = klijenti.get(0).getIdKlijenta();//Moze jer je samo jedan Klijent
        imePrezimeKlijenta = klijenti.get(0).getImePrezime();//Moze jer je samo jedan Klijent
        //Popunjavanje GUIa
        txtfKlijent.setText(imePrezimeKlijenta);
        txtfRegOznaka.setText(regOznakaAutomobila);

    }

    private void newOrEditRadniNalog(boolean isInEditMode) {
        initGUI(); //Inicijalizacija podataka za novi radni nalog bez obzira na edit mode
        if (isInEditMode) {
            System.out.println("TRUE WE ARE IN EDIT MODE RADNI NALOG");
            txtfIdRadnogNaloga.setText(String.valueOf(noviRadniNalog.getIdRadnogNaloga()));
            datePickerDatum.setValue(LocalDate.parse(noviRadniNalog.getDatum()));
            txtfVreme.setText(noviRadniNalog.getVreme());
            txtfKilometraza.setText(noviRadniNalog.getKilometraza());
            txtAreaDetaljiStranke.setText(noviRadniNalog.getDetaljiStranke());
            txtAreDetaljiServisera.setText(noviRadniNalog.getDetaljiServisera());
        } else {
            System.out.println("F A L S EEEE  WE ARE NOT IN EDIT MODE RADNI NALOG");
            noviRadniNalog = new RadniNalog();
            noviRadniNalog.setIdRadnogNaloga(brojRadnogNaloga);
            noviRadniNalog.setIdAutomobila(idAutomobila);
            noviRadniNalog.setKilometraza(txtfKilometraza.getText());
            noviRadniNalog.setDatum(datePickerDatum.getValue().toString());

            try {
                radniNalogDAO.insertRadniNalog(noviRadniNalog);
                //Inicijalizacija broja fakture MORA DA IDE OVDE
                radniNalozi = FXCollections.observableArrayList(radniNalogDAO.findAllRadniNalog());
                brojRadnogNaloga = radniNalogDAO.findAllRadniNalog().get(radniNalozi.size() - 1).getIdRadnogNaloga();
                txtfIdRadnogNaloga.setText(String.valueOf(brojRadnogNaloga));

            } catch (AcrenoException | SQLException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * Zatvaranje RADNI NALOG UIa i refresh tabele RADNI NALOG u {@link AutomobiliController}-u
     * <p>
     * Da bi se refresovala tabela Racuni u {@link AutomobiliController}-u potrebno je pozvati
     * {@code WindowEvent.WINDOW_CLOSE_REQUEST} koji je implementiran u {@link AutomobiliController#btnOpenNoviRadniNalog()}
     *
     * @param actionEvent event for hide scene {@link RadniNalogController}
     * @see AutomobiliController#btnOpenNoviRadniNalog
     */
    @FXML
    private void btnCloseFaktureAction(@NotNull ActionEvent actionEvent) {
        //TODO: pitati na zatvaranju da li hocemo da se sacuva RACUN ili da obrise
        btnCloseRadniNalog.fireEvent(new WindowEvent(automobilStage, WindowEvent.WINDOW_CLOSE_REQUEST));
        ((Stage) (((Button) actionEvent.getSource()).getScene().getWindow())).close();
    }

    /**
     * Bitna promenjiva jer se sve bazira na Broju Radnog Naloga ili ti ID Radnog Naloga
     */
    private int brojRadnogNaloga;

    public void setBrojFakture(int brojRadnogNaloga) {
        this.brojRadnogNaloga = brojRadnogNaloga;
    }

    //RADNI NALOG STAFF OBJECT
    private RadniNalog noviRadniNalog;

    public void setEditRadniNalog(RadniNalog noviRadniNalog) {
        this.noviRadniNalog = noviRadniNalog;
    }

    /*
     ************************************************************
     ******************* BUTTON ACTION STAFF*********************
     ************************************************************
     */

    /**
     * UPDATE Radnog Naloga kada se nesto promeni u njemu...(Datum...Vreme...Detalji...)
     * <p>
     * Setuju se svi podaci za izmenjen Radn Nalog pokupljeni iz TF-ova
     * Zatim se radi update sa {@link RadniNalogDAO#updateRadniNalog(RadniNalog)}
     *
     * @see RadniNalogDAO#updateRadniNalog(RadniNalog)
     * @see GeneralUiUtility#alertDialogBox(Alert.AlertType, String, String, String)
     */
    @FXML
    private void btnSacuvajRacunAction() {
        try {
            //UPDATE NOVOG RNa SA NOVIM VREDNOSTIMA ZATO OVDE REDEFINISEMO NOVI RADNI NALOG
            noviRadniNalog.setIdRadnogNaloga(brojRadnogNaloga);
            noviRadniNalog.setIdAutomobila(idAutomobila);
            noviRadniNalog.setKilometraza(txtfKilometraza.getText());
            noviRadniNalog.setDatum(datePickerDatum.getValue().toString());
            noviRadniNalog.setVreme(txtfVreme.getText());
            noviRadniNalog.setDetaljiStranke(txtAreaDetaljiStranke.getText());
            noviRadniNalog.setDetaljiServisera(txtAreDetaljiServisera.getText());
            radniNalogDAO.updateRadniNalog(noviRadniNalog);
            GeneralUiUtility.alertDialogBox(
                    Alert.AlertType.CONFIRMATION,
                    "USPESNO SACUVAN RADNI NALOG",
                    "EDITOVANJE RADNOG NALOGA",
                    "Uspesno ste sacuvali RN pod brojem: " + brojRadnogNaloga
            );
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
            radniNalogDAO.deleteRadniNalog(brojRadnogNaloga);
            btnObrisiRadniNalogAction.fireEvent(new WindowEvent(automobilStage, WindowEvent.WINDOW_CLOSE_REQUEST));
            ((Stage) (((Button) actionEvent.getSource()).getScene().getWindow())).close();
        } catch (AcrenoException | SQLException acrenoException) {
            acrenoException.printStackTrace();
        }
    }

}
