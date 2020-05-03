package rs.acreno.system.util;

import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import org.jetbrains.annotations.NotNull;

public class GeneralUiUtility {

    /**
     * Cisti podatke iz svih TextFiel Polja u nekom Panelu odjednom.
     * Ovde se koristi:
     *
     * @param pane Pane u kom se nalaze TextField polja.
     *             TODO: Prebaciti u klasu Utility
     */
    public static void clearTextFieldsInPane(@NotNull Pane pane) {
        for (Node node : pane.getChildren()) {
            if (node instanceof TextField) {
                ((TextField) node).setText("");
                node.setDisable(false);
            }
            if (node instanceof TextArea) {
                ((TextArea) node).setText("");
            }
        }
    }

    /**
     * AlertBox kada se uradi akcija na nekom dugmetu
     *
     * @param alertType  Tip alerta Alert.AlertType.INFORMATION ili vec
     * @param headerText Naslov hedera
     * @param title      Naslov poruke
     * @param poruka     Poruka u alert
     * @return Alert
     */
    public static @NotNull Alert alertDialogBox(Alert.AlertType alertType, String headerText, String title, String poruka) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(poruka);
        alert.show();
        return alert;
    }
}

