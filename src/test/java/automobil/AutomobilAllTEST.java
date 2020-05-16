package automobil;

import org.junit.Test;
import rs.acreno.automobil.AutoSearchType;
import rs.acreno.automobil.Automobil;
import rs.acreno.automobil.AutomobilDAO;
import rs.acreno.automobil.SQLAutomobilDAO;
import rs.acreno.klijent.Klijent;
import rs.acreno.system.exeption.AcrenoException;

import java.sql.SQLException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class AutomobilAllTEST {

    AutomobilDAO mockedAutoDAO = mock(AutomobilDAO.class);
    Automobil mockedAutoomobil = mock(Automobil.class);
    SQLAutomobilDAO automobilTest = new SQLAutomobilDAO();


    static List<Klijent> findByPropKllijenti;
    static List<Automobil> findByPropAutomobili;
    // KlijentDAO klijentDAO = new SQLiteKlijnetDAO();
    AutomobilDAO automobilDAO = new SQLAutomobilDAO();

    private final Automobil automobil;

    public AutomobilAllTEST() {
        automobil = new Automobil();
        //automobil.setIdAuta(1);
        automobil.setIdKlijenta(57);
        automobil.setRegOznaka("BG0435AA");
        automobil.setKilomteraza("128.000");
        automobil.setVrstaVozila("Golf");
        automobil.setMarkaVozila("Reno");
        automobil.setModelVozila("SCENIC");
        automobil.setGodisteVozila(2004);
        automobil.setZapreminaVozila(34535);
        automobil.setSnagaVozila(34535);
        automobil.setVinVozila("SADFSDG43534534534");
        automobil.setBrojMotoraVozila("SADFSDG43534534534");
        automobil.setVrstaGorivaVozila("SADFSDG43534534534");
        automobil.setBojaVozila("SADFSDG43534534534");
        automobil.setMasaVozila(2344);
        automobil.setNajvecaDozvoljenaMasaVozila(2344);
        automobil.setDatumPrveRegistracijeVozila("6565");
        automobil.setBrojMestaZaSedenje(1);
        automobil.setBrojVrataVozila(5);
        automobil.setNapomeneAutomobila("setNapomeneAutomobila");
        automobil.setDatumAcrRegistracijeAuta("15.5.2020");
    }


    @Test
    public void insertAutomobil() throws AcrenoException, SQLException {
        //automobil.setIdKlijenta(4);
        automobil.setRegOznaka("NS-039-CC");
        automobilTest.insertAutomobil(automobil);
       /* for (int i = 1; i < 3; i++) {
            automobil.setIdKlijenta(i);
            automobil.setRegOznaka(i + "-BG157-" + i);
            automobilTest.insertAutomobil(automobil);
        }*/
    }

    @Test
    public void findAllAutomobili() throws AcrenoException, SQLException {

        //PRONALAZENJE I PRIKAZ SVIH AUTOMOBILA
        System.out.println("******************************************************");
        System.out.println("PRIKAZ SVIH AUTOMOBILA");
        List<Automobil> automobils = automobilDAO.findAllAutomobil();
      //  assertEquals("57-BG157-57", automobils.get(0).getRegOznaka());


        for (Automobil automobil : automobils) {

            findByPropAutomobili = automobilDAO.findAllAutomobil();

            List<Automobil> automobils1 = automobilDAO.findAutomobilByProperty(AutoSearchType.KLIJNET_ID, 1);
            System.out.println(
                    " - ID AUTA: " + automobil.getIdAuta()
                            + " - ID KLIJENTA: " + automobil.getDatumAcrRegistracijeAuta()
                    //  " || IME: " + automobils1.get(0).getRegOznaka()
                    // " || MESTO: " + findByPropKllijenti.get(0).getMesto()
                    // + " - REG. OZNAKA: " +automobil.getRegOznaka()
                    // + " - NAPOMENA: " +automobil.getNapomeneAutomobila()
            );
        }
    }

    @Test
    public void findCarByProperty() throws AcrenoException, SQLException {
        System.out.println("******************************************************");
        System.out.println("FIND CAR BY PROPERTY TABLICA");
        List<Automobil> automobili = automobilDAO.findAllAutomobil();
        findByPropAutomobili = automobilDAO.findAllAutomobil();
        String exeptedLicencPlate = findByPropAutomobili.get(0).getRegOznaka();
        int exeptedIdKlijenta = findByPropAutomobili.get(0).getIdKlijenta();
        System.out.println(exeptedLicencPlate);
        assertEquals(1, exeptedIdKlijenta);

    }

    @Test
    public void updateAutomobil() throws SQLException, AcrenoException {
        Automobil podaciAuto = new Automobil();
        podaciAuto.setIdAuta(6);
        podaciAuto.setIdKlijenta(65);
        podaciAuto.setRegOznaka("BG0435AA");
        podaciAuto.setKilomteraza("128.000");
        podaciAuto.setVrstaVozila("Golf");
        podaciAuto.setMarkaVozila("Reno");
        podaciAuto.setModelVozila("SCENIC");
        podaciAuto.setGodisteVozila(2004);
        podaciAuto.setZapreminaVozila(34535);
        podaciAuto.setSnagaVozila(34535);
        podaciAuto.setVinVozila("SADFSDG43534534534");
        podaciAuto.setBrojMotoraVozila("SADFSDG43534534534");
        podaciAuto.setVrstaGorivaVozila("SADFSDG43534534534");
        podaciAuto.setBojaVozila("SADFSDG43534534534");
        podaciAuto.setMasaVozila(2344);
        podaciAuto.setNajvecaDozvoljenaMasaVozila(2344);
        podaciAuto.setDatumPrveRegistracijeVozila("12.2.2020");
        podaciAuto.setBrojMestaZaSedenje(1);
        podaciAuto.setBrojVrataVozila(5);
        podaciAuto.setDatumAcrRegistracijeAuta("15.2.2020");

        automobilDAO.updateAutomobil(podaciAuto);
    }

    @Test
    public void deleteCarFromDb() throws AcrenoException, SQLException {
        automobil.setIdAuta(1);
        automobil.setIdKlijenta(67);
        //Automobil automobil = new Automobil(3, 3);
        automobilTest.deleteAutomobil(automobil);

    }
}
