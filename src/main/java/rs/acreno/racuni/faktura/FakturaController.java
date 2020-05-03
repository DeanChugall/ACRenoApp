package rs.acreno.racuni.faktura;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import rs.acreno.automobil.Automobil;
import rs.acreno.klijent.Klijent;
import rs.acreno.racuni.Racun;
import rs.acreno.racuni.RacuniDAO;
import rs.acreno.racuni.SQLRacuniDAO;
import rs.acreno.system.exeption.AcrenoException;

import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class FakturaController implements Initializable {


    public TextField txtFieldBrojRacuna;
    public TextField txtFieldImeKlijenta;
    public TextField txtFieldRegTablica;

    private ObservableList<Automobil> automobili;
    private ObservableList<Klijent> klijenti;
    private ObservableList<Racun> racuni;

    //Inicijalizacija Racuni Objekta
    private final RacuniDAO racuniDAO = new SQLRacuniDAO();

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
            } catch (AcrenoException | SQLException e) {
                e.printStackTrace();
            }
            System.out.println("ID RACUNA: " + racuni.toString());

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

