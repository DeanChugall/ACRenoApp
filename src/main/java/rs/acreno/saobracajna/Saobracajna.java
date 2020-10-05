package rs.acreno.saobracajna;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import rs.acreno.automobil.Automobil;
import rs.acreno.saobracajna.tools.EvrcCard;
import rs.acreno.saobracajna.tools.EvrcInfo;

import javax.smartcardio.Card;
import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.TerminalFactory;
import java.util.List;
import java.util.Scanner;

public class Saobracajna {

    private static final Logger logger = Logger.getLogger(Saobracajna.class);

    public static CardTerminal pickTerminal(List<CardTerminal> terminals) {
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

    public static Automobil automobil() {
        Automobil automobil = new Automobil();
        CardTerminal terminal = null;
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
            System.out.println(info.toJSON());
            JSONObject reader = new JSONObject(info.toJSON());
            JSONObject customer = (JSONObject) reader.get("document");
            System.out.println(customer.get("issuing_date"));

            JSONObject vozilo = (JSONObject) reader.get("vehicle");
            automobil.setRegOznaka(vozilo.get("registration_number").toString());

        } catch (CardException e) {
            e.printStackTrace();
        }
        logger.info("********** OCITANA SAOBRACAJNA **********");
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
