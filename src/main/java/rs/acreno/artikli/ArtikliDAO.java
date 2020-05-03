package rs.acreno.artikli;

import rs.acreno.system.DAO.DAO;
import rs.acreno.system.exeption.AcrenoException;

import java.sql.SQLException;
import java.util.List;

public interface ArtikliDAO extends DAO {

    long insertArtikli(Artikl usluga) throws AcrenoException, SQLException;

    boolean updateArtikli(Artikl usluga) throws SQLException, AcrenoException;

    boolean deleteArtikli(Artikl usluga) throws AcrenoException, SQLException;

    List<Artikl> findArtikliByProperty(ArtikliSearchType artikliSearchType, Object value) throws AcrenoException, SQLException;

    List<Artikl> findAllArtikle() throws AcrenoException, SQLException;

}
