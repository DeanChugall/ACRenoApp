package rs.acreno.racuni;

import rs.acreno.automobil.AutoSearchType;
import rs.acreno.automobil.Automobil;
import rs.acreno.system.DAO.DAO;
import rs.acreno.system.exeption.AcrenoException;

import java.sql.SQLException;
import java.util.List;

public interface RacuniDAO extends DAO {

    long insertRacun(Racun racun) throws AcrenoException, SQLException;

    boolean updateRacun(Racun racun) throws SQLException, AcrenoException;

    boolean deleteRacun(Racun racun) throws AcrenoException, SQLException;

    List<Racun> findRacunByProperty(RacuniSearchType racuniSearchType, Object value) throws AcrenoException, SQLException;

    List<Racun> findAllRacune() throws AcrenoException, SQLException;

}
