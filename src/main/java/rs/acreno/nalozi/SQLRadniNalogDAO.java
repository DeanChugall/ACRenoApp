package rs.acreno.nalozi;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.jetbrains.annotations.NotNull;
import rs.acreno.system.DAO.SqlQuerys;
import rs.acreno.system.exeption.AcrenoException;
import rs.acreno.system.util.AcrSqlSetUp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class SQLRadniNalogDAO implements RadniNalogDAO {
    private Connection connection;
    private final QueryRunner dbAccess = new QueryRunner();
    private List<RadniNalog> radniNalog = null;

    @Override
    public Connection connect() {
        return connection = AcrSqlSetUp.connect();
    }

    @Override
    public void close() throws SQLException {
        connection.close();
    }

    @Override
    public long insertRadniNalog(@NotNull RadniNalog radniNalog) throws AcrenoException, SQLException {

        String sql = SqlQuerys.INSERT_RADNI_NALOG_IN_TABLE;

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, radniNalog.getIdRadnogNaloga());
            pstmt.setInt(2, radniNalog.getIdAutomobila());
            pstmt.setString(3, radniNalog.getDatum());
            pstmt.setString(4, radniNalog.getVreme());
            pstmt.setString(5, radniNalog.getKilometraza());
            pstmt.setString(6, radniNalog.getDetaljiStranke());
            pstmt.setString(7, radniNalog.getDetaljiServisera());
            pstmt.executeUpdate();
            System.out.println("FROM : insertRadnogNaloga");
        } catch (SQLException e) {
            throw new AcrenoException("Greska u DB insertRadnogNaloga RADNOG_NALOGA", e);
        } finally {
            if (connection != null) {
                close();
            }
        }
        return -1L;
    }

    @Override
    public boolean updateRadniNalog(@NotNull RadniNalog radniNalog) throws SQLException, AcrenoException {
        try {
            connect();
            dbAccess.update(connection, SqlQuerys.UPDATE_RADNI_NALOG_TABLE,
                    radniNalog.getIdAutomobila(),
                    radniNalog.getDatum(),
                    radniNalog.getVreme(),
                    radniNalog.getKilometraza(),
                    radniNalog.getDetaljiStranke(),
                    radniNalog.getDetaljiServisera(),
                    radniNalog.getIdRadnogNaloga());
            return true;
        } catch (SQLException e) {
            throw new AcrenoException("Greska u DB updateRadniNalog RADNOG_NALOGA", e);
        } finally {
            if (connection != null) {
                close();
            }
        }
    }

    @Override
    public boolean deleteRadniNalog(int idRadnogNaloga) throws AcrenoException, SQLException {
        try {
            connect();
            dbAccess.update(connection, SqlQuerys.DELETE_FROM_RADNI_NALOG, idRadnogNaloga);
            return true;
        } catch (Exception e) {
            throw new AcrenoException("Greska u DB deleteRadniNalog RADNOG_NALOGA", e);
        } finally {
            if (connection != null) {
                close();
            }
        }
    }

    @Override
    public List<RadniNalog> findRadniNalogByProperty(@NotNull RadniNalogSearchType radniNalogSearchType,
                                                     Object value) throws AcrenoException, SQLException {
        String whereClause = "";
        String valueClause = "";
        switch (radniNalogSearchType) {
            case ID_NALOGA -> {
                whereClause = "IdRadnogNaloga = ?";
                valueClause = value.toString();
            }
            case DATUM_NALOGA -> {
                whereClause = "Datum = ?";
                valueClause = value.toString();
            }
            case ID_AUTOMOBILA -> {
                whereClause = "IdAutomobila = ?";
                valueClause = value.toString();
            }
            default -> System.out.println("Nepoznati Search type RADNOG_NALOGA");
        }
        try {
            connect();
            radniNalog = dbAccess.query(connection, SqlQuerys.FIND_ALL_RADNI_NALOG_BY_PROPERTY +
                    whereClause, new BeanListHandler<>(RadniNalog.class), valueClause);
        } catch (Exception e) {
            throw new AcrenoException("Greska u DB findRadniNalogByProperty RADNOG_NALOGA", e);
        } finally {
            if (connection != null) {
                close();
            }
        }
        return radniNalog;
    }

    @Override
    public List<RadniNalog> findAllRadniNalog() throws AcrenoException, SQLException {
        try {
            connect();
            radniNalog = dbAccess.query(connection, SqlQuerys.FIND_ALL_RADNI_NALOG, new BeanListHandler<>(RadniNalog.class));
        } catch (SQLException e) {
            throw new AcrenoException("Greska u DB findAllRadniNalog RADNOG_NALOGA", e);
        } finally {
            if (connection != null) {
                close();
            }
        }
        return radniNalog;
    }
}
