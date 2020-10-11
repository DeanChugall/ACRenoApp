package rs.acreno.system.pretraga.ui_pretraga;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class PretragaUiControler {
    public void btnActZatvoriPretragu(ActionEvent actionEvent) {
        ((Stage) (((Button) actionEvent.getSource()).getScene().getWindow())).close();
    }

    public void btnPonistiPretraguAct(ActionEvent actionEvent) {
        System.out.println("PONISTI PRETRAGU");
    }
}
