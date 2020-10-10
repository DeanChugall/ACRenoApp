package rs.acreno.defektaza;

import rs.acreno.system.DAO.DAO;
import rs.acreno.system.exeption.AcrenoException;

import java.sql.SQLException;
import java.util.List;

public interface DefektazaDAO extends DAO {

    long insertDefektaza(Defektaza defektaza) throws AcrenoException, SQLException;

    boolean updateDefektaza(Defektaza defektaza) throws SQLException, AcrenoException;

    boolean deleteDefektaza(int idDefektaze) throws AcrenoException, SQLException;

    List<Defektaza> findDefektazaByProperty(DefektazaSearchType defektazaSearchType, Object value) throws AcrenoException, SQLException;

    List<Defektaza> findAllDefektaza() throws AcrenoException, SQLException;

}
