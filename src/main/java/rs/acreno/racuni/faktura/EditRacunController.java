package rs.acreno.racuni.faktura;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import rs.acreno.artikli.posao_artikli_dao.PosaoArtikli;
import rs.acreno.artikli.posao_artikli_dao.PosaoArtikliDAO;
import rs.acreno.artikli.posao_artikli_dao.PosaoArtikliDaoSearchType;
import rs.acreno.artikli.posao_artikli_dao.SQLPosaoArtikliDAO;
import rs.acreno.automobil.AutomobiliController;
import rs.acreno.autoservis.AutoServisController;
import rs.acreno.racuni.Racun;
import rs.acreno.racuni.RacuniSearchType;
import rs.acreno.system.exeption.AcrenoException;
import rs.acreno.system.util.ActionButtonTableCell;
import rs.acreno.system.util.GeneralUiUtility;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class EditRacunController implements Initializable {

    // Anchore Pane Top
    @FXML private TextField txtFIdRacuna;

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

    private ObservableList<PosaoArtikli> posaoArtikliList;
    private PosaoArtikliDAO posaoArtikli = new SQLPosaoArtikliDAO();
    private final PosaoArtikliDAO posaoArtikliDAO = new SQLPosaoArtikliDAO();

    /**
     * IdRacuna potreban za popunjavanje Svih podataka iz DB tabele "PosaoArtikli"
     *
     * @param idRacuna jedinstveni parametarIdRacuna koji je dobijen
     *                 iz {@link AutomobiliController#btnOpenEditRacunUiAction(Racun)}
     * @see PosaoArtikli
     * @see AutomobiliController#btnOpenEditRacunUiAction(Racun)
     */
    public void setIdRacuna(int idRacuna) {
        txtFIdRacuna.setText(String.valueOf(idRacuna));
    }


    private AutomobiliController automobiliController;
    private Stage stageEditRacun;

    /**
     * Empty "EditRacunUiController" if we need in some case
     */
    public EditRacunController() {
    }

    /**
     * <p>
     *     Inicijalizacija Controlora sa ID RACUNOM preko koga dobijamo objekat {@link PosaoArtikli}
     *     filterom {@code ID_RACUNA_POSAO_ARTIKLI_DAO} {@link PosaoArtikliDaoSearchType#ID_RACUNA_POSAO_ARTIKLI_DAO }
     * </p>
     * <p>Inicijalizacija dugmica u Tabeli {@link #tblPosaoArtikli}</p>
     * <p>Popunjavanje Table {@link #tblPosaoArtikli}</p>
     * @param url            url
     * @param resourceBundle resource bundle
     * @see PosaoArtikli
     * @see PosaoArtikliDAO
     * @see SQLPosaoArtikliDAO
     * @see PosaoArtikliDaoSearchType
     */
    @Override public void initialize(URL url, ResourceBundle resourceBundle) {
        Platform.runLater(() -> {
            //Postavljenje dugmica ADD u Tabeli ARTIKLI
            tblRowButton.setCellFactory(ActionButtonTableCell.forTableColumn("Obrisi", (PosaoArtikli p) -> {
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


            //Inicijalizacija POSAO ARTIKL objekta (P.A) po Filteru "ID_RACUNA_POSAO_ARTIKLI_DAO"
            int posaoArtikliId = Integer.parseInt(txtFIdRacuna.getText()); // Id P.A objekta
            try {
                //Get All staf form DB filtrirane po IDRacuna
                posaoArtikliList = FXCollections.observableArrayList(
                        posaoArtikli.findPosaoArtikliByPropertyDao(
                                PosaoArtikliDaoSearchType.ID_RACUNA_POSAO_ARTIKLI_DAO, posaoArtikliId));


                for (PosaoArtikli posaoArtikl : posaoArtikliList) {
                    System.out.println("ID POSAO ARTIKL: " + posaoArtikl.getIdArtikla());
                }
                System.out.println("*************************************************8");

            } catch (AcrenoException | SQLException e) {
                e.printStackTrace();
            }

            popuniTabeluRacuni(); //Popunjavanje Tabele Posao Artikli
        });
    }

    /**
     * Seter metoda koja se koristi u {@link AutomobiliController#setAutoServisController(AutoServisController, Stage)}-u
     * Preko nje mozemo da prosledimo Klijent i Automobil Objekat ovde,
     * a iz {@link AutoServisController #showAutomobiliUi()}-a
     *
     * @param automobiliController referenca ka auto servis kontroloru
     * @param stageEditRacun       refereca ka Stage-u auto servisu
     * @see AutoServisController
     */
    public void setAutomobiliController(AutomobiliController automobiliController, Stage stageEditRacun) {
        this.automobiliController = automobiliController;
        this.stageEditRacun = stageEditRacun;
    }

    private void popuniTabeluRacuni() {

        tblRowidPosaoArtikli.setCellValueFactory(new PropertyValueFactory<>("idPosaoArtikli"));
        tblRowidPosaoArtikli.setStyle("-fx-alignment: CENTER;");
        tblRowidArtikla.setCellValueFactory(new PropertyValueFactory<>("idArtikla"));
        tblRowidArtikla.setStyle("-fx-alignment: CENTER;");
        tblRowNazivArtikla.setCellValueFactory(new PropertyValueFactory<>("nazivArtikla"));
        tblRowNazivArtikla.setStyle("-fx-alignment: CENTER;");
        tblRowOpisArtikla.setCellValueFactory(new PropertyValueFactory<>("opisPosaoArtiklli"));
        tblRowOpisArtikla.setStyle("-fx-alignment: CENTER;");
        tblRowCena.setCellValueFactory(new PropertyValueFactory<>("cena"));
        tblRowCena.setStyle("-fx-alignment: CENTER;");
        tblRowNabavnaCena.setCellValueFactory(new PropertyValueFactory<>("nabavnaCena"));
        tblRowNabavnaCena.setStyle("-fx-alignment: CENTER;");
        tblRowKolicina.setCellValueFactory(new PropertyValueFactory<>("kolicina"));
        tblRowKolicina.setStyle("-fx-alignment: CENTER;");
        tblRowJedinicaMere.setCellValueFactory(new PropertyValueFactory<>("jedinicaMere"));
        tblRowJedinicaMere.setStyle("-fx-alignment: CENTER;");
        tblRowPopust.setCellValueFactory(new PropertyValueFactory<>("popust"));
        tblRowPopust.setStyle("-fx-alignment: CENTER;");
        tblPosaoArtikli.setItems(posaoArtikliList);
    }


}
