package rs.acreno.racuni.print_racun;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
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
import rs.acreno.racuni.faktura.FakturaController;
import rs.acreno.system.exeption.AcrenoException;
import rs.acreno.system.util.GeneralUiUtility;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class UiPrintRacuniControler implements Initializable {


    public Button btnPrint;
    public AnchorPane ancorPanePrint;
    public TextField txtFidRacuna;

    private int idRacuna;

    //Pretraga Artikala Tabela
    public TableView<PosaoArtikli> tblPosaoArtikli;
    public TableColumn<PosaoArtikli, Number> tblRowidPosaoArtikli;
    public TableColumn<PosaoArtikli, Number> tblRowidRacuna;
    public TableColumn<PosaoArtikli, Number> tblRowidArtikla;
    public TableColumn<PosaoArtikli, String> tblRowNazivArtikla;
    public TableColumn<PosaoArtikli, String> tblRowOpisArtikla;
    public TableColumn<PosaoArtikli, Number> tblRowCena;
    public TableColumn<PosaoArtikli, Number> tblRowNabavnaCena;
    public TableColumn<PosaoArtikli, Number> tblRowKolicina;
    public TableColumn<PosaoArtikli, String> tblRowJedinicaMere;
    public TableColumn<PosaoArtikli, Number> tblRowPopust;

    public UiPrintRacuniControler() {}

    /**
     * Referenca ka {@link FakturaController}-u, ako slucajno zatreba nesto iz tog kontrolora
     */
    private Stage fakturaStage;
    private FakturaController fakturaController;
    public void setFakturaController(FakturaController fakturaController, Stage fakturaStage) {
        this.fakturaController = fakturaController;
        this.fakturaStage = fakturaStage;
    }

    public int getIdRacuna() {
        return idRacuna;
    }

    public void setIdRacuna(int idRacuna) {
        this.idRacuna = idRacuna;
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Platform.runLater(() -> {
            txtFidRacuna.setText(String.valueOf(getIdRacuna()));
            initPosaoArtikliDbTable(getIdRacuna()); // inicijalizuj PosaoArtikl Objekat
            popuniTabeluPosaoArtikli();
        });
    }


    public void btnPrintAct(ActionEvent actionEvent) {
        PrinterJob job = PrinterJob.createPrinterJob();

        if (job != null && job.showPrintDialog(ancorPanePrint.getScene().getWindow())) {
            btnPrint.setVisible(false);
            Printer printer = job.getPrinter();
            job.getJobSettings().setPrintQuality(PrintQuality.HIGH);
            PageLayout pageLayout = printer.createPageLayout(Paper.A4, PageOrientation.PORTRAIT, Printer.MarginType.HARDWARE_MINIMUM);

            double scaleX = pageLayout.getPrintableWidth() / ancorPanePrint.getBoundsInParent().getWidth();
            ancorPanePrint.getTransforms().add(new Scale(scaleX, scaleX));

            boolean success = job.printPage(pageLayout, ancorPanePrint);
            if (success) {
                job.endJob();
                GeneralUiUtility.alertDialogBox(Alert.AlertType.CONFIRMATION
                        , "USPESAN PRINT"
                        , "PRINT"
                        , "Uspesno isprintan racuna" + job.getJobStatus());
            } else {
                GeneralUiUtility.alertDialogBox(Alert.AlertType.ERROR
                        , "GRESKA PRINT"
                        , "PRINT"
                        , "GRESKA U PRINTU racuna" + job.getJobStatus());
            }
            btnPrint.setVisible(true);
        }
    }

    private void popuniTabeluPosaoArtikli() {
        ObservableList<PosaoArtikli> posaoArtikli = initPosaoArtikliDbTable(getIdRacuna());
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
     *Prilikom ulaza u UiPrintControler potrebno je naci sve artikle iz vezne tabele koji su u korelaciji
     * sa ovim ID Racunom. Korisi se {@link #initialize}
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

            for (PosaoArtikli posaoArtikli1 : posaoArtikli) {
                System.out.println(posaoArtikli1.getNazivArtikla());
            }

        } catch (AcrenoException | SQLException e) {
            e.printStackTrace();
        }
        return posaoArtikli;
    }

}
