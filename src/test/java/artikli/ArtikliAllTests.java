package artikli;

import org.junit.Test;
import rs.acreno.artikli.Artikl;
import rs.acreno.artikli.ArtikliDAO;
import rs.acreno.artikli.SQLArtikliDAO;
import rs.acreno.system.exeption.AcrenoException;

import java.sql.SQLException;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class ArtikliAllTests {

    private  Artikl artikl;


    private final ArtikliDAO artikliDAO = new SQLArtikliDAO();

    public ArtikliAllTests() {
        artikl = new Artikl();
        artikl.setIdArtikla(1);
        artikl.setNazivArtikla("LETVA CVOLANA");
        artikl.setKataloskiBrArtikla("asdasdasd");
        artikl.setOpisArtikla("LETVA LETVA Scenic II");
        artikl.setJedinicaMere("lit");
        artikl.setKolicina(3);
        artikl.setNabavnaCenaArtikla(34234.56);
        artikl.setCenaArtikla(12345);
    }

    @Test
    public void insertArtikal() throws AcrenoException, SQLException {

        artikliDAO.insertArtikli(artikl);
    }


    @Test
    public void pretraziSveArtikleSamoTest() throws AcrenoException, SQLException {
        //PRONALAZENJE I PRIKAZ SVIH ARTIKALA
        System.out.println("******************************************************");
        System.out.println("PRIKAZ SVIH ARTIKALA");
        List<Artikl> artikls = artikliDAO.findAllArtikle();
        int idArtikla = artikls.get(0).getIdArtikla();
        assertEquals(3, idArtikla);
        for (Artikl artikl : artikls) {
            System.out.println(
                    artikl.getIdArtikla()
                            + " - " + artikl.getNazivArtikla()
                            + " - " + artikl.getKolicina());
        }
    }

    @Test
    public void updateArtikal() throws SQLException, AcrenoException {
        artikl.setIdArtikla(3);
        artikl.setKataloskiBrArtikla("123123123");
        artikliDAO.updateArtikli(artikl);
    }

    @Test
    public void findArtikalByParameter() throws AcrenoException, SQLException {


        int idArtikla = artikliDAO.findAllArtikle().get(0).getIdArtikla();
        assertEquals(3, idArtikla);
        System.out.println("ID ARTKLA IS: " + idArtikla);


    }


    @Test
    public void deleteArtikal() throws AcrenoException, SQLException {
        artikl.setIdArtikla(4);
        int fromArticleObject = artikl.getIdArtikla();
        int toCheckValue = 4;
        assertEquals("Property is not same as excepted - ", toCheckValue, fromArticleObject);

        artikliDAO.deleteArtikli(artikl);

    }
}
