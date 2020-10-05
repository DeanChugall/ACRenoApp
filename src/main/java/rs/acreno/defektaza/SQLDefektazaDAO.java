package rs.acreno.defektaza;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.jetbrains.annotations.NotNull;
import rs.acreno.system.DAO.SqlQuerys;
import rs.acreno.system.exeption.AcrenoException;
import rs.acreno.system.util.AcrSqlSetUp;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class SQLDefektazaDAO implements DefektazaDAO {
    private Connection connection;
    private final QueryRunner dbAccess = new QueryRunner();
    private List<Defektaza> defektaze = null;

    @Override
    public Connection connect() {
        return connection = AcrSqlSetUp.connect();
    }

    @Override
    public void close() throws SQLException {
        connection.close();
    }

    @Override
    public long insertDefektaza(@NotNull Defektaza defektaza) throws AcrenoException, SQLException {
        try {
            connect();
            dbAccess.update(connection,
                    SqlQuerys.INSERT_INTO_DEFEKTAZA_TABLE,
                    defektaza.getIdDefektaze(),
                    defektaza.getIdAuta(),
                    defektaza.getKilometraza(),
                    defektaza.getDatumDefektaze(),
                    defektaza.getVreme(),
                    defektaza.getOpisDefektaze(),
                    defektaza.getOstaliDetaljiDefektaze());

        } catch (SQLException e) {
            throw new AcrenoException("Greska u DB insertDefektaza DEFEKTAZA", e);
        } finally {
            if (connection != null) {
                close();
            }
        }
        return -1L;
    }

    @Override
    public boolean updateDefektaza(@NotNull Defektaza defektaza) throws SQLException, AcrenoException {
        try {
            connect();
            dbAccess.update(connection, SqlQuerys.UPDATE_DEFEKTAZA_TABLE,
                    defektaza.getIdAuta(),
                    defektaza.getKilometraza(),
                    defektaza.getDatumDefektaze(),
                    defektaza.getVreme(),
                    defektaza.getOpisDefektaze(),
                    defektaza.getOstaliDetaljiDefektaze(),
                    defektaza.getIdDefektaze());
            return true;
        } catch (SQLException e) {
            throw new AcrenoException("Greska u DB updateDefektaza DEFEKTAZA", e);
        } finally {
            if (connection != null) {
                close();
            }
        }
    }

    @Override
    public boolean deleteDefektaza(int idDefektaze) throws AcrenoException, SQLException {
        try {
            connect();
            dbAccess.update(connection, SqlQuerys.DELETE_FROM_TABLE_DEFEKTAZA, idDefektaze);
            return true;
        } catch (Exception e) {
            throw new AcrenoException("Greska u DB DELETE deleteDefektaza DEFEKTAZA", e);
        } finally {
            if (connection != null) {
                close();
            }
        }
    }

    @Override
    public List<Defektaza> findDefektazaByProperty(@NotNull DefektazaSearchType defektazaSearchType, Object value) throws AcrenoException, SQLException {
        String whereClause = "";
        String valueClause = "";
        switch (defektazaSearchType) {
            case ID_DEFEKTAZE -> {
                whereClause = "idDefektaze = ?";
                valueClause = value.toString();
            }
            case ID_AUTA -> {
                whereClause = "idAuta = ?";
                valueClause = value.toString();
            }
            case OPIS_DEFEKTAZE -> {
                whereClause = "opisDefektaze LIKE ?";
                valueClause = "%" + value.toString() + "%";
            }
            case DATUM_DEFEKTAZE -> {
                whereClause = "datumDefektaze LIKE ?";
                valueClause = "%" + value.toString() + "%";
            }
            default -> System.out.println("Nepoznati Search type");
        }
        try {
            connect();
            defektaze = dbAccess.query(connection, SqlQuerys.FIND_ALL_DEFEKTAZE_BY_PROPERTY +
                    whereClause, new BeanListHandler<>(Defektaza.class), valueClause);
        } catch (Exception e) {
            throw new AcrenoException("Greska u DB findDefektazaByProperty DEFEKTAZA", e);
        } finally {
            if (connection != null) {
                close();
            }
        }
        return defektaze;
    }

    @Override
    public List<Defektaza> findAllDefektaza() throws AcrenoException, SQLException {
        try {
            connect();
            defektaze = dbAccess.query(connection, SqlQuerys.FIND_ALL_DEFEKTAZE,
                    new BeanListHandler<>(Defektaza.class));
        } catch (SQLException e) {
            throw new AcrenoException("Greska u DB findAllDefektaza DEFEKTAZA", e);
        } finally {
            if (connection != null) {
                close();
            }
        }
        return defektaze;
    }
}
