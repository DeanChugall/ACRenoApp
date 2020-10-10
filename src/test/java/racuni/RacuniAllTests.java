package racuni;

import org.junit.Test;
import rs.acreno.racuni.Racun;
import rs.acreno.racuni.RacuniDAO;
import rs.acreno.racuni.SQLRacuniDAO;
import rs.acreno.system.exeption.AcrenoException;

import java.sql.SQLException;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class RacuniAllTests {

    private final Racun racun;


    private final RacuniDAO racuniDAO = new SQLRacuniDAO();

    public RacuniAllTests() {
        racun = new Racun();
        //racun.setIdRacuna(1);
        racun.setIdAutomobila(3);
        racun.setDatum("12.4.2020");
        racun.setPopust(10);
        racun.setNapomeneRacuna("setNapomeneRacuna");
    }


    @Test
    public void insertRacun() throws AcrenoException, SQLException {

        racuniDAO.insertRacun(racun);
    }

    @Test
    public void pretraziSveRacuneSamoTest() throws AcrenoException, SQLException {
        //PRONALAZENJE I PRIKAZ SVIH Racuna
        System.out.println("******************************************************");
        System.out.println("PRIKAZ SVIH USLUGA");
        List<Racun> racuns = racuniDAO.findAllRacune();
        int idRacuna = racuns.get(0).getIdRacuna();
        assertEquals(1, idRacuna);
        for (Racun racun : racuns) {
            System.out.println(
                    racun.getIdRacuna()
                            + " - " + racun.getIdRacuna()
                            + " - " + racun.getDatum());
        }
    }

    @Test
    public void findRacuneByParameter() throws AcrenoException, SQLException {
        //********************USLUGE***************************
        int idUsluge = racuniDAO.findAllRacune().get(0).getIdRacuna();
        assertEquals(1, idUsluge);
        System.out.println(idUsluge);
    }

    @Test
    public void updateRacuna() throws SQLException, AcrenoException {
        racun.setIdRacuna(1);
        racun.setDatum("popunjavanjePoljaSaLicneKarte datum");
        racuniDAO.updateRacun(racun);
    }

    @Test
    public void deleteRacun() throws AcrenoException, SQLException {
        int fromRacunObject = racun.getIdRacuna();
        int toCheckValue = 1;
        assertEquals("Property is not same as excepted - ", toCheckValue, fromRacunObject);

        racuniDAO.deleteRacun(1);

    }
}
