package rs.acreno.automobil;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import rs.acreno.system.DAO.SqlQuerys;
import rs.acreno.system.exeption.AcrenoException;
import rs.acreno.system.util.AcrSqlSetUp;
import rs.acreno.system.util.properties.AcrenoProperties;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class SQLAutomobilDAO implements AutomobilDAO{

    private static final Logger logger = Logger.getLogger(AcrenoProperties.class);

    private Connection connection;
    private final QueryRunner dbAccess = new QueryRunner();
    private List<Automobil> automobili = null;

    @Override
    public Connection connect() throws SQLException {
        return connection = AcrSqlSetUp.connect();
    }

    @Override
    public void close() throws AcrenoException, SQLException {
        connection.close();
    }

    @Override
    public void insertAutomobil(@NotNull Automobil auto) throws AcrenoException, SQLException {

        String sql = SqlQuerys.INSERT_AUTOMOBIL_IN_TABLE;

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, auto.getIdAuta());
            pstmt.setInt(2, auto.getIdKlijenta());
            pstmt.setString(3, auto.getRegOznaka());
            pstmt.setString(4, auto.getKilomteraza());
            pstmt.setString(5, auto.getVrstaVozila());
            pstmt.setString(6, auto.getMarkaVozila());
            pstmt.setString(7, auto.getModelVozila());
            pstmt.setInt(8, auto.getGodisteVozila());
            pstmt.setInt(9, auto.getZapreminaVozila());
            pstmt.setInt(10, auto.getSnagaVozila());
            pstmt.setString(11, auto.getVinVozila());
            pstmt.setString(12, auto.getBrojMotoraVozila());
            pstmt.setString(13, auto.getVrstaGorivaVozila());
            pstmt.setString(14, auto.getBojaVozila());
            pstmt.setInt(15, auto.getMasaVozila());
            pstmt.setInt(16, auto.getNajvecaDozvoljenaMasaVozila());
            pstmt.setString(17, auto.getDatumPrveRegistracijeVozila());
            pstmt.setInt(18, auto.getBrojMestaZaSedenje());
            pstmt.setInt(19, auto.getBrojVrataVozila());
            pstmt.setString(20, auto.getNapomeneAutomobila());
            pstmt.setString(21, auto.getDatumAcrRegistracijeAuta());
            pstmt.executeUpdate();
            System.out.println("FROM : insertKlijnet");
        } catch (SQLException e) {
            logger.error(e.getMessage() + " >>>>  Greska u DB insertAutomobil AUTA !");
            throw new AcrenoException("Greska u DB insertAutomobil AUTA", e);
        } finally {
            if (connection != null) {
                close();
            }
        }
    }

    @Override
    public void updateAutomobil(@NotNull Automobil auto) throws SQLException, AcrenoException {
        try {
            connect();
            dbAccess.update(connection, SqlQuerys.UPDATE_CAR_TABLE,
                    auto.getIdKlijenta(), auto.getRegOznaka(), auto.getKilomteraza(),
                    auto.getVrstaVozila(), auto.getMarkaVozila(), auto.getModelVozila(),
                    auto.getGodisteVozila(), auto.getZapreminaVozila(), auto.getSnagaVozila(),
                    auto.getVinVozila(), auto.getBrojMotoraVozila(), auto.getVrstaGorivaVozila(),
                    auto.getBojaVozila(), auto.getMasaVozila(), auto.getNajvecaDozvoljenaMasaVozila(),
                    auto.getDatumPrveRegistracijeVozila(), auto.getBrojMestaZaSedenje(),
                    auto.getBrojVrataVozila(), auto.getNapomeneAutomobila(),
                    auto.getDatumAcrRegistracijeAuta(), auto.getIdAuta());
        } catch (SQLException e) {
            logger.error(e.getMessage() + " >>>>  Greska u DB UpdateAutomobil AUTA !");
            throw new AcrenoException("Greska u DB UpdateAutomobil AUTA", e);
        } finally {
            if (connection != null) {
                close();
            }
        }
    }

    @Override
    public void deleteAutomobil(Automobil auto) throws AcrenoException, SQLException {
        try {
            connect();
            dbAccess.update(connection, SqlQuerys.DELETE_FROM_AUTOMOBILI, auto.getIdAuta());
        } catch (Exception e) {
            logger.error(e.getMessage() + " >>>>  Greska u DB DELETE deleteAutomobil AUTA !");
            throw new AcrenoException("Greska u DB DELETE deleteAutomobil AUTA", e);
        } finally {
            if (connection != null) {
                close();
            }
        }
    }

    @Override
    public List<Automobil> findAutomobilByProperty(@NotNull AutoSearchType autoSearchType, Object value) throws AcrenoException, SQLException {
        String whereClause = "";
        String valueClause = "";
        switch (autoSearchType) {
            case ID_AUTA -> {
                whereClause = "idAuta = ?";
                valueClause = value.toString();
            }
            case BR_TABLICE -> {
                whereClause = "regOznaka LIKE ?";
                valueClause = "%" + value.toString() + "%";
            }
            case VIN_BROJ -> {
                whereClause = "vinVozila = ?";
                valueClause = value.toString();
            }
            case KLIJNET_ID -> {
                whereClause = "idKlijenta = ?";
                valueClause = value.toString();
            }
            default -> System.out.println("Nepoznati Search type");
        }
        try {
            connect();
            automobili = dbAccess.query(connection, SqlQuerys.FIND_ALL_AUTOMOBILI_BY_PROPERTY +
                    whereClause, new BeanListHandler<>(Automobil.class), valueClause);
        } catch (Exception e) {
            logger.error(e.getMessage() + " >>>>  Greska u DB findAllAutomobil AUTA !");
            throw new AcrenoException("Greska u DB findAutomobilByProperty AUTA", e);
        } finally {
            if (connection != null) {
                close();
            }
        }
        return automobili;
    }

    @Override
    public List<Automobil> findAllAutomobil() throws AcrenoException, SQLException {
        try {
            connect();
            automobili = dbAccess.query(connection, SqlQuerys.FIND_ALL_AUTOMOBILI, new BeanListHandler<>(Automobil.class));
        } catch (SQLException e) {
            logger.error(e.getMessage() + " >>>>  Greska u DB findAllAutomobil AUTA !");
            System.exit(-1);
            throw new AcrenoException("Greska u DB findAllAutomobil AUTA", e);
        } finally {
            if (connection != null) {
                close();

            }
        }
        return automobili;
    }
}
