package rs.acreno.artikli.posao_artikli_dao;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.jetbrains.annotations.NotNull;
import rs.acreno.system.DAO.SqlQuerys;
import rs.acreno.system.exeption.AcrenoException;
import rs.acreno.system.util.AcrSqlSetUp;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class SQLPosaoArtikliDAO implements PosaoArtikliDAO {
    private Connection connection;
    private final QueryRunner dbAccess = new QueryRunner();
    private List<PosaoArtikli> posaoArtikliDao = null;

    @Override
    public Connection connect() throws SQLException {
        return connection = AcrSqlSetUp.connect();
    }

    @Override
    public void close() throws AcrenoException, SQLException {
        connection.close();
    }

    @Override
    public long insertPosaoArtikliDao(@NotNull PosaoArtikli posaoArtikli) throws AcrenoException, SQLException {
        try {
            connect();
            dbAccess.update(connection,
                    SqlQuerys.INSERT_INTO_POSAO_ARTIKLI_DAO
                    , posaoArtikli.getIdPosaoArtikli()
                    , posaoArtikli.getIdRacuna()
                    , posaoArtikli.getIdArtikla()
                    , posaoArtikli.getNazivArtikla()
                    , posaoArtikli.getCena()
                    , posaoArtikli.getNabavnaCena()
                    , posaoArtikli.getKolicina()
                    , posaoArtikli.getJedinicaMere()
                    , posaoArtikli.getPopust()
                    , posaoArtikli.getOpisPosaoArtiklli()
                    , posaoArtikli.getDetaljiPosaoArtikli());

        } catch (SQLException e) {
            throw new AcrenoException("Greska u DB insertArtikli ARTIKLI", e);
        } finally {
            if (connection != null) {
                close();
            }
        }
        return -1L;
    }

    @Override
    public boolean updatePosaoArtikliDao(@NotNull PosaoArtikli posaoArtikli) throws SQLException, AcrenoException {
        try {
            connect();
            dbAccess.update(connection, SqlQuerys.UPDATE_POSAO_ARTIKLI_DAO_TABLE
                    , posaoArtikli.getIdRacuna()
                    , posaoArtikli.getIdArtikla()
                    , posaoArtikli.getNazivArtikla()
                    , posaoArtikli.getCena()
                    , posaoArtikli.getNabavnaCena()
                    , posaoArtikli.getKolicina()
                    , posaoArtikli.getJedinicaMere()
                    , posaoArtikli.getPopust()
                    , posaoArtikli.getOpisPosaoArtiklli()
                    , posaoArtikli.getDetaljiPosaoArtikli()
                    , posaoArtikli.getIdPosaoArtikli());
            return true;
        } catch (SQLException e) {
            throw new AcrenoException("Greska u DB updateArtikli ARTIKLI", e);
        } finally {
            if (connection != null) {
                close();
            }
        }
    }

    @Override
    public boolean deletePosaoArtikliDao(PosaoArtikli posaoArtikli) throws AcrenoException, SQLException {
        try {
            connect();
            dbAccess.update(connection, SqlQuerys.DELETE_FROM_TABLE_POSAO_ARTIKLI_DAO, posaoArtikli.getIdPosaoArtikli());
            return true;
        } catch (Exception e) {
            throw new AcrenoException("Greska u DB DELETE deleteArtikli ARTIKLI", e);
        } finally {
            if (connection != null) {
                close();
            }
        }
    }

    @Override
    public List<PosaoArtikli> findPosaoArtikliByPropertyDao(@NotNull PosaoArtikliDaoSearchType posaoArtikliDaoSearchType,
                                                            Object value) throws AcrenoException, SQLException {
        String whereClause = "";
        String valueClause = "";
        switch (posaoArtikliDaoSearchType) {
            case ID_ARTIKLA_POSAO_DAO -> {
                whereClause = "idArtikla=?";
                valueClause = value.toString();
            }
            case ID_RACUNA_POSAO_ARTIKLI_DAO -> {
                whereClause = "idRacuna=?";
                valueClause = value.toString();
            }
            case ID_POSAO_ARTIKLI -> {
                whereClause = "idPosaoArtikli=?";
                valueClause = value.toString();
            }
            default -> System.out.println("Nepoznati Search type ARTIKL");
        }
        try {
            connect();
            posaoArtikliDao = dbAccess.query(connection, SqlQuerys.FIND_ALL_POSAO_ARTIKLE_DAO_BY_PROPERTY +
                    whereClause, new BeanListHandler<>(PosaoArtikli.class), valueClause);
        } catch (Exception e) {
            throw new AcrenoException("Greska u DB findUsluguByProperty ARTIKAL", e);
        } finally {
            if (connection != null) {
                close();
            }
        }
        return posaoArtikliDao;
    }

    @Override
    public List<PosaoArtikli> findAllPosaoArtikleDao() throws AcrenoException, SQLException {
        try {
            connect();
            posaoArtikliDao = dbAccess.query(connection, SqlQuerys.FIND_ALL_POSAO_ARTIKLE_DAO,
                    new BeanListHandler<>(PosaoArtikli.class));
        } catch (SQLException e) {
            throw new AcrenoException("Greska u DB findAllArtikle ARTIKAL", e);
        } finally {
            if (connection != null) {
                close();
            }
        }
        return posaoArtikliDao;
    }
}
