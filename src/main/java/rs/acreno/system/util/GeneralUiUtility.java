package rs.acreno.system.util;

import javafx.print.*;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.transform.Scale;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import rs.acreno.racuni.faktura.FakturaController;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

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

    public static void printStaff(AnchorPane ancorPanePrint, Button btnPrint) {
        PrinterJob printJobRadniNalog = PrinterJob.createPrinterJob();

        if (printJobRadniNalog != null && printJobRadniNalog.showPrintDialog(ancorPanePrint.getScene().getWindow())) {
            btnPrint.setVisible(false);
            Printer printer = printJobRadniNalog.getPrinter();
            printJobRadniNalog.getJobSettings().setPrintQuality(PrintQuality.HIGH);
            PageLayout pageLayout = printer.createPageLayout(Paper.A4, PageOrientation.PORTRAIT, Printer.MarginType.HARDWARE_MINIMUM);

            double scaleX = pageLayout.getPrintableWidth() / ancorPanePrint.getBoundsInParent().getWidth();
            ancorPanePrint.getTransforms().add(new Scale(scaleX, scaleX));

            boolean success = printJobRadniNalog.printPage(pageLayout, ancorPanePrint);
            if (success) {
                printJobRadniNalog.endJob();
                GeneralUiUtility.alertDialogBox(Alert.AlertType.INFORMATION
                        , "USPEŠAN PRINT"
                        , "PRINT"
                        , "Uspešno Printanje" + printJobRadniNalog.getJobStatus());
            } else {
                GeneralUiUtility.alertDialogBox(Alert.AlertType.ERROR
                        , "GREŠKA PRINT"
                        , "PRINT"
                        , "GREŠKA U PRINTU" + printJobRadniNalog.getJobStatus());
            }
            btnPrint.setVisible(true);
        }
    }

    /**
     * Promena datum a u ono sto nama treba za Srbiju. FORMAT "dd.MM.yyyy"
     * Koristimo svuda gde treba da se postavi datum...npr. iz DatePckera u bazu pa jedna od koriscenja je
     * {@link rs.acreno.racuni.faktura.FakturaController #newOrEditRacun} i u
     * {@link FakturaController #btnSacuvajRacunAction()}
     * @param date uzima LocalDate i vraca Formatiran String
     */
    public static @NotNull String formatDateForUs(@NotNull LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        return date.format(formatter);
    }

    /**
     * Promena datum a u ono sto nama treba za Srbiju. FORMAT "dd.MM.yyyy"
     * Koristimo svuda gde treba da se postavi datum...npr. iz DatePckera u bazu pa jedna od koriscenja je
     * {@link rs.acreno.racuni.faktura.FakturaController #newOrEditRacun} i u
     * {@link FakturaController #btnSacuvajRacunAction()}
     * @param string Uzima String i vraca formatiran LocalDate
     */
    public static @Nullable LocalDate fromStringDate(String string) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        return LocalDate.parse(string, formatter);
    }

    public static String formatDecimalPlaces(double sum) {
        return  new DecimalFormat("###,###.00").format(sum);
    }
}

