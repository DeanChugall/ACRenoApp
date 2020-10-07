package rs.acreno.automobil.saobracajna;

import javafx.scene.control.Alert;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONObject;
import rs.acreno.automobil.Automobil;
import rs.acreno.automobil.saobracajna.tools.EvrcCard;
import rs.acreno.automobil.saobracajna.tools.EvrcInfo;
import rs.acreno.klijent.Klijent;
import rs.acreno.system.util.GeneralUiUtility;

import javax.smartcardio.Card;
import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.TerminalFactory;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

public class Saobracajna {

    private static final Logger logger = Logger.getLogger(Saobracajna.class);

    public static CardTerminal pickTerminal(@NotNull List<CardTerminal> terminals) {
        if (terminals.size() > 1) {
            System.out.println("Available readers:\n");
            int c = 1;
            for (CardTerminal terminal : terminals) {
                System.out.format("%d) %s\n", c++, terminal);
            }

            @SuppressWarnings("resource")
            Scanner in = new Scanner(System.in);
            while (true) {
                System.out.print("Select number: ");
                System.out.flush();

                c = in.nextInt();
                if (c > 0 && c < terminals.size()) {
                    return terminals.get(c);
                }
            }
        } else {
            return terminals.get(0);
        }
    }

    static CardTerminal terminal = null;
    public static Klijent klijent;

    public static @NotNull Automobil automobil() {
        Automobil automobil = new Automobil();
        // get the terminal
        try {
            TerminalFactory factory = TerminalFactory.getDefault();
            terminal = pickTerminal(factory.terminals().list());

        } catch (CardException e) {
            GeneralUiUtility.alertDialogBox(Alert.AlertType.ERROR, "Using reader   : " + terminal,
                    "Greška u nije prepoznat čitač saobraćajne...", "**********GREŠKA---NIJE PREPOZNAT ČITAČ **********\"");
            final Runnable runnable = (Runnable) Toolkit.getDefaultToolkit().getDesktopProperty("win.sound.exclamation");
            if (runnable != null) {
                runnable.run();
            }
            logger.error("**********GREŠKA---NIJE PREPOZNAT ČITAČ **********");
        }
        try {
            // establish a connection with the card
            Card card = terminal.connect("*");
            GeneralUiUtility.alertDialogBox(Alert.AlertType.INFORMATION, "Using reader   : " + terminal,
                    "Čitanje Saobraćajne...", "Očitana saobracajna...");
            // read evrc data
            EvrcCard evrccard = new EvrcCard(card);
            EvrcInfo info = evrccard.readEvrcInfo();
            JSONObject reader = new JSONObject(info.toJSON());

            JSONObject vozilo = (JSONObject) reader.get("vehicle");
            automobil.setRegOznaka(vozilo.get("registration_number").toString());
            automobil.setVinVozila(vozilo.get("id_number").toString());
            automobil.setMarkaVozila(vozilo.get("make").toString());
            automobil.setModelVozila(vozilo.get("commercial_description").toString());
            automobil.setVrstaVozila(vozilo.get("vehicle_category").toString());
            automobil.setGodisteVozila(Integer.parseInt(vozilo.get("production_year").toString()));
            automobil.setZapreminaVozila(Integer.parseInt(vozilo.get("engine_capacity").toString()));
            automobil.setSnagaVozila(Integer.parseInt(vozilo.get("max_net_power").toString()));
            automobil.setVrstaGorivaVozila(vozilo.get("fuel_type").toString());
            automobil.setBrojMotoraVozila(vozilo.get("engine_id_number").toString());
            automobil.setBojaVozila(vozilo.get("color").toString());
            automobil.setMasaVozila(Integer.parseInt(vozilo.get("mass").toString()));
            automobil.setBrojVrataVozila(5);
            automobil.setNajvecaDozvoljenaMasaVozila(Integer.parseInt(vozilo.get("max_permissible_laden_mass").toString()));

            LocalDate ld = LocalDate.parse(vozilo.get("first_registration_date").toString(), DateTimeFormatter.ofPattern("yyyyMMdd"));
            int year = ld.getYear();
            int month = ld.getMonthValue();
            int day = ld.getDayOfMonth();
            System.out.println(month + " " + day + " " + year);
            automobil.setDatumPrveRegistracijeVozila((month + "." + day + "." + year + "."));

            automobil.setBrojMestaZaSedenje(5);

            klijent = new Klijent();
            JSONObject vlasnik = (JSONObject) reader.get("personal");
            String imePrezime = (vlasnik.get("owner_first_name") + " " +
                    vlasnik.get("owner_legal_name")).toLowerCase();
            String maticniBroj = String.valueOf(vlasnik.get("owner_personalno"));
            String adresa = String.valueOf(vlasnik.get("owner_address"));
            klijent.setImePrezime(imePrezime);
            klijent.setMaticniBroj(maticniBroj);
            klijent.setUlicaBroj(adresa);
            System.out.println("**********************  " + imePrezime + " *************************");


            logger.info("********** OCITANA SAOBRACAJNA **********" + ": " + automobil.getRegOznaka());
            final Runnable runnable = (Runnable) Toolkit.getDefaultToolkit().getDesktopProperty("win.sound.exclamation");
            if (runnable != null) {
                runnable.run();
            }

        } catch (CardException e) {
            e.printStackTrace();
            GeneralUiUtility.alertDialogBox(Alert.AlertType.ERROR, "Using reader   : " + terminal,
                    "Greška u čitanju Saobraćajne...", "**********GREŠKA U ČITANJU SAOBRAĆAJNE **********");
            final Runnable runnable = (Runnable) Toolkit.getDefaultToolkit().getDesktopProperty("win.sound.default");
            if (runnable != null) {
                runnable.run();
            }
            logger.error("**********GREŠKA U ČITANJU SAOBRACAJNE **********");
        }


        return automobil;
    }


    /* public static void main(String[] args) {
     *//*  CardTerminal terminal = null;

        // get the terminal
        try {
            TerminalFactory factory = TerminalFactory.getDefault();
            terminal = pickTerminal(factory.terminals().list());

            System.out.println("Using reader   : " + terminal);
        } catch (CardException e) {
            System.err.println("Missing card reader.");
        }

        try {
            // establish a connection with the card
            Card card = terminal.connect("*");

            // read evrc data
            EvrcCard evrccard = new EvrcCard(card);
            EvrcInfo info = evrccard.readEvrcInfo();
            System.out.println("***********************************************************");
            System.out.println("***********************************************************");
            System.out.println("***********************************************************");
            System.out.println(info.toJSON());
            System.out.println("***********************************************************");
            System.out.println("***********************************************************");
            JSONObject reader = new JSONObject(info.toJSON());
            JSONObject customer = (JSONObject) reader.get("document");
            System.out.println(customer.get("issuing_date"));

            JSONObject vozilo = (JSONObject) reader.get("vehicle");
            System.out.println("Registarska Oznaka: " + vozilo.get("registration_number"));


        } catch (CardException e) {
            e.printStackTrace();
        }*//*
        System.out.println("****************>>>>>> REG: " + Saobracajna.automobil().getRegOznaka());
    }*/
}
