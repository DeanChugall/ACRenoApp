package rs.acreno.artikli;

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

public class SQLiteArtikliDAO implements ArtikliDAO {
    private Connection connection;
    private final QueryRunner dbAccess = new QueryRunner();
    private List<Artikl> artiklsList = null;

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
    public long insertArtikli(@NotNull Artikl artikl) throws AcrenoException, SQLException {
        try {
            connect();
            dbAccess.update(connection,
                    SqlQuerys.INSERT_INTO_ARTIKLE
                    , artikl.getIdArtikla()
                    , artikl.getKataloskiBrArtikla()
                    , artikl.getNazivArtikla()
                    , artikl.getOpisArtikla()
                    , artikl.getJedinicaMere()
                    , artikl.getKolicina()
                    , artikl.getNabavnaCenaArtikla()
                    , artikl.getCenaArtikla());

        } catch (SQLException e) {
            throw new AcrenoException("Greska u DB insertArtikli ARTIKLI", e);
        }
        close();
        return -1L;
    }

    @Override
    public boolean updateArtikli(@NotNull Artikl artikl) throws SQLException, AcrenoException {
        try {
            connect();
            dbAccess.update(connection, SqlQuerys.UPDATE_ARTIKLI_TABLE
                    , artikl.getKataloskiBrArtikla()
                    , artikl.getNazivArtikla()
                    , artikl.getOpisArtikla()
                    , artikl.getJedinicaMere()
                    , artikl.getKolicina()
                    , artikl.getNabavnaCenaArtikla()
                    , artikl.getCenaArtikla()
                    , artikl.getIdArtikla());
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
    public boolean deleteArtikli(Artikl artikl) throws AcrenoException, SQLException {
        try {
            connect();
            dbAccess.update(connection, SqlQuerys.DELETE_FROM_TABLE_ARTIKLI, artikl.getIdArtikla());
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
    public List<Artikl> findArtikliByProperty(@NotNull ArtikliSearchType artikliSearchType, Object value)
            throws AcrenoException, SQLException {
        String whereClause = "";
        String valueClause = "";
        switch (artikliSearchType) {
            case ID_ARTIKLA -> {
                whereClause = "idArtikla LIKE ?";
                valueClause = "%" + value.toString() + "%";
            }
            case KATALOSKI_BROJ_ARTIKLA -> {
                whereClause = "kataloskiBrArtikla LIKE ?";
                valueClause = "%" + value.toString() + "%";
            }
            case NAZIV_ARTIKLA -> {
                whereClause = "nazivArtikla LIKE ?";
                valueClause = "%" + value.toString() + "%";
            }
            default -> System.out.println("Nepoznati Search type ARTIKL");
        }
        try {
            connect();
            artiklsList = dbAccess.query(connection, SqlQuerys.FIND_ALL_ARTIKLE_BY_PROPERTY +
                    whereClause, new BeanListHandler<>(Artikl.class), valueClause);
        } catch (Exception e) {
            throw new AcrenoException("Greska u DB findUsluguByProperty ARTIKAL", e);
        } finally {
            if (connection != null) {
                close();
            }
        }
        return artiklsList;
    }

    @Override
    public List<Artikl> findAllArtikle() throws AcrenoException, SQLException {
        try {
            connect();
            artiklsList = dbAccess.query(connection, SqlQuerys.FIND_ALL_ARTIKLE,
                    new BeanListHandler<>(Artikl.class));
        } catch (SQLException e) {
            throw new AcrenoException("Greska u DB findAllArtikle ARTIKAL", e);
        } finally {
            if (connection != null) {
                close();
            }
        }
        return artiklsList;
    }
}
