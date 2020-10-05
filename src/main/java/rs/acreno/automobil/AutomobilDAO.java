package rs.acreno.automobil;

import rs.acreno.system.DAO.DAO;
import rs.acreno.system.exeption.AcrenoException;

import java.sql.SQLException;
import java.util.List;

public interface AutomobilDAO extends DAO {

    void insertAutomobil(Automobil auto) throws AcrenoException, SQLException;

    void updateAutomobil(Automobil auto) throws SQLException, AcrenoException;

    void deleteAutomobil(Automobil auto) throws AcrenoException, SQLException;

    List<Automobil> findAutomobilByProperty(AutoSearchType autoSearchType, Object value) throws AcrenoException, SQLException;

    List<Automobil> findAllAutomobil() throws AcrenoException, SQLException;

}
