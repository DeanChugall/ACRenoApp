package rs.acreno.racuni.faktura;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import org.jetbrains.annotations.NotNull;
import rs.acreno.artikli.Artikl;
import rs.acreno.artikli.ArtikliDAO;
import rs.acreno.artikli.SQLArtikliDAO;
import rs.acreno.artikli.posao_artikli_dao.PosaoArtikli;
import rs.acreno.artikli.posao_artikli_dao.PosaoArtikliDAO;
import rs.acreno.artikli.posao_artikli_dao.PosaoArtikliDaoSearchType;
import rs.acreno.artikli.posao_artikli_dao.SQLPosaoArtikliDAO;
import rs.acreno.automobil.Automobil;
import rs.acreno.klijent.Klijent;
import rs.acreno.racuni.Racun;
import rs.acreno.racuni.RacuniDAO;
import rs.acreno.racuni.SQLRacuniDAO;
import rs.acreno.system.exeption.AcrenoException;
import rs.acreno.system.util.ActionButtonTableCell;
import rs.acreno.system.util.GeneralUiUtility;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

public class FakturaController implements Initializable {


    public TextField txtFieldBrojRacuna;
    public TextField txtFieldImeKlijenta;
    public TextField txtFieldRegTablica;
    public DatePicker dPickDatumFaktura;
    public TextField txtFieldIdAutomobila;
    public TextField txtFieldPopustRacuna;
    public TextArea txtAreaNapomenaRacuna;

    //Pretraga Artikala Tabela
    public ListView<Artikl> listViewPretragaArtikli;
    public TextField txtFieldPretragaArtikla;
    public TableView<PosaoArtikli> tblPosaoArtikli;
    public TableColumn<PosaoArtikli, Integer> tblRowidPosaoArtikli;
    public TableColumn<String, String> tblRowidRacuna;
    public TableColumn<Artikl, Integer> tblRowidArtikla;
    public TableColumn<PosaoArtikli, Button> tblRowButton;
    public TableColumn<Artikl, Integer> tblRowCena;

    private ObservableList<Automobil> automobili;
    private ObservableList<Klijent> klijenti;
    private ObservableList<Racun> racuni;
    private ObservableList<Artikl> artikli;
    private ObservableList<PosaoArtikli> posaoArtikli;

    //Inicijalizacija Racuni Objekta
    private final RacuniDAO racuniDAO = new SQLRacuniDAO();
    private final ArtikliDAO artikliDAO = new SQLArtikliDAO();
    private final PosaoArtikliDAO posaoArtikliDAO = new SQLPosaoArtikliDAO();
    private PosaoArtikli posaoArtikliObject= new PosaoArtikli();


    public void setAutomobili(ObservableList<Automobil> automobili) {
        this.automobili = automobili;
    }

    public void setKlijenti(ObservableList<Klijent> klijenti) {
        this.klijenti = klijenti;
    }

    public void setRacuni(ObservableList<Racun> racuni) {
        this.racuni = racuni;
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Platform.runLater(() -> {
            System.out.println(automobili.get(0).getRegOznaka());
            System.out.println(klijenti.get(0).getImePrezime());

            for (Racun racun : racuni) {
                System.out.println(racun.getIdRacuna());
            }
            try {
                txtFieldBrojRacuna.setText(String.valueOf(brojFakture()));
                txtFieldImeKlijenta.setText(klijenti.get(0).getImePrezime());
                txtFieldRegTablica.setText(automobili.get(0).getRegOznaka());
                txtFieldIdAutomobila.setText(String.valueOf(automobili.get(0).getIdAuta()));
                //Datum
                LocalDate now = LocalDate.now();
                dPickDatumFaktura.setValue(now);

                //Inicijalizacija Artikala
               // artikli = FXCollections.observableArrayList(artikliDAO.findAllArtikle());
                //Inicijalizacija Artikala
               // posaoArtikli = FXCollections.observableArrayList();
            } catch (AcrenoException | SQLException e) {
                e.printStackTrace();
            }
            //Postavljenje dugmica ADD u Tabeli ARTIKLI
            tblRowButton.setCellFactory(ActionButtonTableCell.forTableColumn("x", (PosaoArtikli p) -> {
                System.out.println("KLICK FROM BUTTON IN TABLE ARTIKLI");
                p.setIdRacuna(Integer.parseInt(txtFieldBrojRacuna.getText()));
                System.out.println("ID RACINA: " + p.getIdRacuna());
                System.out.println("ID ARTIKLA: " + p.getIdArtikla());
                tblPosaoArtikli.getItems().remove(p);

                try {
                    posaoArtikliDAO.deletePosaoArtikliDao(p);
                } catch (AcrenoException | SQLException e) {
                    e.printStackTrace();
                }

                return p;

            }));
        });
    }


