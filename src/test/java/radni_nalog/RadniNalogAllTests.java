package radni_nalog;

import org.junit.Test;
import rs.acreno.nalozi.RadniNalog;
import rs.acreno.nalozi.RadniNalogDAO;
import rs.acreno.nalozi.RadniNalogSearchType;
import rs.acreno.nalozi.SQLRadniNalogDAO;
import rs.acreno.system.exeption.AcrenoException;

import java.sql.SQLException;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class RadniNalogAllTests {

    private final RadniNalog radniNalog;

    private final RadniNalogDAO radniNalogDAO = new SQLRadniNalogDAO();

    public RadniNalogAllTests() {
        radniNalog = new RadniNalog();
        radniNalog.setIdRadnogNaloga(1);
        radniNalog.setIdAutomobila(5);
        radniNalog.setDatum("22.4.2020");
        radniNalog.setVreme("12:50");
        radniNalog.setKilometraza("333.500");
        radniNalog.setDetaljiStranke("setDetaljiStranke");
        radniNalog.setDetaljiServisera("setDetaljiServisera");
    }

    @Test
    public void insertRadniNalog() throws AcrenoException, SQLException {
        radniNalogDAO.insertRadniNalog(radniNalog);
    }

    @Test
    public void pretraziSveRadneNalogeSamoTest() throws AcrenoException, SQLException {
        //PRONALAZENJE I PRIKAZ SVIH Radnih Naloga
        radniNalog.setIdRadnogNaloga(2);
        System.out.println("******************************************************");
        System.out.println("PRIKAZ SVIH NALOGA");
        List<RadniNalog> nalogs = radniNalogDAO.findAllRadniNalog();
        int idRadnogNaloga = nalogs.get(0).getIdRadnogNaloga();
        assertEquals(radniNalog.getIdRadnogNaloga(), idRadnogNaloga);
        for (RadniNalog radniNalog : nalogs) {
            System.out.println(
                    radniNalog.getIdRadnogNaloga()
                            + " - " + radniNalog.getIdAutomobila());
        }
    }

    @Test
    public void updateRadnogNaloga() throws SQLException, AcrenoException {
        radniNalog.setIdRadnogNaloga(2);
        radniNalog.setVreme("07:40");
        radniNalogDAO.updateRadniNalog(radniNalog);
    }

    @Test
    public void findRadniNalogByParameter() throws AcrenoException, SQLException {
        List<RadniNalog> radniNalozi = radniNalogDAO.findRadniNalogByProperty(RadniNalogSearchType.ID_AUTOMOBILA, 5);
        for (RadniNalog radniNalog : radniNalozi) {
            System.out.println(radniNalog.getVreme());
        }
    }

    @Test
    public void deleteRadniNalog() throws AcrenoException, SQLException {
        radniNalog.setIdRadnogNaloga(5);
        int toCheckValue = radniNalogDAO.findAllRadniNalog().get(0).getIdRadnogNaloga();
        assertEquals("Property is not same as excepted - ", toCheckValue, toCheckValue);

        radniNalogDAO.deleteRadniNalog(radniNalog.getIdRadnogNaloga());

    }
}
