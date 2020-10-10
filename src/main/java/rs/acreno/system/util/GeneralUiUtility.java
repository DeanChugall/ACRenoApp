package rs.acreno.system.util;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.print.*;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.jetbrains.annotations.NotNull;
import rs.acreno.racuni.faktura.FakturaController;
import rs.acreno.system.constants.Constants;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.FileSystems;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.TimerTask;

public class GeneralUiUtility {

    /**
     * Cisti podatke iz svih TextFiel Polja u nekom Panelu odjednom.
     * Ovde se koristi:
     *
     * @param pane Pane u kom se nalaze TextField polja.
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
        ((Stage) alert.getDialogPane().getScene().getWindow()).getIcons().add(new Image(Constants.APP_ICON));
        alert.show();
        return alert;
    }

    /**
     * TODO: Napisati Java Doc
     *
     * @param ancorPanePrint
     * @param btnPrint
     * @param closePrint
     */
    public static void printStaff(AnchorPane ancorPanePrint, Button btnPrint, Button closePrint) {
        PrinterJob printJobRadniNalog = PrinterJob.createPrinterJob();

        if (printJobRadniNalog != null && printJobRadniNalog.showPrintDialog(ancorPanePrint.getScene().getWindow())) {
            btnPrint.setVisible(false);
            closePrint.setVisible(false);
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
            closePrint.setVisible(true);
        }
    }

    /**
     * Promena datum a u ono sto nama treba za Srbiju. FORMAT "dd.MM.yyyy"
     * Koristimo svuda gde treba da se postavi datum...npr. iz DatePckera u bazu pa jedna od koriscenja je
     * {@link rs.acreno.racuni.faktura.FakturaController #newOrEditRacun} i u
     * {@link FakturaController #btnSacuvajRacunAction()}
     *
     * @param date uzima LocalDate i vraca Formatiran String
     */
    public static @NotNull String formatDateForUs(@NotNull LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        return date.format(formatter).trim();
    }

