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
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
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
import java.util.ResourceBundle;

public class DefektazaController implements Initializable {

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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Platform.runLater(() -> {

            System.out.println(automobiliController.getAutomobil().get(0).getRegOznaka());
            System.out.println(automobiliController.getKlijenti().get(0).getImePrezime());

            if (automobiliController.isDefektazaInEditMode()) { //TRUE
                newOrEditDefektaza(true);

            } else { //Nismo u Edit Modu (FALSE)
                //Datum
                LocalDate now = LocalDate.now();
                datePickerDatum.setValue(now); //Postavi danasnji datum Racuna u datePiceru
                newOrEditDefektaza(false); // Nismo u edit modu pa napravi novi racun
            }

        });
    }

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
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        txtfVreme.setText(dtf.format(now));
    }

    private void newOrEditDefektaza(boolean isInEditMode) {
        initGUI(); //Inicijalizacija podataka za novu DEFEKTAZU bez obzira na edit mode

        if (isInEditMode) {
            txtfIdDefektaze.setText(String.valueOf(novaDefektaza.getIdDefektaze()));
            txtfKilometraza.setText(novaDefektaza.getKilometraza());
            datePickerDatum.setValue(LocalDate.parse(novaDefektaza.getDatumDefektaze()));
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

    /*
     ************************************************************
     ******************* BUTTON ACTION STAFF*********************
     ************************************************************
     */

    /**
     * UPDATE Defektaze kada se nesto promeni u njemu...(Datum...Vreme...Detalji...)
     * <p>
     * Setuju se svi podaci za izmenjena Defektaza pokupljeni iz TF-ova
     * Zatim se radi update sa {@link DefektazaDAO#updateDefektaza(Defektaza)}
     *
     * @see DefektazaDAO#updateDefektaza(Defektaza)
     * @see GeneralUiUtility#alertDialogBox(Alert.AlertType, String, String, String)
     */
    @FXML
    private void btnSacuvajDefektazuAction() {
        try {
            //UPDATE NOVE DEFEKTAZE SA NOVIM VREDNOSTIMA ZATO OVDE REDEFINISEMO NOVU DEFEKTAZU
            novaDefektaza.setIdDefektaze(brojDefektaze);
            novaDefektaza.setIdAuta(idAutomobila);
            novaDefektaza.setKilometraza(txtfKilometraza.getText());
            novaDefektaza.setDatumDefektaze(datePickerDatum.getValue().toString());
            novaDefektaza.setVreme(txtfVreme.getText());
            novaDefektaza.setOpisDefektaze(txtAreaOpisDefektaze.getText());
            novaDefektaza.setOstaliDetaljiDefektaze(txtAreOstaliDetaljiDefektaze.getText());
            defektazaDAO.updateDefektaza(novaDefektaza);
            GeneralUiUtility.alertDialogBox(
                    Alert.AlertType.CONFIRMATION,
                    "USPESNO SACUVANA DEFEKTAZA",
                    "EDITOVANJE DEFEKTAZE",
                    "Uspesno ste sacuvali DEF pod brojem: " + brojDefektaze
            );
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
            defektazaDAO.deleteDefektaza(brojDefektaze); //TODO PROVERITI OVO
            btnObrisiDefektazu.fireEvent(new WindowEvent(stageDefektaza, WindowEvent.WINDOW_CLOSE_REQUEST));
            ((Stage) (((Button) actionEvent.getSource()).getScene().getWindow())).close();
        } catch (AcrenoException | SQLException acrenoException) {
            acrenoException.printStackTrace();
        }
    }

    /**
     * Zatvaranje DEFEKTAZA UI UIa i refresh tabele Defektaza u {@link AutomobiliController}-u
     * <p>
     * Da bi se refresovala tabela Racuni u {@link AutomobiliController}-u potrebno je pozvati
     * {@code WindowEvent.WINDOW_CLOSE_REQUEST} koji je implementiran u {@link AutomobiliController#btnOpenDefektaza()} ()}
     *
     * @param actionEvent event for hide scene {@link DefektazaController}
     * @see AutomobiliController#btnOpenDefektaza()
     */
    @FXML
    private void btnCloseDefektazaAction(@NotNull ActionEvent actionEvent) {
        //TODO: pitati na zatvaranju da li hocemo da se sacuva RACUN ili da obrise
        btnCloseDefektaza.fireEvent(new WindowEvent(stageDefektaza, WindowEvent.WINDOW_CLOSE_REQUEST));
        ((Stage) (((Button) actionEvent.getSource()).getScene().getWindow())).close();
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

    public void btnPrintPregledDefektaza(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(Constants.PRINT_DEFEKTAZA_UI_VIEW_URI));
            stageDefektaza = new Stage();
            stageDefektaza.initModality(Modality.APPLICATION_MODAL);
            stageDefektaza.setScene(new Scene(loader.load()));

            initUiPrintControler(loader); //Inicijalizacija Porint Controlora i prosledjivanje id Racuna

            stageDefektaza.showAndWait();//Open Stage and wait

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
