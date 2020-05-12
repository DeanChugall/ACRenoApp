package rs.acreno.klijent;

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

public class SQLKlijnetDAO implements KlijentDAO {
    private Connection connection;
    private final QueryRunner dbAccess = new QueryRunner();
    private List<Klijent> klijents = null;

    @Override
    public Connection connect() {
        return connection = AcrSqlSetUp.connect();
    }

    @Override
    public void close() throws SQLException {
        connection.close();
    }

    @Override
    public void insertKlijnet(@NotNull Klijent klijent) throws AcrenoException, SQLException {

        String sql = SqlQuerys.INSERT_KLIJENT_IN_TABLE;

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, klijent.getImePrezime());
            pstmt.setString(2, klijent.getMesto());
            pstmt.setLong(3, klijent.getPostanskiBroj());
            pstmt.setString(4, klijent.getUlicaBroj());
            pstmt.setString(5, klijent.getBrLicneKarte());
            pstmt.setLong(6, klijent.getMaticniBroj());
            pstmt.setString(7, klijent.getOstaliDetalji());
            pstmt.setString(8, klijent.getEmail());
            pstmt.setString(9, klijent.getTelefonMobilni());
            pstmt.setString(10, klijent.getTelefonFiksni());
            pstmt.setString(11, klijent.getWeb());
            pstmt.setString(12, klijent.getBrojRacuna());
            pstmt.setString(13, klijent.getBanka());
            pstmt.executeUpdate();
            System.out.println("FROM : insertKlijnet");
        } catch (SQLException e) {
            throw new AcrenoException("Greska u DB UPDATE KLIJENTA", e);
        } finally {
            if (connection != null) {
                close();
            }
        }
    }

    @Override
    public void updateKlijent(@NotNull Klijent klijent) throws SQLException, AcrenoException {
        try {
            connect();
            dbAccess.update(connection, SqlQuerys.UPDATE_KLIJENT_TABLE,
                    klijent.getImePrezime(),
                    klijent.getMesto(),
                    klijent.getPostanskiBroj(),
                    klijent.getUlicaBroj(),
                    klijent.getBrLicneKarte(),
                    klijent.getMaticniBroj(),
                    klijent.getOstaliDetalji(),
                    klijent.getEmail(),
                    klijent.getTelefonMobilni(),
                    klijent.getTelefonFiksni(),
                    klijent.getWeb(),
                    klijent.getBrojRacuna(),
                    klijent.getBanka(),
                    klijent.getIdKlijenta());
        } catch (SQLException e) {
            throw new AcrenoException("Greska u DB UPDATE KLIJENTA", e);
        } finally {
            if (connection != null) {
                close();
            }
        }
    }

    @Override
    public void deleteKlijent(Klijent klijent) throws AcrenoException, SQLException {
        try {
            connect();
            dbAccess.update(connection, "DELETE FROM Klijenti WHERE IdKlijenta=?", klijent.getIdKlijenta());
        } catch (Exception e) {
            throw new AcrenoException("Greska u DB DELETE KLIJENTA", e);
        } finally {
            if (connection != null) {
                close();
            }
        }
    }

    @Override
    public List<Klijent> findKlijentByProperty(@NotNull KlijentSearchType klijentSearchType,
                                               Object value) throws AcrenoException, SQLException {
        String whereClause = "";
        String valueClause = "";
        switch (klijentSearchType) {
            case ID_KLIJENTA -> {
                whereClause = "IdKlijenta LIKE ?";
                valueClause = "%" + value.toString() + "%";
            }
            case IME_PREZIME -> {
                whereClause = "imePrezime LIKE ?";
                valueClause = "%" + value.toString() + "%";
            }
            case BR_LICNE_KARTE -> {
                whereClause = "brLicneKarte LIKE ?";
                valueClause = "%" + value.toString() + "%";
            }
            case MOBILNI_TELEFON -> {
                whereClause = "mobilniTelefon LIKE ?";
                valueClause = "%" + value.toString() + "%";
            }
            default -> System.out.println("Nepoznati Search type");
        }
        try {
            connect();
            klijents = dbAccess.query(connection, "SELECT * FROM Klijenti WHERE " +
                    whereClause, new BeanListHandler<>(Klijent.class), valueClause);
        } catch (Exception e) {
            throw new AcrenoException("Greska u DB findKlijentByProperty KLIJENTA", e);
        } finally {
            if (connection != null) {
                close();
            }
        }
        return klijents;
    }

    @Override
    public List<Klijent> findAllKlijents() throws AcrenoException, SQLException {
        try {
            connect();
            klijents = dbAccess.query(connection, SqlQuerys.FIND_ALL_KLIJENTS, new BeanListHandler<>(Klijent.class));
        } catch (SQLException e) {
            throw new AcrenoException("Greska u DB findAll KLIJENTA", e);
        } finally {
            if (connection != null) {
                close();
            }
        }
        return klijents;
    }
}
