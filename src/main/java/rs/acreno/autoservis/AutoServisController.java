package rs.acreno.autoservis;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
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
import rs.acreno.racuni.Racun;
import rs.acreno.racuni.RacuniDAO;
import rs.acreno.racuni.SQLRacuniDAO;
import rs.acreno.system.constants.Constants;
import rs.acreno.system.exeption.AcrenoException;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AutoServisController implements Initializable {

    public ListView<Automobil> listViewAutmobiliSearch;
    public TextField txtFieldRegOznaka;
    //Inicijalizacija AUTOMOBIL Objekta
    private final AutomobilDAO automobilDAO = new SQLAutomobilDAO();
    private  ObservableList<Automobil> automobili =
            FXCollections.observableArrayList(automobilDAO.findAllAutomobil());
    //Inicijalizacija Klijent Objekta
    private final KlijentDAO klijentDAO = new SQLKlijnetDAO();
    private final ObservableList<Klijent> klijenti =
            FXCollections.observableArrayList(klijentDAO.findAllKlijents());
    //Inicijalizacija Racuni Objekta
    private final RacuniDAO racuniDAO = new SQLRacuniDAO();
    private final ObservableList<Racun> racuni =
            FXCollections.observableArrayList(racuniDAO.findAllRacune());

    @FXML
    public Button btnOpenAutomobili;

    public AutoServisController() throws AcrenoException, SQLException {
    }


    @FXML
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // TODO
        btnOpenAutomobili.setOnAction(e -> {
            ((Stage) ((Node) e.getSource()).getScene().getWindow()).hide();
            showAutomobiliUi();
            ((Stage) ((Node) e.getSource()).getScene().getWindow()).show();

        });
    }

    public void showAutomobiliUi() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(AutoServisController.class.getResource(Constants.AUTOMOBILI_UI_VIEW_URI));
            Parent root1 = (Parent) fxmlLoader.load();

            //Referenca ka AutomobiliController
            AutomobiliController automobiliController = fxmlLoader.getController();
            String regOznaka = txtFieldRegOznaka.getText(); //Uzmi reg tablicu kao parametar za auto
            //Temp lista za cuvanje samo jednog filtriranog auta
            automobili = FXCollections.observableArrayList(
                    automobilDAO.findAutomobilByProperty(AutoSearchType.BR_TABLICE, regOznaka));

            automobiliController.setAutomobil(automobili); //Prosledjivanje filtriranog Auto Objekta

            // Prosledjivanje filtriranog Klijent Objekta ka AutomobiliController-u
            automobiliController.setKlijenti(FXCollections.observableArrayList(
                    klijentDAO.findKlijentByProperty(KlijentSearchType.ID_KLIJENTA,
                            automobili.get(0).getIdKlijenta())
            ));
            //Prosledjivanje Filtriranih racuna
            automobiliController.setRacuni(racuni);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            //stage.initStyle(StageStyle.UNDECORATED);
            stage.setTitle(txtFieldRegOznaka.getText());
            stage.setScene(new Scene(root1));
            stage.showAndWait();
        } catch (IOException ex) {
            Logger.getLogger(AutoServisController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (AcrenoException | SQLException e) {
            e.printStackTrace();
        }
    }

    public void btnOpenAutomobiliMouseEvent(MouseEvent mouseEvent) {
    }

    public void btnRegTablicaSaerchKeyListener(KeyEvent keyEvent) {
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

        } else if (mouseEvent.getButton().equals(MouseButton.PRIMARY) && mouseEvent.getClickCount() == 1) {
            //System.out.println(tblRadniNalog.getSelectionModel().getSelectedItem().getIdAutomobila());
        }
    }
}
