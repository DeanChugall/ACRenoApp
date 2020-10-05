package klijent;

import org.junit.Test;
import rs.acreno.klijent.Klijent;
import rs.acreno.klijent.KlijentDAO;
import rs.acreno.klijent.KlijentSearchType;
import rs.acreno.klijent.SQLKlijnetDAO;
import rs.acreno.system.exeption.AcrenoException;

import java.sql.SQLException;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class KlijentiAllTests {

    private final Klijent podaciKlijenta;

    private final KlijentDAO klijentDAO = new SQLKlijnetDAO();


    public KlijentiAllTests() {
        podaciKlijenta = new Klijent();
        //podaciKlijenta.setIdKlijenta(1);
        podaciKlijenta.setImePrezime("");
        podaciKlijenta.setMesto("");
        podaciKlijenta.setPostanskiBroj("21000");
        podaciKlijenta.setUlicaBroj("Molomora testr");
        podaciKlijenta.setBrLicneKarte("234235234");
        podaciKlijenta.setMaticniBroj("43523246456465256");
        podaciKlijenta.setOstaliDetalji("setOstaliDetalji");
        podaciKlijenta.setEmail("setEmail");
        podaciKlijenta.setTelefonMobilni("435345345");
        podaciKlijenta.setTelefonFiksni("234234234");
        podaciKlijenta.setWeb("www.google.com");
        podaciKlijenta.setBrojRacuna("4353453455234");
        podaciKlijenta.setBanka("MOBI BANKA");
        podaciKlijenta.setDatumAcrRegistracijeKliljenta("11.10.2000");

    }

    @Test
    public void insertKlijent() throws AcrenoException, SQLException {
        // podaciKlijenta.setIdKlijenta(i);
        podaciKlijenta.setImePrezime("Miroljub Stanic");
        podaciKlijenta.setMesto("Smederevo");
        klijentDAO.insertKlijnet(podaciKlijenta);
       /* for (int i = 0; i < 50; i++) {
            podaciKlijenta.setIdKlijenta(i);
            podaciKlijenta.setImePrezime(i + "-Dejan");
            podaciKlijenta.setMesto(i + " -Novi Sad");
            klijentDAO.insertKlijnet(podaciKlijenta);
        }*/

    }

    @Test
    public void pretraziSveKlijenteSamoTest() throws AcrenoException, SQLException {
        //PRONALAZENJE I PRIKAZ SVIH KLIJENATA
        System.out.println("******************************************************");
        System.out.println("PRIKAZ SVIH KLIJENATA");
        List<Klijent> klijents = klijentDAO.findAllKlijents();
        int idKlijneta = klijents.get(0).getIdKlijenta();
        assertEquals(1, idKlijneta);
        for (Klijent klijent : klijents) {
            System.out.println(
                    klijent.getIdKlijenta()
                            + " - " + klijent.getImePrezime()
                            + " - " + klijent.getMesto());
        }
    }

    @Test
    public void findKlijnetByParameter() throws AcrenoException, SQLException {
        //********************KLIJENTI***************************
        SQLKlijnetDAO sqLiteKlijnetDAO = new SQLKlijnetDAO();
        List<Klijent> klijents = sqLiteKlijnetDAO.findKlijentByProperty(KlijentSearchType.ID_KLIJENTA, 1);
        System.out.println(klijents.get(0).getIdKlijenta());
        assertEquals(1, klijents.get(0).getIdKlijenta());

    }

    @Test
    public void updateKlijent() throws SQLException, AcrenoException {

        podaciKlijenta.setIdKlijenta(38);
        podaciKlijenta.setImePrezime("Dex Cugs");
        klijentDAO.updateKlijent(podaciKlijenta);
    }


    @Test
    public void deleteKlijent() throws AcrenoException, SQLException {
        podaciKlijenta.setIdKlijenta(3);
        int fromKlijentObject = podaciKlijenta.getIdKlijenta();
        int toCheckValue = 3;
        assertEquals("Property is not same as excepted - ", toCheckValue, fromKlijentObject);

        klijentDAO.deleteKlijent(podaciKlijenta);

    }
}
