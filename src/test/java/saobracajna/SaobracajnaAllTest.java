package saobracajna;

import org.json.simple.JSONObject;
import org.junit.Test;
import rs.acreno.automobil.saobracajna.Saobracajna;
import rs.acreno.automobil.saobracajna.tools.EvrcCard;
import rs.acreno.automobil.saobracajna.tools.EvrcInfo;

import javax.smartcardio.Card;
import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.TerminalFactory;

import static rs.acreno.automobil.saobracajna.Saobracajna.pickTerminal;

public class SaobracajnaAllTest {
    @Test
     public static void main(String[] args) {
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
            System.out.println("***********************************************************");
            System.out.println("***********************************************************");
            System.out.println("***********************************************************");
            System.out.println(info.toJSON());
            System.out.println("***********************************************************");
            System.out.println("***********************************************************");
        } catch (CardException e) {
            e.printStackTrace();
        }
    }
}
