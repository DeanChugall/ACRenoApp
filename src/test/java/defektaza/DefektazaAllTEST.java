package defektaza;

import org.junit.Test;
import rs.acreno.defektaza.Defektaza;
import rs.acreno.defektaza.DefektazaDAO;
import rs.acreno.defektaza.SQLDefektazaDAO;
import rs.acreno.system.exeption.AcrenoException;

import java.sql.SQLException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class DefektazaAllTEST {

    private final Defektaza defektaza;
    static List<Defektaza> findByPropDefektaze;

    DefektazaDAO defektazaDAO = new SQLDefektazaDAO();

    public DefektazaAllTEST() {
        defektaza = new Defektaza();
        defektaza.setIdDefektaze(1);
        defektaza.setIdAuta(5);
        defektaza.setOpisDefektaze("TEST-6 DEFEKTAZA");
        defektaza.setDatumDefektaze("10.5.2020");
        defektaza.setOstaliDetaljiDefektaze("TEST-6 DEFEKTAZA");
    }

    @Test
    public void insertDefektaza() throws AcrenoException, SQLException {
        defektazaDAO.insertDefektaza(defektaza);
    }

    @Test
    public void findAllDefektaza() throws AcrenoException, SQLException {

        //PRONALAZENJE I PRIKAZ SVIH DEFEKTAZA
        System.out.println("PRIKAZ SVIH DEFEKTAZA");
        System.out.println("******************************************************");
        List<Defektaza> defektaze = defektazaDAO.findAllDefektaza();

        assertFalse("LISTA DEFEKTAZE JE PRAZNA ! - ", defektaze.isEmpty());
        if (defektaze.isEmpty()) {
            System.out.println("Lista je prazna");
        } else {
            for (Defektaza defektaza : defektaze) {
                System.out.println(
                        " - ID DEFEKTAZE: " + defektaza.getIdDefektaze()
                                + " - ID AUTA: " + defektaza.getIdAuta()
                                + " - OPIS DEFEKTAZE: " + defektaza.getOpisDefektaze()
                                + " - DATUM DEFEKTAZE: " + defektaza.getDatumDefektaze()
                                + " - OSTALI DETALJI DEFEKTAZE: " + defektaza.getOstaliDetaljiDefektaze()
                );
            }
        }
    }

    @Test
    public void findDefektazaByProperty() throws AcrenoException, SQLException {
        System.out.println("FIND DEFEKTAZA BY PROPERTY DATUM");
        System.out.println("******************************************************");
        defektaza.setOpisDefektaze("TEST-1 DEFEKTAZA");
        //List<Defektaza> automobili = defektazaDAO.findAllDefektaza();
        findByPropDefektaze = defektazaDAO.findAllDefektaza();
        for (Defektaza defektaza :
                findByPropDefektaze) {
            System.out.println(defektaza.getDatumDefektaze());
        }
        String exeptedOpisDefektaze = findByPropDefektaze.get(0).getOpisDefektaze();
        System.out.println("Excepted OPIS DEFEKTAZE: " + exeptedOpisDefektaze);
        assertEquals("Property is not same as excepted - ", defektaza.getOpisDefektaze(), exeptedOpisDefektaze);
    }

    @Test
    public void deleteDefektazaFromDb() throws AcrenoException, SQLException {
        defektaza.setIdDefektaze(3);
        defektazaDAO.deleteDefektaza(defektaza.getIdDefektaze());
    }


    @Test
    public void updateDefektaza() throws SQLException, AcrenoException {
        defektaza.setIdDefektaze(5);
        defektaza.setOpisDefektaze("PROBA ZA 5");
        defektazaDAO.updateDefektaza(defektaza);
    }
}
