package rs.acreno.autoservis;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;
import rs.acreno.automobil.*;
import rs.acreno.klijent.Klijent;
import rs.acreno.klijent.KlijentDAO;
import rs.acreno.klijent.KlijentSearchType;
import rs.acreno.klijent.SQLKlijnetDAO;
import rs.acreno.system.constants.Constants;
import rs.acreno.system.exeption.AcrenoException;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicReference;

public class AutoServisController implements Initializable {

    /**
     * ListView koja cuva Listu Automobila u TxtF-u prilikom fil;tera u kucanju
     *
     * @see #txtFieldRegTablicaSaerchKeyListener
     * @see #zatvoriListViewSearchAutomobil
     */
    @FXML private ListView<Automobil> listViewAutmobiliSearch;

    /**
     * txtF za Reg. Tablicu u {@link #txtFieldRegTablicaSaerchKeyListener), pa posle bitno za
     * prosledjivanje tablice {@link AutomobiliController}-u, a koristi se {@link #openAutomobiliUi }
     *
     * @see {@link #txtFieldRegTablicaSaerchKeyListener()}
     * @see {@link #openAutomobiliUi }
     * @see {@link AutomobiliController}
     */
    @FXML private TextField txtFieldRegOznaka;

    /**
     * Zatvori Aplikaciju
     * @see #btnCloseApplication()
     */
    @FXML public Button btnCloseApplication;

    /**
     * Inicijalizacija AUTOMOBIL Objekta
     * Da bi u samom startu imali sve objekte {@link Automobil}. Koristi se {@link AutomobilDAO} interfejs,
     * koji je implemetiran u {@link SQLAutomobilDAO}, a trenutno se koristi MSACCESS implementacija.
     * Sve se ubacuje u {@code ObservableList<Automobil>} zbog FX frameworks-a.
     *
     * @see Automobil
     * @see SQLAutomobilDAO#findAllAutomobil()
     */
    private final AutomobilDAO automobilDAO = new SQLAutomobilDAO();
    private ObservableList<Automobil> automobili =
            FXCollections.observableArrayList(automobilDAO.findAllAutomobil());

    /**
     * Inicijalizacija KLIJENT Objekta
     * Da bi u samom startu imali sve objekte {@link Klijent}. Koristi se {@link KlijentDAO} interfejs,
     * koji je implemetiran u {@link SQLKlijnetDAO}, a trenutno se koristi MSACCESS implementacija.
     * Sve se ubacuje u {@code ObservableList<Klijent>} zbog FX frameworks-a.
     *
     * @see Klijent
     * @see SQLKlijnetDAO#findAllKlijents()
     */
    private final KlijentDAO klijentDAO = new SQLKlijnetDAO();
    private final AtomicReference<ObservableList<Klijent>> klijenti =
            new AtomicReference<>(FXCollections.observableArrayList(klijentDAO.findAllKlijents()));

    /**
     * Empty Constructor if we need this for later use
     *
     * @throws AcrenoException Zbog Exeption-sa u inicijalizaciji Objekata KLIJENT i AUTOMOBIL
     * @throws SQLException    Zbog Exeption-sa u inicijalizaciji Objekata KLIJENT i AUTOMOBIL
     */
    public AutoServisController() throws AcrenoException, SQLException {
    }

    /**
     * Empty inicijalizaciona metoda ovog {@link AutoServisController}-a, if we need this for later use
     *
     * @param url            FX staff
     * @param resourceBundle FX staff
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }

    /**
     * Pretraga i filtriranje Autmobila po REG. TABLICI u KeyListeneru TxtF-a
     * <p>
     * Prilikom kucanja u txtF pokazuju se filtrirani Automobili u ListView koji je inicijalno sakriven
     * Ukoliko ima podataka ListView se prikazuje i duplim klikom se selektuje Automobil objekat i
     * otvara se Automobil UI {@link AutomobiliController}. Ova implementacija otvaranja Automobil UIa
     * je definisana {@link #zatvoriListViewSearchAutomobil}
     *
     * @author Dejan Cugalj
     * @see #zatvoriListViewSearchAutomobil(MouseEvent)
     * @see AutomobiliController
     */
    @FXML
    private void txtFieldRegTablicaSaerchKeyListener() {
        txtFieldRegOznaka.textProperty().addListener(observable -> {
            if (txtFieldRegOznaka.textProperty().get().isEmpty()) {
                listViewAutmobiliSearch.setItems(automobili);
                return;
            }
            ObservableList<Automobil> auto = null;
            ObservableList<Automobil> tempAutomobil = null;
            try {
                auto = FXCollections.observableArrayList(automobilDAO.findAllAutomobil()); //Svi Automobili
                tempAutomobil = FXCollections.observableArrayList(); //Lista u koju dodajemo nadjene Auto objekte
            } catch (AcrenoException | SQLException e) {
                e.printStackTrace();
            }
            for (int i = 0; i < (auto != null ? auto.size() : 0); i++) {

                String RegTablica = auto.get(i).getRegOznaka().toLowerCase();//Trenutna tablica auta

                if (RegTablica.contains(txtFieldRegOznaka.textProperty().get())) {
                    tempAutomobil.add(auto.get(i)); // Dodaje nadjeni auto u temp listu
                    listViewAutmobiliSearch.setItems(tempAutomobil); // Dodaje u FXlistView
                    listViewAutmobiliSearch.setCellFactory(param -> new ListCell<>() {
                        @Override
                        protected void updateItem(Automobil item, boolean empty) {
                            super.updateItem(item, empty);
                            listViewAutmobiliSearch.setVisible(true); //Prikazuje listu vidljivom
                            if (empty || item == null || item.getRegOznaka() == null) {
                                setText(null);
                            } else {
                                setText(item.getRegOznaka());
                            }
                        }
                    });
                    //break;
                }
            }
        });
    }

