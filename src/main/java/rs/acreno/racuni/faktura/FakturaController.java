package rs.acreno.racuni.faktura;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.jetbrains.annotations.NotNull;
import rs.acreno.artikli.Artikl;
import rs.acreno.artikli.ArtikliDAO;
import rs.acreno.artikli.SQLArtikliDAO;
import rs.acreno.artikli.posao_artikli_dao.PosaoArtikli;
import rs.acreno.artikli.posao_artikli_dao.PosaoArtikliDAO;
import rs.acreno.artikli.posao_artikli_dao.SQLPosaoArtikliDAO;
import rs.acreno.automobil.Automobil;
import rs.acreno.automobil.AutomobiliController;
import rs.acreno.klijent.Klijent;
import rs.acreno.racuni.Racun;
import rs.acreno.racuni.RacuniDAO;
import rs.acreno.racuni.SQLRacuniDAO;
import rs.acreno.system.constants.Constants;
import rs.acreno.system.exeption.AcrenoException;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

public class FakturaController implements Initializable {

    public BorderPane bPaneMainContainer;
    @FXML
    private TextField txtFidRacuna;

    @FXML
    private TextField txtFklijentImePrezime;
    @FXML
    private TextField txtFregTablica;
    @FXML
    private DatePicker datePickerDatumRacuna;

    private Stage stage;
    private final AutomobiliController automobiliController;

    //INIT GUI FIELDS
    private int idFakture;
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


    //Inicijalizacija Racuni Objekta
    private final RacuniDAO racuniDAO = new SQLRacuniDAO();
    private final ArtikliDAO artikliDAO = new SQLArtikliDAO();
    private final PosaoArtikliDAO posaoArtikliDAO = new SQLPosaoArtikliDAO();
    private final PosaoArtikli posaoArtikliObject = new PosaoArtikli();


    public FakturaController(AutomobiliController automobiliController) {
        this.automobiliController = automobiliController;
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource(Constants.FAKTURA_UI_VIEW_URI));
            // Set this class as the controller
            loader.setController(this);
            // Load the scene
            stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(loader.load()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Setup the window/stage
    }

    /**
     * Show the stage that was loaded in the constructor
     */
    public void showFakturaStage() {
        initGUI();
        stage.showAndWait();
    }


    private void initGUI() {
        //Inicijalizacija podataka
        automobili = automobiliController.getAutomobil(); //Get AUTOMOBIL from automobiliController #Filtered
        klijenti = automobiliController.getKlijenti(); //Get KLIJENTA from automobiliController #Filtered
        idFakture = brojFakture();
        idAutomobila = automobili.get(0).getIdAuta();
        regOznakaAutomobila = automobili.get(0).getRegOznaka();
        idKlijenta = klijenti.get(0).getIdKlijenta();
        imePrezimeKlijenta = klijenti.get(0).getImePrezime();
        //Popunjavanje GUIa
        stage.setTitle("Registarska Oznaka: " +regOznakaAutomobila + " || Klijent: " + imePrezimeKlijenta);
        txtFklijentImePrezime.setText(imePrezimeKlijenta);
        txtFregTablica.setText(regOznakaAutomobila);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Platform.runLater(() -> {
            txtFidRacuna.setText(String.valueOf(brojFakture()));
           /* txtFidRacuna.setText(String.valueOf(racuni.get(0).getIdRacuna()));
            txtFklijentImePrezime.setText(klijenti.get(0).getImePrezime());
            txtFregTablica.setText(automobili.get(0).getRegOznaka());*/
            //Datum
            LocalDate now = LocalDate.now();
            datePickerDatumRacuna.setValue(now);

        });
    }

    public void btnOdustaniMouseClick(@NotNull MouseEvent mouseEvent) throws AcrenoException, SQLException {
        racuniDAO.deleteRacun(Integer.parseInt(txtFidRacuna.getText()));
        ((Stage) (((Button) mouseEvent.getSource()).getScene().getWindow())).close();
        System.out.println("ID RACUNA = " + racuni.get(0).getIdRacuna());
    }

    private void closeWindowEvent(WindowEvent event) {
        System.out.println("Window close request ...");
/*
        if(storageModel.dataSetChanged()) {  // if the dataset has changed, alert the user with a popup
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.getButtonTypes().remove(ButtonType.OK);
            alert.getButtonTypes().add(ButtonType.CANCEL);
            alert.getButtonTypes().add(ButtonType.YES);
            alert.setTitle("Quit application");
            alert.setContentText(String.format("Close without saving?"));
            alert.initOwner(primaryStage.getOwner());
            Optional<ButtonType> res = alert.showAndWait();

            if(res.isPresent()) {
                if(res.get().equals(ButtonType.CANCEL))
                    event.consume();
            }
        }*/
    }

    //Odredjuje poslednji broj fakture i dodaje 1
    private int brojFakture() {
        int brojFakture = 0;
        List<Racun> racuni = null;
        try {
            racuni = racuniDAO.findAllRacune();
            for (Racun racun : racuni) {
                //System.out.println("brojFakture: " + racun.getIdRacuna());
                brojFakture = racun.getIdRacuna() + 1;
            }
        } catch (AcrenoException | SQLException e) {
            e.printStackTrace();
        }
        return brojFakture;
    }
}

