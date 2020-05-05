package rs.acreno.autoservis;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;
import rs.acreno.automobil.*;
import rs.acreno.klijent.Klijent;
import rs.acreno.klijent.KlijentDAO;
import rs.acreno.klijent.KlijentSearchType;
import rs.acreno.klijent.SQLKlijnetDAO;
import rs.acreno.system.exeption.AcrenoException;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class AutoServisController implements Initializable {

    public ListView<Automobil> listViewAutmobiliSearch;
    public TextField txtFieldRegOznaka;
    //Inicijalizacija AUTOMOBIL Objekta
    private final AutomobilDAO automobilDAO = new SQLAutomobilDAO();
    private ObservableList<Automobil> automobili =
            FXCollections.observableArrayList(automobilDAO.findAllAutomobil());
    //Inicijalizacija Klijent Objekta
    private final KlijentDAO klijentDAO = new SQLKlijnetDAO();
    private ObservableList<Klijent> klijenti =
            FXCollections.observableArrayList(klijentDAO.findAllKlijents());

    public ObservableList<Automobil> getAutomobili() {
        return automobili;
    }

    public void setAutomobili(ObservableList<Automobil> automobili) {
        this.automobili = automobili;
    }

    @FXML
    public Button btnOpenAutomobili;

    public AutoServisController() throws AcrenoException, SQLException {
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }


    public void showAutomobiliUi() throws AcrenoException, SQLException {
        AutomobiliController automobiliController = new AutomobiliController(this);
        try {
            //Prosledjivanje filtriranog AUTOMOBILA po REG tablici
            String regOznaka = txtFieldRegOznaka.getText(); //Uzmi reg tablicu kao parametar za auto
            automobili = FXCollections.observableArrayList(
                    automobilDAO.findAutomobilByProperty(AutoSearchType.BR_TABLICE, regOznaka));
            automobiliController.setAutomobil(automobili);

            //Prosledjivanje filtriranog KLIJENTA po ID AUTOMOBILA
            int idKlijenta = automobili.get(0).getIdKlijenta();
            klijenti = FXCollections.observableArrayList(
                    klijentDAO.findKlijentByProperty(KlijentSearchType.ID_KLIJENTA, idKlijenta)
            );
            automobiliController.setKlijenti(klijenti);


        } catch (AcrenoException | SQLException e) {
            e.printStackTrace();
        }

        // Show the new stage/window
        automobiliController.showAutmobilStage();

    }


    public void txtFieldRegTablicaSaerchKeyListener(KeyEvent keyEvent) {
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

    // Zatvara popUp ListView pretrage i setuje selektovanu vrednost u TF sa double click
    public void zatvoriListViewSearchAutomobil(@NotNull MouseEvent mouseEvent) throws AcrenoException, SQLException {
        //Na dupli click vraca Radni Nalog Objekat i otvara Radni nalog Dashboard
        if (mouseEvent.getButton().equals(MouseButton.PRIMARY) && mouseEvent.getClickCount() == 2) {
            String regOznaka = listViewAutmobiliSearch.getSelectionModel().getSelectedItems().get(0).getRegOznaka();

            txtFieldRegOznaka.setText(regOznaka);
            listViewAutmobiliSearch.setVisible(false);
            ((Node) mouseEvent.getSource()).getScene().getWindow().hide();
            showAutomobiliUi();
            ((Stage) ((Node) mouseEvent.getSource()).getScene().getWindow()).show();

        } else if (mouseEvent.getButton().equals(MouseButton.PRIMARY) && mouseEvent.getClickCount() == 1) {
            //System.out.println(tblRadniNalog.getSelectionModel().getSelectedItem().getIdAutomobila());
        }
    }

    public void btnOpenAutomobiliMouseEvent(MouseEvent mouseEvent) {
        System.exit(0);
    }

}