    /**
     * Zatvara popUp ListView pretrage i setuje selektovanu vrednost u TF sa double click
     * Nakon toga @see {@link #openAutomobiliUi()}
     *
     * @param mouseEvent da se otvori Automobil controller i zatvori AutoServis Controler
     * @throws AcrenoException Malo bolje objasnjenje SQL Except
     * @throws SQLException    zbog {@code showAutomobiliUi()} {@link #openAutomobiliUi()}
     * @author Dejan Cugalj
     * @see #openAutomobiliUi()
     */
    @FXML
    private void zatvoriListViewSearchAutomobil(@NotNull MouseEvent mouseEvent) throws AcrenoException, SQLException {
        //Na dupli click vraca Radni Nalog Objekat i otvara Radni nalog Dashboard
        if (mouseEvent.getButton().equals(MouseButton.PRIMARY) && mouseEvent.getClickCount() == 2) {
            //Moze .getSelectedItems().get(0) jer ima samo jedan Automobil
            String regOznaka = listViewAutmobiliSearch.getSelectionModel().getSelectedItems().get(0).getRegOznaka();

            txtFieldRegOznaka.setText(regOznaka);
            listViewAutmobiliSearch.setVisible(false);
            ((Node) mouseEvent.getSource()).getScene().getWindow().hide();
            openAutomobiliUi();
            ((Stage) ((Node) mouseEvent.getSource()).getScene().getWindow()).show();
        }
    }

    /**
     * Metoda koja otvara {@link AutomobiliController} UI i prosledjuje CONTROLLER i STAGE u {@link AutomobiliController}.
     * <p>
     * Prosledjuje {@link Automobil} objekat u {@link AutomobiliController} filtriran po REG. TABLICI.
     * Automobil se vraca {@code ObservableList<Automobil> automobili} i sada je samo jedan Automobil u listi.
     * <p>
     * Takodje, posto imamo samo jednog {@link Klijent}-a vezanog za Automobil, prosledjujemo isto i ID Klijenta
     * u  {@link AutomobiliController}.  Cinjenica da imamo samo jednog Klijenta
     * moze {@code automobili.get(0).getIdKlijenta()}. Filtriran Klijent Objekat
     * se vraca u {@code AtomicReference<ObservableList<Klijent>> klijenti}.
     *
     * @throws AcrenoException Malo bolji pregled greske
     * @throws SQLException    SQL MSACCESS greska prilikom dobijanja podataka iz MS baze
     * @author Dejan Cugalj
     * @see #txtFieldRegTablicaSaerchKeyListener
     * @see #zatvoriListViewSearchAutomobil
     * @see AutomobiliController
     */
    private void openAutomobiliUi() throws AcrenoException, SQLException {
        try {
            // Standart FX load UI
            FXMLLoader fxmlLoaderAutomobil = new FXMLLoader(getClass().getResource(Constants.AUTOMOBILI_UI_VIEW_URI));
            Stage stageAutomobil = new Stage();
            stageAutomobil.initModality(Modality.APPLICATION_MODAL);
            Scene scene = new Scene(fxmlLoaderAutomobil.load());
            stageAutomobil.setScene(scene);
            stageAutomobil.setTitle("Automobil: " + txtFieldRegOznaka.getText());

            //Set AutoServisController u AutomobiliController UI
            AutomobiliController automobiliController = fxmlLoaderAutomobil.getController();
            automobiliController.setAutoServisController(this, stageAutomobil);

            //Prosledjivanje filtriranog AUTOMOBILA po REG tablici u ObservableList "automobil"
            String regOznaka = txtFieldRegOznaka.getText(); //Uzmi reg tablicu kao filter parametar za auto
            automobili = FXCollections.observableArrayList(
                    automobilDAO.findAutomobilByProperty(AutoSearchType.BR_TABLICE, regOznaka));
            automobiliController.setAutomobil(automobili); // Prosledjivanje "Automobil" Obj u Automobil Kontroler

            //Prosledjivanje filtriranog KLIJENTA po ID AUTOMOBILA u ObservableList "klijenti"
            int idKlijenta = automobili.get(0).getIdKlijenta();//Moze automobili.get(0), jer imamo samo jednog Klijenta.
            klijenti.set(FXCollections.observableArrayList(
                    klijentDAO.findKlijentByProperty(KlijentSearchType.ID_KLIJENTA, idKlijenta)
            ));
            automobiliController.setKlijenti(klijenti.get()); // Prosledjivanje "Klijent" Obj u Automobil Kontroler

            stageAutomobil.showAndWait(); // Prikazi i cekaj Automobil UI

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Zatvori ACReno Aplikaciju
     */
    public void btnCloseApplication() {
        System.exit(0);
    }

}
