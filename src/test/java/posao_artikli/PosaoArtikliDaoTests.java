package posao_artikli;

import org.junit.Test;
import rs.acreno.artikli.posao_artikli_dao.PosaoArtikli;
import rs.acreno.artikli.posao_artikli_dao.PosaoArtikliDAO;
import rs.acreno.artikli.posao_artikli_dao.SQLPosaoArtikliDAO;
import rs.acreno.system.exeption.AcrenoException;

import java.sql.SQLException;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class PosaoArtikliDaoTests {

    private final PosaoArtikli posaoArtikli;

    //private final KlijentDAO klijentDAO = new SQLiteKlijnetDAO();
    //private final AutomobilDAO automobilDAO = new SQLiteAutomobilDAO();
    //private final DefektazaDAO defektazaDAO = new SQLiteDefektazaDAO();
    //private final UslugeDAO uslugeDAO = new SQLiteUslugeDAO();
    //private final ArtikliDAO artikliDAO = new SQLiteArtikliDAO();
    //private final RadniNalogDAO radniNalogDAO = new SQLRadniNalogDAO();
    private final PosaoArtikliDAO posaoArtikliDAO = new SQLPosaoArtikliDAO();

    public PosaoArtikliDaoTests() {
        posaoArtikli = new PosaoArtikli();
        posaoArtikli.setCena(1444);
        posaoArtikli.setNabavnaCena(184447);
        posaoArtikli.setKolicina(14442);
        posaoArtikli.setJedinicaMere("tegdfgdfgst");
        posaoArtikli.setPopust(35);
        posaoArtikli.setOpisPosaoArtiklli("tedfgdgete");
        posaoArtikli.setDetaljiPosaoArtikli("33dfgdfg3.500");
    }


    @Test
    public void insertPosaoArtiklDao() throws AcrenoException, SQLException {
        posaoArtikli.setIdRacuna(2);
        posaoArtikli.setIdArtikla(3);
        posaoArtikliDAO.insertPosaoArtikliDao(posaoArtikli);
    }


    @Test
    public void pretraziSvePosaoArtikleDaoTest() throws AcrenoException, SQLException {
        //PRONALAZENJE I PRIKAZ SVIH Radnih Naloga
        System.out.println("******************************************************");
        System.out.println("PRIKAZ SVIH pretraziSvePosaoArtikleDaoTest");
        List<PosaoArtikli> posaoArtiklis = posaoArtikliDAO.findAllPosaoArtikleDao();
        //int idRacuna = posaoArtikli.setIdRacuna(1);
        //int idRadnogNaloga = posaoArtiklis.get(0).getIdRacuna();
        //assertEquals(posaoArtikli.getIdRacuna(), idRadnogNaloga);
        for (PosaoArtikli posaoArtikli : posaoArtiklis) {
            System.out.println(
                    posaoArtikli.getIdRacuna()
                            + " - " + posaoArtikli.getIdArtikla());
        }
    }

    @Test
    public void updatePosaoArtikleDao() throws SQLException, AcrenoException {
        posaoArtikli.setIdPosaoArtikli(1);
        posaoArtikli.setIdRacuna(4);
        posaoArtikli.setIdArtikla(3);
        posaoArtikliDAO.updatePosaoArtikliDao(posaoArtikli);
    }

    @Test
    public void findPosaoArtikleDaoByParameter() throws AcrenoException, SQLException {

        int idRadnogNaloga = posaoArtikliDAO.findAllPosaoArtikleDao().get(0).getIdPosaoArtikli();
        posaoArtikli.setIdPosaoArtikli(1);
        posaoArtikli.setIdRacuna(2);
        assertEquals(posaoArtikli.getIdPosaoArtikli(), idRadnogNaloga);
        System.out.println("ID Radnog Naloga IS: " + idRadnogNaloga);

    }


    @Test
    public void deletePosaoArtikleDao() throws AcrenoException, SQLException {
        posaoArtikli.setIdRacuna(1);
        posaoArtikli.setIdArtikla(3);
        int toCheckValue = posaoArtikliDAO.findAllPosaoArtikleDao().get(0).getIdRacuna();
        assertEquals("Property is not same as excepted - ", toCheckValue, toCheckValue);

        posaoArtikliDAO.deletePosaoArtikliDao(posaoArtikli);

    }
}
