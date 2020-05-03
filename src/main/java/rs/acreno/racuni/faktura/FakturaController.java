package rs.acreno.racuni.faktura;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import rs.acreno.automobil.Automobil;
import rs.acreno.klijent.Klijent;
import rs.acreno.racuni.Racun;

import java.net.URL;
import java.util.ResourceBundle;

public class FakturaController implements Initializable {


    private ObservableList<Automobil> automobili;
    private ObservableList<Klijent> klijenti;
    private ObservableList<Racun> racuni;

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
        });
    }


}

