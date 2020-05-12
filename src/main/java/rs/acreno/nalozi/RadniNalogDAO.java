package rs.acreno.nalozi;

import rs.acreno.system.DAO.DAO;
import rs.acreno.system.exeption.AcrenoException;

import java.sql.SQLException;
import java.util.List;

public interface RadniNalogDAO extends DAO {

    long insertRadniNalog(RadniNalog radniNalog) throws AcrenoException, SQLException;

    boolean updateRadniNalog(RadniNalog radniNalog) throws SQLException, AcrenoException;

    boolean deleteRadniNalog(int idRadnogNaloga) throws AcrenoException, SQLException;

    List<RadniNalog> findRadniNalogByProperty(RadniNalogSearchType radniNalogSearchType,
                                              Object value) throws AcrenoException, SQLException;

    List<RadniNalog> findAllRadniNalog() throws AcrenoException, SQLException;
}
