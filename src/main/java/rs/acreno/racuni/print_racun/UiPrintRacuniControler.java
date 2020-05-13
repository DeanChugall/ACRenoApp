package rs.acreno.racuni.print_racun;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.print.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;
import rs.acreno.artikli.posao_artikli_dao.PosaoArtikli;
import rs.acreno.artikli.posao_artikli_dao.PosaoArtikliDAO;
import rs.acreno.artikli.posao_artikli_dao.PosaoArtikliDaoSearchType;
import rs.acreno.artikli.posao_artikli_dao.SQLPosaoArtikliDAO;
import rs.acreno.nalozi.print_nalozi.PrintNaloziController;
import rs.acreno.racuni.faktura.FakturaController;
import rs.acreno.system.exeption.AcrenoException;
import rs.acreno.system.util.DragAndDropTable;
import rs.acreno.system.util.GeneralUiUtility;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class UiPrintRacuniControler implements Initializable {


    @FXML private Button btnPrint;
    @FXML private AnchorPane ancorPanePrint;
    @FXML private TextField txtFidRacuna;
    @FXML private TextField txtfUkupnoSadPopustomNaDelove;
    @FXML private TextField txtfTotalBezPopusta;
    @FXML private TextField txtFpopustRacuna;
    @FXML private TextField txtfPopustDoleNaRacunu;
    @FXML private TextField txtfGrandTotal;

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
    @FXML private TableColumn<PosaoArtikli, Number> tblRowTotal;

    public UiPrintRacuniControler() {
    }

    /**
     * Referenca ka {@link FakturaController}-u, ako slucajno zatreba nesto iz tog kontrolora
     */
    private Stage fakturaStage;
    private FakturaController fakturaController;

    public void setFakturaController(FakturaController fakturaController, Stage fakturaStage) {
        this.fakturaController = fakturaController;
        this.fakturaStage = fakturaStage;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Platform.runLater(() -> {
            txtFidRacuna.setText(fakturaController.getIdRacuna()); // SET ID RACUNA/FAKTURE form Faktura Controller
            txtfUkupnoSadPopustomNaDelove.setText(fakturaController.getTotalSumaSaPopustomNaDelove()); // Set total sa popustom na delove form Faktura Controller
            txtfTotalBezPopusta.setText(fakturaController.getTotalBezPopustaSuma()); // Set Total bez popusta form Faktura Controller
            txtFpopustRacuna.setText(fakturaController.getPopustRacuna()); // Set Popust RACUNA TF
            txtfPopustDoleNaRacunu.setText(txtFpopustRacuna.getText());
            txtfGrandTotal.setText(fakturaController.getGrandTotalSumaSuma()); // Grand Total suma sa popustom
            popuniTabeluPosaoArtikli(); // Popuni tabelu Posao Artikli
            DragAndDropTable.dragAndDropTbl(tblPosaoArtikli); //Rearagne table rows if need in print Racun/Faktura

            //Izracunavanje TOTAL sume u tabeli
            FakturaController.setGrandTotalSuma(tblRowTotal);

        });
    }

    public void btnPrintAct(ActionEvent actionEvent) {

        ancorPanePrint.requestFocus(); // remove focus from table for print
        tblPosaoArtikli.getSelectionModel().clearSelection(); // clear selection from table for print

        GeneralUiUtility.printStaff(ancorPanePrint, btnPrint);
    }

    private void popuniTabeluPosaoArtikli() {
        ObservableList<PosaoArtikli> posaoArtikli = initPosaoArtikliDbTable(Integer.parseInt(fakturaController.getIdRacuna()));
        tblRowidPosaoArtikli.setCellValueFactory(new PropertyValueFactory<>("idPosaoArtikli"));
        tblRowidPosaoArtikli.setStyle("-fx-alignment: CENTER;");
        tblRowidRacuna.setCellValueFactory(new PropertyValueFactory<>("idRacuna"));
        tblRowidRacuna.setStyle("-fx-alignment: CENTER;");
        tblRowidArtikla.setCellValueFactory(new PropertyValueFactory<>("idArtikla"));
        tblRowidArtikla.setStyle("-fx-alignment: CENTER;");
        tblRowNazivArtikla.setCellValueFactory(new PropertyValueFactory<>("nazivArtikla"));
        tblRowNazivArtikla.setStyle("-fx-alignment: CENTER;");
        tblRowOpisArtikla.setCellValueFactory(new PropertyValueFactory<>("opisPosaoArtiklli"));
        tblRowOpisArtikla.setStyle("-fx-alignment: CENTER;");
        tblRowKolicina.setCellValueFactory(new PropertyValueFactory<>("kolicina"));
        tblRowKolicina.setStyle("-fx-alignment: CENTER;");
        tblRowJedinicaMere.setCellValueFactory(new PropertyValueFactory<>("jedinicaMere"));
        tblRowJedinicaMere.setStyle("-fx-alignment: CENTER;");
        tblRowCena.setCellValueFactory(new PropertyValueFactory<>("cena"));
        tblRowCena.setStyle("-fx-alignment: CENTER;");
        tblRowPopust.setCellValueFactory(new PropertyValueFactory<>("popust"));
        tblRowPopust.setStyle("-fx-alignment: CENTER;");

        tblPosaoArtikli.setItems(posaoArtikli);
    }

    /**
     * Prilikom ulaza u UiPrintControler potrebno je naci sve artikle iz vezne tabele koji su u korelaciji
     * sa ovim ID Racunom. Korisi se {@link #initialize}
     *
     * @param idRacuna id racuna kao parametar pretrage u bazi za Posao Artikle veznu Tabelu
     * @return ObservableList<PosaoArtikli>
     */
    private ObservableList<PosaoArtikli> initPosaoArtikliDbTable(int idRacuna) {
        PosaoArtikliDAO posaoArtikliDAO = new SQLPosaoArtikliDAO();
        ObservableList<PosaoArtikli> posaoArtikli = null;
        try {
            posaoArtikli = FXCollections.observableArrayList(
                    posaoArtikliDAO.findPosaoArtikliByPropertyDao(
                            PosaoArtikliDaoSearchType.ID_RACUNA_POSAO_ARTIKLI_DAO, idRacuna));

        } catch (AcrenoException | SQLException e) {
            e.printStackTrace();
        }
        return posaoArtikli;
    }
}
