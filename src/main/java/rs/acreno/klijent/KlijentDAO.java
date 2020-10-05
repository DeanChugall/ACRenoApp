package rs.acreno.klijent;

import rs.acreno.system.DAO.DAO;
import rs.acreno.system.exeption.AcrenoException;

import java.sql.SQLException;
import java.util.List;

public interface KlijentDAO extends DAO {

    void insertKlijnet(Klijent klijent) throws AcrenoException, SQLException;

    void updateKlijent(Klijent klijent) throws SQLException, AcrenoException;

    void deleteKlijent(Klijent klijent) throws AcrenoException, SQLException;

    List<Klijent> findKlijentByProperty(KlijentSearchType klijentSearchType, Object value) throws AcrenoException, SQLException;

    List<Klijent> findAllKlijents() throws AcrenoException, SQLException;


}