    public void txtFieldPretragaArtiklaKeyListener(KeyEvent keyEvent) {
        txtFieldPretragaArtikla.textProperty().addListener(observable -> {
            if (txtFieldPretragaArtikla.textProperty().get().isEmpty()) {
                listViewPretragaArtikli.setItems(artikli);
                return;
            }
            ObservableList<Artikl> artikl = null;
            ObservableList<Artikl> tempArtikl = null;
            try {
                artikl = FXCollections.observableArrayList(artikliDAO.findAllArtikle()); //Svi Automobili
                tempArtikl = FXCollections.observableArrayList(); //Lista u koju dodajemo nadjene Auto objekte
            } catch (AcrenoException | SQLException e) {
                e.printStackTrace();
            }
            for (int i = 0; i < (artikl != null ? artikl.size() : 0); i++) {

                String RegTablica = artikl.get(i).getNazivArtikla().toLowerCase();//Trenutna tablica auta

                if (RegTablica.contains(txtFieldPretragaArtikla.textProperty().get())) {
                    tempArtikl.add(artikl.get(i)); // Dodaje nadjeni auto u temp listu
                    listViewPretragaArtikli.setItems(tempArtikl); // Dodaje u FXlistView
                    listViewPretragaArtikli.setCellFactory(param -> new ListCell<>() {
                        @Override
                        protected void updateItem(Artikl item, boolean empty) {
                            super.updateItem(item, empty);
                            listViewPretragaArtikli.setVisible(true); //Prikazuje listu vidljivom
                            if (empty || item == null || item.getNazivArtikla() == null) {
                                setText(null);
                            } else {
                                setText(item.getNazivArtikla());

                               }
                        }
                    });
                    //break;
                }
            }
        });
    }

    @FXML
    // Zatvara popUp ListView pretrage i setuje selektovanu vrednost u TF sa double click
    public void zatvoriListViewSearchAutomobil(@NotNull MouseEvent mouseEvent) throws AcrenoException, SQLException {
        //Na dupli click vraca Radni Nalog Objekat i otvara Radni nalog Dashboard
        if (mouseEvent.getButton().equals(MouseButton.PRIMARY) && mouseEvent.getClickCount() == 2) {
            String nazivArtikla = listViewPretragaArtikli.getSelectionModel().getSelectedItems().get(0).getNazivArtikla();
            txtFieldPretragaArtikla.setText(nazivArtikla);
            listViewPretragaArtikli.setVisible(false);
            //Dodavanje objekta Artikl iz filtrirane ListeView


            //posaoArtikliObject.setIdPosaoArtikli(4);
           // posaoArtikliObject.setIdPosaoArtikli(4);
            posaoArtikliObject.setIdRacuna(Integer.parseInt(txtFieldBrojRacuna.getText()));
            posaoArtikliObject.setIdArtikla(listViewPretragaArtikli.getSelectionModel().getSelectedItem().getIdArtikla());
            posaoArtikliObject.setJedinicaMere(listViewPretragaArtikli.getSelectionModel().getSelectedItem().getJedinicaMere());
            posaoArtikliObject.setOpisPosaoArtiklli("setOpisPosaoArtiklli");
            posaoArtikliObject.setDetaljiPosaoArtikli("setDetaljiPosaoArtikli");

            posaoArtikliDAO.insertPosaoArtikliDao(posaoArtikliObject);



            tblRowidPosaoArtikli.setCellValueFactory(new PropertyValueFactory<>("idPosaoArtikli"));
            tblRowidRacuna.setCellValueFactory(param -> new ReadOnlyStringWrapper(txtFieldBrojRacuna.getText()));
            tblRowidArtikla.setCellValueFactory(new PropertyValueFactory<>("idArtikla"));
          /*  tblRowCena.setCellValueFactory(new PropertyValueFactory<>("cenaArtikla"));*/
            //tblRowidRacuna.setCellValueFactory(param -> new ReadOnlyStringWrapper(param.getValue()));
            tblPosaoArtikli.getItems().add(listViewPretragaArtikli.getSelectionModel().getSelectedItem()); //Dodaje



        } else if (mouseEvent.getButton().equals(MouseButton.PRIMARY) && mouseEvent.getClickCount() == 1) {

        }
    }

    private int brojFakture() throws AcrenoException, SQLException {
        int brojFakture = 0;
        List<Racun> racuni = racuniDAO.findAllRacune();
        for (Racun racun : racuni) {
            System.out.println(racun.getIdRacuna());
            brojFakture = racun.getIdRacuna();
        }
        return brojFakture + 1;
    }

    public void btnKreirajNoviRacunAction(ActionEvent actionEvent)  {
        Racun noviRacun = new Racun();
        noviRacun.setIdAutomobila(Integer.parseInt(txtFieldIdAutomobila.getText()));
        noviRacun.setDatum(dPickDatumFaktura.getValue().toString());
        if (!txtFieldPopustRacuna.getText().isEmpty()) {
            noviRacun.setPopust(Integer.parseInt(txtFieldPopustRacuna.getText()));
        }
        noviRacun.setNapomeneRacuna(txtAreaNapomenaRacuna.getText());
        try {
            racuniDAO.insertRacun(noviRacun);
            GeneralUiUtility.alertDialogBox(Alert.AlertType.CONFIRMATION, "Uspesno kreiranje racuna",
                    "Kreiranje racuna", "Uspesno kreiran racun !");
        } catch (AcrenoException | SQLException e) {
            GeneralUiUtility.alertDialogBox(Alert.AlertType.ERROR, "ERROR kreiranje racuna",
                    "Kreiranje racuna", "Greska, kontakrirajte Dexa !/n" + e);
            e.printStackTrace();
        }
    }
}