    /**
     * Promena datum a u ono sto nama treba za Srbiju. FORMAT "dd.MM.yyyy"
     * Koristimo svuda gde treba da se postavi datum...npr. iz DatePckera u bazu pa jedna od koriscenja je
     * {@link rs.acreno.racuni.faktura.FakturaController #newOrEditRacun} i u
     * {@link FakturaController #btnSacuvajRacunAction()}
     *
     * @param string Uzima String i vraca formatiran LocalDate
     */
    public static LocalDate fromStringDate(String string) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        return LocalDate.parse(string.trim(), formatter);
    }

    /**
     * //TODO: Napisati DOC
     *
     * @param sum double koji se formatra
     * @return String pa se posle radi parse to Double
     */
    public static String formatDecimalPlaces(double sum) {
        return new DecimalFormat("###,###.00").format(sum);
    }

    /**
     * Postavljenje stat koji se prikazuje u Labelu
     *
     * @param label     gde prikazijemo sat
     * @param formatter format sata
     */
    public static void initSat(Label label, DateTimeFormatter formatter) {
        Timeline clock = new Timeline(new KeyFrame(Duration.ZERO, e -> {
            label.setText(LocalDateTime.now().format(formatter));
            label.setAlignment(Pos.CENTER);
        }), new KeyFrame(Duration.seconds(1)));
        clock.setCycleCount(Animation.INDEFINITE);
        clock.play();
    }

    //CHECK INTERNET
    private TimerTask tt;

    public static boolean netIsAvailable() {
        try {
            final URL url = new URL("http://www.google.com");
            final URLConnection conn = url.openConnection();
            conn.connect();
            conn.getInputStream().close();
            return true;
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            return false;
        }
    }

    //From https://stackoverflow.com/questions/16273318/transliteration-from-cyrillic-to-latin-icu4j-java
    public static String transliterate(String message) {
        char[] abcCyr = {' ', 'а', 'б', 'в', 'г', 'д', 'ђ', 'e', 'ж', 'з', 'и', 'ј', 'к', 'л', 'љ', 'м', 'н', 'њ', 'о', 'п', 'р', 'с', 'т', 'у', 'ф', 'х', 'ц', 'ч', 'ш', 'ć', 'А', 'Б', 'В', 'Г', 'Д', 'Е', 'Ж', 'З', 'И', 'Ј', 'К', 'Л', 'Љ', 'М', 'Н', 'О', 'П', 'Р', 'С', 'Т', 'У', 'Ф', 'Х', 'Ц', 'Ч', 'Ш', 'Щ', 'Ъ', 'Ы', 'Ь', 'Э', 'Ю', 'Я', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
        String[] abcLat = {" ", "a", "b", "v", "g", "d", "đ", "e", "ž", "z", "i", "j", "k", "l", "lj", "m", "n", "nj", "o", "p", "r", "s", "t", "u", "f", "h", "c", "č", "š", "ć", "A", "B", "V", "G", "D", "E", "Ž", "Z", "I", "Ј", "K", "L", "LJ", "M", "N", "O", "P", "R", "S", "T", "U", "F", "H", "C", "Č", "Š", "Sch", "", "I", "", "E", "Ju", "Ja", "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < message.length(); i++) {
            for (int x = 0; x < abcCyr.length; x++) {
                if (message.charAt(i) == abcCyr[x]) {
                    builder.append(abcLat[x]);
                }
            }
        }
        return builder.toString();
    }

    /**
     * TODO: Popuniti jos marki automobila
     * Metoda koja menja logoe na pocetnom GUIu kada se izabere ili ucita {@link rs.acreno.automobil.Automobil}
     *
     * @param markaAutomobila prosledjujemo marku automobila
     */
    public static String ucitajLogoAutomobila(String markaAutomobila) {
        String imageSource = "";
        if (markaAutomobila.toLowerCase().contains("renau")) {
            imageSource = "marke_automobila/renault_logo.png";
        } else if (markaAutomobila.toLowerCase().contains("vw")) {
            imageSource = "marke_automobila/vw_logo.png";
        } else if (markaAutomobila.toLowerCase().contains("dacia")) {
            imageSource = "marke_automobila/dacia_logo.png";
        } else if (markaAutomobila.toLowerCase().contains("fiat")) {
            imageSource = "marke_automobila/fiat_logo.png";
        } else if (markaAutomobila.toLowerCase().contains("citroe")) {
            imageSource = "marke_automobila/citroen_logo.png";
        } else if (markaAutomobila.toLowerCase().contains("peug")) {
            imageSource = "marke_automobila/pezo_logo.png";
        } else if (markaAutomobila.toLowerCase().contains("koda")) {
            imageSource = "marke_automobila/skoda_logo.png";
        } else if (markaAutomobila.toLowerCase().contains("bmw")) {
            imageSource = "marke_automobila/bmw_logo.png";
        } else {
            imageSource = "marke_automobila/acr_car.png";
        }
        return imageSource;
    }

    /**
     * Odredjivanje apsolutne putanje gde je pokrenut JAR
     *
     * @return String absolutePath
     */
    public static String getExecutionPath(){
        String absolutePath = GeneralUiUtility.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        absolutePath = absolutePath.substring(0, absolutePath.lastIndexOf("/"));
        absolutePath = absolutePath.replaceAll("%20"," "); // Surely need to do this here
        return absolutePath;
    }

    /**
     * Vraca sistemski separator u zavisnosti od OSa
     *
     * @return String system separator
     */
    public static String getSystemSeparator() {
        return FileSystems.getDefault().getSeparator();
    }

    /**
     *Ako hocemo da u Stringu budu samo brojevi, Provera da li u Stringu ima slova.
     *
     * @param unos String
     * @return boolean Vrati vrednost TRUE or FALSE
     */
    public static boolean proveriStringDaLiImaSlova(String unos) {
        return unos.matches(".*[a-zA-Z]+.*");
    }
}

