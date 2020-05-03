package rs.acreno.racuni.faktura;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import rs.acreno.artikli.Artikl;
import rs.acreno.artikli.ArtikliDAO;
import rs.acreno.artikli.SQLArtikliDAO;
import rs.acreno.automobil.Automobil;
import rs.acreno.klijent.Klijent;
import rs.acreno.racuni.Racun;
import rs.acreno.racuni.RacuniDAO;
import rs.acreno.racuni.SQLRacuniDAO;
import rs.acreno.system.exeption.AcrenoException;

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

    private ObservableList<Automobil> automobili;
    private ObservableList<Klijent> klijenti;
    private ObservableList<Racun> racuni;
    private ObservableList<Artikl> artikli;

    //Inicijalizacija Racuni Objekta
    private final RacuniDAO racuniDAO = new SQLRacuniDAO();
    private final ArtikliDAO artikliDAO = new SQLArtikliDAO();

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
                artikli = FXCollections.observableArrayList(artikliDAO.findAllArtikle());

            } catch (AcrenoException | SQLException e) {
                e.printStackTrace();
            }
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


    private int brojFakture() throws AcrenoException, SQLException {
        int brojFakture = 0;
        List<Racun> racuni = racuniDAO.findAllRacune();
        for (Racun racun : racuni) {
            System.out.println(racun.getIdRacuna());
            brojFakture = racun.getIdRacuna();
        }
        return brojFakture + 1;
    }
}

