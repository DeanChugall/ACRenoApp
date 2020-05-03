package rs.acreno.artikli.posao_artikli_dao;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.jetbrains.annotations.NotNull;
import org.sqlite.SQLiteDataSource;
import rs.acreno.system.DAO.SqlQuerys;
import rs.acreno.system.constants.Constants;
import rs.acreno.system.exeption.AcrenoException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

public class SQLPosaoArtikliDAO implements PosaoArtikliDAO {
    private Connection connection;
    private final QueryRunner dbAccess = new QueryRunner();
    private List<PosaoArtikli> posaoArtikliDao = null;

    @Override
    public Connection connect() throws SQLException {
        SQLiteDataSource ds = new SQLiteDataSource();
        ds.setUrl(Constants.MSACCESS_STRING_URL);
        connection = ds.getConnection();
        return connection = DriverManager.getConnection(Constants.MSACCESS_STRING_URL);
    }

    @Override
    public void close() throws AcrenoException, SQLException {
        connection.close();
        try {
            DriverManager.getConnection(Constants.MSACCESS_STRING_URL);
        } catch (Exception e) {
            throw new AcrenoException("Greska u DB close USLUGE", e);
        }
    }

    @Override
    public long insertPosaoArtikliDao(@NotNull PosaoArtikli posaoArtikli) throws AcrenoException, SQLException {
        try {
            connect();
            dbAccess.update(connection,
                    SqlQuerys.INSERT_INTO_POSAO_ARTIKLI_DAO
                   /* , posaoArtikli.getIdPosaoArtikli()*/
                    , posaoArtikli.getIdRacuna()
                    , posaoArtikli.getIdArtikla()
                    , posaoArtikli.getCena()
                    , posaoArtikli.getNabavnaCena()
                    , posaoArtikli.getKolicina()
                    , posaoArtikli.getJedinicaMere()
                    , posaoArtikli.getPopust()
                    , posaoArtikli.getOpisPosaoArtiklli()
                    , posaoArtikli.getDetaljiPosaoArtikli());

        } catch (SQLException e) {
            throw new AcrenoException("Greska u DB insertArtikli ARTIKLI", e);
        }
        close();
        return -1L;
    }

    @Override
    public boolean updatePosaoArtikliDao(@NotNull PosaoArtikli posaoArtikli) throws SQLException, AcrenoException {
        try {
            connect();
            dbAccess.update(connection, SqlQuerys.UPDATE_POSAO_ARTIKLI_DAO_TABLE
                    , posaoArtikli.getIdRacuna()
                    , posaoArtikli.getIdArtikla()
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
            dbAccess.update(connection, SqlQuerys.DELETE_FROM_TABLE_POSAO_ARTIKLI_DAO,
                    posaoArtikli.getIdRacuna(), posaoArtikli.getIdArtikla());
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
                                                            Object value)  throws AcrenoException, SQLException {
        String whereClause = "";
        String valueClause = "";
        switch (posaoArtikliDaoSearchType) {
            case ID_ARTIKLA_POSAO_DAO -> {
                whereClause = "idArtikla LIKE ?";
                valueClause = "%" + value.toString() + "%";
            }
            case ID_RACUNA_POSAO_ARTIKLI_DAO -> {
                whereClause = "idRacuna LIKE ?";
                valueClause = "%" + value.toString() + "%";
            }
            case ID_POSAO_ARTIKLI -> {
                whereClause = "idPosaoArtikli LIKE ?";
                valueClause = "%" + value.toString() + "%";
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
