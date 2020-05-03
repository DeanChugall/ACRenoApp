package rs.acreno.artikli.posao_artikli_dao;

import rs.acreno.artikli.Artikl;
import rs.acreno.artikli.ArtikliSearchType;
import rs.acreno.system.DAO.DAO;
import rs.acreno.system.exeption.AcrenoException;

import java.sql.SQLException;
import java.util.List;

public interface PosaoArtikliDAO extends DAO {

    long insertPosaoArtikliDao(PosaoArtikli posaoArtikli) throws AcrenoException, SQLException;

    boolean updatePosaoArtikliDao(PosaoArtikli posaoArtikli) throws SQLException, AcrenoException;

    boolean deletePosaoArtikliDao(PosaoArtikli posaoArtikli) throws AcrenoException, SQLException;

    List<PosaoArtikli> findPosaoArtikliByPropertyDao(PosaoArtikliDaoSearchType posaoArtikliDaoSearchType, Object value) throws AcrenoException, SQLException;

    List<PosaoArtikli> findAllPosaoArtikleDao() throws AcrenoException, SQLException;

}
